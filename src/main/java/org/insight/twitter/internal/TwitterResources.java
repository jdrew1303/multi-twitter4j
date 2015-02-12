package org.insight.twitter.internal;

import java.io.File;
import java.io.InputStream;

import twitter4j.AccountSettings;
import twitter4j.Friendship;
import twitter4j.IDs;
import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
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
 * This implements the unsupported methods, throwing exceptions, and leaves the valid methods to be implemented by NewMultiTwitter This is class is mostly to
 * make the implementing MultiTwitter class a bit easier to work with. Unimplemented methods not suitable for bots.
 */
public abstract class TwitterResources implements TimelinesResources, TweetsResources, SearchResource, FriendsFollowersResources, UsersResources,
// ** Partially implemented:
    FavoritesResources, ListsResources, PlacesGeoResources, TrendsResources,
    // ** Not Implemented: Authenticating users only:
    // SpamReportingResource,
    // SavedSearchesResources,
    // SuggestedUsersResources,
    // DirectMessagesResources,
    // OAuthSupport,
    // OAuth2Support,
    // TwitterBase
    // ** Rate Limits Are Handled internally:
    HelpResources {

  private static final String UNSUPPORTED_METHOD = "This API call cannot be distributed between bots!";

  /*
   * Interfaces
   */
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
   * TimelinesResources
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
   * Unimplemented TimelinesResources:
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
   * Unimplemented TweetsResources:
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
   * Unsupported ListsResources
   */

  @Override
  @Deprecated
  public PagableResponseList<UserList>
      getUserListMemberships(final String listMemberScreenName, final long cursor, final boolean filterToOwnedLists) throws TwitterException {
    throw new TwitterException(UNSUPPORTED_METHOD);
  }

  @Override
  @Deprecated
  public PagableResponseList<UserList>
      getUserListMemberships(final long listMemberId, final long cursor, final boolean filterToOwnedLists) throws TwitterException {
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
  public
      UserList
      updateUserList(final long ownerId, final String slug, final String newListName, final boolean isPublicList, final String newDescription)
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

}
