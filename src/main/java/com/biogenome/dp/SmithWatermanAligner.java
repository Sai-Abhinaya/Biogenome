package com.biogenome.dp;

import com.biogenome.model.AlignmentResult;
import com.biogenome.model.DnaSequence;

/**
 * Smith-Waterman Dynamic Programming Algorithm for Local Pairwise DNA Sequence Alignment.
 * <p>
 * Identifies high-scoring conserved regions, homologous gene domains, and localized motifs
 * between two sequences without penalizing non-matching flanking ends.
 * </p>
 * 
 * <b>Recurrence Relation:</b>
 * <pre>
 * H[i][j] = max {
 *     0                                  (Local alignment restart floor)
 *     H[i-1][j-1] + score(S1[i], S2[j])  (Match / Mismatch)
 *     H[i-1][j]   + gapPenalty           (Deletion)
 *     H[i][j-1]   + gapPenalty           (Insertion)
 * }
 * </pre>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(N &times; M)</li>
 *   <li><b>Space Complexity:</b> O(N &times; M)</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class SmithWatermanAligner {

    public static final int DEFAULT_MATCH_SCORE = 3;
    public static final int DEFAULT_MISMATCH_PENALTY = -2;
    public static final int DEFAULT_GAP_PENALTY = -2;

    private final int matchScore;
    private final int mismatchPenalty;
    private final int gapPenalty;

    public SmithWatermanAligner() {
        this(DEFAULT_MATCH_SCORE, DEFAULT_MISMATCH_PENALTY, DEFAULT_GAP_PENALTY);
    }

    public SmithWatermanAligner(int matchScore, int mismatchPenalty, int gapPenalty) {
        this.matchScore = matchScore;
        this.mismatchPenalty = mismatchPenalty;
        this.gapPenalty = gapPenalty;
    }

    /**
     * Executes local pairwise sequence alignment between two genomic sequences.
     *
     * @param seq1 First DNA sequence
     * @param seq2 Second DNA sequence
     * @return AlignmentResult containing local conserved alignment domain
     */
    public AlignmentResult align(String seq1, String seq2) {
        if (seq1 == null) seq1 = "";
        if (seq2 == null) seq2 = "";

        int n = seq1.length();
        int m = seq2.length();

        int[][] dp = new int[n + 1][m + 1];
        int maxScore = 0;
        int maxI = 0;
        int maxJ = 0;

        // DP table initialization (all zeros)
        for (int i = 1; i <= n; i++) {
            char c1 = seq1.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                char c2 = seq2.charAt(j - 1);
                int scoreSub = (c1 == c2) ? matchScore : mismatchPenalty;

                int diagonal = dp[i - 1][j - 1] + scoreSub;
                int up = dp[i - 1][j] + gapPenalty;
                int left = dp[i][j - 1] + gapPenalty;

                dp[i][j] = Math.max(0, Math.max(diagonal, Math.max(up, left)));

                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback from maximum scoring cell (maxI, maxJ) until score becomes 0
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();

        int i = maxI;
        int j = maxJ;

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            char c1 = seq1.charAt(i - 1);
            char c2 = seq2.charAt(j - 1);
            int scoreSub = (c1 == c2) ? matchScore : mismatchPenalty;

            if (dp[i][j] == dp[i - 1][j - 1] + scoreSub) {
                aligned1.append(c1);
                aligned2.append(c2);
                i--;
                j--;
            } else if (dp[i][j] == dp[i - 1][j] + gapPenalty) {
                aligned1.append(c1);
                aligned2.append('-');
                i--;
            } else if (dp[i][j] == dp[i][j - 1] + gapPenalty) {
                aligned1.append('-');
                aligned2.append(c2);
                j--;
            } else {
                break;
            }
        }

        int startPos1 = i;
        int startPos2 = j;

        String res1 = aligned1.reverse().toString();
        String res2 = aligned2.reverse().toString();

        return new AlignmentResult(
                AlignmentResult.AlignmentType.LOCAL_SMITH_WATERMAN,
                maxScore,
                res1,
                res2,
                startPos1, maxI,
                startPos2, maxJ,
                dp
        );
    }

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
