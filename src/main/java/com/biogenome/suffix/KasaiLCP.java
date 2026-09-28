package com.biogenome.suffix;

import com.biogenome.model.DnaSequence;
import com.biogenome.model.SuffixArrayResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kasai's Linear-Time Longest Common Prefix (LCP) Array Construction.
 * <p>
 * Given a sequence of length N and its Suffix Array, Kasai's algorithm computes the
 * LCP array in strictly O(N) time. The algorithm leverages the key mathematical invariant:
 * <code>LCP[Rank[i]] &ge; LCP[Rank[i-1]] - 1</code>, which ensures that the character comparison
 * pointer decreases at most N times across all suffixes.
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(N) strictly linear</li>
 *   <li><b>Space Complexity:</b> O(N) for Rank and LCP arrays</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class KasaiLCP {

    /**
     * Constructs the LCP Array using Kasai's algorithm.
     *
     * @param sequence    The genomic text
     * @param suffixArray The precomputed Suffix Array
     * @return Integer array of size N where LCP[i] is the length of LCP between suffix SA[i-1] and SA[i] (LCP[0]=0)
     */
    public static int[] computeLCPArray(String sequence, int[] suffixArray) {
        if (sequence == null || suffixArray == null || sequence.isEmpty()) {
            return new int[0];
        }

        int n = sequence.length();
        if (suffixArray.length != n) {
            throw new IllegalArgumentException("Suffix array size must match sequence length");
        }

        // 1. Build Inverse Suffix Array (Rank Array)
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0; // Length of current known common prefix

        // 2. Iterate suffixes in order of their original text positions (0 to n-1)
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = suffixArray[rank[i] - 1]; // Preceding suffix in lexicographical order

                // Extend common prefix character by character
                while (i + h < n && j + h < n && sequence.charAt(i + h) == sequence.charAt(j + h)) {
                    h++;
                }

                lcp[rank[i]] = h;

                // Decrease h by 1 for next iteration (as guaranteed by Kasai's lemma)
                if (h > 0) {
                    h--;
                }
            } else {
                lcp[0] = 0; // First lexicographical suffix has no predecessor
            }
        }

        return lcp;
    }

    /**
     * Identifies the longest repeated nucleotide substrings (repeats) in a DNA sequence.
     *
     * @param sequence Genomic sequence
     * @return List of longest repeated DNA substring patterns
     */
    public static List<String> findLongestRepeatedSubstring(String sequence) {
        if (sequence == null || sequence.length() < 2) {
            return Collections.emptyList();
        }

        SuffixArray sa = new SuffixArray(sequence);
        int[] saArr = sa.getSuffixArray();
        int[] lcp = computeLCPArray(sequence, saArr);

        int maxLcp = 0;
        for (int val : lcp) {
            if (val > maxLcp) {
                maxLcp = val;
            }
        }

        if (maxLcp == 0) {
            return Collections.emptyList();
        }

        List<String> repeats = new ArrayList<>();
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] == maxLcp) {
                String sub = sequence.substring(saArr[i], saArr[i] + maxLcp);
                if (!repeats.contains(sub)) {
                    repeats.add(sub);
                }
            }
        }

        return repeats;
    }

    /**
     * Computes the complete {@link SuffixArrayResult} composite object.
     *
     * @param dnaSequence Domain sequence record
     * @return Complete SuffixArrayResult
     */
    public static SuffixArrayResult analyze(DnaSequence dnaSequence) {
        if (dnaSequence == null) {
            return new SuffixArrayResult("", new int[0], new int[0], new int[0], Collections.emptyList());
        }

        String seq = dnaSequence.getSequence();
        SuffixArray sa = new SuffixArray(seq);
        int[] saArr = sa.getSuffixArray();
        int[] lcp = computeLCPArray(seq, saArr);

        int[] rank = new int[seq.length()];
        for (int i = 0; i < seq.length(); i++) {
            rank[saArr[i]] = i;
        }

        List<String> repeats = findLongestRepeatedSubstring(seq);
        return new SuffixArrayResult(seq, saArr, lcp, rank, repeats);
    }
}
