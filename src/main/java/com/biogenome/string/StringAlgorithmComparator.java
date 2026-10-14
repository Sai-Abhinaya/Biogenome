package com.biogenome.string;

import com.biogenome.model.DnaSequence;

import java.util.*;

/**
 * Comparative benchmark service for Module 1: String Algorithms.
 * Evaluates KMP, Z-Algorithm, and Rabin-Karp side-by-side on identical genomic sequences.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class StringAlgorithmComparator {

    public static final class AlgorithmBenchmark {
        private final String algorithmName;
        private final int matchesFound;
        private final List<Integer> matchIndices;
        private final double executionTimeMs;
        private final String timeComplexity;
        private final String spaceComplexity;
        private final String algorithmicApproach;
        private final Map<String, Object> details;

        public AlgorithmBenchmark(String algorithmName, int matchesFound, List<Integer> matchIndices,
                                  double executionTimeMs, String timeComplexity, String spaceComplexity,
                                  String algorithmicApproach, Map<String, Object> details) {
            this.algorithmName = algorithmName;
            this.matchesFound = matchesFound;
            this.matchIndices = matchIndices;
            this.executionTimeMs = executionTimeMs;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.algorithmicApproach = algorithmicApproach;
            this.details = details;
        }

        public String getAlgorithmName() { return algorithmName; }
        public int getMatchesFound() { return matchesFound; }
        public List<Integer> getMatchIndices() { return matchIndices; }
        public double getExecutionTimeMs() { return executionTimeMs; }
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
        public String getAlgorithmicApproach() { return algorithmicApproach; }
        public Map<String, Object> getDetails() { return details; }
    }

    public static final class ComparisonResult {
        private final String sequence;
        private final String pattern;
        private final int sequenceLength;
        private final int patternLength;
        private final List<AlgorithmBenchmark> benchmarks;
        private final boolean allMatchesConsistent;

        public ComparisonResult(String sequence, String pattern, List<AlgorithmBenchmark> benchmarks) {
            this.sequence = sequence;
            this.pattern = pattern;
            this.sequenceLength = sequence.length();
            this.patternLength = pattern.length();
            this.benchmarks = benchmarks;

            boolean consistent = true;
            if (benchmarks.size() > 1) {
                int firstCount = benchmarks.get(0).getMatchesFound();
                for (AlgorithmBenchmark b : benchmarks) {
                    if (b.getMatchesFound() != firstCount) {
                        consistent = false;
                        break;
                    }
                }
            }
            this.allMatchesConsistent = consistent;
        }

        public String getSequence() { return sequence; }
        public String getPattern() { return pattern; }
        public int getSequenceLength() { return sequenceLength; }
        public int getPatternLength() { return patternLength; }
        public List<AlgorithmBenchmark> getBenchmarks() { return benchmarks; }
        public boolean isAllMatchesConsistent() { return allMatchesConsistent; }
    }

    public static ComparisonResult compareAll(String sequence, String pattern) {
        if (sequence == null || pattern == null || sequence.isEmpty() || pattern.isEmpty()) {
            throw new IllegalArgumentException("Sequence and pattern must not be empty");
        }

        List<AlgorithmBenchmark> benchmarks = new ArrayList<>();

        // 1. KMP
        long startKmp = System.nanoTime();
        int[] piTable = KMPMotifFinder.computePrefixFunction(pattern);
        List<Integer> kmpMatches = KMPMotifFinder.searchMotif(sequence, pattern);
        double kmpTime = (System.nanoTime() - startKmp) / 1_000_000.0;
        Map<String, Object> kmpDetails = new LinkedHashMap<>();
        kmpDetails.put("piTableSample", Arrays.toString(Arrays.copyOf(piTable, Math.min(10, piTable.length))));
        benchmarks.add(new AlgorithmBenchmark(
                "Knuth-Morris-Pratt (KMP)",
                kmpMatches.size(),
                kmpMatches,
                kmpTime,
                "O(N + M)",
                "O(M)",
                "Deterministic Failure Function (π / LPS Table)",
                kmpDetails
        ));

        // 2. Z-Algorithm
        long startZ = System.nanoTime();
        List<Integer> zMatches = ZAlgorithm.searchMotif(sequence, pattern);
        String concat = pattern + "$" + sequence;
        int[] zArr = ZAlgorithm.calculateZArray(concat);
        double zTime = (System.nanoTime() - startZ) / 1_000_000.0;
        Map<String, Object> zDetails = new LinkedHashMap<>();
        zDetails.put("zArrayLength", zArr.length);
        benchmarks.add(new AlgorithmBenchmark(
                "Z-Algorithm",
                zMatches.size(),
                zMatches,
                zTime,
                "O(N + M)",
                "O(N + M)",
                "Exact Prefix Length Matching (Z-Box Boundaries [L, R])",
                zDetails
        ));

        // 3. Rabin-Karp
        long startRk = System.nanoTime();
        RabinKarpMatcher.SearchStats rkStats = RabinKarpMatcher.searchWithDetailedStats(sequence, pattern);
        double rkTime = (System.nanoTime() - startRk) / 1_000_000.0;
        Map<String, Object> rkDetails = new LinkedHashMap<>();
        rkDetails.put("spuriousHits", rkStats.getSpuriousHits());
        rkDetails.put("hashComparisons", rkStats.getHashComparisons());
        benchmarks.add(new AlgorithmBenchmark(
                "Rabin-Karp",
                rkStats.getMatches().size(),
                rkStats.getMatches(),
                rkTime,
                "O(N + M) avg, O(N*M) worst",
                "O(1)",
                "Rolling Polynomial Hash Fingerprinting",
                rkDetails
        ));

        return new ComparisonResult(sequence, pattern, benchmarks);
    }
}
