package com.labs.srh.logjack;

import com.labs.srh.logjack.config.LogFileReaderConfig;
import com.labs.srh.logjack.sender.LogLinesSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestLogFileReader {

    private Path logFileDir;
    private Path logFilePath;
    private ExecutorService executorService;
    private FileWriter logFileWriter;

    @Before
    public void setup() throws IOException {
        logFileDir = Files.createTempDirectory(FileSystems.getDefault().getPath("."), "log_file_reader_test");
        assertTrue(Files.exists(logFileDir));
        logFilePath = Files.createTempFile(logFileDir, "apache", ".log");
        assertTrue(Files.exists(logFilePath));
        executorService = Executors.newFixedThreadPool(1);
        logFileWriter = new FileWriter(logFilePath.toFile());
    }

    @After
    public void cleanup() throws IOException {
        deleteAllFilesInDir(logFileDir);
        Files.deleteIfExists(logFileDir);
        executorService.shutdownNow();
        assertFalse(Files.exists(logFileDir));
        logFileWriter.close();
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

    private void runLogFileReaderAsync(LogFileReader logFileReader) {
        executorService.submit(() -> logFileReader.startReading(logFilePath));
    }

    @Test
    public void testRun() throws Exception {

        // Create mocks for interactions.
        LogFileReaderConfig logFileReaderConfigMock = new LogFileReaderConfig(3, 5);
        LogLinesSender logLinesSenderMock = mock(LogLinesSender.class);

        LogFileReader logFileReaderMock = new LogFileReader(logFileReaderConfigMock, logLinesSenderMock);
        runLogFileReaderAsync(logFileReaderMock);

        // Write 2 lines to log file. This is one less than max lines to read from log file.
        writeLinesToLogFile("line1", "line2");
        Thread.sleep(20);

        // Verify the 2 log lines are sent to log lines sender.
        InOrder inOrder = inOrder(logLinesSenderMock);
        inOrder.verify(logLinesSenderMock).send(eq(Arrays.asList(
                new LogMessage("line1", logFilePath.getFileName().toString()),
                new LogMessage("line2", logFilePath.getFileName().toString()))));

        // Write 4 lines to log file. This is one more than max lines to read from log file.
        writeLinesToLogFile("line3", "line4", "line5", "line6");
        Thread.sleep(20);

        // Verify the 4 new lines appended are sent in 2 batches to the log lines sender. 3 lines are sent first and
        // the remaining 1 is sent next.
        inOrder.verify(logLinesSenderMock).send(eq(Arrays.asList(
                new LogMessage("line3", logFilePath.getFileName().toString()),
                new LogMessage("line4", logFilePath.getFileName().toString()),
                new LogMessage("line5", logFilePath.getFileName().toString()))));
        inOrder.verify(logLinesSenderMock).send(eq(Collections.singletonList(
                new LogMessage("line6", logFilePath.getFileName().toString()))));

        // Verify no more interactions with log lines sender.
        verifyNoMoreInteractions(logLinesSenderMock);
    }

    private void writeLinesToLogFile(String... lines) throws IOException {
        for (String line : lines) {
            this.logFileWriter.write(line + "\n");
        }
        this.logFileWriter.flush();
    }
}
