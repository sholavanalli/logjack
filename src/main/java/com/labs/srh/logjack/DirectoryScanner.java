package com.labs.srh.logjack;

import com.labs.srh.logjack.config.DirectoryScannerConfig;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryScanner implements DisposableBean {

    private static final Logger log = Logger.getLogger(DirectoryScanner.class.getName());

    private DirectoryScannerConfig config;

    private ExecutorService executorService;

    private LogFileReader logFileReader;

    private LogFilesHolder logFilesHolder;

    public DirectoryScanner(ExecutorService executorService, DirectoryScannerConfig config, LogFileReader logFileReader,
                            LogFilesHolder logFilesHolder) {
        this.executorService = executorService;
        this.logFilesHolder = logFilesHolder;
        this.config = config;
        this.logFileReader = logFileReader;
    }

    public void run() throws InterruptedException, IOException {

        Path logDirPath = FileSystems.getDefault().getPath(this.config.getDirectoryPathToScan());
        if (!Files.isDirectory(logDirPath)) {
            log.log(Level.SEVERE, "Configured path is not a directory: " + logDirPath.toAbsolutePath());
            return;
        }

        while (true) {

            // Find current active log file paths and new active log file paths.
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(logDirPath)) {
                dirStream.forEach(oneDirPath -> {
                    if (!Files.isDirectory(oneDirPath) && oneDirPath.getFileName().toString().endsWith(".log")) {
                        logFilesHolder.add(oneDirPath);
                    }
                });
            }

            // Start a log file watcher for every new active log file path.
            logFilesHolder.getNewLogFilePaths().forEach(path -> {
                log.info("Starting file watcher for: " + path);
                executorService.submit(() -> logFileReader.startReading(path));
            });

            // Remove inactive log files.
            logFilesHolder.refresh();

            Thread.sleep(this.config.getDirectoryScanIntervalMillis());
        }
    }

    /**
     * Force all active log reader threads to shut down when the program is ready to exit.
     */
    private void stopAllLogFileReaders() {
        if (this.executorService != null) {
            log.info("Directory scanner shutting down. Stopping all active log file reader threads.");
            executorService.shutdownNow();
        }
    }

    @Override
    public void destroy() throws Exception {
        stopAllLogFileReaders();
    }
}
