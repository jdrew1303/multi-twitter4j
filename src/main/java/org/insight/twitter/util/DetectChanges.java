package org.insight.twitter.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter4j.User;

/*
 * Compare Twitter Objects - Return Changes
 */
public class DetectChanges {

  public static <T> SetComparison<T> getDifferences(List<T> list1, List<T> list2) {
    return SetComparison.findChanges(new HashSet<T>(list1), new HashSet<T>(list2));
  }

  public static SetComparison<User> getUserDifferences(List<User> list1, List<User> list2) {
    return SetComparison.findChanges(new HashSet<User>(list1), new HashSet<User>(list2));
  }

  public static Set<User> getNewUsers(List<User> list1, List<User> list2) {
    return getUserDifferences(list1, list2).additions();
  }

}
