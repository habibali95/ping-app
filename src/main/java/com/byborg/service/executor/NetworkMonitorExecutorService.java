package com.byborg.service.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.byborg.service.task.MonitorTask;

public class NetworkMonitorExecutorService {
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public void execute(MonitorTask pingTask, long delay) {
        executorService.scheduleWithFixedDelay(pingTask, 0, delay, TimeUnit.MILLISECONDS);
    }
}
