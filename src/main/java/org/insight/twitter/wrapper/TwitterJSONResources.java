package org.insight.twitter.wrapper;

import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterResponse;


/*
 * Wrap TwitterResources methods to access original JSON from Twitter.
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

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserTimeline()
   */
  public <T> List<String> fetchUserTimeline(As json, final T ident, final Paging paging) throws TwitterException {
    return getJSONList(fetchUserTimeline(ident, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String)
   */
  public List<String> getUserTimeline(As json, final String screenName) throws TwitterException {
    return fetchUserTimeline(json, screenName, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String, Paging)
   */
  public List<String> getUserTimeline(As json, final String screenName, final Paging paging) throws TwitterException {
    return fetchUserTimeline(json, screenName, paging);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long)
   */
  public List<String> getUserTimeline(As json, final long userId) throws TwitterException {
    return fetchUserTimeline(json, userId, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long, Paging)
   */
  public List<String> getUserTimeline(As json, final long userId, final Paging paging) throws TwitterException {
    return fetchUserTimeline(json, userId, paging);
  }

  /*
   * TweetsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweets()
   */
  public List<String> getRetweets(As json, final long statusId) throws TwitterException {
    return getJSONList(getRetweets(statusId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  public String getRetweeterIds(As json, final long statusId, final long cursor) throws TwitterException {
    return getJSON(getRetweeterIds(statusId, 100, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  public String getRetweeterIds(As json, final long statusId, final int count, final long cursor) throws TwitterException {
    return getJSON(getRetweeterIds(statusId, count, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showStatus(long)
   */
  public String showStatus(As json, final long id) throws TwitterException {
    return getJSON(showStatus(id));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookup(long[])
   */
  public List<String> lookup(As json, final long[] ids) throws TwitterException {
    return getJSONList(lookup(ids));
  }

  /*
   * SearchResource
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#search()
   */
  public String search(As json, final Query query) throws TwitterException {
    return getJSON(search(query));
  }

  /*
   * FriendsFollowersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsIDs()
   */
  public <T> String fetchFriendsIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
    return getJSON(fetchFriendsIDs(ident, cursor, count));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFriendsIDs(json, userId, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(json, userId, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsIDs(json, screenName, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(json, screenName, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFollowersIDs()
   */
  public <T> String fetchFollowersIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
    return getJSON(fetchFollowersIDs(ident, cursor, count));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFollowersIDs(json, userId, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(json, userId, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersIDs(json, screenName, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(json, screenName, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendship()
   */
  public <T> String fetchFriendship(As json, final T sourceIdent, final T targetIdent) throws TwitterException {
    return getJSON(fetchFriendship(sourceIdent, targetIdent));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  public String showFriendship(As json, final long sourceId, final long targetId) throws TwitterException {
    return fetchFriendship(json, sourceId, targetId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  public String showFriendship(As json, final String sourceScreenName, final String targetScreenName) throws TwitterException {
    return fetchFriendship(json, sourceScreenName, targetScreenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsList()
   */
  public <T> List<String> fetchFriendsList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
                                           final boolean includeUserEntities) throws TwitterException {
    return getJSONList(fetchFriendsList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFriendsList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public
      List<String>
      getFriendsList(As json, final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
                                                                                                                                                 throws TwitterException {
    return fetchFriendsList(json, userId, cursor, count, skipStatus, includeUserEntities);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, count, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
                                     final boolean includeUserEntities) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, count, skipStatus, includeUserEntities);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public <T> List<String> fetchFollowersList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
                                             final boolean includeUserEntities) throws TwitterException {
    return getJSONList(fetchFollowersList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count, final boolean skipStatus,
                                       final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, count, skipStatus, includeUserEntities);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, count, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
                                       final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, count, skipStatus, includeUserEntities);
  }

  /*
   * UsersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchLookupUsers()
   */
  public <T> List<String> fetchLookupUsers(As json, final T idents) throws TwitterException {
    return getJSONList(fetchLookupUsers(idents));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  public List<String> lookupUsers(As json, final long[] ids) throws TwitterException {
    return fetchLookupUsers(json, ids);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  public List<String> lookupUsers(As json, final String[] screenNames) throws TwitterException {
    return fetchLookupUsers(json, screenNames);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUser()
   */
  public <T> String fetchUser(As json, final T ident) throws TwitterException {
    return getJSON(fetchUser(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  public String showUser(As json, final long userId) throws TwitterException {
    return fetchUser(json, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  public String showUser(As json, final String screenName) throws TwitterException {
    return fetchUser(json, screenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchUsers()
   */
  public List<String> searchUsers(As json, final String query, final int page) throws TwitterException {
    return getJSONList(searchUsers(query, page));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributees()
   */
  public <T> List<String> fetchContributees(As json, final T ident) throws TwitterException {
    return getJSONList(fetchContributees(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  public List<String> getContributees(As json, final long userId) throws TwitterException {
    return fetchContributees(json, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  public List<String> getContributees(As json, final String screenName) throws TwitterException {
    return fetchContributees(json, screenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributors()
   */
  public <T> List<String> fetchContributors(As json, final T ident) throws TwitterException {
    return getJSONList(fetchContributees(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  public List<String> getContributors(As json, final long userId) throws TwitterException {
    return fetchContributors(json, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  public List<String> getContributors(As json, final String screenName) throws TwitterException {
    return fetchContributors(json, screenName);
  }

  /*
   * FavoritesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFavorites()
   */
  public <T> List<String> fetchFavorites(As json, final T ident, final Paging paging) throws TwitterException {
    return getJSONList(fetchFavorites(ident, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final long userId) throws TwitterException {
    return fetchFavorites(json, userId, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final long userId, final Paging paging) throws TwitterException {
    return fetchFavorites(json, userId, paging);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final String screenName) throws TwitterException {
    return fetchFavorites(json, screenName, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final String screenName, final Paging paging) throws TwitterException {
    return fetchFavorites(json, screenName, paging);
  }

  /*
   * ListsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserLists()
   */
  public <T> List<String> fetchUserLists(As json, final T ident) throws TwitterException {
    return getJSONList(fetchUserLists(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  public List<String> getUserLists(As json, final String listOwnerScreenName) throws TwitterException {
    return fetchUserLists(json, listOwnerScreenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  public List<String> getUserLists(As json, final long listOwnerUserId) throws TwitterException {
    return fetchUserLists(json, listOwnerUserId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatuses(As json, final long listId, final Paging paging) throws TwitterException {
    return getJSONList(getUserListStatuses(listId, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListStatuses()
   */
  public <T> List<String> fetchUserListStatuses(As json, final T ident, final String slug, final Paging paging) throws TwitterException {
    return getJSONList(fetchUserListStatuses(ident, slug, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatuses(As json, final long ownerId, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(json, ownerId, slug, paging);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatuses(As json, final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(json, ownerScreenName, slug, paging);
  }


  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMemberships()
   */
  public <T> List<String> fetchUserListMemberships(As json, final T ident, final long cursor) throws TwitterException {
    return getJSONList(fetchUserListMemberships(ident, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  public List<String> getUserListMemberships(As json, final long listMemberId, final long cursor) throws TwitterException {
    return fetchUserListMemberships(json, listMemberId, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  public List<String> getUserListMemberships(As json, final String listMemberScreenName, final long cursor) throws TwitterException {
    return fetchUserListMemberships(json, listMemberScreenName, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribers(As json, final long listId, final long cursor) throws TwitterException {
    return getJSONList(getUserListSubscribers(listId, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscribers()
   */
  public <T> List<String> fetchUserListSubscribers(As json, final T ident, final String slug, final long cursor) throws TwitterException {
    return getJSONList(fetchUserListSubscribers(ident, slug, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException {
    return fetchUserListSubscribers(json, ownerId, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return fetchUserListSubscribers(json, ownerScreenName, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscription()
   */
  public <T> String fetchUserListSubscription(As json, final T ident, final String slug, final long userId) throws TwitterException {
    return getJSON(fetchUserListSubscription(ident, slug, userId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  public String showUserListSubscription(As json, final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(json, ownerId, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  public String showUserListSubscription(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(json, ownerScreenName, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembership()
   */
  public <T> String fetchUserListMembership(As json, final T ident, final String slug, final long userId) throws TwitterException {
    return getJSON(fetchUserListMembership(ident, slug, userId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  public String showUserListMembership(As json, final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(json, ownerId, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  public String showUserListMembership(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(json, ownerScreenName, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembers()
   */
  public <T> List<String> fetchUserListMembers(As json, final T ident, final String slug, final long cursor) throws TwitterException {
    return getJSONList(fetchUserListMembers(ident, slug, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  public List<String> getUserListMembers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException {
    return fetchUserListMembers(json, ownerId, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  public List<String> getUserListMembers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return fetchUserListMembers(json, ownerScreenName, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserList()
   */
  public <T> String fetchUserList(As json, final T ident, final String slug) throws TwitterException {
    return getJSON(fetchUserList(ident, slug));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  public String showUserList(As json, final long ownerId, final String slug) throws TwitterException {
    return fetchUserList(json, ownerId, slug);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  public String showUserList(As json, final String ownerScreenName, final String slug) throws TwitterException {
    return fetchUserList(json, ownerScreenName, slug);
  }

  /*
   * TODO: Twitter4J Missing getUserListSubscriptions(long userId, cursor...)
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#()
   */
  public <T> List<String> fetchUserListSubscriptions(As json, final T ident, final long cursor) throws TwitterException {
    return getJSONList(fetchUserListSubscriptions(ident, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscriptions()
   */
  public List<String> getUserListSubscriptions(As json, final String listOwnerScreenName, final long cursor) throws TwitterException {
    return fetchUserListSubscriptions(json, listOwnerScreenName, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListsOwnerships()
   */
  public <T> List<String> fetchUserListsOwnerships(As json, final T ident, final int count, final long cursor) throws TwitterException {
    return getJSONList(fetchUserListsOwnerships(ident, count, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  public List<String> getUserListsOwnerships(As json, final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(json, listOwnerScreenName, count, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  public List<String> getUserListsOwnerships(As json, final long listOwnerId, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(json, listOwnerId, count, cursor);
  }

  /*
   * PlacesGeoResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getGeoDetails()
   */
  public String getGeoDetails(As json, final String placeId) throws TwitterException {
    return getJSON(getGeoDetails(placeId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#reverseGeoCode()
   */
  public List<String> reverseGeoCode(As json, final GeoQuery query) throws TwitterException {
    return getJSONList(reverseGeoCode(query));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchPlaces()
   */
  public List<String> searchPlaces(As json, final GeoQuery query) throws TwitterException {
    return getJSONList(searchPlaces(query));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getSimilarPlaces()
   */
  public List<String>
      getSimilarPlaces(As json, final GeoLocation location, final String name, final String containedWithin, final String streetAddress) throws TwitterException {
    return getJSONList(getSimilarPlaces(location, name, containedWithin, streetAddress));
  }

  /*
   * TrendsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getPlaceTrends(int)
   */
  public String getPlaceTrends(As json, final int woeid) throws TwitterException {
    return getJSON(getPlaceTrends(woeid));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getAvailableTrends()
   */
  public String getAvailableTrends(As json) throws TwitterException {
    return getJSON(getAvailableTrends());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getClosestTrends(GeoLocation)
   */
  public String getClosestTrends(As json, final GeoLocation location) throws TwitterException {
    return getJSON(getClosestTrends(location));
  }

  /*
   * HelpResources
   */

  //

}
