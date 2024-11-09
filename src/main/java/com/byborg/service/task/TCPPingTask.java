package com.byborg.service.task;

import static com.byborg.store.PingResultStore.ICMP_RESULTS;
import static com.byborg.store.PingResultStore.TCP_RESULTS;
import static com.byborg.store.PingResultStore.TRACE_RESULTS;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.byborg.exception.TechnicalException;
import com.byborg.model.MonitoringResult;
import com.byborg.model.Report;
import com.byborg.service.report.ReportService;
import com.byborg.store.PingResultStore;

public class TCPPingTask implements MonitorTask {
    private final HttpExecutor httpExecutor;
    private final ReportService reportService;
    private String host;
    private int timeout;
    private final String reportUrl;
    private final PingResultStore pingResultStore;
    private static final Logger logger = LogManager.getLogger(TCPPingTask.class);

    public TCPPingTask(HttpExecutor httpExecutor, ReportService reportService, PingResultStore pingResultStore, String host, int timeout, String reportUrl) {
        this.httpExecutor = httpExecutor;
        this.reportService = reportService;
        this.host = host;
        this.timeout = timeout;
        this.reportUrl = reportUrl;
        this.pingResultStore = pingResultStore;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            int responseCode = httpExecutor.execute(host, timeout);
            long responseTime = System.currentTimeMillis() - startTime;
            List<String> result = new ArrayList<>();
            result.add("Response Code: " + responseCode);
            result.add("Response Time: " + responseTime + " ms");

            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.warn("TCP/IP Ping {} succeeded, response time {}", host, responseTime);
                pingResultStore.addTcpResult(host, new MonitoringResult(host, result));
                if (responseTime > timeout) {
                    sendReport();
                }
            } else {
                logger.warn("TCP/IP Ping {} failed", host);
                sendReport();
            }
        } catch (IOException e) {
            sendReport();
            throw new TechnicalException("TCP/IP Ping failed", e);
        }
    }

    private void sendReport() {
        Map<String, List<String>> latestPingResults = pingResultStore.getLatestPingResults(host);
        String icmpResults = String.join("\n", latestPingResults.get(ICMP_RESULTS));
        String tcpResults = String.join("\n", latestPingResults.get(TCP_RESULTS));
        String traceResults = String.join("\n", latestPingResults.get(TRACE_RESULTS));
        reportService.reportError(new Report(host, icmpResults, tcpResults, traceResults), reportUrl);
    }

}
