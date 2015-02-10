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
 * This implements the unsupported methods, throwing exceptions, and leaves the valid methods to be implemented by NewMultiTwitter
 * This is class is mostly to make the implementing NewMultiTwitter class a bit easier to work with.
 * Unimplemented methods are methods that can be used / affect authenticating users, not suitable for groups of bots.
 */
public abstract class TwitterResources implements TimelinesResources,
        TweetsResources, SearchResource,
        FriendsFollowersResources,
        // **Partially Implemented - User Specifics (Profile modification etc.
        // disabled - but, could be handy for bot management)
        UsersResources,
        // **Partially implemented:
        FavoritesResources, ListsResources, PlacesGeoResources,
        TrendsResources,
        // **Authenticating users only Resources Not Implemented:
        // SpamReportingResource,
        // SavedSearchesResources,
        // SuggestedUsersResources,
        // DirectMessagesResources,
        // **Internal:
        // OAuthSupport,
        // OAuth2Support,
        // TwitterBase
        // **Rate Limits Are Handled internally. MultiTwitter makes a fake
        // Aggregated RateLimit Status:
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
     * Throw exceptions for Unsupported Methods
     */

    /*
     * Unsupported TimelinesResources
     */

    @Override
    @Deprecated
    public ResponseList<Status> getMentionsTimeline() throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Status> getMentionsTimeline(final Paging paging)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Status> getUserTimeline() throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Status> getUserTimeline(final Paging paging)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Status> getHomeTimeline() throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Status> getHomeTimeline(final Paging paging)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Status> getRetweetsOfMe(final Paging paging)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    /*
     * Unsupported TweetsResources
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
    public Status updateStatus(final StatusUpdate latestStatus)
            throws TwitterException {
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
    public UploadedMedia uploadMedia(final File mediaFile)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    /*
     * Unsupported SearchResource
     */

    //

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
    public User createFriendship(final String screenName)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User createFriendship(final long userId, final boolean follow)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User createFriendship(final String screenName, final boolean follow)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User destroyFriendship(final long userId) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User destroyFriendship(final String screenName)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public Relationship updateFriendship(final long userId,
            final boolean enableDeviceNotification, final boolean retweets)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public Relationship updateFriendship(final String screenName,
            final boolean enableDeviceNotification, final boolean retweets)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Friendship> lookupFriendships(final long[] ids)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public ResponseList<Friendship> lookupFriendships(final String[] screenNames)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public IDs getIncomingFriendships(final long cursor)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public IDs getOutgoingFriendships(final long cursor)
            throws TwitterException {
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
    public AccountSettings updateAccountSettings(
            final Integer trendLocationWoeid, final Boolean sleepTimeEnabled,
            final String startSleepTime, final String endSleepTime,
            final String timeZone, final String lang) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User updateProfile(final String name, final String url,
            final String location, final String description)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User updateProfileBackgroundImage(final File image,
            final boolean tile) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User updateProfileBackgroundImage(final InputStream image,
            final boolean tile) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User updateProfileColors(final String profileBackgroundColor,
            final String profileTextColor, final String profileLinkColor,
            final String profileSidebarFillColor,
            final String profileSidebarBorderColor) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User updateProfileImage(final File image) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public User updateProfileImage(final InputStream image)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public PagableResponseList<User> getBlocksList() throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public PagableResponseList<User> getBlocksList(final long cursor)
            throws TwitterException {
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
    public void updateProfileBanner(final InputStream image)
            throws TwitterException {
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
    public PagableResponseList<User> getMutesList(final long cursor)
            throws TwitterException {
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
    public ResponseList<Status> getFavorites(final Paging paging)
            throws TwitterException {
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
    public PagableResponseList<UserList> getUserListMemberships(
            final String listMemberScreenName, final long cursor,
            final boolean filterToOwnedLists) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public PagableResponseList<UserList> getUserListMemberships(
            final long listMemberId, final long cursor,
            final boolean filterToOwnedLists) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public PagableResponseList<UserList> getUserListMemberships(
            final long cursor) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListMember(final long ownerId,
            final String slug, final long userId) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListMember(final long ownerId, final String slug)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListMembers(final long ownerId,
            final long[] userIds) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListMembers(final long ownerId,
            final String[] userIds) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListMember(final String ownerScreenName,
            final String slug, final long userId) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListSubscription(final long ownerId)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListSubscription(final long ownerId,
            final String slug) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListSubscription(final String ownerScreenName,
            final String slug) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListSubscription(final long ownerId,
            final String slug) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListSubscription(final String ownerScreenName,
            String slug) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMember(final long ownerId, final long userId)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMembers(final long ownerId,
            final String[] userIds) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMembers(final long ownerId,
            final long[] userIds) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMembers(final long ownerId,
            final String slug, final long[] userIds) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMembers(final String ownerScreenName,
            final String slug, final long[] userIds) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMembers(final long ownerId,
            final String slug, final String[] screenNames)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMembers(final String ownerScreenName,
            final String slug, final String[] screenNames)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListMembers(final String ownerScreenName,
            final String slug, final String[] screenNames)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListMember(final long ownerId, final long userId)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMember(final long ownerId, final String slug,
            final long userId) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserListMember(final String ownerScreenName,
            final String slug, final long userId) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserList(final long ownerId) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserList(final long ownerId, final String slug)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserList(final String ownerScreenName,
            final String slug) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList destroyUserListSubscription(final long id)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList updateUserList(final long ownerId, final String slug,
            final String newListName, final boolean isPublicList,
            final String newDescription) throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList updateUserList(final String ownerScreenName,
            final String slug, final String newListName,
            final boolean isPublicList, final String newDescription)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList createUserList(final String listName,
            final boolean isPublicList, final String description)
            throws TwitterException {
        throw new TwitterException(UNSUPPORTED_METHOD);
    }

    @Override
    @Deprecated
    public UserList updateUserList(final long id, final String listName,
            final boolean isPublicList, final String description)
            throws TwitterException {
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
    public TwitterAPIConfiguration getAPIConfiguration()
            throws TwitterException {
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
