package org.insight.twitter.rpc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.insight.twitter.util.EndPoint;

import twitter4j.RateLimitStatusImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/*
 * Listen for Rate Limit Requests, Return Merged Rate Limit From all bots.
 */
class RatelimitWorker implements Runnable {

  public final String TASK_QUEUE_NAME = EndPoint.APPLICATION_RATE_LIMIT_STATUS.toString();
  private final String ident;

  private final Map<String, Set<TwitterWorker>> BOTS = new ConcurrentHashMap<String, Set<TwitterWorker>>();

  public RatelimitWorker(Set<TwitterWorker> allWorkers) {
    // Initialize Map:
    for (EndPoint endpoint : EndPoint.values()) {
      BOTS.put(endpoint.toString(), new HashSet<TwitterWorker>());
    }

    this.ident = "rate_limit_status";

    System.out.println(this.ident + " RateLimits for " + allWorkers.size() + " workers:");
    System.out.println("----------");


    // Load Bots:
    for (TwitterWorker w : allWorkers) {
      Set<TwitterWorker> workers = BOTS.get(w.TASK_QUEUE_NAME);
      workers.add(w);
      BOTS.put(w.TASK_QUEUE_NAME, workers);
    }

    for (EndPoint endpoint : EndPoint.values()) {
      RateLimitStatusImpl rl = new RateLimitStatusImpl();
      for (TwitterWorker w : BOTS.get(endpoint.toString())) {
        rl = rl.mergeWith(w.cachedRateLimit);
      }

      System.out.println(endpoint + " " + rl.getRemaining() + " of " + rl.getLimit() + " Reset in " + rl.getSecondsUntilReset());

    }
    System.out.println("----------");
  }

  @Override
  public void run() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      Entry<Channel, QueueingConsumer> rabbitmq = RabbitMQ.createChannelConsumer(TASK_QUEUE_NAME, ident);
      Channel channel = rabbitmq.getKey();
      QueueingConsumer consumer = rabbitmq.getValue();

      System.out.println(this.ident + " Ready for RPC requests on " + TASK_QUEUE_NAME);

      while (true) {
        // Get a message (task) from an endpoint
        QueueingConsumer.Delivery delivery;
        delivery = consumer.nextDelivery();
        AMQP.BasicProperties props = delivery.getProperties();
        AMQP.BasicProperties.Builder replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId());
        byte[] response;

        String endpoint = new String(delivery.getBody());

        RateLimitStatusImpl rl = new RateLimitStatusImpl();

        for (TwitterWorker w : BOTS.get(endpoint.toString())) {
          rl = rl.mergeWith(w.cachedRateLimit);
        }

        response = mapper.writeValueAsBytes(rl);
        channel.basicPublish("", props.getReplyTo(), replyProps.build(), response);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        System.out.println(endpoint + " " + rl.getRemaining() + " of " + rl.getLimit() + " Reset in " + rl.getSecondsUntilReset());
      }

    } catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
