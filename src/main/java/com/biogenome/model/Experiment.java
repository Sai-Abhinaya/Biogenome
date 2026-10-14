package com.biogenome.model;

import java.util.Objects;

/**
 * Domain model representing a genomics research experiment or project.
 * Matches to researchers based on required specialization.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class Experiment {

    private final String id;
    private final String name;
    private final String requiredSpecialization;
    private final String priority; // High, Medium, Low
    private final int resourceDemand;
    private final String status; // Pending, In Progress, Completed

    public Experiment(String id, String name, String requiredSpecialization, String priority, int resourceDemand, String status) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.requiredSpecialization = Objects.requireNonNull(requiredSpecialization, "Specialization must not be null");
        this.priority = priority != null ? priority : "Medium";
        this.resourceDemand = resourceDemand > 0 ? resourceDemand : 1;
        this.status = status != null ? status : "Pending";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRequiredSpecialization() {
        return requiredSpecialization;
    }

    public String getPriority() {
        return priority;
    }

    public int getResourceDemand() {
        return resourceDemand;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Req: %s, Priority: %s)", id, name, requiredSpecialization, priority);
    }
}
