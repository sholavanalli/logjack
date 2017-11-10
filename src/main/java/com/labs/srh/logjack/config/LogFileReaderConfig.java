package com.labs.srh.logjack.config;

public class LogFileReaderConfig {

    private int maxLogLinesToReadAtOnce;
    private long logFileReadIntervalMillis;

    public LogFileReaderConfig() {
    }

    public LogFileReaderConfig(int maxLogLinesToReadAtOnce, long logFileReadIntervalMillis) {
        this.maxLogLinesToReadAtOnce = maxLogLinesToReadAtOnce;
        this.logFileReadIntervalMillis = logFileReadIntervalMillis;
    }

    public int getMaxLogLinesToReadAtOnce() {
        return maxLogLinesToReadAtOnce;
    }

    public void setMaxLogLinesToReadAtOnce(int maxLogLinesToReadAtOnce) {
        this.maxLogLinesToReadAtOnce = maxLogLinesToReadAtOnce;
    }

    public long getLogFileReadIntervalMillis() {
        return logFileReadIntervalMillis;
    }

    public void setLogFileReadIntervalMillis(long logFileReadIntervalMillis) {
        this.logFileReadIntervalMillis = logFileReadIntervalMillis;
    }

    @Override
    public String toString() {
        return "LogFileReaderConfig{" +
                "maxLogLinesToReadAtOnce=" + maxLogLinesToReadAtOnce +
                ", logFileReadIntervalMillis=" + logFileReadIntervalMillis +
                '}';
    }
}

