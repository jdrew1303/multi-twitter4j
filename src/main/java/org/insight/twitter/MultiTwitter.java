package org.insight.twitter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insight.twitter.internal.EndPoint;
import org.insight.twitter.internal.InternalRateLimitStatus;
import org.insight.twitter.internal.LimitedTwitterResources;
import org.insight.twitter.internal.TwitterBot;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import twitter4j.UserList;

/*
 * Only implements REST API calls that can be spread over multiple accounts.
 * 
 * Should be straight forward to add unimplemented methods, if you really need them.
 */

public class MultiTwitter extends LimitedTwitterResources {

	// Read configs from file
	private final Set<String> configuredBots;	

	// How to use the Queue - Block, or throw rate limit exceptions
	private final boolean blockOnRateLimit;

	// Use blocking queues by default, configs from twitter4j.properties
	public MultiTwitter() {
		this(true, "twitter4j.properties");
	}

	public MultiTwitter(final boolean blockOnRateLimit, String configFile) {
		this.blockOnRateLimit = blockOnRateLimit;
		this.configuredBots = getConfiguredBots(configFile);
	}

	/* ============================================================================
	 * Useful single method calls for paging through results, avoiding cursors etc:
	 * ============================================================================
	 */

	//TODO

	/*
	 * Timelines
	 */

	public List<String> getFullUserTimeline(long userId, Date oldest_created_at) {
		
		List<String> timeline = new ArrayList<String>();

		Paging paging = new Paging();
		paging.count(200);

		int ctweets = 0;
		
		boolean datelimit = false;
		
		while (ctweets < 3200 && !datelimit) {

			try {
			ResponseList<Status> page = getUserTimeline(userId, paging);
									
		
			Collection<Long> lowestID = new ArrayList<Long>();
			for (Status s : page) {
				lowestID.add(s.getId());
				
				if (s.getCreatedAt().compareTo(oldest_created_at) < 0) {
					//System.out.println("Date Limit!");
					datelimit = true;
					continue;
				}
				
				timeline.add(TwitterObjectFactory.getRawJSON(s));
			}
			
			System.out.println( userId + " Fetched: " +lowestID.size()+":" + ctweets +  " tweets");

			if (lowestID.size() < 1) {
				break;
			}

			// Max ID is the lowest ID tweet you have already processed
			paging.setMaxId((Collections.min(lowestID) - 1));
			//pg.setSinceId(sinceId);

			ctweets = ctweets + page.size();
			
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("FAILED TO RETRIEVE TIMELINE FOR USER: " + userId);
				break;
			}

		}

		return timeline;
	}
	
	
	
public List<String> getUpdateUserTimeline(long userId, long since_id) {
		
		List<String> timeline = new ArrayList<String>();

		Paging paging = new Paging();
		paging.count(200);
		paging.setSinceId(since_id);

		int ctweets = 0;
		
		boolean datelimit = false;
		
		while (ctweets < 3200 && !datelimit) {

			try {
			ResponseList<Status> page = getUserTimeline(userId, paging);
									
		
			Collection<Long> lowestID = new ArrayList<Long>();
			for (Status s : page) {
				lowestID.add(s.getId());
								
				timeline.add(TwitterObjectFactory.getRawJSON(s));
			}
			
			System.out.println( userId + " Fetched: " +lowestID.size()+":" + ctweets +  " tweets");

			if (lowestID.size() < 1) {
				break;
			}

			// Max ID is the lowest ID tweet you have already processed
			paging.setMaxId((Collections.min(lowestID) - 1));
			paging.setSinceId(since_id);

			ctweets = ctweets + page.size();
			
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("FAILED TO RETRIEVE TIMELINE FOR USER: " + userId);
				break;
			}

		}

		return timeline;
	}


	/* 
	 * Tweets
	 */

	//TODO		

	/*
	 * Search
	 */

	//TODO

	/*
	 * FriendsFollowers
	 */

	//TODO

	/*
	 * Users
	 */

	//TODO

	/*
	 * FavoritesResources
	 */

	//TODO

	/*
	 * ListsResources
	 */

	//TODO
	
	public List<User> getAllListMembers(long listId) {
		
		List<User> members = new ArrayList<User>();
		
		long cursor = -1;
		
		try {
			
			while(cursor != 0) {
			
			PagableResponseList<User> pg = getUserListMembers(listId, cursor);
			
			members.addAll(pg);
			
			cursor = pg.getNextCursor();
			}
			
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return members;
	}
		

	/*
	 * TrendsResources
	 */

	//TODO	

	/* ========================================================
	 * Twitter4J Wrapper Implementations:
	 * ========================================================
	 */

	/*
	 * TimelinesResources
	 */

	@Override
	public ResponseList<Status> getUserTimeline(String screenName) throws TwitterException {
		return getUserTimeline(screenName, new Paging());
	}

	@Override
	public ResponseList<Status> getUserTimeline(final String screenName, final Paging paging) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().timelines().getUserTimeline(screenName, paging);
			}			
		}).getResponse(EndPoint.STATUSES_USER_TIMELINE);
	}

	@Override
	public ResponseList<Status> getUserTimeline(long userId)	throws TwitterException {
		return getUserTimeline(userId, new Paging());
	}

	@Override
	public ResponseList<Status> getUserTimeline(final long userId, final Paging paging) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().timelines().getUserTimeline(userId, paging);
			}			
		}).getResponse(EndPoint.STATUSES_USER_TIMELINE);
	}

	/* 
	 * TweetsResources
	 */

	@Override
	public ResponseList<Status> getRetweets(final long statusId) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().tweets().getRetweets(statusId);
			}			
		}).getResponse(EndPoint.STATUSES_RETWEETS);
	}

	@Override
	public IDs getRetweeterIds(long statusId, long cursor) throws TwitterException {
		return getRetweeterIds(statusId, 100, cursor);
	}

	@Override
	public IDs getRetweeterIds(final long statusId, final int count, final long cursor) throws TwitterException {
		return (new TwitterCommand<IDs>() {
			@Override
			public IDs fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().tweets().getRetweeterIds(statusId, count, cursor);
			}			
		}).getResponse(EndPoint.STATUSES_RETWEETERS);
	}

	@Override
	public Status showStatus(final long id) throws TwitterException {
		return (new TwitterCommand<Status>() {
			@Override
			public Status fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().tweets().showStatus(id);
			}			
		}).getResponse(EndPoint.STATUSES_SHOW);
	}

	@Override
	public ResponseList<Status> lookup(final long[] ids) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().tweets().lookup(ids);
			}			
		}).getResponse(EndPoint.STATUSES_LOOKUP);
	}

	/*
	 * SearchResource
	 */

	@Override
	public QueryResult search(final Query query) throws TwitterException {
		return (new TwitterCommand<QueryResult>() {
			@Override
			public QueryResult fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().search().search(query);
			}
		}).getResponse(EndPoint.SEARCH_TWEETS);
	}

	/*
	 * FriendsFollowersResources
	 */

	@Override
	public IDs getFriendsIDs(long userId, long cursor) throws TwitterException {
		return getFriendsIDs(userId, cursor, 5000);
	}

	@Override
	public IDs getFriendsIDs(final long userId, final long cursor, final int count) throws TwitterException {
		return (new TwitterCommand<IDs>() {
			@Override
			public IDs fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFollowersIDs(userId, cursor, count);
			}
		}).getResponse(EndPoint.FRIENDS_IDS);
	}

	@Override
	public IDs getFriendsIDs(String screenName, long cursor) throws TwitterException {
		return getFriendsIDs(screenName, cursor, 5000);
	}

	@Override
	public IDs getFriendsIDs(final String screenName, final long cursor, final int count)	throws TwitterException {
		return (new TwitterCommand<IDs>() {
			@Override
			public IDs fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFriendsIDs(screenName, cursor, count);
			}
		}).getResponse(EndPoint.FRIENDS_IDS);
	}

	@Override
	public IDs getFollowersIDs(long userId, long cursor) throws TwitterException {
		return getFollowersIDs(userId, cursor, 5000);
	}

	@Override
	public IDs getFollowersIDs(final long userId, final long cursor, final int count) throws TwitterException {
		return (new TwitterCommand<IDs>() {
			@Override
			public IDs fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFollowersIDs(userId, cursor, count);
			}
		}).getResponse(EndPoint.FOLLOWERS_IDS);
	}

	@Override
	public IDs getFollowersIDs(String screenName, long cursor) throws TwitterException {
		return getFollowersIDs(screenName, cursor, 5000);
	}

	@Override
	public IDs getFollowersIDs(final String screenName, final long cursor, final int count) throws TwitterException {
		return (new TwitterCommand<IDs>() {
			@Override
			public IDs fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFollowersIDs(screenName, cursor, count);
			}
		}).getResponse(EndPoint.FOLLOWERS_IDS);
	}

	@Override
	public Relationship showFriendship(final long sourceId, final long targetId) throws TwitterException {
		return (new TwitterCommand<Relationship>() {
			@Override
			public Relationship fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().showFriendship(sourceId, targetId);
			}
		}).getResponse(EndPoint.FRIENDSHIPS_SHOW);
	}

	@Override
	public Relationship showFriendship(final String sourceScreenName, final String targetScreenName) throws TwitterException {
		return (new TwitterCommand<Relationship>() {
			@Override
			public Relationship fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().showFriendship(sourceScreenName, targetScreenName);
			}
		}).getResponse(EndPoint.FRIENDSHIPS_SHOW);
	}

	@Override
	public PagableResponseList<User> getFriendsList(long userId, long cursor) throws TwitterException {
		return getFriendsList(userId, cursor, 200, false, true);
	}

	@Override
	public PagableResponseList<User> getFriendsList(long userId, long cursor, int count) throws TwitterException {
		return getFriendsList(userId, cursor, 200, false, true);
	}

	@Override
	public PagableResponseList<User> getFriendsList(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities)	throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFriendsList(userId, cursor, count, skipStatus, includeUserEntities);
			}
		}).getResponse(EndPoint.FRIENDS_LIST);
	}

	@Override
	public PagableResponseList<User> getFriendsList(String screenName, long cursor) throws TwitterException {
		return getFriendsList(screenName, cursor, 200, false, true);
	}

	@Override
	public PagableResponseList<User> getFriendsList(String screenName, long cursor, int count) throws TwitterException {
		return getFriendsList(screenName, cursor, count, false, true);
	}

	@Override
	public PagableResponseList<User> getFriendsList(final String screenName, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFriendsList(screenName, cursor, count, skipStatus, includeUserEntities);
			}
		}).getResponse(EndPoint.FRIENDS_LIST);
	}

	@Override
	public PagableResponseList<User> getFollowersList(long userId, long cursor)	throws TwitterException {
		return getFollowersList(userId, cursor, 200, false, true);
	}

	@Override
	public PagableResponseList<User> getFollowersList(long userId, long cursor, int count) throws TwitterException {
		return getFollowersList(userId, cursor, count, false, true);
	}

	@Override
	public PagableResponseList<User> getFollowersList(final long userId, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFollowersList(userId, cursor, count, skipStatus, includeUserEntities);
			}
		}).getResponse(EndPoint.FOLLOWERS_LIST);
	}

	@Override
	public PagableResponseList<User> getFollowersList(String screenName, long cursor) throws TwitterException {
		return getFollowersList(screenName, cursor, 200, false, true);
	}

	@Override
	public PagableResponseList<User> getFollowersList(String screenName, long cursor, int count) throws TwitterException {
		return getFollowersList(screenName, cursor, count, false, true);
	}

	@Override
	public PagableResponseList<User> getFollowersList(final String screenName, final long cursor, final int count, final boolean skipStatus, final boolean includeUserEntities) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().friendsFollowers().getFollowersList(screenName, cursor, count, skipStatus, includeUserEntities);
			}
		}).getResponse(EndPoint.FOLLOWERS_LIST);
	}

	/*
	 * UsersResources
	 */

	@Override
	public ResponseList<User> lookupUsers(final long[] ids) throws TwitterException {
		return (new TwitterCommand<ResponseList<User>>() {
			@Override
			public ResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().lookupUsers(ids);
			}
		}).getResponse(EndPoint.USERS_LOOKUP);
	}

	@Override
	public ResponseList<User> lookupUsers(final String[] screenNames) throws TwitterException {
		return (new TwitterCommand<ResponseList<User>>() {
			@Override
			public ResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().lookupUsers(screenNames);
			}
		}).getResponse(EndPoint.USERS_LOOKUP);
	}

	@Override
	public User showUser(final long userId) throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().showUser(userId);
			}
		}).getResponse(EndPoint.USERS_SHOW);
	}

	@Override
	public User showUser(final String screenName) throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().showUser(screenName);
			}
		}).getResponse(EndPoint.USERS_SHOW);
	}

	@Override
	public ResponseList<User> searchUsers(final String query, final int page) throws TwitterException {
		return (new TwitterCommand<ResponseList<User>>() {
			@Override
			public ResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().searchUsers(query, page);
			}
		}).getResponse(EndPoint.USERS_SEARCH);
	}

	@Override
	public ResponseList<User> getContributees(final long userId) throws TwitterException {
		return (new TwitterCommand<ResponseList<User>>() {
			@Override
			public ResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().getContributees(userId);
			}
		}).getResponse(EndPoint.USERS_CONTRIBUTEES);
	}

	@Override
	public ResponseList<User> getContributees(final String screenName) throws TwitterException {
		return (new TwitterCommand<ResponseList<User>>() {
			@Override
			public ResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().getContributees(screenName);
			}
		}).getResponse(EndPoint.USERS_CONTRIBUTEES);
	}

	@Override
	public ResponseList<User> getContributors(final String screenName)	throws TwitterException {
		return (new TwitterCommand<ResponseList<User>>() {
			@Override
			public ResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {

				return bot.getTwitter().users().getContributors(screenName);
			}
		}).getResponse(EndPoint.USERS_CONTRIBUTORS);
	}

	@Override
	public ResponseList<User> getContributors(final long userId) throws TwitterException {
		return (new TwitterCommand<ResponseList<User>>() {
			@Override
			public ResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().users().getContributors(userId);
			}
		}).getResponse(EndPoint.USERS_CONTRIBUTORS);
	}

	/*
	 * FavoritesResources
	 */

	@Override
	public ResponseList<Status> getFavorites(long userId) throws TwitterException {
		return getFavorites(userId, new Paging());
	}

	@Override
	public ResponseList<Status> getFavorites(final long userId, final Paging paging)	throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().favorites().getFavorites(userId, paging);
			}
		}).getResponse(EndPoint.FAVORITES_LIST);
	}

	@Override
	public ResponseList<Status> getFavorites(String screenName) throws TwitterException {
		return getFavorites(screenName, new Paging());
	}

	@Override
	public ResponseList<Status> getFavorites(final String screenName, final Paging paging) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().favorites().getFavorites(screenName, paging);
			}
		}).getResponse(EndPoint.FAVORITES_LIST);
	}

	/*
	 * ListsResources
	 */

	@Override
	public ResponseList<UserList> getUserLists(final String listOwnerScreenName) throws TwitterException {
		return (new TwitterCommand<ResponseList<UserList>>() {
			@Override
			public ResponseList<UserList> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserLists(listOwnerScreenName);
			}
		}).getResponse(EndPoint.LISTS_LIST);
	}

	@Override
	public ResponseList<UserList> getUserLists(final long listOwnerUserId)	throws TwitterException {
		return (new TwitterCommand<ResponseList<UserList>>() {
			@Override
			public ResponseList<UserList> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserLists(listOwnerUserId);
			}
		}).getResponse(EndPoint.LISTS_LIST);
	}

	@Override
	public ResponseList<Status> getUserListStatuses(final long listId, final Paging paging) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListStatuses(listId, paging);
			}
		}).getResponse(EndPoint.LISTS_STATUSES);
	}

	@Override
	public ResponseList<Status> getUserListStatuses(final long ownerId, final String slug, final Paging paging) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListStatuses(ownerId, slug, paging);
			}
		}).getResponse(EndPoint.LISTS_STATUSES);
	}

	@Override
	public ResponseList<Status> getUserListStatuses(final String ownerScreenName, final String slug, final Paging paging) throws TwitterException {
		return (new TwitterCommand<ResponseList<Status>>() {
			@Override
			public ResponseList<Status> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListStatuses(ownerScreenName, slug, paging);
			}
		}).getResponse(EndPoint.LISTS_STATUSES);
	}

	@Override
	public PagableResponseList<UserList> getUserListMemberships(final long listMemberId, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<UserList>>() {
			@Override
			public PagableResponseList<UserList> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListMemberships(listMemberId, cursor);
			}
		}).getResponse(EndPoint.LISTS_MEMBERSHIPS);
	}

	@Override
	public PagableResponseList<UserList> getUserListMemberships(final String listMemberScreenName, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<UserList>>() {
			@Override
			public PagableResponseList<UserList> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListMemberships(listMemberScreenName, cursor);
			}
		}).getResponse(EndPoint.LISTS_MEMBERSHIPS);
	}

	@Override
	public PagableResponseList<User> getUserListSubscribers(final long listId, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListSubscribers(listId, cursor);
			}
		}).getResponse(EndPoint.LISTS_SUBSCRIBERS);
	}

	@Override
	public PagableResponseList<User> getUserListSubscribers(final long ownerId, final String slug, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListSubscribers(ownerId, slug, cursor);
			}
		}).getResponse(EndPoint.LISTS_SUBSCRIBERS);
	}

	@Override
	public PagableResponseList<User> getUserListSubscribers(final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListSubscribers(ownerScreenName, slug, cursor);
			}
		}).getResponse(EndPoint.LISTS_SUBSCRIBERS);
	}

	@Override
	public User showUserListSubscription(final long listId, final long userId) throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserListSubscription(listId, userId);
			}
		}).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
	}

	@Override
	public User showUserListSubscription(final long ownerId, final String slug, final long userId) throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserListSubscription(ownerId, slug, userId);
			}
		}).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
	}

	@Override
	public User showUserListSubscription(final String ownerScreenName, final String slug, final long userId) throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserListSubscription(ownerScreenName, slug, userId);
			}
		}).getResponse(EndPoint.LISTS_SUBSCRIBERS_SHOW);
	}

	@Override
	public User showUserListMembership(final long listId, final long userId) 	throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserListMembership(listId, userId);
			}
		}).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
	}

	@Override
	public User showUserListMembership(final long ownerId, final String slug, final long userId)	throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserListMembership(ownerId, slug, userId);
			}
		}).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
	}

	@Override
	public User showUserListMembership(final String ownerScreenName, final String slug,	final long userId) throws TwitterException {
		return (new TwitterCommand<User>() {
			@Override
			public User fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserListMembership(ownerScreenName, slug, userId);
			}
		}).getResponse(EndPoint.LISTS_MEMBERS_SHOW);
	}

	@Override
	public PagableResponseList<User> getUserListMembers(final long listId, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListMembers(listId, cursor);
			}
		}).getResponse(EndPoint.LISTS_MEMBERS);
	}

	@Override
	public PagableResponseList<User> getUserListMembers(final long ownerId, final String slug, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListMembers(ownerId, slug, cursor);
			}
		}).getResponse(EndPoint.LISTS_MEMBERS);
	}

	@Override
	public PagableResponseList<User> getUserListMembers(final String ownerScreenName, final String slug, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<User>>() {
			@Override
			public PagableResponseList<User> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListMembers(ownerScreenName, slug, cursor);
			}
		}).getResponse(EndPoint.LISTS_MEMBERS);
	}

	@Override
	public UserList showUserList(final long listId) throws TwitterException {
		return (new TwitterCommand<UserList>() {
			@Override
			public UserList fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserList(listId);
			}
		}).getResponse(EndPoint.LISTS_SHOW);
	}

	@Override
	public UserList showUserList(final long ownerId, final String slug) throws TwitterException {
		return (new TwitterCommand<UserList>() {
			@Override
			public UserList fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserList(ownerId, slug);
			}
		}).getResponse(EndPoint.LISTS_SHOW);
	}

	@Override
	public UserList showUserList(final String ownerScreenName, final String slug) throws TwitterException {
		return (new TwitterCommand<UserList>() {
			@Override
			public UserList fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().showUserList(ownerScreenName, slug);
			}
		}).getResponse(EndPoint.LISTS_SHOW);
	}

	@Override
	public PagableResponseList<UserList> getUserListSubscriptions(final String listOwnerScreenName, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<UserList>>() {
			@Override
			public PagableResponseList<UserList> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListSubscriptions(listOwnerScreenName, cursor);
			}
		}).getResponse(EndPoint.LISTS_SUBSCRIPTIONS);
	}

	@Override
	public PagableResponseList<UserList> getUserListsOwnerships(final String listOwnerScreenName, final int count, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<UserList>>() {
			@Override
			public PagableResponseList<UserList> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListsOwnerships(listOwnerScreenName, count, cursor);
			}
		}).getResponse(EndPoint.LISTS_OWNERSHIPS);
	}

	@Override
	public PagableResponseList<UserList> getUserListsOwnerships(final long listOwnerId, final int count, final long cursor) throws TwitterException {
		return (new TwitterCommand<PagableResponseList<UserList>>() {
			@Override
			public PagableResponseList<UserList> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().list().getUserListsOwnerships(listOwnerId, count, cursor);
			}
		}).getResponse(EndPoint.LISTS_OWNERSHIPS);
	}

	/*
	 * PlacesGeoResources
	 */

	@Override
	public Place getGeoDetails(final String placeId) throws TwitterException {
		return (new TwitterCommand<Place>() {
			@Override
			public Place fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().placesGeo().getGeoDetails(placeId);
			}
		}).getResponse(EndPoint.GEO_ID);
	}

	@Override
	public ResponseList<Place> reverseGeoCode(final GeoQuery query) 	throws TwitterException {
		return (new TwitterCommand<ResponseList<Place>>() {
			@Override
			public ResponseList<Place> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().placesGeo().reverseGeoCode(query);
			}
		}).getResponse(EndPoint.GEO_REVERSE_GEOCODE);
	}

	@Override
	public ResponseList<Place> searchPlaces(final GeoQuery query) throws TwitterException {
		return (new TwitterCommand<ResponseList<Place>>() {
			@Override
			public ResponseList<Place> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().placesGeo().searchPlaces(query);
			}
		}).getResponse(EndPoint.GEO_SEARCH);
	}

	@Override
	public ResponseList<Place> getSimilarPlaces(final GeoLocation location, final String name, final String containedWithin, final String streetAddress) throws TwitterException {
		return (new TwitterCommand<ResponseList<Place>>() {
			@Override
			public ResponseList<Place> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().placesGeo().getSimilarPlaces(location, name, containedWithin, streetAddress);
			}
		}).getResponse(EndPoint.GEO_SIMILAR_PLACES);
	}

	/*
	 * TrendsResources
	 */

	@Override
	public Trends getPlaceTrends(final int woeid) throws TwitterException {
		return (new TwitterCommand<Trends>() {
			@Override
			public Trends fetchResponse(TwitterBot bot) throws TwitterException {

				return bot.getTwitter().trends().getPlaceTrends(woeid);
			}
		}).getResponse(EndPoint.TRENDS_PLACE);
	}

	@Override
	public ResponseList<Location> getAvailableTrends() throws TwitterException {
		return (new TwitterCommand<ResponseList<Location>>() {
			@Override
			public ResponseList<Location> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().trends().getAvailableTrends();
			}
		}).getResponse(EndPoint.TRENDS_AVAILABLE);
	}

	@Override
	public ResponseList<Location> getClosestTrends(final GeoLocation location)	throws TwitterException {
		return (new TwitterCommand<ResponseList<Location>>() {
			@Override
			public ResponseList<Location> fetchResponse(TwitterBot bot) throws TwitterException {
				return bot.getTwitter().trends().getClosestTrends(location);
			}
		}).getResponse(EndPoint.TRENDS_CLOSEST);
	}

	/*
	 * HelpResources
	 */

	@Override
	public Map<String, RateLimitStatus> getRateLimitStatus(String... resources) throws TwitterException {
		EndPoint[] endpoints = new EndPoint[resources.length];
		for (int i=0; i < resources.length; i++) {
			endpoints[i] = EndPoint.fromString(resources[i]);			
		}
		return getRateLimitStatus(endpoints);
	}

	@Override
	public Map<String, RateLimitStatus> getRateLimitStatus()	throws TwitterException {
		return 	getRateLimitStatus(EndPoint.values());
	}

	/*
	 * Get combined Rate Limit for an endpoint (or several)
	 */
	public Map<String, RateLimitStatus> getRateLimitStatus(EndPoint[] endpoints) throws TwitterException {
		Map<String, RateLimitStatus> rateLimits = new HashMap<String, RateLimitStatus>();
		for (EndPoint target : endpoints) {
			rateLimits.putAll(getRateLimitStatus(target));
		}		
		return rateLimits;
	}

	/*
	 * Get Combined Rate Limit from all available bots
	 */
	public Map<String, RateLimitStatus> getRateLimitStatus(EndPoint endpoint) throws TwitterException {
		Map<String, RateLimitStatus> rateLimit = new HashMap<String, RateLimitStatus>();
		InternalRateLimitStatus rl = new InternalRateLimitStatus();
		Set<TwitterBot> allActiveBots = endpoint.getBotQueue().getLoadedBots();

		for (TwitterBot bot : allActiveBots) {
			rl = rl.mergeWith(bot.getCachedRateLimitStatus());
		}
		rateLimit.put(endpoint.toString(), rl);
		return rateLimit;
	}

	/*
	 * All unimplemented methods will throw UnsupportedMethodException 
	 */

	/*
	 * --------------------------
	 * Utility Methods:
	 * --------------------------
	 */

	/*
	 * Read config file, extract all the access tokens.
	 */
	public static Set<String> getConfiguredBots(String configFile) {
		Set<String> botIDs = new HashSet<String>();
		Properties t4jProperties = new Properties();
		try {
			System.out.println("Reading Bot Configs from: " + "/" + configFile);
			
			//InputStream in = MultiTwitter.class.getResourceAsStream("/" + configFile);
			InputStream in = new FileInputStream(configFile);

			t4jProperties.load(in);

			in.close();
		} catch (IOException e) {
			System.err.println("IO ERROR Reading Properties!");
			e.printStackTrace();
			return botIDs;
		}
		// Find bots we have...
		// Config file must be well formed!
		Pattern p = Pattern.compile( "bot\\.(.*?)\\.oauth\\.accessTokenSecret" );
		for (String key : t4jProperties.stringPropertyNames()) {			
			Matcher m = p.matcher(key);
			if( m.find() )	{
				String strBotNum = m.group(1);
				botIDs.add( "bot." + strBotNum );
				System.out.println("Detected config for: " + strBotNum );
			}
		}
		return botIDs;
	}

	/*
	 * Taking bots from Queue:
	 */
	private TwitterBot takeBot(final EndPoint endpoint) throws TwitterException {
		// Either Block with take() until a bot is available, or throw rate limit exception:
		// Lazy load bots to endpoints:
		endpoint.getBotQueue().reloadConfiguredBots(configuredBots);

		TwitterBot bot = blockOnRateLimit ? endpoint.getBotQueue().take() : endpoint.getBotQueue().poll();

		if (bot == null) {
			throw new TwitterException(endpoint + " Queue is EMPTY! Rate Limit for all Bots Reached!");
		}	

		return bot;
	}

	/*
	 * Returning bots to Queue:
	 */
	private void releaseBot(final TwitterBot bot) {
		//Always return bot to queue:
		if (bot != null) {
			//System.out.println("Returning bot " + bot.toString() + " back to queue, ratelimit remaining: " + bot.getCachedRateLimitStatus().getRemaining()); 
			bot.getEndPoint().getBotQueue().offer(bot);
		}
	}

	/*
	 * Wrap Responses from Twitter, Retry on failures, throw appropriate Exceptions.
	 * This provides retry functions to any method:
	 * Can be replaced with something else that manages bots & calls.
	 */
	public abstract class TwitterCommand<T> {		
		public T getResponse(EndPoint endpoint) throws TwitterException {			
			T result = null;
			int retryLimit = configuredBots.size();
			while (true) {
				TwitterBot bot = null;
				try {
					bot = takeBot(endpoint);
					result = fetchResponse(bot);
					break;				
				} catch (TwitterException e) {
					if (e.exceededRateLimitation() || e.isCausedByNetworkIssue()) {
						if (--retryLimit <= 0) {
							System.out.println("Retried " + configuredBots.size() + " times, giving up.");
							throw e;
						}
					}
					/*
					 * Skip retrying Private / Deleted / Banned accounts
					 */
					if (e.resourceNotFound() || e.getStatusCode() == 401) {
						System.out.println("Resource not found / Unauthorized, giving up.");
						throw e;
					}
					System.out.println("Temporary Rate Limit / Connection Error!, Retrying " + retryLimit + " more times... " + e.toString());
				} finally {
					releaseBot(bot);
				}
			}
			return result;
		}
		/*
		 * Methods must extend this: 
		 */
		public abstract T fetchResponse(TwitterBot bot) throws TwitterException;
	}

}
