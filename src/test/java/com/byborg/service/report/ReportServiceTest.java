package com.byborg.service.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.net.HttpURLConnection;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.byborg.exception.TechnicalException;
import com.byborg.model.Report;

public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private Logger logger;

    @Mock
    private HttpURLConnection httpURLConnection;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReportError_Successful() throws Exception {
        Report report = new Report("google.com", "icmpResult", "tcpResult", "traceResult");
        String expectedJson = "{\"host\":\"google.com\",\"icmp_ping\":\"icmpResult\",\"tcp_ping\":\"tcpResult\",\"trace\":\"traceResult\"}";
        assertEquals(expectedJson, reportService.getReportAsJson(report));
     }

    @Test
    void testReportError_Failure() throws Exception {
        // Arrange
        Report report = new Report("google.com", "icmpResult", "tcpResult", "traceResult");
        String url = "http://localhost:8080";

        when(httpURLConnection.getOutputStream()).thenThrow(new RuntimeException("Connection error"));

        assertThrows(TechnicalException.class, () -> reportService.reportError(report, url));
     }
}
