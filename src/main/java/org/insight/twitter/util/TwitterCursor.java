package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.insight.twitter.wrapper.JSONResources.As;

import twitter4j.CursorSupport;
import twitter4j.TwitterException;
import twitter4j.TwitterResponse;

/*
 * Page Through Results with Cursors:
 */
public abstract class TwitterCursor<K> {
  public List<K> getElements(As type, final int maxElements) throws TwitterException {
    final int limit = (maxElements <= 0) ? Integer.MAX_VALUE : maxElements;
    List<K> elements = new ArrayList<>();
    long cursor = -1;
    try {
      while (cursor != 0) {
        CursorSupport pg = cursorResponse(cursor);
        elements.addAll(processElements(type, pg));
        // Limit check:
        if (elements.size() >= limit) {
          break;
        }
        cursor = pg.getNextCursor();
      }
      return elements;
    } catch (TwitterException e) {
      throw e;
    }
  }

  /*
   * Do something with elements on page:
   * By default, add all elements / json to list:
   */
  @SuppressWarnings("unchecked")
  public List<K> processElements(As type, CursorSupport page) throws TwitterException {
    if (type.equals(As.JSON)) {
      return (List<K>) TwitterObjects.getJSONList((List<? extends TwitterResponse>) page);
    } else {
      return (List<K>) page;
    }
  }

  /*
   * Get a CursorSupport page of results from somewhere:
   */
  public abstract CursorSupport cursorResponse(long cursor) throws TwitterException;



  /*
   * This is just for users/search - the only endpoint that doesn't do cursors or Paging...
   */
  public List<K> getManualPageElements(final String query, final int maxElements) throws TwitterException {
    final int limit = (maxElements <= 0) ? Integer.MAX_VALUE : maxElements;
    List<K> elements = new ArrayList<>();
    // Stopping Conditions: No more Results, Returning same result twice.
    Set<K> lastResult = new HashSet<K>();
    try {
      for (int page = 1; page < 50; page++) {

        List<K> pg = manualPageResponse(page);
        //System.out.println("Got " + pg.size() + "results..");
        // Stop Search Check:
        if (lastResult.containsAll(pg)) {
          //System.out.println("Got same results, stop.");
          break;
        }
        lastResult = new HashSet<K>(pg);
        elements.addAll(pg);
        // Limit check:
        if (elements.size() >= limit) {
          break;
        }
      }
      return elements;
    } catch (TwitterException e) {
      throw e;
    }
  }

  public List<K> manualPageResponse(int page) throws TwitterException {
    return null;
  }
}
