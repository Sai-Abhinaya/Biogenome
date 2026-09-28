package com.biogenome.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the complete suffix indexing results including Suffix Array (SA),
 * Longest Common Prefix (LCP) Array, Rank Array, and detected repeated motifs.
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class SuffixArrayResult {

    private final String sequence;
    private final int[] suffixArray;
    private final int[] lcpArray;
    private final int[] rankArray;
    private final List<String> longestRepeatedSubstrings;

    public SuffixArrayResult(String sequence, int[] suffixArray, int[] lcpArray, int[] rankArray, List<String> longestRepeatedSubstrings) {
        this.sequence = Objects.requireNonNull(sequence, "Sequence cannot be null");
        this.suffixArray = suffixArray.clone();
        this.lcpArray = lcpArray != null ? lcpArray.clone() : new int[0];
        this.rankArray = rankArray != null ? rankArray.clone() : new int[0];
        this.longestRepeatedSubstrings = longestRepeatedSubstrings != null ? List.copyOf(longestRepeatedSubstrings) : List.of();
    }

    public String getSequence() {
        return sequence;
    }

    public int[] getSuffixArray() {
        return suffixArray.clone();
    }

    public int[] getLcpArray() {
        return lcpArray.clone();
    }

    public int[] getRankArray() {
        return rankArray.clone();
    }

    public List<String> getLongestRepeatedSubstrings() {
        return longestRepeatedSubstrings;
    }

    public int length() {
        return suffixArray.length;
    }

    /**
     * Retrieves the suffix string corresponding to the i-th entry in the Suffix Array.
     *
     * @param index Index in Suffix Array [0 .. N-1]
     * @return Suffix string
     */
    public String getSuffix(int index) {
        if (index < 0 || index >= suffixArray.length) {
            throw new IndexOutOfBoundsException("Suffix index out of range: " + index);
        }
        return sequence.substring(suffixArray[index]);
    }

    @Override
    public String toString() {
        return String.format("SuffixArrayResult [Length=%d, SA=%s, LCP=%s, LongestRepeats=%s]",
                sequence.length(), Arrays.toString(suffixArray), Arrays.toString(lcpArray), longestRepeatedSubstrings);
    }
}
