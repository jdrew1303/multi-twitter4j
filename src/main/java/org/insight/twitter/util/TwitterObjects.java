package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterResponse;

/*
 * Useful Things to do with Twitter Objects:
 */
public class TwitterObjects {

  public static final String TWITTER_DATE_FORMAT = "EEE MMM d HH:mm:ss Z yyyy";

  public static long[] toPrimitive(final Long[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return new long[0];
    }
    final long[] result = new long[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i].longValue();
    }
    return result;
  }

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


}
