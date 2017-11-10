package com.labs.srh.logjack.sender.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;

/**
 * Wrapper for the http client. This wrapper is spring friendly because it implements the
 * {@link org.springframework.beans.factory.DisposableBean} interface.
 */
public class CustomHttpClient implements DisposableBean {

    private CloseableHttpClient closeableHttpClient;

    public CustomHttpClient() {
        closeableHttpClient = HttpClients.createDefault();
    }

    public CloseableHttpResponse execute(final HttpUriRequest request) throws IOException {
        return closeableHttpClient.execute(request);
    }

    @Override
    public void destroy() throws Exception {
        if (closeableHttpClient != null) {
            closeableHttpClient.close();
        }
    }
}
