package com.byborg.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.byborg.exception.TechnicalException;

public class PingConfiguration {

    private static final String ICMP_PING_DELAY = "icmp_ping_delay";
    private static final String HOSTS = "hosts";
    private static final String REPORT_URL = "report_url";
    private static final String TCP_PING_DELAY = "tcp_ping_delay";
    private static final String TRACE_ROUTE_DELAY = "trace_route_delay";
    public static final String DELAY_DEFAULT_VALUE = "5";
    public static final String REPORT_URL_DEFAULT_VALUE = "report_url_default";
    public static final String TCP_PING_TIMEOUT = "tcp_ping_timeout";
    private static PingConfiguration pingConfiguration;

    private long icmpPingDelay;
    private long tcpPingDelay;
    private int tcpPingTimeout;
    private long traceRouteDelay;
    private String reportUrl;
    private List<String> hosts;
    private Properties properties;

    private PingConfiguration(String filePath) {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filePath) != null ?
                getClass().getClassLoader().getResourceAsStream(filePath) :
                new FileInputStream(filePath)) {
            properties.load(input);
            setHosts(List.of(properties.getProperty(HOSTS).split(",")));
            setIcmpPingDelay(Long.parseLong(properties.getProperty(ICMP_PING_DELAY, DELAY_DEFAULT_VALUE)));
            setReportUrl(properties.getProperty(REPORT_URL, REPORT_URL_DEFAULT_VALUE));
            setTcpPingDelay(Long.parseLong(properties.getProperty(TCP_PING_DELAY, DELAY_DEFAULT_VALUE)));
            setTcpPingTimeout(Integer.parseInt(properties.getProperty(TCP_PING_TIMEOUT, DELAY_DEFAULT_VALUE)));
            setTraceRouteDelay(Long.parseLong(properties.getProperty(TRACE_ROUTE_DELAY, DELAY_DEFAULT_VALUE)));
        } catch (IOException e) {
            throw new TechnicalException("Unable to read " + filePath, e);
        }
    }

    /**
     * Create and load the main configuration, the source should be either
     * the main pingApp.properties under resources (this could change) or the provided file path
     * in the java -jar command args.
     * The system properties will take precedence over the configuration properties.
     *
     * @param configFilePath The path for ping-app main configuration.
     * @return the PingConfiguration singleton instance
     */
    public static PingConfiguration loadConfiguration(String configFilePath) {
        if (pingConfiguration == null) {
            pingConfiguration = new PingConfiguration(configFilePath);
        }

        return pingConfiguration;
    }

    public List<String> getHosts() {
        String hosts = System.getenv(HOSTS);
        return (hosts != null) ? List.of(hosts.split(",")) : this.hosts;
    }

    private void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public long getTraceRouteDelay() {
        String envValue = System.getenv(TRACE_ROUTE_DELAY);
        return envValue != null ? Long.valueOf(envValue) : traceRouteDelay;
    }

    private void setTraceRouteDelay(long traceRouteDelay) {
        this.traceRouteDelay = traceRouteDelay;
    }

    public String getReportUrl() {
        String envValue = System.getenv(REPORT_URL);
        return (envValue != null) ? envValue : reportUrl;
    }

    private void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public long getTcpPingDelay() {
        String envValue = System.getenv(TCP_PING_DELAY);
        return envValue != null ? Long.valueOf(envValue) : tcpPingDelay;
    }

    private void setTcpPingDelay(long tcpPingDelay) {
        this.tcpPingDelay = tcpPingDelay;
    }

    public int geTcpPingTimeout() {
        String envValue = System.getenv(TCP_PING_TIMEOUT);
        return envValue != null ? Integer.parseInt(envValue) : tcpPingTimeout;
    }

    private void setTcpPingTimeout(int tcpPingTimeout) {
        this.tcpPingTimeout = tcpPingTimeout;
    }

    public long getIcmpPingDelay() {
        String envValue = System.getenv(ICMP_PING_DELAY);
        return envValue != null ? Long.parseLong(envValue) : icmpPingDelay;
    }

    private void setIcmpPingDelay(long icmpPingDelay) {
        this.icmpPingDelay = icmpPingDelay;
    }
}
