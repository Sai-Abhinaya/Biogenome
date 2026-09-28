package com.biogenome.model;

import java.util.List;
import java.util.Map;

/**
 * Unified REST API Response wrapper for all BioGenome algorithm endpoints.
 * Carries result data, algorithm metadata, and execution timing.
 */
public class ApiResponse {
    private boolean success;
    private String algorithm;
    private String message;
    private long executionTimeMs;
    private Object data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String algorithm, long executionTimeMs, Object data) {
        this.success = success;
        this.algorithm = algorithm;
        this.executionTimeMs = executionTimeMs;
        this.data = data;
        this.message = success ? "Computation completed successfully." : "An error occurred.";
    }

    public static ApiResponse ok(String algorithm, long execMs, Object data) {
        return new ApiResponse(true, algorithm, execMs, data);
    }

    public static ApiResponse error(String algorithm, String message) {
        ApiResponse r = new ApiResponse(false, algorithm, 0, null);
        r.message = message;
        return r;
    }

    // ---- Getters & Setters ----
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
