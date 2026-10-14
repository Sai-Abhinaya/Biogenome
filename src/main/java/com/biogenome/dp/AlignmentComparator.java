package com.biogenome.dp;

import com.biogenome.model.AlignmentResult;
import com.biogenome.model.EditDistanceResult;

import java.util.*;

/**
 * Comparative Dynamic Programming Service for Module 3.
 * Compares Global (Needleman-Wunsch), Local (Smith-Waterman), and Edit Distance (Damerau-Levenshtein) side-by-side.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class AlignmentComparator {

    public static final class MethodResult {
        private final String methodName;
        private final String biologicalGoal;
        private final String scoreOrDistance;
        private final String similarityScore;
        private final String alignedSeqA;
        private final String alignedSeqB;
        private final String visualAlignment;
        private final double executionTimeMs;
        private final String timeComplexity;
        private final String spaceComplexity;
        private final String interpretation;

        public MethodResult(String methodName, String biologicalGoal, String scoreOrDistance,
                            String similarityScore, String alignedSeqA, String alignedSeqB,
                            String visualAlignment, double executionTimeMs, String timeComplexity,
                            String spaceComplexity, String interpretation) {
            this.methodName = methodName;
            this.biologicalGoal = biologicalGoal;
            this.scoreOrDistance = scoreOrDistance;
            this.similarityScore = similarityScore;
            this.alignedSeqA = alignedSeqA;
            this.alignedSeqB = alignedSeqB;
            this.visualAlignment = visualAlignment;
            this.executionTimeMs = executionTimeMs;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.interpretation = interpretation;
        }

        public String getMethodName() { return methodName; }
        public String getBiologicalGoal() { return biologicalGoal; }
        public String getScoreOrDistance() { return scoreOrDistance; }
        public String getSimilarityScore() { return similarityScore; }
        public String getAlignedSeqA() { return alignedSeqA; }
        public String getAlignedSeqB() { return alignedSeqB; }
        public String getVisualAlignment() { return visualAlignment; }
        public double getExecutionTimeMs() { return executionTimeMs; }
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
        public String getInterpretation() { return interpretation; }
    }

    public static final class DpComparisonResult {
        private final String seq1;
        private final String seq2;
        private final List<MethodResult> methods;

        public DpComparisonResult(String seq1, String seq2, List<MethodResult> methods) {
            this.seq1 = seq1;
            this.seq2 = seq2;
            this.methods = methods;
        }

        public String getSeq1() { return seq1; }
        public String getSeq2() { return seq2; }
        public List<MethodResult> getMethods() { return methods; }
    }

    public static DpComparisonResult compareAlignments(String seq1, String seq2,
                                                       int match, int mismatch, int gap) {
        if (seq1 == null || seq2 == null || seq1.isEmpty() || seq2.isEmpty()) {
            throw new IllegalArgumentException("Sequences cannot be empty");
        }

        List<MethodResult> methods = new ArrayList<>();

        // 1. Needleman-Wunsch (Global)
        long t1 = System.nanoTime();
        NeedlemanWunschAligner nw = new NeedlemanWunschAligner(match, mismatch, gap);
        AlignmentResult nwRes = nw.align(seq1, seq2);
        double nwMs = (System.nanoTime() - t1) / 1_000_000.0;
        methods.add(new MethodResult(
                "Needleman-Wunsch (Global)",
                "Full End-to-End Sequence Similarity",
                "Score: " + nwRes.getAlignmentScore(),
                String.format("%.1f%% Identity", nwRes.getIdentityPercentage()),
                nwRes.getAlignedSeq1(),
                nwRes.getAlignedSeq2(),
                nwRes.getVisualAlignment(),
                nwMs,
                "O(N * M)",
                "O(N * M)",
                "Aligns every single base from start to end. Best for homologous genes of similar length."
        ));

        // 2. Smith-Waterman (Local)
        long t2 = System.nanoTime();
        SmithWatermanAligner sw = new SmithWatermanAligner(match, mismatch, gap);
        AlignmentResult swRes = sw.align(seq1, seq2);
        double swMs = (System.nanoTime() - t2) / 1_000_000.0;
        methods.add(new MethodResult(
                "Smith-Waterman (Local)",
                "Conserved Motif / Domain Extraction",
                "Score: " + swRes.getAlignmentScore(),
                String.format("%.1f%% Local Identity", swRes.getIdentityPercentage()),
                swRes.getAlignedSeq1(),
                swRes.getAlignedSeq2(),
                swRes.getVisualAlignment(),
                swMs,
                "O(N * M)",
                "O(N * M)",
                "Finds the highest-scoring local matching islands. Tolerates large flanking non-homologous regions."
        ));

        // 3. Damerau-Levenshtein (Edit Distance)
        long t3 = System.nanoTime();
        EditDistanceResult dlRes = DamerauLevenshteinAligner.align(seq1, seq2);
        double dlMs = (System.nanoTime() - t3) / 1_000_000.0;
        List<String> opNames = new ArrayList<>();
        if (dlRes.getOperations() != null) {
            for (EditDistanceResult.EditOperation op : dlRes.getOperations()) {
                opNames.add(op.name());
            }
        }
        methods.add(new MethodResult(
                "Damerau-Levenshtein (Edit Distance)",
                "Sequencing Error Correction & Edit Budget",
                "Distance: " + dlRes.getDistance() + " ops",
                String.format("%.1f%% Similarity", dlRes.getSimilarity()),
                dlRes.getQuerySequence(),
                dlRes.getTargetSequence(),
                "Operations: " + String.join(" -> ", opNames),
                dlMs,
                "O(N * M)",
                "O(N * M)",
                "Minimum number of mutations, indels, or adjacent transpositions needed to transform Sequence A into B."
        ));

        return new DpComparisonResult(seq1, seq2, methods);
    }
}
