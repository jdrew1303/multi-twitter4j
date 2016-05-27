package org.insight.twitter.tools;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.insight.twitter.MultiTwitter;

import twitter4j.TwitterException;

/*
 * Take 1 Tweet ID per line -> Ouptup File with 1 JSON Tweet Per Line.
 */
public class RehydrateTweets {

  public static void main(String[] args) throws TwitterException, IOException {
    Charset utf8 = Charset.forName("utf-8");

    String inputFile = args[0];
    String outputFile = args[1];

    MultiTwitter mt = new MultiTwitter();

    List<String> lines = Files.readAllLines(Paths.get(inputFile), utf8);
    List<Long> ids = convertList(lines, s -> Long.parseLong(s));

    try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), utf8)))) {

      // Apply this to each tweet:
      BiConsumer<Long, String> w = (Long id, String val) -> {
        try {
          if (val != null) {
            out.println(val);
          } else {
            out.println(String.format("{\"id\":%s,\"deleted\":true}\n", id));
            System.out.println("Deleted!" + " Tweet:" + id);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      };

      System.out.println("Processing : " + ids.size() + " Tweets.");

      mt.getBulkTweetLookupMap(ids, w::accept);

      System.out.println("Finished.");
      mt.close();

    } catch (IOException e) {
      System.err.println("IO ERROR!");
      e.printStackTrace();
    }

  }

  public static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
    return from.stream().map(func).collect(Collectors.toList());
  }

}
