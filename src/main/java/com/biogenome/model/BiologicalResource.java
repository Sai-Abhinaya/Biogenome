package com.biogenome.model;

import java.util.Objects;

/**
 * Domain model representing laboratory equipment, computing clusters, and biological materials.
 * Used for Network Flow capacity constraints in Module 4.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class BiologicalResource {

    private final String id;
    private final String name;
    private final String type; // Sequencer, Cryo-EM, PCR Lab, Supercomputing Node, Cell Culture
    private final int capacity; // Daily throughput / flow capacity units
    private final String location;

    public BiologicalResource(String id, String name, String type, int capacity, String location) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.type = Objects.requireNonNull(type, "Type must not be null");
        this.capacity = capacity > 0 ? capacity : 1;
        this.location = location != null ? location : "Central Core Lab";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, Cap: %d)", id, name, type, capacity);
    }
}
