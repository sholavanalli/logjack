package com.labs.srh.logjack.sender.http;

import com.labs.srh.logjack.LogMessage;
import com.labs.srh.logjack.config.HttpLogLinesSenderConfig;
import com.labs.srh.logjack.sender.LogLinesSender;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class HttpLogLinesSender implements LogLinesSender {

    private static final Logger log = Logger.getLogger(LogLinesSender.class.getName());

    private HttpLogLinesSenderConfig config;
    private CustomHttpClient httpClient;

    public HttpLogLinesSender(HttpLogLinesSenderConfig config, CustomHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    public void send(List<LogMessage> logMessages) {
        while (true) {
            if (!postLogMessagesToURL(logMessages)) {
                log.severe(String.format("Could not send log messages to %s. Retrying in %dms.", config.getHttpPostUrl(),
                        config.getRetryIntervalMillis()));
                try {
                    Thread.sleep(config.getRetryIntervalMillis());
                } catch (InterruptedException e) {
                    log.severe(e.getMessage());
                }
            } else {
                return;
            }
        }
    }

    private boolean postLogMessagesToURL(List<LogMessage> logMessages) {
        HttpPost httpPost = new HttpPost(config.getHttpPostUrl());
        httpPost.setEntity(new StringEntity(toJson(logMessages), ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (IOException e) {
            log.severe(String.format("Error posting log lines to URL %s. Reason: %s", config.getHttpPostUrl(), e.getMessage()));
            return false;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.severe(String.format("Error closing HTTP response. Reason: %s", e.getMessage()));
                }
            }
        }
    }

    private String toJson(List<LogMessage> logMessages) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        logMessages.forEach(msg -> jsonArrayBuilder.add(msg.toJsonObjectBuilder()));
        return jsonArrayBuilder.build().toString();
    }
}
