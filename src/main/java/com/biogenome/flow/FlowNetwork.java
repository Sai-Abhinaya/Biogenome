package com.biogenome.flow;

import java.util.*;

/**
 * Residual Flow Network representation supporting node labels and layout metadata.
 * Serves Ford-Fulkerson, Edmonds-Karp, Dinic, and Min-Cut biological algorithms.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public class FlowNetwork {

    private final int vertexCount;
    private final List<List<FlowEdge>> adj;
    private final List<String> vertexNames;
    private final List<String> vertexTypes; // e.g. "Source", "Lab", "Resource", "Experiment", "Sink"
    private final List<FlowEdge> allEdges;

    public FlowNetwork(int vertexCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive: " + vertexCount);
        }
        this.vertexCount = vertexCount;
        this.adj = new ArrayList<>(vertexCount);
        this.vertexNames = new ArrayList<>(vertexCount);
        this.vertexTypes = new ArrayList<>(vertexCount);
        this.allEdges = new ArrayList<>();

        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
            vertexNames.add("V" + i);
            vertexTypes.add("General");
        }
    }

    public void setVertexInfo(int index, String name, String type) {
        if (index >= 0 && index < vertexCount) {
            vertexNames.set(index, name);
            vertexTypes.set(index, type);
        }
    }

    public void addEdge(int from, int to, int capacity) {
        if (from < 0 || from >= vertexCount || to < 0 || to >= vertexCount) {
            throw new IllegalArgumentException(String.format("Invalid edge indices: %d -> %d (V=%d)", from, to, vertexCount));
        }
        FlowEdge forward = new FlowEdge(from, to, capacity);
        FlowEdge backward = new FlowEdge(to, from, 0); // Residual backward edge with 0 initial capacity
        forward.setResidual(backward);
        backward.setResidual(forward);

        adj.get(from).add(forward);
        adj.get(to).add(backward);
        allEdges.add(forward);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public List<FlowEdge> getAdj(int v) {
        return adj.get(v);
    }

    public String getVertexName(int v) {
        return vertexNames.get(v);
    }

    public String getVertexType(int v) {
        return vertexTypes.get(v);
    }

    public List<String> getVertexNames() {
        return Collections.unmodifiableList(vertexNames);
    }

    public List<String> getVertexTypes() {
        return Collections.unmodifiableList(vertexTypes);
    }

    public List<FlowEdge> getAllEdges() {
        return Collections.unmodifiableList(allEdges);
    }

    public void resetFlows() {
        for (FlowEdge edge : allEdges) {
            edge.setFlow(0);
            if (edge.getResidual() != null) {
                edge.getResidual().setFlow(0);
            }
        }
    }
}
