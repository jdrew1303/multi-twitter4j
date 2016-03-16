package org.insight.twitter.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insight.twitter.util.EndPoint;

import twitter4j.TwitterException;

import com.rabbitmq.client.AMQP.Queue.DeleteOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RPCServer {

  public static void main(String[] args) throws InterruptedException, IOException {

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties properties = new Properties();
    try (InputStream resourceStream = loader.getResourceAsStream("twitter4j.properties")) {
      properties.load(resourceStream);
    }

    // Bots:

    Set<String> bots = RPCServer.getConfiguredBots(properties);

    //EndPoint[] endpoints = new EndPoint[] { EndPoint.SEARCH_TWEETS };

    //}, EndPoint.FAVORITES_LIST, EndPoint.FOLLOWERS_IDS,
    //        EndPoint.FRIENDS_IDS, EndPoint.LISTS_MEMBERSHIPS };

    // By Group "/statuses/", "/friends/", "/followers/", "/friendships/", "/users/", "/favorites/", "/lists/", "/geo/", "/trends/"

    EndPoint[] endpoints = EndPoint.fromGroup("search", "statuses", "friends", "followers", "friendships", "users", "favorites", "lists");
    //EndPoint[] endpoints = EndPoint.fromGroup("lists", "users", "search");


    //EndPoint[] endpoints = EndPoint.values();

    // Keep a reference to workers for checking rate limits:
    Set<TwitterWorker> workers = new HashSet<TwitterWorker>();
    // Executors:
    Set<ScheduledExecutorService> exs = new HashSet<ScheduledExecutorService>();


    // Clear queues:

    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(properties.getProperty("rabbitmq"));
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();
      for (EndPoint endpoint : endpoints) {
        //channel.queueDeleteNoWait(endpoint.toString(), false, false);
        DeleteOk del = channel.queueDelete(endpoint.toString());
        System.out.println(del.getMessageCount());
      }
    } catch (Exception e) {
    }

    // Create Bots:
    for (EndPoint endpoint : endpoints) {
      Set<String> loadBots = new HashSet<>(bots);
      System.out.println("\nEndpoint:: " + endpoint);
      if (endpoint.hasApplicationOnlySupport()) {
        loadBots.add("bot.app");
      }

      ScheduledExecutorService executor = Executors.newScheduledThreadPool(loadBots.size());

      System.out.println("Loading " + loadBots.size() + " Bots: " + new ArrayList<>(loadBots));

      for (String bot : loadBots) {
        try {
          //System.out.println("Adding Worker: " + bot + endpoint);
          TwitterWorker worker = new TwitterWorker(bot, endpoint, executor);
          workers.add(worker);
          executor.execute(worker);
        } catch (TwitterException e) {
          System.err.println("FAILED TO ADD WORKER: " + bot + endpoint);
          e.printStackTrace();
        }
      }

      exs.add(executor);
      Thread.sleep(2000);
    } // endpoints

    // Start RateLimit monitor:
    ExecutorService rateLimitEx = Executors.newFixedThreadPool(1);
    rateLimitEx.execute(new RatelimitWorker(workers));

  }

  /*
   * -------------------------- Utility Methods: --------------------------
   */

  /*
   * Read config file, extract all the access tokens.
   */
  public static Set<String> getConfiguredBots(Properties t4jProperties) {
    Set<String> botIDs = new HashSet<>();
    System.out.println("Reading Bot Configs from: " + "/" + "twitter4j.properties");
    // Find bots we have...
    // Config file must be well formed!
    Pattern p = Pattern.compile("bot\\.(.*?)\\.oauth\\.accessTokenSecret");
    for (String key : t4jProperties.stringPropertyNames()) {
      Matcher m = p.matcher(key);
      if (m.find()) {
        String strBotNum = m.group(1);
        botIDs.add("bot." + strBotNum);
        System.out.println("Detected config for: " + strBotNum);
      }
    }
    return botIDs;
  }

}
