package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

/*
 * Container for different Endpoint Names and Queues.
 */

public enum EndPoint implements ApplicationOnlySupport {
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
      return "/lists/members/show"; // DEPRECATE
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
  },

  /*
   * Rate Limits
   */

  APPLICATION_RATE_LIMIT_STATUS {
    @Override
    public String toString() {
      return "/application/rate_limit_status";
    }
  }

  ;

  /*
   * Define new Endpoints in the same way: RESOURCE_ENDPOINT {
   *
   * @Override public String toString() { return "/resource/endpoint"; } }
   */

  // Default: Most endpoints have application only calls.
  @Override
  public boolean hasApplicationOnlySupport() {
    return true;
  }

  public static EndPoint fromString(String resource) {
    if (null != resource) {
      for (EndPoint e : EndPoint.values()) {
        if (resource.equalsIgnoreCase(e.toString())) {
          return e;
        }
      }
    }
    return null;
  }

  public static EndPoint[] fromGroup(String... groups) {
    List<EndPoint> endpoints = new ArrayList<>();
    for (String group : groups) {
      for (EndPoint e : EndPoint.values()) {
        if (e.toString().contains(group)) {
          endpoints.add(e);
        }
      }
    }
    return endpoints.toArray(new EndPoint[endpoints.size()]);
  }

}


interface ApplicationOnlySupport {
  boolean hasApplicationOnlySupport();
}
