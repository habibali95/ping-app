package com.byborg.service.task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpExecutor {

    private static final String GET = "GET";

    /**
     * Execute HTTP call and returns the response code.
     * @param url
     * @param timeout
     * @return
     * @throws IOException
     */
    public int execute(String url, int timeout) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://"+url).openConnection();
        connection.setConnectTimeout(timeout);
        connection.setRequestMethod(GET);
        return connection.getResponseCode();
    }
}
