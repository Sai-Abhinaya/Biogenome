package com.biogenome.model;

import java.util.Objects;

/**
 * Domain model representing a continuous real-time observation from DNA sequencing instruments.
 * Used for streaming algorithms like Reservoir Sampling (Module 6).
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class ContinuousObservation {

    private final String id;
    private final String type; // Error Rate, Quality Score (Q), Flow Rate, GC Deviation, Base Call Confidence
    private final double value;
    private final String unit;
    private final String timestamp;
    private final String sourceInstrument;

    public ContinuousObservation(String id, String type, double value, String unit, String timestamp, String sourceInstrument) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        this.type = Objects.requireNonNull(type, "Type must not be null");
        this.value = value;
        this.unit = unit != null ? unit : "";
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp must not be null");
        this.sourceInstrument = sourceInstrument != null ? sourceInstrument : "Illumina NovaSeq X";
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSourceInstrument() {
        return sourceInstrument;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s = %.2f %s (%s)", id, type, value, unit, timestamp);
    }
}
