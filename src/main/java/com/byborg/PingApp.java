package com.byborg;

import java.util.List;

import com.byborg.configuration.PingConfiguration;
import com.byborg.service.executor.NetworkMonitorExecutorService;
import com.byborg.service.report.ReportService;
import com.byborg.service.task.HttpExecutor;
import com.byborg.service.task.ICMPPingTask;
import com.byborg.service.task.ProcessExecutor;
import com.byborg.service.task.TCPPingTask;
import com.byborg.service.task.TaskHelper;
import com.byborg.service.task.TraceRouteTask;
import com.byborg.store.PingResultStore;

public class PingApp {
    private final static String DEFAULT_CONFIGURATION_FILE = "pingApp.properties";

    public static void main(String[] args) {

        String configFile = args.length > 0 ? args[0] : DEFAULT_CONFIGURATION_FILE;
        PingConfiguration pingConfiguration = PingConfiguration.loadConfiguration(configFile);
        PingResultStore pingResultStore = PingResultStore.getPingResultStore();
        List<String> hosts = pingConfiguration.getHosts();
        TaskHelper taskHelper = new TaskHelper();
        ReportService reportService = new ReportService();
        ProcessExecutor processExecutor = new ProcessExecutor();
        HttpExecutor httpExecutor = new HttpExecutor();
        hosts.parallelStream().forEach(host -> {
                    new NetworkMonitorExecutorService().execute(new ICMPPingTask(host, taskHelper, reportService, processExecutor,
                                    pingResultStore, pingConfiguration.getReportUrl()),
                            pingConfiguration.getIcmpPingDelay());
                    new NetworkMonitorExecutorService().execute(new TraceRouteTask(processExecutor, pingResultStore, host),
                            pingConfiguration.getTraceRouteDelay());
                    new NetworkMonitorExecutorService().execute(new TCPPingTask( httpExecutor, reportService, pingResultStore, host,
                            pingConfiguration.geTcpPingTimeout(), pingConfiguration.getReportUrl()), pingConfiguration.getTcpPingDelay());
                }
        );
    }

}
