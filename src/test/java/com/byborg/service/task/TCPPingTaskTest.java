package com.byborg.service.task;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.byborg.store.PingResultStore.ICMP_RESULTS;
import static com.byborg.store.PingResultStore.TCP_RESULTS;
import static com.byborg.store.PingResultStore.TRACE_RESULTS;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.byborg.exception.TechnicalException;
import com.byborg.service.report.ReportService;
import com.byborg.store.PingResultStore;

public class TCPPingTaskTest {

    private TCPPingTask tcpPingTask;

    @Mock
    private ReportService reportService;
    @Mock
    private PingResultStore pingResultStore;
    @Mock
    private Logger logger;
    @Mock
    private HttpExecutor httpExecutor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        tcpPingTask = new TCPPingTask(httpExecutor, reportService, pingResultStore, "google.com", 1000, "http://localhost:8080");
    }

    @Test
    void testRun_PingSuccess() throws IOException {
        when(httpExecutor.execute("google.com", 1000)).thenReturn(200);

        tcpPingTask.run();

        verify(pingResultStore).addTcpResult(eq("google.com"), any());
        verify(reportService, never()).reportError(any(), eq("http://localhost:8080"));
    }

    @Test
    void testRun_PingFailed() throws Exception {
        when(httpExecutor.execute("google.com", 1000)).thenReturn(500);
        when(pingResultStore.getLatestPingResults(anyString())).thenReturn(Map.of(ICMP_RESULTS, List.of(), TCP_RESULTS, List.of(), TRACE_RESULTS, List.of()));

        tcpPingTask.run();

        verify(pingResultStore, never()).addTcpResult(eq("google.com"), any());
        verify(reportService).reportError(any(), eq("http://localhost:8080"));
    }

    @Test
    void testRun_PingThrows() throws Exception {
        when(httpExecutor.execute("google.com", 1000)).thenThrow(IOException.class);
        when(pingResultStore.getLatestPingResults(anyString())).thenReturn(Map.of(ICMP_RESULTS, List.of(), TCP_RESULTS, List.of(), TRACE_RESULTS, List.of()));

        assertThrows(TechnicalException.class, () -> tcpPingTask.run());

        verify(pingResultStore, never()).addTcpResult(eq("google.com"), any());
        verify(reportService).reportError(any(), eq("http://localhost:8080"));
    }

}
