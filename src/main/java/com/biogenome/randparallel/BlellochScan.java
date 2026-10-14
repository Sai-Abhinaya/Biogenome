package com.biogenome.randparallel;

import java.util.*;

/**
 * Blelloch Parallel Prefix Scan (Work-Efficient Prefix Sum).
 * <p>
 * Employs a binary tree structure across two distinct parallel phases:
 * <ol>
 *   <li><b>Up-Sweep (Reduce Phase):</b> Ascends the binary tree, computing pairwise sums in O(log N) parallel steps.</li>
 *   <li><b>Down-Sweep (Distribute Phase):</b> Descends from the root (seeded with 0), distributing partial prefix sums in O(log N) steps.</li>
 * </ol>
 * <b>Bioinformatics Application:</b> Rapid cumulative sequencing depth calculation,
 * genomic locus coverage aggregation, and parallel indexing.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Work Complexity W:</b> O(N) operations (work-efficient)</li>
 *   <li><b>Span (Parallel Depth) S:</b> O(log N) parallel execution time</li>
 *   <li><b>Processor Utilization:</b> Linear speedup under P processors</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class BlellochScan {

    public static final class TreePhaseStep {
        private final String phaseName; // "Up-Sweep" or "Down-Sweep"
        private final int stepLevel;
        private final int stride;
        private final long[] arraySnapshot;
        private final String description;

        public TreePhaseStep(String phaseName, int stepLevel, int stride, long[] arraySnapshot, String description) {
            this.phaseName = phaseName;
            this.stepLevel = stepLevel;
            this.stride = stride;
            this.arraySnapshot = arraySnapshot.clone();
            this.description = description;
        }

        public String getPhaseName() { return phaseName; }
        public int getStepLevel() { return stepLevel; }
        public int getStride() { return stride; }
        public long[] getArraySnapshot() { return arraySnapshot; }
        public String getDescription() { return description; }
    }

    public static final class ScanResult {
        private final long[] originalInput;
        private final long[] exclusivePrefixSum;
        private final long[] inclusivePrefixSum;
        private final List<TreePhaseStep> upSweepSteps;
        private final List<TreePhaseStep> downSweepSteps;
        private final int paddedLength;
        private final int workOperations;
        private final int spanSteps;
        private final long executionTimeNs;

        public ScanResult(long[] originalInput, long[] exclusivePrefixSum, long[] inclusivePrefixSum,
                          List<TreePhaseStep> upSweepSteps, List<TreePhaseStep> downSweepSteps,
                          int paddedLength, int workOperations, int spanSteps, long executionTimeNs) {
            this.originalInput = originalInput;
            this.exclusivePrefixSum = exclusivePrefixSum;
            this.inclusivePrefixSum = inclusivePrefixSum;
            this.upSweepSteps = upSweepSteps;
            this.downSweepSteps = downSweepSteps;
            this.paddedLength = paddedLength;
            this.workOperations = workOperations;
            this.spanSteps = spanSteps;
            this.executionTimeNs = executionTimeNs;
        }

        public long[] getOriginalInput() { return originalInput; }
        public long[] getExclusivePrefixSum() { return exclusivePrefixSum; }
        public long[] getInclusivePrefixSum() { return inclusivePrefixSum; }
        public List<TreePhaseStep> getUpSweepSteps() { return upSweepSteps; }
        public List<TreePhaseStep> getDownSweepSteps() { return downSweepSteps; }
        public int getPaddedLength() { return paddedLength; }
        public int getWorkOperations() { return workOperations; }
        public int getSpanSteps() { return spanSteps; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "Work W = O(N), Span S = O(log N)"; }
    }

    public static ScanResult scan(long[] input) {
        long start = System.nanoTime();
        if (input == null || input.length == 0) {
            return new ScanResult(new long[0], new long[0], new long[0],
                    Collections.emptyList(), Collections.emptyList(), 0, 0, 0, 0);
        }

        int n = input.length;
        // Pad to nearest power of 2
        int m = 1;
        while (m < n) m <<= 1;

        long[] a = new long[m];
        System.arraycopy(input, 0, a, 0, n);

        List<TreePhaseStep> upSweepSteps = new ArrayList<>();
        List<TreePhaseStep> downSweepSteps = new ArrayList<>();
        int workOps = 0;
        int spanSteps = 0;

        // 1. Up-Sweep (Reduce Phase)
        for (int d = 0; (1 << d) < m; d++) {
            spanSteps++;
            int stride = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += stride) {
                a[k + stride - 1] += a[k + half - 1];
                workOps++;
            }
            upSweepSteps.add(new TreePhaseStep("Up-Sweep", d + 1, stride, a,
                    String.format("Level %d: Pairwise reduction with stride %d", d + 1, stride)));
        }

        // 2. Clear root (last element) to 0
        a[m - 1] = 0;
        downSweepSteps.add(new TreePhaseStep("Down-Sweep", 0, m, a, "Root initialized to 0 (identity element)"));

        // 3. Down-Sweep (Distribute Phase)
        int downLevel = 1;
        for (int d = (int) (Math.log(m) / Math.log(2)) - 1; d >= 0; d--) {
            spanSteps++;
            int stride = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += stride) {
                long t = a[k + half - 1];
                a[k + half - 1] = a[k + stride - 1];
                a[k + stride - 1] += t;
                workOps += 2;
            }
            downSweepSteps.add(new TreePhaseStep("Down-Sweep", downLevel++, stride, a,
                    String.format("Level %d: Distributed prefix sum with stride %d", downLevel - 1, stride)));
        }

        // Result extraction
        long[] exclusive = new long[n];
        long[] inclusive = new long[n];
        System.arraycopy(a, 0, exclusive, 0, n);
        for (int i = 0; i < n; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }

        long elapsedNs = System.nanoTime() - start;
        return new ScanResult(input, exclusive, inclusive, upSweepSteps, downSweepSteps,
                m, workOps, spanSteps, elapsedNs);
    }
}
