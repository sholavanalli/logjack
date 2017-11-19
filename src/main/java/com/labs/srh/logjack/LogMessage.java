package com.labs.srh.logjack;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public class LogMessage {

    private static final Logger log = Logger.getLogger(LogMessage.class.getName());

    private static String HOST_IP;

    static {
        try {
            HOST_IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            HOST_IP = "Unknown";
            log.severe(String.format(Locale.US, "Could not get host IP: %s", e.getMessage()));
        }
    }

    private String message;
    private String source;
    private long timeUTC;

    public LogMessage(String message, String source) {
        this.message = message;
        this.source = source;
        this.timeUTC = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogMessage that = (LogMessage) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, source);
    }

    @Override
    public String toString() {
        return "LogMessage{" +
                "message='" + message + '\'' +
                ", source='" + source + '\'' +
                ", timeUTC=" + timeUTC +
                '}';
    }

    public JsonObjectBuilder toJsonObjectBuilder() {
        return Json.createObjectBuilder()
                .add("message", message)
                .add("source", source)
                .add("timeUTC", timeUTC)
                .add("hostIP", HOST_IP);
    }
}
