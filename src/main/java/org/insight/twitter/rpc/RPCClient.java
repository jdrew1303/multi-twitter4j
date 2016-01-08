package org.insight.twitter.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.insight.twitter.util.EndPoint;

import twitter4j.HttpParameter;
import twitter4j.TwitterException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RPCClient {

  private final Connection connection;
  private final Channel channel;
  private final String replyQueueName;
  private final QueueingConsumer consumer;

  private final ObjectMapper mapper;

  public RPCClient() throws IOException, TimeoutException {

    Properties properties = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try (InputStream resourceStream = loader.getResourceAsStream("twitter4j.properties")) {
      properties.load(resourceStream);
    }

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(properties.getProperty("rabbitmq"));

    this.connection = factory.newConnection();
    this.channel = this.connection.createChannel();

    this.replyQueueName = this.channel.queueDeclare().getQueue();
    this.consumer = new QueueingConsumer(this.channel);
    this.channel.basicConsume(this.replyQueueName, true, this.consumer);

    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public String call(EndPoint queue, HttpParameter... params) throws TwitterException {
    return this.call(queue, queue.toString() + ".json?" + HttpParameter.encodeParameters(params));
  }

  public String call(EndPoint queue, String path, HttpParameter... params) throws TwitterException {
    return this.call(queue, path + "?" + HttpParameter.encodeParameters(params));
  }

  public String call(EndPoint queue, String url) throws TwitterException {

    String corrId = UUID.randomUUID().toString();
    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(this.replyQueueName).build();

    try {
      this.channel.basicPublish("", queue.toString(), props, url.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }

    String response = "";
    while (true) {
      try {
        QueueingConsumer.Delivery delivery = this.consumer.nextDelivery();
        //StringBuilder sb = new StringBuilder();
        //delivery.getProperties().appendPropertyDebugStringTo(sb);
        //System.out.println(sb.toString());
        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
          byte[] bytes = delivery.getBody();
          response = new String(bytes);
          if ((null != delivery.getProperties().getType()) && "ERROR".equalsIgnoreCase(delivery.getProperties().getType())) {
            ObjectNode j = (ObjectNode) this.mapper.readTree(bytes);
            j.remove("stackTrace");
            throw new TwitterException(j.toString());
          }
          break;
        }
      } catch (ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        break;
      } catch (IOException e) {
        throw new TwitterException(e);
      }
    }
    return response;
  }

  public void close() throws IOException {
    this.connection.close();
  }

}
