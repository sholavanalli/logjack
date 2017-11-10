package com.labs.srh.logjack.config;

public class HttpLogLinesSenderConfig {

    private String httpPostUrl;
    private String userName;
    private String password;
    private long retryIntervalMillis;

    public HttpLogLinesSenderConfig() {
    }

    public HttpLogLinesSenderConfig(String httpPostUrl, String userName, String password, long retryIntervalMillis) {
        this.httpPostUrl = httpPostUrl;
        this.userName = userName;
        this.password = password;
        this.retryIntervalMillis = retryIntervalMillis;
    }

    public String getHttpPostUrl() {
        return httpPostUrl;
    }

    public void setHttpPostUrl(String httpPostUrl) {
        this.httpPostUrl = httpPostUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRetryIntervalMillis() {
        return retryIntervalMillis;
    }

    public void setRetryIntervalMillis(long retryIntervalMillis) {
        this.retryIntervalMillis = retryIntervalMillis;
    }

    @Override
    public String toString() {
        return "LogLinesSenderConfig{" +
                "httpPostUrl='" + httpPostUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", retryIntervalMillis=" + retryIntervalMillis +
                '}';
    }
}
