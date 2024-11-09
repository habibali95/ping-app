package com.habib.service.task;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.habib.store.PingResultStore.ICMP_RESULTS;
import static com.habib.store.PingResultStore.TCP_RESULTS;
import static com.habib.store.PingResultStore.TRACE_RESULTS;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.habib.exception.TechnicalException;
import com.habib.service.report.ReportService;
import com.habib.store.PingResultStore;

public class ICMPPingTaskTest {

    private ICMPPingTask icmpPingTask;

    @Mock
    private TaskHelper taskHelper;
    @Mock
    private ReportService reportService;
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
        icmpPingTask = new ICMPPingTask("google.com", taskHelper, reportService, processExecutor, pingResultStore,"http://localhost:8080");
    }

    @Test
    void testRun_PingSuccess() throws Exception {
        when(processExecutor.startProcess(any())).thenReturn(process);
        when(process.exitValue()).thenReturn(0);
        when(process.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("ping result".getBytes()));
        when(taskHelper.isPingSuccessful(anyList())).thenReturn(true);

        icmpPingTask.run();

        verify(pingResultStore).addIcmpResult(eq("google.com"), any());
        verify(reportService, never()).reportError(any(), eq("http://localhost:8080"));
    }

    @Test
    void testRun_PingFailure() throws Exception {
        when(processExecutor.startProcess(any())).thenReturn(process);
        when(process.exitValue()).thenReturn(0);
        when(process.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("ping result".getBytes()));
        when(taskHelper.isPingSuccessful(anyList())).thenReturn(false);
        when(pingResultStore.getLatestPingResults(anyString())).thenReturn(Map.of(ICMP_RESULTS, List.of(), TCP_RESULTS, List.of(), TRACE_RESULTS, List.of()));

        icmpPingTask.run();

        verify(pingResultStore).addIcmpResult(eq("google.com"), any());
        verify(reportService).reportError(any(), eq("http://localhost:8080"));
    }

    @Test
    void testRun_ProcessInterrupted() throws Exception {
        when(processExecutor.startProcess(any())).thenReturn(process);
        when(process.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("ping result".getBytes()));
        when(process.waitFor()).thenThrow(InterruptedException.class);

        assertThrows(TechnicalException.class, () -> icmpPingTask.run());

        verify(pingResultStore, never()).addIcmpResult(eq("google.com"), any());
    }


}
