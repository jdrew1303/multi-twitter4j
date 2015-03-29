package org.insight.twitter.multi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Container for different Endpoint Names, and priority queues. Uses Enum Singleton pattern because we only want 1 global instance of each endpoint+bot. As far
 * as Twitter is concerned, there is 1 Ratelimit per endpoint, per access token.
 */

public enum EndPoint implements GetBotQueue, ApplicationOnlySupport {
  /*
   * TimelinesResources
   */
  STATUSES_USER_TIMELINE {
    @Override
    public String toString() {
      return "/statuses/user_timeline";
    }
  },

  /*
   * TweetsResources
   */
  STATUSES_RETWEETS {
    @Override
    public String toString() {
      return "/statuses/retweets/:id";
    }
  },
  STATUSES_RETWEETERS {
    @Override
    public String toString() {
      return "/statuses/retweeters/ids";
    }
  },
  STATUSES_SHOW {
    @Override
    public String toString() {
      return "/statuses/show/:id";
    }
  },
  STATUSES_LOOKUP {
    @Override
    public String toString() {
      return "/statuses/lookup";
    }
  },

  /*
   * SearchResource
   */
  SEARCH_TWEETS {
    @Override
    public String toString() {
      return "/search/tweets";
    }
  },

  /*
   * FriendsFollowersResources
   */
  FRIENDS_LIST {
    @Override
    public String toString() {
      return "/friends/list";
    }
  },
  FRIENDS_IDS {
    @Override
    public String toString() {
      return "/friends/ids";
    }
  },

  FOLLOWERS_LIST {
    @Override
    public String toString() {
      return "/followers/list";
    }
  },
  FOLLOWERS_IDS {
    @Override
    public String toString() {
      return "/followers/ids";
    }
  },

  FRIENDSHIPS_SHOW {
    @Override
    public String toString() {
      return "/friendships/show";
    }
  },

  /*
   * UsersResources
   */
  USERS_LOOKUP {
    @Override
    public String toString() {
      return "/users/lookup";
    }
  },
  USERS_SHOW {
    @Override
    public String toString() {
      return "/users/show/:id";
    }
  },
  USERS_SEARCH {
    @Override
    public String toString() {
      return "/users/search";
    }

    @Override
    public boolean hasApplicationOnlySupport() {
      return false;
    }
  },
  USERS_CONTRIBUTEES {
    @Override
    public String toString() {
      return "/users/contributees";
    }

    @Override
    public boolean hasApplicationOnlySupport() {
      return false;
    }
  },
  USERS_CONTRIBUTORS {
    @Override
    public String toString() {
      return "/users/contributors";
    }

    @Override
    public boolean hasApplicationOnlySupport() {
      return false;
    }
  },

  /*
   * FavoritesResources
   */
  FAVORITES_LIST {
    @Override
    public String toString() {
      return "/favorites/list";
    }
  },

  /*
   * ListsResources
   */
  LISTS_LIST {
    @Override
    public String toString() {
      return "/lists/list";
    }
  },
  LISTS_MEMBERS {
    @Override
    public String toString() {
      return "/lists/members";
    }
  },
  LISTS_MEMBERS_SHOW {
    @Override
    public String toString() {
      return "/lists/members/show";
    }
  },
  LISTS_MEMBERSHIPS {
    @Override
    public String toString() {
      return "/lists/memberships";
    }
  },
  LISTS_OWNERSHIPS {
    @Override
    public String toString() {
      return "/lists/ownerships";
    }
  },
  LISTS_SHOW {
    @Override
    public String toString() {
      return "/lists/show";
    }
  },
  LISTS_STATUSES {
    @Override
    public String toString() {
      return "/lists/statuses";
    }
  },
  LISTS_SUBSCRIBERS {
    @Override
    public String toString() {
      return "/lists/subscribers";
    }
  },
  LISTS_SUBSCRIBERS_SHOW {
    @Override
    public String toString() {
      return "/lists/subscribers/show";
    }
  },
  LISTS_SUBSCRIPTIONS {
    @Override
    public String toString() {
      return "/lists/subscriptions";
    }
  },

  /*
   * PlacesGeoResources
   */
  GEO_ID {
    @Override
    public String toString() {
      return "/geo/id";
    }

    @Override
    public boolean hasApplicationOnlySupport() {
      return false;
    }
  },
  GEO_REVERSE_GEOCODE {
    @Override
    public String toString() {
      return "/geo/reverse_geocode";
    }

    @Override
    public boolean hasApplicationOnlySupport() {
      return false;
    }
  },
  GEO_SEARCH {
    @Override
    public String toString() {
      return "/geo/search";
    }

    @Override
    public boolean hasApplicationOnlySupport() {
      return false;
    }
  },
  GEO_SIMILAR_PLACES {
    @Override
    public String toString() {
      return "/geo/similar_places";
    }

    @Override
    public boolean hasApplicationOnlySupport() {
      return false;
    }
  },

  /*
   * TrendsResources
   */
  TRENDS_PLACE {
    @Override
    public String toString() {
      return "/trends/place";
    }
  },
  TRENDS_AVAILABLE {
    @Override
    public String toString() {
      return "/trends/available";
    }
  },
  TRENDS_CLOSEST {
    @Override
    public String toString() {
      return "/trends/closest";
    }
  };

  /*
   * Define new Endpoints in the same way: RESOURCE_ENDPOINT {
   *
   * @Override public String toString() { return "/resource/endpoint"; } }
   */

  // Make a queue for each endpoint:
  private static final Map<String, BotQueue> BOT_QUEUES = new ConcurrentHashMap<>();

  static {
    for (EndPoint endpoint : EndPoint.values()) {
      BOT_QUEUES.put(endpoint.toString(), new BotQueue(endpoint));
    }
  }

  @Override
  public BotQueue getBotQueue() {
    return BOT_QUEUES.get(this.toString());
  }

  // Default: Most endpoints have application only calls.
  @Override
  public boolean hasApplicationOnlySupport() {
    return true;
  }

  public static EndPoint fromString(final String resource) {
    if (resource != null) {
      for (EndPoint e : EndPoint.values()) {
        if (resource.equalsIgnoreCase(e.toString())) {
          return e;
        }
      }
    }
    return null;
  }
}


interface GetBotQueue {
  public BotQueue getBotQueue();
}


interface ApplicationOnlySupport {
  public boolean hasApplicationOnlySupport();
}
