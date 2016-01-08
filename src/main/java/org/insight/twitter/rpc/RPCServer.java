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

public class RPCServer {

  public static void main(String[] args) throws InterruptedException {
    // Bots:
    Set<String> bots = RPCServer.getConfiguredBots();

    //EndPoint[] endpoints = new EndPoint[] { EndPoint.STATUSES_LOOKUP };

    // By Group "/statuses/", "/friends/", "/followers/", "/friendships/", "/users/", "/favorites/", "/lists/", "/geo/", "/trends/"
    //EndPoint[] endpoints = EndPoint.fromGroup("/statuses/");

    EndPoint[] endpoints = EndPoint.values();

    // Keep a reference to workers for checking rate limits:
    Set<TwitterWorker> workers = new HashSet<TwitterWorker>();
    // Executors:
    Set<ScheduledExecutorService> exs = new HashSet<ScheduledExecutorService>();

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
  public static Set<String> getConfiguredBots() {
    Set<String> botIDs = new HashSet<>();

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties t4jProperties = new Properties();
    try (InputStream resourceStream = loader.getResourceAsStream("twitter4j.properties")) {
      System.out.println("Reading Bot Configs from: " + "/" + "twitter4j.properties");
      t4jProperties.load(resourceStream);

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

    } catch (IOException e) {
      System.err.println("IO ERROR Reading Properties!");
      e.printStackTrace();
    }
    return botIDs;
  }

}
