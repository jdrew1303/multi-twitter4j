package org.insight.twitter.internal;

import twitter4j.RateLimitStatus;

public class InternalRateLimitStatus implements RateLimitStatus {

	/**
	 * Dummy RateLimitStatus: for endpoints with "no ratelimit" from Twitter API, and aggregates.
	 */
	private static final long serialVersionUID = 1L;

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

	public RateLimitStatus withRemaining(int remaining) {
		this.remaining = remaining;
		return this;
	}

	public InternalRateLimitStatus mergeWith(RateLimitStatus other) {

		this.remaining += other.getRemaining();
		this.limit += other.getLimit();

		if (other.getSecondsUntilReset() < this.secondsUntilReset) {
			this.secondsUntilReset = other.getSecondsUntilReset();
			this.resetTimeInSeconds = other.getResetTimeInSeconds();
		}

		return this;
	}

	@Override
	public int getRemaining() {
		return this.remaining;
	}

	@Override
	public int getLimit() {
		return this.limit;
	}

	@Override
	public int getResetTimeInSeconds() {
		return this.resetTimeInSeconds;
	}

	@Override
	public int getSecondsUntilReset() {
		return this.secondsUntilReset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InternalRateLimitStatus other = (InternalRateLimitStatus) o;

		if (limit != other.limit) return false;
		if (remaining != other.remaining) return false;
		if (resetTimeInSeconds != other.resetTimeInSeconds) return false;
		if (secondsUntilReset != other.secondsUntilReset) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = remaining;
		result = 31 * result + limit;
		result = 31 * result + resetTimeInSeconds;
		result = 31 * result + secondsUntilReset;
		return result;
	}

	@Override
	public String toString() {
		return "RateLimitStatusJSONImpl{" +
				"remaining=" + remaining +
				", limit=" + limit +
				", resetTimeInSeconds=" + resetTimeInSeconds +
				", secondsUntilReset=" + secondsUntilReset +
				'}';
	}

}
