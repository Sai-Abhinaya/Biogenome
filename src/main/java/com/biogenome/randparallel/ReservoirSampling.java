package com.biogenome.randparallel;

import com.biogenome.model.ContinuousObservation;

import java.util.*;

/**
 * Reservoir Sampling Algorithm (Algorithm R).
 * <p>
 * Selects an unbiased random sample of size <code>k</code> from an unbounded or continuous stream
 * of biological observations without requiring the total sequence count to be known in advance.
 * <br/>
 * <b>Mathematical Invariant:</b> At any point after observing <code>N</code> stream items,
 * every individual item has an exact, uniform selection probability of <code>k / N</code>.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(N) single pass over data stream</li>
 *   <li><b>Space Complexity:</b> O(k) strictly bounded memory</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class ReservoirSampling {

    public static final class StreamStep {
        private final int itemIndex; // 1-based index in stream
        private final ContinuousObservation observation;
        private final String action; // "DIRECT_INSERT", "REPLACED", "DISCARDED"
        private final int replacedSlot; // -1 if not replaced
        private final List<ContinuousObservation> reservoirSnapshot;
        private final double currentInclusionProbability;

        public StreamStep(int itemIndex, ContinuousObservation observation, String action,
                          int replacedSlot, List<ContinuousObservation> reservoirSnapshot,
                          double currentInclusionProbability) {
            this.itemIndex = itemIndex;
            this.observation = observation;
            this.action = action;
            this.replacedSlot = replacedSlot;
            this.reservoirSnapshot = new ArrayList<>(reservoirSnapshot);
            this.currentInclusionProbability = currentInclusionProbability;
        }

        public int getItemIndex() { return itemIndex; }
        public ContinuousObservation getObservation() { return observation; }
        public String getAction() { return action; }
        public int getReplacedSlot() { return replacedSlot; }
        public List<ContinuousObservation> getReservoirSnapshot() { return reservoirSnapshot; }
        public double getCurrentInclusionProbability() { return currentInclusionProbability; }
    }

    public static final class ReservoirResult {
        private final int reservoirCapacity;
        private final int totalStreamItems;
        private final List<ContinuousObservation> finalSample;
        private final List<StreamStep> executionSteps;
        private final double theoreticalProbability;
        private final long executionTimeNs;

        public ReservoirResult(int reservoirCapacity, int totalStreamItems,
                               List<ContinuousObservation> finalSample, List<StreamStep> executionSteps,
                               double theoreticalProbability, long executionTimeNs) {
            this.reservoirCapacity = reservoirCapacity;
            this.totalStreamItems = totalStreamItems;
            this.finalSample = finalSample;
            this.executionSteps = executionSteps;
            this.theoreticalProbability = theoreticalProbability;
            this.executionTimeNs = executionTimeNs;
        }

        public int getReservoirCapacity() { return reservoirCapacity; }
        public int getTotalStreamItems() { return totalStreamItems; }
        public List<ContinuousObservation> getFinalSample() { return finalSample; }
        public List<StreamStep> getExecutionSteps() { return executionSteps; }
        public double getTheoreticalProbability() { return theoreticalProbability; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "O(N) Time, O(k) Space"; }
    }

    private static final Random random = new Random();

    public static ReservoirResult sampleStream(List<ContinuousObservation> stream, int k) {
        long start = System.nanoTime();
        if (k <= 0) {
            throw new IllegalArgumentException("Reservoir capacity must be positive: " + k);
        }

        List<ContinuousObservation> reservoir = new ArrayList<>(k);
        List<StreamStep> steps = new ArrayList<>();
        int n = stream.size();

        for (int i = 0; i < n; i++) {
            ContinuousObservation item = stream.get(i);
            int itemNum = i + 1; // 1-based

            if (i < k) {
                // Initial fill of reservoir
                reservoir.add(item);
                double prob = 1.0;
                steps.add(new StreamStep(itemNum, item, "DIRECT_INSERT", i, reservoir, prob));
            } else {
                // Uniformly choose random index in [0, i]
                int j = random.nextInt(i + 1);
                double prob = (double) k / itemNum;

                if (j < k) {
                    reservoir.set(j, item);
                    steps.add(new StreamStep(itemNum, item, "REPLACED", j, reservoir, prob));
                } else {
                    steps.add(new StreamStep(itemNum, item, "DISCARDED", -1, reservoir, prob));
                }
            }
        }

        double finalProb = n > 0 ? Math.min(1.0, (double) k / n) : 1.0;
        long elapsedNs = System.nanoTime() - start;
        return new ReservoirResult(k, n, new ArrayList<>(reservoir), steps, finalProb, elapsedNs);
    }
}
