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
import org.insight.twitter.internal.TwitterBot;
import org.insight.twitter.internal.TwitterJSONResources;
import org.insight.twitter.internal.Util;

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
public class MultiTwitter extends TwitterJSONResources {

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
   * -------------------------- Utility Methods: --------------------------
   */

  /*
   * Read config file, extract all the access tokens.
   */
  private Set<String> getConfiguredBots(final String configFile) {
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
    if (bot != null) {
      bot.getEndPoint().getBotQueue().offer(bot);
    }
  }

  /*
   * Wrap Responses from Twitter, Retry on failures, throw appropriate
   * Exceptions. This provides retry functions to any method.
   */
  public abstract class TwitterCommand<T> {
    public T getResponse(final EndPoint endpoint) throws TwitterException {
      T result;
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
          if (e.resourceNotFound() || (e.getStatusCode() == 401)) {
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
     * Make an API Call with a given bot:
     */
    public abstract T fetchResponse(TwitterBot bot) throws TwitterException;
  }

  /*
   * Page Through Results with Cursors:
   * TODO: JSON Response Per page??? 
   */
  public abstract class TwitterCursor<T> {
    public List<T> getElements(final int maxElements) throws TwitterException {
      final int limit = (maxElements <= 0) ? Integer.MAX_VALUE : maxElements;
      List<T> elements = new ArrayList<>();
      long cursor = -1;
      try {
        while (cursor != 0) {
          CursorSupport pg = pageResponse(cursor);
          elements.addAll(processElements(pg));
          // Limit check:
          if (elements.size() >= limit) {
            break;
          }
          cursor = pg.getNextCursor();
        }
        return elements;
      } catch (TwitterException e) {
        throw e;
      }
    }

    /*
     * Get a cursored page of results from somewhere:
     */
    public abstract CursorSupport pageResponse(long cursor) throws TwitterException;

    /*
     * Do something with elements on page:
     */
    public abstract List<T> processElements(CursorSupport page) throws TwitterException;
  }

  /*
   * Page Through Results with "Pages" - timelines etc:
   */
  // TODO:

  /*
   * ========================================================================
   * Bulk Request Methods: Paging through results, avoiding cursors etc. 
   * ========================================================================
   */

  // TODO

  /*
   * Timelines
   */

  // TODO:


  public List<Status> getUserTimelineJSON(final long userId, final Date oldest_created_at) {

    List<Status> timeline = new ArrayList<>();

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

          timeline.add(s);
        }

        System.out.println(userId + " Fetched: " + lowestID.size() + ":" + ctweets + " tweets");

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
        System.err.println("FAILED TO RETRIEVE TIMELINE FOR USER: " + userId);
        break;
      }

    }

    return timeline;
  }

  public List<Status> getUpdateUserTimeline(final long userId, final long sinceId) {

    List<Status> timeline = new ArrayList<>();

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
          timeline.add(s);
        }

        System.out.println(userId + " Fetched: " + lowestID.size() + ":" + ctweets + " tweets");

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
        System.err.println("FAILED TO RETRIEVE TIMELINE FOR USER: " + userId);
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

  // TODO:

  /*
   * Get Lots of Friends IDs - By screenName or UserID. 5000 Per Call.
   */
  public <T> List<Long> getBulkFriendsIDs(final T ident) throws TwitterException {
    return getBulkFriendsIDs(ident, -1);
  }

  public <T> List<Long> getBulkFriendsIDs(final T ident, final int maxElements) throws TwitterException {
    return (new TwitterCursor<Long>() {
      @Override
      public CursorSupport pageResponse(long cursor) throws TwitterException {
        return fetchFriendsIDs(ident, cursor, 5000);
      }

      @Override
      public List<Long> processElements(CursorSupport page) throws TwitterException {
        List<Long> friends = new ArrayList<>();
        for (Long l : ((IDs) page).getIDs()) {
          friends.add(l);
        }
        return friends;
      }
    }).getElements(maxElements);
  }

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

  /*
   * Get Lots of List Members
   */
  public List<User> getBulkListMembers(final long listId, final int maxElements) throws TwitterException {
    return (new TwitterCursor<User>() {
      @Override
      public CursorSupport pageResponse(long cursor) throws TwitterException {
        return getUserListMembers(listId, cursor);
      }

      @SuppressWarnings("unchecked")
      @Override
      public List<User> processElements(CursorSupport page) throws TwitterException {
        List<User> listMembers = new ArrayList<>();
        listMembers.addAll((PagableResponseList<User>) page);
        return listMembers;
      }
    }).getElements(maxElements);
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
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        if (ident instanceof String) {
          return bot.getTwitter().timelines().getUserTimeline((String) ident, paging);
        } else if (ident instanceof Long) {
          return bot.getTwitter().timelines().getUserTimeline((Long) ident, paging);
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
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().tweets().getRetweets(statusId);
      }
    }).getResponse(EndPoint.STATUSES_RETWEETS);
  }

  @Override
  public IDs getRetweeterIds(final long statusId, final int count, final long cursor) throws TwitterException {
    return (new TwitterCommand<IDs>() {
      @Override
      public IDs fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().tweets().getRetweeterIds(statusId, count, cursor);
      }
    }).getResponse(EndPoint.STATUSES_RETWEETERS);
  }

  @Override
  public Status showStatus(final long id) throws TwitterException {
    return (new TwitterCommand<Status>() {
      @Override
      public Status fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().tweets().showStatus(id);
      }
    }).getResponse(EndPoint.STATUSES_SHOW);
  }

  @Override
  public ResponseList<Status> lookup(final long[] ids) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().tweets().lookup(ids);
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
      public QueryResult fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().search().search(query);
      }
    }).getResponse(EndPoint.SEARCH_TWEETS);
  }


  /*
   * FriendsFollowersResources
   */

  public <T> IDs fetchFriendsIDs(final T ident, final long cursor, final int count) throws TwitterException {
    return (new TwitterCommand<IDs>() {
      @Override
      public IDs fetchResponse(final TwitterBot bot) throws TwitterException {
        if (ident instanceof String) {
          return bot.getTwitter().friendsFollowers().getFriendsIDs((String) ident, cursor, count);
        } else if (ident instanceof Long) {
          return bot.getTwitter().friendsFollowers().getFriendsIDs((Long) ident, cursor, count);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FRIENDS_IDS);
  }

  public <T> IDs fetchFollowersIDs(final T ident, final long cursor, final int count) throws TwitterException {
    return (new TwitterCommand<IDs>() {
      @Override
      public IDs fetchResponse(final TwitterBot bot) throws TwitterException {
        if (ident instanceof String) {
          return bot.getTwitter().friendsFollowers().getFollowersIDs((String) ident, cursor, count);
        } else if (ident instanceof Long) {
          return bot.getTwitter().friendsFollowers().getFollowersIDs((Long) ident, cursor, count);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FOLLOWERS_IDS);
  }

  public <T> Relationship fetchFriendship(final T sourceIdent, final T targetIdent) throws TwitterException {
    return (new TwitterCommand<Relationship>() {
      @Override
      public Relationship fetchResponse(final TwitterBot bot) throws TwitterException {
        if (sourceIdent instanceof String && targetIdent instanceof String) {
          return bot.getTwitter().friendsFollowers().showFriendship((String) sourceIdent, (String) targetIdent);
        } else if (sourceIdent instanceof Long && targetIdent instanceof Long) {
          return bot.getTwitter().friendsFollowers().showFriendship((Long) sourceIdent, (Long) targetIdent);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FRIENDSHIPS_SHOW);
  }

  public <T> PagableResponseList<User> fetchFriendsList(final T ident, final long cursor, final int count, final boolean skipStatus,
                                                        final boolean includeUserEntities) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        if (ident instanceof String) {
          return bot.getTwitter().friendsFollowers().getFriendsList((String) ident, cursor, count, skipStatus, includeUserEntities);
        } else if (ident instanceof Long) {
          return bot.getTwitter().friendsFollowers().getFriendsList((Long) ident, cursor, count, skipStatus, includeUserEntities);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FRIENDS_LIST);
  }

  public <T> PagableResponseList<User> fetchFollowersList(final T ident, final long cursor, final int count, final boolean skipStatus,
                                                          final boolean includeUserEntities) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        if (ident instanceof String) {
          return bot.getTwitter().friendsFollowers().getFollowersList((String) ident, cursor, count, skipStatus, includeUserEntities);
        } else if (ident instanceof Long) {
          return bot.getTwitter().friendsFollowers().getFollowersList((Long) ident, cursor, count, skipStatus, includeUserEntities);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.FOLLOWERS_LIST);
  }

  /*
   * UsersResources
   */

  public <T> ResponseList<User> fetchLookupUsers(final T idents) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {

        if (idents instanceof String[]) {
          return bot.getTwitter().users().lookupUsers((String[]) idents);
        } else if (idents instanceof Long[]) {
          return bot.getTwitter().users().lookupUsers(Util.toPrimitive((Long[]) idents));
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
      public User fetchResponse(final TwitterBot bot) throws TwitterException {
        if (ident instanceof String) {
          return bot.getTwitter().users().showUser((String) ident);
        } else if (ident instanceof Long) {
          return bot.getTwitter().users().showUser((Long) ident);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }).getResponse(EndPoint.USERS_SHOW);
  }


  @Override
  public ResponseList<User> searchUsers(final String query, final int page) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().users().searchUsers(query, page);
      }
    }).getResponse(EndPoint.USERS_SEARCH);
  }



  // TODO: Overrides:



  @Override
  public ResponseList<User> getContributees(final long userId) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().users().getContributees(userId);
      }
    }).getResponse(EndPoint.USERS_CONTRIBUTEES);
  }

  @Override
  public ResponseList<User> getContributees(final String screenName) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().users().getContributees(screenName);
      }
    }).getResponse(EndPoint.USERS_CONTRIBUTEES);
  }



  @Override
  public ResponseList<User> getContributors(final String screenName) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {

        return bot.getTwitter().users().getContributors(screenName);
      }
    }).getResponse(EndPoint.USERS_CONTRIBUTORS);
  }

  @Override
  public ResponseList<User> getContributors(final long userId) throws TwitterException {
    return (new TwitterCommand<ResponseList<User>>() {
      @Override
      public ResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().users().getContributors(userId);
      }
    }).getResponse(EndPoint.USERS_CONTRIBUTORS);
  }



  /*
   * FavoritesResources
   */



  @Override
  public ResponseList<Status> getFavorites(final long userId) throws TwitterException {
    return getFavorites(userId, new Paging());
  }

  @Override
  public ResponseList<Status> getFavorites(final long userId, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().favorites().getFavorites(userId, paging);
      }
    }).getResponse(EndPoint.FAVORITES_LIST);
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName) throws TwitterException {
    return getFavorites(screenName, new Paging());
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().favorites().getFavorites(screenName, paging);
      }
    }).getResponse(EndPoint.FAVORITES_LIST);
  }

  /*
   * ListsResources
   */

  @Override
  public ResponseList<UserList> getUserLists(final String listOwnerScreenName) throws TwitterException {
    return (new TwitterCommand<ResponseList<UserList>>() {
      @Override
      public ResponseList<UserList> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserLists(listOwnerScreenName);
      }
    }).getResponse(EndPoint.LISTS_LIST);
  }

  @Override
  public ResponseList<UserList> getUserLists(final long listOwnerUserId) throws TwitterException {
    return (new TwitterCommand<ResponseList<UserList>>() {
      @Override
      public ResponseList<UserList> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserLists(listOwnerUserId);
      }
    }).getResponse(EndPoint.LISTS_LIST);
  }

  @Override
  public ResponseList<Status> getUserListStatuses(final long listId, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListStatuses(listId, paging);
      }
    }).getResponse(EndPoint.LISTS_STATUSES);
  }

  @Override
  public ResponseList<Status> getUserListStatuses(final long ownerId, final String slug, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListStatuses(ownerId, slug, paging);
      }
    }).getResponse(EndPoint.LISTS_STATUSES);
  }

  @Override
  public ResponseList<Status> getUserListStatuses(final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
    return (new TwitterCommand<ResponseList<Status>>() {
      @Override
      public ResponseList<Status> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListStatuses(ownerScreenName, slug, paging);
      }
    }).getResponse(EndPoint.LISTS_STATUSES);
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(final long listMemberId, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListMemberships(listMemberId, cursor);
      }
    }).getResponse(EndPoint.LISTS_MEMBERSHIPS);
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(final String listMemberScreenName, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListMemberships(listMemberScreenName, cursor);
      }
    }).getResponse(EndPoint.LISTS_MEMBERSHIPS);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final long listId, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListSubscribers(listId, cursor);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final long ownerId, final String slug, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListSubscribers(ownerId, slug, cursor);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListSubscribers(ownerScreenName, slug, cursor);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS);
  }

  @Override
  public User showUserListSubscription(final long listId, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserListSubscription(listId, userId);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
  }

  @Override
  public User showUserListSubscription(final long ownerId, final String slug, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserListSubscription(ownerId, slug, userId);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
  }

  @Override
  public User showUserListSubscription(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserListSubscription(ownerScreenName, slug, userId);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
  }

  @Override
  public User showUserListMembership(final long listId, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserListMembership(listId, userId);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
  }

  @Override
  public User showUserListMembership(final long ownerId, final String slug, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserListMembership(ownerId, slug, userId);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
  }

  @Override
  public User showUserListMembership(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return (new TwitterCommand<User>() {
      @Override
      public User fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserListMembership(ownerScreenName, slug, userId);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final long listId, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListMembers(listId, cursor);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final long ownerId, final String slug, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListMembers(ownerId, slug, cursor);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<User>>() {
      @Override
      public PagableResponseList<User> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListMembers(ownerScreenName, slug, cursor);
      }
    }).getResponse(EndPoint.LISTS_MEMBERS);
  }

  @Override
  public UserList showUserList(final long listId) throws TwitterException {
    return (new TwitterCommand<UserList>() {
      @Override
      public UserList fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserList(listId);
      }
    }).getResponse(EndPoint.LISTS_SHOW);
  }

  @Override
  public UserList showUserList(final long ownerId, final String slug) throws TwitterException {
    return (new TwitterCommand<UserList>() {
      @Override
      public UserList fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserList(ownerId, slug);
      }
    }).getResponse(EndPoint.LISTS_SHOW);
  }

  @Override
  public UserList showUserList(final String ownerScreenName, final String slug) throws TwitterException {
    return (new TwitterCommand<UserList>() {
      @Override
      public UserList fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().showUserList(ownerScreenName, slug);
      }
    }).getResponse(EndPoint.LISTS_SHOW);
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(final String listOwnerScreenName, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListSubscriptions(listOwnerScreenName, cursor);
      }
    }).getResponse(EndPoint.LISTS_SUBSCRIPTIONS);
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListsOwnerships(listOwnerScreenName, count, cursor);
      }
    }).getResponse(EndPoint.LISTS_OWNERSHIPS);
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(final long listOwnerId, final int count, final long cursor) throws TwitterException {
    return (new TwitterCommand<PagableResponseList<UserList>>() {
      @Override
      public PagableResponseList<UserList> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().list().getUserListsOwnerships(listOwnerId, count, cursor);
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
      public Place fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().placesGeo().getGeoDetails(placeId);
      }
    }).getResponse(EndPoint.GEO_ID);
  }

  @Override
  public ResponseList<Place> reverseGeoCode(final GeoQuery query) throws TwitterException {
    return (new TwitterCommand<ResponseList<Place>>() {
      @Override
      public ResponseList<Place> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().placesGeo().reverseGeoCode(query);
      }
    }).getResponse(EndPoint.GEO_REVERSE_GEOCODE);
  }

  @Override
  public ResponseList<Place> searchPlaces(final GeoQuery query) throws TwitterException {
    return (new TwitterCommand<ResponseList<Place>>() {
      @Override
      public ResponseList<Place> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().placesGeo().searchPlaces(query);
      }
    }).getResponse(EndPoint.GEO_SEARCH);
  }

  @Override
  public ResponseList<Place>
      getSimilarPlaces(final GeoLocation location, final String name, final String containedWithin, final String streetAddress) throws TwitterException {
    return (new TwitterCommand<ResponseList<Place>>() {
      @Override
      public ResponseList<Place> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().placesGeo().getSimilarPlaces(location, name, containedWithin, streetAddress);
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
      public Trends fetchResponse(final TwitterBot bot) throws TwitterException {

        return bot.getTwitter().trends().getPlaceTrends(woeid);
      }
    }).getResponse(EndPoint.TRENDS_PLACE);
  }

  @Override
  public ResponseList<Location> getAvailableTrends() throws TwitterException {
    return (new TwitterCommand<ResponseList<Location>>() {
      @Override
      public ResponseList<Location> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().trends().getAvailableTrends();
      }
    }).getResponse(EndPoint.TRENDS_AVAILABLE);
  }

  @Override
  public ResponseList<Location> getClosestTrends(final GeoLocation location) throws TwitterException {
    return (new TwitterCommand<ResponseList<Location>>() {
      @Override
      public ResponseList<Location> fetchResponse(final TwitterBot bot) throws TwitterException {
        return bot.getTwitter().trends().getClosestTrends(location);
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
