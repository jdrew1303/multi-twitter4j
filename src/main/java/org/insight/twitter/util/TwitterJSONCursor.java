package org.insight.twitter.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.TwitterObjects;

import com.fasterxml.jackson.databind.JsonNode;

/*
 * Page Through Results with Cursors:
 */
public abstract class TwitterJSONCursor {

  /*
   * Keep track of cursors
   */
  public List<String> getElements(String property, int maxElements) throws TwitterException {
    int limit = 0 >= maxElements ? Integer.MAX_VALUE : maxElements;
    List<String> elements = new ArrayList<String>();
    long cursor = -1;
    while (cursor != 0) {
      try {
        JsonNode c = TwitterObjects.mapper.readTree(this.cursorResponse(cursor));
        elements.addAll(this.processElements(c, property));
        // Limit check:
        if (elements.size() >= limit) {
          break;
        }
        cursor = c.get("next_cursor").asLong();

      } catch (IOException e) {
        throw new TwitterException(e);
      }
    }
    return elements;

  }

  /*
   * Do something with elements on page:
   * By default, add all elements / json to list:
   */
  private List<String> processElements(JsonNode page, String property) {
    List<String> elements = new ArrayList<>();
    for (JsonNode u : page.get(property)) {
      elements.add(u.toString());
    }
    return elements;
  }

  /*
   * Get a CursorSupport page of results from somewhere:
   */
  public abstract String cursorResponse(long cursor) throws TwitterException;

}
