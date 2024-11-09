package com.habib.service.task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskHelperTest {

    @InjectMocks
    private TaskHelper taskHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsPingSuccessful_whenPingIsSuccessful() {
        List<String> successfulPingOutput = Arrays.asList(
                "PING google.com(fra15s10-in-x0e.1e100.net (2a00:1450:4001:811::200e)) 56 data bytes",
                "64 bytes from fra16s51-in-x0e.1e100.net (2a00:1450:4001:811::200e): icmp_seq=1 ttl=116 time=17.2 ms",
                "--- google.com ping statistics ---",
                "5 packets transmitted, 5 received, 0% packet loss, time 20087ms",
                "rtt min/avg/max/mdev = 14.947/15.887/17.214/0.976 ms"
        );

        assertTrue(taskHelper.isPingSuccessful(successfulPingOutput), "Ping should be successful");
    }

    @Test
    public void testIsPingSuccessful_whenPingIsUnsuccessfulDueToPacketLoss() {
        List<String> failedPingOutput = Arrays.asList(
                "PING google.com(fra15s10-in-x0e.1e100.net (2a00:1450:4001:811::200e)) 56 data bytes",
                "64 bytes from fra16s51-in-x0e.1e100.net (2a00:1450:4001:811::200e): icmp_seq=1 ttl=116 time=17.2 ms",
                "--- google.com ping statistics ---",
                "5 packets transmitted, 4 received, 20% packet loss, time 20087ms",
                "rtt min/avg/max/mdev = 14.947/15.887/17.214/0.976 ms"
        );

        assertFalse(taskHelper.isPingSuccessful(failedPingOutput), "Ping should be unsuccessful due to packet loss");
    }

    @Test
    public void testIsPingSuccessful_whenPingIsUnsuccessfulDueToMismatchInSentAndReceivedPackets() {
        List<String> failedPingOutput = Arrays.asList(
                "PING google.com(fra15s10-in-x0e.1e100.net (2a00:1450:4001:811::200e)) 56 data bytes",
                "64 bytes from fra16s51-in-x0e.1e100.net (2a00:1450:4001:811::200e): icmp_seq=1 ttl=116 time=17.2 ms",
                "--- google.com ping statistics ---",
                "5 packets transmitted, 3 received, 40% packet loss, time 20087ms",
                "rtt min/avg/max/mdev = 14.947/15.887/17.214/0.976 ms"
        );

        assertFalse(taskHelper.isPingSuccessful(failedPingOutput), "Ping should be unsuccessful due to packet mismatch");
    }
}


