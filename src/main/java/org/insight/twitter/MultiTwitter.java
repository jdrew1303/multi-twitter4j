package org.insight.twitter;

import static twitter4j.TwitterObjects.join;
import static twitter4j.TwitterObjects.jsonList;
import static twitter4j.TwitterObjects.jsonMap;
import static twitter4j.TwitterObjects.newIDs;
import static twitter4j.TwitterObjects.parameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.insight.twitter.rpc.RPCClient;
import org.insight.twitter.util.EndPoint;
import org.insight.twitter.util.PartitioningSpliterator;
import org.insight.twitter.util.TwitterCursor;
import org.insight.twitter.util.TwitterJSONCursor;
import org.insight.twitter.util.TwitterManualPage;
import org.insight.twitter.util.TwitterPage;
import org.insight.twitter.util.TwitterQueryPage;
import org.insight.twitter.wrapper.TwitterResources;

import twitter4j.CursorSupport;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.HttpParameter;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.TwitterObjects;
import twitter4j.User;

/*
 * Only implements REST API calls that can be spread over multiple accounts.
 * 
 * Should be straight forward to add unimplemented methods, if you really need them.
 * 
 * All unimplemented methods will throw UnsupportedMethodException
 */
public class MultiTwitter extends TwitterResources implements AutoCloseable {

  private RPCClient rpc;

  public MultiTwitter() {
    try {
      rpc = new RPCClient();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() {
    try {
      rpc.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
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
  public <T> List<String> getBulkUserTimeline(final T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return new TwitterPage() {
      @Override
      public List<String> pageResponse(Paging page) throws TwitterException {
        return getUserTimelineJSON(ident, page);
      }
    }.getElements(initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<String> getBulkFavorites(final T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return new TwitterPage() {
      @Override
      public List<String> pageResponse(Paging page) throws TwitterException {
        return getFavoritesJSON(ident, page);
      }
    }.getElements(initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<String> getBulkUserListStatuses(final T ident, final String slug, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return new TwitterPage() {
      @Override
      public List<String> pageResponse(Paging page) throws TwitterException {
        return getUserListStatusesJSON(ident, slug, page);
      }
    }.getElements(initSinceId, initMaxId, maxElements);
  }

  /*
   * Tweets:
   */

  @Override
  public List<Long> getBulkRetweeterIds(final long statusId, int maxElements) throws TwitterException {
    return new TwitterCursor<Long>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return getRetweeterIds(statusId, 100, cursor);
      }
    }.getElements(maxElements);
  }

  @Override
  public Map<Long, String> getBulkTweetLookupMap(Collection<Long> ids) throws TwitterException {
    Map<Long, String> sort = new TreeMap<Long, String>();
    getBulkTweetLookupMap(ids, sort::put);
    return sort;
  }

  public void getBulkTweetLookupStream(Stream<Long> ids, BiConsumer<? super Long, ? super String> action) throws TwitterException {
    ExecutorService executor = Executors.newWorkStealingPool(64); // num of bots

    Stream<List<Long>> partitioned = PartitioningSpliterator.partition(ids, 100, 100);

    partitioned.parallel().map(TwitterObjects::toPrimitive).forEach(batch -> CompletableFuture.supplyAsync(() -> {
      Map<Long, String> m = new HashMap<Long, String>();
      try (MultiTwitter mt = new MultiTwitter()) {
        m = mt.lookupJSON(batch);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return m;
    }, executor).thenAccept(m -> {
      m.forEach(action);
    }));
  }

  public void getBulkTweetLookupMap(Collection<Long> ids, BiConsumer<? super Long, ? super String> action) throws TwitterException {
    Set<Long> uniqueIds = new LinkedHashSet<Long>(ids);
    List<List<Long>> batches = TwitterObjects.partitionList(new ArrayList<Long>(uniqueIds), 100);

    ExecutorService executor = Executors.newWorkStealingPool();

    /*
    public static ExecutorService executor = Executors.newFixedThreadPool(8, r -> {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      return thread;
    });*/

    ExecutorCompletionService<Map<Long, String>> completionService = new ExecutorCompletionService<Map<Long, String>>(executor);

    for (List<Long> batch : batches) {
      Callable<Map<Long, String>> c = () -> {
        try (MultiTwitter mt = new MultiTwitter()) {
          return mt.lookupJSON(TwitterObjects.toPrimitive(batch));
        }
      };
      completionService.submit(c);
    }

    for (int i = 0; i < batches.size(); ++i) {
      try {
        final Future<Map<Long, String>> future = completionService.take();
        final Map<Long, String> content = future.get();
        content.forEach(action); // Apply an action to each tweet
      } catch (ExecutionException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  /*
   * Users
   */

  @Override
  public <T> List<String> getBulkLookupUsers(final Collection<T> idents) throws TwitterException {
    List<String> results = new ArrayList<String>();
    getBulkLookupUsers(idents, results::add);
    return results;
  }

  public <T> void getBulkLookupUsers(final Collection<T> idents, Consumer<? super String> action) throws TwitterException {
    Set<T> unique_list = new LinkedHashSet<T>(idents);
    List<List<T>> batches = TwitterObjects.partitionList(new ArrayList<T>(unique_list), 100);

    ExecutorService executor = Executors.newWorkStealingPool();
    ExecutorCompletionService<List<String>> completionService = new ExecutorCompletionService<List<String>>(executor);

    for (List<T> batch : batches) {
      Callable<List<String>> c = () -> {
        try (MultiTwitter mt = new MultiTwitter()) {
          // To array:
          if (batch.get(0) instanceof String) {
            return mt.lookupUsersJSON(batch.toArray(new String[batch.size()]));
          } else if (batch.get(0) instanceof Long) {
            return mt.lookupUsersJSON(batch.toArray(new Long[batch.size()]));
          } else {
            return new ArrayList<String>();
          }
        }
      };
      completionService.submit(c);
    }

    for (int i = 0; i < batches.size(); ++i) {
      try {
        final Future<List<String>> future = completionService.take();
        final List<String> content = future.get();
        content.forEach(action); // Apply an action to each user
      } catch (ExecutionException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  /*
   * FriendsFollowers
   */

  @Override
  public <T> List<Long> getBulkFriendsIDs(final T ident, int maxElements) throws TwitterException {
    return new TwitterCursor<Long>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return newIDs(getFriendsIDsJSON(ident, cursor, 5000));
      }
    }.getElements(maxElements);
  }

  @Override
  public <T> List<Long> getBulkFollowersIDs(final T ident, int maxElements) throws TwitterException {
    return new TwitterCursor<Long>() {
      @Override
      public CursorSupport cursorResponse(long cursor) throws TwitterException {
        return newIDs(getFollowersIDsJSON(ident, cursor, 5000));
      }
    }.getElements(maxElements);
  }

  @Override
  public <T> List<String> getBulkFriendsList(final T ident, int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return new TwitterJSONCursor() {
      @Override
      public String cursorResponse(long cursor) throws TwitterException {
        return getFriendsListJSON(ident, cursor, 200, skipStatus, includeUserEntities);
      }
    }.getElements("users", maxElements);
  }

  @Override
  public <T> List<String> getBulkFollowersList(final T ident, int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return new TwitterJSONCursor() {
      @Override
      public String cursorResponse(long cursor) throws TwitterException {
        return getFollowersListJSON(ident, cursor, 200, skipStatus, includeUserEntities);
      }
    }.getElements("users", maxElements);
  }

  /*
   * Search
   */



  @Override
  public List<String> getBulkSearchResults(Query query, int maxElements) throws TwitterException {
    return new TwitterQueryPage() {
      @Override
      public String pageResponse(Query query) throws TwitterException {
        return searchJSON(query);
      }
    }.getElements(query, maxElements);
  }

  /*
   * Users
   */

  // Awkwardly doesn't implement Paging or Cursors: Max Per page: 20, Max Results 1000.
  @Override
  public List<String> getBulkSearchUsers(final String query, int maxElements) throws TwitterException {
    return new TwitterManualPage() {
      @Override
      public List<String> pageResponse(int page) throws TwitterException {
        return searchUsersJSON(query, page);
      }
    }.getElements(maxElements);
  }

  /*
   * ListsResources
   */

  @Override
  public <T> List<String> getBulkUserListMemberships(final T ident, int maxElements) throws TwitterException {
    return new TwitterJSONCursor() {
      @Override
      public String cursorResponse(long cursor) throws TwitterException {
        return getUserListMembershipsJSON(ident, 1000, cursor, false);
      }
    }.getElements("lists", maxElements);
  }


  @Override
  public <T> List<String> getBulkUserListSubscribers(final T ident, final String slug, int maxElements) throws TwitterException {
    return new TwitterJSONCursor() {
      @Override
      public String cursorResponse(long cursor) throws TwitterException {
        return getUserListSubscribersJSON(ident, slug, 5000, cursor, false);
      }
    }.getElements("users", maxElements);
  }


  @Override
  public List<String> getBulkUserListMembers(final long listId, int maxElements) throws TwitterException {
    return new TwitterJSONCursor() {
      @Override
      public String cursorResponse(long cursor) throws TwitterException {
        return getUserListMembersJSON(listId, 5000, cursor, true);
      }
    }.getElements("users", maxElements);
  }

  @Override
  public <T> List<String> getBulkUserListSubscriptions(final T ident, int maxElements) throws TwitterException {
    return new TwitterJSONCursor() {
      @Override
      public String cursorResponse(long cursor) throws TwitterException {
        return getUserListSubscriptionsJSON(ident, 1000, cursor);
      }
    }.getElements("lists", maxElements);

  }

  @Override
  public <T> List<String> getBulkUserListsOwnerships(final T ident, int maxElements) throws TwitterException {
    return new TwitterJSONCursor() {
      @Override
      public String cursorResponse(long cursor) throws TwitterException {
        return getUserListsOwnershipsJSON(ident, 1000, cursor);
      }
    }.getElements("lists", maxElements);
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
  public <T> List<String> getUserTimelineJSON(T ident, Paging paging) throws TwitterException {
    if (ident instanceof String) {
      return jsonList(rpc.call(EndPoint.STATUSES_USER_TIMELINE, parameters(paging, new HttpParameter("screen_name", (String) ident))));
    } else if (ident instanceof Long) {
      return jsonList(rpc.call(EndPoint.STATUSES_USER_TIMELINE, parameters(paging, new HttpParameter("user_id", (Long) ident))));
    } else if (ident instanceof User) {
      return jsonList(rpc.call(EndPoint.STATUSES_USER_TIMELINE, parameters(paging, new HttpParameter("user_id", ((User) ident).getId()))));
    } else {
      throw new IllegalArgumentException();
    }
  }

  /*
   * TweetsResources
   */

  @Override
  public List<String> getRetweetsJSON(long statusId) throws TwitterException {
    return jsonList(rpc.call(EndPoint.STATUSES_RETWEETS, "statuses/retweets/" + statusId + ".json", new HttpParameter("count", 100)));
  }

  @Override
  public String getRetweeterIdsJSON(long statusId, int count, long cursor) throws TwitterException {
    return rpc.call(EndPoint.STATUSES_RETWEETERS, new HttpParameter("id", statusId), new HttpParameter("cursor", cursor), new HttpParameter("count", count));
  }

  @Override
  public String showStatusJSON(long id) throws TwitterException {
    return rpc.call(EndPoint.STATUSES_SHOW, "statuses/show/" + id + ".json");
  }

  @Override
  public Map<Long, String> lookupJSON(long... ids) throws TwitterException {
    return jsonMap(rpc.call(EndPoint.STATUSES_LOOKUP, new HttpParameter("map", true), new HttpParameter("id", join(ids))));
  }

  /*
   * SearchResource
   */

  @Override
  public String searchJSON(Query query) throws TwitterException {
    return rpc.call(EndPoint.SEARCH_TWEETS, parameters(query));
  }

  @Override
  public List<String> searchUsersJSON(String query, int page) throws TwitterException {
    return jsonList(rpc.call(EndPoint.USERS_SEARCH, new HttpParameter("q", query), new HttpParameter("count", 20), new HttpParameter("page", page)));
  }

  /*
   * FriendsFollowersResources
   */

  @Override
  public <T> String getFriendsIDsJSON(T ident, long cursor, int count) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.FRIENDS_IDS, new HttpParameter("screen_name", (String) ident), new HttpParameter("cursor", cursor), new HttpParameter("count",
          count));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.FRIENDS_IDS, new HttpParameter("user_id", (Long) ident), new HttpParameter("cursor", cursor), new HttpParameter("count", count));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String getFollowersIDsJSON(T ident, long cursor, int count) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.FOLLOWERS_IDS, new HttpParameter("screen_name", (String) ident), new HttpParameter("cursor", cursor), new HttpParameter("count",
          count));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.FOLLOWERS_IDS, new HttpParameter("user_id", (Long) ident), new HttpParameter("cursor", cursor),
          new HttpParameter("count", count));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String getFriendshipJSON(T sourceIdent, T targetIdent) throws TwitterException {
    if ((sourceIdent instanceof String) && (targetIdent instanceof String)) {
      return rpc.call(EndPoint.FRIENDSHIPS_SHOW, new HttpParameter("source_screen_name", (String) sourceIdent), new HttpParameter("target_screen_name",
          (String) targetIdent));
    } else if ((sourceIdent instanceof Long) && (targetIdent instanceof Long)) {
      return rpc.call(EndPoint.FRIENDSHIPS_SHOW, new HttpParameter("source_id", (Long) sourceIdent), new HttpParameter("target_id", (Long) targetIdent));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String getFriendsListJSON(T ident, long cursor, int count, boolean skipStatus, boolean includeUserEntities) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.FRIENDS_LIST, new HttpParameter("screen_name", (String) ident), new HttpParameter("cursor", cursor), new HttpParameter("count",
          count), new HttpParameter("skip_status", skipStatus), new HttpParameter("include_user_entities", includeUserEntities));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.FRIENDS_LIST, new HttpParameter("user_id", (Long) ident), new HttpParameter("cursor", cursor),
          new HttpParameter("count", count), new HttpParameter("skip_status", skipStatus), new HttpParameter("include_user_entities", includeUserEntities));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String getFollowersListJSON(T ident, long cursor, int count, boolean skipStatus, boolean includeUserEntities) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.FOLLOWERS_LIST, new HttpParameter("screen_name", (String) ident), new HttpParameter("cursor", cursor), new HttpParameter(
          "count", count), new HttpParameter("skip_status", skipStatus), new HttpParameter("include_user_entities", includeUserEntities));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.FOLLOWERS_LIST, new HttpParameter("user_id", (Long) ident), new HttpParameter("cursor", cursor), new HttpParameter("count",
          count), new HttpParameter("skip_status", skipStatus), new HttpParameter("include_user_entities", includeUserEntities));
    } else {
      throw new IllegalArgumentException();
    }
  }

  /*
   * UsersResources
   */

  @Override
  public <T> List<String> lookupUsersJSON(T idents) throws TwitterException {
    if (idents instanceof String[]) {
      return jsonList(rpc.call(EndPoint.USERS_LOOKUP, new HttpParameter("screen_name", join((String[]) idents))));
    } else if (idents instanceof long[]) {
      return jsonList(rpc.call(EndPoint.USERS_LOOKUP, new HttpParameter("user_id", join((long[]) idents))));
    } else if (idents instanceof Long[]) {
      return jsonList(rpc.call(EndPoint.USERS_LOOKUP, new HttpParameter("user_id", join((Long[]) idents))));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String showUserJSON(T ident) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.USERS_SHOW, new HttpParameter("screen_name", (String) ident));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.USERS_SHOW, new HttpParameter("user_id", (Long) ident));
    } else {
      throw new IllegalArgumentException();
    }
  }

  /*
  @Override
  public <T> List<String> getContributeesJSON(T ident) throws TwitterException {
    if (ident instanceof String) {
      return jsonList(rpc.call(EndPoint.USERS_CONTRIBUTEES, new HttpParameter("screen_name", (String) ident)));
    } else if (ident instanceof Long) {
      return jsonList(rpc.call(EndPoint.USERS_CONTRIBUTEES, new HttpParameter("user_id", (Long) ident)));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> List<String> getContributorsJSON(T ident) throws TwitterException {
    if (ident instanceof String) {
      return jsonList(rpc.call(EndPoint.USERS_CONTRIBUTORS, new HttpParameter("screen_name", (String) ident)));
    } else if (ident instanceof Long) {
      return jsonList(rpc.call(EndPoint.USERS_CONTRIBUTORS, new HttpParameter("user_id", (Long) ident)));
    } else {
      throw new IllegalArgumentException();
    }
  }
  */

  /*
   * FavoritesResources
   */

  @Override
  public <T> List<String> getFavoritesJSON(T ident, Paging paging) throws TwitterException {
    if (ident instanceof String) {
      return jsonList(rpc.call(EndPoint.FAVORITES_LIST, parameters(paging, new HttpParameter("screen_name", (String) ident))));
    } else if (ident instanceof Long) {
      return jsonList(rpc.call(EndPoint.FAVORITES_LIST, parameters(paging, new HttpParameter("user_id", (Long) ident))));
    } else {
      throw new IllegalArgumentException();
    }
  }

  /*
   * ListsResources
   */

  @Override
  public <T> List<String> getUserListsJSON(T ident, boolean reverse) throws TwitterException {
    if (ident instanceof String) {
      return jsonList(rpc.call(EndPoint.LISTS_LIST, new HttpParameter("screen_name", (String) ident), new HttpParameter("reverse", reverse)));
    } else if (ident instanceof Long) {
      return jsonList(rpc.call(EndPoint.LISTS_LIST, new HttpParameter("user_id", (Long) ident), new HttpParameter("reverse", reverse)));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public List<String> getUserListStatusesJSON(long listId, Paging paging) throws TwitterException {
    return jsonList(rpc.call(EndPoint.LISTS_STATUSES, parameters(paging, new HttpParameter("list_id", listId))));
  }

  @Override
  public <T> List<String> getUserListStatusesJSON(T ident, String slug, Paging paging) throws TwitterException {
    if (ident instanceof String) {
      return jsonList(rpc.call(EndPoint.LISTS_STATUSES,
          parameters(paging, new HttpParameter("slug", slug), new HttpParameter("owner_screen_name", (String) ident))));
    } else if (ident instanceof Long) {
      return jsonList(rpc.call(EndPoint.LISTS_STATUSES, parameters(paging, new HttpParameter("slug", slug), new HttpParameter("owner_id", (Long) ident))));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String getUserListMembershipsJSON(T ident, int count, long cursor, boolean filterToOwnedLists) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_MEMBERSHIPS, new HttpParameter("screen_name", (String) ident), new HttpParameter("count", count), new HttpParameter(
          "cursor", cursor), new HttpParameter("filter_to_owned_lists", filterToOwnedLists));

    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.LISTS_MEMBERSHIPS, new HttpParameter("user_id", (Long) ident), new HttpParameter("count", count), new HttpParameter("cursor",
          cursor), new HttpParameter("filter_to_owned_lists", filterToOwnedLists));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String getUserListSubscribersJSON(long listId, int count, long cursor, boolean skipStatus) throws TwitterException {
    return rpc.call(EndPoint.LISTS_SUBSCRIBERS, new HttpParameter("list_id", listId), new HttpParameter("count", count), new HttpParameter("cursor", cursor),
        new HttpParameter("skip_status", skipStatus));
  }

  @Override
  public <T> String getUserListSubscribersJSON(T ident, String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_SUBSCRIBERS, new HttpParameter("owner_screen_name", (String) ident), new HttpParameter("slug", slug), new HttpParameter(
          "count", count), new HttpParameter("cursor", cursor), new HttpParameter("skip_status", skipStatus));
    } else if (ident instanceof Long) {

      return rpc.call(EndPoint.LISTS_SUBSCRIBERS, new HttpParameter("owner_id", (Long) ident), new HttpParameter("slug", slug), new HttpParameter("count",
          count), new HttpParameter("cursor", cursor), new HttpParameter("skip_status", skipStatus));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String showUserListSubscriptionJSON(long listId, long userId) throws TwitterException {
    return rpc.call(EndPoint.LISTS_SUBSCRIBERS_SHOW, new HttpParameter("list_id", listId), new HttpParameter("user_id", userId));
  }

  @Override
  public <T> String getUserListSubscriptionJSON(T ident, String slug, long userId) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_SUBSCRIBERS_SHOW, new HttpParameter("owner_screen_name", (String) ident), new HttpParameter("slug", slug),
          new HttpParameter("user_id", userId));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.LISTS_SUBSCRIBERS_SHOW, new HttpParameter("owner_id", (Long) ident), new HttpParameter("slug", slug), new HttpParameter(
          "user_id", userId));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String showUserListMembershipJSON(long listId, long userId) throws TwitterException {
    return rpc.call(EndPoint.LISTS_MEMBERS_SHOW, new HttpParameter("list_id", listId), new HttpParameter("user_id", userId));
  }

  @Override
  public <T> String getUserListMembershipJSON(T ident, String slug, long userId) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_MEMBERS_SHOW, new HttpParameter("owner_screen_name", (String) ident), new HttpParameter("slug", slug), new HttpParameter(
          "user_id", userId));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.LISTS_MEMBERS_SHOW, new HttpParameter("owner_id", (Long) ident), new HttpParameter("slug", slug), new HttpParameter("user_id",
          userId));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String getUserListMembersJSON(long listId, int count, long cursor, boolean skipStatus) throws TwitterException {
    return rpc.call(EndPoint.LISTS_MEMBERS, new HttpParameter("list_id", listId), new HttpParameter("count", count), new HttpParameter("cursor", cursor),
        new HttpParameter("skip_status", skipStatus));
  }

  @Override
  public <T> String getUserListMembersJSON(T ident, String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_MEMBERS, new HttpParameter("owner_screen_name", (String) ident), new HttpParameter("slug", slug), new HttpParameter(
          "count", count), new HttpParameter("cursor", cursor), new HttpParameter("skip_status", skipStatus));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.LISTS_MEMBERS, new HttpParameter("owner_id", (Long) ident), new HttpParameter("slug", slug), new HttpParameter("count", count),
          new HttpParameter("cursor", cursor), new HttpParameter("skip_status", skipStatus));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String showUserListJSON(long listId) throws TwitterException {
    return rpc.call(EndPoint.LISTS_SHOW, new HttpParameter("list_id", listId));
  }

  @Override
  public <T> String showUserListJSON(T ident, String slug) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_SHOW, new HttpParameter("owner_screen_name", (String) ident), new HttpParameter("slug", slug));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.LISTS_SHOW, new HttpParameter("owner_id", (Long) ident), new HttpParameter("slug", slug));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String getUserListSubscriptionsJSON(T ident, int count, long cursor) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_SUBSCRIPTIONS, new HttpParameter("screen_name", (String) ident), new HttpParameter("count", count), new HttpParameter(
          "cursor", cursor));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.LISTS_SUBSCRIPTIONS, new HttpParameter("user_id", (Long) ident), new HttpParameter("count", count), new HttpParameter("cursor",
          cursor));
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public <T> String getUserListsOwnershipsJSON(T ident, int count, long cursor) throws TwitterException {
    if (ident instanceof String) {
      return rpc.call(EndPoint.LISTS_OWNERSHIPS, new HttpParameter("screen_name", (String) ident), new HttpParameter("count", count), new HttpParameter(
          "cursor", cursor));
    } else if (ident instanceof Long) {
      return rpc.call(EndPoint.LISTS_OWNERSHIPS, new HttpParameter("user_id", (Long) ident), new HttpParameter("count", count), new HttpParameter("cursor",
          cursor));
    } else {
      throw new IllegalArgumentException();
    }
  }

  /*
   * PlacesGeoResources
   */

  @Override
  public String getGeoDetailsJSON(String placeId) throws TwitterException {
    return rpc.call(EndPoint.GEO_ID, "geo/id/" + placeId + ".json");
  }

  @Override
  public List<String> reverseGeoCodeJSON(GeoQuery query) throws TwitterException {
    return jsonList(rpc.call(EndPoint.GEO_REVERSE_GEOCODE, parameters(query)));
  }

  @Override
  public List<String> searchPlacesJSON(GeoQuery query) throws TwitterException {
    return jsonList(rpc.call(EndPoint.GEO_SEARCH, parameters(query)));
  }

  @Override
  public List<String> getSimilarPlacesJSON(GeoLocation location, String name, String containedWithin, String streetAddress) throws TwitterException {
    List<HttpParameter> params = new ArrayList<>(3);
    params.add(new HttpParameter("lat", location.getLatitude()));
    params.add(new HttpParameter("long", location.getLongitude()));
    params.add(new HttpParameter("name", name));
    if (null != containedWithin) {
      params.add(new HttpParameter("contained_within", containedWithin));
    }
    if (null != streetAddress) {
      params.add(new HttpParameter("attribute:street_address", streetAddress));
    }
    return jsonList(rpc.call(EndPoint.GEO_SIMILAR_PLACES, params.toArray(new HttpParameter[params.size()])));
  }

  /*
   * TrendsResources
   */

  @Override
  public String getPlaceTrendsJSON(int woeid) throws TwitterException {
    return rpc.call(EndPoint.TRENDS_PLACE, new HttpParameter("id", woeid));
  }

  @Override
  public List<String> getAvailableTrendsJSON() throws TwitterException {
    return jsonList(rpc.call(EndPoint.TRENDS_AVAILABLE));
  }

  @Override
  public List<String> getClosestTrendsJSON(GeoLocation location) throws TwitterException {
    return jsonList(rpc.call(EndPoint.TRENDS_CLOSEST, new HttpParameter("lat", location.getLatitude()), new HttpParameter("long", location.getLongitude())));
  }

  /*
   * HelpResources
   */

  @Override
  public Map<String, RateLimitStatus> getRateLimitStatus(String... resources) throws TwitterException {
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
  public Map<String, RateLimitStatus> getRateLimitStatus(EndPoint... endpoints) throws TwitterException {
    Map<String, RateLimitStatus> rateLimits = new HashMap<>();
    for (EndPoint target : endpoints) {
      rateLimits.put(target.toString(), getRateLimitStatus(target));
    }
    return rateLimits;
  }

  /*
   * Get Combined Rate Limit from all available bots
   */
  public RateLimitStatus getRateLimitStatus(EndPoint endpoint) throws TwitterException {
    String rl = rpc.call(EndPoint.APPLICATION_RATE_LIMIT_STATUS, endpoint.toString());
    return TwitterObjects.newRateLimitStatus(rl);
  }



  @Override
  public ResponseList<User> getContributees(long userId) throws TwitterException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseList<User> getContributees(String screenName) throws TwitterException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseList<User> getContributors(long userId) throws TwitterException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseList<User> getContributors(String screenName) throws TwitterException {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * All other unimplemented methods will throw UnsupportedMethodException
   */
}
