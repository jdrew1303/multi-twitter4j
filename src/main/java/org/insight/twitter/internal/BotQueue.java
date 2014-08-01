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
	public boolean offer(TwitterBot e) {
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
	public TwitterBot poll() {
		TwitterBot bot = null;
		do {
			bot = super.poll();
			if (bot == null) {
				System.out.println( endpoint + " NO MORE BOTS IN QUEUE - WAIT!!! ");
				System.out.println("");
				return null;
			}
		} while (!checkBot(bot));
		//System.out.println( endpoint + " Non Blocking Took bot: " + bot + " Ratelimit:" + bot.getCachedRateLimitStatus().getRemaining());
		return bot;
	}


	// Blocking take - return bot after a wait.
	@Override
	public TwitterBot take() {
		TwitterBot bot = null;
		do {
			try {				
				if (this.isEmpty() || (this.peek() == null)) {
					System.out.println(String.format("** WARNING: Rate Limits for %s exhaused! Likely to wait 15 min for next rate limit window!" , endpoint));
				}
				//System.out.println("TWITTERAPIDEBUG:0 " + endpoint + "Q Blocking taking bot");
				bot = super.take();
				//System.out.println("TWITTERAPIDEBUG:1 " + endpoint + "Q Blocking checking bot: " + bot + " Ratelimit:" + bot.getCachedRateLimitStatus().getRemaining());
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		} while (!checkBot(bot));
		//System.out.println("TWITTERAPIDEBUG:2 " + endpoint + "Q Blocking Took bot: " + bot + " Ratelimit:" + bot.getCachedRateLimitStatus().getRemaining());
		return bot;
	}


	private boolean checkBot(TwitterBot bot) {
		if (bot == null) {
			return false;
		}
		int remaining = bot.getCachedRateLimitStatus().getRemaining();

		// Double check here, in case a bot was on 0, and there were no requests for >15 min.
		long now = new Date().getTime();
		long rateLimitAge = (bot.getCachedRateLimitStatus().getResetTimeInSeconds() * 1000) - now;
		if ((remaining) < 1 && (rateLimitAge > 0)) {
			remaining = bot.getRateLimitStatus().getRemaining();
		}
		
		// If the head of the queue is 0, it means we've run out of bots. Time to sleep:
		if (remaining < 1) {
			//System.out.println("Queue refresh in a while for .. "+ bot.toString());
			long howLong = bot.getCachedRateLimitStatus().getSecondsUntilReset();
			if (howLong < 1) {
				// Wait 10 seconds if reset time is 0 or negative.
				howLong = 10;
				//System.out.println("Reset Time too close, adding a few seconds longer..."+ bot.toString());
			} else {
				// N seconds + 10 because reset times can be flaky.	
				howLong = howLong + 5;			
			}
			
			scheduler.schedule(new DelayedRateLimitRefresh(bot, this), howLong, TimeUnit.SECONDS);
			System.out.println("Put "+ bot.toString() +"  to sleep for " + howLong);
			return false;

		} else {
			return true;
		}
	}

	public Set<TwitterBot> getLoadedBots() {
		return this.loadedBots;
	}

	public void shutdown() {
		scheduler.shutdown();
	}

	public void shutdown(boolean force) {
		scheduler.shutdownNow();
	}
		
	public boolean reloadConfiguredBots(final Set<String> cnfBots) {
		
		Set<String> configureBots = new HashSet<String>(cnfBots);
		// Add Application Only endpoints:
		if (endpoint.hasApplicationOnlySupport()) {
			configureBots.add("app");
		}
		
		// If there are not bots loaded yet:
		if (loadedBots.size() == 0) {
			System.out.println(endpoint + " 0 Bots loaded, reloading: " + configureBots.size() + " from memory "); 
		}
		
		// If bots are loaded, check for new additions:
		else 			
		{
			//System.err.println("HAVE KNOWN BOTS! CHECKING!!");			
			if ( loadedBots.size() == configureBots.size() ) {
				//System.err.println(endpoint + " KNOWN BOTS! NOT RELOADING! " + loadedBots.size()); 
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
