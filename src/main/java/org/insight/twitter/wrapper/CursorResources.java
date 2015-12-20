package org.insight.twitter.wrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import twitter4j.Query;
import twitter4j.TwitterException;

/*
 * Bulk Requests - Hide Paging, cursors etc.
 */
public interface CursorResources {

  /*
   * Timelines
   */

  <T> List<String> getBulkUserTimeline(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException;

  <T> List<String> getBulkUserTimeline(T ident) throws TwitterException;

  /*
   * Favorites
   */

  <T> List<String> getBulkFavorites(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException;

  <T> List<String> getBulkFavorites(T ident) throws TwitterException;

  /*
   * Tweets
   */

  List<Long> getBulkRetweeterIds(long statusId, int maxElements) throws TwitterException;

  List<Long> getBulkRetweeterIds(long statusId) throws TwitterException;

  Map<Long, String> getBulkTweetLookupMap(Collection<Long> ids) throws TwitterException;

  List<String> getBulkTweetLookup(final Collection<Long> ids) throws TwitterException;

  /*
   * Users
   */

  <T> List<String> getBulkLookupUsers(final Collection<T> idents) throws TwitterException;

  /*
   * FriendsFollowers
   */

  <T> List<Long> getBulkFriendsIDs(T ident, int maxElements) throws TwitterException;

  <T> List<Long> getBulkFriendsIDs(T ident) throws TwitterException;

  <T> List<Long> getBulkFollowersIDs(T ident, int maxElements) throws TwitterException;

  <T> List<Long> getBulkFollowersIDs(T ident) throws TwitterException;

  <T> List<String> getBulkFriendsList(T ident, int maxElements, boolean skipStatus, boolean includeUserEntities) throws TwitterException;

  <T> List<String> getBulkFriendsList(T ident) throws TwitterException;

  <T> List<String> getBulkFollowersList(T ident, int maxElements, boolean skipStatus, boolean includeUserEntities) throws TwitterException;

  <T> List<String> getBulkFollowersList(T ident) throws TwitterException;

  /*
   * Lists
   */

  <T> List<String> getBulkUserListStatuses(T ident, String slug, long initSinceId, long initMaxId, int maxElements) throws TwitterException;

  <T> List<String> getBulkUserListStatuses(T ident, String slug) throws TwitterException;

  <T> List<String> getBulkUserListMemberships(T ident, int maxElements) throws TwitterException;

  <T> List<String> getBulkUserListMemberships(T ident) throws TwitterException;

  <T> List<String> getBulkUserListSubscribers(T ident, String slug, int maxElements) throws TwitterException;

  <T> List<String> getBulkUserListSubscribers(T ident, String slug) throws TwitterException;

  <T> List<String> getBulkUserListMembers(long listId, int maxElements) throws TwitterException;

  <T> List<String> getBulkUserListMembers(long listId) throws TwitterException;

  <T> List<String> getBulkUserListSubscriptions(T ident, int maxElements) throws TwitterException;

  <T> List<String> getBulkUserListSubscriptions(T ident) throws TwitterException;

  <T> List<String> getBulkUserListsOwnerships(T ident, int maxElements) throws TwitterException;

  <T> List<String> getBulkUserListsOwnerships(T ident) throws TwitterException;

  /*
   * Search
   */

  <T> List<String> getBulkSearchUsers(String query, int maxElements) throws TwitterException;

  <T> List<String> getBulkSearchUsers(String query) throws TwitterException;

  <T> List<String> getBulkSearchResults(String query, int maxElements) throws TwitterException;

  <T> List<String> getBulkSearchResults(String query) throws TwitterException;

  <T> List<String> getBulkSearchResults(Query query, int maxElements) throws TwitterException;

  <T> List<String> getBulkSearchResults(Query query) throws TwitterException;

}
