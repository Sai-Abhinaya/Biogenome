package com.biogenome.service;

import com.biogenome.flow.FlowNetwork;
import com.biogenome.model.*;
import com.biogenome.np.SatClause;
import com.biogenome.np.ThreeSatSolver;
import com.biogenome.util.GenomicDataGenerator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Central In-Memory Repository & Biological Data Service for BioGenome.
 * Manages validated DNA sequences, genetic variants, researchers, experiments,
 * laboratory resources, continuous instrument observations, and analysis session history.
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
@Service
public class BioGenomeDataService {

    private final Map<String, DnaSequence> sequences = new LinkedHashMap<>();
    private final List<GeneticVariant> variants = new ArrayList<>();
    private final List<Researcher> researchers = new ArrayList<>();
    private final List<Experiment> experiments = new ArrayList<>();
    private final List<BiologicalResource> resources = new ArrayList<>();
    private final List<ContinuousObservation> observations = new ArrayList<>();
    private final Deque<AnalysisRecord> analysisHistory = new ConcurrentLinkedDeque<>();

    public BioGenomeDataService() {
        initializeSampleSequences();
        initializeSampleVariants();
        initializeSamplePersonnel();
        initializeSampleResources();
        initializeSampleObservations();
        initializeDefaultHistory();
    }

    private void initializeSampleSequences() {
        sequences.put("ECOLI_ORIC", new DnaSequence(
                "ECOLI_ORIC",
                "E. coli K-12 Origin of Replication (oriC core 245 bp)",
                GenomicDataGenerator.ECOLI_ORIC_DNA
        ));

        sequences.put("HUMAN_HBB_WT", new DnaSequence(
                "HUMAN_HBB_WT",
                "Human Beta-Globin (HBB) Normal Adult Hemoglobin (HbA) Exon 1",
                GenomicDataGenerator.HUMAN_HBB_WILDTYPE
        ));

        sequences.put("HUMAN_HBB_SICKLE", new DnaSequence(
                "HUMAN_HBB_SICKLE",
                "Human Beta-Globin (HBB) Sickle Cell Allele (HbS, Glu6Val mutation)",
                GenomicDataGenerator.HUMAN_HBB_SICKLE_CELL
        ));

        sequences.put("SARS2_RBD_WT", new DnaSequence(
                "SARS2_RBD_WT",
                "SARS-CoV-2 Spike Receptor Binding Domain (RBD) Wildtype",
                GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT
        ));

        sequences.put("SARS2_RBD_VAR", new DnaSequence(
                "SARS2_RBD_VAR",
                "SARS-CoV-2 Spike RBD Lineage Variant (Point mutations & indels)",
                GenomicDataGenerator.SARS_COV2_SPIKE_RBD_VARIANT
        ));

        sequences.put("LAMBDA_REPRESSOR", new DnaSequence(
                "LAMBDA_REPRESSOR",
                "Bacteriophage Lambda cI Repressor Operator OR1 site",
                "TATCACCGCCAGTGGTATTTATGTCAACACCGCCAGAGATAATTTATCACCGCAGATGGTTC"
        ));

        sequences.put("BRCA1_EXON11", new DnaSequence(
                "BRCA1_EXON11",
                "Human BRCA1 Breast Cancer Susceptibility Gene Exon 11 Segment",
                "GTACCTTGATTTCGTATTCTGAGAGGCTGCTGCTTAGCGGTAGCCCCTTGGTTTCCGTGGCA" +
                "ACGGAAAACCCTCTGAAGTGCTACGAGCATCGTAGCTTGATGTCGAACCGTGCCTACGTCAG"
        ));

        sequences.put("HTT_CAG_REPEAT", new DnaSequence(
                "HTT_CAG_REPEAT",
                "Human Huntingtin (HTT) Exon 1 Pathological CAG Trinucleotide Repeat",
                "ATGGCGACCCTGGAAAAGCTGATGAAGGCCTTCGAGTCCCTCAAGTCCTTC" +
                "CAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAG" +
                "CAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAGCAACAG" +
                "CCGCCACCGCCGCCGCCGCCGCCGCCTCCTCAGCTTCCTCAGCCGCCGCCG"
        ));
    }

    private void initializeSampleVariants() {
        variants.add(new GeneticVariant("VAR-001", "HBB", 20, "A", "T", "SNP", "Pathogenic", "Glu6Val (Sickle Cell Anemia)"));
        variants.add(new GeneticVariant("VAR-002", "HBB", 73, "A", "G", "SNP", "Benign", "Synonymous codon variation"));
        variants.add(new GeneticVariant("VAR-003", "BRCA1", 185, "AG", "", "Deletion", "Pathogenic", "185delAG Frameshift termination"));
        variants.add(new GeneticVariant("VAR-004", "BRCA1", 5382, "", "C", "Insertion", "Pathogenic", "5382insC Protein truncation"));
        variants.add(new GeneticVariant("VAR-005", "TP53", 248, "C", "T", "Missense", "Pathogenic", "Arg248Trp DNA-binding domain loss"));
        variants.add(new GeneticVariant("VAR-006", "TP53", 175, "G", "A", "Missense", "Pathogenic", "Arg175His Structural destabilization"));
        variants.add(new GeneticVariant("VAR-007", "CFTR", 508, "CTT", "", "Deletion", "Pathogenic", "DeltaF508 Cystic Fibrosis gating defect"));
        variants.add(new GeneticVariant("VAR-008", "EGFR", 858, "T", "G", "Missense", "Therapeutic", "Leu858Arg Gefitinib/Erlotinib sensitivity"));
        variants.add(new GeneticVariant("VAR-009", "EGFR", 790, "C", "T", "Missense", "Resistance", "Thr790Met 1st-gen TKI drug resistance"));
        variants.add(new GeneticVariant("VAR-010", "KRAS", 12, "G", "T", "Missense", "Pathogenic", "Gly12Val Constitutive GTPase activation"));
        variants.add(new GeneticVariant("VAR-011", "BRAF", 600, "T", "A", "Missense", "Therapeutic", "Val600Glu Melanoma MAPK hyperactivation"));
        variants.add(new GeneticVariant("VAR-012", "APOE", 112, "T", "C", "SNP", "Risk Factor", "Cys112Arg APOE-epsilon4 Alzheimer risk"));
        variants.add(new GeneticVariant("VAR-013", "HTT", 18, "", "CAGCAG", "Insertion", "Pathogenic", "Polyglutamine tract pathogenic expansion"));
        variants.add(new GeneticVariant("VAR-014", "DMD", 45, "A", "G", "Frameshift", "Pathogenic", "Duchenne muscular dystrophy exon skipping"));
    }

    private void initializeSamplePersonnel() {
        researchers.add(new Researcher("RES-1", "Dr. Rosalind Franklin", "Structural Biology", "Principal Investigator", 3));
        researchers.add(new Researcher("RES-2", "Dr. Jennifer Doudna", "CRISPR Engineering", "Principal Investigator", 3));
        researchers.add(new Researcher("RES-3", "Dr. Francis Collins", "Comparative Genomics", "Senior Scientist", 2));
        researchers.add(new Researcher("RES-4", "Dr. Craig Venter", "Synthetic Biology", "Senior Scientist", 2));
        researchers.add(new Researcher("RES-5", "Dr. Svante Pääbo", "Paleogenomics", "Senior Scientist", 2));
        researchers.add(new Researcher("RES-6", "Dr. Feng Zhang", "Molecular Engineering", "Principal Investigator", 3));
        researchers.add(new Researcher("RES-7", "Dr. Emmanuelle Charpentier", "CRISPR Engineering", "Senior Scientist", 2));
        researchers.add(new Researcher("RES-8", "Dr. David Baker", "Structural Biology", "Principal Investigator", 3));

        experiments.add(new Experiment("EXP-1", "CRISPR Oncogene Silencing", "CRISPR Engineering", "High", 45, "In Progress"));
        experiments.add(new Experiment("EXP-2", "Cryo-EM Spike RBD Mapping", "Structural Biology", "High", 30, "Pending"));
        experiments.add(new Experiment("EXP-3", "Synthetic Minimal Genome Assembly", "Synthetic Biology", "Medium", 60, "Pending"));
        experiments.add(new Experiment("EXP-4", "Neanderthal Locus Alignment", "Paleogenomics", "Medium", 25, "In Progress"));
        experiments.add(new Experiment("EXP-5", "Sickle Cell Exon-1 Base Editing", "CRISPR Engineering", "High", 40, "Pending"));
        experiments.add(new Experiment("EXP-6", "De Novo Protein Binder Design", "Structural Biology", "High", 50, "Pending"));
        experiments.add(new Experiment("EXP-7", "Multiplexed Diagnostic RNA Sensor", "Molecular Engineering", "Medium", 35, "Pending"));
        experiments.add(new Experiment("EXP-8", "Pan-Genomic Structural Variation Survey", "Comparative Genomics", "Medium", 40, "In Progress"));
    }

    private void initializeSampleResources() {
        resources.add(new BiologicalResource("RES-EQ-1", "Illumina NovaSeq X Plus", "Sequencer", 250, "Sequencing Core A"));
        resources.add(new BiologicalResource("RES-EQ-2", "Oxford Nanopore PromethION", "Sequencer", 180, "Long-Read Facility"));
        resources.add(new BiologicalResource("RES-EQ-3", "Titan Krios 300kV Cryo-TEM", "Cryo-EM", 90, "Imaging Suite 2"));
        resources.add(new BiologicalResource("RES-EQ-4", "Bio-Rad QX600 ddPCR Suite", "PCR Lab", 140, "Molecular Diagnostics"));
        resources.add(new BiologicalResource("RES-EQ-5", "NVIDIA DGX H100 Bio-Cluster", "Supercomputing Node", 350, "Data Center 1"));
        resources.add(new BiologicalResource("RES-EQ-6", "Automated Bio-Reactor Array", "Cell Culture", 120, "Bioprocess Hall"));
        resources.add(new BiologicalResource("RES-EQ-7", "Orbitrap Tribrid Mass Spec", "Proteomics", 85, "Mass Spec Center"));
        resources.add(new BiologicalResource("RES-EQ-8", "Echo 650 Acoustic Liquid Handler", "Automation", 200, "High-Throughput Core"));
    }

    private void initializeSampleObservations() {
        String[] types = {"Phred Q-Score", "Sequencing Error Rate", "GC Fluctuation", "Flow Pressure", "Base Call Confidence"};
        double[] vals = {38.4, 0.12, 51.3, 14.2, 99.88, 39.1, 0.08, 48.7, 13.9, 99.92,
                         37.8, 0.15, 52.1, 14.0, 99.85, 36.9, 0.22, 53.0, 14.5, 99.78,
                         39.5, 0.06, 50.2, 13.8, 99.94};
        String[] units = {"Q", "%", "%", "PSI", "%"};
        for (int i = 0; i < vals.length; i++) {
            observations.add(new ContinuousObservation(
                    "OBS-" + String.format("%03d", i + 1),
                    types[i % types.length],
                    vals[i],
                    units[i % units.length],
                    String.format("2026-10-07 19:%02d:%02d", 10 + (i / 2), (i * 13) % 60),
                    "Illumina NovaSeq X FlowCell-" + ((i % 2) + 1)
            ));
        }
    }

    private void initializeDefaultHistory() {
        analysisHistory.add(new AnalysisRecord("M1 - String", "KMP Algorithm", "DNA Sequence",
                "E. coli oriC (245 bp), Motif: AGCT", "Found 4 occurrences", 2, "O(N + M)", "O(M)"));
        analysisHistory.add(new AnalysisRecord("M3 - DP", "Needleman-Wunsch", "DNA Sequences",
                "Human HBB WT vs Sickle (60 bp)", "Score: 114, Identity: 98.3%", 4, "O(N * M)", "O(N * M)"));
        analysisHistory.add(new AnalysisRecord("M2 - Suffix", "Suffix Array (SA-IS)", "DNA Sequence",
                "SARS-CoV-2 Spike RBD (80 bp)", "Built 80 suffixes, 2 matches", 3, "O(N)", "O(N)"));
    }

    // ===== Accessors & Queries =====

    public Collection<DnaSequence> getAllSequences() {
        return Collections.unmodifiableCollection(sequences.values());
    }

    public DnaSequence getSequence(String id) {
        return sequences.get(id);
    }

    public void addSequence(DnaSequence seq) {
        sequences.put(seq.getId(), seq);
    }

    public List<GeneticVariant> getAllVariants() {
        return Collections.unmodifiableList(variants);
    }

    public List<Researcher> getAllResearchers() {
        return Collections.unmodifiableList(researchers);
    }

    public List<Experiment> getAllExperiments() {
        return Collections.unmodifiableList(experiments);
    }

    public List<BiologicalResource> getAllResources() {
        return Collections.unmodifiableList(resources);
    }

    public List<ContinuousObservation> getAllObservations() {
        return Collections.unmodifiableList(observations);
    }

    public List<AnalysisRecord> getRecentHistory(int limit) {
        List<AnalysisRecord> list = new ArrayList<>(analysisHistory);
        Collections.reverse(list);
        if (list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    public void recordAnalysis(String module, String algorithm, String inputType,
                               String inputSummary, String resultSummary, long timeMs,
                               String timeComplexity, String spaceComplexity) {
        AnalysisRecord rec = new AnalysisRecord(module, algorithm, inputType, inputSummary,
                resultSummary, timeMs, timeComplexity, spaceComplexity);
        analysisHistory.add(rec);
        // Retain max 100 records
        while (analysisHistory.size() > 100) {
            analysisHistory.poll();
        }
    }

    public void clearHistory() {
        analysisHistory.clear();
    }

    /**
     * Builds pre-configured biological resource network for Module 4 demonstrations.
     */
    public FlowNetwork buildSampleResourceNetwork() {
        // Topology:
        // 0: Source (Supplier Core)
        // 1: Lab North (Microbiology)
        // 2: Lab South (Genomics Core)
        // 3: Lab Central (Structural Core)
        // 4: Res A (NovaSeq Sequencer)
        // 5: Res B (Cryo-TEM Suite)
        // 6: Res C (Compute Cluster)
        // 7: Exp 1 (CRISPR Trial)
        // 8: Exp 2 (Protein Folding)
        // 9: Sink (Completed Analysis)
        FlowNetwork net = new FlowNetwork(10);
        net.setVertexInfo(0, "Central Reagent Supplier", "Source");
        net.setVertexInfo(1, "Microbiology Core Lab", "Lab");
        net.setVertexInfo(2, "Genomics Core Lab", "Lab");
        net.setVertexInfo(3, "Structural Biology Core", "Lab");
        net.setVertexInfo(4, "NovaSeq X Sequencer", "Resource");
        net.setVertexInfo(5, "Titan Cryo-EM Suite", "Resource");
        net.setVertexInfo(6, "DGX Supercompute Node", "Resource");
        net.setVertexInfo(7, "CRISPR Oncogene Trial", "Experiment");
        net.setVertexInfo(8, "Spike RBD Structural Trial", "Experiment");
        net.setVertexInfo(9, "Research Sink (Output)", "Sink");

        // Source to Labs
        net.addEdge(0, 1, 40);
        net.addEdge(0, 2, 50);
        net.addEdge(0, 3, 35);

        // Labs to Resources
        net.addEdge(1, 4, 25);
        net.addEdge(1, 6, 20);
        net.addEdge(2, 4, 30);
        net.addEdge(2, 6, 30);
        net.addEdge(3, 5, 35);
        net.addEdge(3, 6, 15);

        // Resources to Experiments
        net.addEdge(4, 7, 45);
        net.addEdge(5, 8, 30);
        net.addEdge(6, 7, 25);
        net.addEdge(6, 8, 20);

        // Experiments to Sink
        net.addEdge(7, 9, 55);
        net.addEdge(8, 9, 45);

        return net;
    }

    /**
     * Builds default researcher-to-experiment eligibility graph for Bipartite Matching.
     */
    public Map<Integer, List<Integer>> buildSampleEligibility() {
        Map<Integer, List<Integer>> map = new LinkedHashMap<>();
        // Researchers:
        // 0: Dr. Rosalind Franklin (Structural) -> Exp 1 (Cryo-EM), Exp 5 (Protein Design)
        map.put(0, Arrays.asList(1, 5));
        // 1: Dr. Jennifer Doudna (CRISPR) -> Exp 0 (CRISPR Trial), Exp 4 (Sickle Cell Edit)
        map.put(1, Arrays.asList(0, 4));
        // 2: Dr. Francis Collins (Genomics) -> Exp 3 (Neanderthal Locus), Exp 7 (Pan-Genomics)
        map.put(2, Arrays.asList(3, 7));
        // 3: Dr. Craig Venter (Synthetic) -> Exp 2 (Synthetic Minimal Genome), Exp 7 (Pan-Genomics)
        map.put(3, Arrays.asList(2, 7));
        // 4: Dr. Svante Pääbo (Paleogenomics) -> Exp 3 (Neanderthal Locus)
        map.put(4, Collections.singletonList(3));
        // 5: Dr. Feng Zhang (Molecular) -> Exp 0 (CRISPR Trial), Exp 6 (Diagnostic Sensor)
        map.put(5, Arrays.asList(0, 6));
        // 6: Dr. Emmanuelle Charpentier (CRISPR) -> Exp 0 (CRISPR Trial), Exp 4 (Sickle Cell Edit)
        map.put(6, Arrays.asList(0, 4));
        // 7: Dr. David Baker (Structural) -> Exp 1 (Cryo-EM), Exp 5 (Protein Design)
        map.put(7, Arrays.asList(1, 5));
        return map;
    }

    /**
     * Sample 3-SAT formula modeling gene regulatory switch constraints.
     */
    public List<SatClause> getSample3SatFormula() {
        String cnf = "(TP53 | MDM2 | ~CDKN1A) & (~TP53 | BAX | BCL2) & (~MDM2 | ~BAX | PTEN) & (CDKN1A | ~BCL2 | EGFR)";
        return ThreeSatSolver.parseCnf(cnf);
    }
}
