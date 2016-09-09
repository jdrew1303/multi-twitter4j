package org.insight.twitter.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.insight.twitter.MultiTwitter;
import org.insight.twitter.util.PartitioningSpliterator;

import twitter4j.TwitterException;
import twitter4j.TwitterObjects;

/*
 * Hydrate very large collections of tweets, log deletions, progress:
 */
public class HydrateTweets {

  public static Map<Long, String> getTweetBatch(long[] batch, PrintWriter errors) {
    Map<Long, String> tweetBatch = new HashMap<Long, String>();
    try (MultiTwitter mt = new MultiTwitter()) {
      tweetBatch = mt.lookupJSON(batch);
    } catch (TwitterException e) {
      e.printStackTrace();
      for (long l : batch) {
        errors.println(l);
      }
    }
    return tweetBatch;
  }

  public static BiConsumer<Long, String> tweetConsumer(PrintWriter writerTweets, PrintWriter writerDeleted, PrintWriter writerLog) {
    return (Long id, String val) -> {
      if (val != null) {
        writerTweets.println(val);
      } else {
        writerTweets.println(String.format("{\"id\":%s,\"deleted\":true}", id));
        writerDeleted.println(id);
      }
      writerLog.println(id);
    };
  }

  public static PrintWriter writerForFile(File output) throws FileNotFoundException {
    // for tweets - large buffer, for ids - smaller
    return writerForFile(output, 20);
  }

  public static PrintWriter writerForFile(File output, int buffer) throws FileNotFoundException {
    return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, true), Charset.forName("utf-8")), buffer));
  }

  public static BufferedReader readerFromGZorTextFile(File input) throws FileNotFoundException, IOException {
    BufferedInputStream bis =
        input.getName().endsWith("gz") ? new BufferedInputStream(new GZIPInputStream(new FileInputStream(input)), 165536) : new BufferedInputStream(
            new FileInputStream(input), 165536);

    return new BufferedReader(new InputStreamReader(bis), 65536);
  }

  public static void main(String[] args) {
    Instant start = Instant.now();

    // eg:
    //args = new String[] { "~/tmp/test" };

    File inputIDs = new File(args[0] + "-tweetids.txt.gz");
    File outputTweets = new File(args[0] + "-tweets.jsonl");
    File outputDeletions = new File(args[0] + "-deleted.txt");
    File outputLog = new File(args[0] + "-log.txt");
    File outputErrors = new File(args[0] + "-error.txt");

    System.out.println("Checking files...");
    for (File file : Arrays.asList(outputTweets, outputDeletions, outputLog, outputErrors)) {
      try {
        Files.createFile(Paths.get(file.getAbsolutePath()));
      } catch (FileAlreadyExistsException e) {
        System.out.println(file + " exists...");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    double expectedLogSize = 0;
    // Estimate File size for progress:
    if (inputIDs.getName().endsWith("gz")) {
      try {
        RandomAccessFile raf = new RandomAccessFile(inputIDs, "r");
        raf.seek(raf.length() - 4);
        byte[] bytes = new byte[4];
        raf.read(bytes);
        expectedLogSize = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (expectedLogSize < 0) {
          expectedLogSize += (1L << 32);
        }
        raf.close();
      } catch (IOException e) {
        System.out.println("Can't get file size for progress!");
        e.printStackTrace();
      }
    } else {
      expectedLogSize = inputIDs.length();
    }

    // Output file stats:
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    scheduler.scheduleAtFixedRate(new IOFileProgress(outputLog, expectedLogSize, outputTweets, 5), 5, 5, TimeUnit.SECONDS);

    double soFar = ((outputLog.length() / expectedLogSize) * 100.0D);
    String message = String.format("Current progress: %.2f - %s expected %s", soFar, outputLog.length(), expectedLogSize);
    System.out.println(message);

    final ExecutorService executor = Executors.newWorkStealingPool(32);

    try (BufferedReader reader = readerFromGZorTextFile(inputIDs);
        PrintWriter writerDeleted = writerForFile(outputDeletions);
        PrintWriter writerErrors = writerForFile(outputErrors);
        PrintWriter writerLog = writerForFile(outputLog);
        PrintWriter writerTweets = writerForFile(outputTweets, 65566)) {

      System.out.println("Reading IDs...");
      Stream<Long> ids = reader.lines().mapToLong(Long::parseLong).boxed().sorted().sequential();
      Stream<List<Long>> partitioned = PartitioningSpliterator.partition(ids, 100, 100);
      System.out.println("Partitioned stream, Downloading Tweets.");

      partitioned.parallel().map(TwitterObjects::toPrimitive).parallel().forEach(batch -> CompletableFuture.supplyAsync(() -> {
        return getTweetBatch(batch, writerErrors);
      }, executor).thenAccept(m -> m.forEach(tweetConsumer(writerTweets, writerDeleted, writerLog))).join());

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // TODO: Resume / Count
    //System.out.println("Loaded " + wc.get() + " IDs.");

    Instant end = Instant.now();
    System.out.println("Done! " + Duration.between(start, end).getSeconds() + " sec.");

    scheduler.shutdownNow();
  }

  static class IOFileProgress implements Runnable {
    final File outputLog;
    final double expectedLogSize;
    final int delay;
    final File tweetFile;
    private long previousSizeBytes = 0L;

    public IOFileProgress(File outputLog, double expectedLogSize, File tweetFile, int delay) {
      this.outputLog = outputLog;
      this.expectedLogSize = expectedLogSize;
      this.tweetFile = tweetFile;
      this.delay = delay;
    }

    @Override
    public void run() {
      long currentSizeBytes = tweetFile.length();
      long currentLogSizeBytes = outputLog.length();

      double mbps = (currentSizeBytes - previousSizeBytes) / (1024.0D * 1024.0D * delay);
      double precentDone = ((currentLogSizeBytes / expectedLogSize) * 100.0D);
      double sizeinMB = (tweetFile.length() / (1024.0D * 1024.0D));

      String report = String.format("Tweet File: %.2f MB at %.2f MB/s : %.2f%%", sizeinMB, mbps, precentDone);
      System.out.println(report);

      previousSizeBytes = currentSizeBytes;
    }
  }
}
