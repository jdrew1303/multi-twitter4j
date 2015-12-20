package org.insight.twitter.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

// TODO: A better one...
public class OAuthSetup {

  public boolean run(String[] args) {

    String consumerKey = args[0];
    String consumerSecret = args[1];

    try {
      this.authorize(consumerKey, consumerSecret);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void authorize(String consumerKey, String consumerSecret) throws TwitterException, IOException {
    Twitter twitter = new TwitterFactory().getInstance();

    // Do not set if defined in twitter4j.properties:
    // twitter.setOAuthConsumer(consumerKey, consumerSecret);

    RequestToken requestToken = twitter.getOAuthRequestToken();
    AccessToken accessToken = null;

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      while (accessToken == null) {
        System.out.println("Open the following URL and grant access to your account:");
        System.out.println(requestToken.getAuthorizationURL());
        System.out.print("Enter the PIN (if available) or just hit enter [PIN]:");
        String pin = br.readLine();
        try {
          if (!pin.isEmpty()) {
            accessToken = twitter.getOAuthAccessToken(requestToken, pin);
          } else {
            accessToken = twitter.getOAuthAccessToken();
          }
        } catch (TwitterException te) {
          if (401 == te.getStatusCode()) {
            System.out.println("Unable to get the access token.");
          }
          te.printStackTrace();
        }
      }
    }

    // persist to the accessToken for future reference.
    String screenName = accessToken.getScreenName().toLowerCase();

    System.out.println("Screenname: " + screenName);
    System.out.println(twitter.verifyCredentials().getId());
    System.out.println("Token : " + accessToken.getToken());
    System.out.println("TokenSecret : " + accessToken.getTokenSecret());

    String str =
        String.format("bot.%s.oauth.consumerKey=%s\n" + "bot.%s.oauth.consumerSecret=%s\n" + "bot.%s.oauth.accessToken=%s\n"
            + "bot.%s.oauth.accessTokenSecret=%s\n", screenName, consumerKey, screenName, consumerSecret, screenName, accessToken.getToken(), screenName,
            accessToken.getTokenSecret());

    Files.write(Paths.get(screenName + ".txt"), str.getBytes());

    System.out.println("Stored. Run Again to auth another account... ");

  }

  /**
   * Main method
   */
  public static void main(String[] args) {
    OAuthSetup app = new OAuthSetup();
    if (!app.run(args)) {
      System.exit(1);
    }
  }
}
