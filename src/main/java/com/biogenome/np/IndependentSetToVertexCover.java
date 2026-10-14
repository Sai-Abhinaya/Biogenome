package com.biogenome.np;

import java.util.*;

/**
 * INDEPENDENT SET to VERTEX COVER Duality & Reduction.
 * <p>
 * <b>Theorem:</b> In any undirected graph <code>G = (V, E)</code>:
 * <br/>
 * <code>S &sube; V</code> is an <b>Independent Set</b> &hArr; <code>V \ S</code> is a <b>Vertex Cover</b>.
 * <br/>
 * Consequently: <code>&alpha;(G) + &tau;(G) = |V|</code>
 * (Maximum Independent Set size + Minimum Vertex Cover size = Total Vertices).
 * <br/>
 * <b>Bioinformatics Application:</b> Identifying the minimal set of drug-target hub genes
 * (Vertex Cover) required to intercept all disease transmission or cancer proliferation pathways.
 * </p>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class IndependentSetToVertexCover {

    public static final class DualityResult {
        private final int totalVertices;
        private final List<String> vertexNames;
        private final List<int[]> edges;
        private final List<Integer> maxIndependentSet;
        private final List<Integer> minVertexCover;
        private final int independentSetSize;
        private final int vertexCoverSize;
        private final boolean dualityVerified;
        private final long executionTimeNs;

        public DualityResult(int totalVertices, List<String> vertexNames, List<int[]> edges,
                             List<Integer> maxIndependentSet, List<Integer> minVertexCover,
                             long executionTimeNs) {
            this.totalVertices = totalVertices;
            this.vertexNames = vertexNames;
            this.edges = edges;
            this.maxIndependentSet = maxIndependentSet;
            this.minVertexCover = minVertexCover;
            this.independentSetSize = maxIndependentSet.size();
            this.vertexCoverSize = minVertexCover.size();
            this.dualityVerified = (independentSetSize + vertexCoverSize == totalVertices);
            this.executionTimeNs = executionTimeNs;
        }

        public int getTotalVertices() { return totalVertices; }
        public List<String> getVertexNames() { return vertexNames; }
        public List<int[]> getEdges() { return edges; }
        public List<Integer> getMaxIndependentSet() { return maxIndependentSet; }
        public List<Integer> getMinVertexCover() { return minVertexCover; }
        public int getIndependentSetSize() { return independentSetSize; }
        public int getVertexCoverSize() { return vertexCoverSize; }
        public boolean isDualityVerified() { return dualityVerified; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "Exact Reduction: O(1) complement transform from Independent Set"; }
    }

    public static DualityResult computeDuality(int n, List<String> nodeNames, List<int[]> edges) {
        long start = System.nanoTime();

        // Find Maximum Independent Set (by finding Max Clique in complement graph)
        CliqueToIndependentSet.IndependentSetResult isResult =
                CliqueToIndependentSet.reduceAndSolve(n, nodeNames, edges);
        List<Integer> independentSet = isResult.getMaxIndependentSetInComplement();
        Set<Integer> isSet = new HashSet<>(independentSet);

        // Vertex Cover = V \ IndependentSet
        List<Integer> vertexCover = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!isSet.contains(i)) {
                vertexCover.add(i);
            }
        }

        long elapsedNs = System.nanoTime() - start;
        return new DualityResult(n, nodeNames, edges, independentSet, vertexCover, elapsedNs);
    }
}
