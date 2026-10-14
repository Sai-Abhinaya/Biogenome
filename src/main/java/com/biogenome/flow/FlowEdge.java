package com.biogenome.flow;

/**
 * Directed residual edge for network flow algorithms.
 * Encapsulates forward capacity, current flow, and pointer to residual backward edge.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class FlowEdge {

    private final int from;
    private final int to;
    private final int capacity;
    private int flow;
    private FlowEdge residual;

    public FlowEdge(int from, int to, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Edge capacity cannot be negative: " + capacity);
        }
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public FlowEdge getResidual() {
        return residual;
    }

    public void setResidual(FlowEdge residual) {
        this.residual = residual;
    }

    public int getResidualCapacity() {
        return capacity - flow;
    }

    public void addResidualFlowTo(int delta) {
        this.flow += delta;
        if (residual != null) {
            residual.flow -= delta;
        }
    }

    @Override
    public String toString() {
        return String.format("%d -> %d [%d/%d]", from, to, flow, capacity);
    }
}
