package com.habib;

import java.util.List;

import com.habib.configuration.PingConfiguration;
import com.habib.service.executor.NetworkMonitorExecutorService;
import com.habib.service.report.ReportService;
import com.habib.service.task.HttpExecutor;
import com.habib.service.task.ICMPPingTask;
import com.habib.service.task.ProcessExecutor;
import com.habib.service.task.TCPPingTask;
import com.habib.service.task.TaskHelper;
import com.habib.service.task.TraceRouteTask;
import com.habib.store.PingResultStore;

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
