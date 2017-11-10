package com.labs.srh.logjack.config;

import org.springframework.stereotype.Component;

public class DirectoryScannerConfig {

    private int directoryScanIntervalMillis;
    private int numberOfLogFileReaders;
    private String directoryPathToScan;

    public DirectoryScannerConfig() {
    }

    public DirectoryScannerConfig(int directoryScanIntervalMillis, int numberOfLogFileReaders, String directoryPathToScan) {
        this.directoryScanIntervalMillis = directoryScanIntervalMillis;
        this.numberOfLogFileReaders = numberOfLogFileReaders;
        this.directoryPathToScan = directoryPathToScan;
    }

    public int getDirectoryScanIntervalMillis() {
        return directoryScanIntervalMillis;
    }

    public void setDirectoryScanIntervalMillis(int directoryScanIntervalMillis) {
        this.directoryScanIntervalMillis = directoryScanIntervalMillis;
    }

    public int getNumberOfLogFileReaders() {
        return numberOfLogFileReaders;
    }

    public void setNumberOfLogFileReaders(int numberOfLogFileReaders) {
        this.numberOfLogFileReaders = numberOfLogFileReaders;
    }

    public String getDirectoryPathToScan() {
        return directoryPathToScan;
    }

    public void setDirectoryPathToScan(String directoryPathToScan) {
        this.directoryPathToScan = directoryPathToScan;
    }

    @Override
    public String toString() {
        return "DirectoryScannerConfig{" +
                "directoryScanIntervalMillis=" + directoryScanIntervalMillis +
                ", numberOfLogFileReaders=" + numberOfLogFileReaders +
                ", directoryPathToScan='" + directoryPathToScan + '\'' +
                '}';
    }
}

