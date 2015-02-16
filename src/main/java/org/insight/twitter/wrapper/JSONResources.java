package org.insight.twitter.wrapper;

import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.TwitterException;

/*
 * Wrap TwitterResources methods to access original JSON from Twitter.
 */
public interface JSONResources {

  public enum As {
    JSON, POJO;
  };

  /*
   * TimelinesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserTimeline(As, T, Paging)
   */
  public <T> List<String> fetchUserTimeline(As json, final T ident, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String)
   */
  public List<String> getUserTimeline(As json, final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String, Paging)
   */
  public List<String> getUserTimeline(As json, final String screenName, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long)
   */
  public List<String> getUserTimeline(As json, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long, Paging)
   */
  public List<String> getUserTimeline(As json, final long userId, final Paging paging) throws TwitterException;

  /*
   * TweetsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweets()
   */
  public List<String> getRetweets(As json, final long statusId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  public String getRetweeterIds(As json, final long statusId, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  public String getRetweeterIds(As json, final long statusId, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showStatus(long)
   */
  public String showStatus(As json, final long id) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookup(long[])
   */
  public List<String> lookup(As json, final long[] ids) throws TwitterException;

  /*
   * SearchResource
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#search()
   */
  public String search(As json, final Query query) throws TwitterException;

  /*
   * FriendsFollowersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsIDs()
   */
  public <T> String fetchFriendsIDs(As json, final T ident, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final long userId, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final String screenName, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFollowersIDs()
   */
  public <T> String fetchFollowersIDs(As json, final T ident, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final long userId, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final String screenName, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendship()
   */
  public <T> String fetchFriendship(As json, final T sourceIdent, final T targetIdent) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  public String showFriendship(As json, final long sourceId, final long targetId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  public String showFriendship(As json, final String sourceScreenName, final String targetScreenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsList()
   */
  public <T> List<String> fetchFriendsList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final long userId, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final String screenName, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public <T> List<String> fetchFollowersList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final long userId, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final String screenName, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  /*
   * UsersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchLookupUsers()
   */
  public <T> List<String> fetchLookupUsers(As json, final T idents) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  public List<String> lookupUsers(As json, final long[] ids) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  public List<String> lookupUsers(As json, final String[] screenNames) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUser()
   */
  public <T> String fetchUser(As json, final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  public String showUser(As json, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  public String showUser(As json, final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchUsers()
   */
  public List<String> searchUsers(As json, final String query, final int page) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributees()
   */
  public <T> List<String> fetchContributees(As json, final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  public List<String> getContributees(As json, final long userId) throws TwitterException;


  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  public List<String> getContributees(As json, final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributors()
   */
  public <T> List<String> fetchContributors(As json, final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  public List<String> getContributors(As json, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  public List<String> getContributors(As json, final String screenName) throws TwitterException;

  /*
   * FavoritesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFavorites()
   */
  public <T> List<String> fetchFavorites(As json, final T ident, final Paging paging) throws TwitterException;


  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final long userId, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavorites(As json, final String screenName, final Paging paging) throws TwitterException;

  /*
   * ListsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserLists()
   */
  public <T> List<String> fetchUserLists(As json, final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  public List<String> getUserLists(As json, final String listOwnerScreenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  public List<String> getUserLists(As json, final long listOwnerUserId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatuses(As json, final long listId, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListStatuses()
   */
  public <T> List<String> fetchUserListStatuses(As json, final T ident, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatuses(As json, final long ownerId, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatuses(As json, final String ownerScreenName, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMemberships()
   */
  public <T> List<String> fetchUserListMemberships(As json, final T ident, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  public List<String> getUserListMemberships(As json, final long listMemberId, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  public List<String> getUserListMemberships(As json, final String listMemberScreenName, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribers(As json, final long listId, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscribers()
   */
  public <T> List<String> fetchUserListSubscribers(As json, final T ident, final String slug, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscription()
   */
  public <T> String fetchUserListSubscription(As json, final T ident, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  public String showUserListSubscription(As json, final long ownerId, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  public String showUserListSubscription(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembership()
   */
  public <T> String fetchUserListMembership(As json, final T ident, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  public String showUserListMembership(As json, final long ownerId, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  public String showUserListMembership(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembers()
   */
  public <T> List<String> fetchUserListMembers(As json, final T ident, final String slug, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  public List<String> getUserListMembers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  public List<String> getUserListMembers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserList()
   */
  public <T> String fetchUserList(As json, final T ident, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  public String showUserList(As json, final long ownerId, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  public String showUserList(As json, final String ownerScreenName, final String slug) throws TwitterException;

  /*
   * TODO: Twitter4J Missing getUserListSubscriptions(long userId, cursor...)
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#()
   */
  public <T> List<String> fetchUserListSubscriptions(As json, final T ident, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscriptions()
   */
  public List<String> getUserListSubscriptions(As json, final String listOwnerScreenName, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListsOwnerships()
   */
  public <T> List<String> fetchUserListsOwnerships(As json, final T ident, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  public List<String> getUserListsOwnerships(As json, final String listOwnerScreenName, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  public List<String> getUserListsOwnerships(As json, final long listOwnerId, final int count, final long cursor) throws TwitterException;

  /*
   * PlacesGeoResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getGeoDetails()
   */
  public String getGeoDetails(As json, final String placeId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#reverseGeoCode()
   */
  public List<String> reverseGeoCode(As json, final GeoQuery query) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchPlaces()
   */
  public List<String> searchPlaces(As json, final GeoQuery query) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getSimilarPlaces()
   */
  public List<String> getSimilarPlaces(As json, final GeoLocation location, final String name, final String containedWithin, final String streetAddress)
      throws TwitterException;

  /*
   * TrendsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getPlaceTrends(int)
   */
  public String getPlaceTrends(As json, final int woeid) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getAvailableTrends()
   */
  public String getAvailableTrends(As json) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getClosestTrends(GeoLocation)
   */
  public String getClosestTrends(As json, final GeoLocation location) throws TwitterException;

}
