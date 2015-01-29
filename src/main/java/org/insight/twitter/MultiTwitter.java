package org.insight.twitter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insight.twitter.internal.EndPoint;
import org.insight.twitter.internal.InternalRateLimitStatus;
import org.insight.twitter.internal.LimitedTwitterResources;
import org.insight.twitter.internal.TwitterBot;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import twitter4j.UserList;

/*
 * Only implements REST API calls that can be spread over multiple accounts.
 * 
 * Should be straight forward to add unimplemented methods, if you really need them.
 */
abstract class MultiTwitter extends LimitedTwitterResources {

    // Read configs from file
    private final Set<String> configuredBots;

    // How to use the Queue - Block, or throw rate limit exceptions
    private final boolean blockOnRateLimit;

    // Use blocking queues by default, configs from twitter4j.properties
    public MultiTwitter() {
        this(true, "twitter4j.properties");
    }

    public MultiTwitter(final boolean blocking, final String configFile) {
        this.blockOnRateLimit = blocking;
        this.configuredBots = getConfiguredBots(configFile);
    }

    /*
     * All unimplemented methods will throw UnsupportedMethodException
     */
    /*
     * -------------------------- Utility Methods: --------------------------
     */

    /*
     * HelpResources
     */

    @Override
    public final Map<String, RateLimitStatus> getRateLimitStatus(
            final String... resources) throws TwitterException {
        EndPoint[] endpoints = new EndPoint[resources.length];
        for (int i = 0; i < resources.length; i++) {
            endpoints[i] = EndPoint.fromString(resources[i]);
        }
        return getRateLimitStatus(endpoints);
    }

    @Override
    public final Map<String, RateLimitStatus> getRateLimitStatus()
            throws TwitterException {
        return getRateLimitStatus(EndPoint.values());
    }

    /*
     * Get combined Rate Limit for an endpoint (or several)
     */
    final Map<String, RateLimitStatus> getRateLimitStatus(
            final EndPoint[] endpoints) {
        Map<String, RateLimitStatus> rateLimits = new HashMap<>();
        for (EndPoint target : endpoints) {
            rateLimits.putAll(getRateLimitStatus(target));
        }
        return rateLimits;
    }

    /*
     * Get Combined Rate Limit from all available bots
     */
    final Map<String, RateLimitStatus> getRateLimitStatus(
            final EndPoint endpoint) {
        Map<String, RateLimitStatus> rateLimit = new HashMap<>();
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
    private static Set<String> getConfiguredBots(final String configFile) {
        Set<String> botIDs = new HashSet<>();
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
        Pattern p = Pattern.compile("bot\\.(.*?)\\.oauth\\.accessTokenSecret");
        for (String key : t4jProperties.stringPropertyNames()) {
            Matcher m = p.matcher(key);
            if (m.find()) {
                String strBotNum = m.group(1);
                botIDs.add("bot." + strBotNum);
                System.out.println("Detected config for: " + strBotNum);
            }
        }
        return botIDs;
    }

    /*
     * Taking bots from Queue:
     */
    private TwitterBot takeBot(final EndPoint endpoint) throws TwitterException {
        // Either Block with take() until a bot is available, or throw rate
        // limit exception:
        // Lazy load bots to endpoints:
        endpoint.getBotQueue().reloadConfiguredBots(configuredBots);

        TwitterBot bot = blockOnRateLimit ? endpoint.getBotQueue().take()
                : endpoint.getBotQueue().poll();

        if (bot == null) {
            throw new TwitterException(endpoint
                    + " Queue is EMPTY! Rate Limit for all Bots Reached!");
        }
        return bot;
    }

    /*
     * Returning bots to Queue:
     */
    private void releaseBot(final TwitterBot bot) {
        // Always return bot to queue:
        if (bot != null) {
            // System.out.println("Returning bot " + bot.toString() +
            // " back to queue, ratelimit remaining: " +
            // bot.getCachedRateLimitStatus().getRemaining());
            bot.getEndPoint().getBotQueue().offer(bot);
        }
    }

    /*
     * Wrap Responses from Twitter, Retry on failures, throw appropriate
     * Exceptions. This provides retry functions to any method: Can be replaced
     * with something else that manages bots & calls.
     */
    public abstract class TwitterCommand<T> {
        public final T getResponse(final EndPoint endpoint)
                throws TwitterException {
            T result;
            int retryLimit = configuredBots.size();
            while (true) {
                TwitterBot bot = null;
                try {
                    bot = takeBot(endpoint);
                    result = fetchResponse(bot);
                    break;
                } catch (TwitterException e) {
                    if (e.exceededRateLimitation()
                            || e.isCausedByNetworkIssue()) {
                        if (--retryLimit <= 0) {
                            System.out.println("Retried "
                                    + configuredBots.size()
                                    + " times, giving up.");
                            throw e;
                        }
                    }
                    /*
                     * Skip retrying Private / Deleted / Banned accounts
                     */
                    if (e.resourceNotFound() || (e.getStatusCode() == 401)) {
                        System.out
                                .println("Resource not found / Unauthorized, Giving Up.");
                        throw e;
                    }
                    System.out
                            .println("Temporary Rate Limit / Connection Error!, Retrying "
                                    + retryLimit
                                    + " more times... "
                                    + e.toString());
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

    /*
     * =======================================================================
     * Twitter4J Wrapper Implementations:
     * =======================================================================
     */

    /*
     * ========================================================================
     * Useful single method calls for paging through results, avoiding cursors
     * ========================================================================
     */

    // TODO

    /*
     * Timelines
     */

    public final List<String> getUserTimelineToDate(final long userId,
            final Date oldest_created_at) {

        List<String> timeline = new ArrayList<>();

        Paging paging = new Paging();
        paging.count(200);

        int ctweets = 0;

        boolean datelimit = false;

        while ((ctweets < 3200) && !datelimit) {

            try {
                ResponseList<Status> page = getUserTimeline(userId, paging);

                Collection<Long> lowestID = new ArrayList<>();
                for (Status s : page) {
                    lowestID.add(s.getId());

                    if (s.getCreatedAt().compareTo(oldest_created_at) < 0) {
                        // System.out.println("Date Limit!");
                        datelimit = true;
                        continue;
                    }

                    timeline.add(TwitterObjectFactory.getRawJSON(s));
                }

                System.out.println(userId + " Fetched: " + lowestID.size()
                        + ":" + ctweets + " tweets");

                if (lowestID.size() < 1) {
                    break;
                }

                // Max ID is the lowest ID tweet you have already processed
                paging.setMaxId((Collections.min(lowestID) - 1));
                // pg.setSinceId(sinceId);

                ctweets = ctweets + page.size();

            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.err.println("FAILED TO RETRIEVE TIMELINE FOR USER: "
                        + userId);
                break;
            }

        }

        return timeline;
    }

    public final List<String> getUpdateUserTimeline(final long userId,
            final long sinceId) {

        List<String> timeline = new ArrayList<>();

        Paging paging = new Paging();
        paging.count(200);
        paging.setSinceId(sinceId);

        int ctweets = 0;

        while (ctweets < 3200) {

            try {
                ResponseList<Status> page = getUserTimeline(userId, paging);

                Collection<Long> lowestID = new ArrayList<>();
                for (Status s : page) {
                    lowestID.add(s.getId());

                    timeline.add(TwitterObjectFactory.getRawJSON(s));
                }

                System.out.println(userId + " Fetched: " + lowestID.size()
                        + ":" + ctweets + " tweets");

                if (lowestID.size() < 1) {
                    break;
                }

                // Max ID is the lowest ID tweet you have already processed
                paging.setMaxId((Collections.min(lowestID) - 1));
                paging.setSinceId(sinceId);

                ctweets = ctweets + page.size();

            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.err.println("FAILED TO RETRIEVE TIMELINE FOR USER: "
                        + userId);
                break;
            }

        }

        return timeline;
    }

    /*
     * Tweets
     */

    // TODO

    /*
     * Search
     */

    // TODO

    /*
     * FriendsFollowers
     */

    // TODO

    /*
     * Users
     */

    // TODO

    /*
     * FavoritesResources
     */

    // TODO

    /*
     * ListsResources
     */

    // TODO

    public final List<User> getAllListMembers(final long listId) {
        List<User> members = new ArrayList<>();
        long cursor = -1;
        try {
            while (cursor != 0) {
                PagableResponseList<User> pg = getUserListMembers(listId,
                        cursor);
                members.addAll(pg);
                cursor = pg.getNextCursor();
            }
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return members;
    }

    /*
     * TimelinesResources
     */

    @Override
    public final ResponseList<Status> getUserTimeline(final String screenName)
            throws TwitterException {
        return getUserTimeline(screenName, new Paging());
    }

    @Override
    public final ResponseList<Status> getUserTimeline(final String screenName,
            final Paging paging) throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().timelines()
                        .getUserTimeline(screenName, paging);
            }
        }).getResponse(EndPoint.STATUSES_USER_TIMELINE);
    }

    @Override
    public final ResponseList<Status> getUserTimeline(final long userId)
            throws TwitterException {
        return getUserTimeline(userId, new Paging());
    }

    @Override
    public final ResponseList<Status> getUserTimeline(final long userId,
            final Paging paging) throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().timelines()
                        .getUserTimeline(userId, paging);
            }
        }).getResponse(EndPoint.STATUSES_USER_TIMELINE);
    }

    /*
     * TweetsResources
     */

    @Override
    public final ResponseList<Status> getRetweets(final long statusId)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().tweets().getRetweets(statusId);
            }
        }).getResponse(EndPoint.STATUSES_RETWEETS);
    }

    @Override
    public final IDs getRetweeterIds(final long statusId, final long cursor)
            throws TwitterException {
        return getRetweeterIds(statusId, 100, cursor);
    }

    @Override
    public final IDs getRetweeterIds(final long statusId, final int count,
            final long cursor) throws TwitterException {
        return (new TwitterCommand<IDs>() {
            @Override
            public IDs fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().tweets()
                        .getRetweeterIds(statusId, count, cursor);
            }
        }).getResponse(EndPoint.STATUSES_RETWEETERS);
    }

    @Override
    public final Status showStatus(final long id) throws TwitterException {
        return (new TwitterCommand<Status>() {
            @Override
            public Status fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().tweets().showStatus(id);
            }
        }).getResponse(EndPoint.STATUSES_SHOW);
    }

    @Override
    public final ResponseList<Status> lookup(final long[] ids)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().tweets().lookup(ids);
            }
        }).getResponse(EndPoint.STATUSES_LOOKUP);
    }

    /*
     * SearchResource
     */

    @Override
    public final QueryResult search(final Query query) throws TwitterException {
        return (new TwitterCommand<QueryResult>() {
            @Override
            public QueryResult fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().search().search(query);
            }
        }).getResponse(EndPoint.SEARCH_TWEETS);
    }

    /*
     * FriendsFollowersResources
     */

    @Override
    public final IDs getFriendsIDs(final long userId, final long cursor)
            throws TwitterException {
        return getFriendsIDs(userId, cursor, 5000);
    }

    @Override
    public final IDs getFriendsIDs(final long userId, final long cursor,
            final int count) throws TwitterException {
        return (new TwitterCommand<IDs>() {
            @Override
            public IDs fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().friendsFollowers()
                        .getFollowersIDs(userId, cursor, count);
            }
        }).getResponse(EndPoint.FRIENDS_IDS);
    }

    @Override
    public final IDs getFriendsIDs(final String screenName, final long cursor)
            throws TwitterException {
        return getFriendsIDs(screenName, cursor, 5000);
    }

    @Override
    public final IDs getFriendsIDs(final String screenName, final long cursor,
            final int count) throws TwitterException {
        return (new TwitterCommand<IDs>() {
            @Override
            public IDs fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().friendsFollowers()
                        .getFriendsIDs(screenName, cursor, count);
            }
        }).getResponse(EndPoint.FRIENDS_IDS);
    }

    @Override
    public final IDs getFollowersIDs(final long userId, final long cursor)
            throws TwitterException {
        return getFollowersIDs(userId, cursor, 5000);
    }

    @Override
    public final IDs getFollowersIDs(final long userId, final long cursor,
            final int count) throws TwitterException {
        return (new TwitterCommand<IDs>() {
            @Override
            public IDs fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().friendsFollowers()
                        .getFollowersIDs(userId, cursor, count);
            }
        }).getResponse(EndPoint.FOLLOWERS_IDS);
    }

    @Override
    public final IDs getFollowersIDs(final String screenName, final long cursor)
            throws TwitterException {
        return getFollowersIDs(screenName, cursor, 5000);
    }

    @Override
    public final IDs getFollowersIDs(final String screenName,
            final long cursor, final int count) throws TwitterException {
        return (new TwitterCommand<IDs>() {
            @Override
            public IDs fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().friendsFollowers()
                        .getFollowersIDs(screenName, cursor, count);
            }
        }).getResponse(EndPoint.FOLLOWERS_IDS);
    }

    @Override
    public final Relationship showFriendship(final long sourceId,
            final long targetId) throws TwitterException {
        return (new TwitterCommand<Relationship>() {
            @Override
            public Relationship fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().friendsFollowers()
                        .showFriendship(sourceId, targetId);
            }
        }).getResponse(EndPoint.FRIENDSHIPS_SHOW);
    }

    @Override
    public final Relationship showFriendship(final String sourceScreenName,
            final String targetScreenName) throws TwitterException {
        return (new TwitterCommand<Relationship>() {
            @Override
            public Relationship fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().friendsFollowers()
                        .showFriendship(sourceScreenName, targetScreenName);
            }
        }).getResponse(EndPoint.FRIENDSHIPS_SHOW);
    }

    @Override
    public final PagableResponseList<User> getFriendsList(final long userId,
            final long cursor) throws TwitterException {
        return getFriendsList(userId, cursor, 200, false, true);
    }

    @Override
    public final PagableResponseList<User> getFriendsList(final long userId,
            final long cursor, final int count) throws TwitterException {
        return getFriendsList(userId, cursor, 200, false, true);
    }

    @Override
    public final PagableResponseList<User> getFriendsList(final long userId,
            final long cursor, final int count, final boolean skipStatus,
            final boolean includeUserEntities) throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot
                        .getTwitter()
                        .friendsFollowers()
                        .getFriendsList(userId, cursor, count, skipStatus,
                                includeUserEntities);
            }
        }).getResponse(EndPoint.FRIENDS_LIST);
    }

    @Override
    public final PagableResponseList<User> getFriendsList(
            final String screenName, final long cursor) throws TwitterException {
        return getFriendsList(screenName, cursor, 200, false, true);
    }

    @Override
    public final PagableResponseList<User> getFriendsList(
            final String screenName, final long cursor, final int count)
            throws TwitterException {
        return getFriendsList(screenName, cursor, count, false, true);
    }

    @Override
    public final PagableResponseList<User> getFriendsList(
            final String screenName, final long cursor, final int count,
            final boolean skipStatus, final boolean includeUserEntities)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot
                        .getTwitter()
                        .friendsFollowers()
                        .getFriendsList(screenName, cursor, count, skipStatus,
                                includeUserEntities);
            }
        }).getResponse(EndPoint.FRIENDS_LIST);
    }

    @Override
    public final PagableResponseList<User> getFollowersList(final long userId,
            final long cursor) throws TwitterException {
        return getFollowersList(userId, cursor, 200, false, true);
    }

    @Override
    public final PagableResponseList<User> getFollowersList(final long userId,
            final long cursor, final int count) throws TwitterException {
        return getFollowersList(userId, cursor, count, false, true);
    }

    @Override
    public final PagableResponseList<User> getFollowersList(final long userId,
            final long cursor, final int count, final boolean skipStatus,
            final boolean includeUserEntities) throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot
                        .getTwitter()
                        .friendsFollowers()
                        .getFollowersList(userId, cursor, count, skipStatus,
                                includeUserEntities);
            }
        }).getResponse(EndPoint.FOLLOWERS_LIST);
    }

    @Override
    public final PagableResponseList<User> getFollowersList(
            final String screenName, final long cursor) throws TwitterException {
        return getFollowersList(screenName, cursor, 200, false, true);
    }

    @Override
    public final PagableResponseList<User> getFollowersList(
            final String screenName, final long cursor, final int count)
            throws TwitterException {
        return getFollowersList(screenName, cursor, count, false, true);
    }

    @Override
    public final PagableResponseList<User> getFollowersList(
            final String screenName, final long cursor, final int count,
            final boolean skipStatus, final boolean includeUserEntities)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot
                        .getTwitter()
                        .friendsFollowers()
                        .getFollowersList(screenName, cursor, count,
                                skipStatus, includeUserEntities);
            }
        }).getResponse(EndPoint.FOLLOWERS_LIST);
    }

    /*
     * UsersResources
     */

    @Override
    public final ResponseList<User> lookupUsers(final long[] ids)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<User>>() {
            @Override
            public ResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().lookupUsers(ids);
            }
        }).getResponse(EndPoint.USERS_LOOKUP);
    }

    @Override
    public final ResponseList<User> lookupUsers(final String[] screenNames)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<User>>() {
            @Override
            public ResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().lookupUsers(screenNames);
            }
        }).getResponse(EndPoint.USERS_LOOKUP);
    }

    @Override
    public final User showUser(final long userId) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().showUser(userId);
            }
        }).getResponse(EndPoint.USERS_SHOW);
    }

    @Override
    public final User showUser(final String screenName) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().showUser(screenName);
            }
        }).getResponse(EndPoint.USERS_SHOW);
    }

    @Override
    public final ResponseList<User> searchUsers(final String query,
            final int page) throws TwitterException {
        return (new TwitterCommand<ResponseList<User>>() {
            @Override
            public ResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().searchUsers(query, page);
            }
        }).getResponse(EndPoint.USERS_SEARCH);
    }

    @Override
    public final ResponseList<User> getContributees(final long userId)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<User>>() {
            @Override
            public ResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().getContributees(userId);
            }
        }).getResponse(EndPoint.USERS_CONTRIBUTEES);
    }

    @Override
    public final ResponseList<User> getContributees(final String screenName)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<User>>() {
            @Override
            public ResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().getContributees(screenName);
            }
        }).getResponse(EndPoint.USERS_CONTRIBUTEES);
    }

    @Override
    public final ResponseList<User> getContributors(final String screenName)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<User>>() {
            @Override
            public ResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {

                return bot.getTwitter().users().getContributors(screenName);
            }
        }).getResponse(EndPoint.USERS_CONTRIBUTORS);
    }

    @Override
    public final ResponseList<User> getContributors(final long userId)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<User>>() {
            @Override
            public ResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().users().getContributors(userId);
            }
        }).getResponse(EndPoint.USERS_CONTRIBUTORS);
    }

    /*
     * FavoritesResources
     */

    @Override
    public final ResponseList<Status> getFavorites(final long userId)
            throws TwitterException {
        return getFavorites(userId, new Paging());
    }

    @Override
    public final ResponseList<Status> getFavorites(final long userId,
            final Paging paging) throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().favorites()
                        .getFavorites(userId, paging);
            }
        }).getResponse(EndPoint.FAVORITES_LIST);
    }

    @Override
    public final ResponseList<Status> getFavorites(final String screenName)
            throws TwitterException {
        return getFavorites(screenName, new Paging());
    }

    @Override
    public final ResponseList<Status> getFavorites(final String screenName,
            final Paging paging) throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().favorites()
                        .getFavorites(screenName, paging);
            }
        }).getResponse(EndPoint.FAVORITES_LIST);
    }

    /*
     * ListsResources
     */

    @Override
    public final ResponseList<UserList> getUserLists(
            final String listOwnerScreenName) throws TwitterException {
        return (new TwitterCommand<ResponseList<UserList>>() {
            @Override
            public ResponseList<UserList> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserLists(listOwnerScreenName);
            }
        }).getResponse(EndPoint.LISTS_LIST);
    }

    @Override
    public final ResponseList<UserList> getUserLists(final long listOwnerUserId)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<UserList>>() {
            @Override
            public ResponseList<UserList> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list().getUserLists(listOwnerUserId);
            }
        }).getResponse(EndPoint.LISTS_LIST);
    }

    @Override
    public final ResponseList<Status> getUserListStatuses(final long listId,
            final Paging paging) throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListStatuses(listId, paging);
            }
        }).getResponse(EndPoint.LISTS_STATUSES);
    }

    @Override
    public final ResponseList<Status> getUserListStatuses(final long ownerId,
            final String slug, final Paging paging) throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListStatuses(ownerId, slug, paging);
            }
        }).getResponse(EndPoint.LISTS_STATUSES);
    }

    @Override
    public final ResponseList<Status> getUserListStatuses(
            final String ownerScreenName, final String slug, final Paging paging)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListStatuses(ownerScreenName, slug, paging);
            }
        }).getResponse(EndPoint.LISTS_STATUSES);
    }

    @Override
    public final PagableResponseList<UserList> getUserListMemberships(
            final long listMemberId, final long cursor) throws TwitterException {
        return (new TwitterCommand<PagableResponseList<UserList>>() {
            @Override
            public PagableResponseList<UserList> fetchResponse(
                    final TwitterBot bot) throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListMemberships(listMemberId, cursor);
            }
        }).getResponse(EndPoint.LISTS_MEMBERSHIPS);
    }

    @Override
    public final PagableResponseList<UserList> getUserListMemberships(
            final String listMemberScreenName, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<UserList>>() {
            @Override
            public PagableResponseList<UserList> fetchResponse(
                    final TwitterBot bot) throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListMemberships(listMemberScreenName, cursor);
            }
        }).getResponse(EndPoint.LISTS_MEMBERSHIPS);
    }

    @Override
    public final PagableResponseList<User> getUserListSubscribers(
            final long listId, final long cursor) throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListSubscribers(listId, cursor);
            }
        }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
    }

    @Override
    public final PagableResponseList<User> getUserListSubscribers(
            final long ownerId, final String slug, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListSubscribers(ownerId, slug, cursor);
            }
        }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
    }

    @Override
    public final PagableResponseList<User> getUserListSubscribers(
            final String ownerScreenName, final String slug, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListSubscribers(ownerScreenName, slug, cursor);
            }
        }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
    }

    @Override
    public final User showUserListSubscription(final long listId,
            final long userId) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .showUserListSubscription(listId, userId);
            }
        }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
    }

    @Override
    public final User showUserListSubscription(final long ownerId,
            final String slug, final long userId) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .showUserListSubscription(ownerId, slug, userId);
            }
        }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
    }

    @Override
    public final User showUserListSubscription(final String ownerScreenName,
            final String slug, final long userId) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot
                        .getTwitter()
                        .list()
                        .showUserListSubscription(ownerScreenName, slug, userId);
            }
        }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
    }

    @Override
    public final User showUserListMembership(final long listId,
            final long userId) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .showUserListMembership(listId, userId);
            }
        }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
    }

    @Override
    public final User showUserListMembership(final long ownerId,
            final String slug, final long userId) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .showUserListMembership(ownerId, slug, userId);
            }
        }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
    }

    @Override
    public final User showUserListMembership(final String ownerScreenName,
            final String slug, final long userId) throws TwitterException {
        return (new TwitterCommand<User>() {
            @Override
            public User fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .showUserListMembership(ownerScreenName, slug, userId);
            }
        }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
    }

    @Override
    public final PagableResponseList<User> getUserListMembers(
            final long listId, final long cursor) throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListMembers(listId, cursor);
            }
        }).getResponse(EndPoint.LISTS_MEMBERS);
    }

    @Override
    public final PagableResponseList<User> getUserListMembers(
            final long ownerId, final String slug, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListMembers(ownerId, slug, cursor);
            }
        }).getResponse(EndPoint.LISTS_MEMBERS);
    }

    @Override
    public final PagableResponseList<User> getUserListMembers(
            final String ownerScreenName, final String slug, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListMembers(ownerScreenName, slug, cursor);
            }
        }).getResponse(EndPoint.LISTS_MEMBERS);
    }

    @Override
    public final UserList showUserList(final long listId)
            throws TwitterException {
        return (new TwitterCommand<UserList>() {
            @Override
            public UserList fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list().showUserList(listId);
            }
        }).getResponse(EndPoint.LISTS_SHOW);
    }

    @Override
    public final UserList showUserList(final long ownerId, final String slug)
            throws TwitterException {
        return (new TwitterCommand<UserList>() {
            @Override
            public UserList fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list().showUserList(ownerId, slug);
            }
        }).getResponse(EndPoint.LISTS_SHOW);
    }

    @Override
    public final UserList showUserList(final String ownerScreenName,
            final String slug) throws TwitterException {
        return (new TwitterCommand<UserList>() {
            @Override
            public UserList fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().list()
                        .showUserList(ownerScreenName, slug);
            }
        }).getResponse(EndPoint.LISTS_SHOW);
    }

    @Override
    public final PagableResponseList<UserList> getUserListSubscriptions(
            final String listOwnerScreenName, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<UserList>>() {
            @Override
            public PagableResponseList<UserList> fetchResponse(
                    final TwitterBot bot) throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListSubscriptions(listOwnerScreenName, cursor);
            }
        }).getResponse(EndPoint.LISTS_SUBSCRIPTIONS);
    }

    @Override
    public final PagableResponseList<UserList> getUserListsOwnerships(
            final String listOwnerScreenName, final int count, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<UserList>>() {
            @Override
            public PagableResponseList<UserList> fetchResponse(
                    final TwitterBot bot) throws TwitterException {
                return bot
                        .getTwitter()
                        .list()
                        .getUserListsOwnerships(listOwnerScreenName, count,
                                cursor);
            }
        }).getResponse(EndPoint.LISTS_OWNERSHIPS);
    }

    @Override
    public final PagableResponseList<UserList> getUserListsOwnerships(
            final long listOwnerId, final int count, final long cursor)
            throws TwitterException {
        return (new TwitterCommand<PagableResponseList<UserList>>() {
            @Override
            public PagableResponseList<UserList> fetchResponse(
                    final TwitterBot bot) throws TwitterException {
                return bot.getTwitter().list()
                        .getUserListsOwnerships(listOwnerId, count, cursor);
            }
        }).getResponse(EndPoint.LISTS_OWNERSHIPS);
    }

    /*
     * PlacesGeoResources
     */

    @Override
    public final Place getGeoDetails(final String placeId)
            throws TwitterException {
        return (new TwitterCommand<Place>() {
            @Override
            public Place fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().placesGeo().getGeoDetails(placeId);
            }
        }).getResponse(EndPoint.GEO_ID);
    }

    @Override
    public final ResponseList<Place> reverseGeoCode(final GeoQuery query)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<Place>>() {
            @Override
            public ResponseList<Place> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().placesGeo().reverseGeoCode(query);
            }
        }).getResponse(EndPoint.GEO_REVERSE_GEOCODE);
    }

    @Override
    public final ResponseList<Place> searchPlaces(final GeoQuery query)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<Place>>() {
            @Override
            public ResponseList<Place> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().placesGeo().searchPlaces(query);
            }
        }).getResponse(EndPoint.GEO_SEARCH);
    }

    @Override
    public final ResponseList<Place> getSimilarPlaces(
            final GeoLocation location, final String name,
            final String containedWithin, final String streetAddress)
            throws TwitterException {
        return (new TwitterCommand<ResponseList<Place>>() {
            @Override
            public ResponseList<Place> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot
                        .getTwitter()
                        .placesGeo()
                        .getSimilarPlaces(location, name, containedWithin,
                                streetAddress);
            }
        }).getResponse(EndPoint.GEO_SIMILAR_PLACES);
    }

    /*
     * TrendsResources
     */

    @Override
    public final Trends getPlaceTrends(final int woeid) throws TwitterException {
        return (new TwitterCommand<Trends>() {
            @Override
            public Trends fetchResponse(final TwitterBot bot)
                    throws TwitterException {

                return bot.getTwitter().trends().getPlaceTrends(woeid);
            }
        }).getResponse(EndPoint.TRENDS_PLACE);
    }

    @Override
    public final ResponseList<Location> getAvailableTrends()
            throws TwitterException {
        return (new TwitterCommand<ResponseList<Location>>() {
            @Override
            public ResponseList<Location> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().trends().getAvailableTrends();
            }
        }).getResponse(EndPoint.TRENDS_AVAILABLE);
    }

    @Override
    public final ResponseList<Location> getClosestTrends(
            final GeoLocation location) throws TwitterException {
        return (new TwitterCommand<ResponseList<Location>>() {
            @Override
            public ResponseList<Location> fetchResponse(final TwitterBot bot)
                    throws TwitterException {
                return bot.getTwitter().trends().getClosestTrends(location);
            }
        }).getResponse(EndPoint.TRENDS_CLOSEST);
    }

    /*
     * All other unimplemented methods will throw UnsupportedMethodException
     */

}
