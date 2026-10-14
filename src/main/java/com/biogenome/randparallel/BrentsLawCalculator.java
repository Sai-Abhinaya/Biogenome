package com.biogenome.randparallel;

import java.util.*;

/**
 * Interactive Brent's Theorem Calculator for Parallel Genome Processing.
 * <p>
 * Evaluates Brent's Scheduling Principle:
 * <br/>
 * <code>T<sub>P</sub> &le; (W - S) / P + S</code> &approx; <code>W / P + S</code>
 * <br/>
 * where:
 * <ul>
 *   <li><b>W:</b> Total Work (sequential execution time / total operations)</li>
 *   <li><b>S:</b> Span / Depth (length of the critical path / parallel execution time on &infin; cores)</li>
 *   <li><b>P:</b> Number of parallel processing units / compute cores</li>
 * </ul>
 * <b>Bioinformatics Application:</b> Capacity planning and cluster node allocation
 * for Next-Generation Sequencing (NGS) alignment, variant calling, and genome assembly pipelines.
 * </p>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class BrentsLawCalculator {

    public static final class CoreScalingPoint {
        private final int cores;
        private final double estimatedTime;
        private final double speedup;
        private final double efficiency;

        public CoreScalingPoint(int cores, double estimatedTime, double speedup, double efficiency) {
            this.cores = cores;
            this.estimatedTime = estimatedTime;
            this.speedup = speedup;
            this.efficiency = efficiency;
        }

        public int getCores() { return cores; }
        public double getEstimatedTime() { return estimatedTime; }
        public double getSpeedup() { return speedup; }
        public double getEfficiency() { return efficiency; }
    }

    public static final class BrentResult {
        private final double work;
        private final double span;
        private final int processors;
        private final double estimatedParallelTime;
        private final double speedup;
        private final double efficiency;
        private final double parallelismRatio; // W / S (maximum useful processors)
        private final String regimeInterpretation;
        private final List<CoreScalingPoint> scalingCurve;

        public BrentResult(double work, double span, int processors, double estimatedParallelTime,
                           double speedup, double efficiency, double parallelismRatio,
                           String regimeInterpretation, List<CoreScalingPoint> scalingCurve) {
            this.work = work;
            this.span = span;
            this.processors = processors;
            this.estimatedParallelTime = estimatedParallelTime;
            this.speedup = speedup;
            this.efficiency = efficiency;
            this.parallelismRatio = parallelismRatio;
            this.regimeInterpretation = regimeInterpretation;
            this.scalingCurve = scalingCurve;
        }

        public double getWork() { return work; }
        public double getSpan() { return span; }
        public int getProcessors() { return processors; }
        public double getEstimatedParallelTime() { return estimatedParallelTime; }
        public double getSpeedup() { return speedup; }
        public double getEfficiency() { return efficiency; }
        public double getParallelismRatio() { return parallelismRatio; }
        public String getRegimeInterpretation() { return regimeInterpretation; }
        public List<CoreScalingPoint> getScalingCurve() { return scalingCurve; }
        public String getFormula() { return "Tp <= (W - S) / P + S"; }
    }

    public static BrentResult calculate(double work, double span, int processors) {
        if (work <= 0 || span <= 0) {
            throw new IllegalArgumentException("Work and Span must be positive values");
        }
        if (span > work) {
            throw new IllegalArgumentException("Span cannot exceed total Work (S <= W invariant violated)");
        }
        if (processors <= 0) {
            processors = 1;
        }

        // Brent's inequality: T_P <= (W - S) / P + S
        double tp = (work - span) / processors + span;
        double speedup = work / tp;
        double efficiency = speedup / processors;
        double parallelism = work / span;

        String regime;
        if (processors < parallelism * 0.5) {
            regime = String.format("Work-Dominated Regime (P = %d < W/S = %.1f): High processor efficiency (%.1f%%). Adding cores will yield near-linear speedup.",
                    processors, parallelism, efficiency * 100);
        } else if (processors <= parallelism * 1.5) {
            regime = String.format("Balanced Regime (P = %d ~ W/S = %.1f): Approaching optimal processor capacity. Speedup is %.2fx with moderate saturation.",
                    processors, parallelism, speedup);
        } else {
            regime = String.format("Span-Dominated Regime (P = %d > W/S = %.1f): Diminishing returns. Critical path span (S = %.1f) limits further acceleration.",
                    processors, parallelism, span);
        }

        // Generate scaling curve for typical core counts: 1, 2, 4, 8, 16, 32, 64, 128
        List<CoreScalingPoint> curve = new ArrayList<>();
        int[] sampleCores = {1, 2, 4, 8, 16, 32, 64, 128};
        for (int c : sampleCores) {
            double cTime = (work - span) / c + span;
            double cSpeedup = work / cTime;
            double cEff = cSpeedup / c;
            curve.add(new CoreScalingPoint(c, cTime, cSpeedup, cEff));
        }

        return new BrentResult(work, span, processors, tp, speedup, efficiency, parallelism, regime, curve);
    }
}
