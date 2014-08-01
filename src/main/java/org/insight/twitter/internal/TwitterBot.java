package org.insight.twitter.internal;

import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/*
 * Wrapper for Twitter That keeps track of Rate Limits: One Instance per endpoint, per access token.
 * 
 * Don't use this directly, Use MultiTwitter!
 * 
 */

public final class TwitterBot implements Comparable<TwitterBot>{

	//private Logger log = Logger.getLogger( getClass().getSimpleName() );

	private final String config;
	private final String ident;
	
	private final Twitter t4jConnection;
	private final Twitter t4jRateLimitConnection;
	
	private final EndPoint endpoint;

	// Mutable
	volatile private RateLimitStatus cachedRateLimit = new InternalRateLimitStatus();

	// Create Bot:
	public TwitterBot(final String conf, final EndPoint endpoint) throws TwitterException {
		
		this.config = conf;
		this.ident = String.format("%s['%s']", conf, endpoint.toString());
		this.endpoint = endpoint;

		if (conf.equalsIgnoreCase("app")) {
			// Need to tweak config before creating Application only Auth token:
			ConfigurationBuilder cb = new ConfigurationBuilder().setApplicationOnlyAuthEnabled(true);
			Configuration config = cb.build();
			
			this.t4jConnection = new TwitterFactory(config).getInstance();
			this.t4jConnection.getOAuth2Token();
			
			this.t4jRateLimitConnection = new TwitterFactory(config).getInstance();
			this.t4jRateLimitConnection.getOAuth2Token();
			
		}  else {
		
			this.t4jConnection = new TwitterFactory(conf).getInstance();
			this.t4jRateLimitConnection = new TwitterFactory(conf).getInstance();

		}

		// Try to set an initial rate limit
		getRateLimitStatus();

		// Listener reacts to any rate limit status... so to avoid adding rate limit status of getRateLimitStatus, add listener after requesting initial ratelimit:
		// This "error" will happen during Queue if Bot is out of rate limit and getRateLimitStatus is called, but is quickly replaced by the proper rate limit
		RateLimitStatusListener listener = new RateLimitStatusListener() {
			@Override
			public void onRateLimitStatus(RateLimitStatusEvent event) {
				cachedRateLimit = event.getRateLimitStatus();
				//System.out.println(ident + " Listener Set RateLimit: " + endpoint + "  " + event.getRateLimitStatus().toString());
			}

			@Override
			public void onRateLimitReached(RateLimitStatusEvent event) {
				//System.out.println(ident + " RATE LIMIT REACHED!!: " + endpoint + "  " + event.getRateLimitStatus().toString());
				cachedRateLimit = event.getRateLimitStatus();
			}
		};
		this.t4jConnection.addRateLimitStatusListener(listener);

		// Add this TwitterBot to the right Endpoint Queue:
		//System.err.println("Created Bot: " + ident);
		
		endpoint.getBotQueue().offer(this);
	}

	public RateLimitStatus getRateLimitStatus() {

		RateLimitStatus newRateLimitStatus = new InternalRateLimitStatus();

		try {
			// Make a smaller request for the endpoint family only:
			String endpointFamily = endpoint.toString().split("/")[1];
			
			Map<String, RateLimitStatus> twitterRateLimits = this.t4jRateLimitConnection.getRateLimitStatus(endpointFamily);
			
			//System.out.println("-----REQUESTING NEW RATE LIMIT------");
			//System.out.println(ident + " Ratelimits: " + TwitterObjectFactory.getRawJSON(twitterRateLimits));
			//System.out.println("---------------");
			
			// If the rate limit is defined by t4jConnection, use that - if not, create our own dummy ratelimit object:
			if (twitterRateLimits.containsKey(endpoint.toString())) {
				newRateLimitStatus = twitterRateLimits.get(endpoint.toString());
				//System.out.println(ident + " Method New Ratelimit for: " + endpoint + "  " + newRateLimitStatus.getRemaining() + " " + newRateLimitStatus.toString());
			} else {
				newRateLimitStatus = new InternalRateLimitStatus().withRemaining(1);
				//System.out.println(ident + " Method Faking Ratelimit for: " + endpoint + "  " + newRateLimitStatus.getRemaining() + " " + newRateLimitStatus.toString());
			}
				
		} catch (TwitterException e) {
			//System.err.println(ident + " Method Ratelimit? Error fetching rate limits!: " + endpoint + "  " + cachedRateLimit.getRemaining());
			// Failed to retrieve rate limit, we'll play it safe, and create a fake rate limit with 0 remaining:
			newRateLimitStatus = new InternalRateLimitStatus();		
			System.err.println(ident + " Error Refreshing Ratelimit for: " + endpoint + "  " + newRateLimitStatus.getRemaining());
			e.printStackTrace();
		}
		
		this.cachedRateLimit = newRateLimitStatus;
		
		//System.out.println(ident + " Method Set Ratelimit: " + endpoint + "  " + cachedRateLimit.getRemaining());
		return cachedRateLimit;

	}

	// Get Last Rate Limit Status
	public RateLimitStatus getCachedRateLimitStatus () {
		return this.cachedRateLimit;
	}

	// Don't use this instance to request anything other than the designated Endpoint.
	// Only Use MultiTwitter!
	// Rate limit Listener will fail if you do so!
	public final Twitter getTwitter() {
		return this.t4jConnection;	
	}

	public final EndPoint getEndPoint() {
		return this.endpoint;	
	}

	@Override
	public int compareTo(TwitterBot other) {

		// Compare 2 Bots on RateLimits: by Rate Limit Remaining, and then by Reset Time:
		// 1 Bot per access token, per endpoint:
		int this_limit = this.getCachedRateLimitStatus().getRemaining();
		int other_limit = other.getCachedRateLimitStatus().getRemaining();

		int this_wait = this.getCachedRateLimitStatus().getSecondsUntilReset();
		int other_wait = other.getCachedRateLimitStatus().getSecondsUntilReset();

		// Highest limit first:
		int limit = -1 * Integer.valueOf(this_limit).compareTo(other_limit);

		// If rate Limit is the same, one with lowest reset time is higher:
		if (limit == 0) {
			return (Integer.valueOf( this_wait ).compareTo( other_wait ));
		} else {
			return limit; 
		}

	}
	
	@Override
	public boolean equals(Object other){
		 if (other == null) return false;
		 if (other == this) return true;
		 if (!(other instanceof TwitterBot)) return false;
		 
		 TwitterBot otherBot = (TwitterBot)other;
		 
		 if (this.ident.equalsIgnoreCase(otherBot.ident)) {
			 return true;
		 } else {
			 return false;
		 }
	}
	
	@Override 
	public int hashCode(){
		return this.ident.hashCode();
	}	
		
	@Override
	public String toString() {
		return this.ident;
	}
	
	public String getConfig() {
		return this.config;
	}

}
