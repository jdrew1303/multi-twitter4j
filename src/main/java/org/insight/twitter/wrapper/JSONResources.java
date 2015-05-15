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

  enum As {
    JSON, POJO
  }

  /*
   * TimelinesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserTimeline(As, T, Paging)
   */
  <T> List<String> fetchUserTimelineJSON(final T ident, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String, Paging)
   */
  List<String> getUserTimelineJSON(final String screenName, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long, Paging)
   */
  List<String> getUserTimelineJSON(final long userId, final Paging paging) throws TwitterException;

  /*
   * TweetsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweets()
   */
  List<String> getRetweetsJSON(final long statusId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  String getRetweeterIdsJSON(final long statusId, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showStatus(long)
   */
  String showStatusJSON(final long id) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookup(long[])
   */
  List<String> lookupJSON(final long[] ids) throws TwitterException;

  /*
   * SearchResource
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#search()
   */
  String searchJSON(final Query query) throws TwitterException;

  /*
   * FriendsFollowersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsIDs()
   */
  <T> String fetchFriendsIDsJSON(final T ident, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  String getFriendsIDsJSON(final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  String getFriendsIDsJSON(final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFollowersIDs()
   */
  <T> String fetchFollowersIDsJSON(final T ident, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  String getFollowersIDsJSON(final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  String getFollowersIDsJSON(final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendship()
   */
  <T> String fetchFriendshipJSON(final T sourceIdent, final T targetIdent) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  String showFriendshipJSON(final long sourceId, final long targetId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  String showFriendshipJSON(final String sourceScreenName, final String targetScreenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsList()
   */
  <T> List<String> fetchFriendsListJSON(final T ident, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  List<String> getFriendsListJSON(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  List<String> getFriendsListJSON(final String screenName, final long cursor, final int count, final boolean skipStatus,
                                  final boolean includeUserEntities) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  <T> List<String> fetchFollowersListJSON(final T ident, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  List<String> getFollowersListJSON(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  List<String> getFollowersListJSON(final String screenName, final long cursor, final int count, final boolean skipStatus,
                                    final boolean includeUserEntities) throws TwitterException;

  /*
   * UsersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchLookupUsers()
   */
  <T> List<String> fetchLookupUsersJSON(final T idents) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  List<String> lookupUsersJSON(final long[] ids) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  List<String> lookupUsersJSON(final String[] screenNames) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUser()
   */
  <T> String fetchUserJSON(final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  String showUserJSON(final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  String showUserJSON(final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchUsers()
   */
  List<String> searchUsersJSON(final String query, final int page) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributees()
   */
  <T> List<String> fetchContributeesJSON(final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  List<String> getContributeesJSON(final long userId) throws TwitterException;


  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  List<String> getContributeesJSON(final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributors()
   */
  <T> List<String> fetchContributorsJSON(final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  List<String> getContributorsJSON(final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  List<String> getContributorsJSON(final String screenName) throws TwitterException;

  /*
   * FavoritesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFavorites()
   */
  <T> List<String> fetchFavoritesJSON(final T ident, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  List<String> getFavoritesJSON(final long userId, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  List<String> getFavoritesJSON(final String screenName, final Paging paging) throws TwitterException;

  /*
   * ListsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserLists()
   */
  <T> List<String> fetchUserListsJSON(final T ident, boolean reverse) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  List<String> getUserListsJSON(final String listOwnerScreenName, boolean reverse) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  List<String> getUserListsJSON(final long listOwnerUserId, boolean reverse) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  List<String> getUserListStatusesJSON(final long listId, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListStatuses()
   */
  <T> List<String> fetchUserListStatusesJSON(final T ident, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  List<String> getUserListStatusesJSON(final long ownerId, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  List<String> getUserListStatusesJSON(final String ownerScreenName, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMemberships()
   */
  <T> List<String> fetchUserListMembershipsJSON(final T ident, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  List<String> getUserListMembershipsJSON(final long listMemberId, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  List<String> getUserListMembershipsJSON(final String listMemberScreenName, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  List<String> getUserListSubscribersJSON(final long listId, final int count, final long cursor, boolean skipStatus) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscribers()
   */
  <T> List<String> fetchUserListSubscribersJSON(final T ident, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  List<String> getUserListSubscribersJSON(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  List<String> getUserListSubscribersJSON(final String ownerScreenName, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscription()
   */
  <T> String fetchUserListSubscriptionJSON(final T ident, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  String showUserListSubscriptionJSON(final long ownerId, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  String showUserListSubscriptionJSON(final String ownerScreenName, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembership()
   */
  <T> String fetchUserListMembershipJSON(final T ident, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  String showUserListMembershipJSON(final long ownerId, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  String showUserListMembershipJSON(final String ownerScreenName, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembers()
   */
  <T> List<String> fetchUserListMembersJSON(final T ident, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  List<String> getUserListMembersJSON(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  List<String> getUserListMembersJSON(final String ownerScreenName, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserList()
   */
  <T> String fetchUserListJSON(final T ident, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  String showUserListJSON(final long ownerId, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  String showUserListJSON(final String ownerScreenName, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#()
   */
  <T> List<String> fetchUserListSubscriptionsJSON(final T ident, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscriptions()
   */
  List<String> getUserListSubscriptionsJSON(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscriptions()
   */
  List<String> getUserListSubscriptionsJSON(final long listOwnerId, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListsOwnerships()
   */
  <T> List<String> fetchUserListsOwnershipsJSON(final T ident, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  List<String> getUserListsOwnershipsJSON(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  List<String> getUserListsOwnershipsJSON(final long listOwnerId, final int count, final long cursor) throws TwitterException;

  /*
   * PlacesGeoResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getGeoDetails(String)
   */
  String getGeoDetailsJSON(final String placeId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#reverseGeoCode(GeoQuery)
   */
  List<String> reverseGeoCodeJSON(final GeoQuery query) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchPlaces(GeoQuery)
   */
  List<String> searchPlacesJSON(final GeoQuery query) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getSimilarPlaces(GeoLocation, String, String, String)
   */
  List<String> getSimilarPlacesJSON(final GeoLocation location, final String name, final String containedWithin, final String streetAddress)
      throws TwitterException;

  /*
   * TrendsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getPlaceTrends(int)
   */
  String getPlaceTrendsJSON(final int woeid) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getAvailableTrends()
   */
  String getAvailableTrendsJSON() throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getClosestTrends(GeoLocation)
   */
  String getClosestTrendsJSON(final GeoLocation location) throws TwitterException;

}
