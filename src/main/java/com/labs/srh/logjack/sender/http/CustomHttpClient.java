package com.labs.srh.logjack.sender.http;

import com.labs.srh.logjack.config.AppConfig;
import com.labs.srh.logjack.config.HttpLogLinesSenderConfig;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Wrapper for the http client. This wrapper is spring friendly because it implements the
 * {@link org.springframework.beans.factory.DisposableBean} interface.
 */
public class CustomHttpClient implements DisposableBean {

    private static final Logger log = Logger.getLogger(CustomHttpClient.class.getName());

    private CloseableHttpClient closeableHttpClient;

    public CustomHttpClient(AppConfig appConfig) {
        HttpLogLinesSenderConfig config = appConfig.getHttpLogLinesSenderConfig();
        if (config.getUserName() != null && !config.getUserName().isEmpty()) {
            log.info("Using basic auth.");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            UsernamePasswordCredentials unPwCreds = new UsernamePasswordCredentials(config.getUserName(), config.getPassword());
            credentialsProvider.setCredentials(AuthScope.ANY, unPwCreds);
            closeableHttpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        } else {
            closeableHttpClient = HttpClients.createDefault();
        }
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
