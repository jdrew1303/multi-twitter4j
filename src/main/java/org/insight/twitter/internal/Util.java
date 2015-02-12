package org.insight.twitter.internal;

public class Util {

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



}
