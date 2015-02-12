package org.insight.twitter.internal;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterResponse;


/*
 * This implements the unsupported methods, throwing exceptions, and leaves the valid methods to be implemented by NewMultiTwitter This is class is mostly to
 * make the implementing MultiTwitter class a bit easier to work with. Unimplemented methods not suitable for bots.
 */
public abstract class TwitterJSONResources extends TwitterResources {

  public enum As {
    JSON;
  };

  /*
   * Get JSON - Only works if calling from the same thread, and immediately after last request!
   */
  public static String getJSON(TwitterResponse r) {
    return TwitterObjectFactory.getRawJSON(r);
  }

  public static List<String> getJSONList(List<? extends TwitterResponse> l) {
    List<String> objects = new ArrayList<>();
    for (Object r : l) {
      objects.add(TwitterObjectFactory.getRawJSON(r));
    }
    return objects;
  }

  /*
   * TimelinesResources
   */

  public <T> List<String> fetchUserTimeline(As json, final T ident, final Paging paging) throws TwitterException {
    return getJSONList(fetchUserTimeline(ident, paging));
  }

  public List<String> getUserTimeline(As json, final String screenName) throws TwitterException {
    return fetchUserTimeline(json, screenName, new Paging());
  }

  public List<String> getUserTimeline(As json, final String screenName, final Paging paging) throws TwitterException {
    return fetchUserTimeline(json, screenName, paging);
  }

  public List<String> getUserTimeline(As json, final long userId) throws TwitterException {
    return fetchUserTimeline(json, userId, new Paging());
  }

  public List<String> getUserTimeline(As json, final long userId, final Paging paging) throws TwitterException {
    return fetchUserTimeline(json, userId, paging);
  }

  /*
   * TweetsResources
   */

  public List<String> getRetweets(As json, final long statusId) throws TwitterException {
    return getJSONList(getRetweets(statusId));
  }

  public String getRetweeterIds(As json, final long statusId, final long cursor) throws TwitterException {
    return getJSON(getRetweeterIds(statusId, 100, cursor));
  }

  public String getRetweeterIds(As json, final long statusId, final int count, final long cursor) throws TwitterException {
    return getJSON(getRetweeterIds(statusId, count, cursor));
  }

  public String showStatus(As json, final long id) throws TwitterException {
    return getJSON(showStatus(id));
  }

  public List<String> lookup(As json, final long[] ids) throws TwitterException {
    return getJSONList(lookup(ids));
  }

  /*
   * SearchResource
   */

  public String search(As json, final Query query) throws TwitterException {
    return getJSON(search(query));
  }

  /*
   * FriendsFollowersResources
   */

  public <T> String fetchFriendsIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
    return getJSON(fetchFriendsIDs(ident, cursor, count));
  }

  public String getFriendsIDs(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFriendsIDs(json, userId, cursor, 5000);
  }

  public String getFriendsIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(json, userId, cursor, count);
  }

  public String getFriendsIDs(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsIDs(json, screenName, cursor, 5000);
  }

  public String getFriendsIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(json, screenName, cursor, count);
  }

  public <T> String fetchFollowersIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
    return getJSON(fetchFollowersIDs(ident, cursor, count));
  }

  public String getFollowersIDs(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFollowersIDs(json, userId, cursor, 5000);
  }

  public String getFollowersIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(json, userId, cursor, count);
  }

  public String getFollowersIDs(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersIDs(json, screenName, cursor, 5000);
  }

  public String getFollowersIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(json, screenName, cursor, count);
  }

  public <T> String fetchFriendship(As json, final T sourceIdent, final T targetIdent) throws TwitterException {
    return getJSON(fetchFriendship(sourceIdent, targetIdent));
  }

  public String showFriendship(As json, final long sourceId, final long targetId) throws TwitterException {
    return fetchFriendship(json, sourceId, targetId);
  }

  public String showFriendship(As json, final String sourceScreenName, final String targetScreenName) throws TwitterException {
    return fetchFriendship(json, sourceScreenName, targetScreenName);
  }

  public <T> List<String> fetchFriendsList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
                                           final boolean includeUserEntities) throws TwitterException {
    return getJSONList(fetchFriendsList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  public List<String> getFriendsList(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFriendsList(json, userId, cursor, 200, false, true);
  }

  public List<String> getFriendsList(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(json, userId, cursor, 200, false, true);
  }

  public
      List<String>
      getFriendsList(As json, final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
                                                                                                                                                 throws TwitterException {
    return fetchFriendsList(json, userId, cursor, count, skipStatus, includeUserEntities);
  }

  public List<String> getFriendsList(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, 200, false, true);
  }

  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, count, false, true);
  }

  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
                                     final boolean includeUserEntities) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, count, skipStatus, includeUserEntities);
  }

  public <T> List<String> fetchFollowersList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
                                             final boolean includeUserEntities) throws TwitterException {
    return getJSONList(fetchFollowersList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  public List<String> getFollowersList(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, 200, false, true);
  }

  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, 200, false, true);
  }

  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count, final boolean skipStatus,
                                       final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, count, skipStatus, includeUserEntities);
  }

  public List<String> getFollowersList(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, 200, false, true);
  }

  public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, count, false, true);
  }

  public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
                                       final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, count, skipStatus, includeUserEntities);
  }



  /*
   * UsersResources
   */

  public <T> List<String> fetchLookupUsers(As json, final T idents) throws TwitterException {
    return getJSONList(fetchLookupUsers(idents));
  }

  public List<String> lookupUsers(As json, final long[] ids) throws TwitterException {
    return fetchLookupUsers(json, ids);
  }

  public List<String> lookupUsers(As json, final String[] screenNames) throws TwitterException {
    return fetchLookupUsers(json, screenNames);
  }

  public <T> String fetchUser(As json, final T ident) throws TwitterException {
    return getJSON(fetchUser(ident));
  }

  public String showUser(As json, final long userId) throws TwitterException {
    return fetchUser(json, userId);
  }

  public String showUser(As json, final String screenName) throws TwitterException {
    return fetchUser(json, screenName);
  }

  public List<String> searchUsers(As json, final String query, final int page) throws TwitterException {
    return getJSONList(searchUsers(query, page));
  }

  /*
   * FavoritesResources
   */



  /*
   * ListsResources
   */



  /*
   * PlacesGeoResources
   */

  //

  /*
   * TrendsResources
   */

  //

  /*
   * HelpResources
   */



}
