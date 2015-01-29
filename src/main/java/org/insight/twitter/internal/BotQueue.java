package org.insight.twitter.internal;

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
 * An extended PriorityBlockingQueue for Twitter bots - manages Queue of bots, puts them to sleep if needed.
 * At most, 1 BotQueue per endpoint! - Slower, but more robust and reliable with Twitter
 */

public class BotQueue extends PriorityBlockingQueue<TwitterBot>{
	private static final long serialVersionUID = 1L;
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
		if ( !super.contains(e) ) {
			loadedBots.add(e);
			//System.err.println("q Adding Bot to Queue!! " + endpoint + " " + e );
			return super.offer(e);
		} else {
			//System.err.println("q Bot EXISTS !! " + endpoint + " " + e );
			return false;
		}
	}

	// Non blocking Poll - return null if queue has no rate limits left.
	@Override
    public final TwitterBot poll() {
		TwitterBot bot = null;
		do {
			bot = super.poll();
			if (bot == null) {
				System.err.println(String.format("** WARNING: Rate Limits for %s exhaused! Wait up to 15 min for next rate limit window!" , endpoint));
				return null;
			}
		} while (!checkBot(bot));

		return bot;
	}

	// Blocking take - return bot after a wait.
	@Override
    public final TwitterBot take() {
		TwitterBot bot = null;
		do {
			try {				
				if (this.isEmpty() || (this.peek() == null)) {
					System.out.println(String.format("** WARNING: Rate Limits for %s exhaused! Wait up to 15 min for next rate limit window!" , endpoint));
				}
				bot = super.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		} while (!checkBot(bot));

		return bot;
	}

	private boolean checkBot(final TwitterBot bot) {
		if (bot == null) {
			return false;
		}
		int remaining = bot.getCachedRateLimitStatus().getRemaining();
		// Double check here, in case a bot was on 0, and there were no requests for >15 min.
		long now = new Date().getTime();
		long rateLimitAge = (bot.getCachedRateLimitStatus().getResetTimeInSeconds() * 1000) - now;
		if ((remaining < 1) && (rateLimitAge > 0)) {
			bot.refreshRateLimit();
			remaining = bot.getCachedRateLimitStatus().getRemaining();
		}
		// If the head of the queue is 0, it means we've run out of bots. Time to sleep:
		if (remaining < 1) {
			//System.out.println("Queue refresh in a while for .. "+ bot.toString());
			long howLong = bot.getCachedRateLimitStatus().getSecondsUntilReset();
			if (howLong < 1) {
				// Wait 10 seconds if reset time is 0 or negative.
				howLong = 10;
			} else {
				// N seconds + 5 because reset times can be flaky.	
				howLong += 5;			
			}
			scheduler.schedule(new DelayedRateLimitRefresh(bot, this), howLong, TimeUnit.SECONDS);
			System.out.println("Put "+ bot.toString() +"  to sleep for " + howLong);
			return false;
		} else {
			return true;
		}
	}

	public final Set<TwitterBot> getLoadedBots() {
		return this.loadedBots;
	}

	public final void shutdown() {
		scheduler.shutdown();
	}

	public final void shutdown(final boolean force) {
		scheduler.shutdownNow();
	}
		
	public final boolean reloadConfiguredBots(final Set<String> confBots) {
		Set<String> configureBots = new HashSet<String>(confBots); 
		// Add Application Only endpoints:
		if (endpoint.hasApplicationOnlySupport()) {
			configureBots.add("app");
		}
		
		// If there are not bots loaded yet:
		if (loadedBots.size() == 0) {
			System.out.println(endpoint + " 0 Bots loaded, reloading: " + configureBots.size() + " from memory "); 
		} else 	{
			// If bots are loaded, check for new additions:
			if ( loadedBots.size() == configureBots.size() ) {
				return true;	
			} else {
				System.err.println(endpoint + " KNOWN BOTS! NEW FOUND! RELOADING! loaded: " +loadedBots.size() + " conf: " + configureBots.size()); 
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
					System.err.println("Created :" + newBot + " " + newBot.getCachedRateLimitStatus() + "");
				} catch (TwitterException e) {
					System.err.println("FAILED TO CREATE BOT: " + conf);
					e.printStackTrace();
				}
		}
		return true;
	}
}
