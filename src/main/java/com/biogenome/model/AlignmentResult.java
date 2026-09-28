package com.biogenome.model;

import java.util.Objects;

/**
 * Encapsulates the output of Pairwise Sequence Alignment (Global or Local).
 * Contains score, aligned sequences with gaps, identity metrics, CIGAR string,
 * and visual alignment rendering.
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class AlignmentResult {

    public enum AlignmentType {
        GLOBAL_NEEDLEMAN_WUNSCH,
        LOCAL_SMITH_WATERMAN
    }

    private final AlignmentType alignmentType;
    private final int alignmentScore;
    private final String alignedSeq1;
    private final String alignedSeq2;
    private final int startPosSeq1;
    private final int endPosSeq1;
    private final int startPosSeq2;
    private final int endPosSeq2;
    private final int matches;
    private final int mismatches;
    private final int gaps;
    private final double identityPercentage;
    private final String cigarString;
    private final int[][] scoreMatrix;

    public AlignmentResult(AlignmentType alignmentType, int alignmentScore,
                           String alignedSeq1, String alignedSeq2,
                           int startPosSeq1, int endPosSeq1,
                           int startPosSeq2, int endPosSeq2,
                           int[][] scoreMatrix) {
        this.alignmentType = Objects.requireNonNull(alignmentType, "Alignment type cannot be null");
        this.alignmentScore = alignmentScore;
        this.alignedSeq1 = Objects.requireNonNull(alignedSeq1, "Aligned sequence 1 cannot be null");
        this.alignedSeq2 = Objects.requireNonNull(alignedSeq2, "Aligned sequence 2 cannot be null");
        this.startPosSeq1 = startPosSeq1;
        this.endPosSeq1 = endPosSeq1;
        this.startPosSeq2 = startPosSeq2;
        this.endPosSeq2 = endPosSeq2;
        this.scoreMatrix = scoreMatrix;

        int m = 0, mm = 0, g = 0;
        int alignLen = Math.min(alignedSeq1.length(), alignedSeq2.length());
        StringBuilder cigar = new StringBuilder();

        for (int i = 0; i < alignLen; i++) {
            char c1 = alignedSeq1.charAt(i);
            char c2 = alignedSeq2.charAt(i);
            if (c1 == '-' || c2 == '-') {
                g++;
                cigar.append(c1 == '-' ? 'I' : 'D');
            } else if (c1 == c2) {
                m++;
                cigar.append('M');
            } else {
                mm++;
                cigar.append('X');
            }
        }

        this.matches = m;
        this.mismatches = mm;
        this.gaps = g;
        this.identityPercentage = alignLen == 0 ? 0.0 : (m * 100.0) / alignLen;
        this.cigarString = compressCigar(cigar.toString());
    }

    private static String compressCigar(String raw) {
        if (raw.isEmpty()) return "";
        StringBuilder compressed = new StringBuilder();
        int count = 1;
        for (int i = 1; i < raw.length(); i++) {
            if (raw.charAt(i) == raw.charAt(i - 1)) {
                count++;
            } else {
                compressed.append(count).append(raw.charAt(i - 1));
                count = 1;
            }
        }
        compressed.append(count).append(raw.charAt(raw.length() - 1));
        return compressed.toString();
    }

    public AlignmentType getAlignmentType() {
        return alignmentType;
    }

    public int getAlignmentScore() {
        return alignmentScore;
    }

    public String getAlignedSeq1() {
        return alignedSeq1;
    }

    public String getAlignedSeq2() {
        return alignedSeq2;
    }

    public int getStartPosSeq1() {
        return startPosSeq1;
    }

    public int getEndPosSeq1() {
        return endPosSeq1;
    }

    public int getStartPosSeq2() {
        return startPosSeq2;
    }

    public int getEndPosSeq2() {
        return endPosSeq2;
    }

    public int getMatches() {
        return matches;
    }

    public int getMismatches() {
        return mismatches;
    }

    public int getGaps() {
        return gaps;
    }

    public double getIdentityPercentage() {
        return identityPercentage;
    }

    public String getCigarString() {
        return cigarString;
    }

    public int[][] getScoreMatrix() {
        return scoreMatrix;
    }

    /**
     * Generates an ASCII alignment visualization with match/mismatch pipes.
     *
     * @return Formatted multi-line string showing sequence pairing
     */
    public String getVisualAlignment() {
        StringBuilder mid = new StringBuilder();
        int len = Math.min(alignedSeq1.length(), alignedSeq2.length());
        for (int i = 0; i < len; i++) {
            char c1 = alignedSeq1.charAt(i);
            char c2 = alignedSeq2.charAt(i);
            if (c1 != '-' && c1 == c2) {
                mid.append('|');
            } else if (c1 == '-' || c2 == '-') {
                mid.append(' ');
            } else {
                mid.append('.');
            }
        }

        return String.format(
            "Seq1: %s (pos %d..%d)\n" +
            "      %s\n" +
            "Seq2: %s (pos %d..%d)\n" +
            "Score: %d | Identity: %.2f%% (%d/%d bp) | Gaps: %d | CIGAR: %s",
            alignedSeq1, startPosSeq1, endPosSeq1,
            mid.toString(),
            alignedSeq2, startPosSeq2, endPosSeq2,
            alignmentScore, identityPercentage, matches, len, gaps, cigarString
        );
    }

    @Override
    public String toString() {
        return String.format("[%s] Score: %d | Identity: %.2f%% | Length: %d",
                alignmentType, alignmentScore, identityPercentage, alignedSeq1.length());
    }
}
