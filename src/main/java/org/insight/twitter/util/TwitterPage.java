package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.insight.twitter.wrapper.JSONResources.As;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

/*
 * Page Through Results with "Pages": fetchUserTimeline(ident, paging) fetchFavorites(ident, paging) fetchUserListStatuses(ident, slug, paging) users/search
 * search
 */
public abstract class TwitterPage<K> {

  /*
   * Get a ResponseList<Status> page of results from somewhere:
   */
  public abstract List<Status> pageResponse(Paging page) throws TwitterException;

  /*
   * Keep Track of Paging for Timeline-like resources:
   */
  public List<K> getElements(As type, final long initSinceId, final long initMaxId, final int maxElements) throws TwitterException {
    final int limit = (maxElements <= 0) ? Integer.MAX_VALUE : maxElements;
    List<K> elements = new ArrayList<>();
    Paging paging = new Paging();
    paging.setCount(200);
    if (initMaxId > 0) {
      paging.setMaxId(initMaxId);
    }
    if (initSinceId > 0) {
      paging.setSinceId(initSinceId);
    }
    try {
      int retrieved = 0;
      do {
        List<Status> pg = pageResponse(paging);
        retrieved = pg.size();
        // Stop when Tweet Limit or SinceID limit:
        if (pg.size() < 1) {
          break;
        }
        // Process Results somehow:
        elements.addAll(processElements(type, pg));
        // Stop when max items reached:
        if (elements.size() >= limit) {
          break;
        }
        paging.setMaxId(pg.get(pg.size() - 1).getId() - 1);
      } while (retrieved > 0);
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
  public List<K> processElements(As type, List<Status> pg) throws TwitterException {
    if (type.equals(As.JSON)) {
      return (List<K>) TwitterObjects.getJSONList(pg);
    }
    return (List<K>) pg;
  }

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
    throw new TwitterException("Manual Paging Method is for users/search only!");
  }
}
