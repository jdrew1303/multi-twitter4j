package org.insight.twitter.multi;

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
 * Wrapper for Twitter That keeps track of Rate Limits: One Instance per endpoint, per access token. Don't use this directly, Use MultiTwitter!
 */
public final class TwitterBot implements Comparable<TwitterBot> {
  private final String config;
  private final String ident;
  // 2 Instances make rate limit tracking easier, Rate Limit Listener on t4jConnection:
  private final Twitter t4jConnection;
  private final Twitter t4jRateLimitConnection;
  private final EndPoint endpoint;

  // Mutable
  private volatile RateLimitStatus cachedRateLimit = new InternalRateLimitStatus();

  // Create Bot:
  public TwitterBot(final String conf, final EndPoint endpt) throws TwitterException {
    this.config = conf;
    this.endpoint = endpt;
    this.ident = String.format("{\"config\":\"%s\", \"endPoint\":\"%s\"}", conf, endpt.toString());

    if (conf.equalsIgnoreCase("app")) {
      // Need to tweak config before creating Application only Auth token:
      ConfigurationBuilder cb = new ConfigurationBuilder().setApplicationOnlyAuthEnabled(true);
      Configuration t4jConfig = cb.build();
      this.t4jConnection = new TwitterFactory(t4jConfig).getInstance();
      this.t4jConnection.getOAuth2Token();
      this.t4jRateLimitConnection = new TwitterFactory(t4jConfig).getInstance();
      this.t4jRateLimitConnection.getOAuth2Token();
    } else {
      this.t4jConnection = new TwitterFactory(conf).getInstance();
      this.t4jRateLimitConnection = new TwitterFactory(conf).getInstance();
    }

    // Try to set an initial rate limit
    refreshRateLimit();

    RateLimitStatusListener listener = new RateLimitStatusListener() {
      @Override
      public void onRateLimitStatus(final RateLimitStatusEvent event) {
        cachedRateLimit = event.getRateLimitStatus();
      }

      @Override
      public void onRateLimitReached(final RateLimitStatusEvent event) {
        cachedRateLimit = event.getRateLimitStatus();
      }
    };
    this.t4jConnection.addRateLimitStatusListener(listener);

    // Add this TwitterBot to the right Endpoint Queue:
    endpoint.getBotQueue().offer(this);
  }

  public void refreshRateLimit() {
    RateLimitStatus newRateLimitStatus = new InternalRateLimitStatus();
    try {
      // Make a smaller request for the endpoint family only:
      String endpointFamily = endpoint.toString().split("/")[1];
      Map<String, RateLimitStatus> twitterRateLimits = this.t4jRateLimitConnection.getRateLimitStatus(endpointFamily);
      // If the rate limit is defined by t4jConnection, use that - if not, create our own dummy ratelimit object:
      if (twitterRateLimits.containsKey(endpoint.toString())) {
        newRateLimitStatus = twitterRateLimits.get(endpoint.toString());
      } else {
        newRateLimitStatus = new InternalRateLimitStatus().withRemaining(1);
      }
    } catch (TwitterException e) {
      System.err.println(ident + " Error Refreshing Ratelimit for: " + endpoint + "  " + newRateLimitStatus.getRemaining());
      e.printStackTrace();
    }
    this.cachedRateLimit = newRateLimitStatus;
  }

  // Get Last Rate Limit Status
  public RateLimitStatus getCachedRateLimitStatus() {
    return this.cachedRateLimit;
  }

  // Don't use this instance to request anything other than the designated Endpoint.
  // Only Use MultiTwitter! Rate limit Listener will fail otherwise:
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
    int thisLimit = this.getCachedRateLimitStatus().getRemaining();
    int otherLimit = other.getCachedRateLimitStatus().getRemaining();

    int thisWait = this.getCachedRateLimitStatus().getSecondsUntilReset();
    int otherWait = other.getCachedRateLimitStatus().getSecondsUntilReset();

    // Highest limit first:
    int limit = -1 * Integer.valueOf(thisLimit).compareTo(otherLimit);
    // If rate Limit is the same, one with lowest reset time is higher:
    if (limit == 0) {
      return (Integer.valueOf(thisWait).compareTo(otherWait));
    } else {
      return limit;
    }
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    }
    if (!(other instanceof TwitterBot)) {
      return false;
    }
    return other == this || this.ident.equalsIgnoreCase(((TwitterBot) other).ident);
  }

  @Override
  public int hashCode() {
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
