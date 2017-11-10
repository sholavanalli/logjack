package com.labs.srh.logjack.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.logging.Logger;

public class AppConfig {

    private static final Logger log = Logger.getLogger(AppConfig.class.getName());

    private static AppConfig appConfigInstance;

    private DirectoryScannerConfig directoryScannerConfig;
    private LogFileReaderConfig logFileReaderConfig;
    private HttpLogLinesSenderConfig httpLogLinesSenderConfig;
    private SenderTypeConfig senderTypeConfig;

    private AppConfig(DirectoryScannerConfig directoryScannerConfig, LogFileReaderConfig logFileReaderConfig,
                      HttpLogLinesSenderConfig httpLogLinesSenderConfig, SenderTypeConfig senderTypeConfig) {
        this.directoryScannerConfig = directoryScannerConfig;
        this.logFileReaderConfig = logFileReaderConfig;
        this.httpLogLinesSenderConfig = httpLogLinesSenderConfig;
        this.senderTypeConfig = senderTypeConfig;
    }

    public DirectoryScannerConfig getDirectoryScannerConfig() {
        return directoryScannerConfig;
    }

    public void setDirectoryScannerConfig(DirectoryScannerConfig directoryScannerConfig) {
        this.directoryScannerConfig = directoryScannerConfig;
    }

    public LogFileReaderConfig getLogFileReaderConfig() {
        return logFileReaderConfig;
    }

    public void setLogFileReaderConfig(LogFileReaderConfig logFileReaderConfig) {
        this.logFileReaderConfig = logFileReaderConfig;
    }

    public HttpLogLinesSenderConfig getHttpLogLinesSenderConfig() {
        return httpLogLinesSenderConfig;
    }

    public void setHttpLogLinesSenderConfig(HttpLogLinesSenderConfig httpLogLinesSenderConfig) {
        this.httpLogLinesSenderConfig = httpLogLinesSenderConfig;
    }

    public SenderTypeConfig getSenderTypeConfig() {
        return senderTypeConfig;
    }

    public void setSenderTypeConfig(SenderTypeConfig senderTypeConfig) {
        this.senderTypeConfig = senderTypeConfig;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "directoryScannerConfig=" + directoryScannerConfig +
                ", logFileReaderConfig=" + logFileReaderConfig +
                ", logLinesSenderConfig=" + httpLogLinesSenderConfig +
                ", senderTypeConfig=" + senderTypeConfig +
                '}';
    }

    public static AppConfig get(String configDir) {

        if (appConfigInstance == null) {
            synchronized (AppConfig.class) {
                if (appConfigInstance == null) {
                    // Defaults for all configurations
                    DirectoryScannerConfig directoryScannerConfig = new DirectoryScannerConfig(500, 10, ".");
                    LogFileReaderConfig logFileReaderConfig = new LogFileReaderConfig(100, 100);
                    HttpLogLinesSenderConfig httpLogLinesSenderConfig = new HttpLogLinesSenderConfig("https://localhost/rest/log", "",
                            "", 1000);
                    SenderTypeConfig senderTypeConfig = new SenderTypeConfig("http");
                    appConfigInstance = new AppConfig(directoryScannerConfig, logFileReaderConfig, httpLogLinesSenderConfig,
                            senderTypeConfig);

                    // Override default app configuration from YAML file if defined.
                    try (InputStream inputStream = Files.newInputStream(FileSystems.getDefault().getPath(configDir, "config.yml"))) {
                        Iterable<Object> configs = new Yaml().loadAll(inputStream);
                        for (Object config : configs) {
                            if (config instanceof DirectoryScannerConfig) {
                                directoryScannerConfig = (DirectoryScannerConfig) config;
                            } else if (config instanceof LogFileReaderConfig) {
                                logFileReaderConfig = (LogFileReaderConfig) config;
                            } else if (config instanceof HttpLogLinesSenderConfig) {
                                httpLogLinesSenderConfig = (HttpLogLinesSenderConfig) config;
                            } else if (config instanceof SenderTypeConfig) {
                                senderTypeConfig = (SenderTypeConfig) config;
                            }
                        }
                        appConfigInstance = new AppConfig(directoryScannerConfig, logFileReaderConfig, httpLogLinesSenderConfig, senderTypeConfig);
                    } catch (IOException e) {
                        log.warning(String.format("Error reading configuration. Reason: %s", e.getMessage()));
                        log.info(String.format("Using default configuration %s: ", appConfigInstance));
                    }
                }
            }
        }
        return appConfigInstance;
    }
}
