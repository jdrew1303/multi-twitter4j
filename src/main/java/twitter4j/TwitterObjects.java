package twitter4j;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Utility Methods, in a twitter4j package to access package private t4j methods
 */
public class TwitterObjects {

  public static final ObjectMapper mapper = new ObjectMapper();

  public static final String TWITTER_DATE_FORMAT = "EEE MMM d HH:mm:ss Z yyyy";

  public static long[] toPrimitive(final List<Long> list) {
    return toPrimitive(list.toArray(new Long[list.size()]));
  }

  public static long[] toPrimitive(Long[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return new long[0];
    }
    long[] result = new long[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  /*
   * Build Parameter String:
   */
  public static HttpParameter[] parameters(Query query, HttpParameter... params2) {
    return TwitterObjects.parameters(query.asHttpParameterArray(), params2);
  }

  public static HttpParameter[] parameters(GeoQuery query, HttpParameter... params2) {
    return TwitterObjects.parameters(query.asHttpParameterArray(), params2);
  }

  public static HttpParameter[] parameters(Paging page, HttpParameter... params2) {
    return TwitterObjects.parameters(page.asPostParameterArray(), params2);
  }

  public static HttpParameter[] parameters(HttpParameter[] params1, HttpParameter... params2) {
    List<HttpParameter> parameters = new ArrayList<>();
    parameters.addAll(Arrays.asList(params1));
    parameters.addAll(Arrays.asList(params2));
    return parameters.toArray(new HttpParameter[parameters.size()]);
  }

  public static String join(Long[] ids) {
    return StringUtil.join(TwitterObjects.toPrimitive(ids));
  }

  public static String join(long[] ids) {
    return StringUtil.join(ids);
  }

  public static String join(String[] names) {
    return StringUtil.join(names);
  }

  /*
   * Create Twitter4J Objects
   */
  public static RateLimitStatus newRateLimitStatus(String json) throws TwitterException {
    try {
      JsonNode n = mapper.readTree(json);
      return new RateLimitStatusImpl(n.get("remaining").asInt(), n.get("limit").asInt(), n.get("resetTimeInSeconds").asInt(), n.get("secondsUntilReset")
          .asInt());
    } catch (IOException e) {
      throw new TwitterException(e);
    }
  }

  /*
   * Sort
   */
  public static List<String> sortJsonByID(List<String> json) throws TwitterException {
    Map<Long, String> sort = new TreeMap<Long, String>();
    try {
      for (String s : json) {
        JsonNode n = TwitterObjects.mapper.readTree(s);
        sort.put(n.get("id").asLong(), s);
      }
    } catch (IOException e) {
      throw new TwitterException(e);
    }
    return new ArrayList<String>(sort.values());
  }

  public static Map<Long, String> jsonMap(String jsonMap) throws TwitterException {
    Map<Long, String> sort = new TreeMap<Long, String>();
    try {
      JsonNode map = mapper.readTree(jsonMap);
      for (Iterator<Entry<String, JsonNode>> it = map.get("id").fields(); it.hasNext();) {
        Entry<String, JsonNode> e = it.next();
        long id = Long.parseLong(e.getKey());
        if (!e.getValue().isNull()) {
          sort.put(id, e.getValue().toString());
        }
      }
    } catch (IOException e) {
      throw new TwitterException(e);
    }
    return sort;
  }

  public static List<String> jsonList(String json) throws TwitterException {
    List<String> list = new ArrayList<>();
    try {
      for (JsonNode n : TwitterObjects.mapper.readTree(json)) {
        list.add(n.toString());
      }
    } catch (IOException e) {
      throw new TwitterException(e);
    }
    return list;
  }

  public static <T> ResponseList<T> newResponseList() {
    return TwitterObjects.newResponseList(null);
  }

  public static <T> ResponseList<T> newResponseList(RateLimitStatus ratelimit) {
    return new ResponseListImpl<T>(ratelimit, 4);
  }

  public static ResponseList<Place> newPlaceResponseList(List<String> json) throws TwitterException {
    ResponseList<Place> list = newResponseList();
    for (String s : json) {
      list.add(TwitterObjectFactory.createPlace(s));
    }
    return list;
  }

  public static ResponseList<Location> newLocationResponseList(List<String> json) throws TwitterException {
    ResponseList<Location> list = newResponseList();
    for (String s : json) {
      list.add(TwitterObjectFactory.createLocation(s));
    }
    return list;
  }

  public static ResponseList<Status> newStatusResponseList(List<String> json) throws TwitterException {
    ResponseList<Status> list = newResponseList();
    for (String s : json) {
      list.add(TwitterObjectFactory.createStatus(s));
    }
    return list;
  }

  public static ResponseList<User> newUserResponseList(List<String> json) throws TwitterException {
    ResponseList<User> list = newResponseList();
    for (String s : json) {
      list.add(TwitterObjectFactory.createUser(s));
    }
    return list;
  }

  public static ResponseList<UserList> newUserListResponseList(List<String> json) throws TwitterException {
    ResponseList<UserList> list = newResponseList();
    for (String s : json) {
      list.add(TwitterObjectFactory.createUserList(s));
    }
    return list;
  }

  public static PagableResponseList<User> newPagableUser(String json) throws TwitterException {
    try {
      JsonNode node = TwitterObjects.mapper.readTree(json);
      PagableResponseList<User> list = new PagableResponseListImpl<>(node.get("previous_cursor").asLong(), node.get("next_cursor").asLong());
      for (JsonNode u : node.get("users")) {
        list.add(TwitterObjectFactory.createUser(u.toString()));
      }
      return list;
    } catch (IOException e) {
      throw new TwitterException(e);
    }
  }

  public static PagableResponseList<UserList> newPagableUserList(String json) throws TwitterException {
    try {
      JsonNode node = TwitterObjects.mapper.readTree(json);
      PagableResponseList<UserList> list = new PagableResponseListImpl<>(node.get("previous_cursor").asLong(), node.get("next_cursor").asLong());
      for (JsonNode s : node.get("lists")) {
        list.add(TwitterObjectFactory.createUserList(s.toString()));
      }
      return list;
    } catch (IOException e) {
      throw new TwitterException(e);
    }
  }

  public static List<String> getQueryResultTweets(String json) throws TwitterException {
    List<String> tweets = new ArrayList<String>();
    try {
      JsonNode node = TwitterObjects.mapper.readTree(json);
      for (JsonNode s : node.get("statuses")) {
        tweets.add(s.toString());
      }
      return tweets;
    } catch (IOException e) {
      throw new TwitterException(e);
    }
  }

  public static QueryResult newQueryResult(String json) throws TwitterException {
    return new QueryResultJSONImpl(json);
  }

  public static IDs newIDs(String json) throws TwitterException {
    return new IDsJSONImpl(json);
  }

  /*
   * List of IDs from List of Users
   */
  public static List<Long> getUserIDs(List<User> users) {
    List<Long> ids = new ArrayList<Long>();
    for (User u : users) {
      ids.add(u.getId());
    }
    return ids;
  }

  public static List<Long> getUserJsonIDs(List<String> users) {
    List<Long> ids = new ArrayList<Long>();
    for (String json : users) {
      try {
        User u = TwitterObjectFactory.createUser(json);
        ids.add(u.getId());
      } catch (TwitterException e) {
        System.out.println("Failed to Parse: " + json);
        e.printStackTrace();
      }
    }
    return ids;
  }

  /*
   * List of IDs from List of Statuses
   */
  public static List<Long> getTweetIDs(List<Status> statuses) {
    return getTweetIDs(statuses, true);
  }

  public static List<Long> getTweetIDs(List<Status> statuses, boolean includeRetweets) {
    List<Long> ids = new ArrayList<Long>();
    for (Status s : statuses) {
      ids.add(s.getId());
      if (includeRetweets && s.isRetweet()) {
        ids.add(s.getRetweetedStatus().getId());
      }
    }
    return ids;
  }

  /*
   * Adapted from http://code.google.com/p/guava-libraries/
   */
  public static <T> List<List<T>> partitionList(final List<T> list, final int size) {
    if (list == null) {
      throw new IllegalArgumentException("List must not be null");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Size must be greater than 0");
    }
    return new Partition<T>(list, size);
  }

  private static class Partition<T> extends AbstractList<List<T>> {
    private final List<T> list;
    private final int size;

    private Partition(final List<T> list, final int size) {
      this.list = list;
      this.size = size;
    }

    @Override
    public List<T> get(final int index) {
      final int listSize = size();
      if (listSize < 0) {
        throw new IllegalArgumentException("negative size: " + listSize);
      }
      if (index < 0) {
        throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
      }
      if (index >= listSize) {
        throw new IndexOutOfBoundsException("Index " + index + " must be less than size " + listSize);
      }
      final int start = index * size;
      final int end = Math.min(start + size, list.size());
      return list.subList(start, end);
    }

    @Override
    public int size() {
      return ((list.size() + size) - 1) / size;
    }

    @Override
    public boolean isEmpty() {
      return list.isEmpty();
    }
  }

}
