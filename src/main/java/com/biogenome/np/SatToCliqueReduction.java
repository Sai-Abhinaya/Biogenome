package com.biogenome.np;

import java.util.*;

/**
 * 3-SAT to CLIQUE Polynomial-Time Karp Reduction.
 * <p>
 * <b>Reduction Construction:</b><br/>
 * Given a 3-CNF formula with <code>m</code> clauses, construct a graph <code>G = (V, E)</code>:
 * <ul>
 *   <li><b>Vertices V:</b> For each clause C<sub>i</sub>, create 3 vertices representing each literal in C<sub>i</sub>. (|V| = 3m)</li>
 *   <li><b>Edges E:</b> An edge exists between vertex (i, u) and (j, v) iff:
 *       <ol>
 *         <li>They belong to distinct clauses (i &ne; j)</li>
 *         <li>Their corresponding literals are non-contradictory (u &ne; &not;v)</li>
 *       </ol>
 *   </li>
 * </ul>
 * <b>Karp Theorem:</b> The 3-CNF formula is satisfiable &hArr; Graph G contains a CLIQUE of size <code>k = m</code>.
 * </p>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class SatToCliqueReduction {

    public static final class ReductionNode {
        private final int id;
        private final int clauseIndex;
        private final int literalIndex;
        private final SatLiteral literal;
        private final String label;

        public ReductionNode(int id, int clauseIndex, int literalIndex, SatLiteral literal) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literalIndex = literalIndex;
            this.literal = literal;
            this.label = String.format("C%d:%s", clauseIndex + 1, literal);
        }

        public int getId() { return id; }
        public int getClauseIndex() { return clauseIndex; }
        public int getLiteralIndex() { return literalIndex; }
        public SatLiteral getLiteral() { return literal; }
        public String getLabel() { return label; }
    }

    public static final class ReductionResult {
        private final List<ReductionNode> nodes;
        private final List<int[]> edges;
        private final int requiredCliqueSize;
        private final List<ReductionNode> foundClique;
        private final boolean isSatisfied;
        private final Map<String, Boolean> derivedModel;
        private final long executionTimeNs;

        public ReductionResult(List<ReductionNode> nodes, List<int[]> edges, int requiredCliqueSize,
                               List<ReductionNode> foundClique, boolean isSatisfied,
                               Map<String, Boolean> derivedModel, long executionTimeNs) {
            this.nodes = nodes;
            this.edges = edges;
            this.requiredCliqueSize = requiredCliqueSize;
            this.foundClique = foundClique;
            this.isSatisfied = isSatisfied;
            this.derivedModel = derivedModel;
            this.executionTimeNs = executionTimeNs;
        }

        public List<ReductionNode> getNodes() { return nodes; }
        public List<int[]> getEdges() { return edges; }
        public int getRequiredCliqueSize() { return requiredCliqueSize; }
        public List<ReductionNode> getFoundClique() { return foundClique; }
        public boolean isSatisfied() { return isSatisfied; }
        public Map<String, Boolean> getDerivedModel() { return derivedModel; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getReductionComplexity() { return "O(M^2) reduction time, O(M^2) space"; }
    }

    public static ReductionResult reduceAndSolve(List<SatClause> clauses) {
        long start = System.nanoTime();
        int m = clauses.size();
        List<ReductionNode> nodes = new ArrayList<>();
        int nodeId = 0;

        for (int cIdx = 0; cIdx < m; cIdx++) {
            SatClause clause = clauses.get(cIdx);
            for (int lIdx = 0; lIdx < clause.size(); lIdx++) {
                nodes.add(new ReductionNode(nodeId++, cIdx, lIdx, clause.getLiterals().get(lIdx)));
            }
        }

        int totalNodes = nodes.size();
        boolean[][] adj = new boolean[totalNodes][totalNodes];
        List<int[]> edges = new ArrayList<>();

        for (int i = 0; i < totalNodes; i++) {
            for (int j = i + 1; j < totalNodes; j++) {
                ReductionNode u = nodes.get(i);
                ReductionNode v = nodes.get(j);

                // Edge condition: different clauses AND not contradictory
                if (u.getClauseIndex() != v.getClauseIndex() && !u.getLiteral().isOpposite(v.getLiteral())) {
                    adj[i][j] = true;
                    adj[j][i] = true;
                    edges.add(new int[]{i, j});
                }
            }
        }

        // Search for a clique of size k = m
        int k = m;
        List<Integer> bestCliqueIndices = new ArrayList<>();
        findClique(adj, totalNodes, k, new ArrayList<>(), 0, bestCliqueIndices);

        List<ReductionNode> foundClique = new ArrayList<>();
        Map<String, Boolean> derivedModel = new LinkedHashMap<>();
        boolean satisfied = (bestCliqueIndices.size() >= k);

        if (satisfied) {
            for (int idx : bestCliqueIndices) {
                ReductionNode rn = nodes.get(idx);
                foundClique.add(rn);
                derivedModel.put(rn.getLiteral().getVariable(), !rn.getLiteral().isNegated());
            }
        }

        long elapsedNs = System.nanoTime() - start;
        return new ReductionResult(nodes, edges, k, foundClique, satisfied, derivedModel, elapsedNs);
    }

    private static boolean findClique(boolean[][] adj, int n, int targetK, List<Integer> current,
                                      int startIdx, List<Integer> best) {
        if (current.size() == targetK) {
            best.clear();
            best.addAll(current);
            return true;
        }

        if (current.size() > best.size()) {
            best.clear();
            best.addAll(current);
        }

        for (int i = startIdx; i < n; i++) {
            // Check if vertex i is connected to all current clique members
            boolean canAdd = true;
            for (int u : current) {
                if (!adj[u][i]) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                current.add(i);
                if (findClique(adj, n, targetK, current, i + 1, best)) {
                    return true;
                }
                current.remove(current.size() - 1);
            }
        }
        return false;
    }
}
