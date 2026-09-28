package com.biogenome.dp;

import com.biogenome.model.AlignmentResult;
import com.biogenome.model.DnaSequence;

/**
 * Needleman-Wunsch Dynamic Programming Algorithm for Global Pairwise DNA Sequence Alignment.
 * <p>
 * Finds the optimal end-to-end alignment between two genomic sequences by optimizing
 * a score matrix accounting for matches, mismatches, and indel penalties.
 * </p>
 * 
 * <b>Recurrence Relation:</b>
 * <pre>
 * D[i][j] = max {
 *     D[i-1][j-1] + score(S1[i], S2[j])  (Diagonal: Match / Mismatch)
 *     D[i-1][j]   + gapPenalty           (Up: Deletion in S2 / Gap in S2)
 *     D[i][j-1]   + gapPenalty           (Left: Insertion in S2 / Gap in S1)
 * }
 * </pre>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(N &times; M) where N = |S1|, M = |S2|</li>
 *   <li><b>Space Complexity:</b> O(N &times; M) for DP scoring matrix</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class NeedlemanWunschAligner {

    public static final int DEFAULT_MATCH_SCORE = 2;
    public static final int DEFAULT_MISMATCH_PENALTY = -1;
    public static final int DEFAULT_GAP_PENALTY = -2;

    private final int matchScore;
    private final int mismatchPenalty;
    private final int gapPenalty;

    public NeedlemanWunschAligner() {
        this(DEFAULT_MATCH_SCORE, DEFAULT_MISMATCH_PENALTY, DEFAULT_GAP_PENALTY);
    }

    public NeedlemanWunschAligner(int matchScore, int mismatchPenalty, int gapPenalty) {
        this.matchScore = matchScore;
        this.mismatchPenalty = mismatchPenalty;
        this.gapPenalty = gapPenalty;
    }

    /**
     * Executes global pairwise sequence alignment between two DNA sequences.
     *
     * @param seq1 First genomic DNA sequence (length N)
     * @param seq2 Second genomic DNA sequence (length M)
     * @return {@link AlignmentResult} with optimal alignment, score, and CIGAR string
     */
    public AlignmentResult align(String seq1, String seq2) {
        if (seq1 == null) seq1 = "";
        if (seq2 == null) seq2 = "";

        int n = seq1.length();
        int m = seq2.length();

        int[][] dp = new int[n + 1][m + 1];

        // Base cases: aligning against gaps
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * gapPenalty;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * gapPenalty;
        }

        // Fill dynamic programming matrix
        for (int i = 1; i <= n; i++) {
            char c1 = seq1.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                char c2 = seq2.charAt(j - 1);
                int scoreSub = (c1 == c2) ? matchScore : mismatchPenalty;

                int diagonal = dp[i - 1][j - 1] + scoreSub;
                int up = dp[i - 1][j] + gapPenalty;
                int left = dp[i][j - 1] + gapPenalty;

                dp[i][j] = Math.max(diagonal, Math.max(up, left));
            }
        }

        // Traceback from (n, m) to (0, 0)
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();

        int i = n;
        int j = m;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0) {
                char c1 = seq1.charAt(i - 1);
                char c2 = seq2.charAt(j - 1);
                int scoreSub = (c1 == c2) ? matchScore : mismatchPenalty;

                if (dp[i][j] == dp[i - 1][j - 1] + scoreSub) {
                    aligned1.append(c1);
                    aligned2.append(c2);
                    i--;
                    j--;
                    continue;
                }
            }

            if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
                aligned1.append(seq1.charAt(i - 1));
                aligned2.append('-');
                i--;
            } else {
                aligned1.append('-');
                aligned2.append(seq2.charAt(j - 1));
                j--;
            }
        }

        String res1 = aligned1.reverse().toString();
        String res2 = aligned2.reverse().toString();

        return new AlignmentResult(
                AlignmentResult.AlignmentType.GLOBAL_NEEDLEMAN_WUNSCH,
                dp[n][m],
                res1,
                res2,
                0, n,
                0, m,
                dp
        );
    }

    /**
     * Domain wrapper aligning two {@link DnaSequence} records.
     */
    public AlignmentResult align(DnaSequence s1, DnaSequence s2) {
        String seq1 = s1 != null ? s1.getSequence() : "";
        String seq2 = s2 != null ? s2.getSequence() : "";
        return align(seq1, seq2);
    }

    public int getMatchScore() {
        return matchScore;
    }

    public int getMismatchPenalty() {
        return mismatchPenalty;
    }

    public int getGapPenalty() {
        return gapPenalty;
    }
}
