package org.insight.twitter.wrapper;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.insight.twitter.util.TwitterObjects;

import twitter4j.AccountSettings;
import twitter4j.Friendship;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.IDs;
import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.api.FavoritesResources;
import twitter4j.api.FriendsFollowersResources;
import twitter4j.api.HelpResources;
import twitter4j.api.ListsResources;
import twitter4j.api.PlacesGeoResources;
import twitter4j.api.SearchResource;
import twitter4j.api.TimelinesResources;
import twitter4j.api.TrendsResources;
import twitter4j.api.TweetsResources;
import twitter4j.api.UsersResources;

/*
 * Selected Methods from twitter4J Twitter interface. Unsupported methods throw exceptions.
 *
 * Partially implemented: FavoritesResources, ListsResources, PlacesGeoResources, TrendsResources, Not Implemented: For Authenticating Users Only:
 * SpamReportingResource, SavedSearchesResources, SuggestedUsersResources, DirectMessagesResources, OAuthSupport, OAuth2Support, TwitterBase, Rate Limits Are
 * Handled internally: HelpResources
 */
public abstract class TwitterResources implements TimelinesResources, TweetsResources, SearchResource, FriendsFollowersResources, UsersResources,
    FavoritesResources, ListsResources, PlacesGeoResources, TrendsResources, HelpResources, CursorResources, JSONResources {

  private static final String UNSUPPORTED_METHOD = "This API call cannot be distributed between bots!";

  /*
   * Interfaces
   */

  public JSONResources json() {
    return this;
  }

  public CursorResources cursor() {
    return this;
  }

  public TimelinesResources timelines() {
    return this;
  }

  public TweetsResources tweets() {
    return this;
  }

  public SearchResource search() {
    return this;
  }

  public FriendsFollowersResources friendsFollowers() {
    return this;
  }

  public UsersResources users() {
    return this;
  }

  public FavoritesResources favorites() {
    return this;
  }

  public ListsResources list() {
    return this;
  }

  public PlacesGeoResources placesGeo() {
    return this;
  }

  public TrendsResources trends() {
    return this;
  }

  public HelpResources help() {
    return this;
  }

  /*
   * CursorResources / Bulk Request Methods:
   */

  /*
   * Bulk Timelines:
   */
  @Override
  public abstract <T, K> List<K> getBulkUserTimeline(As type, final T ident, final long initSinceId, final long initMaxId, final int maxElements)
      throws TwitterException;

  @Override
  public <T, K> List<K> getBulkUserTimeline(As type, T ident) throws TwitterException {
    return getBulkUserTimeline(type, ident, -1, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkUserTimeline(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return getBulkUserTimeline(As.POJO, ident, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<Status> getBulkUserTimeline(T ident) throws TwitterException {
    return getBulkUserTimeline(As.POJO, ident, -1, -1, -1);
  }

  // Bulk Favorites:

  @Override
  public abstract <T, K> List<K> getBulkFavorites(As type, final T ident, final long initSinceId, final long initMaxId, final int maxElements)
      throws TwitterException;

  @Override
  public <T, K> List<K> getBulkFavorites(As type, T ident) throws TwitterException {
    return getBulkFavorites(type, ident, -1, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkFavorites(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return getBulkFavorites(As.POJO, ident, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<Status> getBulkFavorites(T ident) throws TwitterException {
    return getBulkFavorites(As.POJO, ident, -1, -1, -1);
  }

  /*
   * Bulk Tweets
   */
  @Override
  public abstract List<Long> getBulkRetweeterIds(final long statusId, final int maxElements) throws TwitterException;

  @Override
  public List<Long> getBulkRetweeterIds(final long statusId) throws TwitterException {
    return getBulkRetweeterIds(statusId, -1);
  }

  /*
   * Bulk FriendsFollowers
   */
  @Override
  public abstract <T> List<Long> getBulkFriendsIDs(final T ident, final int maxElements) throws TwitterException;

  @Override
  public <T> List<Long> getBulkFriendsIDs(T ident) throws TwitterException {
    return getBulkFriendsIDs(ident, -1);
  }

  @Override
  public abstract <T> List<Long> getBulkFollowersIDs(final T ident, final int maxElements) throws TwitterException;

  @Override
  public <T> List<Long> getBulkFollowersIDs(T ident) throws TwitterException {
    return getBulkFollowersIDs(ident, -1);
  }

  @Override
  public abstract <T, K> List<K> getBulkFriendsList(As type, final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  @Override
  public <T, K> List<K> getBulkFriendsList(As type, final T ident) throws TwitterException {
    return getBulkFriendsList(type, ident, -1, false, true);
  }

  @Override
  public <T> List<User> getBulkFriendsList(final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return getBulkFriendsList(As.POJO, ident, maxElements, skipStatus, includeUserEntities);
  }

  @Override
  public <T> List<User> getBulkFriendsList(final T ident) throws TwitterException {
    return getBulkFriendsList(As.POJO, ident, -1, false, true);
  }

  @Override
  public abstract <T, K> List<K> getBulkFollowersList(As type, final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException;

  @Override
  public <T, K> List<K> getBulkFollowersList(As type, final T ident) throws TwitterException {
    return getBulkFollowersList(type, ident, -1, false, true);
  }

  @Override
  public <T> List<User> getBulkFollowersList(final T ident, final int maxElements, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return getBulkFollowersList(As.POJO, ident, maxElements, skipStatus, includeUserEntities);
  }

  @Override
  public <T> List<User> getBulkFollowersList(final T ident) throws TwitterException {
    return getBulkFollowersList(As.POJO, ident, -1, false, true);
  }


  // Bulk List Statuses:

  @Override
  public abstract <T, K> List<K> getBulkUserListStatuses(As type, final T ident, final String slug, final long initSinceId, final long initMaxId,
      final int maxElements) throws TwitterException;

  @Override
  public <T, K> List<K> getBulkUserListStatuses(As type, T ident, String slug) throws TwitterException {
    return getBulkUserListStatuses(type, ident, slug, -1, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkUserListStatuses(T ident, String slug, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return getBulkUserListStatuses(As.POJO, ident, slug, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<Status> getBulkUserListStatuses(T ident, String slug) throws TwitterException {
    return getBulkUserListStatuses(As.POJO, ident, slug, -1, -1, -1);
  }

  /*
   * Lists:
   */

  @Override
  public abstract <T, K> List<K> getBulkUserListMemberships(As type, final T ident, int maxElements) throws TwitterException;

  @Override
  public <T, K> List<K> getBulkUserListMemberships(As type, T ident) throws TwitterException {
    return getBulkUserListMemberships(type, ident, -1);
  }

  @Override
  public <T> List<UserList> getBulkUserListMemberships(T ident, int maxElements) throws TwitterException {
    return getBulkUserListMemberships(As.POJO, ident, maxElements);
  }

  @Override
  public <T> List<UserList> getBulkUserListMemberships(T ident) throws TwitterException {
    return getBulkUserListMemberships(As.POJO, ident, -1);
  }

  @Override
  public abstract <T, K> List<K> getBulkUserListSubscribers(As type, final T ident, final String slug, int maxElements) throws TwitterException;

  @Override
  public <T, K> List<K> getBulkUserListSubscribers(As type, T ident, String slug) throws TwitterException {
    return getBulkUserListSubscribers(type, ident, slug, -1);
  }

  @Override
  public <T> List<User> getBulkUserListSubscribers(T ident, String slug, int maxElements) throws TwitterException {
    return getBulkUserListSubscribers(As.POJO, ident, slug, maxElements);
  }

  @Override
  public <T> List<User> getBulkUserListSubscribers(T ident, String slug) throws TwitterException {
    return getBulkUserListSubscribers(As.POJO, ident, slug, -1);
  }

  @Override
  public abstract <K> List<K> getBulkUserListMembers(As type, final long listId, final int maxElements) throws TwitterException;

  @Override
  public <K> List<K> getBulkUserListMembers(As type, long listId) throws TwitterException {
    return getBulkUserListMembers(type, listId, -1);
  }

  @Override
  public List<User> getBulkUserListMembers(long listId, int maxElements) throws TwitterException {
    return getBulkUserListMembers(As.POJO, listId, maxElements);
  }

  @Override
  public List<User> getBulkUserListMembers(long listId) throws TwitterException {
    return getBulkUserListMembers(As.POJO, listId, -1);
  }

  @Override
  public abstract <T, K> List<K> getBulkUserListSubscriptions(As type, final T ident, int maxElements) throws TwitterException;

  @Override
  public <T, K> List<K> getBulkUserListSubscriptions(As type, T ident) throws TwitterException {
    return getBulkUserListSubscriptions(type, ident, -1);
  }

  @Override
  public <T> List<UserList> getBulkUserListSubscriptions(T ident, int maxElements) throws TwitterException {
    return getBulkUserListSubscriptions(As.POJO, ident, maxElements);
  }

  @Override
  public <T> List<UserList> getBulkUserListSubscriptions(T ident) throws TwitterException {
    return getBulkUserListSubscriptions(As.POJO, ident, -1);
  }

  @Override
  public abstract <T, K> List<K> getBulkUserListsOwnerships(As type, final T ident, int maxElements) throws TwitterException;

  @Override
  public <T, K> List<K> getBulkUserListsOwnerships(As type, T ident) throws TwitterException {
    return getBulkUserListsOwnerships(type, ident, -1);
  }

  @Override
  public <T> List<UserList> getBulkUserListsOwnerships(T ident, int maxElements) throws TwitterException {
    return getBulkUserListsOwnerships(As.POJO, ident, maxElements);
  }

  @Override
  public <T> List<UserList> getBulkUserListsOwnerships(T ident) throws TwitterException {
    return getBulkUserListsOwnerships(As.POJO, ident, -1);
  }

  /*
   * Search
   */

  @Override
  public abstract <K> List<K> getBulkSearchUsers(As type, final String query, int maxElements) throws TwitterException;

  @Override
  public <K> List<K> getBulkSearchUsers(As type, final String query) throws TwitterException {
    return getBulkSearchUsers(type, query, -1);
  }

  @Override
  public List<User> getBulkSearchUsers(final String query, int maxElements) throws TwitterException {
    return getBulkSearchUsers(As.POJO, query, maxElements);
  }

  @Override
  public List<User> getBulkSearchUsers(final String query) throws TwitterException {
    return getBulkSearchUsers(As.POJO, query, -1);
  }

  @Override
  public abstract <K> List<K> getBulkSearchResults(final As type, final Query query, int maxElements) throws TwitterException;

  @Override
  public <K> List<K> getBulkSearchResults(final As type, final Query query) throws TwitterException {
    return getBulkSearchResults(type, query, -1);
  }

  @Override
  public List<User> getBulkSearchResults(final Query query, int maxElements) throws TwitterException {
    return getBulkSearchResults(As.POJO, query, maxElements);
  }

  @Override
  public List<User> getBulkSearchResults(final Query query) throws TwitterException {
    return getBulkSearchResults(As.POJO, query, -1);
  }


  /*
   * TimelinesResources
   */

  public abstract <T> ResponseList<Status> fetchUserTimeline(final T ident, final Paging paging) throws TwitterException;

  @Override
  public <T> List<String> fetchUserTimelineJSON(final T ident, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserTimeline(ident, paging));
  }

  @Override
  public ResponseList<Status> getUserTimeline(final String screenName) throws TwitterException {
    return fetchUserTimeline(screenName, new Paging());
  }

  @Override
  public ResponseList<Status> getUserTimeline(final String screenName, final Paging paging) throws TwitterException {
    return fetchUserTimeline(screenName, paging);
  }

  @Override
  public List<String> getUserTimelineJSON(final String screenName, final Paging paging) throws TwitterException {
    return fetchUserTimelineJSON(screenName, paging);
  }

  @Override
  public ResponseList<Status> getUserTimeline(final long userId) throws TwitterException {
    return fetchUserTimeline(userId, new Paging());
  }

  @Override
  public ResponseList<Status> getUserTimeline(final long userId, final Paging paging) throws TwitterException {
    return fetchUserTimeline(userId, paging);
  }

  @Override
  public List<String> getUserTimelineJSON(final long userId, final Paging paging) throws TwitterException {
    return fetchUserTimelineJSON(userId, paging);
  }

  /*
   * Unsupported TimelinesResources:
   */

  @Override
  @Deprecated
  public ResponseList<Status> getMentionsTimeline() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getMentionsTimeline(final Paging paging) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getUserTimeline() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getUserTimeline(final Paging paging) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getHomeTimeline() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getHomeTimeline(final Paging paging) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getRetweetsOfMe(final Paging paging) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  /*
   * TweetsResources
   */

  @Override
  public abstract IDs getRetweeterIds(long statusId, int count, long cursor) throws TwitterException;

  @Override
  public IDs getRetweeterIds(final long statusId, final long cursor) throws TwitterException {
    return getRetweeterIds(statusId, 200, cursor);
  }

  @Override
  public String getRetweeterIdsJSON(final long statusId, final int count, final long cursor) throws TwitterException {
    return TwitterObjects.getJSON(getRetweeterIds(statusId, count, cursor));
  }

  @Override
  public abstract ResponseList<Status> getRetweets(long statusId) throws TwitterException;

  @Override
  public List<String> getRetweetsJSON(final long statusId) throws TwitterException {
    return TwitterObjects.getJSONList(getRetweets(statusId));
  }

  @Override
  public String showStatusJSON(final long id) throws TwitterException {
    return TwitterObjects.getJSON(showStatus(id));
  }

  @Override
  public List<String> lookupJSON(final long[] ids) throws TwitterException {
    return TwitterObjects.getJSONList(lookup(ids));
  }

  /*
   * Unsupported TweetsResources:
   */

  @Override
  @Deprecated
  public Status destroyStatus(final long statusId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status updateStatus(final String status) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status updateStatus(final StatusUpdate latestStatus) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status retweetStatus(final long statusId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public OEmbed getOEmbed(final OEmbedRequest req) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UploadedMedia uploadMedia(final File mediaFile) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  /*
   * SearchResource
   */

  @Override
  public abstract QueryResult search(final Query query) throws TwitterException;

  @Override
  public String searchJSON(final Query query) throws TwitterException {
    return TwitterObjects.getJSON(search(query));
  }

  /*
   * FriendsFollowersResources
   */

  public abstract <T> IDs fetchFriendsIDs(final T ident, final long cursor, final int count) throws TwitterException;

  @Override
  public IDs getFriendsIDs(final long userId, final long cursor) throws TwitterException {
    return fetchFriendsIDs(userId, cursor, 5000);
  }

  @Override
  public IDs getFriendsIDs(final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(userId, cursor, count);
  }

  @Override
  public IDs getFriendsIDs(final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsIDs(screenName, cursor, 5000);
  }

  @Override
  public IDs getFriendsIDs(final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(screenName, cursor, count);
  }

  public abstract <T> IDs fetchFollowersIDs(final T ident, final long cursor, final int count) throws TwitterException;

  @Override
  public IDs getFollowersIDs(final long userId, final long cursor) throws TwitterException {
    return fetchFollowersIDs(userId, cursor, 5000);
  }

  @Override
  public IDs getFollowersIDs(final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(userId, cursor, count);
  }

  @Override
  public IDs getFollowersIDs(final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersIDs(screenName, cursor, 5000);
  }

  @Override
  public IDs getFollowersIDs(final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(screenName, cursor, count);
  }

  public abstract <T> Relationship fetchFriendship(final T sourceIdent, final T targetIdent) throws TwitterException;

  @Override
  public Relationship showFriendship(final long sourceId, final long targetId) throws TwitterException {
    return fetchFriendship(sourceId, targetId);
  }

  @Override
  public Relationship showFriendship(final String sourceScreenName, final String targetScreenName) throws TwitterException {
    return fetchFriendship(sourceScreenName, targetScreenName);
  }

  public abstract <T> PagableResponseList<User> fetchFriendsList(final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  @Override
  public PagableResponseList<User> getFriendsList(final long userId, final long cursor) throws TwitterException {
    return fetchFriendsList(userId, cursor, 200, false, true);
  }

  @Override
  public PagableResponseList<User> getFriendsList(final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(userId, cursor, 200, false, true);
  }

  @Override
  public PagableResponseList<User> getFriendsList(final long userId, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFriendsList(userId, cursor, count, skipStatus, includeUserEntities);
  }

  @Override
  public PagableResponseList<User> getFriendsList(final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsList(screenName, cursor, 200, false, true);
  }

  @Override
  public PagableResponseList<User> getFriendsList(final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(screenName, cursor, count, false, true);
  }

  @Override
  public PagableResponseList<User> getFriendsList(final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFriendsList(screenName, cursor, count, skipStatus, includeUserEntities);
  }

  public abstract <T> PagableResponseList<User> fetchFollowersList(final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException;

  @Override
  public PagableResponseList<User> getFollowersList(final long userId, final long cursor) throws TwitterException {
    return fetchFollowersList(userId, cursor, 200, false, true);
  }

  @Override
  public PagableResponseList<User> getFollowersList(final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(userId, cursor, 200, false, true);
  }

  @Override
  public PagableResponseList<User> getFollowersList(final long userId, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersList(userId, cursor, count, skipStatus, includeUserEntities);
  }

  @Override
  public PagableResponseList<User> getFollowersList(final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersList(screenName, cursor, 200, false, true);
  }

  @Override
  public PagableResponseList<User> getFollowersList(final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(screenName, cursor, count, false, true);
  }

  @Override
  public PagableResponseList<User> getFollowersList(final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersList(screenName, cursor, count, skipStatus, includeUserEntities);
  }

  /*
   * FriendsFollowersResources
   */

  @Override
  public <T> String fetchFriendsIDsJSON(final T ident, final long cursor, final int count) throws TwitterException {
    return TwitterObjects.getJSON(fetchFriendsIDs(ident, cursor, count));
  }

  @Override
  public String getFriendsIDsJSON(final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDsJSON(userId, cursor, count);
  }

  @Override
  public String getFriendsIDsJSON(final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDsJSON(screenName, cursor, count);
  }

  @Override
  public <T> String fetchFollowersIDsJSON(final T ident, final long cursor, final int count) throws TwitterException {
    return TwitterObjects.getJSON(fetchFollowersIDs(ident, cursor, count));
  }

  @Override
  public String getFollowersIDsJSON(final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDsJSON(userId, cursor, count);
  }

  @Override
  public String getFollowersIDsJSON(final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDsJSON(screenName, cursor, count);
  }

  @Override
  public <T> String fetchFriendshipJSON(final T sourceIdent, final T targetIdent) throws TwitterException {
    return TwitterObjects.getJSON(fetchFriendship(sourceIdent, targetIdent));
  }

  @Override
  public String showFriendshipJSON(final long sourceId, final long targetId) throws TwitterException {
    return fetchFriendshipJSON(sourceId, targetId);
  }

  @Override
  public String showFriendshipJSON(final String sourceScreenName, final String targetScreenName) throws TwitterException {
    return fetchFriendshipJSON(sourceScreenName, targetScreenName);
  }

  @Override
  public <T> List<String> fetchFriendsListJSON(final T ident, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return TwitterObjects.getJSONList(fetchFriendsList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  @Override
  public List<String> getFriendsListJSON(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return fetchFriendsListJSON(userId, cursor, count, skipStatus, includeUserEntities);
  }

  @Override
  public List<String> getFriendsListJSON(final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFriendsListJSON(screenName, cursor, count, skipStatus, includeUserEntities);
  }

  @Override
  public <T> List<String> fetchFollowersListJSON(final T ident, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return TwitterObjects.getJSONList(fetchFollowersList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  @Override
  public List<String> getFollowersListJSON(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return fetchFollowersListJSON(userId, cursor, count, skipStatus, includeUserEntities);
  }

  @Override
  public List<String> getFollowersListJSON(final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersListJSON(screenName, cursor, count, skipStatus, includeUserEntities);
  }

  /*
   * Unsupported FriendsFollowersResources
   */

  @Override
  @Deprecated
  public User createFriendship(final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createFriendship(final String screenName) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createFriendship(final long userId, final boolean follow) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createFriendship(final String screenName, final boolean follow) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyFriendship(final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyFriendship(final String screenName) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Relationship updateFriendship(final long userId, final boolean enableDeviceNotification, final boolean retweets) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Relationship updateFriendship(final String screenName, final boolean enableDeviceNotification, final boolean retweets) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Friendship> lookupFriendships(final long[] ids) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Friendship> lookupFriendships(final String[] screenNames) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getIncomingFriendships(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getOutgoingFriendships(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getFriendsIDs(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getFollowersIDs(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getNoRetweetsFriendships() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  /*
   * UsersResources
   */


  public abstract <T> ResponseList<User> fetchLookupUsers(final T idents) throws TwitterException;

  @Override
  public <T> List<String> fetchLookupUsersJSON(final T idents) throws TwitterException {
    return TwitterObjects.getJSONList(fetchLookupUsers(idents));
  }

  @Override
  public List<String> searchUsersJSON(final String query, final int page) throws TwitterException {
    return TwitterObjects.getJSONList(searchUsers(query, page));
  }

  @Override
  public ResponseList<User> lookupUsers(final long[] ids) throws TwitterException {
    return fetchLookupUsers(ids);
  }

  @Override
  public List<String> lookupUsersJSON(final long[] ids) throws TwitterException {
    return fetchLookupUsersJSON(ids);
  }

  @Override
  public ResponseList<User> lookupUsers(final String[] screenNames) throws TwitterException {
    return fetchLookupUsers(screenNames);
  }

  @Override
  public List<String> lookupUsersJSON(final String[] screenNames) throws TwitterException {
    return fetchLookupUsersJSON(screenNames);
  }

  public abstract <T> User fetchUser(final T ident) throws TwitterException;

  @Override
  public <T> String fetchUserJSON(final T ident) throws TwitterException {
    return TwitterObjects.getJSON(fetchUser(ident));
  }

  @Override
  public User showUser(final long userId) throws TwitterException {
    return fetchUser(userId);
  }

  @Override
  public String showUserJSON(final long userId) throws TwitterException {
    return fetchUserJSON(userId);
  }

  @Override
  public User showUser(final String screenName) throws TwitterException {
    return fetchUser(screenName);
  }

  @Override
  public String showUserJSON(final String screenName) throws TwitterException {
    return fetchUserJSON(screenName);
  }

  public abstract <T> ResponseList<User> fetchContributees(final T ident) throws TwitterException;

  @Override
  public <T> List<String> fetchContributeesJSON(final T ident) throws TwitterException {
    return TwitterObjects.getJSONList(fetchContributees(ident));
  }

  @Override
  public ResponseList<User> getContributees(final long userId) throws TwitterException {
    return fetchContributees(userId);
  }

  @Override
  public List<String> getContributeesJSON(final long userId) throws TwitterException {
    return fetchContributeesJSON(userId);
  }

  @Override
  public ResponseList<User> getContributees(final String screenName) throws TwitterException {
    return fetchContributees(screenName);
  }

  @Override
  public List<String> getContributeesJSON(final String screenName) throws TwitterException {
    return fetchContributeesJSON(screenName);
  }

  public abstract <T> ResponseList<User> fetchContributors(final T ident) throws TwitterException;

  @Override
  public <T> List<String> fetchContributorsJSON(final T ident) throws TwitterException {
    return TwitterObjects.getJSONList(fetchContributees(ident));
  }

  @Override
  public ResponseList<User> getContributors(final long userId) throws TwitterException {
    return fetchContributors(userId);
  }

  @Override
  public List<String> getContributorsJSON(final long userId) throws TwitterException {
    return fetchContributorsJSON(userId);
  }

  @Override
  public ResponseList<User> getContributors(final String screenName) throws TwitterException {
    return fetchContributors(screenName);
  }

  @Override
  public List<String> getContributorsJSON(final String screenName) throws TwitterException {
    return fetchContributorsJSON(screenName);
  }

  /*
   * Unsupported UsersResources
   */

  @Override
  @Deprecated
  public AccountSettings getAccountSettings() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User verifyCredentials() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public AccountSettings updateAccountSettings(final Integer trendLocationWoeid, final Boolean sleepTimeEnabled, final String startSleepTime,
      final String endSleepTime, final String timeZone, final String lang) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfile(final String name, final String url, final String location, final String description) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileBackgroundImage(final File image, final boolean tile) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileBackgroundImage(final InputStream image, final boolean tile) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileColors(final String profileBackgroundColor, final String profileTextColor, final String profileLinkColor,
      final String profileSidebarFillColor, final String profileSidebarBorderColor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileImage(final File image) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileImage(final InputStream image) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<User> getBlocksList() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<User> getBlocksList(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getBlocksIDs() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getBlocksIDs(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createBlock(final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createBlock(final String screenName) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyBlock(final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyBlock(final String screen_name) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public void removeProfileBanner() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public void updateProfileBanner(final File image) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public void updateProfileBanner(final InputStream image) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createMute(final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createMute(final String screenName) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyMute(final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyMute(final String screenName) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getMutesIDs(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<User> getMutesList(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  /*
   * FavoritesResources
   */

  public abstract <T> ResponseList<Status> fetchFavorites(final T ident, final Paging paging) throws TwitterException;

  @Override
  public <T> List<String> fetchFavoritesJSON(final T ident, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(fetchFavorites(ident, paging));
  }

  @Override
  public ResponseList<Status> getFavorites(final long userId) throws TwitterException {
    return fetchFavorites(userId, new Paging());
  }

  @Override
  public ResponseList<Status> getFavorites(final long userId, final Paging paging) throws TwitterException {
    return fetchFavorites(userId, paging);
  }

  @Override
  public List<String> getFavoritesJSON(final long userId, final Paging paging) throws TwitterException {
    return fetchFavoritesJSON(userId, paging);
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName) throws TwitterException {
    return fetchFavorites(screenName, new Paging());
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName, final Paging paging) throws TwitterException {
    return fetchFavorites(screenName, paging);
  }

  @Override
  public List<String> getFavoritesJSON(final String screenName, final Paging paging) throws TwitterException {
    return fetchFavoritesJSON(screenName, paging);
  }

  /*
   * Unsupported FavoritesResources
   */

  @Override
  @Deprecated
  public ResponseList<Status> getFavorites() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getFavorites(final Paging paging) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status createFavorite(final long id) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status destroyFavorite(final long id) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  /*
   * ListsResources
   */

  public abstract <T> ResponseList<UserList> fetchUserLists(final T ident, boolean reverse) throws TwitterException;

  @Override
  public <T> List<String> fetchUserListsJSON(final T ident, boolean reverse) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserLists(ident, reverse));
  }

  @Override
  public ResponseList<UserList> getUserLists(final String listOwnerScreenName) throws TwitterException {
    return fetchUserLists(listOwnerScreenName, false);
  }

  @Override
  public ResponseList<UserList> getUserLists(final long listOwnerUserId) throws TwitterException {
    return fetchUserLists(listOwnerUserId, false);
  }

  @Override
  public ResponseList<UserList> getUserLists(String listOwnerScreenName, boolean reverse) throws TwitterException {
    return fetchUserLists(listOwnerScreenName, reverse);
  }

  @Override
  public List<String> getUserListsJSON(final String listOwnerScreenName, boolean reverse) throws TwitterException {
    return fetchUserListsJSON(listOwnerScreenName, reverse);
  }

  @Override
  public ResponseList<UserList> getUserLists(long listOwnerUserId, boolean reverse) throws TwitterException {
    return fetchUserLists(listOwnerUserId, reverse);
  }

  @Override
  public List<String> getUserListsJSON(final long listOwnerUserId, boolean reverse) throws TwitterException {
    return fetchUserListsJSON(listOwnerUserId, reverse);
  }



  public abstract <T> ResponseList<Status> fetchUserListStatuses(final T ident, final String slug, final Paging paging) throws TwitterException;

  @Override
  public ResponseList<Status> getUserListStatuses(final long ownerId, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(ownerId, slug, paging);
  }

  @Override
  public ResponseList<Status> getUserListStatuses(final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(ownerScreenName, slug, paging);
  }



  public abstract <T> PagableResponseList<UserList> fetchUserListMemberships(final T ident, final int count, final long cursor) throws TwitterException;

  @Override
  public PagableResponseList<UserList> getUserListMemberships(final long listMemberId, final long cursor) throws TwitterException {
    return fetchUserListMemberships(listMemberId, 20, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(final String listMemberScreenName, final long cursor) throws TwitterException {
    return fetchUserListMemberships(listMemberScreenName, 20, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(long listMemberId, int count, long cursor) throws TwitterException {
    return fetchUserListMemberships(listMemberId, count, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, int count, long cursor) throws TwitterException {
    return fetchUserListMemberships(listMemberScreenName, count, cursor);
  }

  @Override
  public <T> List<String> fetchUserListMembershipsJSON(final T ident, final int count, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListMemberships(ident, count, cursor));
  }

  @Override
  public List<String> getUserListMembershipsJSON(final long listMemberId, final int count, final long cursor) throws TwitterException {
    return fetchUserListMembershipsJSON(listMemberId, count, cursor);
  }

  @Override
  public List<String> getUserListMembershipsJSON(final String listMemberScreenName, final int count, final long cursor) throws TwitterException {
    return fetchUserListMembershipsJSON(listMemberScreenName, count, cursor);
  }



  public abstract <T> PagableResponseList<User> fetchUserListSubscribers(final T ident, final String slug, final int count, final long cursor,
      boolean skipStatus) throws TwitterException;

  @Override
  public PagableResponseList<User> getUserListSubscribers(long listId, int count, long cursor) throws TwitterException {
    return getUserListSubscribers(listId, count, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long listId, long cursor) throws TwitterException {
    return getUserListSubscribers(listId, 5000, cursor, false);
  }

  @Override
  public abstract PagableResponseList<User> getUserListSubscribers(long listId, int count, long cursor, boolean skipStatus) throws TwitterException;

  @Override
  public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, long cursor) throws TwitterException {
    return fetchUserListSubscribers(ownerId, slug, 5000, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, int count, long cursor) throws TwitterException {
    return fetchUserListSubscribers(ownerId, slug, count, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(String ownerScreenName, String slug, long cursor) throws TwitterException {
    return fetchUserListSubscribers(ownerScreenName, slug, 5000, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(String ownerScreenName, String slug, int count, long cursor) throws TwitterException {
    return fetchUserListSubscribers(ownerScreenName, slug, count, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return fetchUserListSubscribers(ownerId, slug, count, cursor, skipStatus);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final String ownerScreenName, final String slug, final int count, final long cursor,
      boolean skipStatus) throws TwitterException {
    return fetchUserListSubscribers(ownerScreenName, slug, count, cursor, skipStatus);
  }

  @Override
  public List<String> getUserListSubscribersJSON(final long listId, final int count, final long cursor, boolean skipStatus) throws TwitterException {
    return TwitterObjects.getJSONList(getUserListSubscribers(listId, count, cursor, skipStatus));
  }

  @Override
  public <T> List<String> fetchUserListSubscribersJSON(final T ident, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListSubscribers(ident, slug, count, cursor, skipStatus));
  }

  @Override
  public List<String> getUserListSubscribersJSON(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return fetchUserListSubscribersJSON(ownerId, slug, count, cursor, skipStatus);
  }

  @Override
  public List<String> getUserListSubscribersJSON(final String ownerScreenName, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return fetchUserListSubscribersJSON(ownerScreenName, slug, count, cursor, skipStatus);
  }



  public abstract <T> User fetchUserListSubscription(final T ident, final String slug, final long userId) throws TwitterException;

  @Override
  public <T> String fetchUserListSubscriptionJSON(final T ident, final String slug, final long userId) throws TwitterException {
    return TwitterObjects.getJSON(fetchUserListSubscription(ident, slug, userId));
  }

  @Override
  public User showUserListSubscription(final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(ownerId, slug, userId);
  }

  @Override
  public String showUserListSubscriptionJSON(final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscriptionJSON(ownerId, slug, userId);
  }

  @Override
  public User showUserListSubscription(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(ownerScreenName, slug, userId);
  }

  @Override
  public String showUserListSubscriptionJSON(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscriptionJSON(ownerScreenName, slug, userId);
  }



  public abstract <T> User fetchUserListMembership(final T ident, final String slug, final long userId) throws TwitterException;

  @Override
  public <T> String fetchUserListMembershipJSON(final T ident, final String slug, final long userId) throws TwitterException {
    return TwitterObjects.getJSON(fetchUserListMembership(ident, slug, userId));
  }

  @Override
  public User showUserListMembership(final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(ownerId, slug, userId);
  }

  @Override
  public String showUserListMembershipJSON(final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembershipJSON(ownerId, slug, userId);
  }

  @Override
  public User showUserListMembership(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(ownerScreenName, slug, userId);
  }

  @Override
  public String showUserListMembershipJSON(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembershipJSON(ownerScreenName, slug, userId);
  }



  public abstract <T> PagableResponseList<User> fetchUserListMembers(final T ident, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException;

  @Override
  public PagableResponseList<User> getUserListMembers(long listId, int count, long cursor) throws TwitterException {
    return getUserListMembers(listId, count, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(long listId, long cursor) throws TwitterException {
    return getUserListMembers(listId, 5000, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(long ownerId, String slug, long cursor) throws TwitterException {
    return fetchUserListMembers(ownerId, slug, 5000, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(long ownerId, String slug, int count, long cursor) throws TwitterException {
    return fetchUserListMembers(ownerId, slug, count, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(String ownerScreenName, String slug, long cursor) throws TwitterException {
    return fetchUserListMembers(ownerScreenName, slug, 5000, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(String ownerScreenName, String slug, int count, long cursor) throws TwitterException {
    return fetchUserListMembers(ownerScreenName, slug, count, cursor, false);
  }

  @Override
  public <T> List<String> fetchUserListMembersJSON(final T ident, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListMembers(ident, slug, count, cursor, skipStatus));
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return fetchUserListMembers(ownerId, slug, count, cursor, skipStatus);
  }

  @Override
  public List<String> getUserListMembersJSON(final long ownerId, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return fetchUserListMembersJSON(ownerId, slug, count, cursor, skipStatus);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final String ownerScreenName, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return fetchUserListMembers(ownerScreenName, slug, count, cursor, skipStatus);
  }

  @Override
  public List<String> getUserListMembersJSON(final String ownerScreenName, final String slug, final int count, final long cursor, boolean skipStatus)
      throws TwitterException {
    return fetchUserListMembersJSON(ownerScreenName, slug, count, cursor, skipStatus);
  }



  public abstract <T> UserList fetchUserList(final T ident, final String slug) throws TwitterException;

  @Override
  public UserList showUserList(final long ownerId, final String slug) throws TwitterException {
    return fetchUserList(ownerId, slug);
  }

  @Override
  public UserList showUserList(final String ownerScreenName, final String slug) throws TwitterException {
    return fetchUserList(ownerScreenName, slug);
  }

  @Override
  public <T> String fetchUserListJSON(final T ident, final String slug) throws TwitterException {
    return TwitterObjects.getJSON(fetchUserList(ident, slug));
  }

  @Override
  public String showUserListJSON(final long ownerId, final String slug) throws TwitterException {
    return fetchUserListJSON(ownerId, slug);
  }

  @Override
  public String showUserListJSON(final String ownerScreenName, final String slug) throws TwitterException {
    return fetchUserListJSON(ownerScreenName, slug);
  }



  public abstract <T> PagableResponseList<UserList> fetchUserListSubscriptions(final T ident, final int count, final long cursor) throws TwitterException;

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(final String listSubscriberScreenName, final int count, final long cursor)
      throws TwitterException {
    return fetchUserListSubscriptions(listSubscriberScreenName, count, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(final long listSubscriberId, final int count, final long cursor) throws TwitterException {
    return fetchUserListSubscriptions(listSubscriberId, count, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(String listSubscriberScreenName, long cursor) throws TwitterException {
    return fetchUserListSubscriptions(listSubscriberScreenName, 1000, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(long listSubscriberId, long cursor) throws TwitterException {
    return fetchUserListSubscriptions(listSubscriberId, 1000, cursor);
  }

  @Override
  public <T> List<String> fetchUserListSubscriptionsJSON(final T ident, int count, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListSubscriptions(ident, count, cursor));
  }

  @Override
  public List<String> getUserListSubscriptionsJSON(final String listSubscriberScreenName, final int count, final long cursor) throws TwitterException {
    return fetchUserListSubscriptionsJSON(listSubscriberScreenName, count, cursor);
  }

  @Override
  public List<String> getUserListSubscriptionsJSON(final long listSubscriberId, final int count, final long cursor) throws TwitterException {
    return fetchUserListSubscriptionsJSON(listSubscriberId, count, cursor);
  }



  public abstract <T> PagableResponseList<UserList> fetchUserListsOwnerships(final T ident, final int count, final long cursor) throws TwitterException;

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(listOwnerScreenName, count, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(final long listOwnerId, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(listOwnerId, count, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(String listOwnerScreenName, long cursor) throws TwitterException {
    return fetchUserListsOwnerships(listOwnerScreenName, 1000, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(long listOwnerId, long cursor) throws TwitterException {
    return fetchUserListsOwnerships(listOwnerId, 1000, cursor);
  }

  @Override
  public <T> List<String> fetchUserListsOwnershipsJSON(final T ident, final int count, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListsOwnerships(ident, count, cursor));
  }

  @Override
  public List<String> getUserListsOwnershipsJSON(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnershipsJSON(listOwnerScreenName, count, cursor);
  }

  @Override
  public List<String> getUserListsOwnershipsJSON(final long listOwnerId, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnershipsJSON(listOwnerId, count, cursor);
  }

  /*
   * Unsupported ListsResources
   */

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(int count, long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(final String listMemberScreenName, final long cursor, final boolean filterToOwnedLists)
      throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(final long listMemberId, final long cursor, final boolean filterToOwnedLists)
      throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, int count, long cursor, boolean filterToOwnedLists)
      throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(long listMemberId, int count, long cursor, boolean filterToOwnedLists) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(final long cursor) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(final long ownerId, final String slug, final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(final long ownerId, final String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMembers(final long ownerId, final long[] userIds) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMembers(final long ownerId, final String[] userIds) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListSubscription(final long ownerId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListSubscription(final long ownerId, final String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListSubscription(final String ownerScreenName, final String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListSubscription(final long ownerId, final String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListSubscription(final String ownerScreenName, String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMember(final long ownerId, final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(final long ownerId, final String[] userIds) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(final long ownerId, final long[] userIds) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(final long ownerId, final String slug, final long[] userIds) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(final String ownerScreenName, final String slug, final long[] userIds) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(final long ownerId, final String slug, final String[] screenNames) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(final String ownerScreenName, final String slug, final String[] screenNames) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMembers(final String ownerScreenName, final String slug, final String[] screenNames) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(final long ownerId, final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMember(final long ownerId, final String slug, final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMember(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserList(final long ownerId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserList(final long ownerId, final String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserList(final String ownerScreenName, final String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListSubscription(final long id) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList updateUserList(final long ownerId, final String slug, final String newListName, final boolean isPublicList, final String newDescription)
      throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList updateUserList(final String ownerScreenName, final String slug, final String newListName, final boolean isPublicList,
      final String newDescription) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserList(final String listName, final boolean isPublicList, final String description) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList updateUserList(final long id, final String listName, final boolean isPublicList, final String description) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }


  /*
   * PlacesGeoResources
   */


  @Override
  public String getGeoDetailsJSON(final String placeId) throws TwitterException {
    return TwitterObjects.getJSON(getGeoDetails(placeId));
  }

  @Override
  public List<String> reverseGeoCodeJSON(final GeoQuery query) throws TwitterException {
    return TwitterObjects.getJSONList(reverseGeoCode(query));
  }

  @Override
  public List<String> searchPlacesJSON(final GeoQuery query) throws TwitterException {
    return TwitterObjects.getJSONList(searchPlaces(query));
  }

  @Override
  public List<String> getSimilarPlacesJSON(final GeoLocation location, final String name, final String containedWithin, final String streetAddress)
      throws TwitterException {
    return TwitterObjects.getJSONList(getSimilarPlaces(location, name, containedWithin, streetAddress));
  }

  /*
   * TrendsResources
   */

  @Override
  public String getPlaceTrendsJSON(final int woeid) throws TwitterException {
    return TwitterObjects.getJSON(getPlaceTrends(woeid));
  }

  @Override
  public String getAvailableTrendsJSON() throws TwitterException {

    return TwitterObjects.getJSON(getAvailableTrends());
  }


  @Override
  public String getClosestTrendsJSON(final GeoLocation location) throws TwitterException {

    return TwitterObjects.getJSON(getClosestTrends(location));
  }


  /*
   * HelpResources
   */

  @Override
  @Deprecated
  public TwitterAPIConfiguration getAPIConfiguration() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Language> getLanguages() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public String getPrivacyPolicy() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public String getTermsOfService() throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UploadedMedia uploadMedia(String fileName, InputStream media) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }



  @Override
  public List<String> getUserListStatusesJSON(final long listId, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(getUserListStatuses(listId, paging));
  }

  @Override
  public <T> List<String> fetchUserListStatusesJSON(final T ident, final String slug, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListStatuses(ident, slug, paging));
  }

  @Override
  public List<String> getUserListStatusesJSON(final long ownerId, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatusesJSON(ownerId, slug, paging);
  }

  @Override
  public List<String> getUserListStatusesJSON(final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatusesJSON(ownerScreenName, slug, paging);
  }



}
