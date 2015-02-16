package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

import org.insight.twitter.wrapper.JSONResources.As;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

/*
 * Page Through Results with "Pages": fetchUserTimeline(ident, paging) fetchFavorites(ident, paging) fetchUserListStatuses(ident, slug, paging)
 */
public abstract class TwitterPage<K> {
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
   * Get a ResponseList<Status> page of results from somewhere:
   */
  public abstract List<Status> pageResponse(Paging page) throws TwitterException;
}
