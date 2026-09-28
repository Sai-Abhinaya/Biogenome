package com.biogenome.string;

import com.biogenome.model.DnaSequence;
import com.biogenome.model.MotifMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) Exact DNA Motif Matching Algorithm.
 * <p>
 * Optimizes linear sequence search by computing the Longest Proper Prefix which is
 * also a Suffix (&pi; / LPS table). When a nucleotide mismatch occurs, the search
 * pointer skips redundant comparisons without backtracking the target genome text.
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Preprocessing Time:</b> O(M) where M = length of target motif</li>
 *   <li><b>Search Time:</b> O(N) where N = length of genomic sequence</li>
 *   <li><b>Total Time:</b> O(N + M)</li>
 *   <li><b>Space Complexity:</b> O(M) for the &pi; table</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class KMPMotifFinder {

    /**
     * Computes the &pi; (LPS - Longest Proper Prefix which is also Suffix) table for a DNA motif.
     * <p>
     * &pi;[i] stores the length of the longest proper prefix of motif[0..i] that is
     * also a suffix of motif[0..i].
     * </p>
     *
     * @param targetMotif Target nucleotide pattern
     * @return Integer array of size M representing &pi; values
     */
    public static int[] computePrefixFunction(String targetMotif) {
        if (targetMotif == null || targetMotif.isEmpty()) {
            return new int[0];
        }

        int m = targetMotif.length();
        int[] pi = new int[m];
        pi[0] = 0; // Proper prefix of length 1 is empty string

        int k = 0; // Length of current longest matching prefix
        for (int i = 1; i < m; i++) {
            // Backtrack k along failure transitions until match or reach start
            while (k > 0 && targetMotif.charAt(k) != targetMotif.charAt(i)) {
                k = pi[k - 1];
            }
            if (targetMotif.charAt(k) == targetMotif.charAt(i)) {
                k++;
            }
            pi[i] = k;
        }

        return pi;
    }

    /**
     * Finds all 0-based starting indices of a target motif in a genomic DNA sequence.
     * Supports overlapping occurrences (e.g. "AAA" in "AAAAAA" -> [0, 1, 2, 3]).
     *
     * @param dnaSequence Target genomic DNA string (length N)
     * @param targetMotif Target nucleotide motif to locate (length M)
     * @return List of starting indices where motif matches exactly
     */
    public static List<Integer> searchMotif(String dnaSequence, String targetMotif) {
        if (dnaSequence == null || targetMotif == null) {
            return Collections.emptyList();
        }

        int n = dnaSequence.length();
        int m = targetMotif.length();

        if (m == 0 || n == 0 || m > n) {
            return Collections.emptyList();
        }

        int[] pi = computePrefixFunction(targetMotif);
        List<Integer> occurrences = new ArrayList<>();

        int q = 0; // Number of characters matched in targetMotif
        for (int i = 0; i < n; i++) {
            while (q > 0 && targetMotif.charAt(q) != dnaSequence.charAt(i)) {
                q = pi[q - 1];
            }
            if (targetMotif.charAt(q) == dnaSequence.charAt(i)) {
                q++;
            }
            if (q == m) {
                // Match found at start index (i - m + 1)
                occurrences.add(i - m + 1);
                // Shift q according to prefix table to find overlapping matches
                q = pi[q - 1];
            }
        }

        return occurrences;
    }

    /**
     * High-level biological search wrapper returning rich {@link MotifMatch} domain objects.
     *
     * @param dnaSequence Validated genomic record
     * @param motifName   Descriptive label (e.g., "EcoRI Site", "TATA Box")
     * @param targetMotif Nucleotide pattern to find
     * @return List of structured MotifMatch objects
     */
    public static List<MotifMatch> searchWithMatches(DnaSequence dnaSequence, String motifName, String targetMotif) {
        if (dnaSequence == null || targetMotif == null) return Collections.emptyList();

        List<Integer> indices = searchMotif(dnaSequence.getSequence(), targetMotif);
        List<MotifMatch> matches = new ArrayList<>(indices.size());
        String seq = dnaSequence.getSequence();
        int mLen = targetMotif.length();

        for (int idx : indices) {
            int ctxStart = Math.max(0, idx - 5);
            int ctxEnd = Math.min(seq.length(), idx + mLen + 5);
            String snippet = seq.substring(ctxStart, ctxEnd);
            matches.add(new MotifMatch(motifName, targetMotif, idx, idx + mLen, 1.0, snippet));
        }

        return matches;
    }
}
