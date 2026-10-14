package com.biogenome.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Audit and session history record for any executed DSA algorithm in BioGenome.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class AnalysisRecord {

    private final String id;
    private final String moduleName;
    private final String algorithmName;
    private final String inputType;
    private final String inputSummary;
    private final String resultSummary;
    private final long executionTimeMs;
    private final String timeComplexity;
    private final String spaceComplexity;
    private final String timestamp;

    public AnalysisRecord(String moduleName, String algorithmName, String inputType,
                          String inputSummary, String resultSummary, long executionTimeMs,
                          String timeComplexity, String spaceComplexity) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.moduleName = moduleName;
        this.algorithmName = algorithmName;
        this.inputType = inputType;
        this.inputSummary = inputSummary;
        this.resultSummary = resultSummary;
        this.executionTimeMs = executionTimeMs;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getId() {
        return id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getInputType() {
        return inputType;
    }

    public String getInputSummary() {
        return inputSummary;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s in %d ms", timestamp, algorithmName, moduleName, resultSummary, executionTimeMs);
    }
}
