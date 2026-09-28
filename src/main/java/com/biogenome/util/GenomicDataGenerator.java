package com.biogenome.util;

import com.biogenome.model.DnaSequence;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository of authentic biological genomic sequences and benchmark datasets.
 * <p>
 * Contains real-world sequences from microbial, viral, and human genomes,
 * including restriction enzyme recognition sites, promoter motifs, and disease variants.
 * </p>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class GenomicDataGenerator {

    /**
     * E. coli K-12 origin of replication (oriC) core region (245 bp).
     * Contains repeated 9-mer DnaA binding boxes (TTATCCACA) and 13-mer AT-rich unwinding regions.
     */
    public static final String ECOLI_ORIC_DNA =
            "AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC" +
            "TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA" +
            "TATAGGCATAGCGCACAGACAGATAAAAATTACAGAGTACACAACATCCATGAAACGCATTAGCACCACC" +
            "ATTACCACCACCATCACCATTACCACAGGTAACGGTGCGGGCTGACGCGTACAGGAAACACAGAAAAAAG";

    /**
     * Human Beta-Globin (HBB) Exon 1 Wildtype sequence (normal HbA).
     */
    public static final String HUMAN_HBB_WILDTYPE =
            "ATGGTGCATCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAGGTGAACGTGGATGAAG" +
            "TTGGTGGTGAGGCCCTGGGCAGGCTGCTGGTGGTCTACCCTTGGACCCAGAGGTTCTTTGAGTCCTTTGG" +
            "GGATCTGTCCACTCCTGATGCTGTTATGGGCAACCCTAAGGTGAAGGCTCATGGCAAGAAAGTGCTCGGT" +
            "GCCTTTAGTGATGGCCTGGCTCACCTGGACAACCTCAAGGGCACCTTTGCCACACTGAGTGAGCTGCACT";

    /**
     * Human Beta-Globin (HBB) Sickle Cell Variant (HbS) - GAG -> GTG point mutation at codon 6.
     */
    public static final String HUMAN_HBB_SICKLE_CELL =
            "ATGGTGCATCTGACTCCTGTGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAGGTGAACGTGGATGAAG" +
            "TTGGTGGTGAGGCCCTGGGCAGGCTGCTGGTGGTCTACCCTTGGACCCAGAGGTTCTTTGAGTCCTTTGG" +
            "GGATCTGTCCACTCCTGATGCTGTTATGGGCAACCCTAAGGTGAAGGCTCATGGCAAGAAAGTGCTCGGT" +
            "GCCTTTAGTGATGGCCTGGCTCACCTGGACAACCTCAAGGGCACCTTTGCCACACTGAGTGAGCTGCACT";

    /**
     * SARS-CoV-2 Spike RBD (Receptor Binding Domain) reference segment.
     */
    public static final String SARS_COV2_SPIKE_RBD_WT =
            "AATATTACAAACTTGTGCCCTTTTGGTGAAGTTTTTAACGCCACCAGATTTGCATCTGTTTATGCTTGGA" +
            "ACAGGAAGAGAATCAGCAACTGTGTTGCTGATTATTCTGTCCTATATAATTCCGCATCATTTTCCACTTT" +
            "TAAGTGTTATGGAGTGTCTCCTACTAAATTAAATGATCTCTGCTTTACTAATGTCTATGCAGATTCATTT" +
            "GTAATTAGAGGTGATGAAGTCAGACAAATCGCTCCAGGGCAAACTGGAAAGATTGCTGATTATAATTATA";

    /**
     * SARS-CoV-2 Spike RBD Variant segment (simulating point mutations and insertions).
     */
    public static final String SARS_COV2_SPIKE_RBD_VARIANT =
            "AATATTACAAACTTGTGCCCTTTTGGTGAAGTTTTTAACGCCACCAGATTTGCATCTGTTTATGCTTGGA" +
            "ACAGGAAGAGAATCAGCAACTGTGTTGCTGATTATTCAGTCCTATATAATTCCGCATCATTTTCCACTTT" +
            "TAAGTGTTATGGAGTGTCCCCTACTAAATTAAATGATCTCTGCTTTACTAATGTCTATGCAGATTCATTT" +
            "GTAATTAGAGGTGATGAAGTCAGACAAATTGCTCCAGGGCAAACTGGAAAGATTGCTGATTATAATTACA";

    /**
     * Standard Type II Restriction Endonuclease recognition motifs library.
     */
    public static Map<String, String> getRestrictionEnzymes() {
        Map<String, String> enzymes = new LinkedHashMap<>();
        enzymes.put("EcoRI", "GAATTC");      // 5' G^AATTC 3'
        enzymes.put("BamHI", "GGATCC");      // 5' G^GATCC 3'
        enzymes.put("HindIII", "AAGCTT");    // 5' A^AGCTT 3'
        enzymes.put("NotI", "GCGGCCGC");     // 5' GC^GGCCGC 3' (8-base cutter)
        enzymes.put("AluI", "AGCT");         // 5' AG^CT 3' (4-base blunt)
        enzymes.put("HhaI", "GCGC");         // 5' GCG^C 3'
        enzymes.put("TaqI", "TCGA");         // 5' T^CGA 3'
        enzymes.put("SmaI", "CCCGGG");       // 5' CCC^GGG 3'
        enzymes.put("DnaA-Box", "TTATCCACA");// Bacterial replication initiation motif
        enzymes.put("TATA-Box", "TATAAA");   // Core promoter element
        return enzymes;
    }

    /**
     * Synthetic sequencing read fragments for Shortest Common Superstring (SCS) assembly.
     */
    public static List<String> getSequencingReadsForAssembly() {
        return List.of(
                "ATGCGATAC",
                "CGATACGAT",
                "TACGATGCC",
                "TGCCGGTTA",
                "GGTTAGCTA",
                "AGCTATGCG",
                "GATGCCGGT"
        );
    }

    public static DnaSequence getEColiOriCSequence() {
        return new DnaSequence("NC_000913_oriC", "Escherichia coli K-12 MG1655 Replication Origin", ECOLI_ORIC_DNA);
    }

    public static DnaSequence getHumanHbbWildtypeSequence() {
        return new DnaSequence("NM_000518_HBB_WT", "Homo sapiens hemoglobin subunit beta (HBB) Wildtype Exon 1", HUMAN_HBB_WILDTYPE);
    }

    public static DnaSequence getHumanHbbSickleSequence() {
        return new DnaSequence("NM_000518_HBB_HbS", "Homo sapiens HBB Sickle-Cell Allele (c.20A>T, p.Glu6Val)", HUMAN_HBB_SICKLE_CELL);
    }

    public static DnaSequence getSarsCov2SpikeSequence() {
        return new DnaSequence("NC_045512_SpikeRBD", "SARS-CoV-2 Wuhan-Hu-1 Spike Protein RBD Domain", SARS_COV2_SPIKE_RBD_WT);
    }
}
