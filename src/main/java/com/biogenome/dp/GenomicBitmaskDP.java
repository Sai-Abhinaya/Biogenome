package com.biogenome.dp;

import java.util.*;

/**
 * Bitmask Dynamic Programming for Genome Assembly & Combinatorial Motif Optimization.
 * <p>
 * Solves NP-hard genomic combinatorial problems for small-scale datasets (K &le; 20 reads):
 * <ol>
 *   <li><b>Shortest Common Superstring (SCS):</b> De novo assembly of fragmented sequencing
 *       reads into the minimal consensus chromosome/contig.</li>
 *   <li><b>Optimal Motif Subset Selection:</b> Maximizing biological regulatory score subject
 *       to non-overlapping genomic interval constraints.</li>
 * </ol>
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>SCS Time Complexity:</b> O(K&sup2; &times; 2<sup>K</sup> + K&sup2; &times; L) where K = number of reads, L = max read length</li>
 *   <li><b>Space Complexity:</b> O(K &times; 2<sup>K</sup>) for memoization table</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class GenomicBitmaskDP {

    /**
     * Result of Shortest Common Superstring Assembly.
     */
    public static final class AssemblyResult {
        private final String assembledSequence;
        private final List<String> readOrder;
        private final int totalLength;
        private final int overlapSaved;

        public AssemblyResult(String assembledSequence, List<String> readOrder, int totalLength, int overlapSaved) {
            this.assembledSequence = assembledSequence;
            this.readOrder = readOrder;
            this.totalLength = totalLength;
            this.overlapSaved = overlapSaved;
        }

        public String getAssembledSequence() {
            return assembledSequence;
        }

        public List<String> getReadOrder() {
            return readOrder;
        }

        public int getTotalLength() {
            return totalLength;
        }

        public int getOverlapSaved() {
            return overlapSaved;
        }

        @Override
        public String toString() {
            return String.format("AssemblyResult [Length=%d bp, Reads=%d, OverlapSaved=%d bp]\nConsensus: %s",
                    totalLength, readOrder.size(), overlapSaved, assembledSequence);
        }
    }

    /**
     * Assembles short sequencing reads into the Shortest Common Superstring (SCS) via Bitmask DP.
     *
     * @param reads List of DNA sequencing reads (K &le; 20)
     * @return AssemblyResult containing reconstructed consensus genome
     */
    public static AssemblyResult assembleShortestCommonSuperstring(List<String> reads) {
        if (reads == null || reads.isEmpty()) {
            return new AssemblyResult("", Collections.emptyList(), 0, 0);
        }

        // 1. Remove duplicate or completely contained reads
        List<String> cleanReads = filterContainedReads(reads);
        int k = cleanReads.size();

        if (k == 1) {
            return new AssemblyResult(cleanReads.get(0), List.of(cleanReads.get(0)), cleanReads.get(0).length(), 0);
        }

        if (k > 20) {
            throw new IllegalArgumentException("Bitmask DP is restricted to K <= 20 reads (received " + k + ")");
        }

        // 2. Precompute overlap matrix: overlap[i][j] = length of max suffix of reads[i] that is prefix of reads[j]
        int[][] overlap = new int[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    overlap[i][j] = computeMaxOverlap(cleanReads.get(i), cleanReads.get(j));
                }
            }
        }

        // 3. Bitmask DP table: dp[mask][i] = max total overlap achieved covering subset 'mask' ending at read 'i'
        int numStates = 1 << k;
        int[][] dp = new int[numStates][k];
        int[][] parent = new int[numStates][k];

        for (int[] row : dp) Arrays.fill(row, -1);
        for (int[] row : parent) Arrays.fill(row, -1);

        // Base cases: single read visited
        for (int i = 0; i < k; i++) {
            dp[1 << i][i] = 0;
        }

        // DP Transitions
        for (int mask = 1; mask < numStates; mask++) {
            for (int last = 0; last < k; last++) {
                if ((mask & (1 << last)) == 0 || dp[mask][last] == -1) continue;

                for (int next = 0; next < k; next++) {
                    if ((mask & (1 << next)) == 0) {
                        int nextMask = mask | (1 << next);
                        int newOverlap = dp[mask][last] + overlap[last][next];

                        if (newOverlap > dp[nextMask][next]) {
                            dp[nextMask][next] = newOverlap;
                            parent[nextMask][next] = last;
                        }
                    }
                }
            }
        }

        // Find optimal ending read for full mask (all reads included)
        int fullMask = numStates - 1;
        int maxOverlap = -1;
        int bestLast = 0;

        for (int i = 0; i < k; i++) {
            if (dp[fullMask][i] > maxOverlap) {
                maxOverlap = dp[fullMask][i];
                bestLast = i;
            }
        }

        // Traceback read order
        List<Integer> orderIndices = new ArrayList<>();
        int currMask = fullMask;
        int currLast = bestLast;

        while (currLast != -1) {
            orderIndices.add(currLast);
            int prevLast = parent[currMask][currLast];
            currMask ^= (1 << currLast);
            currLast = prevLast;
        }

        Collections.reverse(orderIndices);

        // Reconstruct superstring from ordered reads
        StringBuilder consensus = new StringBuilder();
        List<String> orderedReads = new ArrayList<>();

        int firstIdx = orderIndices.get(0);
        consensus.append(cleanReads.get(firstIdx));
        orderedReads.add(cleanReads.get(firstIdx));

        for (int step = 1; step < orderIndices.size(); step++) {
            int prev = orderIndices.get(step - 1);
            int curr = orderIndices.get(step);
            String nextRead = cleanReads.get(curr);
            orderedReads.add(nextRead);

            int shared = overlap[prev][curr];
            consensus.append(nextRead.substring(shared));
        }

        int totalIndividualLen = 0;
        for (String r : cleanReads) totalIndividualLen += r.length();
        int saved = totalIndividualLen - consensus.length();

        return new AssemblyResult(consensus.toString(), orderedReads, consensus.length(), saved);
    }

    private static int computeMaxOverlap(String a, String b) {
        int maxLen = Math.min(a.length(), b.length());
        for (int len = maxLen; len >= 1; len--) {
            if (a.endsWith(b.substring(0, len))) {
                return len;
            }
        }
        return 0;
    }

    private static List<String> filterContainedReads(List<String> reads) {
        List<String> filtered = new ArrayList<>();
        for (int i = 0; i < reads.size(); i++) {
            String r1 = reads.get(i).trim();
            if (r1.isEmpty()) continue;
            boolean contained = false;
            for (int j = 0; j < reads.size(); j++) {
                if (i != j) {
                    String r2 = reads.get(j).trim();
                    if (r2.contains(r1)) {
                        if (r1.length() < r2.length() || i > j) {
                            contained = true;
                            break;
                        }
                    }
                }
            }
            if (!contained && !filtered.contains(r1)) {
                filtered.add(r1);
            }
        }
        return filtered;
    }
}
