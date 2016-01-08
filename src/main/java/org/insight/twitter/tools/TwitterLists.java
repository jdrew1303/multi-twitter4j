package org.insight.twitter.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.insight.twitter.MultiTwitter;
import org.insight.twitter.util.SetComparison;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjects;
import twitter4j.UserList;

/*
 * Merge Twitter Lists
 */
public class TwitterLists {

  // Twitter App Write permissions
  public final Twitter ownerTwitter;

  public TwitterLists() {
    // With Write Permissions:
    ownerTwitter = new TwitterFactory("bot.owner").getInstance();
  }

  /*
   * Util:
   */
  public UserList list(String user, String slug) throws TwitterException {
    try (MultiTwitter multiTwitter = new MultiTwitter()) {
      return multiTwitter.list().showUserList(user, slug);
    }
  }

  public List<UserList> listsFrom(String user) throws TwitterException {
    try (MultiTwitter multiTwitter = new MultiTwitter()) {
      return multiTwitter.getUserListsOwnerships(user, 1000, -1);
    }
  }

  /*
   * Compare Lists:
   */

  public SetComparison<Long> compareLists(long xList, long yList) {

    List<String> xUsers = new ArrayList<String>();
    List<String> yUsers = new ArrayList<String>();

    try (MultiTwitter multiTwitter = new MultiTwitter()) {

      xUsers.addAll(multiTwitter.cursor().getBulkUserListMembers(xList));
      System.out.println(xUsers.size());
      yUsers.addAll(multiTwitter.cursor().getBulkUserListMembers(yList));
      System.out.println(yUsers.size());

    } catch (TwitterException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


    System.out.println(TwitterObjects.getUserJsonIDs(xUsers));
    System.out.println(TwitterObjects.getUserJsonIDs(yUsers));

    SetComparison<Long> changes =
        SetComparison.findChanges(new HashSet<Long>(TwitterObjects.getUserJsonIDs(xUsers)), new HashSet<Long>(TwitterObjects.getUserJsonIDs(yUsers)));

    return changes;
  }

  /*
   * Copy users from ownerTwitter lists to a single target list
   */
  public void merge(UserList target, List<UserList> sources) throws TwitterException, IOException {
    System.out.println("Target List: " + target.toString());
    HashSet<Long> userIDs = new HashSet<Long>();

    System.out.println("Found " + sources.size() + " list sources");
    int i = 0;
    for (UserList source : sources) {
      if (source == null) {
        System.err.println("ERROR! on source " + i);
        i++;
        continue;
      }
      // Get all members
      try (MultiTwitter multiTwitter = new MultiTwitter()) {
        List<String> src_members = multiTwitter.getBulkUserListMembers(source.getId());
        System.out.println(i + " Found " + src_members.size() + " members in " + source.getFullName());
        userIDs.addAll(TwitterObjects.getUserJsonIDs(src_members));
      }
      i++;
    }
    updateTwitter(target, userIDs);
  }

  /*
   *  From flat file of account names:
   */
  public void merge(UserList target, File screenNames) throws TwitterException, IOException {
    System.out.println("Target List: " + target.toString());
    HashSet<Long> userIDs = new HashSet<Long>();

    List<String> screen_names = Files.lines(screenNames.toPath()).collect(Collectors.toList());
    System.out.println("Source Names: " + screen_names.size());
    try (MultiTwitter multiTwitter = new MultiTwitter()) {
      List<String> users = multiTwitter.cursor().getBulkLookupUsers(screen_names);
      userIDs.addAll(TwitterObjects.getUserJsonIDs(users));
    }

    System.out.println("Total IDs: " + userIDs.size());
    updateTwitter(target, userIDs);
  }

  /*
   * Update List on Twitter:
   */
  public void updateTwitter(UserList target, Set<Long> userIDs) {
    /*
     *  Copy All Users to Target List:
     */
    List<Long> uids_list = new ArrayList<Long>(userIDs);

    List<List<Long>> batches = TwitterObjects.partitionList(uids_list, 100);
    for (List<Long> batch : batches) {
      System.out.println("Adding " + batch.size() + " members to " + target.getFullName());
      // Primitive array:
      long[] ids = TwitterObjects.toPrimitive(batch.toArray(new Long[batch.size()]));
      try {
        ownerTwitter.list().createUserListMembers(target.getId(), ids);
      } catch (TwitterException e) {
        System.err.println("FAILED TO ADD BATCH OF USERS! " + batch);
        e.printStackTrace();
      }
    }
    System.out.println("Finished Adding " + userIDs.size() + " members to " + target.getFullName());
  }

}
