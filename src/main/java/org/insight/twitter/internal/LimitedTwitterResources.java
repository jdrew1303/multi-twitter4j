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
 * 
 */
public abstract class LimitedTwitterResources implements
TimelinesResources,
TweetsResources,
SearchResource,
FriendsFollowersResources,
UsersResources, // Partially Implemented - User Specifics (Profile modification etc. disabled - but, could be handy for bot management)
FavoritesResources, // Partially implemented
ListsResources,
PlacesGeoResources,
TrendsResources,
HelpResources // Rate Limits Are Handled internally. NewMultiTwitter makes a fake Aggregated RateLimit Status

//Authenticating users only Resources Not Implemented:
//*SpamReportingResource,
//*SavedSearchesResources,
//*SuggestedUsersResources,
//*DirectMessagesResources,

// Internal:
//OAuthSupport,
//OAuth2Support,
//TwitterBase
{

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
	 *  Now for the Unsupported methods, these will just throw exceptions.
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
	public ResponseList<Status> getMentionsTimeline(Paging paging) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Status> getUserTimeline() throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Status> getUserTimeline(Paging paging) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Status> getHomeTimeline() throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Status> getHomeTimeline(Paging paging) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Status> getRetweetsOfMe(Paging paging) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	/* 
	 * Unsupported TweetsResources
	 */

	@Override
	@Deprecated
	public Status destroyStatus(long statusId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public Status updateStatus(String status) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public Status updateStatus(StatusUpdate latestStatus) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public Status retweetStatus(long statusId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public OEmbed getOEmbed(OEmbedRequest req) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UploadedMedia uploadMedia(File mediaFile) throws TwitterException {
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
	public User createFriendship(long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User createFriendship(String screenName) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User createFriendship(long userId, boolean follow) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User createFriendship(String screenName, boolean follow)	throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User destroyFriendship(long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User destroyFriendship(String screenName) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public Relationship updateFriendship(long userId, boolean enableDeviceNotification, boolean retweets) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public Relationship updateFriendship(String screenName, boolean enableDeviceNotification, boolean retweets) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Friendship> lookupFriendships(long[] ids) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public ResponseList<Friendship> lookupFriendships(String[] screenNames) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public IDs getIncomingFriendships(long cursor) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public IDs getOutgoingFriendships(long cursor) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public IDs getFriendsIDs(long cursor) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public IDs getFollowersIDs(long cursor) throws TwitterException {
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
	public AccountSettings updateAccountSettings(Integer trendLocationWoeid, Boolean sleepTimeEnabled, String startSleepTime, String endSleepTime, String timeZone, String lang)	throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User updateProfile(String name, String url, String location, String description) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User updateProfileBackgroundImage(File image, boolean tile) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User updateProfileBackgroundImage(InputStream image, boolean tile) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User updateProfileColors(String profileBackgroundColor, String profileTextColor, String profileLinkColor,	String profileSidebarFillColor, String profileSidebarBorderColor) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User updateProfileImage(File image) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User updateProfileImage(InputStream image) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public PagableResponseList<User> getBlocksList() throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public PagableResponseList<User> getBlocksList(long cursor) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public IDs getBlocksIDs() throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public IDs getBlocksIDs(long cursor) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User createBlock(long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User createBlock(String screenName) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User destroyBlock(long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User destroyBlock(String screen_name) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public void removeProfileBanner() throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public void updateProfileBanner(File image) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public void updateProfileBanner(InputStream image) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User createMute(long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User createMute(String screenName) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User destroyMute(long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public User destroyMute(String screenName) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public IDs getMutesIDs(long cursor) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public PagableResponseList<User> getMutesList(long cursor)	throws TwitterException {
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
	public ResponseList<Status> getFavorites(Paging paging) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public Status createFavorite(long id) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public Status destroyFavorite(long id) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	/*
	 * Unsupported ListsResources
	 */


	@Override
	@Deprecated
	public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, long cursor, boolean filterToOwnedLists) throws TwitterException{
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public PagableResponseList<UserList> getUserListMemberships(long listMemberId, long cursor, boolean filterToOwnedLists) throws TwitterException{
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public PagableResponseList<UserList> getUserListMemberships(long cursor) throws TwitterException{
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListMember(long ownerId, String slug, long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListMember(long ownerId, String slug) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListMembers(long ownerId, long[] userIds) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListMembers(long ownerId, String[] userIds) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListMember(String ownerScreenName, String slug, long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListSubscription(long ownerId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListSubscription(long ownerId, String slug) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListSubscription(String ownerScreenName, String slug) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListSubscription(long ownerId, String slug)	throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListSubscription(String ownerScreenName,	String slug) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListMember(long ownerId, long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListMembers(long ownerId, String[] userIds) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListMembers(long ownerId, long[] userIds) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListMembers(long ownerId, String slug, long[] userIds) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListMembers(String ownerScreenName, String slug, long[] userIds) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListMembers(long ownerId, String slug, String[] screenNames) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserListMembers(String ownerScreenName, String slug, String[] screenNames) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListMembers(String ownerScreenName, String slug, String[] screenNames) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListMember(long ownerId, long userId) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
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
	public UserList destroyUserList(String ownerScreenName, String slug)	throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList destroyUserListSubscription(long id)	throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList updateUserList(long ownerId, String slug, String newListName, boolean isPublicList, String newDescription) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList updateUserList(String ownerScreenName, String slug, String newListName, boolean isPublicList, String newDescription) 	throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList createUserList(String listName, boolean isPublicList, String description) throws TwitterException {
		throw new TwitterException(UNSUPPORTED_METHOD);
	}

	@Override
	@Deprecated
	public UserList updateUserList(long id, String listName, boolean isPublicList, String description) throws TwitterException {
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
	public TwitterAPIConfiguration getAPIConfiguration()	throws TwitterException {
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
