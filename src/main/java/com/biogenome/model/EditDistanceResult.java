package com.biogenome.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the output of Damerau-Levenshtein / Levenshtein Sequence Error Correction,
 * including edit distance, list of atomic mutational operations, and aligned representation.
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class EditDistanceResult {

    public enum EditOperation {
        MATCH("M"),
        SUBSTITUTION("S"),
        INSERTION("I"),
        DELETION("D"),
        TRANSPOSITION("T");

        private final String code;

        EditOperation(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    private final String querySequence;
    private final String targetSequence;
    private final int distance;
    private final List<EditOperation> operations;
    private final String alignedQuery;
    private final String alignedTarget;
    private final String cigarString;

    public EditDistanceResult(String querySequence, String targetSequence, int distance,
                              List<EditOperation> operations, String alignedQuery,
                              String alignedTarget, String cigarString) {
        this.querySequence = Objects.requireNonNull(querySequence);
        this.targetSequence = Objects.requireNonNull(targetSequence);
        this.distance = distance;
        this.operations = operations != null ? List.copyOf(operations) : List.of();
        this.alignedQuery = Objects.requireNonNull(alignedQuery);
        this.alignedTarget = Objects.requireNonNull(alignedTarget);
        this.cigarString = Objects.requireNonNull(cigarString);
    }

    public String getQuerySequence() {
        return querySequence;
    }

    public String getTargetSequence() {
        return targetSequence;
    }

    public int getDistance() {
        return distance;
    }

    public List<EditOperation> getOperations() {
        return operations;
    }

    public String getAlignedQuery() {
        return alignedQuery;
    }

    public String getAlignedTarget() {
        return alignedTarget;
    }

    public String getCigarString() {
        return cigarString;
    }

    public double getSimilarity() {
        int maxLen = Math.max(querySequence.length(), targetSequence.length());
        if (maxLen == 0) return 100.0;
        return ((maxLen - distance) * 100.0) / maxLen;
    }

    @Override
    public String toString() {
        return String.format("EditDistanceResult [Distance=%d, Similarity=%.2f%%, CIGAR=%s]",
                distance, getSimilarity(), cigarString);
    }
}
