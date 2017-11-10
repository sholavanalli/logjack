package com.labs.srh.logjack;

import com.labs.srh.logjack.config.AppConfig;
import com.labs.srh.logjack.sender.LogLinesSender;
import com.labs.srh.logjack.sender.http.CustomHttpClient;
import com.labs.srh.logjack.sender.http.HttpLogLinesSender;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class App {

    // Configuration beans ---------------------------------------------------------------------------------------------

    @Bean
    public AppConfig appConfig(ConfigurableApplicationContext applicationContext) {
        return AppConfig.get(applicationContext.getEnvironment().getProperty("user.dir", "."));
    }

    // Utility beans ---------------------------------------------------------------------------------------------------

    @Bean
    public ExecutorService executorService(AppConfig appConfig) {
        return Executors.newFixedThreadPool(appConfig.getDirectoryScannerConfig().getNumberOfLogFileReaders());
    }

    @Bean
    public CustomHttpClient customHttpClient() {
        return new CustomHttpClient();
    }

    // Application beans -----------------------------------------------------------------------------------------------

    @Bean
    public DirectoryScanner directoryScanner(AppConfig appConfig, ExecutorService executorService,
                                             LogFileReader logFileReader, LogFilesHolder logFilesHolder) {
        return new DirectoryScanner(executorService, appConfig.getDirectoryScannerConfig(), logFileReader, logFilesHolder);
    }

    @Bean
    public LogFileReader logFileReader(AppConfig appConfig, LogLinesSender logLinesSender) {
        // This reader will be wired with the configured sender.
        return new LogFileReader(appConfig.getLogFileReaderConfig(), logLinesSender);
    }

    @Bean
    public LogLinesSender logLinesSender(AppConfig appConfig) {
        if ("http".equals(appConfig.getSenderTypeConfig())) {
            return new HttpLogLinesSender(appConfig.getHttpLogLinesSenderConfig(), customHttpClient());
        } else {
            return new HttpLogLinesSender(appConfig.getHttpLogLinesSenderConfig(), customHttpClient());
        }
    }

    @Bean
    public LogFilesHolder logFilesHolder() {
        return new LogFilesHolder();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(App.class);
        DirectoryScanner directoryScanner = applicationContext.getBean(DirectoryScanner.class);
        directoryScanner.run();
        applicationContext.close();
    }
}
