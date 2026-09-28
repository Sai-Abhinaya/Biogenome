package com.biogenome.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable domain model representing a validated Genomic DNA Sequence.
 * <p>
 * Provides core bioinformatics transformations including reverse complement,
 * GC-content quantification, RNA transcription, and nucleotide validation.
 * </p>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class DnaSequence {

    private final String id;
    private final String description;
    private final String rawSequence;
    private final int length;

    /**
     * Constructs a validated DnaSequence.
     *
     * @param id          Unique identifier for the sequence (e.g., accession number, gene symbol)
     * @param rawSequence Nucleotide string (A, C, G, T, N)
     * @throws IllegalArgumentException if sequence contains invalid nucleotide characters
     */
    public DnaSequence(String id, String rawSequence) {
        this(id, "Genomic DNA Record", rawSequence);
    }

    /**
     * Constructs a validated DnaSequence with custom metadata.
     *
     * @param id          Unique identifier for the sequence
     * @param description Biological description or annotation
     * @param rawSequence Nucleotide string
     * @throws IllegalArgumentException if sequence is null or contains invalid nucleotide characters
     */
    public DnaSequence(String id, String description, String rawSequence) {
        this.id = Objects.requireNonNull(id, "Sequence ID must not be null").trim();
        this.description = Objects.requireNonNull(description, "Description must not be null").trim();
        Objects.requireNonNull(rawSequence, "Raw sequence must not be null");

        String normalized = rawSequence.toUpperCase().replaceAll("\\s+", "");
        validateNucleotides(normalized);
        this.rawSequence = normalized;
        this.length = normalized.length();
    }

    /**
     * Validates that all characters in the sequence are valid IUPAC DNA symbols.
     *
     * @param sequence String to validate
     */
    private static void validateNucleotides(String sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (c != 'A' && c != 'C' && c != 'G' && c != 'T' && c != 'N' && c != '$') {
                throw new IllegalArgumentException(
                    String.format("Invalid nucleotide character '%c' at position %d. Allowed: A, C, G, T, N.", c, i)
                );
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getSequence() {
        return rawSequence;
    }

    public int length() {
        return length;
    }

    public char charAt(int index) {
        return rawSequence.charAt(index);
    }

    public String substring(int start, int end) {
        return rawSequence.substring(start, end);
    }

    /**
     * Calculates the GC-Content percentage of the DNA sequence.
     * GC-content is directly correlated with DNA thermal stability and melting temperature.
     *
     * @return GC percentage [0.0 - 100.0]
     */
    public double getGcContent() {
        if (length == 0) return 0.0;
        int gcCount = 0;
        for (int i = 0; i < length; i++) {
            char c = rawSequence.charAt(i);
            if (c == 'G' || c == 'C') {
                gcCount++;
            }
        }
        return (gcCount * 100.0) / length;
    }

    /**
     * Generates the Watson-Crick reverse complement strand (5' to 3').
     * Complement mappings: A <-> T, C <-> G, N <-> N.
     *
     * @return DnaSequence object representing the complementary reverse strand
     */
    public DnaSequence getReverseComplement() {
        StringBuilder sb = new StringBuilder(length);
        for (int i = length - 1; i >= 0; i--) {
            char c = rawSequence.charAt(i);
            switch (c) {
                case 'A': sb.append('T'); break;
                case 'T': sb.append('A'); break;
                case 'C': sb.append('G'); break;
                case 'G': sb.append('C'); break;
                default:  sb.append(c);   break;
            }
        }
        return new DnaSequence(id + "_RevComp", "Reverse complement of " + id, sb.toString());
    }

    /**
     * Transcribes the coding DNA strand into Messenger RNA (mRNA) by replacing Thymine (T) with Uracil (U).
     *
     * @return Transcribed mRNA string
     */
    public String transcribeToRna() {
        return rawSequence.replace('T', 'U');
    }

    /**
     * Computes the frequency distribution of individual nucleotides.
     *
     * @return Unmodifiable map of nucleotide character to count
     */
    public Map<Character, Integer> getNucleotideCounts() {
        Map<Character, Integer> counts = new HashMap<>();
        counts.put('A', 0);
        counts.put('C', 0);
        counts.put('G', 0);
        counts.put('T', 0);
        for (int i = 0; i < length; i++) {
            char c = rawSequence.charAt(i);
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        return Collections.unmodifiableMap(counts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DnaSequence that = (DnaSequence) o;
        return Objects.equals(id, that.id) && Objects.equals(rawSequence, that.rawSequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rawSequence);
    }

    @Override
    public String toString() {
        return String.format(">%s | %s (Len: %d bp, GC: %.2f%%)", id, description, length, getGcContent());
    }
}
