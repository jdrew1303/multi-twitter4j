package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

/*
 * Page Through Results with "Pages": fetchUserTimeline(ident, paging) fetchFavorites(ident, paging) fetchUserListStatuses(ident, slug, paging)
 */
public abstract class TwitterPage {

  /*
   * Get a page of results from somewhere:
   */
  public abstract List<String> pageResponse(Paging page) throws TwitterException;

  /*
   * Keep Track of Paging for Timeline-like resources:
   */
  public List<String> getElements(long initSinceId, long initMaxId, int maxElements) throws TwitterException {
    int limit = 0 >= maxElements ? Integer.MAX_VALUE : maxElements;

    List<String> elements = new ArrayList<String>();

    Paging paging = new Paging();
    paging.setCount(200);

    if (initMaxId > 0) {
      paging.setMaxId(initMaxId);
    }

    if (initSinceId > 0) {
      paging.setSinceId(initSinceId);
    }

    int retrieved;

    do {

      List<String> pg = pageResponse(paging);
      retrieved = pg.size();
      // Stop when Tweet Limit or SinceID limit:
      if (retrieved < 1) {
        break;
      }

      elements.addAll(pg);

      // Stop when max items reached:
      if (elements.size() >= limit) {
        break;
      }

      // Find largest / last ID:
      String last = pg.get(pg.size() - 1);
      long id = TwitterObjectFactory.createStatus(last).getId();
      paging.setMaxId(id - 1);

    } while (retrieved > 0);

    return elements;
  }
}
