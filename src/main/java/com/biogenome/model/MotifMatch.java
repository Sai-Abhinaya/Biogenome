package com.biogenome.model;

import java.util.Objects;

/**
 * Encapsulates the location, pattern identifier, and biological attributes
 * of a detected sequence motif occurrence within a DNA sequence.
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class MotifMatch implements Comparable<MotifMatch> {

    private final String motifName;
    private final String motifPattern;
    private final int startIndex;
    private final int endIndex;
    private final double matchScore;
    private final String contextSnippet;

    /**
     * Constructs a MotifMatch record.
     *
     * @param motifName      Identifier of the motif (e.g., "EcoRI Restriction Site", "TATA Box")
     * @param motifPattern   Nucleotide string of the motif
     * @param startIndex     0-based start index in target DNA sequence
     * @param endIndex       0-based exclusive end index in target DNA sequence
     * @param matchScore     Confidence/similarity score (1.0 for exact matches)
     * @param contextSnippet Surrounding genomic context
     */
    public MotifMatch(String motifName, String motifPattern, int startIndex, int endIndex, double matchScore, String contextSnippet) {
        this.motifName = Objects.requireNonNull(motifName, "Motif name cannot be null");
        this.motifPattern = Objects.requireNonNull(motifPattern, "Motif pattern cannot be null");
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.matchScore = matchScore;
        this.contextSnippet = contextSnippet == null ? "" : contextSnippet;
    }

    public MotifMatch(String motifName, String motifPattern, int startIndex) {
        this(motifName, motifPattern, startIndex, startIndex + motifPattern.length(), 1.0, "");
    }

    public String getMotifName() {
        return motifName;
    }

    public String getMotifPattern() {
        return motifPattern;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getOneBasedIndex() {
        return startIndex + 1;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getMatchLength() {
        return endIndex - startIndex;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public String getContextSnippet() {
        return contextSnippet;
    }

    @Override
    public int compareTo(MotifMatch other) {
        int cmp = Integer.compare(this.startIndex, other.startIndex);
        if (cmp != 0) return cmp;
        return this.motifName.compareTo(other.motifName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MotifMatch that = (MotifMatch) o;
        return startIndex == that.startIndex &&
               endIndex == that.endIndex &&
               Double.compare(that.matchScore, matchScore) == 0 &&
               Objects.equals(motifName, that.motifName) &&
               Objects.equals(motifPattern, that.motifPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(motifName, motifPattern, startIndex, endIndex, matchScore);
    }

    @Override
    public String toString() {
        return String.format("[%s] at pos %d..%d (1-based: %d) | Pattern: %s | Score: %.2f",
                motifName, startIndex, endIndex, getOneBasedIndex(), motifPattern, matchScore);
    }
}
