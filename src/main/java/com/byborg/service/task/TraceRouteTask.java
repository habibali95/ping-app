package com.byborg.service.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.byborg.exception.TechnicalException;
import com.byborg.model.MonitoringResult;
import com.byborg.store.PingResultStore;

public class TraceRouteTask implements MonitorTask {

    private static final Logger logger = LogManager.getLogger(TraceRouteTask.class);
    private final ProcessExecutor processExecutor;
    private final PingResultStore pingResultStore;
    private String host;

    public TraceRouteTask(ProcessExecutor processExecutor, PingResultStore pingResultStore, String host) {
        this.processExecutor = processExecutor;
        this.host = host;
        this.pingResultStore = pingResultStore;
    }

    @Override
    public void run() {
        String command = System.getProperty("os.name").toLowerCase().contains("win") ? "tracert" : "traceroute";
        List<String> outputLines = new ArrayList<>();
        Process process = null;
        try {
            processExecutor.startProcess(new String[]{command, host});
            process = processExecutor.startProcess(new String[]{command, host});
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                }
            }
            process.waitFor();
            if (process.exitValue() == 0) {
                pingResultStore.addTraceResult(host, new MonitoringResult(host, outputLines));
                logger.warn("Trace route {} succeeded", host);
                logger.warn("Trace route output: {}", outputLines);
            } else {
                logger.error("Trace route for {} failed, process exit value {}", host, process.exitValue());
            }
        } catch (Exception e) {
            logger.error("Trace route for {} failed", host);
            throw new TechnicalException(e);
        } finally {
            if (process != null && process.isAlive()) {
                process.destroy();
            }
        }
    }

}
