package com.biogenome.model;

import java.util.Objects;

/**
 * Domain model representing a validated genetic mutation or sequence variant.
 * Supports Single Nucleotide Polymorphisms (SNPs), Insertions, Deletions, Missense, and Frameshifts.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class GeneticVariant {

    private final String id;
    private final String gene;
    private final int position;
    private final String refBase;
    private final String altBase;
    private final String type; // SNP, Insertion, Deletion, Missense, Frameshift
    private final String clinicalSignificance;
    private final String consequence;

    public GeneticVariant(String id, String gene, int position, String refBase, String altBase,
                          String type, String clinicalSignificance, String consequence) {
        this.id = Objects.requireNonNull(id, "Variant ID must not be null");
        this.gene = Objects.requireNonNull(gene, "Gene must not be null");
        this.position = position;
        this.refBase = Objects.requireNonNull(refBase, "Reference base must not be null").toUpperCase();
        this.altBase = Objects.requireNonNull(altBase, "Alternate base must not be null").toUpperCase();
        this.type = Objects.requireNonNull(type, "Type must not be null");
        this.clinicalSignificance = clinicalSignificance != null ? clinicalSignificance : "Uncertain Significance";
        this.consequence = consequence != null ? consequence : "Sequence variation";
    }

    public String getId() {
        return id;
    }

    public String getGene() {
        return gene;
    }

    public int getPosition() {
        return position;
    }

    public String getRefBase() {
        return refBase;
    }

    public String getAltBase() {
        return altBase;
    }

    public String getType() {
        return type;
    }

    public String getClinicalSignificance() {
        return clinicalSignificance;
    }

    public String getConsequence() {
        return consequence;
    }

    public String getChangeNotation() {
        return String.format("%s:c.%d%s>%s", gene, position, refBase, altBase);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Pos: %d, %s>%s, Type: %s)", id, gene, position, refBase, altBase, type);
    }
}
