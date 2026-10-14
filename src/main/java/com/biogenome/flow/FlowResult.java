package com.biogenome.flow;

import java.util.*;

/**
 * Encapsulates the execution results of maximum flow and minimum cut algorithms.
 * Includes detailed traces of augmenting paths, bottleneck capacities, and residual cuts.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class FlowResult {

    private final String algorithm;
    private final int maxFlow;
    private final List<AugmentingPathInfo> augmentingPaths;
    private final List<EdgeFlowInfo> edgeFlows;
    private final Set<Integer> sourceCutSet; // Vertices reachable from source (S)
    private final Set<Integer> sinkCutSet;   // Vertices in T = V \ S
    private final List<EdgeFlowInfo> minCutBottleneckEdges;
    private final int minCutCapacity;
    private final long executionTimeNs;
    private final String timeComplexity;
    private final String spaceComplexity;

    public static final class AugmentingPathInfo {
        private final int pathNumber;
        private final List<String> nodeNames;
        private final List<Integer> nodeIndices;
        private final int bottleneckCapacity;

        public AugmentingPathInfo(int pathNumber, List<String> nodeNames, List<Integer> nodeIndices, int bottleneckCapacity) {
            this.pathNumber = pathNumber;
            this.nodeNames = nodeNames;
            this.nodeIndices = nodeIndices;
            this.bottleneckCapacity = bottleneckCapacity;
        }

        public int getPathNumber() { return pathNumber; }
        public List<String> getNodeNames() { return nodeNames; }
        public List<Integer> getNodeIndices() { return nodeIndices; }
        public int getBottleneckCapacity() { return bottleneckCapacity; }
        public String getPathString() { return String.join(" -> ", nodeNames); }
    }

    public static final class EdgeFlowInfo {
        private final int from;
        private final int to;
        private final String fromName;
        private final String toName;
        private final int capacity;
        private final int flow;
        private final boolean isSaturated;

        public EdgeFlowInfo(int from, int to, String fromName, String toName, int capacity, int flow) {
            this.from = from;
            this.to = to;
            this.fromName = fromName;
            this.toName = toName;
            this.capacity = capacity;
            this.flow = flow;
            this.isSaturated = (flow == capacity && capacity > 0);
        }

        public int getFrom() { return from; }
        public int getTo() { return to; }
        public String getFromName() { return fromName; }
        public String getToName() { return toName; }
        public int getCapacity() { return capacity; }
        public int getFlow() { return flow; }
        public boolean isSaturated() { return isSaturated; }
    }

    public FlowResult(String algorithm, int maxFlow, List<AugmentingPathInfo> augmentingPaths,
                      List<EdgeFlowInfo> edgeFlows, Set<Integer> sourceCutSet, Set<Integer> sinkCutSet,
                      List<EdgeFlowInfo> minCutBottleneckEdges, int minCutCapacity,
                      long executionTimeNs, String timeComplexity, String spaceComplexity) {
        this.algorithm = algorithm;
        this.maxFlow = maxFlow;
        this.augmentingPaths = augmentingPaths;
        this.edgeFlows = edgeFlows;
        this.sourceCutSet = sourceCutSet;
        this.sinkCutSet = sinkCutSet;
        this.minCutBottleneckEdges = minCutBottleneckEdges;
        this.minCutCapacity = minCutCapacity;
        this.executionTimeNs = executionTimeNs;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    public String getAlgorithm() { return algorithm; }
    public int getMaxFlow() { return maxFlow; }
    public List<AugmentingPathInfo> getAugmentingPaths() { return augmentingPaths; }
    public List<EdgeFlowInfo> getEdgeFlows() { return edgeFlows; }
    public Set<Integer> getSourceCutSet() { return sourceCutSet; }
    public Set<Integer> getSinkCutSet() { return sinkCutSet; }
    public List<EdgeFlowInfo> getMinCutBottleneckEdges() { return minCutBottleneckEdges; }
    public int getMinCutCapacity() { return minCutCapacity; }
    public long getExecutionTimeNs() { return executionTimeNs; }
    public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
    public String getTimeComplexity() { return timeComplexity; }
    public String getSpaceComplexity() { return spaceComplexity; }
}
