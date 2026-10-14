package com.biogenome.flow;

import java.util.*;

/**
 * Ford-Fulkerson Maximum Flow Algorithm.
 * <p>
 * Finds augmenting paths in the residual graph using Depth-First Search (DFS).
 * Applied in BioGenome for modeling laboratory resource distribution from primary reagent
 * suppliers through intermediate testing laboratories to final analytical experiments.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(E &times; |f*|) where |f*| is the maximum flow value</li>
 *   <li><b>Space Complexity:</b> O(V + E) for recursive DFS call stack and visited tracking</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class FordFulkerson {

    public static FlowResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        long startTime = System.nanoTime();
        network.resetFlows();

        List<FlowResult.AugmentingPathInfo> pathList = new ArrayList<>();
        int maxFlow = 0;
        int pathCount = 0;

        while (true) {
            boolean[] visited = new boolean[network.getVertexCount()];
            List<FlowEdge> path = new ArrayList<>();
            int bottleneck = dfs(network, source, sink, Integer.MAX_VALUE, visited, path);

            if (bottleneck <= 0) {
                break; // No more augmenting paths
            }

            maxFlow += bottleneck;
            pathCount++;

            // Extract node names and indices for this path
            List<String> nodeNames = new ArrayList<>();
            List<Integer> nodeIndices = new ArrayList<>();
            nodeNames.add(network.getVertexName(source));
            nodeIndices.add(source);

            for (FlowEdge edge : path) {
                edge.addResidualFlowTo(bottleneck);
                nodeNames.add(network.getVertexName(edge.getTo()));
                nodeIndices.add(edge.getTo());
            }

            pathList.add(new FlowResult.AugmentingPathInfo(pathCount, nodeNames, nodeIndices, bottleneck));
        }

        long elapsedNs = System.nanoTime() - startTime;
        return buildResult(network, source, sink, "Ford-Fulkerson (DFS)", maxFlow, pathList, elapsedNs,
                "O(E * |f*|)", "O(V + E)");
    }

    private static int dfs(FlowNetwork network, int current, int sink, int flow,
                           boolean[] visited, List<FlowEdge> path) {
        if (current == sink) {
            return flow;
        }
        visited[current] = true;

        for (FlowEdge edge : network.getAdj(current)) {
            if (!visited[edge.getTo()] && edge.getResidualCapacity() > 0) {
                path.add(edge);
                int pushed = dfs(network, edge.getTo(), sink, Math.min(flow, edge.getResidualCapacity()), visited, path);
                if (pushed > 0) {
                    return pushed;
                }
                path.remove(path.size() - 1);
            }
        }
        return 0;
    }

    static FlowResult buildResult(FlowNetwork network, int source, int sink, String algorithm,
                                  int maxFlow, List<FlowResult.AugmentingPathInfo> pathList,
                                  long elapsedNs, String timeComplexity, String spaceComplexity) {
        // Collect edge flow info
        List<FlowResult.EdgeFlowInfo> edgeFlows = new ArrayList<>();
        for (FlowEdge edge : network.getAllEdges()) {
            edgeFlows.add(new FlowResult.EdgeFlowInfo(
                    edge.getFrom(), edge.getTo(),
                    network.getVertexName(edge.getFrom()), network.getVertexName(edge.getTo()),
                    edge.getCapacity(), edge.getFlow()
            ));
        }

        // Min-cut: BFS from source in residual graph to find reachable vertices S
        Set<Integer> sourceCutSet = new HashSet<>();
        Queue<Integer> q = new ArrayDeque<>();
        boolean[] visited = new boolean[network.getVertexCount()];

        q.add(source);
        visited[source] = true;
        sourceCutSet.add(source);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowEdge edge : network.getAdj(u)) {
                if (edge.getResidualCapacity() > 0 && !visited[edge.getTo()]) {
                    visited[edge.getTo()] = true;
                    sourceCutSet.add(edge.getTo());
                    q.add(edge.getTo());
                }
            }
        }

        Set<Integer> sinkCutSet = new HashSet<>();
        for (int i = 0; i < network.getVertexCount(); i++) {
            if (!sourceCutSet.contains(i)) {
                sinkCutSet.add(i);
            }
        }

        // Bottleneck cut edges: forward edges from S to T
        List<FlowResult.EdgeFlowInfo> minCutBottlenecks = new ArrayList<>();
        int minCutCapacity = 0;
        for (FlowEdge edge : network.getAllEdges()) {
            if (sourceCutSet.contains(edge.getFrom()) && sinkCutSet.contains(edge.getTo())) {
                minCutBottlenecks.add(new FlowResult.EdgeFlowInfo(
                        edge.getFrom(), edge.getTo(),
                        network.getVertexName(edge.getFrom()), network.getVertexName(edge.getTo()),
                        edge.getCapacity(), edge.getFlow()
                ));
                minCutCapacity += edge.getCapacity();
            }
        }

        return new FlowResult(algorithm, maxFlow, pathList, edgeFlows, sourceCutSet, sinkCutSet,
                minCutBottlenecks, minCutCapacity, elapsedNs, timeComplexity, spaceComplexity);
    }
}
