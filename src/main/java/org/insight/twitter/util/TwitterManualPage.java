package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter4j.TwitterException;

/*
 * This is just for users/search - the only endpoint that doesn't do cursors or Paging...
 */
public abstract class TwitterManualPage {

  public List<String> getElements(int maxElements) throws TwitterException {
    int limit = 0 >= maxElements ? Integer.MAX_VALUE : maxElements;
    List<String> elements = new ArrayList<String>();
    // Stopping Conditions: No more Results, Returning same result twice.
    Set<String> lastResult = new HashSet<String>();
    for (int page = 1; page < 50; page++) {
      List<String> pg = this.pageResponse(page);
      if (lastResult.containsAll(pg)) {
        break; // Got same results twice, stop.
      }
      lastResult = new HashSet<String>(pg);
      elements.addAll(pg);
      // Limit check:
      if (elements.size() >= limit) {
        break;
      }
    }
    return elements;
  }

  public abstract List<String> pageResponse(int page) throws TwitterException;

}
