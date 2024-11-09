package com.habib.model;

import java.util.List;

public class MonitoringResult {

    private String host;
    private List<String> results;
    private long timestamp;

    public MonitoringResult(String host, List<String> outputLines) {
        this.host = host;
        this.results = outputLines;
        this.timestamp = System.currentTimeMillis();
    }

    public String getHost() {
        return host;
    }

    public List<String> getResults() {
        return results;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
