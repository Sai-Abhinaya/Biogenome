package com.biogenome.np;

import java.util.*;

/**
 * CLIQUE to INDEPENDENT SET Reduction.
 * <p>
 * <b>Theorem:</b> A set of vertices <code>S &sube; V</code> forms a CLIQUE in graph <code>G = (V, E)</code>
 * if and only if <code>S</code> forms an INDEPENDENT SET in the complement graph <code>G&#773; = (V, E&#773;)</code>.
 * <br/>
 * In BioGenome, this models finding mutually non-interacting sets of biological reagents,
 * non-interfering PCR primers, or independent mutations.
 * </p>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class CliqueToIndependentSet {

    public static final class IndependentSetResult {
        private final int vertexCount;
        private final List<String> vertexNames;
        private final List<int[]> originalEdges;
        private final List<int[]> complementEdges;
        private final List<Integer> maxCliqueInOriginal;
        private final List<Integer> maxIndependentSetInComplement;
        private final int size;
        private final long executionTimeNs;

        public IndependentSetResult(int vertexCount, List<String> vertexNames,
                                   List<int[]> originalEdges, List<int[]> complementEdges,
                                   List<Integer> maxCliqueInOriginal,
                                   List<Integer> maxIndependentSetInComplement,
                                   int size, long executionTimeNs) {
            this.vertexCount = vertexCount;
            this.vertexNames = vertexNames;
            this.originalEdges = originalEdges;
            this.complementEdges = complementEdges;
            this.maxCliqueInOriginal = maxCliqueInOriginal;
            this.maxIndependentSetInComplement = maxIndependentSetInComplement;
            this.size = size;
            this.executionTimeNs = executionTimeNs;
        }

        public int getVertexCount() { return vertexCount; }
        public List<String> getVertexNames() { return vertexNames; }
        public List<int[]> getOriginalEdges() { return originalEdges; }
        public List<int[]> getComplementEdges() { return complementEdges; }
        public List<Integer> getMaxCliqueInOriginal() { return maxCliqueInOriginal; }
        public List<Integer> getMaxIndependentSetInComplement() { return maxIndependentSetInComplement; }
        public int getSize() { return size; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getReductionComplexity() { return "O(V^2) complement construction time"; }
    }

    public static IndependentSetResult reduceAndSolve(int n, List<String> nodeNames, List<int[]> originalEdges) {
        long start = System.nanoTime();

        boolean[][] adj = new boolean[n][n];
        for (int[] e : originalEdges) {
            if (e[0] >= 0 && e[0] < n && e[1] >= 0 && e[1] < n) {
                adj[e[0]][e[1]] = true;
                adj[e[1]][e[0]] = true;
            }
        }

        // Build complement graph G_bar
        boolean[][] compAdj = new boolean[n][n];
        List<int[]> compEdges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!adj[i][j]) {
                    compAdj[i][j] = true;
                    compAdj[j][i] = true;
                    compEdges.add(new int[]{i, j});
                }
            }
        }

        // Max clique in G
        List<Integer> maxClique = new ArrayList<>();
        findMaxClique(adj, n, new ArrayList<>(), 0, maxClique);

        // By theorem, maxClique in G is the max independent set in G_bar!
        List<Integer> maxIndependentSet = new ArrayList<>(maxClique);

        long elapsedNs = System.nanoTime() - start;
        return new IndependentSetResult(n, nodeNames, originalEdges, compEdges,
                maxClique, maxIndependentSet, maxIndependentSet.size(), elapsedNs);
    }

    private static void findMaxClique(boolean[][] adj, int n, List<Integer> curr, int startIdx, List<Integer> best) {
        if (curr.size() > best.size()) {
            best.clear();
            best.addAll(curr);
        }

        for (int i = startIdx; i < n; i++) {
            boolean canAdd = true;
            for (int u : curr) {
                if (!adj[u][i]) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                curr.add(i);
                findMaxClique(adj, n, curr, i + 1, best);
                curr.remove(curr.size() - 1);
            }
        }
    }
}
