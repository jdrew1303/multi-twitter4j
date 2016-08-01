package org.insight.twitter.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.insight.twitter.MultiTwitter;
import org.insight.twitter.util.PartitioningSpliterator;

import twitter4j.TwitterException;
import twitter4j.TwitterObjects;

/*
 * Take 1 Tweet ID per line -> Ouptup File with 1 JSON Tweet Per Line.
 */
public class RehydrateTweets {

  static final long secondDelay = 60;
  static final Charset utf8 = Charset.forName("utf-8");
  static final DateTimeFormatter STREAM_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  public static void main(String[] args) throws TwitterException, IOException, InterruptedException, ExecutionException {
    Charset utf8 = Charset.forName("utf-8");

    // Expects collectionName-tweetids.txt.gz as input:

    String inputFile = args[0] + "-tweetids.txt.gz";
    String outputFile = args[0] + "-tweets.jsonl";
    String outputFileDel = args[0] + "-tweetids.deleted.txt";

    String processedFile = args[0] + "-log.txt";

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    final ExecutorService executor = Executors.newWorkStealingPool(64); // num of bots / threads

    InputStream in = Files.newInputStream(Paths.get(inputFile));
    BufferedInputStream bufferedIn = new BufferedInputStream(in, 65535);
    GZIPInputStream gzipIn = new GZIPInputStream(bufferedIn);
    BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIn));

    // recover from errors:
    long skipn = 0L; // eg: 3003700L;

    AtomicLong wc = new AtomicLong(skipn);

    scheduler.scheduleAtFixedRate(new IOFileSpeed(new File(outputFile), wc), secondDelay, secondDelay, TimeUnit.SECONDS);

    // Apply this to each tweet:
    BiConsumer<Long, String> w = (Long id, String val) -> {
      wc.incrementAndGet();

      FileOutputStream out = null;
      PrintWriter writerDeleted = null;

      try {
        out = new FileOutputStream(outputFile, true);
        writerDeleted = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileDel, true), utf8)));
      } catch (Exception e1) {
        e1.printStackTrace();
      }

      BufferedOutputStream bufferedOut = new BufferedOutputStream(out, 65535);
      //GZIPOutputStream gzipOut = new GZIPOutputStream(bufferedOut);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(bufferedOut, utf8));

        try {
          if (val != null) {
            writer.println(val);
          } else {
            writer.println(String.format("{\"id\":%s,\"deleted\":true}", id));
            writerDeleted.println(id);
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {

          writer.flush();
          writer.close();

          writerDeleted.close();
        }
      };

    Stream<Long> ids = reader.lines().mapToLong(Long::parseLong).boxed().sorted().sequential().filter(id -> (id > 624115908093411328L));
    System.out.println("Processing Tweets..." + " w skip: " + skipn);

    Stream<List<Long>> partitioned = PartitioningSpliterator.partition(ids, 100, 100);
    System.out.println("Partitioned stream ...");

    partitioned.parallel().map(TwitterObjects::toPrimitive).forEach(batch -> CompletableFuture.supplyAsync(() -> {
      System.out.println("Batch... " + batch.length + " " + batch[0]);
      Map<Long, String> m = new HashMap<Long, String>();
      try (MultiTwitter mt = new MultiTwitter()) {
        m = mt.lookupJSON(batch);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return m;
    }, executor).thenAccept(m -> m.forEach(w)).join());

    System.out.println("Finished.");

  }

  static class IOFileSpeed implements Runnable {
    File output;
    Long previousSizeBytes = 0L;
    AtomicLong wc;

    public IOFileSpeed(File output, AtomicLong wc) {
      this.output = output;
      this.wc = wc;
    }

    @Override
    public void run() {
      long currentSizeBytes = output.length();
      Double mbps = (currentSizeBytes - previousSizeBytes) / (1024.0D * 1024.0D * secondDelay);
      previousSizeBytes = currentSizeBytes;
      System.out.println(String.format("%s File Size: %.2f MB at %.2f MB/s : %s wc of 43956390 : \t\t\t\t\t %.2f done...", output.getName(),
          (output.length() / (1024.0D * 1024.0D)), mbps, wc.get(), (wc.get() / 439563.900F)));
    }
  }

}
