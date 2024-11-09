package com.byborg.service.task;

import java.util.List;

import com.byborg.exception.TechnicalException;

public class TaskHelper {

    public boolean isPingSuccessful(List<String> outputLines) {
        int packetsTransmitted = -1;
        int packetsReceived = -1;
        int packetLossPercentage = -1;

        for (String line : outputLines) {
            line = line.toLowerCase();

            if (line.contains("packets transmitted")) {
                packetsTransmitted = extractValue(line, "packets transmitted");
                packetsReceived = extractValue(line, "received");
                packetLossPercentage = extractValue(line, "packet loss");
                break;
            }
        }

        return packetsTransmitted > 0 && packetsTransmitted == packetsReceived && packetLossPercentage == 0;
    }

    private int extractValue(String line, String keyword) {
        line = line.replace("%", "");
        try {
            String[] parts = line.split(",");
            for (String part : parts) {
                if (part.contains(keyword)) {
                    String[] words = part.trim().split(" ");
                    for (String word : words) {
                        try {
                            return Integer.parseInt(word);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new TechnicalException("Failed to extract value", e);
        }
        return -1;
    }

}
