package org.insight.twitter.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitMQ {

  public static Entry<Channel, QueueingConsumer> createChannelConsumer(String queueName, String consumerTag) {
    try {

      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Properties properties = new Properties();
      try (InputStream resourceStream = loader.getResourceAsStream("twitter4j.properties")) {
        properties.load(resourceStream);
      }

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(properties.getProperty("rabbitmq"));

      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();

      channel.queueDeclare(queueName, true, false, false, new HashMap<String, Object>());
      channel.basicQos(1); // request 1 Call at a time.

      QueueingConsumer consumer = new QueueingConsumer(channel);
      channel.basicConsume(queueName, false, consumerTag, consumer);

      return new AbstractMap.SimpleEntry<Channel, QueueingConsumer>(channel, consumer);

    } catch (IOException | TimeoutException e) {
      System.err.println("FAILED TO CONNECT OT RABBITMQ!");
    }
    return null;
  }
}
