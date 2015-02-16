package org.insight.twitter.multi;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import twitter4j.TwitterException;

/*
 * An extended PriorityBlockingQueue for Twitter bots - manages Queue of bots, puts them to sleep if needed. At most, 1 BotQueue per endpoint! - Slower, but more
 * robust and reliable with Twitter.
 */

@SuppressWarnings("serial")
public class BotQueue extends PriorityBlockingQueue<TwitterBot> {
  // Scheduled service to revive sleeping bots
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final Set<TwitterBot> loadedBots = Collections.newSetFromMap(new ConcurrentHashMap<TwitterBot, Boolean>());
  private final EndPoint endpoint;

  public BotQueue(final EndPoint endpoint) {
    super(1);
    this.endpoint = endpoint;
  }

  @Override
  public final boolean offer(final TwitterBot e) {
    // Prevent Duplicates During Reloads:
    if (!super.contains(e)) {
      loadedBots.add(e);
      return super.offer(e);
    } else {
      return false;
    }
  }

  // Non blocking Poll - return null if queue has no rate limits left.
  @Override
  public final TwitterBot poll() {
    TwitterBot bot;
    do {
      bot = super.poll();
      if (bot == null) {
        System.err.println(String.format("** WARNING: Rate Limits for %s exhaused! Wait up to 15 min for next rate limit window!", endpoint));
        return null;
      }
    } while (checkNext(bot));

    return bot;
  }

  // Blocking take - return bot after a wait.
  @Override
  public final TwitterBot take() {
    TwitterBot bot = null;
    do {
      try {
        if (this.isEmpty() || (this.peek() == null)) {
          System.out.println(String.format("** WARNING: Rate Limits for %s exhaused! Wait up to 15 min for next rate limit window!", endpoint));
        }
        bot = super.take();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } while (checkNext(bot));

    return bot;
  }

  private boolean checkNext(final TwitterBot bot) {
    if (bot == null) {
      return true;
    }

    int remaining = bot.getCachedRateLimitStatus().getRemaining();
    // Double check here, in case a bot is on 0, and ResetTime has passed:
    long rateLimitAge = (bot.getCachedRateLimitStatus().getResetTimeInSeconds() * 1000) - (new Date().getTime());
    if ((remaining < 1) && (rateLimitAge < 0)) {
      bot.refreshRateLimit();
      remaining = bot.getCachedRateLimitStatus().getRemaining();
    }
    if (remaining < 1) {
      // Wait 10 seconds if reset time is 0 or negative.
      // N seconds + 5 because reset times can be flaky.
      long howLong = bot.getCachedRateLimitStatus().getSecondsUntilReset();
      howLong = (howLong < 1) ? howLong = 10 : howLong + 5;
      scheduler.schedule(new DelayedRateLimitRefresh(bot, this), howLong, TimeUnit.SECONDS);
      System.out.println("Put " + bot.toString() + "  to sleep for " + howLong);
      return true;
    } else {
      // Bot is good, no need to continue checking:
      return false;
    }
  }

  public final Set<TwitterBot> getLoadedBots() {
    return this.loadedBots;
  }

  public final void shutdown() {
    scheduler.shutdown();
  }

  public final void shutdownNow() {
    scheduler.shutdownNow();
  }

  public final void reloadConfiguredBots(final Set<String> confBots) {
    Set<String> configureBots = new HashSet<>(confBots);
    // Add Application Only endpoints:
    if (endpoint.hasApplicationOnlySupport()) {
      configureBots.add("app");
    }

    // If there are no bots loaded yet:
    if (loadedBots.size() == 0) {
      System.out.println(endpoint + " 0 Bots loaded, reloading: " + configureBots.size() + " from conf ");
    } else {
      // If bots are loaded, check for new additions:
      if (loadedBots.size() == configureBots.size()) {
        return;
      } else {
        System.err.println(endpoint + " KNOWN BOTS! NEW FOUND! RELOADING! loaded: " + loadedBots.size() + " conf: " + configureBots.size());
        // Remove already known bots:
        for (TwitterBot bot : loadedBots) {
          configureBots.remove(bot.getConfig());
        }
      }
    }

    // Any new bots can be loaded, if empty, this won't run:
    for (String conf : configureBots) {
      TwitterBot newBot;
      try {
        newBot = new TwitterBot(conf, endpoint);
        System.out.println("Created :" + newBot + " " + newBot.getCachedRateLimitStatus());
      } catch (TwitterException e) {
        System.err.println("FAILED TO CREATE BOT: " + conf);
        e.printStackTrace();
      }
    }
  }
}
