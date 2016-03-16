package org.insight.twitter.wrapper;

import java.util.List;
import java.util.Map;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.TwitterException;

/*
 * Wrap TwitterResources methods to access original JSON from Twitter.
 */
public interface JSONResources {

  /*
   * CursorResources
   */

  /*
   * TimelinesResources
   */
  <T> List<String> getUserTimelineJSON(T ident, Paging paging) throws TwitterException;

  <T> List<String> getUserListStatusesJSON(T ident, String slug, Paging paging) throws TwitterException;

  List<String> getUserListStatusesJSON(long listId, Paging paging) throws TwitterException;

  // Use Bulk Request

  /*
   * TweetsResources
   */

  List<String> getRetweetsJSON(long statusId) throws TwitterException;

  String getRetweeterIdsJSON(long statusId, int count, long cursor) throws TwitterException;

  String showStatusJSON(long id) throws TwitterException;

  Map<Long, String> lookupJSON(long... ids) throws TwitterException;

  /*
   * SearchResource
   */

  String searchJSON(Query query) throws TwitterException;

  /*
   * FriendsFollowersResources
   */

  <T> String getFollowersIDsJSON(T ident, long cursor, int count) throws TwitterException;

  <T> String getFriendsIDsJSON(T ident, long cursor, int count) throws TwitterException;

  <T> String getFriendshipJSON(T sourceIdent, T targetIdent) throws TwitterException;

  String showFriendshipJSON(long sourceId, long targetId) throws TwitterException;

  String showFriendshipJSON(String sourceScreenName, String targetScreenName) throws TwitterException;

  <T> String getFriendsListJSON(T ident, long cursor, int count, boolean skipStatus, boolean includeUserEntities) throws TwitterException;

  <T> String getFollowersListJSON(T ident, long cursor, int count, boolean skipStatus, boolean includeUserEntities) throws TwitterException;

  /*
   * UsersResources
   */

  List<String> searchUsersJSON(String query, int page) throws TwitterException;

  <T> List<String> lookupUsersJSON(T idents) throws TwitterException;

  <T> String showUserJSON(T ident) throws TwitterException;

  //<T> List<String> getContributorsJSON(T ident) throws TwitterException;

  //<T> List<String> getContributeesJSON(T ident) throws TwitterException;

  /*
   * FavoritesResources
   */

  <T> List<String> getFavoritesJSON(T ident, Paging paging) throws TwitterException;

  /*
   * ListsResources
   */

  <T> List<String> getUserListsJSON(T ident, boolean reverse) throws TwitterException;

  List<String> getUserListsJSON(String listOwnerScreenName, boolean reverse) throws TwitterException;

  List<String> getUserListsJSON(long listOwnerUserId, boolean reverse) throws TwitterException;

  String showUserListSubscriptionJSON(long listId, long userId) throws TwitterException;

  <T> String getUserListSubscriptionJSON(T ident, String slug, long userId) throws TwitterException;

  String showUserListSubscriptionJSON(long ownerId, String slug, long userId) throws TwitterException;

  String showUserListSubscriptionJSON(String ownerScreenName, String slug, long userId) throws TwitterException;

  String showUserListMembershipJSON(long listId, long userId) throws TwitterException;

  <T> String getUserListMembershipsJSON(T ident, int count, long cursor, boolean filterToOwnedLists) throws TwitterException;

  <T> String getUserListMembershipJSON(T ident, String slug, long userId) throws TwitterException;

  String showUserListMembershipJSON(String ownerScreenName, String slug, long userId) throws TwitterException;

  String showUserListMembershipJSON(long ownerId, String slug, long userId) throws TwitterException;

  <T> String getUserListsOwnershipsJSON(T ident, int count, long cursor) throws TwitterException;

  <T> String getUserListSubscriptionsJSON(T ident, int count, long cursor) throws TwitterException;

  <T> String getUserListSubscribersJSON(T ident, String slug, int count, long cursor, boolean skipStatus) throws TwitterException;

  String getUserListSubscribersJSON(long listId, int count, long cursor, boolean skipStatus) throws TwitterException;

  String showUserListJSON(long listId) throws TwitterException;

  <T> String showUserListJSON(T ident, String slug) throws TwitterException;

  <T> String getUserListMembersJSON(T ident, String slug, int count, long cursor, boolean skipStatus) throws TwitterException;

  String getUserListMembersJSON(long listId, int count, long cursor, boolean skipStatus) throws TwitterException;

  /*
   * PlacesGeoResources
   */

  List<String> searchPlacesJSON(GeoQuery query) throws TwitterException;

  List<String> getSimilarPlacesJSON(GeoLocation location, String name, String containedWithin, String streetAddress) throws TwitterException;

  List<String> reverseGeoCodeJSON(GeoQuery query) throws TwitterException;

  String getGeoDetailsJSON(String placeId) throws TwitterException;

  /*
   * TrendsResources
   */

  String getPlaceTrendsJSON(int woeid) throws TwitterException;

  List<String> getAvailableTrendsJSON() throws TwitterException;

  List<String> getClosestTrendsJSON(GeoLocation location) throws TwitterException;

}
