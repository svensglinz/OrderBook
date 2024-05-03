package MatchingEngine;

import java.util.concurrent.ExecutorService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * MatchingEngine.TradeLogger is responsible for creating log entries on executed trades.
 * The log entries will be written using a separate thread so as not to block the matching engine
 * Log entries will only be created if the matched trade has the attribute <code>XXX<code/> set to true
 */
public class TradeLogger {
    File logFile;
    PrintStream printStream;
    // log file writing is done on a separate thread
    ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * @param fileName Name of the file that the Logger should create and write to
     * @throws FileNotFoundException
     */
    public TradeLogger(String fileName) throws FileNotFoundException {
         logFile = new File(fileName);
         printStream = new PrintStream(logFile);
    }

    /**
     * @param msg Message that is written to the logfile
     */
    public void logTrade(String msg) {
        executor.submit(() -> {
            printStream.println(msg);
        });
    }

    /**
     * Shuts down the thread which writes to logFiles
     * The thread will wait for the completion of its tasks for a maximum of 600 seconds.
     * If it is not done by then, it will be aborted
     */
    public void shutdown() {
        executor.shutdown();
        try {
            // wait maximum 10 seconds to finish tasks before thread is shut down
            executor.awaitTermination(600, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //
        }
        printStream.close();
    }
}
