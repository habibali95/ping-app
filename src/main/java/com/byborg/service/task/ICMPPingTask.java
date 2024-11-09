package com.byborg.service.task;

import static com.byborg.store.PingResultStore.ICMP_RESULTS;
import static com.byborg.store.PingResultStore.TCP_RESULTS;
import static com.byborg.store.PingResultStore.TRACE_RESULTS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

public class ICMPPingTask implements MonitorTask {
    private static final Logger logger = LogManager.getLogger(ICMPPingTask.class);
    private final TaskHelper taskHelper;
    private final ReportService reportService;
    private final ProcessExecutor processExecutor;
    private String host;
    private final PingResultStore pingResultStore;
    private final String reportUrl;

    public ICMPPingTask(String host, TaskHelper taskHelper, ReportService reportService, ProcessExecutor processExecutor,
                        PingResultStore pingResultStore, String reportUrl) {
        this.host = host;
        this.taskHelper = taskHelper;
        this.reportService = reportService;
        this.processExecutor = processExecutor;
        this.pingResultStore = pingResultStore;
        this.reportUrl = reportUrl;
    }

    @Override
    public void run() {
        Process process = null;
        try {
            String command = System.getProperty("os.name").toLowerCase().contains("win") ? "ping -n 1 " + host : "ping -c 5 " + host;
            process = processExecutor.startProcess(command.split(" "));
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                List<String> outputLines = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                }
                process.waitFor();
                if (process.exitValue() == 0) {
                    pingResultStore.addIcmpResult(host, new MonitoringResult(host, outputLines));
                    boolean pingSuccessful = taskHelper.isPingSuccessful(outputLines);
                    logger.warn("ICMP Ping {} executed", host);
                    logger.warn("ICMP Ping executed successfully= {}", pingSuccessful);
                    logger.warn("ICMP Ping {} outputLines", outputLines);
                    if (!pingSuccessful) {
                        sendReport();
                    }
                } else {
                    logger.warn("ICMP Ping {} failed", host);
                    sendReport();
                }
            }
        } catch (InterruptedException e) {
            throw new TechnicalException("Unable to execute ICMP Ping command, process interrupted", e);

        } catch (Exception e) {
            throw new TechnicalException("Unable to execute ICMP Ping command", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
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
