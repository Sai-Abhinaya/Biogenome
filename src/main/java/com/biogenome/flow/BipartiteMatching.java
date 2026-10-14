package com.biogenome.flow;

import java.util.*;

/**
 * Maximum Bipartite Matching & König's Theorem Implementation.
 * <p>
 * Models the optimal assignment of qualified genomic Researchers to specialized
 * Experiments. Employs reduction to maximum flow and applies König's theorem to
 * find the Minimum Vertex Cover:
 * <br/>
 * <b>Theorem:</b> In any bipartite graph, <code>|Maximum Matching| = |Minimum Vertex Cover|</code>.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(E &times; &radic;V) via Dinic unit-network flow</li>
 *   <li><b>Space Complexity:</b> O(V + E)</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class BipartiteMatching {

    public static final class MatchAssignment {
        private final String researcherId;
        private final String researcherName;
        private final String experimentId;
        private final String experimentName;

        public MatchAssignment(String researcherId, String researcherName, String experimentId, String experimentName) {
            this.researcherId = researcherId;
            this.researcherName = researcherName;
            this.experimentId = experimentId;
            this.experimentName = experimentName;
        }

        public String getResearcherId() { return researcherId; }
        public String getResearcherName() { return researcherName; }
        public String getExperimentId() { return experimentId; }
        public String getExperimentName() { return experimentName; }

        @Override
        public String toString() {
            return researcherName + " <=> " + experimentName;
        }
    }

    public static final class MatchingResult {
        private final int matchingSize;
        private final List<MatchAssignment> assignments;
        private final List<String> unmatchedResearchers;
        private final List<String> unmatchedExperiments;
        private final List<String> minVertexCoverNodes;
        private final int minVertexCoverSize;
        private final boolean konigTheoremSatisfied;
        private final List<Map<String, String>> eligibilityGraph;
        private final long executionTimeNs;

        public MatchingResult(int matchingSize, List<MatchAssignment> assignments,
                              List<String> unmatchedResearchers, List<String> unmatchedExperiments,
                              List<String> minVertexCoverNodes, int minVertexCoverSize,
                              boolean konigTheoremSatisfied, List<Map<String, String>> eligibilityGraph,
                              long executionTimeNs) {
            this.matchingSize = matchingSize;
            this.assignments = assignments;
            this.unmatchedResearchers = unmatchedResearchers;
            this.unmatchedExperiments = unmatchedExperiments;
            this.minVertexCoverNodes = minVertexCoverNodes;
            this.minVertexCoverSize = minVertexCoverSize;
            this.konigTheoremSatisfied = konigTheoremSatisfied;
            this.eligibilityGraph = eligibilityGraph;
            this.executionTimeNs = executionTimeNs;
        }

        public int getMatchingSize() { return matchingSize; }
        public List<MatchAssignment> getAssignments() { return assignments; }
        public List<String> getUnmatchedResearchers() { return unmatchedResearchers; }
        public List<String> getUnmatchedExperiments() { return unmatchedExperiments; }
        public List<String> getMinVertexCoverNodes() { return minVertexCoverNodes; }
        public int getMinVertexCoverSize() { return minVertexCoverSize; }
        public boolean isKonigTheoremSatisfied() { return konigTheoremSatisfied; }
        public List<Map<String, String>> getEligibilityGraph() { return eligibilityGraph; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "O(E * sqrt(V)) Dinic / Hopcroft-Karp"; }
    }

    /**
     * Solves maximum bipartite matching between researchers and experiments.
     *
     * @param researchers List of researcher names/IDs
     * @param experiments List of experiment names/IDs
     * @param eligibility Adjacency map: researcher index -> list of eligible experiment indices
     * @return MatchingResult containing matches, König's vertex cover, and theorem verification
     */
    public static MatchingResult computeMatching(List<String> researchers, List<String> experiments,
                                                 Map<Integer, List<Integer>> eligibility) {
        long start = System.nanoTime();

        int numR = researchers.size();
        int numE = experiments.size();
        int totalVertices = 1 + numR + numE + 1; // S, Researchers, Experiments, T
        int source = 0;
        int sink = totalVertices - 1;

        FlowNetwork net = new FlowNetwork(totalVertices);
        net.setVertexInfo(source, "Source (Pool)", "Source");
        net.setVertexInfo(sink, "Sink (Lab Ops)", "Sink");

        for (int i = 0; i < numR; i++) {
            net.setVertexInfo(1 + i, researchers.get(i), "Researcher");
            net.addEdge(source, 1 + i, 1);
        }

        for (int j = 0; j < numE; j++) {
            net.setVertexInfo(1 + numR + j, experiments.get(j), "Experiment");
            net.addEdge(1 + numR + j, sink, 1);
        }

        List<Map<String, String>> eligibilityList = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : eligibility.entrySet()) {
            int rIdx = entry.getKey();
            for (int eIdx : entry.getValue()) {
                if (rIdx >= 0 && rIdx < numR && eIdx >= 0 && eIdx < numE) {
                    net.addEdge(1 + rIdx, 1 + numR + eIdx, 1);
                    Map<String, String> edgeMap = new LinkedHashMap<>();
                    edgeMap.put("researcher", researchers.get(rIdx));
                    edgeMap.put("experiment", experiments.get(eIdx));
                    eligibilityList.add(edgeMap);
                }
            }
        }

        // Run Dinic on the unit network
        FlowResult flowRes = DinicAlgorithm.computeMaxFlow(net, source, sink);
        int maxMatching = flowRes.getMaxFlow();

        // Extract assignments
        List<MatchAssignment> assignments = new ArrayList<>();
        Set<Integer> matchedResearchers = new HashSet<>();
        Set<Integer> matchedExperiments = new HashSet<>();

        for (FlowEdge edge : net.getAllEdges()) {
            int u = edge.getFrom();
            int v = edge.getTo();
            if (u >= 1 && u <= numR && v >= 1 + numR && v < sink && edge.getFlow() == 1) {
                int rIdx = u - 1;
                int eIdx = v - 1 - numR;
                assignments.add(new MatchAssignment(
                        "RES-" + (rIdx + 1), researchers.get(rIdx),
                        "EXP-" + (eIdx + 1), experiments.get(eIdx)
                ));
                matchedResearchers.add(rIdx);
                matchedExperiments.add(eIdx);
            }
        }

        List<String> unmatchedResearchers = new ArrayList<>();
        for (int i = 0; i < numR; i++) {
            if (!matchedResearchers.contains(i)) {
                unmatchedResearchers.add(researchers.get(i));
            }
        }

        List<String> unmatchedExperiments = new ArrayList<>();
        for (int j = 0; j < numE; j++) {
            if (!matchedExperiments.contains(j)) {
                unmatchedExperiments.add(experiments.get(j));
            }
        }

        // König's Theorem: Minimum Vertex Cover from residual graph
        // In the residual graph, find all vertices reachable from unmatched vertices in L (Researchers)
        Set<Integer> reachableFromUnmatchedL = new HashSet<>();
        Queue<Integer> q = new ArrayDeque<>();
        boolean[] vis = new boolean[totalVertices];

        for (int i = 0; i < numR; i++) {
            if (!matchedResearchers.contains(i)) {
                int v = 1 + i;
                q.add(v);
                vis[v] = true;
                reachableFromUnmatchedL.add(v);
            }
        }

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowEdge e : net.getAdj(u)) {
                if (e.getResidualCapacity() > 0 && !vis[e.getTo()]) {
                    // Do not traverse to Source or Sink for bipartite König set
                    if (e.getTo() != source && e.getTo() != sink) {
                        vis[e.getTo()] = true;
                        reachableFromUnmatchedL.add(e.getTo());
                        q.add(e.getTo());
                    }
                }
            }
        }

        // König's Vertex Cover:
        // (L \ Reachable) U (R ∩ Reachable)
        List<String> minVertexCover = new ArrayList<>();
        for (int i = 0; i < numR; i++) {
            int node = 1 + i;
            if (!reachableFromUnmatchedL.contains(node)) {
                minVertexCover.add(researchers.get(i) + " [Researcher]");
            }
        }
        for (int j = 0; j < numE; j++) {
            int node = 1 + numR + j;
            if (reachableFromUnmatchedL.contains(node)) {
                minVertexCover.add(experiments.get(j) + " [Experiment]");
            }
        }

        int minVertexCoverSize = minVertexCover.size();
        boolean konigSatisfied = (minVertexCoverSize == maxMatching);

        long elapsedNs = System.nanoTime() - start;
        return new MatchingResult(maxMatching, assignments, unmatchedResearchers, unmatchedExperiments,
                minVertexCover, minVertexCoverSize, konigSatisfied, eligibilityList, elapsedNs);
    }
}
