package org.insight.twitter.rpc;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.insight.twitter.util.EndPoint;

import twitter4j.HttpResponse;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusImpl;
import twitter4j.RateLimitStatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterImpl;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

class TwitterWorker implements Runnable {

  // The Executor responsible for this runnable
  private final ScheduledExecutorService ex;

  private final ObjectMapper mapper = new ObjectMapper();

  public RateLimitStatus cachedRateLimit = new RateLimitStatusImpl().withRemaining(1);
  public final String TASK_QUEUE_NAME;
  public final String ident;

  private final Twitter t4jConnection;
  // RateLimitListener gets confused when requesting Ratelimit endpoint...
  private final Twitter t4jRateLimitConnection;

  public TwitterWorker(String ident, EndPoint endpoint, ScheduledExecutorService ex) throws TwitterException {
    this.TASK_QUEUE_NAME = endpoint.toString();
    this.ident = ident;
    this.ex = ex;

    //System.out.println(ident + " Creating Worker: " + TASK_QUEUE_NAME);
    if ("bot.app".equalsIgnoreCase(ident)) {
      // Need to tweak config before creating Application only Auth token:
      ConfigurationBuilder cb = new ConfigurationBuilder().setApplicationOnlyAuthEnabled(true);
      Configuration t4jConfig = cb.build();
      t4jConnection = new TwitterFactory(t4jConfig).getInstance();
      t4jConnection.getOAuth2Token();
      t4jRateLimitConnection = new TwitterFactory(t4jConfig).getInstance();
      t4jRateLimitConnection.getOAuth2Token();
    } else {
      t4jConnection = new TwitterFactory(ident).getInstance();
      t4jRateLimitConnection = new TwitterFactory(ident).getInstance();
    }

    RateLimitStatusListener listener = new RateLimitStatusListener() {
      @Override
      public void onRateLimitStatus(RateLimitStatusEvent event) {
        setRateLimit(event.getRateLimitStatus());
      }

      @Override
      public void onRateLimitReached(RateLimitStatusEvent event) {
        setRateLimit(event.getRateLimitStatus());
      }
    };
    t4jConnection.addRateLimitStatusListener(listener);
  }

  private void refreshRateLimit() {
    RateLimitStatus newRateLimitStatus = new RateLimitStatusImpl();
    try {
      // Make a smaller request for the endpoint family only:
      String endpointFamily = this.TASK_QUEUE_NAME.split("/")[1];
      Map<String, RateLimitStatus> twitterRateLimits = t4jRateLimitConnection.getRateLimitStatus(endpointFamily);
      // If the rate limit is defined by t4jConnection, use that - if not, create our own dummy ratelimit object:
      newRateLimitStatus =
          twitterRateLimits.containsKey(this.TASK_QUEUE_NAME) ? twitterRateLimits.get(this.TASK_QUEUE_NAME) : new RateLimitStatusImpl().withRemaining(1);
    } catch (TwitterException e) {
      System.err.println(this.ident + " Error Refreshing Ratelimit for: " + this.TASK_QUEUE_NAME + "  " + newRateLimitStatus.getRemaining());
      e.printStackTrace();
    }
    setRateLimit(newRateLimitStatus);
    //System.out.println(ident + " New Ratelimit : " + TASK_QUEUE_NAME + "  " + cachedRateLimit);
  }

  private void setRateLimit(RateLimitStatus newRateLimitStatus) {
    this.cachedRateLimit = newRateLimitStatus;
  }

  @Override
  public void run() {
    //System.out.println(ident + " Running Worker " + TASK_QUEUE_NAME);
    this.refreshRateLimit();

    try {

      Entry<Channel, QueueingConsumer> rabbitmq = RabbitMQ.createChannelConsumer(TASK_QUEUE_NAME, ident);
      Channel channel = rabbitmq.getKey();
      QueueingConsumer consumer = rabbitmq.getValue();

      System.out.println(this.ident + " Ready for RPC requests on " + this.TASK_QUEUE_NAME + ": Limit = " + this.cachedRateLimit.getRemaining());

      // Consume while ratelimit allows
      while (this.cachedRateLimit.getRemaining() > 1) {
        // Get a message (task) from an endpoint
        QueueingConsumer.Delivery delivery;

        //System.out.println(TASK_QUEUE_NAME + "Get Next delivery... ");
        delivery = consumer.nextDelivery();
        AMQP.BasicProperties props = delivery.getProperties();
        AMQP.BasicProperties.Builder replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId());
        //System.out.println(TASK_QUEUE_NAME + " Got delivery:" + props.getCorrelationId());

        byte[] response;

        String logmsg = "";

        try {
          String url = new String(delivery.getBody());

          logmsg += this.ident + " " + url + " ";

          TwitterImpl raw = (TwitterImpl) this.t4jConnection;
          HttpResponse r = raw.get(raw.getConfiguration().getRestBaseURL() + url);
          response = r.asString().getBytes();
          //System.out.println(this.ident + " Worker Got : " + response.length + " bytes");
          channel.basicPublish("", props.getReplyTo(), replyProps.build(), response);
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        } catch (TwitterException e) {
          logmsg += " ERROR: " + this.TASK_QUEUE_NAME + " : " + e.getErrorMessage();
          // build error response:
          response = this.mapper.writeValueAsBytes(e);
          // Don't retry unless it's a network issue, or rate limit
          boolean requeue = e.isCausedByNetworkIssue() || e.exceededRateLimitation();
          replyProps.type("ERROR");
          // Reply with Error:
          channel.basicPublish("", props.getReplyTo(), replyProps.build(), response);
          channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, requeue);
        }

        logmsg +=
            "Ok: " + response.length + " bytes " + this.TASK_QUEUE_NAME + " Remaining: " + this.cachedRateLimit.getRemaining() + " of "
                + this.cachedRateLimit.getLimit();
        System.out.println(logmsg);

      }

      channel.close();

    } catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException | TimeoutException e) {
      e.printStackTrace();
    }

    System.out.println(this.ident + " REACHED LIMIT, Disconnecting! " + this.TASK_QUEUE_NAME + " Remaining: " + this.cachedRateLimit.getRemaining());

    // Wait a while before reconnecting...
    long howLong = this.cachedRateLimit.getSecondsUntilReset();
    howLong = 1 > howLong ? 13 : howLong + 7;
    this.ex.schedule(this, howLong, TimeUnit.SECONDS);

    System.out.println(this.ident + " " + new Date() + " Scheduled reconnect " + this.TASK_QUEUE_NAME + " in " + howLong + " ");
  }

}
