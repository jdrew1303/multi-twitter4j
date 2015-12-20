package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

import twitter4j.CursorSupport;
import twitter4j.IDs;
import twitter4j.TwitterException;

/*
 * Page Through Results with Cursors:
 */
public abstract class TwitterCursor<K> {

  /*
   * Keep track of cursors
   */
  @SuppressWarnings("unchecked")
  public List<K> getElements(int maxElements) throws TwitterException {
    int limit = 0 >= maxElements ? Integer.MAX_VALUE : maxElements;
    List<K> elements = new ArrayList<K>();
    long cursor = -1;
    while (cursor != 0) {
      CursorSupport pg = this.cursorResponse(cursor);
      // Treat IDs differently - IDs is not a list, but an array.
      if (pg instanceof IDs) {
        long[] ids = ((IDs) pg).getIDs();
        for (Long l : ids) {
          elements.add((K) l);
        }
      } else {
        elements.addAll((List<K>) pg);
      }
      // Limit check:
      if (elements.size() >= limit) {
        break;
      }
      cursor = pg.getNextCursor();
    }
    return elements;
  }

  /*
   * Get a CursorSupport page of results from somewhere:
   */
  public abstract CursorSupport cursorResponse(long cursor) throws TwitterException;

}
