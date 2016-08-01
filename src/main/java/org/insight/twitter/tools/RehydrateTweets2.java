package org.insight.twitter.tools;

import gnu.trove.set.hash.TLongHashSet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.insight.twitter.MultiTwitter;
import org.insight.twitter.tools.RehydrateTweets.IOFileSpeed;
import org.insight.twitter.util.PartitioningSpliterator;

import twitter4j.TwitterObjects;

public class RehydrateTweets2 {

  static final Charset utf8 = Charset.forName("utf-8");

  public static Map<Long, String> getTweetBatch(long[] batch) {
    Map<Long, String> tweetBatch = new HashMap<Long, String>();
    try (MultiTwitter mt = new MultiTwitter()) {
      tweetBatch = mt.lookupJSON(batch);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tweetBatch;
  }

  public static void main(String[] args) throws IOException {
    Instant start = Instant.now();

    // Expects collectionName-tweetids.txt as input:

    String inputFile = args[0] + "-tweetids.txt";
    String processedFile = args[0] + "-log.txt";

    String deletedFile = args[0] + "-deleted.txt";
    String tweetFile = args[0] + "-tweets.jsonl";

    // Reader
    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(Files.newInputStream(Paths.get(inputFile)), 65535)));
    BufferedReader processedFileR = new BufferedReader(new InputStreamReader(new BufferedInputStream(Files.newInputStream(Paths.get(processedFile)), 65535)));

    System.out.println("Reading IDs...");

    // Processed IDs:
    TLongHashSet procesedIds = new TLongHashSet(44000000);
    processedFileR.lines().parallel().mapToLong(Long::parseLong).boxed().forEach(procesedIds::add);

    System.out.println("Loading " + procesedIds.size() + " IDs...");

    // Input IDs:
    Stream<Long> ids = reader.lines().parallel().mapToLong(Long::parseLong).boxed().filter(l -> (!procesedIds.contains(l))).parallel();
    Stream<List<Long>> partitioned = PartitioningSpliterator.partition(ids, 100, 100);

    System.out.println("Created Batches...");

    AtomicLong wc = new AtomicLong(); // Count Tweets

    BiConsumer<Long, String> w = (Long id, String val) -> {
      // Writers:
        try {
          PrintWriter processedFileW = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(processedFile, true), utf8)));;
          PrintWriter deletedFileW = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(deletedFile, true), utf8)));;
          PrintWriter tweetFileW = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tweetFile, true), utf8)));;

          processedFileW.println(id);
          wc.incrementAndGet();

          if (val != null) {
            tweetFileW.println(val);
          } else {
            tweetFileW.println(String.format("{\"id\":%s,\"deleted\":true}", id));
            deletedFileW.println(id);
          }

          processedFileW.close();
          deletedFileW.close();
          tweetFileW.close();
        } catch (Exception e) {

        }
      };

    //ExecutorService executor = Executors.newWorkStealingPool(32); // num of bots

    //final BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(32);
    //final RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
    //final ExecutorService executor = new ThreadPoolExecutor(32, 256, 0L, TimeUnit.MILLISECONDS, blockingQueue, rejectedExecutionHandler);

    // Output file stats:
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(new IOFileSpeed(new File(processedFile), wc), 5, 60, TimeUnit.SECONDS);

    try {
      partitioned.parallel().map(TwitterObjects::toPrimitive).parallel().map(batch -> getTweetBatch(batch)).parallel().forEach(m -> m.forEach(w));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("Loaded " + wc.get() + " IDs.");

    scheduler.shutdownNow();

    Instant end = Instant.now();

    System.out.println(Duration.between(start, end).getSeconds());

    System.out.println("Done!");
  }
}
