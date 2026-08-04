package com.manit.erp.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalized configuration properties for ERP API integrations and HTTP WebClient settings.
 */
@Component
@ConfigurationProperties(prefix = "erp")
public class ErpProperties {

    private Api api = new Api();
    private Client client = new Client();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public static class Api {
        private String baseUrl = "https://erpapi.manit.ac.in/api";
        private String resultPath = "/student_result";
        private String registrationPath = "/fetch_register";
        private String feePath = "/student_fees";
        private String authToken = "";
        private Integer defaultStudentUid = 4705;
        private Integer defaultProgramId = 82;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getResultPath() {
            return resultPath;
        }

        public void setResultPath(String resultPath) {
            this.resultPath = resultPath;
        }

        public String getRegistrationPath() {
            return registrationPath;
        }

        public void setRegistrationPath(String registrationPath) {
            this.registrationPath = registrationPath;
        }

        public String getFeePath() {
            return feePath;
        }

        public void setFeePath(String feePath) {
            this.feePath = feePath;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        public Integer getDefaultStudentUid() {
            return defaultStudentUid;
        }

        public void setDefaultStudentUid(Integer defaultStudentUid) {
            this.defaultStudentUid = defaultStudentUid;
        }

        public Integer getDefaultProgramId() {
            return defaultProgramId;
        }

        public void setDefaultProgramId(Integer defaultProgramId) {
            this.defaultProgramId = defaultProgramId;
        }

        public String getResultUrl() {
            return baseUrl + resultPath;
        }

        public String getRegistrationUrl() {
            return baseUrl + registrationPath;
        }

        public String getFeeUrl() {
            return baseUrl + feePath;
        }
    }

    public static class Client {
        private int connectTimeoutMs = 5000;
        private int readTimeoutMs = 10000;
        private int maxRetryAttempts = 3;
        private long backoffPeriodMs = 1000;

        public int getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
        }

        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public void setReadTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }

        public int getMaxRetryAttempts() {
            return maxRetryAttempts;
        }

        public void setMaxRetryAttempts(int maxRetryAttempts) {
            this.maxRetryAttempts = maxRetryAttempts;
        }

        public long getBackoffPeriodMs() {
            return backoffPeriodMs;
        }

        public void setBackoffPeriodMs(long backoffPeriodMs) {
            this.backoffPeriodMs = backoffPeriodMs;
        }
    }
}
