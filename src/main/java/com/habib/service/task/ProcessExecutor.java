package com.habib.service.task;

import java.io.IOException;

public class ProcessExecutor {

    public Process startProcess(String[] command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        return processBuilder.start();
    }
}
