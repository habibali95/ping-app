package com.byborg.service.report;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.byborg.exception.TechnicalException;
import com.byborg.model.Report;
import com.google.gson.Gson;

public class ReportService {
    private static final Logger logger = LogManager.getLogger(ReportService.class);

    public void reportError(Report report, String url) {

        String reportAsJson = getReportAsJson(report);
        sendReport(reportAsJson, url);
    }

    public String getReportAsJson(Report report) {
        String reportAsJson = new Gson().toJson(report);
        return reportAsJson;
    }

    private void sendReport(String jsonData, String url) {
        logger.warn("Sending report to {} with payload {}", url, jsonData);

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes());
                os.flush();
            }

            conn.getResponseCode();
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
    }

}
