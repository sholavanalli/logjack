package com.labs.srh.logjack;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Set of all the active log file paths currently known to this system.
 */
public class LogFilesHolder {

    private static final Logger log = Logger.getLogger(LogFilesHolder.class.getName());

    private Set<LogFile> logFiles;

    public LogFilesHolder() {
        this.logFiles = new HashSet<>();
    }

    public void add(Path logFilePath) {
        LogFile logFile = new LogFile(false, false, logFilePath);
        if (logFiles.contains(logFile)) {
            logFiles.remove(logFile);
            logFile.setActive(true);
            logFiles.add(logFile);
        } else {
            log.info(String.format(Locale.US, "Found new log file: %s", logFile.getPath().getFileName()));
            logFile.setActive(true);
            logFile.setNew(true);
            logFiles.add(logFile);
        }
    }

    /**
     * Remove inactive log files and reset active and new flags.
     */
    public void refresh() {
        Set<LogFile> newLogFiles = new HashSet<>();
        for (LogFile logFile : logFiles) {
            if (logFile.isActive()) {
                logFile.setActive(false);
                logFile.setNew(false);
                newLogFiles.add(logFile);
            } else {
                log.info(String.format(Locale.US, "Removing inactive log file: %s", logFile.getPath().getFileName()));
            }
        }
        logFiles = newLogFiles;
    }

    public Set<Path> getNewLogFilePaths() {
        return this.logFiles.stream()
                .filter(LogFile::isNew)
                .map(LogFile::getPath)
                .collect(Collectors.toSet());
    }

    public Set<Path> getLogFilePaths() {
        return this.logFiles.stream()
                .map(LogFile::getPath)
                .collect(Collectors.toSet());
    }

    private class LogFile {

        private boolean isActive;
        private boolean isNew;
        private Path path;

        public LogFile(boolean isActive, boolean isNew, Path path) {
            this.isActive = isActive;
            this.isNew = isNew;
            this.path = path;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LogFile logFile = (LogFile) o;
            return isActive == logFile.isActive &&
                    isNew == logFile.isNew &&
                    // Only compare path strings here.
                    Objects.equals(path.toString(), logFile.path.toString());
        }

        @Override
        public int hashCode() {
            // Only use path string to generate hashcode.
            return Objects.hash(isActive, isNew, path.toString());
        }
    }
}
