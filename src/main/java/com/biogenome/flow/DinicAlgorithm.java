package com.biogenome.flow;

import java.util.*;

/**
 * Dinic's Algorithm for Maximum Network Flow.
 * <p>
 * Employs BFS to construct a Level Graph and DFS with pointer pruning to find
 * a Blocking Flow in each phase. Achieves O(V&sup2; &times; E) worst-case time,
 * and O(E &radic;V) on unit networks (such as bipartite matching).
 * Highly effective for large-scale multi-tier biological resource allocation.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>General Networks:</b> O(V&sup2; &times; E)</li>
 *   <li><b>Unit Networks:</b> O(E &times; &radic;V)</li>
 *   <li><b>Space Complexity:</b> O(V + E) for level array and edge pointers</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class DinicAlgorithm {

    public static FlowResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        long startTime = System.nanoTime();
        network.resetFlows();

        int n = network.getVertexCount();
        int[] level = new int[n];
        int[] ptr = new int[n];
        int maxFlow = 0;
        int pathCount = 0;
        List<FlowResult.AugmentingPathInfo> pathList = new ArrayList<>();

        while (bfsLevelGraph(network, source, sink, level)) {
            Arrays.fill(ptr, 0);

            while (true) {
                List<FlowEdge> path = new ArrayList<>();
                int pushed = dfsBlockingFlow(network, source, sink, Integer.MAX_VALUE, level, ptr, path);
                if (pushed <= 0) {
                    break;
                }
                maxFlow += pushed;
                pathCount++;

                List<String> nodeNames = new ArrayList<>();
                List<Integer> nodeIndices = new ArrayList<>();
                nodeNames.add(network.getVertexName(source));
                nodeIndices.add(source);

                for (FlowEdge edge : path) {
                    nodeNames.add(network.getVertexName(edge.getTo()));
                    nodeIndices.add(edge.getTo());
                }

                pathList.add(new FlowResult.AugmentingPathInfo(pathCount, nodeNames, nodeIndices, pushed));
            }
        }

        long elapsedNs = System.nanoTime() - startTime;
        return FordFulkerson.buildResult(network, source, sink, "Dinic's Algorithm (Level Graph + Blocking Flow)",
                maxFlow, pathList, elapsedNs, "O(V^2 * E)", "O(V + E)");
    }

    private static boolean bfsLevelGraph(FlowNetwork network, int source, int sink, int[] level) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowEdge edge : network.getAdj(u)) {
                if (edge.getResidualCapacity() > 0 && level[edge.getTo()] == -1) {
                    level[edge.getTo()] = level[u] + 1;
                    q.add(edge.getTo());
                }
            }
        }
        return level[sink] != -1;
    }

    private static int dfsBlockingFlow(FlowNetwork network, int u, int sink, int flow,
                                       int[] level, int[] ptr, List<FlowEdge> path) {
        if (u == sink || flow == 0) {
            return flow;
        }

        List<FlowEdge> adj = network.getAdj(u);
        for (; ptr[u] < adj.size(); ptr[u]++) {
            FlowEdge edge = adj.get(ptr[u]);
            if (level[edge.getTo()] == level[u] + 1 && edge.getResidualCapacity() > 0) {
                path.add(edge);
                int pushed = dfsBlockingFlow(network, edge.getTo(), sink, Math.min(flow, edge.getResidualCapacity()),
                        level, ptr, path);
                if (pushed > 0) {
                    edge.addResidualFlowTo(pushed);
                    return pushed;
                }
                path.remove(path.size() - 1);
            }
        }
        return 0;
    }
}
