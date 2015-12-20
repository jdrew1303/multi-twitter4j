package org.insight.twitter.util;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjects;

/*
 * Page Through Results with "Pages"
 */
public abstract class TwitterQueryPage {

  /*
   * Get a ResponseList<Status> page of results from somewhere:
   */
  public abstract String pageResponse(Query query) throws TwitterException;

  /*
   * Keep Track of Paging for Timeline-like resources:
   */
  public List<String> getElements(Query query, int maxElements) throws TwitterException {
    int limit = 0 >= maxElements ? Integer.MAX_VALUE : maxElements;
    List<String> elements = new ArrayList<>();
    query.setCount(100);
    boolean hasNext;
    do {
      String resultJSON = pageResponse(query);
      QueryResult r = TwitterObjects.newQueryResult(resultJSON);
      hasNext = r.hasNext();
      List<Status> pg = r.getTweets();
      // Stop when Tweet Limit or SinceID limit:
      if (pg.size() < 1) {
        break;
      }
      // Process Results somehow:
      elements.addAll(TwitterObjects.getQueryResultTweets(resultJSON));
      // Stop when max items reached:
      if (elements.size() >= limit) {
        break;
      }
      //query.setMaxId(r.getMaxId() - 1);
      query = r.nextQuery();
    } while (hasNext);
    return elements;
  }
}
