package com.labs.srh.logjack;

import com.labs.srh.logjack.config.LogFileReaderConfig;
import com.labs.srh.logjack.sender.LogLinesSender;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LogFileReader {

    private static final Logger log = Logger.getLogger(LogFileReader.class.getName());

    private LogFileReaderConfig config;

    private LogLinesSender logLinesSender;

    /**
     * Constructor with all interactions in parameter for mock testing.
     */
    public LogFileReader(LogFileReaderConfig config, LogLinesSender logLinesSender) {
        this.config = config;
        this.logLinesSender = logLinesSender;
    }

    public void startReading(Path logFilePath) {
        ReadContext readContext = new ReadContext(logFilePath, false, 0, config.getMaxLogLinesToReadAtOnce());
        try {
            while (true) {

                // If log file is deleted then stop this task.
                if (isLogFileDeleted(readContext.getLogFilePath())) {
                    log.info(String.format("Log file %s is deleted. Stopping log file watcher.", readContext.getLogFilePath()));
                    return;
                }

                // Read few lines from log file and then send it.
                List<LogMessage> logMessages = getLogMessagesFromLogFile(readContext);
                if (!logMessages.isEmpty()) {
                    this.logLinesSender.send(logMessages);
                }

                // If end of log file has been reached then sleep for sometime for log file to get more log lines.
                if (readContext.isHasEndOfLogFileReached()) {
                    Thread.sleep(config.getLogFileReadIntervalMillis());
                }
            }
        } catch (Throwable t) {
            log.severe("Error encountered. Stopping watcher for log file: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private boolean isLogFileDeleted(Path logFilePath) throws Exception {
        return Files.notExists(logFilePath);
    }

    private List<LogMessage> getLogMessagesFromLogFile(ReadContext readContext) throws IOException {
        int numOfLinesRead = 0;
        List<LogMessage> logMessages = new ArrayList<>();

        // Open log file in read mode and seek up to the current position that has been read.
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(readContext.getLogFilePath().toFile(), "r")) {
            randomAccessFile.seek(readContext.getCurrentFilePointer());
            while (numOfLinesRead < readContext.getMaxLinesToRead() && randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                logMessages.add(new LogMessage(randomAccessFile.readLine(), readContext.getLogFilePath().getFileName().toString()));
                numOfLinesRead++;
            }
            readContext.setHasEndOfLogFileReached(randomAccessFile.getFilePointer() >= randomAccessFile.length());
            readContext.setCurrentFilePointer(randomAccessFile.getFilePointer());
        }
        return logMessages;
    }

    /**
     * Holds information about current read status and log read configuration.
     */
    private class ReadContext {

        private Path logFilePath;
        private boolean hasEndOfLogFileReached;
        private long currentFilePointer;
        private int maxLinesToRead;

        private ReadContext(Path logFilePath, boolean hasEndOfLogFileReached, long currentFilePointer, int maxLinesToRead) {
            this.logFilePath = logFilePath;
            this.hasEndOfLogFileReached = hasEndOfLogFileReached;
            this.currentFilePointer = currentFilePointer;
            this.maxLinesToRead = maxLinesToRead;
        }

        private Path getLogFilePath() {
            return logFilePath;
        }

        private boolean isHasEndOfLogFileReached() {
            return hasEndOfLogFileReached;
        }

        private void setHasEndOfLogFileReached(boolean hasEndOfLogFileReached) {
            this.hasEndOfLogFileReached = hasEndOfLogFileReached;
        }

        private long getCurrentFilePointer() {
            return currentFilePointer;
        }

        private void setCurrentFilePointer(long currentFilePointer) {
            this.currentFilePointer = currentFilePointer;
        }

        private int getMaxLinesToRead() {
            return maxLinesToRead;
        }
    }
}
