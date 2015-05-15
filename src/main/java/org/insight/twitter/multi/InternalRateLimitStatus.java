package org.insight.twitter.multi;

import twitter4j.RateLimitStatus;

@SuppressWarnings("serial")
public class InternalRateLimitStatus implements RateLimitStatus {

  /**
   * Dummy RateLimitStatus: for endpoints with "no ratelimit" from Twitter API, and aggregates.
   */
  private int remaining = 0;
  private int limit = 0;
  private int resetTimeInSeconds;
  private int secondsUntilReset;

  public InternalRateLimitStatus() {
    int currentTime = (int) (System.currentTimeMillis() / 1000);
    // Some time 15 minutes into the future:
    this.resetTimeInSeconds = currentTime + 900;
    this.secondsUntilReset = 900;

  }

  public final RateLimitStatus withRemaining(final int remaining) {
    this.remaining = 1;
    return this;
  }

  public final InternalRateLimitStatus mergeWith(final RateLimitStatus other) {
    this.remaining += other.getRemaining();
    this.limit += other.getLimit();
    if (other.getSecondsUntilReset() < this.secondsUntilReset) {
      this.secondsUntilReset = other.getSecondsUntilReset();
      this.resetTimeInSeconds = other.getResetTimeInSeconds();
    }
    return this;
  }

  @Override
  public final int getRemaining() {
    return this.remaining;
  }

  @Override
  public final int getLimit() {
    return this.limit;
  }

  @Override
  public final int getResetTimeInSeconds() {
    return this.resetTimeInSeconds;
  }

  @Override
  public final int getSecondsUntilReset() {
    return this.secondsUntilReset;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }

    InternalRateLimitStatus other = (InternalRateLimitStatus) o;
    if (limit != other.limit) {
      return false;
    }
    if (remaining != other.remaining) {
      return false;
    }
    if (resetTimeInSeconds != other.resetTimeInSeconds) {
      return false;
    }
    if (secondsUntilReset != other.secondsUntilReset) {
      return false;
    }

    return true;
  }

  @Override
  public final int hashCode() {
    int result = remaining;
    result = (31 * result) + limit;
    result = (31 * result) + resetTimeInSeconds;
    result = (31 * result) + secondsUntilReset;
    return result;
  }

  @Override
  public final String toString() {
    return "RateLimitStatusJSONImpl{" + "remaining=" + remaining + ", limit=" + limit + ", resetTimeInSeconds=" + resetTimeInSeconds + ", secondsUntilReset="
        + secondsUntilReset + '}';
  }

}
