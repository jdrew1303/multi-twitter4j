package org.insight.twitter.util;

import java.util.AbstractList;
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
