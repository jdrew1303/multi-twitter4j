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
  public <T> List<String> fetchUserTimelineJSON(final T ident, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String, Paging)
   */
  public List<String> getUserTimelineJSON(final String screenName, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long, Paging)
   */
  public List<String> getUserTimelineJSON(final long userId, final Paging paging) throws TwitterException;

  /*
   * TweetsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweets()
   */
  public List<String> getRetweetsJSON(final long statusId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  public String getRetweeterIdsJSON(final long statusId, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showStatus(long)
   */
  public String showStatusJSON(final long id) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookup(long[])
   */
  public List<String> lookupJSON(final long[] ids) throws TwitterException;

  /*
   * SearchResource
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#search()
   */
  public String searchJSON(final Query query) throws TwitterException;

  /*
   * FriendsFollowersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsIDs()
   */
  public <T> String fetchFriendsIDsJSON(final T ident, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDsJSON(final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  public String getFriendsIDsJSON(final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFollowersIDs()
   */
  public <T> String fetchFollowersIDsJSON(final T ident, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDsJSON(final long userId, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  public String getFollowersIDsJSON(final String screenName, final long cursor, final int count) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendship()
   */
  public <T> String fetchFriendshipJSON(final T sourceIdent, final T targetIdent) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  public String showFriendshipJSON(final long sourceId, final long targetId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  public String showFriendshipJSON(final String sourceScreenName, final String targetScreenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsList()
   */
  public <T> List<String> fetchFriendsListJSON(final T ident, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsListJSON(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  public List<String> getFriendsListJSON(final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public <T> List<String> fetchFollowersListJSON(final T ident, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersListJSON(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  public List<String> getFollowersListJSON(final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  /*
   * UsersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchLookupUsers()
   */
  public <T> List<String> fetchLookupUsersJSON(final T idents) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  public List<String> lookupUsersJSON(final long[] ids) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  public List<String> lookupUsersJSON(final String[] screenNames) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUser()
   */
  public <T> String fetchUserJSON(final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  public String showUserJSON(final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  public String showUserJSON(final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchUsers()
   */
  public List<String> searchUsersJSON(final String query, final int page) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributees()
   */
  public <T> List<String> fetchContributeesJSON(final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  public List<String> getContributeesJSON(final long userId) throws TwitterException;


  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  public List<String> getContributeesJSON(final String screenName) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributors()
   */
  public <T> List<String> fetchContributorsJSON(final T ident) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  public List<String> getContributorsJSON(final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  public List<String> getContributorsJSON(final String screenName) throws TwitterException;

  /*
   * FavoritesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFavorites()
   */
  public <T> List<String> fetchFavoritesJSON(final T ident, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavoritesJSON(final long userId, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  public List<String> getFavoritesJSON(final String screenName, final Paging paging) throws TwitterException;

  /*
   * ListsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserLists()
   */
  public <T> List<String> fetchUserListsJSON(final T ident, boolean reverse) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  public List<String> getUserListsJSON(final String listOwnerScreenName, boolean reverse) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  public List<String> getUserListsJSON(final long listOwnerUserId, boolean reverse) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatusesJSON(final long listId, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListStatuses()
   */
  public <T> List<String> fetchUserListStatusesJSON(final T ident, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatusesJSON(final long ownerId, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  public List<String> getUserListStatusesJSON(final String ownerScreenName, final String slug, final Paging paging) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMemberships()
   */
  public <T> List<String> fetchUserListMembershipsJSON(final T ident, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  public List<String> getUserListMembershipsJSON(final long listMemberId, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  public List<String> getUserListMembershipsJSON(final String listMemberScreenName, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribersJSON(final long listId, final int count, final long cursor, boolean skipStatus) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscribers()
   */
  public <T> List<String> fetchUserListSubscribersJSON(final T ident, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribersJSON(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  public List<String> getUserListSubscribersJSON(final String ownerScreenName, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscription()
   */
  public <T> String fetchUserListSubscriptionJSON(final T ident, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  public String showUserListSubscriptionJSON(final long ownerId, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  public String showUserListSubscriptionJSON(final String ownerScreenName, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembership()
   */
  public <T> String fetchUserListMembershipJSON(final T ident, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  public String showUserListMembershipJSON(final long ownerId, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  public String showUserListMembershipJSON(final String ownerScreenName, final String slug, final long userId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembers()
   */
  public <T> List<String> fetchUserListMembersJSON(final T ident, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  public List<String> getUserListMembersJSON(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  public List<String> getUserListMembersJSON(final String ownerScreenName, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserList()
   */
  public <T> String fetchUserListJSON(final T ident, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  public String showUserListJSON(final long ownerId, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  public String showUserListJSON(final String ownerScreenName, final String slug) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#()
   */
  public <T> List<String> fetchUserListSubscriptionsJSON(final T ident, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscriptions()
   */
  public List<String> getUserListSubscriptionsJSON(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscriptions()
   */
  public List<String> getUserListSubscriptionsJSON(final long listOwnerId, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListsOwnerships()
   */
  public <T> List<String> fetchUserListsOwnershipsJSON(final T ident, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  public List<String> getUserListsOwnershipsJSON(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  public List<String> getUserListsOwnershipsJSON(final long listOwnerId, final int count, final long cursor) throws TwitterException;

  /*
   * PlacesGeoResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getGeoDetails(String)
   */
  public String getGeoDetailsJSON(final String placeId) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#reverseGeoCode(GeoQuery)
   */
  public List<String> reverseGeoCodeJSON(final GeoQuery query) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchPlaces(GeoQuery)
   */
  public List<String> searchPlacesJSON(final GeoQuery query) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getSimilarPlaces(GeoLocation, String, String, String)
   */
  public List<String> getSimilarPlacesJSON(final GeoLocation location, final String name, final String containedWithin, final String streetAddress)
      throws TwitterException;

  /*
   * TrendsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getPlaceTrends(int)
   */
  public String getPlaceTrendsJSON(final int woeid) throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getAvailableTrends()
   */
  public String getAvailableTrendsJSON() throws TwitterException;

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getClosestTrends(GeoLocation)
   */
  public String getClosestTrendsJSON(final GeoLocation location) throws TwitterException;

}
