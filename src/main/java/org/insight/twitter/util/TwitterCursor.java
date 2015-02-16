package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

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
    }
    return (List<K>) page;
  }

  /*
   * Get a CursorSupport page of results from somewhere:
   */
  public abstract CursorSupport cursorResponse(long cursor) throws TwitterException;
}
