package com.biogenome.string;

import com.biogenome.model.DnaSequence;
import com.biogenome.model.MotifMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Z-Algorithm for Linear Time Genomic Pattern Matching & Repeated Pattern Detection.
 * <p>
 * The Z-array for a string S of length N contains values where Z[i] is the length of the
 * longest common prefix (LCP) between S and the suffix of S starting at index i (S[i..N-1]).
 * The algorithm maintains an interval [L, R] representing the rightmost segment matching
 * a prefix of S (the "Z-box") to guarantee linear O(N) execution.
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(N + M) strictly linear</li>
 *   <li><b>Space Complexity:</b> O(N + M) for Z-array storage</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class ZAlgorithm {

    /**
     * Computes the Z-array for any given nucleotide sequence string.
     *
     * @param sequence Nucleotide string
     * @return Integer array of size N where Z[i] = length of LCP(S[0..N-1], S[i..N-1])
     */
    public static int[] calculateZArray(String sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return new int[0];
        }

        int n = sequence.length();
        int[] z = new int[n];
        z[0] = n; // Entire string matches itself

        int l = 0, r = 0; // Current Z-box boundary [l, r]

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                // i is inside the current Z-box; use precomputed symmetry
                int k = i - l;
                int remainingInBox = r - i + 1;
                if (z[k] < remainingInBox) {
                    z[i] = z[k];
                } else {
                    // Need to explicitly compare beyond current box boundary r
                    l = i;
                    while (r < n && sequence.charAt(r) == sequence.charAt(r - l)) {
                        r++;
                    }
                    z[i] = r - l;
                    r--; // Adjust r to inclusive boundary
                }
            } else {
                // i is outside current Z-box; initialize new box
                l = i;
                r = i;
                while (r < n && sequence.charAt(r) == sequence.charAt(r - l)) {
                    r++;
                }
                z[i] = r - l;
                r--;
            }
        }

        return z;
    }

    /**
     * Searches all occurrences of a target DNA motif in a genome sequence using the Z-algorithm.
     * Uses concatenation delimiter '$' (which does not appear in valid DNA sequences).
     *
     * @param dnaSequence Target genomic DNA sequence (length N)
     * @param targetMotif Nucleotide pattern to search (length M)
     * @return List of 0-based starting indices of pattern in dnaSequence
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

        // Delimiter '$' separates pattern and genome text
        String concat = targetMotif + "$" + dnaSequence;
        int[] z = calculateZArray(concat);

        List<Integer> matches = new ArrayList<>();
        int concatLen = concat.length();

        for (int i = m + 1; i < concatLen; i++) {
            if (z[i] >= m) {
                // Suffix starting at i matches targetMotif of length m
                matches.add(i - (m + 1));
            }
        }

        return matches;
    }

    /**
     * Detects repeated nucleotide patterns and tandem repeats starting at the sequence origin.
     *
     * @param dnaSequence Genomic sequence
     * @return List of indices where non-trivial prefix matches occur
     */
    public static List<Integer> findPrefixOccurrences(String dnaSequence) {
        if (dnaSequence == null || dnaSequence.length() < 2) {
            return Collections.emptyList();
        }

        int[] z = calculateZArray(dnaSequence);
        List<Integer> prefixOccurrences = new ArrayList<>();

        for (int i = 1; i < z.length; i++) {
            if (z[i] > 0) {
                prefixOccurrences.add(i);
            }
        }

        return prefixOccurrences;
    }

    /**
     * Biological search wrapper returning rich {@link MotifMatch} instances.
     *
     * @param dnaSequence Validated genomic sequence
     * @param motifName   Descriptive motif name
     * @param targetMotif Nucleotide pattern
     * @return List of MotifMatch records
     */
    public static List<MotifMatch> searchWithMatches(DnaSequence dnaSequence, String motifName, String targetMotif) {
        if (dnaSequence == null || targetMotif == null) return Collections.emptyList();

        List<Integer> indices = searchMotif(dnaSequence.getSequence(), targetMotif);
        List<MotifMatch> matches = new ArrayList<>(indices.size());
        String seq = dnaSequence.getSequence();
        int mLen = targetMotif.length();

        for (int idx : indices) {
            int ctxStart = Math.max(0, idx - 4);
            int ctxEnd = Math.min(seq.length(), idx + mLen + 4);
            String snippet = seq.substring(ctxStart, ctxEnd);
            matches.add(new MotifMatch(motifName, targetMotif, idx, idx + mLen, 1.0, snippet));
        }

        return matches;
    }
}
