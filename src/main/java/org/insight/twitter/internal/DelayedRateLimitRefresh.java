package org.insight.twitter.internal;


/*
 * Add a Bot to a queue (used with Scheduler)
 */
public class DelayedRateLimitRefresh implements Runnable {

	private final TwitterBot bot;
	private final BotQueue queue;

	public DelayedRateLimitRefresh(final TwitterBot bot, final BotQueue queue){
		this.bot = bot;
		this.queue = queue;
	}

	@Override
	public void run() {
		bot.getRateLimitStatus();
		queue.offer(bot);
	}
}
