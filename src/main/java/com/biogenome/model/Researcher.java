package com.biogenome.model;

import java.util.Objects;

/**
 * Domain model representing a scientific researcher in the genomics platform.
 * Used for Bipartite Matching with laboratory experiments in Module 4.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class Researcher {

    private final String id;
    private final String name;
    private final String specialization;
    private final String experienceLevel; // Junior, Senior, Principal Investigator
    private final int maxConcurrentProjects;

    public Researcher(String id, String name, String specialization, String experienceLevel, int maxConcurrentProjects) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.specialization = Objects.requireNonNull(specialization, "Specialization must not be null");
        this.experienceLevel = experienceLevel != null ? experienceLevel : "Standard";
        this.maxConcurrentProjects = maxConcurrentProjects > 0 ? maxConcurrentProjects : 1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public int getMaxConcurrentProjects() {
        return maxConcurrentProjects;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", id, name, specialization);
    }
}
