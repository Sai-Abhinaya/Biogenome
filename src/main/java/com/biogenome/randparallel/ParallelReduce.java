package com.biogenome.randparallel;

import java.util.*;

/**
 * Parallel Tree Reduction for Genomic Data Aggregation.
 * <p>
 * Aggregates large-scale genomic arrays (such as contig lengths, GC counts, quality scores,
 * and read depths) using a balanced binary reduction tree in O(log N) parallel steps.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Work Complexity W:</b> O(N) operations</li>
 *   <li><b>Span (Depth) S:</b> O(log N) parallel execution time</li>
 *   <li><b>Speedup on P processors:</b> S_P &approx; min(P, N / log N)</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class ParallelReduce {

    public static final class ReduceLevel {
        private final int level;
        private final List<Long> values;
        private final List<String> operationDescriptions;

        public ReduceLevel(int level, List<Long> values, List<String> operationDescriptions) {
            this.level = level;
            this.values = new ArrayList<>(values);
            this.operationDescriptions = operationDescriptions;
        }

        public int getLevel() { return level; }
        public List<Long> getValues() { return values; }
        public List<String> getOperationDescriptions() { return operationDescriptions; }
    }

    public static final class ReduceResult {
        private final long[] originalInput;
        private final long finalResult;
        private final List<ReduceLevel> reductionTree;
        private final int workOperations;
        private final int spanSteps;
        private final long executionTimeNs;

        public ReduceResult(long[] originalInput, long finalResult, List<ReduceLevel> reductionTree,
                            int workOperations, int spanSteps, long executionTimeNs) {
            this.originalInput = originalInput;
            this.finalResult = finalResult;
            this.reductionTree = reductionTree;
            this.workOperations = workOperations;
            this.spanSteps = spanSteps;
            this.executionTimeNs = executionTimeNs;
        }

        public long[] getOriginalInput() { return originalInput; }
        public long getFinalResult() { return finalResult; }
        public List<ReduceLevel> getReductionTree() { return reductionTree; }
        public int getWorkOperations() { return workOperations; }
        public int getSpanSteps() { return spanSteps; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "Work W = O(N), Span S = O(log N)"; }
    }

    public static ReduceResult reduce(long[] input) {
        long start = System.nanoTime();
        if (input == null || input.length == 0) {
            return new ReduceResult(new long[0], 0, Collections.emptyList(), 0, 0, 0);
        }

        List<ReduceLevel> tree = new ArrayList<>();
        List<Long> current = new ArrayList<>();
        for (long val : input) current.add(val);

        tree.add(new ReduceLevel(0, current, Collections.singletonList("Input level: " + current.size() + " items")));

        int workOps = 0;
        int levelNum = 1;

        while (current.size() > 1) {
            List<Long> next = new ArrayList<>();
            List<String> opDescs = new ArrayList<>();

            for (int i = 0; i < current.size(); i += 2) {
                if (i + 1 < current.size()) {
                    long a = current.get(i);
                    long b = current.get(i + 1);
                    long sum = a + b;
                    next.add(sum);
                    workOps++;
                    opDescs.add(String.format("%d + %d = %d", a, b, sum));
                } else {
                    // Carry over odd element
                    next.add(current.get(i));
                    opDescs.add(String.format("%d (carried over)", current.get(i)));
                }
            }

            tree.add(new ReduceLevel(levelNum++, next, opDescs));
            current = next;
        }

        long finalSum = current.get(0);
        int spanSteps = tree.size() - 1;
        long elapsedNs = System.nanoTime() - start;

        return new ReduceResult(input, finalSum, tree, workOps, spanSteps, elapsedNs);
    }
}
