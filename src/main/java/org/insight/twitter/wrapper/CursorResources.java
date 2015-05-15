package org.insight.twitter.wrapper;

import java.util.List;

import org.insight.twitter.wrapper.JSONResources.As;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;

/*
 * Bulk Requests - Hide Paging, cursors etc.
 */
public interface CursorResources {

  /*
   * Timelines
   */

  <T, K> List<K> getBulkUserTimeline(As type, final T ident, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException;

  <T, K> List<K> getBulkUserTimeline(As type, final T ident) throws TwitterException;

  <T> List<Status> getBulkUserTimeline(final T ident, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException;

  <T> List<Status> getBulkUserTimeline(final T ident) throws TwitterException;

  /*
   * Favorites
   */

  <T, K> List<K> getBulkFavorites(As type, final T ident, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException;

  <T, K> List<K> getBulkFavorites(As type, final T ident) throws TwitterException;

  <T> List<Status> getBulkFavorites(final T ident, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException;

  <T> List<Status> getBulkFavorites(final T ident) throws TwitterException;

  /*
   * Tweets
   */

  List<Long> getBulkRetweeterIds(final long statusId, final int maxElements) throws TwitterException;
  
  List<Long> getBulkRetweeterIds(final long statusId) throws TwitterException;

  /*
   * FriendsFollowers
   */

  <T> List<Long> getBulkFriendsIDs(final T ident, final int maxElements) throws TwitterException;

  <T> List<Long> getBulkFriendsIDs(final T ident) throws TwitterException;
 

  <T> List<Long> getBulkFollowersIDs(final T ident, final int maxElements) throws TwitterException;
  
  <T> List<Long> getBulkFollowersIDs(final T ident) throws TwitterException;


  <T, K> List<K> getBulkFriendsList(As type, final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException;

  <T, K> List<K> getBulkFriendsList(As type, final T ident) throws TwitterException;

  <T> List<User> getBulkFriendsList(final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException;

  <T> List<User> getBulkFriendsList(final T ident) throws TwitterException;


  <T, K> List<K> getBulkFollowersList(As type, final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException;

  <T, K> List<K> getBulkFollowersList(As type, final T ident) throws TwitterException;

  <T> List<User> getBulkFollowersList(final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException;

  <T> List<User> getBulkFollowersList(final T ident) throws TwitterException;

  /*
   * Lists
   */

  <T, K> List<K> getBulkUserListStatuses(As type, final T ident, final String slug, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException;

  <T, K> List<K> getBulkUserListStatuses(As type, final T ident, final String slug) throws TwitterException;

  <T> List<Status> getBulkUserListStatuses(final T ident, final String slug, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException;

  <T> List<Status> getBulkUserListStatuses(final T ident, final String slug) throws TwitterException;


  <T, K> List<K> getBulkUserListMemberships(As type, final T ident, int maxElements) throws TwitterException;

  <T, K> List<K> getBulkUserListMemberships(As type, final T ident) throws TwitterException;

  <T> List<UserList> getBulkUserListMemberships(final T ident, int maxElements) throws TwitterException;

  <T> List<UserList> getBulkUserListMemberships(final T ident) throws TwitterException;


  <T, K> List<K> getBulkUserListSubscribers(As type, final T ident, final String slug, int maxElements) throws TwitterException;

  <T, K> List<K> getBulkUserListSubscribers(As type, final T ident, final String slug) throws TwitterException;

  <T> List<User> getBulkUserListSubscribers(final T ident, final String slug, int maxElements) throws TwitterException;

  <T> List<User> getBulkUserListSubscribers(final T ident, final String slug) throws TwitterException;


  <K> List<K> getBulkUserListMembers(As type, final long listId, final int maxElements) throws TwitterException;

  <K> List<K> getBulkUserListMembers(As type, final long listId) throws TwitterException;

  List<User> getBulkUserListMembers(final long listId, final int maxElements) throws TwitterException;

  List<User> getBulkUserListMembers(final long listId) throws TwitterException;


  <T, K> List<K> getBulkUserListSubscriptions(As type, final T ident, int maxElements) throws TwitterException;

  <T, K> List<K> getBulkUserListSubscriptions(As type, final T ident) throws TwitterException;

  <T> List<UserList> getBulkUserListSubscriptions(final T ident, int maxElements) throws TwitterException;

  <T> List<UserList> getBulkUserListSubscriptions(final T ident) throws TwitterException;


  <T, K> List<K> getBulkUserListsOwnerships(As type, final T ident, int maxElements) throws TwitterException;

  <T, K> List<K> getBulkUserListsOwnerships(As type, final T ident) throws TwitterException;

  <T> List<UserList> getBulkUserListsOwnerships(final T ident, int maxElements) throws TwitterException;

  <T> List<UserList> getBulkUserListsOwnerships(final T ident) throws TwitterException;

  /*
   * Search
   */

  <K> List<K> getBulkSearchUsers(As type, final String query, int maxElements) throws TwitterException;

  <K> List<K> getBulkSearchUsers(As type, final String query) throws TwitterException;

  List<User> getBulkSearchUsers(final String query, int maxElements) throws TwitterException;

  List<User> getBulkSearchUsers(final String query) throws TwitterException;


  <K> List<K> getBulkSearchResults(final As type, final Query query, int maxElements) throws TwitterException;

  <K> List<K> getBulkSearchResults(final As type, final Query query) throws TwitterException;

  List<User> getBulkSearchResults(final Query query, int maxElements) throws TwitterException;

  List<User> getBulkSearchResults(final Query query) throws TwitterException;


}
