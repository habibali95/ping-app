package com.byborg.store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.byborg.model.MonitoringResult;

public class PingResultStore {
    public static final String ICMP_RESULTS = "icmpResults";
    public static final String TCP_RESULTS = "tcpResults";
    public static final String TRACE_RESULTS = "traceResults";
    private static PingResultStore pingResultStore;
    private ConcurrentHashMap<String, MonitoringResult> icmpResults;
    private ConcurrentHashMap<String, MonitoringResult> tcpResults;
    private ConcurrentHashMap<String, MonitoringResult> traceResults;
    private static final int MAX_RESULTS_SIZE = 1000;

    private PingResultStore() {
        this.tcpResults = new ConcurrentHashMap<>();
        this.traceResults = new ConcurrentHashMap<>();
        this.icmpResults = new ConcurrentHashMap<>();
    }

    public static PingResultStore getPingResultStore() {
        if (pingResultStore == null) {
            pingResultStore = new PingResultStore();
        }
        return pingResultStore;
    }

    public void addIcmpResult(String host, MonitoringResult icmpResult) {
        if (icmpResults.size() > MAX_RESULTS_SIZE) {
            icmpResults.remove(icmpResults.keySet().iterator().next());
        }
        icmpResults.put(host, icmpResult);
    }

    public void addTcpResult(String host, MonitoringResult icmpResult) {
        if (tcpResults.size() > MAX_RESULTS_SIZE) {
            tcpResults.remove(tcpResults.keySet().iterator().next());
        }
        tcpResults.put(host, icmpResult);
    }

    public void addTraceResult(String host, MonitoringResult icmpResult) {
        if (traceResults.size() > MAX_RESULTS_SIZE) {
            traceResults.remove(traceResults.keySet().iterator().next());
        }
        traceResults.put(host, icmpResult);
    }

    /**
     *  Returns an Unmodifiable map for the latest TCP/IP, ICMP and trace route results per host.
     * @param host
     * @return
     */
    public Map<String, List<String>> getLatestPingResults(String host) {
        return Map.of(ICMP_RESULTS, icmpResults.containsKey(host) ? icmpResults.get(host).getResults() : List.of(),
                TCP_RESULTS, tcpResults.containsKey(host) ? tcpResults.get(host).getResults() : List.of(),
                TRACE_RESULTS, traceResults.containsKey(host) ? traceResults.get(host).getResults() : List.of()
        );
    }
}
