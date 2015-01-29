package org.insight.twitter.internal;

/*
 * Add a Bot to a queue (used with Scheduler)
 */
class DelayedRateLimitRefresh implements Runnable {

    private final TwitterBot bot;
    private final BotQueue queue;

    public DelayedRateLimitRefresh(final TwitterBot tbot, final BotQueue q) {
        this.bot = tbot;
        this.queue = q;
    }

    @Override
    public final void run() {
        bot.refreshRateLimit();
        queue.offer(bot);
    }
}
