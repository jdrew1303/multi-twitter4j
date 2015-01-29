package org.insight.twitter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insight.twitter.internal.EndPoint;
import org.insight.twitter.internal.InternalRateLimitStatus;
import org.insight.twitter.internal.LimitedTwitterResources;
import org.insight.twitter.internal.TwitterBot;

import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;

/*
 * Only implements REST API calls that can be spread over multiple accounts.
 * 
 * Should be straight forward to add unimplemented methods, if you really need them.
 */
public abstract class MultiTwitter extends LimitedTwitterResources {

	// Read configs from file
	private final Set<String> configuredBots;	

	// How to use the Queue - Block, or throw rate limit exceptions
	private final boolean blockOnRateLimit;

	// Use blocking queues by default, configs from twitter4j.properties
	public MultiTwitter() {
		this(true, "twitter4j.properties");
	}

	public MultiTwitter(final boolean blockOnRateLimit, String configFile) {
		this.blockOnRateLimit = blockOnRateLimit;
		this.configuredBots = getConfiguredBots(configFile);
	}

	/*
	 * All unimplemented methods will throw UnsupportedMethodException 
	 */	
	
	/*
	 * --------------------------
	 * Utility Methods:
	 * --------------------------
	 */
	
	/*
	 * HelpResources
	 */

	@Override
	public Map<String, RateLimitStatus> getRateLimitStatus(String... resources) throws TwitterException {
		EndPoint[] endpoints = new EndPoint[resources.length];
		for (int i=0; i < resources.length; i++) {
			endpoints[i] = EndPoint.fromString(resources[i]);			
		}
		return getRateLimitStatus(endpoints);
	}

	@Override
	public Map<String, RateLimitStatus> getRateLimitStatus()	throws TwitterException {
		return 	getRateLimitStatus(EndPoint.values());
	}

	/*
	 * Get combined Rate Limit for an endpoint (or several)
	 */
	public Map<String, RateLimitStatus> getRateLimitStatus(EndPoint[] endpoints) throws TwitterException {
		Map<String, RateLimitStatus> rateLimits = new HashMap<String, RateLimitStatus>();
		for (EndPoint target : endpoints) {
			rateLimits.putAll(getRateLimitStatus(target));
		}		
		return rateLimits;
	}

	/*
	 * Get Combined Rate Limit from all available bots
	 */
	public Map<String, RateLimitStatus> getRateLimitStatus(EndPoint endpoint) throws TwitterException {
		Map<String, RateLimitStatus> rateLimit = new HashMap<String, RateLimitStatus>();
		InternalRateLimitStatus rl = new InternalRateLimitStatus();
		Set<TwitterBot> allActiveBots = endpoint.getBotQueue().getLoadedBots();
		for (TwitterBot bot : allActiveBots) {
			rl = rl.mergeWith(bot.getCachedRateLimitStatus());
		}
		rateLimit.put(endpoint.toString(), rl);
		return rateLimit;
	}

	/*
	 * Read config file, extract all the access tokens.
	 */
	public static Set<String> getConfiguredBots(String configFile) {
		Set<String> botIDs = new HashSet<String>();
		Properties t4jProperties = new Properties();
		try {
			System.out.println("Reading Bot Configs from: " + "/" + configFile);
			InputStream in = new FileInputStream(configFile);
			t4jProperties.load(in);
			in.close();
		} catch (IOException e) {
			System.err.println("IO ERROR Reading Properties!");
			e.printStackTrace();
			return botIDs;
		}
		// Find bots we have...
		// Config file must be well formed!
		Pattern p = Pattern.compile( "bot\\.(.*?)\\.oauth\\.accessTokenSecret" );
		for (String key : t4jProperties.stringPropertyNames()) {	
			Matcher m = p.matcher(key);
			if( m.find() )	{
				String strBotNum = m.group(1);
				botIDs.add( "bot." + strBotNum );
				System.out.println("Detected config for: " + strBotNum );
			}
		}
		return botIDs;
	}

	/*
	 * Taking bots from Queue:
	 */
	private TwitterBot takeBot(final EndPoint endpoint) throws TwitterException {
		// Either Block with take() until a bot is available, or throw rate limit exception:
		// Lazy load bots to endpoints:
		endpoint.getBotQueue().reloadConfiguredBots(configuredBots);
		TwitterBot bot = blockOnRateLimit ? endpoint.getBotQueue().take() : endpoint.getBotQueue().poll();
		if (bot == null) {
			throw new TwitterException(endpoint + " Queue is EMPTY! Rate Limit for all Bots Reached!");
		}	
		return bot;
	}

	/*
	 * Returning bots to Queue:
	 */
	private void releaseBot(final TwitterBot bot) {
		//Always return bot to queue:
		if (bot != null) {
			//System.out.println("Returning bot " + bot.toString() + " back to queue, ratelimit remaining: " + bot.getCachedRateLimitStatus().getRemaining()); 
			bot.getEndPoint().getBotQueue().offer(bot);
		}
	}

	/*
	 * Wrap Responses from Twitter, Retry on failures, throw appropriate Exceptions.
	 * This provides retry functions to any method:
	 * Can be replaced with something else that manages bots & calls.
	 */
	public abstract class TwitterCommand<T> {		
		public T getResponse(EndPoint endpoint) throws TwitterException {			
			T result = null;
			int retryLimit = configuredBots.size();
			while (true) {
				TwitterBot bot = null;
				try {
					bot = takeBot(endpoint);
					result = fetchResponse(bot);
					break;				
				} catch (TwitterException e) {
					if (e.exceededRateLimitation() || e.isCausedByNetworkIssue()) {
						if (--retryLimit <= 0) {
							System.out.println("Retried " + configuredBots.size() + " times, giving up.");
							throw e;
						}
					}
					/*
					 * Skip retrying Private / Deleted / Banned accounts
					 */
					if (e.resourceNotFound() || e.getStatusCode() == 401) {
						System.out.println("Resource not found / Unauthorized, Giving Up.");
						throw e;
					}
					System.out.println("Temporary Rate Limit / Connection Error!, Retrying " + retryLimit + " more times... " + e.toString());
				} finally {
					releaseBot(bot);
				}
			}
			return result;
		}
		/*
		 * Methods must extend this: 
		 */
		public abstract T fetchResponse(TwitterBot bot) throws TwitterException;
	}

}
