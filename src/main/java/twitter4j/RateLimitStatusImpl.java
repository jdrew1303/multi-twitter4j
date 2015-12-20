package twitter4j;


@SuppressWarnings("serial")
public class RateLimitStatusImpl implements RateLimitStatus {

  /**
   * Dummy RateLimitStatus: for endpoints with "no ratelimit" from Twitter API, and aggregates.
   */
  private int remaining;
  private int limit;
  private int resetTimeInSeconds;
  private int secondsUntilReset;


  public RateLimitStatusImpl() {
    int currentTime = (int) (System.currentTimeMillis() / 1000);
    // Some time 15 minutes into the future:
    resetTimeInSeconds = currentTime + 900;
    secondsUntilReset = 900;
  }

  public RateLimitStatusImpl(int remaining, int limit, int resetTimeInSeconds, int secondsUntilReset) {
    this.remaining = remaining;
    this.limit = limit;
    this.resetTimeInSeconds = resetTimeInSeconds;
    this.secondsUntilReset = secondsUntilReset;
  }

  public final RateLimitStatus withRemaining(int remaining) {
    this.remaining = remaining;
    this.limit = remaining;
    return this;
  }

  public final RateLimitStatusImpl mergeWith(RateLimitStatus other) {
    remaining += other.getRemaining();
    limit += other.getLimit();
    if (other.getSecondsUntilReset() < secondsUntilReset) {
      secondsUntilReset = other.getSecondsUntilReset();
      resetTimeInSeconds = other.getResetTimeInSeconds();
    }
    return this;
  }

  @Override
  public final int getRemaining() {
    return remaining;
  }

  @Override
  public final int getLimit() {
    return limit;
  }

  @Override
  public final int getResetTimeInSeconds() {
    return resetTimeInSeconds;
  }

  @Override
  public final int getSecondsUntilReset() {
    return secondsUntilReset;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if ((null == o) || (getClass() != o.getClass())) {
      return false;
    }

    RateLimitStatusImpl other = (RateLimitStatusImpl) o;
    if (this.limit != other.limit) {
      return false;
    }
    if (this.remaining != other.remaining) {
      return false;
    }
    if (this.resetTimeInSeconds != other.resetTimeInSeconds) {
      return false;
    }

    return this.secondsUntilReset == other.secondsUntilReset;

  }

  @Override
  public final int hashCode() {
    int result = this.remaining;
    result = (31 * result) + this.limit;
    result = (31 * result) + this.resetTimeInSeconds;
    result = (31 * result) + this.secondsUntilReset;
    return result;
  }

  @Override
  public final String toString() {
    return "RateLimitStatusJSONImpl{" + "remaining=" + this.remaining + ", limit=" + this.limit + ", resetTimeInSeconds=" + this.resetTimeInSeconds
        + ", secondsUntilReset=" + this.secondsUntilReset + '}';
  }

}
