package org.insight.twitter.util;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.TwitterObjects;

/*
 * Page Through Results with "Pages"
 */
public abstract class TwitterQueryPageStream {

  /*
   * Get a ResponseList<Status> page of results from somewhere:
   */
  public abstract String pageResponse(Query query) throws TwitterException;

  public abstract void process(List<String> results);

  /*
   * Keep Track of Paging for Timeline-like resources:
   */
  public void getElements(Query query) throws TwitterException {
    boolean hasNext;
    do {
      String resultJSON = pageResponse(query);
      QueryResult r = TwitterObjects.newQueryResult(resultJSON);

      process(TwitterObjects.getQueryResultTweets(resultJSON));

      query = r.nextQuery();
      hasNext = r.hasNext();
    } while (hasNext);

  }
}
