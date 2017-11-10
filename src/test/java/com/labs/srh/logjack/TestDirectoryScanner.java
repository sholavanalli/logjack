package com.labs.srh.logjack;

import com.labs.srh.logjack.config.DirectoryScannerConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TestDirectoryScanner {

    private Path logDirPath;
    private ExecutorService executorService;
    private ExecutorService mainExecutorService;

    @Before
    public void setup() throws IOException {
        logDirPath = Files.createTempDirectory(FileSystems.getDefault().getPath("."), "dir_scanner_test_");
        assertTrue(Files.exists(logDirPath));
        executorService = Executors.newFixedThreadPool(1);
        mainExecutorService = Executors.newFixedThreadPool(1);
    }

    @After
    public void cleanup() throws IOException {
        deleteAllFilesInDir(logDirPath);
        Files.deleteIfExists(logDirPath);
        executorService.shutdownNow();
        mainExecutorService.shutdownNow();
        assertFalse(Files.exists(logDirPath));
    }

    private void deleteAllFilesInDir(Path dirPath) throws IOException {
        Files.walk(dirPath).filter(path -> !Files.isDirectory(path)).forEach(file -> {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void runDirectoryScannerAsync(DirectoryScanner dirScanner) {
        mainExecutorService.submit(() -> {
            try {
                dirScanner.run();
            } catch (Exception e) {
                Assert.assertTrue(e instanceof InterruptedException);
            }
        });
    }

    @Test
    public void testRun() throws Exception {

        // Create mocks for interaction.
        LogFileReader logFileReaderMock = mock(LogFileReader.class);
        DirectoryScannerConfig configMock = new DirectoryScannerConfig(5, 1, logDirPath.toString());
        LogFilesHolder logFilesHolderMock = new LogFilesHolder();

        // Create bean for testing.
        DirectoryScanner dirScanner = new DirectoryScanner(executorService, configMock, logFileReaderMock, logFilesHolderMock);
        runDirectoryScannerAsync(dirScanner);

        // Create first log file
        Path logFilePath1 = Files.createTempFile(logDirPath, "apache", ".log");
        Thread.sleep(20);

        // Verify first log file is detected and log reader is started.
        InOrder inOrder = inOrder(logFileReaderMock);
        assertEquals("Unexpected log files.", Collections.singleton(logFilePath1), logFilesHolderMock.getLogFilePaths());
        inOrder.verify(logFileReaderMock).startReading(eq(logFilePath1));

        // Create second log file
        Path logFilePath2 = Files.createTempFile(logDirPath, "rest", ".log");
        Thread.sleep(20);

        // Verify second log file is detected and log reader is started.
        assertEquals(new HashSet<>(Arrays.asList(logFilePath1, logFilePath2)), logFilesHolderMock.getLogFilePaths());
        inOrder.verify(logFileReaderMock).startReading(eq(logFilePath2));

        // Delete first log file
        Files.delete(logFilePath1);
        Thread.sleep(20);

        // Verify first log file delete is detected.
        assertEquals(Collections.singleton(logFilePath2), logFilesHolderMock.getLogFilePaths());

        // Verify no more log file reader interactions.
        verifyNoMoreInteractions(logFileReaderMock);
    }
}
