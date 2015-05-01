package org.insight.twitter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insight.twitter.multi.EndPoint;
import org.insight.twitter.multi.InternalRateLimitStatus;
import org.insight.twitter.multi.TwitterBot;
import org.insight.twitter.util.TwitterCursor;
import org.insight.twitter.util.TwitterObjects;
import org.insight.twitter.util.TwitterPage;
import org.insight.twitter.util.TwitterQueryPage;
import org.insight.twitter.wrapper.TwitterResources;

import twitter4j.CursorSupport;
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
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;

/*
 * Only implements REST API calls that can be spread over multiple accounts.
 * 
 * Should be straight forward to add unimplemented methods, if you really need them.
 * 
 * All unimplemented methods will throw UnsupportedMethodException
 */
public class MultiTwitter extends TwitterResources {

  private final Set<String> configuredBots;
  private final boolean useBlockingQueue;

  public MultiTwitter() {
    this(true, "/twitter4j.properties");
  }

  public MultiTwitter(final boolean blocking, final String configFile) {
    this.useBlockingQueue = blocking;
    this.configuredBots = getConfiguredBots(configFile);
  }

  public MultiTwitter(final boolean blocking, final Properties t4jProperties) {
    this.useBlockingQueue = blocking;
    this.configuredBots = getConfiguredBots(t4jProperties);
  }

  /*
   * -------------------------- Utility Methods: --------------------------
   */

  /*
   * Read config file, extract all the access tokens.
   */
  private Set<String> getConfiguredBots(final String configFile) {
    Set<String> botIDs = new HashSet<>();
    try (InputStream in = new FileInputStream(configFile)) {
      Properties t4jProperties = new Properties();
      System.out.println("Reading Bot Configs from: " + "/" + configFile);
      t4jProperties.load(in);
      botIDs.addAll(getConfiguredBots(t4jProperties));
    } catch (IOException e) {
      System.err.println("IO ERROR Reading Properties!");
      e.printStackTrace();
    }
    return botIDs;
  }

  private Set<String> getConfiguredBots(final Properties t4jProperties) {
    Set<String> botIDs = new HashSet<>();
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
    // Either Block with take() until a bot is available, or throw rate limit exception:
    // Lazy load bots to endpoints:
    endpoint.getBotQueue().reloadConfiguredBots(configuredBots);
    TwitterBot bot = useBlockingQueue ? endpoint.getBotQueue().take() : endpoint.getBotQueue().poll();
    if (bot == null) {
      throw new TwitterException(endpoint + " Queue is EMPTY! Rate Limit for all Bots Reached!");
    }
    return bot;
  }

  /*
   * Returning bots to Queue:
   */
  private void releaseBot(final TwitterBot bot) {
    if (bot != null) {
      bot.getEndPoint().getBotQueue().offer(bot);
    }
  }

  /*
   * Wrap Responses from Twitter, Retry on failures, throw appropriate
   * Exceptions. This provides retry functions to any method.
   *
   * <T> String (screen_name) or Long (user_id)
   * <K> Twitter4J Object (As.POJO) or String (As.JSON)
   */
  public abstract class TwitterCommand<K> {
    public K getResponse(final EndPoint endpoint) throws TwitterException {
      K result;
      int retryLimit = configuredBots.size();
      while (true) {
        TwitterBot bot = null;
        try {
          bot = takeBot(endpoint);
          result = fetchResponse(bot.getTwitter());
          break;
        } catch (TwitterException e) {
          retryLimit--;
          if (e.exceededRateLimitation() || e.isCausedByNetworkIssue()) {
            if (bot != null) {
              System.err.println("ERROR: bot: " + bot.toString() + " Limit:" + bot.getCachedRateLimitStatus().toString());
            }
            System.err.println("Temporary Rate Limit / Connection Error!, Retrying " + retryLimit + " more times... " + e.toString());
            if (retryLimit <= 0) {
              System.err.println("Retried " + configuredBots.size() + " times, giving up.");
              throw e;
            }
          } else if (e.resourceNotFound() || (e.getStatusCode() == 401)) {
            /*
             * Skip retrying Private / Deleted / Banned accounts
             */
            System.err.println("Resource Not Found / Unauthorized, Giving Up.");
            throw e;
          } else {
            System.err.println("Request Refused, Giving Up.");
            throw e;
          }
        } finally {
          releaseBot(bot);
        }
      }
      return result;
    }

    /*
     * Make an API Call with a given bot:
     */
    public abstract K fetchResponse(final Twitter twitter) throws TwitterException;
  }

  /*
   * ========================================================================
   * Bulk Request Methods: Paging through results, avoiding cursors etc.
   * ========================================================================
   */

  /*
   * Timelines:
   */
  @Override
  public <T, K> List<K> getBulkUserTimeline(As type, final T ident, final long initSinceId, final long initMaxId, final int maxElements)
      throws TwitterException {
    return (new TwitterPage<K>() {
      @Override
      public List<Status> pageResponse(Paging page) throws TwitterException {
        return fetchUserTimeline(ident, page);
      }
    }).getElements(type, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T, K> List<K> getBulkFavorites(As type, final T ident, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException {
    return (new TwitterPage<K>() {
      @Override
      public List<Status> pageResponse(Paging page) throws TwitterException {
        return fetchFavorites(ident, page);
      }
    }).getElements(type, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T, K> List<K> getBulkUserListStatuses(As type, final T ident, final String slug, final long initSinceId, final long initMaxId, final int maxElements)
      throws TwitterException {
    return (new TwitterPage<K>() {
      @Override
      public List<Status> pageResponse(Paging page) throws TwitterException {
        return fetchUserListStatuses(ident, slug, page);
      }
    }).getElements(type, initSinceId, initMaxId, maxElements);
  }

  /*
   * Tweets:
   */

  @Override
  public List<Long> getBulkRetweeterIds(final long statusId, final int maxElements) throws TwitterException {
    return (new TwitterCursor<Long>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return getRetweeterIds(statusId, 200, cursor);
      }

      @Override
      public List<Long> processElements(As type, CursorSupport page) throws TwitterException {
        List<Long> retweeterIDs = new ArrayList<>();
        for (Long l : ((IDs) page).getIDs()) {
          retweeterIDs.add(l);
        }
        return retweeterIDs;
      }
    }).getElements(As.POJO, maxElements);
  }

  // TODO: lookup() Wrapper that uses both lookup() and show()

  /*
   * FriendsFollowers
   */
  @Override
  public <T> List<Long> getBulkFriendsIDs(final T ident, final int maxElements) throws TwitterException {
    return (new TwitterCursor<Long>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchFriendsIDs(ident, cursor, 5000);
      }

      @Override
      public List<Long> processElements(As type, CursorSupport page) throws TwitterException {
        List<Long> friends = new ArrayList<>();
        for (Long l : ((IDs) page).getIDs()) {
          friends.add(l);
        }
        return friends;
      }
    }).getElements(As.POJO, maxElements);
  }

  @Override
  public <T> List<Long> getBulkFollowersIDs(final T ident, final int maxElements) throws TwitterException {
    return (new TwitterCursor<Long>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchFollowersIDs(ident, cursor, 5000);
      }

      @Override
      public List<Long> processElements(As type, CursorSupport page) throws TwitterException {
        List<Long> followers = new ArrayList<>();
        for (Long l : ((IDs) page).getIDs()) {
          followers.add(l);
        }
        return followers;
      }
    }).getElements(As.POJO, maxElements);
  }

  @Override
  public <T, K> List<K> getBulkFriendsList(As type, final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return (new TwitterCursor<K>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchFriendsList(ident, cursor, 200, skipStatus, includeUserEntities);
      }
    }).getElements(type, maxElements);
  }

  @Override
  public <T, K> List<K> getBulkFollowersList(As type, final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return (new TwitterCursor<K>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchFollowersList(ident, cursor, 200, skipStatus, includeUserEntities);
      }
    }).getElements(type, maxElements);
  }

  /*
   * Search
   */

  @Override
  public <K> List<K> getBulkSearchResults(final As type, final Query query, int maxElements) throws TwitterException {
    return (new TwitterQueryPage<K>() {
      @Override
      public QueryResult pageResponse(Query query) throws TwitterException {
        return search(query);
      }
    }).getElements(type, query, maxElements);
  }


  /*
   * Users
   */

  // Awkwardly doesn't implement Paging or Cursors: Max Per page: 20, Max Results 1000.
  @Override
  public <K> List<K> getBulkSearchUsers(final As type, final String query, int maxElements) throws TwitterException {
    return (new TwitterPage<K>() {
      @SuppressWarnings("unchecked")
      @Override
      public List<K> manualPageResponse(int page) throws TwitterException {
        if (type.equals(As.JSON)) {
          return (List<K>) searchUsersJSON(query, page);
        } else {
          return (List<K>) searchUsers(query, page);
        }
      }

      @Override
      public List<Status> pageResponse(Paging page) throws TwitterException {
        return null;
      }
    }).getManualPageElements(query, maxElements);
  }

  /*
   * ListsResources
   */

  @Override
  public <T, K> List<K> getBulkUserListMemberships(As type, final T ident, int maxElements) throws TwitterException {
    return (new TwitterCursor<K>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchUserListMemberships(ident, 1000, cursor);
      }
    }).getElements(type, maxElements);
  }

  @Override
  public <T, K> List<K> getBulkUserListSubscribers(As type, final T ident, final String slug, int maxElements) throws TwitterException {
    return (new TwitterCursor<K>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchUserListSubscribers(ident, slug, 5000, cursor, false);
      }
    }).getElements(type, maxElements);
  }

  @Override
  public <K> List<K> getBulkUserListMembers(As type, final long listId, final int maxElements) throws TwitterException {
    return (new TwitterCursor<K>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return getUserListMembers(listId, 5000, cursor);
      }
    }).getElements(type, maxElements);
  }

  @Override
  public <T, K> List<K> getBulkUserListSubscriptions(As type, final T ident, int maxElements) throws TwitterException {
    return (new TwitterCursor<K>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchUserListSubscriptions(ident, 1000, cursor);
      }
    }).getElements(type, maxElements);
  }

  @Override
  public <T, K> List<K> getBulkUserListsOwnerships(As type, final T ident, int maxElements) throws TwitterException {
    return (new TwitterCursor<K>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return fetchUserListsOwnerships(ident, 1000, cursor);
      }
    }).getElements(type, maxElements);
  }

  /*
   * =======================================================================
   * Twitter4J Wrapper Implementations:
   * =======================================================================
   */

  /*
   * TimelinesResources
   */

  @Override
  public <T> ResponseList<Status> fetchUserTimeline(final T ident, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.timelines().getUserTimeline((String) ident, paging);
        } else if (ident instanceof Long) {
          return twitter.timelines().getUserTimeline((Long) ident, paging);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.STATUSES_USER_TIMELINE);
  }

  /*
   * TweetsResources
   */

  @Override
  public ResponseList<Status> getRetweets(final long statusId) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.tweets().getRetweets(statusId);
      }
    }).getResponse(EndPoint.STATUSES_RETWEETS);
  }

  @Override
  public IDs getRetweeterIds(final long statusId, final int count, final long cursor) throws TwitterException {
    return (new TwitterCommand<IDs>() {
      @Override
      public IDs fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.tweets().getRetweeterIds(statusId, count, cursor);
      }
    }).getResponse(EndPoint.STATUSES_RETWEETERS);
  }

  @Override
  public Status showStatus(final long id) throws TwitterException {
    return (new TwitterCommand<Status>() {
      @Override
      public Status fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.tweets().showStatus(id);
      }
    }).getResponse(EndPoint.STATUSES_SHOW);
  }

  @Override
  public ResponseList<Status> lookup(final long[] ids) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.tweets().lookup(ids);
      }
    }).getResponse(EndPoint.STATUSES_LOOKUP);
  }

  /*
   * SearchResource
   */

  @Override
  public QueryResult search(final Query query) throws TwitterException {
    return (new TwitterCommand<QueryResult>() {
      @Override
      public QueryResult fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.search().search(query);
      }
    }).getResponse(EndPoint.SEARCH_TWEETS);
  }

  @Override
  public ResponseList<User> searchUsers(final String query, final int page) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.users().searchUsers(query, page);
      }
    }).getResponse(EndPoint.USERS_SEARCH);
  }

  /*
   * FriendsFollowersResources
   */

  @Override
  public <T> IDs fetchFriendsIDs(final T ident, final long cursor, final int count) throws TwitterException {
    return (new TwitterCommand<IDs>() {
      @Override
      public IDs fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.friendsFollowers().getFriendsIDs((String) ident, cursor, count);
        } else if (ident instanceof Long) {
          return twitter.friendsFollowers().getFriendsIDs((Long) ident, cursor, count);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FRIENDS_IDS);
  }

  @Override
  public <T> IDs fetchFollowersIDs(final T ident, final long cursor, final int count) throws TwitterException {
    return (new TwitterCommand<IDs>() {
      @Override
      public IDs fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.friendsFollowers().getFollowersIDs((String) ident, cursor, count);
        } else if (ident instanceof Long) {
          return twitter.friendsFollowers().getFollowersIDs((Long) ident, cursor, count);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FOLLOWERS_IDS);
  }

  @Override
  public <T> Relationship fetchFriendship(final T sourceIdent, final T targetIdent) throws TwitterException {
    return (new TwitterCommand<Relationship>() {
      @Override
      public Relationship fetchResponse(final Twitter twitter) throws TwitterException {
        if ((sourceIdent instanceof String) && (targetIdent instanceof String)) {
          return twitter.friendsFollowers().showFriendship((String) sourceIdent, (String) targetIdent);
        } else if ((sourceIdent instanceof Long) && (targetIdent instanceof Long)) {
          return twitter.friendsFollowers().showFriendship((Long) sourceIdent, (Long) targetIdent);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FRIENDSHIPS_SHOW);
  }

  @Override
  public <T> PagableResponseList<User> fetchFriendsList(final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.friendsFollowers().getFriendsList((String) ident, cursor, count, skipStatus, includeUserEntities);
        } else if (ident instanceof Long) {
          return twitter.friendsFollowers().getFriendsList((Long) ident, cursor, count, skipStatus, includeUserEntities);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FRIENDS_LIST);
  }

  @Override
  public <T> PagableResponseList<User> fetchFollowersList(final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.friendsFollowers().getFollowersList((String) ident, cursor, count, skipStatus, includeUserEntities);
        } else if (ident instanceof Long) {
          return twitter.friendsFollowers().getFollowersList((Long) ident, cursor, count, skipStatus, includeUserEntities);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FOLLOWERS_LIST);
  }

  /*
   * UsersResources
   */

  @Override
  public <T> ResponseList<User> fetchLookupUsers(final T idents) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        if (idents instanceof String[]) {
          return twitter.users().lookupUsers((String[]) idents);
        } else if (idents instanceof long[]) {
          return twitter.users().lookupUsers((long[]) idents);
        } else if (idents instanceof Long[]) {
          return twitter.users().lookupUsers(TwitterObjects.toPrimitive((Long[]) idents));
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.USERS_LOOKUP);
  }

  @Override
  public <T> User fetchUser(final T ident) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.users().showUser((String) ident);
        } else if (ident instanceof Long) {
          return twitter.users().showUser((Long) ident);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.USERS_SHOW);
  }

  @Override
  public <T> ResponseList<User> fetchContributees(final T ident) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.users().getContributees((String) ident);
        } else if (ident instanceof Long) {
          return twitter.users().getContributees((Long) ident);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.USERS_CONTRIBUTEES);
  }

  @Override
  public <T> ResponseList<User> fetchContributors(final T ident) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.users().getContributors((String) ident);
        } else if (ident instanceof Long) {
          return twitter.users().getContributors((Long) ident);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.USERS_CONTRIBUTORS);
  }

  /*
   * FavoritesResources
   */

  @Override
  public <T> ResponseList<Status> fetchFavorites(final T ident, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.favorites().getFavorites((String) ident, paging);
        } else if (ident instanceof Long) {
          return twitter.favorites().getFavorites((Long) ident, paging);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FAVORITES_LIST);
  }

  /*
   * ListsResources
   */

  @Override
  public <T> ResponseList<UserList> fetchUserLists(final T ident, boolean reverse) throws TwitterException {
    return (new TwitterCommand<ResponseList<UserList>>() {
      @Override
      public ResponseList<UserList> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().getUserLists((String) ident);
        } else if (ident instanceof Long) {
          return twitter.list().getUserLists((Long) ident);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_LIST);
  }

  @Override
  public ResponseList<Status> getUserListStatuses(final long listId, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.list().getUserListStatuses(listId, paging);
      }
    }).getResponse(EndPoint.LISTS_STATUSES);
  }

  @Override
  public <T> ResponseList<Status> fetchUserListStatuses(final T ident, final String slug, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().getUserListStatuses((String) ident, slug, paging);
        } else if (ident instanceof Long) {
          return twitter.list().getUserListStatuses((Long) ident, slug, paging);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_STATUSES);
  }

  @Override
  public <T> PagableResponseList<UserList> fetchUserListMemberships(final T ident, final int count, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().getUserListMemberships((String) ident, count, cursor);
        } else if (ident instanceof Long) {
          return twitter.list().getUserListMemberships((Long) ident, count, cursor);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_MEMBERSHIPS);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final long listId, final int count, final long cursor, final boolean skipStatus)
      throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.list().getUserListSubscribers(listId, count, cursor, skipStatus);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
  }

  @Override
  public <T> PagableResponseList<User> fetchUserListSubscribers(final T ident, final String slug, final int count, final long cursor, final boolean skipStatus)
      throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().getUserListSubscribers((String) ident, slug, count, cursor, skipStatus);
        } else if (ident instanceof Long) {
          return twitter.list().getUserListSubscribers((Long) ident, slug, count, cursor, skipStatus);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
  }

  @Override
  public User showUserListSubscription(final long listId, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.list().showUserListSubscription(listId, userId);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
  }

  @Override
  public <T> User fetchUserListSubscription(final T ident, final String slug, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().showUserListSubscription((String) ident, slug, userId);
        } else if (ident instanceof Long) {
          return twitter.list().showUserListSubscription((Long) ident, slug, userId);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
  }

  @Override
  public User showUserListMembership(final long listId, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.list().showUserListMembership(listId, userId);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
  }

  @Override
  public <T> User fetchUserListMembership(final T ident, final String slug, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().showUserListMembership((String) ident, slug, userId);
        } else if (ident instanceof Long) {
          return twitter.list().showUserListMembership((Long) ident, slug, userId);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final long listId, final int count, final long cursor, final boolean skipStatus) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.list().getUserListMembers(listId, count, cursor, skipStatus);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS);
  }

  @Override
  public <T> PagableResponseList<User> fetchUserListMembers(final T ident, final String slug, final int count, final long cursor, final boolean skipStatus)
      throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().getUserListMembers((String) ident, slug, count, cursor, skipStatus);
        } else if (ident instanceof Long) {
          return twitter.list().getUserListMembers((Long) ident, slug, count, cursor, skipStatus);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_MEMBERS);
  }

  @Override
  public UserList showUserList(final long listId) throws TwitterException {
    return (new TwitterCommand<UserList>() {
      @Override
      public UserList fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.list().showUserList(listId);
      }
    }).getResponse(EndPoint.LISTS_SHOW);
  }

  @Override
  public <T> UserList fetchUserList(final T ident, final String slug) throws TwitterException {
    return (new TwitterCommand<UserList>() {
      @Override
      public UserList fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().showUserList((String) ident, slug);
        } else if (ident instanceof Long) {
          return twitter.list().showUserList((Long) ident, slug);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_SHOW);
  }

  @Override
  public <T> PagableResponseList<UserList> fetchUserListSubscriptions(final T ident, final int count, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.getUserListSubscriptions((String) ident, count, cursor);
        } else if (ident instanceof Long) {
          return twitter.getUserListSubscriptions((Long) ident, count, cursor);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIPTIONS);
  }

  @Override
  public <T> PagableResponseList<UserList> fetchUserListsOwnerships(final T ident, final int count, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final Twitter twitter) throws TwitterException {
        if (ident instanceof String) {
          return twitter.list().getUserListsOwnerships((String) ident, count, cursor);
        } else if (ident instanceof Long) {
          return twitter.list().getUserListsOwnerships((Long) ident, count, cursor);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.LISTS_OWNERSHIPS);
  }

  /*
   * PlacesGeoResources
   */

  @Override
  public Place getGeoDetails(final String placeId) throws TwitterException {
    return (new TwitterCommand<Place>() {
      @Override
      public Place fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.placesGeo().getGeoDetails(placeId);
      }
    }).getResponse(EndPoint.GEO_ID);
  }

  @Override
  public ResponseList<Place> reverseGeoCode(final GeoQuery query) throws TwitterException {
    return (new TwitterCommand<ResponseList<Place>>() {
      @Override
      public ResponseList<Place> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.placesGeo().reverseGeoCode(query);
      }
    }).getResponse(EndPoint.GEO_REVERSE_GEOCODE);
  }

  @Override
  public ResponseList<Place> searchPlaces(final GeoQuery query) throws TwitterException {
    return (new TwitterCommand<ResponseList<Place>>() {
      @Override
      public ResponseList<Place> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.placesGeo().searchPlaces(query);
      }
    }).getResponse(EndPoint.GEO_SEARCH);
  }

  @Override
  public ResponseList<Place> getSimilarPlaces(final GeoLocation location, final String name, final String containedWithin, final String streetAddress)
      throws TwitterException {
    return (new TwitterCommand<ResponseList<Place>>() {
      @Override
      public ResponseList<Place> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.placesGeo().getSimilarPlaces(location, name, containedWithin, streetAddress);
      }
    }).getResponse(EndPoint.GEO_SIMILAR_PLACES);
  }

  /*
   * TrendsResources
   */

  @Override
  public Trends getPlaceTrends(final int woeid) throws TwitterException {
    return (new TwitterCommand<Trends>() {
      @Override
      public Trends fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.trends().getPlaceTrends(woeid);
      }
    }).getResponse(EndPoint.TRENDS_PLACE);
  }

  @Override
  public ResponseList<Location> getAvailableTrends() throws TwitterException {
    return (new TwitterCommand<ResponseList<Location>>() {
      @Override
      public ResponseList<Location> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.trends().getAvailableTrends();
      }
    }).getResponse(EndPoint.TRENDS_AVAILABLE);
  }

  @Override
  public ResponseList<Location> getClosestTrends(final GeoLocation location) throws TwitterException {
    return (new TwitterCommand<ResponseList<Location>>() {
      @Override
      public ResponseList<Location> fetchResponse(final Twitter twitter) throws TwitterException {
        return twitter.trends().getClosestTrends(location);
      }
    }).getResponse(EndPoint.TRENDS_CLOSEST);
  }

  /*
   * HelpResources
   */

  @Override
  public Map<String, RateLimitStatus> getRateLimitStatus(final String... resources) throws TwitterException {
    EndPoint[] endpoints = new EndPoint[resources.length];
    for (int i = 0; i < resources.length; i++) {
      endpoints[i] = EndPoint.fromString(resources[i]);
    }
    return getRateLimitStatus(endpoints);
  }

  @Override
  public Map<String, RateLimitStatus> getRateLimitStatus() throws TwitterException {
    return getRateLimitStatus(EndPoint.values());
  }

  /*
   * Get combined Rate Limit for an endpoint (or several)
   */
  public Map<String, RateLimitStatus> getRateLimitStatus(final EndPoint[] endpoints) {
    Map<String, RateLimitStatus> rateLimits = new HashMap<>();
    for (EndPoint target : endpoints) {
      rateLimits.putAll(getRateLimitStatus(target));
    }
    return rateLimits;
  }

  /*
   * Get Combined Rate Limit from all available bots
   */
  public Map<String, RateLimitStatus> getRateLimitStatus(final EndPoint endpoint) {
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
   * All other unimplemented methods will throw UnsupportedMethodException
   */
}
