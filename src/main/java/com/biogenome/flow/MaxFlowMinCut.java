package com.biogenome.flow;

import java.util.*;

/**
 * Max-Flow Min-Cut Theorem and Critical Bottleneck Analyzer.
 * <p>
 * Evaluates the max-flow min-cut duality theorem:
 * <br/>
 * <b>Theorem:</b> The maximum value of an s-t flow is strictly equal to the minimum capacity of an s-t cut.
 * <br/>
 * Identifies the exact critical bottleneck edges limiting genomic research pipeline throughput.
 * </p>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class MaxFlowMinCut {

    public static FlowResult analyzeBottlenecks(FlowNetwork network, int source, int sink) {
        // Dinic provides the most efficient residual graph computation
        return DinicAlgorithm.computeMaxFlow(network, source, sink);
    }
}
