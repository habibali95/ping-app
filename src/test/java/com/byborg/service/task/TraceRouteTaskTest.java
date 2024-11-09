package com.byborg.service.task;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.byborg.store.PingResultStore.ICMP_RESULTS;
import static com.byborg.store.PingResultStore.TCP_RESULTS;
import static com.byborg.store.PingResultStore.TRACE_RESULTS;

import java.io.BufferedReader;
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

public class TraceRouteTaskTest {

    private TraceRouteTask traceRouteTask;
    @Mock
    private PingResultStore pingResultStore;
    @Mock
    private Logger logger;
    @Mock
    private ProcessExecutor processExecutor;
    @Mock
    private Process process;
    @Mock
    private BufferedReader bufferedReader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traceRouteTask = new TraceRouteTask(processExecutor, pingResultStore, "google.com");
    }

    @Test
    void testRun_TraceSuccess() throws Exception {
        when(processExecutor.startProcess(any())).thenReturn(process);
        when(process.exitValue()).thenReturn(0);
        when(process.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("trace result".getBytes()));

        traceRouteTask.run();

        verify(pingResultStore).addTraceResult(eq("google.com"), any());
     }

    @Test
    void testRun_TraceFailure() throws Exception {
        when(processExecutor.startProcess(any())).thenReturn(process);
        when(process.exitValue()).thenReturn(-1);
        when(process.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("trace result".getBytes()));

        traceRouteTask.run();

        verify(pingResultStore, never()).addTraceResult(eq("google.com"), any());
     }

    @Test
    void testRun_ProcessInterrupted() throws Exception {
        when(processExecutor.startProcess(any())).thenReturn(process);
        when(process.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("trace result".getBytes()));
        when(process.waitFor()).thenThrow(InterruptedException.class);

        assertThrows(TechnicalException.class, () -> traceRouteTask.run());

        verify(pingResultStore, never()).addTraceResult(eq("google.com"), any());
    }


}
