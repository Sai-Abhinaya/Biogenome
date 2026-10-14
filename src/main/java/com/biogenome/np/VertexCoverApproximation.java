package com.biogenome.np;

import java.util.*;

/**
 * 2-Approximation Algorithm for Minimum Vertex Cover.
 * <p>
 * <b>Algorithm (Maximal Matching Heuristic):</b><br/>
 * 1. Initialize cover set <code>C = &empty;</code>.<br/>
 * 2. Select an arbitrary uncovered edge <code>e = (u, v)</code>.<br/>
 * 3. Add both endpoints <code>u</code> and <code>v</code> to <code>C</code>.<br/>
 * 4. Discard all edges incident to either <code>u</code> or <code>v</code>.<br/>
 * 5. Repeat until no uncovered edges remain.<br/>
 * </p>
 * <b>Theoretical Guarantee:</b><br/>
 * Since the selected edges form a matching M, any valid vertex cover must select at least one
 * endpoint from each edge in M: <code>|OPT| &ge; |M|</code>.
 * Because we select both endpoints for each edge in M: <code>|C| = 2|M| &le; 2|OPT|</code>.
 * Thus the approximation ratio is strictly guaranteed: <code>&le; 2.0</code>.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class VertexCoverApproximation {

    public static final class ApproxResult {
        private final int vertexCount;
        private final List<String> vertexNames;
        private final List<int[]> allEdges;
        private final List<int[]> selectedMatchingEdges;
        private final List<Integer> approximateCoverVertices;
        private final int approxCoverSize;
        private final int exactOptSize;
        private final double empiricalRatio;
        private final double theoreticalBound;
        private final long executionTimeNs;

        public ApproxResult(int vertexCount, List<String> vertexNames, List<int[]> allEdges,
                            List<int[]> selectedMatchingEdges, List<Integer> approximateCoverVertices,
                            int exactOptSize, long executionTimeNs) {
            this.vertexCount = vertexCount;
            this.vertexNames = vertexNames;
            this.allEdges = allEdges;
            this.selectedMatchingEdges = selectedMatchingEdges;
            this.approximateCoverVertices = approximateCoverVertices;
            this.approxCoverSize = approximateCoverVertices.size();
            this.exactOptSize = exactOptSize;
            this.empiricalRatio = exactOptSize > 0 ? (double) approxCoverSize / exactOptSize : 1.0;
            this.theoreticalBound = 2.0;
            this.executionTimeNs = executionTimeNs;
        }

        public int getVertexCount() { return vertexCount; }
        public List<String> getVertexNames() { return vertexNames; }
        public List<int[]> getAllEdges() { return allEdges; }
        public List<int[]> getSelectedMatchingEdges() { return selectedMatchingEdges; }
        public List<Integer> getApproximateCoverVertices() { return approximateCoverVertices; }
        public int getApproxCoverSize() { return approxCoverSize; }
        public int getExactOptSize() { return exactOptSize; }
        public double getEmpiricalRatio() { return empiricalRatio; }
        public double getTheoreticalBound() { return theoreticalBound; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "O(V + E) Linear Time 2-Approximation"; }
    }

    public static ApproxResult compute2Approximation(int n, List<String> nodeNames, List<int[]> edges) {
        long start = System.nanoTime();

        Set<Integer> cover = new LinkedHashSet<>();
        List<int[]> matchingEdges = new ArrayList<>();
        boolean[] covered = new boolean[n];

        // Greedy maximal matching: pick edges whose endpoints are both not yet covered
        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            if (!covered[u] && !covered[v]) {
                matchingEdges.add(new int[]{u, v});
                cover.add(u);
                cover.add(v);
                covered[u] = true;
                covered[v] = true;
            }
        }

        // For viva demonstration, compute exact OPT size (if graph is small enough, e.g. <= 25 nodes)
        int exactOpt = 0;
        if (n <= 25) {
            IndependentSetToVertexCover.DualityResult optRes =
                    IndependentSetToVertexCover.computeDuality(n, nodeNames, edges);
            exactOpt = optRes.getVertexCoverSize();
        } else {
            exactOpt = matchingEdges.size(); // Lower bound |M| <= |OPT|
        }

        long elapsedNs = System.nanoTime() - start;
        return new ApproxResult(n, nodeNames, edges, matchingEdges, new ArrayList<>(cover), exactOpt, elapsedNs);
    }
}
