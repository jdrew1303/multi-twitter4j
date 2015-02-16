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
    FavoritesResources, ListsResources, PlacesGeoResources, TrendsResources, HelpResources, JSONResources, CursorResources {

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
   * Bulk Request Methods:
   */

  /*
   * Timelines:
   */

  @Override
  public <T, K> List<K> getBulkUserTimeline(As type, T ident, long initSinceId) throws TwitterException {
    return getBulkUserTimeline(type, ident, initSinceId, -1, -1);
  }

  @Override
  public <T, K> List<K> getBulkUserTimeline(As type, T ident) throws TwitterException {
    return getBulkUserTimeline(type, ident, -1, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkUserTimeline(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return getBulkUserTimeline(As.POJO, ident, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<Status> getBulkUserTimeline(T ident, long initSinceId) throws TwitterException {
    return getBulkUserTimeline(As.POJO, ident, initSinceId, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkUserTimeline(T ident) throws TwitterException {
    return getBulkUserTimeline(As.POJO, ident, -1, -1, -1);
  }

  // Favorites Timelines:

  @Override
  public <T, K> List<K> getBulkFavorites(As type, T ident, long initSinceId) throws TwitterException {
    return getBulkFavorites(type, ident, initSinceId, -1, -1);
  }

  @Override
  public <T, K> List<K> getBulkFavorites(As type, T ident) throws TwitterException {
    return getBulkFavorites(type, ident, -1, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkFavorites(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return getBulkFavorites(As.POJO, ident, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<Status> getBulkFavorites(T ident, long initSinceId) throws TwitterException {
    return getBulkFavorites(As.POJO, ident, initSinceId, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkFavorites(T ident) throws TwitterException {
    return getBulkFavorites(As.POJO, ident, -1, -1, -1);
  }

  // List Timelines:

  @Override
  public <T, K> List<K> getBulkUserListStatuses(As type, T ident, String slug, long initSinceId) throws TwitterException {
    return getBulkUserListStatuses(type, ident, slug, initSinceId, -1, -1);
  }

  @Override
  public <T, K> List<K> getBulkUserListStatuses(As type, T ident, String slug) throws TwitterException {
    return getBulkUserListStatuses(type, ident, slug, -1, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkUserListStatuses(T ident, String slug, long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    return getBulkUserListStatuses(As.POJO, ident, slug, initSinceId, initMaxId, maxElements);
  }

  @Override
  public <T> List<Status> getBulkUserListStatuses(T ident, String slug, long initSinceId) throws TwitterException {
    return getBulkUserListStatuses(As.POJO, ident, slug, initSinceId, -1, -1);
  }

  @Override
  public <T> List<Status> getBulkUserListStatuses(T ident, String slug) throws TwitterException {
    return getBulkUserListStatuses(As.POJO, ident, slug, -1, -1, -1);
  }

  /*
   * Tweets
   */

  @Override
  public List<Long> getBulkRetweeterIds(final long statusId) throws TwitterException {
    return getBulkRetweeterIds(statusId, -1);
  }

  /*
   * FriendsFollowers
   */

  @Override
  public <T> List<Long> getBulkFriendsIDs(T ident) throws TwitterException {
    return getBulkFriendsIDs(ident, -1);
  }

  @Override
  public <T> List<Long> getBulkFollowersIDs(T ident) throws TwitterException {
    return getBulkFollowersIDs(ident, -1);
  }

  @Override
  public <T, K> List<K> getBulkFriendsList(As type, final T ident, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException {
    return getBulkFriendsList(type, ident, -1, skipStatus, includeUserEntities);
  }

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
  public <T> List<User> getBulkFriendsList(final T ident, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException {
    return getBulkFriendsList(As.POJO, ident, -1, skipStatus, includeUserEntities);
  }

  @Override
  public <T> List<User> getBulkFriendsList(final T ident) throws TwitterException {
    return getBulkFriendsList(As.POJO, ident, -1, false, true);
  }

  @Override
  public <T, K> List<K> getBulkFollowersList(As type, final T ident, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException {
    return getBulkFollowersList(type, ident, -1, skipStatus, includeUserEntities);
  }

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
  public <T> List<User> getBulkFollowersList(final T ident, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException {
    return getBulkFollowersList(As.POJO, ident, -1, skipStatus, includeUserEntities);
  }

  @Override
  public <T> List<User> getBulkFollowersList(final T ident) throws TwitterException {
    return getBulkFollowersList(As.POJO, ident, -1, false, true);
  }

  /*
   * Lists:
   */

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
   * TimelinesResources
   */

  /**
   * @see #getUserTimeline(long, Paging)
   * @see #getUserTimeline(String, Paging)
   */
  public abstract <T> ResponseList<Status> fetchUserTimeline(final T ident, final Paging paging) throws TwitterException;

  @Override
  public ResponseList<Status> getUserTimeline(final String screenName) throws TwitterException {
    return fetchUserTimeline(screenName, new Paging());
  }

  @Override
  public ResponseList<Status> getUserTimeline(final String screenName, final Paging paging) throws TwitterException {
    return fetchUserTimeline(screenName, paging);
  }

  @Override
  public ResponseList<Status> getUserTimeline(final long userId) throws TwitterException {
    return fetchUserTimeline(userId, new Paging());
  }

  @Override
  public ResponseList<Status> getUserTimeline(final long userId, final Paging paging) throws TwitterException {
    return fetchUserTimeline(userId, paging);
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
  public IDs getRetweeterIds(final long statusId, final long cursor) throws TwitterException {
    return getRetweeterIds(statusId, 200, cursor);
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

  //

  /*
   * FriendsFollowersResources
   */

  /**
   * @see #getFriendsIDs(String, long, int)
   * @see #getFriendsIDs(long, long, int)
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

  /**
   * @see #showFriendship(long, long)
   * @see #showFriendship(String, String)
   */
  public abstract <T> Relationship fetchFriendship(final T sourceIdent, final T targetIdent) throws TwitterException;

  @Override
  public Relationship showFriendship(final long sourceId, final long targetId) throws TwitterException {
    return fetchFriendship(sourceId, targetId);
  }

  @Override
  public Relationship showFriendship(final String sourceScreenName, final String targetScreenName) throws TwitterException {
    return fetchFriendship(sourceScreenName, targetScreenName);
  }

  /**
   * @see #getFriendsList(long, long)
   * @see #getFriendsList(String, long)
   */
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

  /**
   * @see #getFollowersList(long, long)
   * @see #getFollowersList(String, long)
   */
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

  /**
   * @see #lookupUsers(long[])
   * @see #lookupUsers(String[])
   */
  public abstract <T> ResponseList<User> fetchLookupUsers(final T idents) throws TwitterException;

  @Override
  public ResponseList<User> lookupUsers(final long[] ids) throws TwitterException {
    return fetchLookupUsers(ids);
  }

  @Override
  public ResponseList<User> lookupUsers(final String[] screenNames) throws TwitterException {
    return fetchLookupUsers(screenNames);
  }

  public abstract <T> User fetchUser(final T ident) throws TwitterException;

  @Override
  public User showUser(final long userId) throws TwitterException {
    return fetchUser(userId);
  }

  @Override
  public User showUser(final String screenName) throws TwitterException {
    return fetchUser(screenName);
  }

  /**
   * @see #getContributees(long)
   * @see #getContributees(String)
   */
  public abstract <T> ResponseList<User> fetchContributees(final T ident) throws TwitterException;

  @Override
  public ResponseList<User> getContributees(final long userId) throws TwitterException {
    return fetchContributees(userId);
  }

  @Override
  public ResponseList<User> getContributees(final String screenName) throws TwitterException {
    return fetchContributees(screenName);
  }

  /**
   * @see #getContributors(long)
   * @see #getContributors(String)
   */
  public abstract <T> ResponseList<User> fetchContributors(final T ident) throws TwitterException;

  @Override
  public ResponseList<User> getContributors(final long userId) throws TwitterException {
    return fetchContributors(userId);
  }

  @Override
  public ResponseList<User> getContributors(final String screenName) throws TwitterException {
    return fetchContributors(screenName);
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

  /**
   * @see #getFavorites(long)
   * @see #getFavorites(String)
   */
  public abstract <T> ResponseList<Status> fetchFavorites(final T ident, final Paging paging) throws TwitterException;

  @Override
  public ResponseList<Status> getFavorites(final long userId) throws TwitterException {
    return fetchFavorites(userId, new Paging());
  }

  @Override
  public ResponseList<Status> getFavorites(final long userId, final Paging paging) throws TwitterException {
    return fetchFavorites(userId, paging);
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName) throws TwitterException {
    return fetchFavorites(screenName, new Paging());
  }

  @Override
  public ResponseList<Status> getFavorites(final String screenName, final Paging paging) throws TwitterException {
    return fetchFavorites(screenName, paging);
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

  /**
   * @see #getUserLists(long)
   * @see #getUserLists(String)
   */
  public abstract <T> ResponseList<UserList> fetchUserLists(final T ident) throws TwitterException;

  @Override
  public ResponseList<UserList> getUserLists(final String listOwnerScreenName) throws TwitterException {
    return fetchUserLists(listOwnerScreenName);
  }

  @Override
  public ResponseList<UserList> getUserLists(final long listOwnerUserId) throws TwitterException {
    return fetchUserLists(listOwnerUserId);
  }

  /**
   * @see #getUserListStatuses(long, String, Paging)
   * @see #getUserListStatuses(String, String, Paging)
   */
  public abstract <T> ResponseList<Status> fetchUserListStatuses(final T ident, final String slug, final Paging paging) throws TwitterException;

  @Override
  public ResponseList<Status> getUserListStatuses(final long ownerId, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(ownerId, slug, paging);
  }

  @Override
  public ResponseList<Status> getUserListStatuses(final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(ownerScreenName, slug, paging);
  }

  /**
   * @see #getUserListMemberships(long, long)
   * @see #getUserListMemberships(String, lonh)
   */
  public abstract <T> PagableResponseList<UserList> fetchUserListMemberships(final T ident, final long cursor) throws TwitterException;


  @Override
  public PagableResponseList<UserList> getUserListMemberships(final long listMemberId, final long cursor) throws TwitterException {
    return fetchUserListMemberships(listMemberId, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(final String listMemberScreenName, final long cursor) throws TwitterException {
    return fetchUserListMemberships(listMemberScreenName, cursor);
  }

  /**
   * @see #getUserListSubscribers(long, String, long)
   * @see #getUserListSubscribers(String, String, long)
   */
  public abstract <T> PagableResponseList<User> fetchUserListSubscribers(final T ident, final String slug, final long cursor) throws TwitterException;

  @Override
  public PagableResponseList<User> getUserListSubscribers(final long ownerId, final String slug, final long cursor) throws TwitterException {
    return fetchUserListSubscribers(ownerId, slug, cursor);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return fetchUserListSubscribers(ownerScreenName, slug, cursor);
  }

  /**
   * @see #showUserListSubscription(long, String, long)
   * @see #showUserListSubscription(String, String, long)
   */
  public abstract <T> User fetchUserListSubscription(final T ident, final String slug, final long userId) throws TwitterException;

  @Override
  public User showUserListSubscription(final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(ownerId, slug, userId);
  }

  @Override
  public User showUserListSubscription(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(ownerScreenName, slug, userId);
  }

  /**
   * @see #showUserListMembership(long, String, long)
   * @see #showUserListMembership(String, String, long)
   */
  public abstract <T> User fetchUserListMembership(final T ident, final String slug, final long userId) throws TwitterException;

  @Override
  public User showUserListMembership(final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(ownerId, slug, userId);
  }

  @Override
  public User showUserListMembership(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(ownerScreenName, slug, userId);
  }

  /**
   * @see #getUserListMembers(long, String, long)
   * @see #getUserListMembers(String, String, long)
   */
  public abstract <T> PagableResponseList<User> fetchUserListMembers(final T ident, final String slug, final long cursor) throws TwitterException;

  @Override
  public PagableResponseList<User> getUserListMembers(final long ownerId, final String slug, final long cursor) throws TwitterException {
    return fetchUserListMembers(ownerId, slug, cursor);
  }

  @Override
  public PagableResponseList<User> getUserListMembers(final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return fetchUserListMembers(ownerScreenName, slug, cursor);
  }

  /**
   * @see #showUserList(long, String)
   * @see #showUserList(String, String)
   */
  public abstract <T> UserList fetchUserList(final T ident, final String slug) throws TwitterException;

  @Override
  public UserList showUserList(final long ownerId, final String slug) throws TwitterException {
    return fetchUserList(ownerId, slug);
  }

  @Override
  public UserList showUserList(final String ownerScreenName, final String slug) throws TwitterException {
    return fetchUserList(ownerScreenName, slug);
  }

  /*
   * TODO: Twitter4J Missing getUserListSubscriptions(long userId, cursor...)
   */
  /**
   * @see #getUserListSubscriptions(String)
   */
  public abstract <T> PagableResponseList<UserList> fetchUserListSubscriptions(final T ident, final long cursor) throws TwitterException;

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(final String listOwnerScreenName, final long cursor) throws TwitterException {
    return fetchUserListSubscriptions(listOwnerScreenName, cursor);
  }

  /**
   * @see #getUserListsOwnerships(long, int, long)
   * @see #getUserListsOwnerships(String, int, long)
   */
  public abstract <T> PagableResponseList<UserList> fetchUserListsOwnerships(final T ident, final int count, final long cursor) throws TwitterException;

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(listOwnerScreenName, count, cursor);
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(final long listOwnerId, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(listOwnerId, count, cursor);
  }

  /*
   * Unsupported ListsResources
   */

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
   * Unsupported PlacesGeoResources
   */

  //

  /*
   * Unsupported TrendsResources
   */

  //

  /*
   * Unsupported HelpResources
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

  /*
   * Wrap TwitterResources methods to access original JSON from Twitter.
   */

  /*
   * TimelinesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserTimeline()
   */
  @Override
  public <T> List<String> fetchUserTimeline(As json, final T ident, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserTimeline(ident, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String)
   */
  @Override
  public List<String> getUserTimeline(As json, final String screenName) throws TwitterException {
    return fetchUserTimeline(json, screenName, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(String, Paging)
   */
  @Override
  public List<String> getUserTimeline(As json, final String screenName, final Paging paging) throws TwitterException {
    return fetchUserTimeline(json, screenName, paging);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long)
   */
  @Override
  public List<String> getUserTimeline(As json, final long userId) throws TwitterException {
    return fetchUserTimeline(json, userId, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserTimeline(long, Paging)
   */
  @Override
  public List<String> getUserTimeline(As json, final long userId, final Paging paging) throws TwitterException {
    return fetchUserTimeline(json, userId, paging);
  }

  /*
   * TweetsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweets()
   */
  @Override
  public List<String> getRetweets(As json, final long statusId) throws TwitterException {
    return TwitterObjects.getJSONList(getRetweets(statusId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  @Override
  public String getRetweeterIds(As json, final long statusId, final long cursor) throws TwitterException {
    return TwitterObjects.getJSON(getRetweeterIds(statusId, 100, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getRetweeterIds()
   */
  @Override
  public String getRetweeterIds(As json, final long statusId, final int count, final long cursor) throws TwitterException {
    return TwitterObjects.getJSON(getRetweeterIds(statusId, count, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showStatus(long)
   */
  @Override
  public String showStatus(As json, final long id) throws TwitterException {
    return TwitterObjects.getJSON(showStatus(id));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookup(long[])
   */
  @Override
  public List<String> lookup(As json, final long[] ids) throws TwitterException {
    return TwitterObjects.getJSONList(lookup(ids));
  }

  /*
   * SearchResource
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#search()
   */
  @Override
  public String search(As json, final Query query) throws TwitterException {
    return TwitterObjects.getJSON(search(query));
  }

  /*
   * FriendsFollowersResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsIDs()
   */
  @Override
  public <T> String fetchFriendsIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
    return TwitterObjects.getJSON(fetchFriendsIDs(ident, cursor, count));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  @Override
  public String getFriendsIDs(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFriendsIDs(json, userId, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  @Override
  public String getFriendsIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(json, userId, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  @Override
  public String getFriendsIDs(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsIDs(json, screenName, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsIDs()
   */
  @Override
  public String getFriendsIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsIDs(json, screenName, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFollowersIDs()
   */
  @Override
  public <T> String fetchFollowersIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
    return TwitterObjects.getJSON(fetchFollowersIDs(ident, cursor, count));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  @Override
  public String getFollowersIDs(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFollowersIDs(json, userId, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  @Override
  public String getFollowersIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(json, userId, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  @Override
  public String getFollowersIDs(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersIDs(json, screenName, cursor, 5000);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersIDs()
   */
  @Override
  public String getFollowersIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersIDs(json, screenName, cursor, count);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendship()
   */
  @Override
  public <T> String fetchFriendship(As json, final T sourceIdent, final T targetIdent) throws TwitterException {
    return TwitterObjects.getJSON(fetchFriendship(sourceIdent, targetIdent));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  @Override
  public String showFriendship(As json, final long sourceId, final long targetId) throws TwitterException {
    return fetchFriendship(json, sourceId, targetId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showFriendship()
   */
  @Override
  public String showFriendship(As json, final String sourceScreenName, final String targetScreenName) throws TwitterException {
    return fetchFriendship(json, sourceScreenName, targetScreenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFriendsList()
   */
  @Override
  public <T> List<String> fetchFriendsList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return TwitterObjects.getJSONList(fetchFriendsList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  @Override
  public List<String> getFriendsList(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFriendsList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  @Override
  public List<String> getFriendsList(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  @Override
  public List<String> getFriendsList(As json, final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
      throws TwitterException {
    return fetchFriendsList(json, userId, cursor, count, skipStatus, includeUserEntities);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  @Override
  public List<String> getFriendsList(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  @Override
  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, count, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFriendsList()
   */
  @Override
  public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFriendsList(json, screenName, cursor, count, skipStatus, includeUserEntities);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  @Override
  public <T> List<String> fetchFollowersList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return TwitterObjects.getJSONList(fetchFollowersList(ident, cursor, count, skipStatus, includeUserEntities));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  @Override
  public List<String> getFollowersList(As json, final long userId, final long cursor) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  @Override
  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  @Override
  public List<String> getFollowersList(As json, final long userId, final long cursor, final int count, final boolean skipStatus,
      final boolean includeUserEntities) throws TwitterException {
    return fetchFollowersList(json, userId, cursor, count, skipStatus, includeUserEntities);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  @Override
  public List<String> getFollowersList(As json, final String screenName, final long cursor) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, 200, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  @Override
  public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
    return fetchFollowersList(json, screenName, cursor, count, false, true);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFollowersList()
   */
  @Override
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
  @Override
  public <T> List<String> fetchLookupUsers(As json, final T idents) throws TwitterException {
    return TwitterObjects.getJSONList(fetchLookupUsers(idents));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  @Override
  public List<String> lookupUsers(As json, final long[] ids) throws TwitterException {
    return fetchLookupUsers(json, ids);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#lookupUsers()
   */
  @Override
  public List<String> lookupUsers(As json, final String[] screenNames) throws TwitterException {
    return fetchLookupUsers(json, screenNames);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUser()
   */
  @Override
  public <T> String fetchUser(As json, final T ident) throws TwitterException {
    return TwitterObjects.getJSON(fetchUser(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  @Override
  public String showUser(As json, final long userId) throws TwitterException {
    return fetchUser(json, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUser()
   */
  @Override
  public String showUser(As json, final String screenName) throws TwitterException {
    return fetchUser(json, screenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchUsers()
   */
  @Override
  public List<String> searchUsers(As json, final String query, final int page) throws TwitterException {
    return TwitterObjects.getJSONList(searchUsers(query, page));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributees()
   */
  @Override
  public <T> List<String> fetchContributees(As json, final T ident) throws TwitterException {
    return TwitterObjects.getJSONList(fetchContributees(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  @Override
  public List<String> getContributees(As json, final long userId) throws TwitterException {
    return fetchContributees(json, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributees()
   */
  @Override
  public List<String> getContributees(As json, final String screenName) throws TwitterException {
    return fetchContributees(json, screenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchContributors()
   */
  @Override
  public <T> List<String> fetchContributors(As json, final T ident) throws TwitterException {
    return TwitterObjects.getJSONList(fetchContributees(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  @Override
  public List<String> getContributors(As json, final long userId) throws TwitterException {
    return fetchContributors(json, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getContributors()
   */
  @Override
  public List<String> getContributors(As json, final String screenName) throws TwitterException {
    return fetchContributors(json, screenName);
  }

  /*
   * FavoritesResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchFavorites()
   */
  @Override
  public <T> List<String> fetchFavorites(As json, final T ident, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(fetchFavorites(ident, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  @Override
  public List<String> getFavorites(As json, final long userId) throws TwitterException {
    return fetchFavorites(json, userId, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  @Override
  public List<String> getFavorites(As json, final long userId, final Paging paging) throws TwitterException {
    return fetchFavorites(json, userId, paging);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  @Override
  public List<String> getFavorites(As json, final String screenName) throws TwitterException {
    return fetchFavorites(json, screenName, new Paging());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getFavorites()
   */
  @Override
  public List<String> getFavorites(As json, final String screenName, final Paging paging) throws TwitterException {
    return fetchFavorites(json, screenName, paging);
  }

  /*
   * ListsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserLists()
   */
  @Override
  public <T> List<String> fetchUserLists(As json, final T ident) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserLists(ident));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  @Override
  public List<String> getUserLists(As json, final String listOwnerScreenName) throws TwitterException {
    return fetchUserLists(json, listOwnerScreenName);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserLists()
   */
  @Override
  public List<String> getUserLists(As json, final long listOwnerUserId) throws TwitterException {
    return fetchUserLists(json, listOwnerUserId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  @Override
  public List<String> getUserListStatuses(As json, final long listId, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(getUserListStatuses(listId, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListStatuses()
   */
  @Override
  public <T> List<String> fetchUserListStatuses(As json, final T ident, final String slug, final Paging paging) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListStatuses(ident, slug, paging));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  @Override
  public List<String> getUserListStatuses(As json, final long ownerId, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(json, ownerId, slug, paging);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListStatuses()
   */
  @Override
  public List<String> getUserListStatuses(As json, final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
    return fetchUserListStatuses(json, ownerScreenName, slug, paging);
  }


  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMemberships()
   */
  @Override
  public <T> List<String> fetchUserListMemberships(As json, final T ident, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListMemberships(ident, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  @Override
  public List<String> getUserListMemberships(As json, final long listMemberId, final long cursor) throws TwitterException {
    return fetchUserListMemberships(json, listMemberId, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMemberships()
   */
  @Override
  public List<String> getUserListMemberships(As json, final String listMemberScreenName, final long cursor) throws TwitterException {
    return fetchUserListMemberships(json, listMemberScreenName, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  @Override
  public List<String> getUserListSubscribers(As json, final long listId, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(getUserListSubscribers(listId, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscribers()
   */
  @Override
  public <T> List<String> fetchUserListSubscribers(As json, final T ident, final String slug, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListSubscribers(ident, slug, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  @Override
  public List<String> getUserListSubscribers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException {
    return fetchUserListSubscribers(json, ownerId, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscribers()
   */
  @Override
  public List<String> getUserListSubscribers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return fetchUserListSubscribers(json, ownerScreenName, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListSubscription()
   */
  @Override
  public <T> String fetchUserListSubscription(As json, final T ident, final String slug, final long userId) throws TwitterException {
    return TwitterObjects.getJSON(fetchUserListSubscription(ident, slug, userId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  @Override
  public String showUserListSubscription(As json, final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(json, ownerId, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListSubscription()
   */
  @Override
  public String showUserListSubscription(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListSubscription(json, ownerScreenName, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembership()
   */
  @Override
  public <T> String fetchUserListMembership(As json, final T ident, final String slug, final long userId) throws TwitterException {
    return TwitterObjects.getJSON(fetchUserListMembership(ident, slug, userId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  @Override
  public String showUserListMembership(As json, final long ownerId, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(json, ownerId, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserListMembership()
   */
  @Override
  public String showUserListMembership(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException {
    return fetchUserListMembership(json, ownerScreenName, slug, userId);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListMembers()
   */
  @Override
  public <T> List<String> fetchUserListMembers(As json, final T ident, final String slug, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListMembers(ident, slug, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  @Override
  public List<String> getUserListMembers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException {
    return fetchUserListMembers(json, ownerId, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListMembers()
   */
  @Override
  public List<String> getUserListMembers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
    return fetchUserListMembers(json, ownerScreenName, slug, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserList()
   */
  @Override
  public <T> String fetchUserList(As json, final T ident, final String slug) throws TwitterException {
    return TwitterObjects.getJSON(fetchUserList(ident, slug));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  @Override
  public String showUserList(As json, final long ownerId, final String slug) throws TwitterException {
    return fetchUserList(json, ownerId, slug);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#showUserList()
   */
  @Override
  public String showUserList(As json, final String ownerScreenName, final String slug) throws TwitterException {
    return fetchUserList(json, ownerScreenName, slug);
  }

  /*
   * TODO: Twitter4J Missing getUserListSubscriptions(long userId, cursor...)
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#()
   */
  @Override
  public <T> List<String> fetchUserListSubscriptions(As json, final T ident, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListSubscriptions(ident, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListSubscriptions()
   */
  @Override
  public List<String> getUserListSubscriptions(As json, final String listOwnerScreenName, final long cursor) throws TwitterException {
    return fetchUserListSubscriptions(json, listOwnerScreenName, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#fetchUserListsOwnerships()
   */
  @Override
  public <T> List<String> fetchUserListsOwnerships(As json, final T ident, final int count, final long cursor) throws TwitterException {
    return TwitterObjects.getJSONList(fetchUserListsOwnerships(ident, count, cursor));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  @Override
  public List<String> getUserListsOwnerships(As json, final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(json, listOwnerScreenName, count, cursor);
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getUserListsOwnerships()
   */
  @Override
  public List<String> getUserListsOwnerships(As json, final long listOwnerId, final int count, final long cursor) throws TwitterException {
    return fetchUserListsOwnerships(json, listOwnerId, count, cursor);
  }

  /*
   * PlacesGeoResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getGeoDetails()
   */
  @Override
  public String getGeoDetails(As json, final String placeId) throws TwitterException {
    return TwitterObjects.getJSON(getGeoDetails(placeId));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#reverseGeoCode()
   */
  @Override
  public List<String> reverseGeoCode(As json, final GeoQuery query) throws TwitterException {
    return TwitterObjects.getJSONList(reverseGeoCode(query));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#searchPlaces()
   */
  @Override
  public List<String> searchPlaces(As json, final GeoQuery query) throws TwitterException {
    return TwitterObjects.getJSONList(searchPlaces(query));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getSimilarPlaces()
   */
  @Override
  public List<String> getSimilarPlaces(As json, final GeoLocation location, final String name, final String containedWithin, final String streetAddress)
      throws TwitterException {
    return TwitterObjects.getJSONList(getSimilarPlaces(location, name, containedWithin, streetAddress));
  }

  /*
   * TrendsResources
   */

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getPlaceTrends(int)
   */
  @Override
  public String getPlaceTrends(As json, final int woeid) throws TwitterException {
    return TwitterObjects.getJSON(getPlaceTrends(woeid));
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getAvailableTrends()
   */
  @Override
  public String getAvailableTrends(As json) throws TwitterException {
    return TwitterObjects.getJSON(getAvailableTrends());
  }

  /**
   * @see org.insight.twitter.wrapper.TwitterResources#getClosestTrends(GeoLocation)
   */
  @Override
  public String getClosestTrends(As json, final GeoLocation location) throws TwitterException {
    return TwitterObjects.getJSON(getClosestTrends(location));
  }

}
