package com.labs.srh.logjack;

import com.labs.srh.logjack.config.HttpLogLinesSenderConfig;
import com.labs.srh.logjack.sender.LogLinesSender;
import com.labs.srh.logjack.sender.http.CustomHttpClient;
import com.labs.srh.logjack.sender.http.HttpLogLinesSender;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestHttpLogLinesSender {

    @Test
    public void testHttpSend() throws Exception {
        List<LogMessage> logMessages = Arrays.asList(new LogMessage("line1"), new LogMessage("line2"));
        HttpLogLinesSenderConfig httpLogLinesSenderConfigMock = new HttpLogLinesSenderConfig("http://localhost/rest/log", "", "", 100);
        CustomHttpClient closeableHttpClientMock = mock(CustomHttpClient.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);

        class HttpPostMatcher extends ArgumentMatcher<HttpPost> {

            @Override
            public boolean matches(Object argument) {
                HttpPost httpPost = (HttpPost) argument;
                byte[] httpPostContentBytes = new byte[(int) httpPost.getEntity().getContentLength()];
                try {
                    httpPost.getEntity().getContent().read(httpPostContentBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new String(httpPostContentBytes).equals(toJson(logMessages));
            }

            public String toString() {
                //printed in verification errors
                return "[list of 2 elements]";
            }
        }

        when(closeableHttpResponseMock.getStatusLine())
                .thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 0), 200, "Success"));
        when(closeableHttpClientMock.execute(argThat(new HttpPostMatcher()))).thenReturn(closeableHttpResponseMock);

        LogLinesSender logLinesSender = new HttpLogLinesSender(httpLogLinesSenderConfigMock, closeableHttpClientMock);
        logLinesSender.send(logMessages);

        verify(closeableHttpClientMock).execute(argThat(new HttpPostMatcher()));
    }

    private String toJson(List<LogMessage> logMessages) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        logMessages.forEach(msg -> {
            jsonArrayBuilder.add(msg.toJsonObjectBuilder());
        });
        return jsonArrayBuilder.build().toString();
    }
}
