package com.biogenome.dp;

import com.biogenome.model.DnaSequence;
import com.biogenome.model.EditDistanceResult;
import com.biogenome.model.EditDistanceResult.EditOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Damerau-Levenshtein Edit Distance for Genomic Sequencing Error Correction.
 * <p>
 * Quantifies sequence divergence and corrects sequencing errors across four fundamental
 * atomic mutational events:
 * <ul>
 *   <li><b>Insertion:</b> Addition of an extra nucleotide</li>
 *   <li><b>Deletion:</b> Omission of a nucleotide</li>
 *   <li><b>Substitution:</b> Single Nucleotide Polymorphism (SNP)</li>
 *   <li><b>Transposition:</b> Swapping of two adjacent nucleotides (common sequencing artifact)</li>
 * </ul>
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(N &times; M) where N = |query|, M = |target|</li>
 *   <li><b>Space Complexity:</b> O(N &times; M) for DP matrix and traceback</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class DamerauLevenshteinAligner {

    /**
     * Computes the Damerau-Levenshtein edit distance and reconstructs the mutational operation trail.
     *
     * @param query  Input read / mutated sequence (length N)
     * @param target Reference genome sequence (length M)
     * @return {@link EditDistanceResult} containing distance, operations list, and aligned strings
     */
    public static EditDistanceResult align(String query, String target) {
        if (query == null) query = "";
        if (target == null) target = "";

        int n = query.length();
        int m = target.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            char qc = query.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                char tc = target.charAt(j - 1);
                int cost = (qc == tc) ? 0 : 1;

                int del = dp[i - 1][j] + 1;
                int ins = dp[i][j - 1] + 1;
                int sub = dp[i - 1][j - 1] + cost;

                int minCost = Math.min(del, Math.min(ins, sub));

                // Check adjacent transposition condition
                if (i > 1 && j > 1 &&
                    query.charAt(i - 1) == target.charAt(j - 2) &&
                    query.charAt(i - 2) == target.charAt(j - 1)) {
                    minCost = Math.min(minCost, dp[i - 2][j - 2] + 1);
                }

                dp[i][j] = minCost;
            }
        }

        // Traceback to reconstruct alignment and atomic operations
        List<EditOperation> ops = new ArrayList<>();
        StringBuilder alignedQ = new StringBuilder();
        StringBuilder alignedT = new StringBuilder();
        StringBuilder cigar = new StringBuilder();

        int i = n;
        int j = m;

        while (i > 0 || j > 0) {
            // Check transposition first if applicable
            if (i > 1 && j > 1 &&
                query.charAt(i - 1) == target.charAt(j - 2) &&
                query.charAt(i - 2) == target.charAt(j - 1) &&
                dp[i][j] == dp[i - 2][j - 2] + 1) {

                ops.add(EditOperation.TRANSPOSITION);
                alignedQ.append(query.charAt(i - 1)).append(query.charAt(i - 2));
                alignedT.append(target.charAt(j - 1)).append(target.charAt(j - 2));
                cigar.append("2T");
                i -= 2;
                j -= 2;
                continue;
            }

            if (i > 0 && j > 0) {
                char qc = query.charAt(i - 1);
                char tc = target.charAt(j - 1);
                int cost = (qc == tc) ? 0 : 1;

                if (dp[i][j] == dp[i - 1][j - 1] + cost) {
                    if (cost == 0) {
                        ops.add(EditOperation.MATCH);
                        cigar.append("M");
                    } else {
                        ops.add(EditOperation.SUBSTITUTION);
                        cigar.append("S");
                    }
                    alignedQ.append(qc);
                    alignedT.append(tc);
                    i--;
                    j--;
                    continue;
                }
            }

            if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(EditOperation.DELETION);
                alignedQ.append(query.charAt(i - 1));
                alignedT.append('-');
                cigar.append("D");
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add(EditOperation.INSERTION);
                alignedQ.append('-');
                alignedT.append(target.charAt(j - 1));
                cigar.append("I");
                j--;
            } else {
                break;
            }
        }

        Collections.reverse(ops);
        String finalQ = alignedQ.reverse().toString();
        String finalT = alignedT.reverse().toString();
        String finalCigar = cigar.reverse().toString();

        return new EditDistanceResult(query, target, dp[n][m], ops, finalQ, finalT, finalCigar);
    }

    public static EditDistanceResult align(DnaSequence query, DnaSequence target) {
        String q = query != null ? query.getSequence() : "";
        String t = target != null ? target.getSequence() : "";
        return align(q, t);
    }
}
