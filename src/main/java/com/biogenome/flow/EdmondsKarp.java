package com.biogenome.flow;

import java.util.*;

/**
 * Edmonds-Karp Maximum Flow Algorithm.
 * <p>
 * Computes maximum flow using Breadth-First Search (BFS) to identify shortest augmenting
 * paths (in terms of number of edges). Solves biological sample distribution bottlenecks
 * with guaranteed polynomial runtime O(V &times; E&sup2;).
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(V &times; E&sup2;)</li>
 *   <li><b>Space Complexity:</b> O(V + E) for BFS queue and parent pointers</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class EdmondsKarp {

    public static FlowResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        long startTime = System.nanoTime();
        network.resetFlows();

        int n = network.getVertexCount();
        List<FlowResult.AugmentingPathInfo> pathList = new ArrayList<>();
        int maxFlow = 0;
        int pathCount = 0;

        while (true) {
            FlowEdge[] parentEdge = new FlowEdge[n];
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(source);

            while (!queue.isEmpty() && parentEdge[sink] == null) {
                int curr = queue.poll();
                for (FlowEdge edge : network.getAdj(curr)) {
                    if (edge.getTo() != source && parentEdge[edge.getTo()] == null && edge.getResidualCapacity() > 0) {
                        parentEdge[edge.getTo()] = edge;
                        queue.add(edge.getTo());
                    }
                }
            }

            if (parentEdge[sink] == null) {
                break; // No augmenting path found by BFS
            }

            // Find bottleneck capacity along the BFS path
            int bottleneck = Integer.MAX_VALUE;
            for (FlowEdge e = parentEdge[sink]; e != null; e = parentEdge[e.getFrom()]) {
                bottleneck = Math.min(bottleneck, e.getResidualCapacity());
            }

            // Augment flow along the path and collect node path info
            List<FlowEdge> pathEdges = new ArrayList<>();
            for (FlowEdge e = parentEdge[sink]; e != null; e = parentEdge[e.getFrom()]) {
                e.addResidualFlowTo(bottleneck);
                pathEdges.add(e);
            }
            Collections.reverse(pathEdges);

            maxFlow += bottleneck;
            pathCount++;

            List<String> nodeNames = new ArrayList<>();
            List<Integer> nodeIndices = new ArrayList<>();
            nodeNames.add(network.getVertexName(source));
            nodeIndices.add(source);

            for (FlowEdge edge : pathEdges) {
                nodeNames.add(network.getVertexName(edge.getTo()));
                nodeIndices.add(edge.getTo());
            }

            pathList.add(new FlowResult.AugmentingPathInfo(pathCount, nodeNames, nodeIndices, bottleneck));
        }

        long elapsedNs = System.nanoTime() - startTime;
        return FordFulkerson.buildResult(network, source, sink, "Edmonds-Karp (BFS Shortest Path)", maxFlow,
                pathList, elapsedNs, "O(V * E^2)", "O(V + E)");
    }
}
