package org.insight.twitter.wrapper;

import static twitter4j.TwitterObjects.newIDs;
import static twitter4j.TwitterObjects.newLocationResponseList;
import static twitter4j.TwitterObjects.newPagableUser;
import static twitter4j.TwitterObjects.newPagableUserList;
import static twitter4j.TwitterObjects.newPlaceResponseList;
import static twitter4j.TwitterObjects.newQueryResult;
import static twitter4j.TwitterObjects.newStatusResponseList;
import static twitter4j.TwitterObjects.newUserListResponseList;
import static twitter4j.TwitterObjects.newUserResponseList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import twitter4j.AccountSettings;
import twitter4j.Friendship;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.Query.ResultType;
import twitter4j.QueryResult;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Trends;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
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

  private static final String UNSUPPORTED_METHOD = "This API method is not supported!";

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
  public abstract <T> List<String> getBulkUserTimeline(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException;

  @Override
  public <T> List<String> getBulkUserTimeline(T ident) throws TwitterException {
    return getBulkUserTimeline(ident, -1, -1, -1);
  }

  /*
   * Bulk Favorites:
   */

  @Override
  public abstract <T> List<String> getBulkFavorites(T ident, long initSinceId, long initMaxId, int maxElements) throws TwitterException;

  @Override
  public <T> List<String> getBulkFavorites(T ident) throws TwitterException {
    return getBulkFavorites(ident, -1, -1, -1);
  }

  /*
   * Bulk Tweets
   */

  @Override
  public abstract Map<Long, String> getBulkTweetLookupMap(Collection<Long> ids) throws TwitterException;

  @Override
  public List<String> getBulkTweetLookup(Collection<Long> ids) throws TwitterException {
    return new ArrayList<String>(getBulkTweetLookupMap(ids).values());
  }

  @Override
  public abstract List<Long> getBulkRetweeterIds(long statusId, int maxElements) throws TwitterException;

  @Override
  public List<Long> getBulkRetweeterIds(long statusId) throws TwitterException {
    return getBulkRetweeterIds(statusId, -1);
  }

  /*
   * Bulk FriendsFollowers
   */
  @Override
  public abstract <T> List<Long> getBulkFriendsIDs(T ident, int maxElements) throws TwitterException;

  @Override
  public <T> List<Long> getBulkFriendsIDs(T ident) throws TwitterException {
    return getBulkFriendsIDs(ident, -1);
  }

  @Override
  public abstract <T> List<Long> getBulkFollowersIDs(T ident, int maxElements) throws TwitterException;

  @Override
  public <T> List<Long> getBulkFollowersIDs(T ident) throws TwitterException {
    return getBulkFollowersIDs(ident, -1);
  }

  @Override
  public abstract <T> List<String> getBulkFriendsList(T ident, int maxElements, boolean skipStatus, boolean includeUserEntities) throws TwitterException;

  @Override
  public <T> List<String> getBulkFriendsList(T ident) throws TwitterException {
    return getBulkFriendsList(ident, -1, false, true);
  }

  @Override
  public abstract <T> List<String> getBulkFollowersList(T ident, int maxElements, boolean skipStatus, boolean includeUserEntities) throws TwitterException;

  @Override
  public <T> List<String> getBulkFollowersList(T ident) throws TwitterException {
    return getBulkFollowersList(ident, -1, false, true);
  }

  // Bulk List Statuses:

  @Override
  public abstract <T> List<String> getBulkUserListStatuses(T ident, String slug, long initSinceId, long initMaxId, int maxElements) throws TwitterException;

  @Override
  public <T> List<String> getBulkUserListStatuses(T ident, String slug) throws TwitterException {
    return getBulkUserListStatuses(ident, slug, -1, -1, -1);
  }

  /*
   * Lists:
   */

  @Override
  public abstract <T> List<String> getBulkUserListMemberships(T ident, int maxElements) throws TwitterException;

  @Override
  public <T> List<String> getBulkUserListMemberships(T ident) throws TwitterException {
    return getBulkUserListMemberships(ident, -1);
  }

  @Override
  public abstract <T> List<String> getBulkUserListSubscribers(T ident, String slug, int maxElements) throws TwitterException;

  @Override
  public <T> List<String> getBulkUserListSubscribers(T ident, String slug) throws TwitterException {
    return getBulkUserListSubscribers(ident, slug, -1);
  }

  @Override
  public abstract List<String> getBulkUserListMembers(long listId, int maxElements) throws TwitterException;

  @Override
  public List<String> getBulkUserListMembers(long listId) throws TwitterException {
    return getBulkUserListMembers(listId, -1);
  }

  @Override
  public abstract <T> List<String> getBulkUserListSubscriptions(T ident, int maxElements) throws TwitterException;

  @Override
  public <T> List<String> getBulkUserListSubscriptions(T ident) throws TwitterException {
    return getBulkUserListSubscriptions(ident, -1);
  }

  @Override
  public abstract <T> List<String> getBulkUserListsOwnerships(T ident, int maxElements) throws TwitterException;

  @Override
  public <T> List<String> getBulkUserListsOwnerships(T ident) throws TwitterException {
    return getBulkUserListsOwnerships(ident, -1);
  }

  /*
   * Search
   */

  @Override
  public abstract List<String> getBulkSearchUsers(String query, int maxElements) throws TwitterException;

  @Override
  public List<String> getBulkSearchUsers(String query) throws TwitterException {
    return getBulkSearchUsers(query, -1);
  }

  @Override
  public List<String> getBulkSearchResults(String query, int maxElements) throws TwitterException {
    Query q = new Query();
    q.setQuery(query);
    q.setResultType(ResultType.mixed);
    q.setCount(100);
    return getBulkSearchResults(q, maxElements);
  }

  @Override
  public List<String> getBulkSearchResults(String query) throws TwitterException {
    return getBulkSearchResults(query, -1);
  }

  @Override
  public abstract List<String> getBulkSearchResults(Query query, int maxElements) throws TwitterException;

  @Override
  public List<String> getBulkSearchResults(Query query) throws TwitterException {
    return getBulkSearchResults(query, -1);
  }

  /*
   * TimelinesResources
   */
  @Override
  public abstract <T> List<String> getUserTimelineJSON(T ident, Paging paging) throws TwitterException;

  @Override
  public ResponseList<Status> getUserTimeline(String screenName) throws TwitterException {
    return newStatusResponseList(getUserTimelineJSON(screenName, new Paging()));
  }

  @Override
  public ResponseList<Status> getUserTimeline(String screenName, Paging paging) throws TwitterException {
    return newStatusResponseList(getUserTimelineJSON(screenName, paging));
  }

  @Override
  public ResponseList<Status> getUserTimeline(long userId) throws TwitterException {
    return newStatusResponseList(getUserTimelineJSON(userId, new Paging()));
  }

  @Override
  public ResponseList<Status> getUserTimeline(long userId, Paging paging) throws TwitterException {
    return newStatusResponseList(getUserTimelineJSON(userId, paging));
  }

  /*
   * Unsupported TimelinesResources:
   */

  @Override
  @Deprecated
  public ResponseList<Status> getMentionsTimeline() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getMentionsTimeline(Paging paging) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getUserTimeline() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getUserTimeline(Paging paging) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getHomeTimeline() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getHomeTimeline(Paging paging) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getRetweetsOfMe(Paging paging) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  /*
   * TweetsResources
   */

  @Override
  public ResponseList<Status> getRetweets(long statusId) throws TwitterException {
    return newStatusResponseList(getRetweetsJSON(statusId));
  }

  @Override
  public IDs getRetweeterIds(long statusId, int count, long cursor) throws TwitterException {
    return newIDs(getRetweeterIdsJSON(statusId, count, cursor));
  }

  @Override
  public IDs getRetweeterIds(long statusId, long cursor) throws TwitterException {
    return newIDs(getRetweeterIdsJSON(statusId, 100, cursor));
  }

  @Override
  public abstract List<String> getRetweetsJSON(long statusId) throws TwitterException;

  @Override
  public abstract String showStatusJSON(long id) throws TwitterException;

  @Override
  public Status showStatus(long id) throws TwitterException {
    return TwitterObjectFactory.createStatus(showStatusJSON(id));
  }

  @Override
  public ResponseList<Status> lookup(long... ids) throws TwitterException {
    return newStatusResponseList(new ArrayList<String>(lookupJSON(ids).values()));
  }

  /*
   * Unsupported TweetsResources:
   */

  @Override
  @Deprecated
  public Status destroyStatus(long statusId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status updateStatus(String status) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status updateStatus(StatusUpdate latestStatus) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status retweetStatus(long statusId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public OEmbed getOEmbed(OEmbedRequest req) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UploadedMedia uploadMedia(File mediaFile) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  /*
   * SearchResource
   */

  @Override
  public abstract String searchJSON(Query query) throws TwitterException;

  @Override
  public abstract List<String> searchUsersJSON(String query, int page) throws TwitterException;

  @Override
  public QueryResult search(Query query) throws TwitterException {
    return newQueryResult(searchJSON(query));
  }

  @Override
  public ResponseList<User> searchUsers(String query, int page) throws TwitterException {
    return newUserResponseList(searchUsersJSON(query, page));
  }

  /*
   * FriendsFollowersResources
   */

  @Override
  public IDs getFriendsIDs(long userId, long cursor) throws TwitterException {
    return newIDs(getFriendsIDsJSON(userId, cursor, 5000));
  }

  @Override
  public IDs getFriendsIDs(long userId, long cursor, int count) throws TwitterException {
    return newIDs(getFriendsIDsJSON(userId, cursor, count));
  }

  @Override
  public IDs getFriendsIDs(String screenName, long cursor) throws TwitterException {
    return newIDs(getFriendsIDsJSON(screenName, cursor, 5000));
  }

  @Override
  public IDs getFriendsIDs(String screenName, long cursor, int count) throws TwitterException {
    return newIDs(getFriendsIDsJSON(screenName, cursor, count));
  }

  @Override
  public IDs getFollowersIDs(long userId, long cursor) throws TwitterException {
    return newIDs(getFollowersIDsJSON(userId, cursor, 5000));
  }

  @Override
  public IDs getFollowersIDs(long userId, long cursor, int count) throws TwitterException {
    return newIDs(getFollowersIDsJSON(userId, cursor, count));
  }

  @Override
  public IDs getFollowersIDs(String screenName, long cursor) throws TwitterException {
    return newIDs(getFollowersIDsJSON(screenName, cursor, 5000));
  }

  @Override
  public IDs getFollowersIDs(String screenName, long cursor, int count) throws TwitterException {
    return newIDs(getFollowersIDsJSON(screenName, cursor, count));
  }

  @Override
  public Relationship showFriendship(long sourceId, long targetId) throws TwitterException {
    return TwitterObjectFactory.createRelationship(getFriendshipJSON(sourceId, targetId));
  }

  @Override
  public Relationship showFriendship(String sourceScreenName, String targetScreenName) throws TwitterException {
    return TwitterObjectFactory.createRelationship(getFriendshipJSON(sourceScreenName, targetScreenName));
  }

  @Override
  public PagableResponseList<User> getFriendsList(long userId, long cursor) throws TwitterException {
    return newPagableUser(getFriendsListJSON(userId, cursor, 200, false, true));
  }

  @Override
  public PagableResponseList<User> getFriendsList(long userId, long cursor, int count) throws TwitterException {
    return newPagableUser(getFriendsListJSON(userId, cursor, 200, false, true));
  }

  @Override
  public PagableResponseList<User> getFriendsList(long userId, long cursor, int count, boolean skipStatus, boolean includeUserEntities) throws TwitterException {
    return newPagableUser(getFriendsListJSON(userId, cursor, count, skipStatus, includeUserEntities));
  }

  @Override
  public PagableResponseList<User> getFriendsList(String screenName, long cursor) throws TwitterException {
    return newPagableUser(getFriendsListJSON(screenName, cursor, 200, false, true));
  }

  @Override
  public PagableResponseList<User> getFriendsList(String screenName, long cursor, int count) throws TwitterException {
    return newPagableUser(getFriendsListJSON(screenName, cursor, count, false, true));
  }

  @Override
  public PagableResponseList<User> getFriendsList(String screenName, long cursor, int count, boolean skipStatus, boolean includeUserEntities)
      throws TwitterException {
    return newPagableUser(getFriendsListJSON(screenName, cursor, count, skipStatus, includeUserEntities));
  }


  @Override
  public PagableResponseList<User> getFollowersList(long userId, long cursor) throws TwitterException {
    return newPagableUser(getFollowersListJSON(userId, cursor, 200, false, true));
  }

  @Override
  public PagableResponseList<User> getFollowersList(long userId, long cursor, int count) throws TwitterException {
    return newPagableUser(getFollowersListJSON(userId, cursor, 200, false, true));
  }

  @Override
  public PagableResponseList<User> getFollowersList(long userId, long cursor, int count, boolean skipStatus, boolean includeUserEntities)
      throws TwitterException {
    return newPagableUser(getFollowersListJSON(userId, cursor, count, skipStatus, includeUserEntities));
  }

  @Override
  public PagableResponseList<User> getFollowersList(String screenName, long cursor) throws TwitterException {
    return newPagableUser(getFollowersListJSON(screenName, cursor, 200, false, true));
  }

  @Override
  public PagableResponseList<User> getFollowersList(String screenName, long cursor, int count) throws TwitterException {
    return newPagableUser(getFollowersListJSON(screenName, cursor, count, false, true));
  }

  @Override
  public PagableResponseList<User> getFollowersList(String screenName, long cursor, int count, boolean skipStatus, boolean includeUserEntities)
      throws TwitterException {
    return newPagableUser(getFollowersListJSON(screenName, cursor, count, skipStatus, includeUserEntities));
  }

  /*
   * FriendsFollowersResources
   */

  @Override
  public abstract <T> String getFriendshipJSON(T sourceIdent, T targetIdent) throws TwitterException;

  @Override
  public String showFriendshipJSON(long sourceId, long targetId) throws TwitterException {
    return getFriendshipJSON(sourceId, targetId);
  }

  @Override
  public String showFriendshipJSON(String sourceScreenName, String targetScreenName) throws TwitterException {
    return getFriendshipJSON(sourceScreenName, targetScreenName);
  }

  /*
   * Unsupported FriendsFollowersResources
   */

  @Override
  @Deprecated
  public User createFriendship(long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createFriendship(String screenName) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createFriendship(long userId, boolean follow) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createFriendship(String screenName, boolean follow) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyFriendship(long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyFriendship(String screenName) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Relationship updateFriendship(long userId, boolean enableDeviceNotification, boolean retweets) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Relationship updateFriendship(String screenName, boolean enableDeviceNotification, boolean retweets) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Friendship> lookupFriendships(long... ids) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Friendship> lookupFriendships(String... screenNames) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getIncomingFriendships(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getOutgoingFriendships(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getFriendsIDs(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getFollowersIDs(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getNoRetweetsFriendships() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  /*
   * UsersResources
   */

  @Override
  public ResponseList<User> lookupUsers(long... ids) throws TwitterException {
    return newUserResponseList(lookupUsersJSON(ids));
  }

  @Override
  public ResponseList<User> lookupUsers(String... screenNames) throws TwitterException {
    return newUserResponseList(lookupUsersJSON(screenNames));
  }

  @Override
  public User showUser(long userId) throws TwitterException {
    return TwitterObjectFactory.createUser(showUserJSON(userId));
  }

  @Override
  public User showUser(String screenName) throws TwitterException {
    return TwitterObjectFactory.createUser(showUserJSON(screenName));
  }

  @Override
  public ResponseList<User> getContributees(long userId) throws TwitterException {
    return newUserResponseList(getContributeesJSON(userId));
  }

  @Override
  public ResponseList<User> getContributees(String screenName) throws TwitterException {
    return newUserResponseList(getContributeesJSON(screenName));
  }

  @Override
  public ResponseList<User> getContributors(long userId) throws TwitterException {
    return newUserResponseList(getContributorsJSON(userId));
  }

  @Override
  public ResponseList<User> getContributors(String screenName) throws TwitterException {
    return newUserResponseList(getContributorsJSON(screenName));
  }

  /*
   * Unsupported UsersResources
   */

  @Override
  @Deprecated
  public AccountSettings getAccountSettings() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User verifyCredentials() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public AccountSettings updateAccountSettings(Integer trendLocationWoeid, Boolean sleepTimeEnabled, String startSleepTime, String endSleepTime,
      String timeZone, String lang) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfile(String name, String url, String location, String description) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileBackgroundImage(File image, boolean tile) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileBackgroundImage(InputStream image, boolean tile) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileColors(String profileBackgroundColor, String profileTextColor, String profileLinkColor, String profileSidebarFillColor,
      String profileSidebarBorderColor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileImage(File image) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User updateProfileImage(InputStream image) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<User> getBlocksList() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<User> getBlocksList(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getBlocksIDs() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getBlocksIDs(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createBlock(long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createBlock(String screenName) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyBlock(long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyBlock(String screen_name) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public void removeProfileBanner() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public void updateProfileBanner(File image) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public void updateProfileBanner(InputStream image) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createMute(long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User createMute(String screenName) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyMute(long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public User destroyMute(String screenName) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public IDs getMutesIDs(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<User> getMutesList(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  /*
   * FavoritesResources
   */

  @Override
  public ResponseList<Status> getFavorites(long userId) throws TwitterException {
    return newStatusResponseList(getFavoritesJSON(userId, new Paging()));
  }

  @Override
  public ResponseList<Status> getFavorites(long userId, Paging paging) throws TwitterException {
    return newStatusResponseList(getFavoritesJSON(userId, paging));
  }

  @Override
  public ResponseList<Status> getFavorites(String screenName) throws TwitterException {
    return newStatusResponseList(getFavoritesJSON(screenName, new Paging()));
  }

  @Override
  public ResponseList<Status> getFavorites(String screenName, Paging paging) throws TwitterException {
    return newStatusResponseList(getFavoritesJSON(screenName, paging));
  }

  /*
   * Unsupported FavoritesResources
   */

  @Override
  @Deprecated
  public ResponseList<Status> getFavorites() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<Status> getFavorites(Paging paging) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status createFavorite(long id) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public Status destroyFavorite(long id) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  /*
   * ListsResources
   */

  @Override
  public abstract <T> List<String> getUserListsJSON(T ident, boolean reverse) throws TwitterException;

  @Override
  public ResponseList<UserList> getUserLists(String listOwnerScreenName) throws TwitterException {
    return newUserListResponseList(getUserListsJSON(listOwnerScreenName, false));
  }

  @Override
  public ResponseList<UserList> getUserLists(long listOwnerUserId) throws TwitterException {
    return newUserListResponseList(getUserListsJSON(listOwnerUserId, false));
  }

  @Override
  public ResponseList<UserList> getUserLists(String listOwnerScreenName, boolean reverse) throws TwitterException {
    return newUserListResponseList(getUserListsJSON(listOwnerScreenName, reverse));
  }

  @Override
  public List<String> getUserListsJSON(String listOwnerScreenName, boolean reverse) throws TwitterException {
    return getUserListsJSON(listOwnerScreenName, reverse);
  }

  @Override
  public ResponseList<UserList> getUserLists(long listOwnerUserId, boolean reverse) throws TwitterException {
    return newUserListResponseList(getUserListsJSON(listOwnerUserId, reverse));
  }

  @Override
  public List<String> getUserListsJSON(long listOwnerUserId, boolean reverse) throws TwitterException {
    return getUserListsJSON(listOwnerUserId, reverse);
  }


  @Override
  public ResponseList<Status> getUserListStatuses(long listId, Paging paging) throws TwitterException {
    return newStatusResponseList(getUserListStatusesJSON(listId, paging));
  }

  @Override
  public ResponseList<Status> getUserListStatuses(long ownerId, String slug, Paging paging) throws TwitterException {
    return newStatusResponseList(getUserListStatusesJSON(ownerId, slug, paging));
  }

  @Override
  public ResponseList<Status> getUserListStatuses(String ownerScreenName, String slug, Paging paging) throws TwitterException {
    return newStatusResponseList(getUserListStatusesJSON(ownerScreenName, slug, paging));
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(long listMemberId, long cursor) throws TwitterException {
    return newPagableUserList(getUserListMembershipsJSON(listMemberId, 20, cursor, false));
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, long cursor) throws TwitterException {
    return newPagableUserList(getUserListMembershipsJSON(listMemberScreenName, 20, cursor, false));
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(long listMemberId, int count, long cursor) throws TwitterException {
    return newPagableUserList(getUserListMembershipsJSON(listMemberId, count, cursor, false));
  }

  @Override
  public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, int count, long cursor) throws TwitterException {
    return newPagableUserList(getUserListMembershipsJSON(listMemberScreenName, count, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long listId, int count, long cursor, boolean skipStatus) throws TwitterException {
    return newPagableUser(getUserListSubscribersJSON(listId, count, cursor, skipStatus));
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long listId, int count, long cursor) throws TwitterException {
    return getUserListSubscribers(listId, count, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long listId, long cursor) throws TwitterException {
    return getUserListSubscribers(listId, 5000, cursor, false);
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, long cursor) throws TwitterException {
    return newPagableUser(getUserListSubscribersJSON(ownerId, slug, 5000, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, int count, long cursor) throws TwitterException {
    return newPagableUser(getUserListSubscribersJSON(ownerId, slug, count, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(String ownerScreenName, String slug, long cursor) throws TwitterException {
    return newPagableUser(getUserListSubscribersJSON(ownerScreenName, slug, 5000, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(String ownerScreenName, String slug, int count, long cursor) throws TwitterException {
    return newPagableUser(getUserListSubscribersJSON(ownerScreenName, slug, count, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
    return newPagableUser(getUserListSubscribersJSON(ownerId, slug, count, cursor, skipStatus));
  }

  @Override
  public PagableResponseList<User> getUserListSubscribers(String ownerScreenName, String slug, int count, long cursor, boolean skipStatus)
      throws TwitterException {
    return newPagableUser(getUserListSubscribersJSON(ownerScreenName, slug, count, cursor, skipStatus));
  }

  @Override
  public abstract <T> String getUserListSubscriptionJSON(T ident, String slug, long userId) throws TwitterException;

  @Override
  public User showUserListSubscription(long listId, long userId) throws TwitterException {
    return TwitterObjectFactory.createUser(showUserListSubscriptionJSON(listId, userId));
  }

  @Override
  public User showUserListSubscription(long ownerId, String slug, long userId) throws TwitterException {
    return TwitterObjectFactory.createUser(getUserListSubscriptionJSON(ownerId, slug, userId));
  }

  @Override
  public String showUserListSubscriptionJSON(long ownerId, String slug, long userId) throws TwitterException {
    return getUserListSubscriptionJSON(ownerId, slug, userId);
  }

  @Override
  public User showUserListSubscription(String ownerScreenName, String slug, long userId) throws TwitterException {
    return TwitterObjectFactory.createUser(getUserListSubscriptionJSON(ownerScreenName, slug, userId));
  }

  @Override
  public String showUserListSubscriptionJSON(String ownerScreenName, String slug, long userId) throws TwitterException {
    return getUserListSubscriptionJSON(ownerScreenName, slug, userId);
  }

  @Override
  public abstract <T> String getUserListMembershipJSON(T ident, String slug, long userId) throws TwitterException;

  @Override
  public User showUserListMembership(long ownerId, String slug, long userId) throws TwitterException {
    return TwitterObjectFactory.createUser(getUserListMembershipJSON(ownerId, slug, userId));
  }

  @Override
  public String showUserListMembershipJSON(long ownerId, String slug, long userId) throws TwitterException {
    return getUserListMembershipJSON(ownerId, slug, userId);
  }

  @Override
  public User showUserListMembership(long listId, long userId) throws TwitterException {
    return TwitterObjectFactory.createUser(showUserListMembershipJSON(listId, userId));
  }

  @Override
  public User showUserListMembership(String ownerScreenName, String slug, long userId) throws TwitterException {
    return TwitterObjectFactory.createUser(getUserListMembershipJSON(ownerScreenName, slug, userId));
  }

  @Override
  public String showUserListMembershipJSON(String ownerScreenName, String slug, long userId) throws TwitterException {
    return getUserListMembershipJSON(ownerScreenName, slug, userId);
  }


  @Override
  public PagableResponseList<User> getUserListMembers(long listId, int count, long cursor, boolean skipStatus) throws TwitterException {
    return newPagableUser(getUserListMembersJSON(listId, count, cursor, skipStatus));
  }

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
    return newPagableUser(getUserListMembersJSON(ownerId, slug, 5000, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListMembers(long ownerId, String slug, int count, long cursor) throws TwitterException {
    return newPagableUser(getUserListMembersJSON(ownerId, slug, count, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListMembers(String ownerScreenName, String slug, long cursor) throws TwitterException {
    return newPagableUser(getUserListMembersJSON(ownerScreenName, slug, 5000, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListMembers(String ownerScreenName, String slug, int count, long cursor) throws TwitterException {
    return newPagableUser(getUserListMembersJSON(ownerScreenName, slug, count, cursor, false));
  }

  @Override
  public PagableResponseList<User> getUserListMembers(long ownerId, String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
    return newPagableUser(getUserListMembersJSON(ownerId, slug, count, cursor, skipStatus));
  }

  @Override
  public PagableResponseList<User> getUserListMembers(String ownerScreenName, String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
    return newPagableUser(getUserListMembersJSON(ownerScreenName, slug, count, cursor, skipStatus));
  }

  @Override
  public abstract String showUserListJSON(long listId) throws TwitterException;

  @Override
  public UserList showUserList(long listId) throws TwitterException {
    return TwitterObjectFactory.createUserList(showUserListJSON(listId));
  }

  @Override
  public UserList showUserList(long ownerId, String slug) throws TwitterException {
    return TwitterObjectFactory.createUserList(showUserListJSON(ownerId, slug));
  }

  @Override
  public UserList showUserList(String ownerScreenName, String slug) throws TwitterException {
    return TwitterObjectFactory.createUserList(showUserListJSON(ownerScreenName, slug));
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(String listSubscriberScreenName, int count, long cursor) throws TwitterException {
    return newPagableUserList(getUserListSubscriptionsJSON(listSubscriberScreenName, count, cursor));
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(long listSubscriberId, int count, long cursor) throws TwitterException {
    return newPagableUserList(getUserListSubscriptionsJSON(listSubscriberId, count, cursor));
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(String listSubscriberScreenName, long cursor) throws TwitterException {
    return newPagableUserList(getUserListSubscriptionsJSON(listSubscriberScreenName, 1000, cursor));
  }

  @Override
  public PagableResponseList<UserList> getUserListSubscriptions(long listSubscriberId, long cursor) throws TwitterException {
    return newPagableUserList(getUserListSubscriptionsJSON(listSubscriberId, 1000, cursor));
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(String listOwnerScreenName, int count, long cursor) throws TwitterException {
    return newPagableUserList(getUserListsOwnershipsJSON(listOwnerScreenName, count, cursor));
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(long listOwnerId, int count, long cursor) throws TwitterException {
    return newPagableUserList(getUserListsOwnershipsJSON(listOwnerId, count, cursor));
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(String listOwnerScreenName, long cursor) throws TwitterException {
    return newPagableUserList(getUserListsOwnershipsJSON(listOwnerScreenName, 1000, cursor));
  }

  @Override
  public PagableResponseList<UserList> getUserListsOwnerships(long listOwnerId, long cursor) throws TwitterException {
    return newPagableUserList(getUserListsOwnershipsJSON(listOwnerId, 1000, cursor));
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
  public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, long cursor, boolean filterToOwnedLists) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(long listMemberId, long cursor, boolean filterToOwnedLists) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, int count, long cursor, boolean filterToOwnedLists)
      throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(long listMemberId, int count, long cursor, boolean filterToOwnedLists) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList> getUserListMemberships(long cursor) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(long ownerId, String slug, long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(long ownerId, String slug) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMembers(long ownerId, long[] userIds) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMembers(long ownerId, String[] userIds) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(String ownerScreenName, String slug, long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListSubscription(long ownerId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListSubscription(long ownerId, String slug) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListSubscription(String ownerScreenName, String slug) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListSubscription(long ownerId, String slug) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListSubscription(String ownerScreenName, String slug) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMember(long ownerId, long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(long ownerId, String... userIds) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(long ownerId, long... userIds) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(long ownerId, String slug, long... userIds) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(String ownerScreenName, String slug, long... userIds) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(long ownerId, String slug, String... screenNames) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMembers(String ownerScreenName, String slug, String... screenNames) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMembers(String ownerScreenName, String slug, String[] screenNames) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListMember(long ownerId, long userId) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMember(long ownerId, String slug, long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserListMember(String ownerScreenName, String slug, long userId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserList(long ownerId) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserList(long ownerId, String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserList(String ownerScreenName, String slug) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList destroyUserListSubscription(long id) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList updateUserList(long ownerId, String slug, String newListName, boolean isPublicList, String newDescription) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList updateUserList(String ownerScreenName, String slug, String newListName, boolean isPublicList, String newDescription) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList createUserList(String listName, boolean isPublicList, String description) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UserList updateUserList(long id, String listName, boolean isPublicList, String description) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }


  /*
   * PlacesGeoResources
   */

  @Override
  public Place getGeoDetails(String placeId) throws TwitterException {
    return TwitterObjectFactory.createPlace(getGeoDetailsJSON(placeId));
  }

  @Override
  public ResponseList<Place> reverseGeoCode(GeoQuery query) throws TwitterException {
    return newPlaceResponseList(reverseGeoCodeJSON(query));
  }

  @Override
  public ResponseList<Place> searchPlaces(GeoQuery query) throws TwitterException {
    return newPlaceResponseList(searchPlacesJSON(query));
  }

  @Override
  public ResponseList<Place> getSimilarPlaces(GeoLocation location, String name, String containedWithin, String streetAddress) throws TwitterException {
    return newPlaceResponseList(getSimilarPlacesJSON(location, name, containedWithin, streetAddress));
  }

  /*
   * TrendsResources
   */

  @Override
  public abstract String getPlaceTrendsJSON(int woeid) throws TwitterException;

  @Override
  public Trends getPlaceTrends(int woeid) throws TwitterException {
    return TwitterObjectFactory.createTrends(getPlaceTrendsJSON(woeid));
  }

  @Override
  public ResponseList<Location> getClosestTrends(GeoLocation location) throws TwitterException {
    return newLocationResponseList(getClosestTrendsJSON(location));
  }

  @Override
  public ResponseList<Location> getAvailableTrends() throws TwitterException {
    return newLocationResponseList(getAvailableTrendsJSON());
  }

  /*
   * HelpResources
   */

  @Override
  @Deprecated
  public TwitterAPIConfiguration getAPIConfiguration() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public ResponseList<HelpResources.Language> getLanguages() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public String getPrivacyPolicy() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public String getTermsOfService() throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public UploadedMedia uploadMedia(String fileName, InputStream media) throws TwitterException {
    throw new TwitterException(TwitterResources.UNSUPPORTED_METHOD);
  }

}
