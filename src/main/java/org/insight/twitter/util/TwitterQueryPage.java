package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

import org.insight.twitter.wrapper.JSONResources.As;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

/*
 * Page Through Results with "Pages": fetchUserTimeline(ident, paging) fetchFavorites(ident, paging) fetchUserListStatuses(ident, slug, paging) users/search
 * search
 */
public abstract class TwitterQueryPage<K> {

  /*
   * Get a ResponseList<Status> page of results from somewhere:
   */
  public abstract QueryResult pageResponse(Query query) throws TwitterException;

  /*
   * Keep Track of Paging for Timeline-like resources:
   */
  public List<K> getElements(As type, Query query, final int maxElements) throws TwitterException {
    final int limit = (maxElements <= 0) ? Integer.MAX_VALUE : maxElements;
    List<K> elements = new ArrayList<>();
    query.setCount(100);
    try {
      boolean hasNext = true;
      do {
        //System.out.println("Query: " + query.toString());
        QueryResult r = pageResponse(query);
        //System.out.println("result: " + r.toString());
        hasNext = r.hasNext();
        //System.out.println("next: " + r.hasNext());
        //System.out.println("next: " + r.nextQuery());
        List<Status> pg = r.getTweets();
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

        //query.setMaxId(r.getMaxId() - 1);
        query = r.nextQuery();

      } while (hasNext);
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
}
