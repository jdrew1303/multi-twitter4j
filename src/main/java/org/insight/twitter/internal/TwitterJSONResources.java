package org.insight.twitter.internal;

import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterResponse;


/*
 * Boilerplate to get original JSON from Twitter.
 */
public abstract class TwitterJSONResources extends TwitterResources {

	public enum As {
		JSON;
	};

	/*
	 * Get JSON - Only works if calling from the same thread, and immediately after last request!
	 */
	public static String getJSON(TwitterResponse r) {
		return TwitterObjectFactory.getRawJSON(r);
	}

	public static List<String> getJSONList(List<? extends TwitterResponse> l) {
		List<String> objects = new ArrayList<>();
		for (Object r : l) {
			objects.add(TwitterObjectFactory.getRawJSON(r));
		}
		return objects;
	}

	/*
	 * TimelinesResources
	 */

	public <T> List<String> fetchUserTimeline(As json, final T ident, final Paging paging) throws TwitterException {
		return getJSONList(fetchUserTimeline(ident, paging));
	}

	public List<String> getUserTimeline(As json, final String screenName) throws TwitterException {
		return fetchUserTimeline(json, screenName, new Paging());
	}

	public List<String> getUserTimeline(As json, final String screenName, final Paging paging) throws TwitterException {
		return fetchUserTimeline(json, screenName, paging);
	}

	public List<String> getUserTimeline(As json, final long userId) throws TwitterException {
		return fetchUserTimeline(json, userId, new Paging());
	}

	public List<String> getUserTimeline(As json, final long userId, final Paging paging) throws TwitterException {
		return fetchUserTimeline(json, userId, paging);
	}

	/*
	 * TweetsResources
	 */

	public List<String> getRetweets(As json, final long statusId) throws TwitterException {
		return getJSONList(getRetweets(statusId));
	}

	public String getRetweeterIds(As json, final long statusId, final long cursor) throws TwitterException {
		return getJSON(getRetweeterIds(statusId, 100, cursor));
	}

	public String getRetweeterIds(As json, final long statusId, final int count, final long cursor) throws TwitterException {
		return getJSON(getRetweeterIds(statusId, count, cursor));
	}

	public String showStatus(As json, final long id) throws TwitterException {
		return getJSON(showStatus(id));
	}

	public List<String> lookup(As json, final long[] ids) throws TwitterException {
		return getJSONList(lookup(ids));
	}

	/*
	 * SearchResource
	 */

	public String search(As json, final Query query) throws TwitterException {
		return getJSON(search(query));
	}

	/*
	 * FriendsFollowersResources
	 */

	public <T> String fetchFriendsIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
		return getJSON(fetchFriendsIDs(ident, cursor, count));
	}

	public String getFriendsIDs(As json, final long userId, final long cursor) throws TwitterException {
		return fetchFriendsIDs(json, userId, cursor, 5000);
	}

	public String getFriendsIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
		return fetchFriendsIDs(json, userId, cursor, count);
	}

	public String getFriendsIDs(As json, final String screenName, final long cursor) throws TwitterException {
		return fetchFriendsIDs(json, screenName, cursor, 5000);
	}

	public String getFriendsIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
		return fetchFriendsIDs(json, screenName, cursor, count);
	}

	public <T> String fetchFollowersIDs(As json, final T ident, final long cursor, final int count) throws TwitterException {
		return getJSON(fetchFollowersIDs(ident, cursor, count));
	}

	public String getFollowersIDs(As json, final long userId, final long cursor) throws TwitterException {
		return fetchFollowersIDs(json, userId, cursor, 5000);
	}

	public String getFollowersIDs(As json, final long userId, final long cursor, final int count) throws TwitterException {
		return fetchFollowersIDs(json, userId, cursor, count);
	}

	public String getFollowersIDs(As json, final String screenName, final long cursor) throws TwitterException {
		return fetchFollowersIDs(json, screenName, cursor, 5000);
	}

	public String getFollowersIDs(As json, final String screenName, final long cursor, final int count) throws TwitterException {
		return fetchFollowersIDs(json, screenName, cursor, count);
	}

	public <T> String fetchFriendship(As json, final T sourceIdent, final T targetIdent) throws TwitterException {
		return getJSON(fetchFriendship(sourceIdent, targetIdent));
	}

	public String showFriendship(As json, final long sourceId, final long targetId) throws TwitterException {
		return fetchFriendship(json, sourceId, targetId);
	}

	public String showFriendship(As json, final String sourceScreenName, final String targetScreenName) throws TwitterException {
		return fetchFriendship(json, sourceScreenName, targetScreenName);
	}

	public <T> List<String> fetchFriendsList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
			final boolean includeUserEntities) throws TwitterException {
		return getJSONList(fetchFriendsList(ident, cursor, count, skipStatus, includeUserEntities));
	}

	public List<String> getFriendsList(As json, final long userId, final long cursor) throws TwitterException {
		return fetchFriendsList(json, userId, cursor, 200, false, true);
	}

	public List<String> getFriendsList(As json, final long userId, final long cursor, final int count) throws TwitterException {
		return fetchFriendsList(json, userId, cursor, 200, false, true);
	}

	public
	List<String>
	getFriendsList(As json, final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)
			throws TwitterException {
		return fetchFriendsList(json, userId, cursor, count, skipStatus, includeUserEntities);
	}

	public List<String> getFriendsList(As json, final String screenName, final long cursor) throws TwitterException {
		return fetchFriendsList(json, screenName, cursor, 200, false, true);
	}

	public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
		return fetchFriendsList(json, screenName, cursor, count, false, true);
	}

	public List<String> getFriendsList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
			final boolean includeUserEntities) throws TwitterException {
		return fetchFriendsList(json, screenName, cursor, count, skipStatus, includeUserEntities);
	}

	public <T> List<String> fetchFollowersList(As json, final T ident, final long cursor, final int count, final boolean skipStatus,
			final boolean includeUserEntities) throws TwitterException {
		return getJSONList(fetchFollowersList(ident, cursor, count, skipStatus, includeUserEntities));
	}

	public List<String> getFollowersList(As json, final long userId, final long cursor) throws TwitterException {
		return fetchFollowersList(json, userId, cursor, 200, false, true);
	}

	public List<String> getFollowersList(As json, final long userId, final long cursor, final int count) throws TwitterException {
		return fetchFollowersList(json, userId, cursor, 200, false, true);
	}

	public List<String> getFollowersList(As json, final long userId, final long cursor, final int count, final boolean skipStatus,
			final boolean includeUserEntities) throws TwitterException {
		return fetchFollowersList(json, userId, cursor, count, skipStatus, includeUserEntities);
	}

	public List<String> getFollowersList(As json, final String screenName, final long cursor) throws TwitterException {
		return fetchFollowersList(json, screenName, cursor, 200, false, true);
	}

	public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count) throws TwitterException {
		return fetchFollowersList(json, screenName, cursor, count, false, true);
	}

	public List<String> getFollowersList(As json, final String screenName, final long cursor, final int count, final boolean skipStatus,
			final boolean includeUserEntities) throws TwitterException {
		return fetchFollowersList(json, screenName, cursor, count, skipStatus, includeUserEntities);
	}

	/*
	 * UsersResources
	 */

	 public <T> List<String> fetchLookupUsers(As json, final T idents) throws TwitterException {
		return getJSONList(fetchLookupUsers(idents));
	}

	public List<String> lookupUsers(As json, final long[] ids) throws TwitterException {
		return fetchLookupUsers(json, ids);
	}

	public List<String> lookupUsers(As json, final String[] screenNames) throws TwitterException {
		return fetchLookupUsers(json, screenNames);
	}

	public <T> String fetchUser(As json, final T ident) throws TwitterException {
		return getJSON(fetchUser(ident));
	}

	public String showUser(As json, final long userId) throws TwitterException {
		return fetchUser(json, userId);
	}

	public String showUser(As json, final String screenName) throws TwitterException {
		return fetchUser(json, screenName);
	}

	public List<String> searchUsers(As json, final String query, final int page) throws TwitterException {
		return getJSONList(searchUsers(query, page));
	}

	public <T> List<String> fetchContributees(As json, final T ident) throws TwitterException {
		return getJSONList(fetchContributees(ident));
	}

	public List<String> getContributees(As json, final long userId) throws TwitterException {
		return fetchContributees(json, userId);
	}

	public List<String> getContributees(As json, final String screenName) throws TwitterException {
		return fetchContributees(json, screenName);
	}

	public <T> List<String> fetchContributors(As json, final T ident) throws TwitterException {
		return getJSONList(fetchContributees(ident));
	}

	public List<String> getContributors(As json, final long userId) throws TwitterException {
		return fetchContributors(json, userId);
	}

	public List<String> getContributors(As json, final String screenName) throws TwitterException {
		return fetchContributors(json, screenName);
	}

	/*
	 * FavoritesResources
	 */

	public <T> List<String> fetchFavorites(As json, final T ident, final Paging paging) throws TwitterException {
		return getJSONList(fetchFavorites(ident, paging));
	}

	public List<String> getFavorites(As json,final long userId) throws TwitterException {
		return fetchFavorites(json, userId, new Paging());
	}

	public List<String> getFavorites(As json,final long userId, final Paging paging) throws TwitterException {
		return fetchFavorites(json, userId, paging);
	}

	public List<String> getFavorites(As json,final String screenName) throws TwitterException {
		return fetchFavorites(json, screenName, new Paging());
	}

	public List<String> getFavorites(As json,final String screenName, final Paging paging) throws TwitterException {
		return fetchFavorites(json, screenName, paging);
	}

	/*
	 * ListsResources
	 */

	public <T> List<String> fetchUserLists(As json, final T ident) throws TwitterException {
		return getJSONList(fetchUserLists(ident));
	}

	public List<String> getUserLists(As json, final String listOwnerScreenName) throws TwitterException {
		return fetchUserLists(json, listOwnerScreenName);
	}

	public List<String> getUserLists(As json, final long listOwnerUserId) throws TwitterException {
		return fetchUserLists(json, listOwnerUserId);
	}

	public List<String> getUserListStatuses(As json, final long listId, final Paging paging) throws TwitterException {
		return getJSONList(getUserListStatuses(listId, paging));
	}

	public <T> List<String> fetchUserListStatuses(As json, final T ident, final String slug, final Paging paging) throws TwitterException{
		return getJSONList(fetchUserListStatuses(ident, slug, paging));
	}

	public List<String> getUserListStatuses(As json, final long ownerId, final String slug, final Paging paging) throws TwitterException {
		return fetchUserListStatuses(json, ownerId, slug, paging);
	}

	public List<String> getUserListStatuses(As json, final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
		return fetchUserListStatuses(json, ownerScreenName, slug, paging);
	}


	public <T> List<String> fetchUserListMemberships(As json, final T ident, final long cursor) throws TwitterException {
		return getJSONList(fetchUserListMemberships(ident, cursor));
	}

	public List<String> getUserListMemberships(As json, final long listMemberId, final long cursor) throws TwitterException {
		return fetchUserListMemberships(json, listMemberId, cursor);
	}

	public List<String> getUserListMemberships(As json, final String listMemberScreenName, final long cursor) throws TwitterException {
		return fetchUserListMemberships(json, listMemberScreenName, cursor);
	}

	public List<String> getUserListSubscribers(As json, final long listId, final long cursor) throws TwitterException {
		return getJSONList(getUserListSubscribers(listId, cursor));
	}

	public  <T> List<String> fetchUserListSubscribers(As json, final T ident, final String slug, final long cursor) throws TwitterException { 
		return getJSONList(fetchUserListSubscribers(ident, slug, cursor));
	}

	public List<String> getUserListSubscribers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException {
		return fetchUserListSubscribers(json, ownerId, slug, cursor);
	}

	public List<String> getUserListSubscribers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
		return fetchUserListSubscribers(json, ownerScreenName, slug, cursor);
	}

	public  <T> String fetchUserListSubscription(As json, final T ident, final String slug, final long userId) throws TwitterException { 
		return getJSON(fetchUserListSubscription(ident, slug, userId));
	}

	public String showUserListSubscription(As json, final long ownerId, final String slug, final long userId) throws TwitterException {
		return fetchUserListSubscription(json, ownerId, slug, userId);
	}

	public String showUserListSubscription(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException {
		return fetchUserListSubscription(json, ownerScreenName, slug, userId);
	}

	public  <T> String fetchUserListMembership(As json, final T ident, final String slug, final long userId) throws TwitterException { 
		return getJSON(fetchUserListMembership(ident, slug, userId));
	}

	public String showUserListMembership(As json, final long ownerId, final String slug, final long userId) throws TwitterException {
		return fetchUserListMembership(json, ownerId, slug, userId);
	}

	public String showUserListMembership(As json, final String ownerScreenName, final String slug, final long userId) throws TwitterException {
		return fetchUserListMembership(json, ownerScreenName, slug, userId);
	}

	public  <T> List<String> fetchUserListMembers(As json, final T ident, final String slug, final long cursor) throws TwitterException { 
		return getJSONList(fetchUserListMembers(ident, slug, cursor));
	}

	public List<String> getUserListMembers(As json, final long ownerId, final String slug, final long cursor) throws TwitterException {
		return fetchUserListMembers(json, ownerId, slug, cursor);
	}

	public List<String> getUserListMembers(As json, final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
		return fetchUserListMembers(json, ownerScreenName, slug, cursor);
	}

	public  <T> String fetchUserList(As json, final T ident, final String slug) throws TwitterException { 
		return getJSON(fetchUserList(ident, slug));
	}

	public String showUserList(As json, final long ownerId, final String slug) throws TwitterException {
		return fetchUserList(json, ownerId, slug);
	}

	public String showUserList(As json, final String ownerScreenName, final String slug) throws TwitterException {
		return fetchUserList(json, ownerScreenName, slug);
	}

	/*
	 * TODO: Twitter4J Missing getUserListSubscriptions(long userId, cursor...)
	 */	  
	 public  <T> List<String> fetchUserListSubscriptions(As json, final T ident, final long cursor) throws TwitterException { 
		 return getJSONList(fetchUserListSubscriptions(ident, cursor));
	 }

	 public List<String> getUserListSubscriptions(As json, final String listOwnerScreenName, final long cursor) throws TwitterException {
		 return  fetchUserListSubscriptions(json, listOwnerScreenName, cursor);
	 }

	 public  <T> List<String> fetchUserListsOwnerships(As json, final T ident, final int count, final long cursor) throws TwitterException { 
		 return getJSONList(fetchUserListsOwnerships(ident, count, cursor));
	 }

	 public List<String> getUserListsOwnerships(As json, final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
		 return  fetchUserListsOwnerships(json, listOwnerScreenName, count, cursor);
	 }

	 public List<String> getUserListsOwnerships(As json, final long listOwnerId, final int count, final long cursor) throws TwitterException {
		 return fetchUserListsOwnerships(json, listOwnerId, count, cursor);
	 }

	 /*
	  * PlacesGeoResources
	  */

	 public String getGeoDetails(As json, final String placeId) throws TwitterException {
		 return getJSON(getGeoDetails(placeId));
	 }

	 public List<String> reverseGeoCode(As json, final GeoQuery query) throws TwitterException {
		 return getJSONList(reverseGeoCode(query));
	 }

	 public List<String> searchPlaces(As json, final GeoQuery query) throws TwitterException {
		 return getJSONList(searchPlaces(query));
	 }

	 public List<String>
	 getSimilarPlaces(As json, final GeoLocation location, final String name, final String containedWithin, final String streetAddress) throws TwitterException {
		 return getJSONList(getSimilarPlaces(location, name, containedWithin, streetAddress));
	 }

	 /*
	  * TrendsResources
	  */

	 public String getPlaceTrends(As json, final int woeid) throws TwitterException {
		 return getJSON(getPlaceTrends(woeid));
	 }

	 public String getAvailableTrends(As json) throws TwitterException {
		 return getJSON(getAvailableTrends());
	 }

	 public String getClosestTrends(As json, final GeoLocation location) throws TwitterException {
		 return getJSON(getClosestTrends(location));
	 }

	 /*
	  * HelpResources
	  */

	 //

}
