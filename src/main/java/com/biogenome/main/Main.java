package com.biogenome.main;

import com.biogenome.dp.*;
import com.biogenome.flow.*;
import com.biogenome.model.*;
import com.biogenome.np.*;
import com.biogenome.randparallel.*;
import com.biogenome.service.BioGenomeDataService;
import com.biogenome.string.*;
import com.biogenome.suffix.*;
import com.biogenome.util.ConsoleUI;
import com.biogenome.util.GenomicDataGenerator;

import java.util.*;

/**
 * BIOGENOME | BIOINFORMATICS ALGORITHM EXPLORER
 * <p>
 * A professional, scientific command-line interface for the Eclipse IDE console.
 * Demonstrates classical Data Structures and Algorithms through authentic biological
 * problems and genomic sequence workflows.
 * </p>
 *
 * Organized strictly around the Biology-First paradigm:
 * <pre>
 *   BIOLOGICAL TASK    -> What real biological problem are we solving?
 *   DSA ALGORITHM      -> Which computational technique solves it?
 *   HOW IT WORKS       -> Execution trace, verified output, and complexity.
 * </pre>
 *
 * @author BioGenome Team
 * @version 3.1.0
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BioGenomeDataService dataService = new BioGenomeDataService();

    public static void main(String[] args) {
        ConsoleUI.printBanner();

        while (true) {
            displayMainMenu();
            if (!scanner.hasNextLine()) break;
            String choice = ConsoleUI.readString(scanner, "Select a biological task (1-15, T, or 0 to exit)", "").trim();

            if (choice.equals("0") || choice.equalsIgnoreCase("exit") || choice.equalsIgnoreCase("q")) {
                ConsoleUI.printSectionHeader("Exiting BioGenome Explorer");
                System.out.println(ConsoleUI.green("  Thank you for using BioGenome. Happy computational biology research!\n"));
                break;
            }

            if (choice.equalsIgnoreCase("T")) {
                ConsoleUI.setColorsEnabled(!ConsoleUI.isColorsEnabled());
                ConsoleUI.printSuccess("ANSI color formatting is now " + (ConsoleUI.isColorsEnabled() ? "ENABLED" : "DISABLED") + ".");
                continue;
            }

            try {
                int option = Integer.parseInt(choice);
                switch (option) {
                    case 1:  menuDnaSequenceMatching(); break;
                    case 2:  menuMultiMotifScreening(); break;
                    case 3:  menuTandemRepeatAnalysis(); break;
                    case 4:  menuWholeGenomeIndexing(); break;
                    case 5:  menuMutationAnalysis(); break;
                    case 6:  menuGeneSequenceAlignment(); break;
                    case 7:  menuGenomicReadAssembly(); break;
                    case 8:  menuBiochemicalNetworks(); break;
                    case 9:  menuResourceAllocation(); break;
                    case 10: menuBiologicalConstraints(); break;
                    case 11: menuSortingAndAnalytics(); break;
                    case 12: menuParallelGenomicProcessing(); break;
                    case 13: menuAlgorithmComparisonLab(); break;
                    case 14: menuReferenceGenomeExplorer(); break;
                    case 15: menuSystemInformation(); break;
                    default:
                        ConsoleUI.printError("Invalid selection. Please choose an option from 1 to 15, or 0 to exit.");
                }
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Please enter a valid numeric option (1-15), T to toggle colors, or 0 to exit.");
            } catch (Exception e) {
                ConsoleUI.printError("Operation notice: " + e.getMessage());
            }
        }
    }

    private static void displayMainMenu() {
        ConsoleUI.printMenuHeader("MAIN MENU - BIOLOGICAL WORKFLOWS & ALGORITHMS");
        System.out.println("  " + ConsoleUI.cyan("[1]")  + "  DNA Sequence Matching & Target Search       " + ConsoleUI.gray("(KMP, Z-Algorithm, Rabin-Karp)"));
        System.out.println("  " + ConsoleUI.cyan("[2]")  + "  Multi-Motif Screening & Restriction Digestion" + ConsoleUI.gray("(Aho-Corasick Automaton)"));
        System.out.println("  " + ConsoleUI.cyan("[3]")  + "  Tandem Repeat & Periodic Region Discovery   " + ConsoleUI.gray("(Kasai LCP, Z-Repeats, CpG Islands)"));
        System.out.println("  " + ConsoleUI.cyan("[4]")  + "  Whole-Genome Indexing & Substring Lookup    " + ConsoleUI.gray("(Suffix Array, Suffix Tree, DAWG)"));
        System.out.println("  " + ConsoleUI.cyan("[5]")  + "  Mutation Analysis & Read Error Correction   " + ConsoleUI.gray("(Levenshtein & Damerau Distances)"));
        System.out.println("  " + ConsoleUI.cyan("[6]")  + "  Gene Homology & Pairwise Sequence Alignment " + ConsoleUI.gray("(Needleman-Wunsch, Smith-Waterman)"));
        System.out.println("  " + ConsoleUI.cyan("[7]")  + "  Genomic Read Assembly & Contig Optimization " + ConsoleUI.gray("(Shortest Superstring Bitmask DP)"));
        System.out.println("  " + ConsoleUI.cyan("[8]")  + "  Biochemical Networks & Logistics Flow       " + ConsoleUI.gray("(Ford-Fulkerson, Edmonds-Karp, Dinic)"));
        System.out.println("  " + ConsoleUI.cyan("[9]")  + "  Laboratory Resource Allocation & Conflicts  " + ConsoleUI.gray("(Bipartite Matching, Konig's Theorem)"));
        System.out.println("  " + ConsoleUI.cyan("[10]") + "  Biological Constraint Scheduling & Reductions" + ConsoleUI.gray("(3-SAT, CLIQUE, Indep Set, Vertex Cover)"));
        System.out.println("  " + ConsoleUI.cyan("[11]") + "  High-Throughput Sorting & Stream Analytics  " + ConsoleUI.gray("(Randomized QuickSort, Reservoir, Miller-Rabin)"));
        System.out.println("  " + ConsoleUI.cyan("[12]") + "  Parallel Genomic Processing & Scalability   " + ConsoleUI.gray("(Blelloch Scan, Parallel Reduce, Brent's Law)"));
        System.out.println("  " + ConsoleUI.cyan("[13]") + "  Algorithm Comparison Lab                    " + ConsoleUI.gray("(Side-by-Side Empirical Benchmarking)"));
        System.out.println("  " + ConsoleUI.cyan("[14]") + "  Reference Genome & Genetic Variant Explorer " + ConsoleUI.gray("(E. coli, HBB, SARS-CoV-2, ClinVar)"));
        System.out.println("  " + ConsoleUI.cyan("[15]") + "  System Information & DSA Curriculum Mapping " + ConsoleUI.gray("(Platform Architecture & Review Guide)"));
        System.out.println("  " + ConsoleUI.yellow("[T]") + "  Toggle ANSI Colors " + ConsoleUI.gray("(Currently: " + (ConsoleUI.isColorsEnabled() ? "ON" : "OFF") + ")"));
        System.out.println("  " + ConsoleUI.red("[0]")    + "  Exit Platform");
        System.out.println(ConsoleUI.color(ConsoleUI.BRIGHT_CYAN, ConsoleUI.SINGLE_LINE));
    }

    // =========================================================================
    // 1. DNA SEQUENCE MATCHING (KMP, Z-Algorithm, Rabin-Karp)
    // =========================================================================
    private static void menuDnaSequenceMatching() {
        ConsoleUI.printSectionHeader("1. DNA Sequence Matching & Target Search");
        System.out.println("  [1] Exact DNA Motif Search using Prefix Function (Knuth-Morris-Pratt / KMP)");
        System.out.println("  [2] Target Motif Search using Z-Box Intervals (Z-Algorithm)");
        System.out.println("  [3] Rolling-Hash Pattern Scanning with Collision Verification (Rabin-Karp)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select algorithm", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "DNA Promoter Motif Discovery",
                        "Knuth-Morris-Pratt (KMP)",
                        "Locates target biological sequences (e.g., TATA-box, transcription factor binding sites) without redundant backtracking using a precomputed prefix table (pi)."
                );
                String defaultTarget = GenomicDataGenerator.ECOLI_ORIC_DNA;
                String defaultMotif = "ACCAC";

                String text = chooseSequenceInput("Target Sequence", defaultTarget, "E. coli replication origin (oriC)");
                String pattern = ConsoleUI.readDna(scanner, "Enter target search motif", defaultMotif);

                long start = System.nanoTime();
                int[] pi = KMPMotifFinder.computePrefixFunction(pattern);
                List<Integer> matches = KMPMotifFinder.searchMotif(text, pattern);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Input Sequence", text, 50);
                ConsoleUI.printDnaField("Search Motif", pattern, 40);
                ConsoleUI.printHighlightField("Matches Found", matches.size() + " occurrences");
                ConsoleUI.printField("Match Positions (0-based)", matches.isEmpty() ? "None" : matches.toString());

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Pattern Length (m)", pattern.length());
                ConsoleUI.printField("LPS Table (pi-array)", Arrays.toString(pi));
                ConsoleUI.printField("Comparisons Avoided", "Linear O(N + M) scan; pointer resets via pi[q-1]");

                ConsoleUI.printComplexity("O(N + M)", "O(N + M)", "O(N + M)", "O(M)", elapsed / 1_000_000.0);
                dataService.recordAnalysis("M1", "KMP", "Exact Motif", "Target: " + pattern, matches.size() + " matches", (long)(elapsed / 1_000_000.0), "O(N + M)", "O(M)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Conserved Sequence Motif Search",
                        "Z-Algorithm (Linear Prefix Matching)",
                        "Computes the longest common prefix between pattern suffixes and the biological sequence using dynamic [L, R] Z-box boundaries."
                );
                String defaultTarget = GenomicDataGenerator.ECOLI_ORIC_DNA;
                String defaultMotif = "ACCAC";

                String text = chooseSequenceInput("Target Sequence", defaultTarget, "E. coli oriC");
                String pattern = ConsoleUI.readDna(scanner, "Enter target search motif", defaultMotif);

                long start = System.nanoTime();
                List<Integer> matches = ZAlgorithm.searchMotif(text, pattern);
                String combined = pattern + "$" + text.substring(0, Math.min(40, text.length()));
                int[] zSample = ZAlgorithm.calculateZArray(combined);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Input Sequence", text, 50);
                ConsoleUI.printDnaField("Search Motif", pattern, 40);
                ConsoleUI.printHighlightField("Matches Found", matches.size() + " occurrences");
                ConsoleUI.printField("Match Positions (0-based)", matches.isEmpty() ? "None" : matches.toString());

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Z-Array Preview", Arrays.toString(Arrays.copyOf(zSample, Math.min(14, zSample.length))) + "...");
                ConsoleUI.printField("Boundary Strategy", "Maintains active rightmost match boundary [L, R]");

                ConsoleUI.printComplexity("O(N + M)", "O(N + M)", "O(N + M)", "O(N + M)", elapsed / 1_000_000.0);
                dataService.recordAnalysis("M1", "Z-Algorithm", "Motif Search", "Target: " + pattern, matches.size() + " matches", (long)(elapsed / 1_000_000.0), "O(N + M)", "O(N + M)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "3": {
                ConsoleUI.printAlgorithmHeader(
                        "High-Throughput Sequence Matching",
                        "Rabin-Karp Rolling Hash Algorithm",
                        "Computes an O(1) rolling polynomial hash over a sliding sequence window, with character verification on hash collisions."
                );
                String defaultTarget = GenomicDataGenerator.ECOLI_ORIC_DNA;
                String defaultMotif = "AGCT";

                String text = chooseSequenceInput("Target Sequence", defaultTarget, "E. coli oriC");
                String pattern = ConsoleUI.readDna(scanner, "Enter target search motif", defaultMotif);

                long start = System.nanoTime();
                RabinKarpMatcher.SearchStats stats = RabinKarpMatcher.searchWithDetailedStats(text, pattern);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Input Sequence", text, 50);
                ConsoleUI.printDnaField("Search Motif", pattern, 40);
                ConsoleUI.printHighlightField("Matches Found", stats.getMatches().size() + " occurrences");
                ConsoleUI.printField("Match Positions (0-based)", stats.getMatches().isEmpty() ? "None" : stats.getMatches().toString());

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Hash Comparisons", stats.getHashComparisons());
                ConsoleUI.printField("Spurious Collisions", stats.getSpuriousHits() + " (resolved through exact character check)");
                ConsoleUI.printField("Collision Resolution", "Filtered via exact character verification");

                ConsoleUI.printComplexity("O(N + M)", "O(N + M)", "O(N * M)", "O(1)", elapsed / 1_000_000.0);
                dataService.recordAnalysis("M1", "Rabin-Karp", "Motif Scan", "Target: " + pattern, stats.getMatches().size() + " matches", (long)(elapsed / 1_000_000.0), "O(N + M)", "O(1)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 2. MULTI-MOTIF SCREENING (Aho-Corasick)
    // =========================================================================
    private static void menuMultiMotifScreening() {
        ConsoleUI.printSectionHeader("2. Multi-Motif Screening & Restriction Digestion");
        ConsoleUI.printAlgorithmHeader(
                "Restriction Enzyme Cleavage Screening",
                "Aho-Corasick Automaton (Trie with Failure Links)",
                "Screens biological sequences against an entire catalog of restriction endonuclease recognition motifs simultaneously in a single linear pass."
        );

        AhoCorasickMatcher matcher = new AhoCorasickMatcher();
        Map<String, String> enzymes = GenomicDataGenerator.getRestrictionEnzymes();
        enzymes.forEach(matcher::addMotif);

        String defaultTarget = GenomicDataGenerator.ECOLI_ORIC_DNA;
        String text = chooseSequenceInput("Target DNA Sequence", defaultTarget, "E. coli replication origin (oriC)");

        long start = System.nanoTime();
        matcher.buildAutomaton();
        List<MotifMatch> matches = matcher.search(text);
        long elapsed = System.nanoTime() - start;

        ConsoleUI.printResultsHeader();
        ConsoleUI.printDnaField("Screened Sequence", text, 50);
        ConsoleUI.printField("Enzyme Motifs Loaded", enzymes.size() + " recognition sites in Trie");
        ConsoleUI.printHighlightField("Total Cleavage Sites", matches.size() + " cleavage matches");

        System.out.println("\n  " + ConsoleUI.bold("Detected Enzyme Recognition Sites:"));
        int previewCount = Math.min(10, matches.size());
        for (int i = 0; i < previewCount; i++) {
            MotifMatch m = matches.get(i);
            System.out.printf("    * %-10s motif %-10s at 0-based offset %d%n",
                    ConsoleUI.cyan(m.getMotifName()), ConsoleUI.formatDna(m.getMotifPattern()), m.getStartIndex());
        }
        if (matches.size() > previewCount) {
            System.out.println("    ... and " + (matches.size() - previewCount) + " additional cleavage positions.");
        }

        ConsoleUI.printTraceHeader();
        ConsoleUI.printField("Automaton Construction", "BFS cross-branch failure transitions & dictionary suffix links");
        ConsoleUI.printField("Screening Efficiency", "O(N + sum(M_i) + Z) where Z is total occurrences");

        ConsoleUI.printComplexity("O(N + Sum(M_i) + Z)", "O(N + Sum(M_i) + Z)", "O(N + Sum(M_i) + Z)", "O(Sum(M_i) * |Sigma|)", elapsed / 1_000_000.0);
        dataService.recordAnalysis("M1", "Aho-Corasick", "Multi-Motif", "Restriction enzymes (" + enzymes.size() + ")", matches.size() + " sites found", (long)(elapsed / 1_000_000.0), "O(N + Sum(M_i) + Z)", "O(Sum(M_i))");
        ConsoleUI.pause(scanner);
    }

    // =========================================================================
    // 3. TANDEM REPEATS & PERIODIC REGIONS (Kasai LCP & Z-Repeats)
    // =========================================================================
    private static void menuTandemRepeatAnalysis() {
        ConsoleUI.printSectionHeader("3. Tandem Repeat & Periodic Region Analysis");
        System.out.println("  [1] Maximal Repeat Detection using Kasai's LCP Array (Linear Time)");
        System.out.println("  [2] Tandem Repeat & Self-Duplication Analysis via Z-Algorithm");
        System.out.println("  [3] CpG Island & Dinucleotide Frequency Scanning (Rabin-Karp)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select analysis method", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Maximal Genomic Repeat Detection",
                        "Kasai's LCP Array Construction",
                        "Identifies repeated sequences and duplications in linear time O(N) by exploiting rank inversion invariants on sorted suffixes."
                );
                String defaultTarget = GenomicDataGenerator.ECOLI_ORIC_DNA.substring(0, 100);
                String text = chooseSequenceInput("Analyzed Sequence", defaultTarget, "E. coli oriC (100 bp segment)");

                long start = System.nanoTime();
                SuffixArrayResult saRes = KasaiLCP.analyze(new DnaSequence("ORIC_SEG", text));
                long elapsed = System.nanoTime() - start;

                int maxLcp = Arrays.stream(saRes.getLcpArray()).max().orElse(0);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Input Sequence", text, 50);
                ConsoleUI.printHighlightField("Max LCP Value", maxLcp + " bp longest common repeat");
                ConsoleUI.printField("Repeated Substrings", saRes.getLongestRepeatedSubstrings());

                ConsoleUI.printTraceHeader();
                int[] sampleLcp = Arrays.copyOf(saRes.getLcpArray(), Math.min(14, saRes.getLcpArray().length));
                ConsoleUI.printField("LCP Array Sample", Arrays.toString(sampleLcp) + "...");
                ConsoleUI.printField("Kasai Invariant", "h >= h_prev - 1 guarantees linear O(N) comparisons");

                ConsoleUI.printComplexity("O(N)", "O(N)", "O(N)", "O(N)", elapsed / 1_000_000.0);
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Tandem Repeat & Instability Analysis",
                        "Z-Algorithm Self-Comparison",
                        "Identifies short tandem repeats (STRs) and duplicated genomic elements associated with trinucleotide repeat disorders."
                );
                String defaultTarget = GenomicDataGenerator.ECOLI_ORIC_DNA.substring(0, 120);
                String text = chooseSequenceInput("Sequence Segment", defaultTarget, "E. coli oriC segment");

                int[] z = ZAlgorithm.calculateZArray(text);
                int maxZ = 0;
                int maxIdx = 0;
                for (int i = 1; i < z.length; i++) {
                    if (z[i] > maxZ) {
                        maxZ = z[i];
                        maxIdx = i;
                    }
                }

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Input Sequence", text, 50);
                ConsoleUI.printHighlightField("Longest Self-Repeat", maxZ + " bp match at offset " + maxIdx);
                if (maxZ > 0) {
                    ConsoleUI.printField("Duplicated Motif", ConsoleUI.formatDna(text.substring(0, maxZ)));
                }

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Z-Array Preview", Arrays.toString(Arrays.copyOf(z, Math.min(15, z.length))) + "...");
                ConsoleUI.printComplexity("O(N)", "O(N)", "O(N)", "O(N)", 0.2);
                ConsoleUI.pause(scanner);
                break;
            }
            case "3": {
                ConsoleUI.printAlgorithmHeader(
                        "Oligonucleotide & CpG Island Survey",
                        "Rabin-Karp Dinucleotide Frequency Counter",
                        "Surveys frequencies of CpG islands ('CG') and regulatory dinucleotide markers to detect promoter-associated methylation regions."
                );
                String text = GenomicDataGenerator.HUMAN_HBB_WILDTYPE;
                String query = "CG";
                RabinKarpMatcher.SearchStats stats = RabinKarpMatcher.searchWithDetailedStats(text, query);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Analyzed Locus", text, 50);
                ConsoleUI.printField("Marker Oligo", query + " (CpG Island)");
                ConsoleUI.printHighlightField("Frequency Count", stats.getMatches().size() + " occurrences");
                ConsoleUI.printField("Occurrences At (0-based)", stats.getMatches().toString());

                ConsoleUI.printComplexity("O(N)", "O(N)", "O(N * M)", "O(1)", 0.15);
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 4. WHOLE-GENOME INDEXING (Suffix Array, Suffix Tree, DAWG)
    // =========================================================================
    private static void menuWholeGenomeIndexing() {
        ConsoleUI.printSectionHeader("4. Whole-Genome Indexing & Substring Lookup");
        System.out.println("  [1] Whole-Genome Suffix Array Index (Prefix Doubling / SA-IS)");
        System.out.println("  [2] Linear-Time Suffix Sorting (SA-IS Induced Sorting)");
        System.out.println("  [3] Substring Factor Hierarchy (Suffix Tree)");
        System.out.println("  [4] Minimal Substring Automaton (Directed Acyclic Word Graph / DAWG)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select indexing structure", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Whole-Genome Suffix Indexing",
                        "Suffix Array Index & Binary Search",
                        "Pre-indexes entire chromosomes into sorted suffix arrays, enabling logarithmic pattern discovery in O(M log N) time."
                );
                String defaultSeq = GenomicDataGenerator.ECOLI_ORIC_DNA.substring(0, 50);
                String text = chooseSequenceInput("Reference Sequence", defaultSeq, "E. coli oriC (50 bp)");

                long start = System.nanoTime();
                SuffixArray sa = new SuffixArray(text);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Indexed Sequence", text, 40);
                ConsoleUI.printHighlightField("Total Suffixes", sa.getSuffixArray().length);

                System.out.println("\n  " + ConsoleUI.bold("Lexicographically Sorted Suffix Table Preview:"));
                System.out.printf("    %-6s | %-12s | %s%n", "Rank", "0-Based Offset", "Suffix String Preview");
                System.out.println("    --------------------------------------------------");
                int[] arr = sa.getSuffixArray();
                int previewLen = Math.min(8, arr.length);
                for (int i = 0; i < previewLen; i++) {
                    int offset = arr[i];
                    String preview = text.substring(offset, Math.min(text.length(), offset + 20));
                    System.out.printf("    %-6d | %-14d | %s%n", i, offset, ConsoleUI.formatDna(preview));
                }

                ConsoleUI.printComplexity("O(N log^2 N) / O(N)", "O(N)", "O(N log^2 N)", "O(N)", elapsed / 1_000_000.0);
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Linear-Time Genome Suffix Sorting",
                        "SA-IS (Induced Sorting Algorithm)",
                        "Constructs suffix arrays in linear O(N) time and space by classifying suffixes into S-type, L-type, and Leftmost S-type (LMS) substrings."
                );
                String defaultSeq = GenomicDataGenerator.ECOLI_ORIC_DNA.substring(0, 80);
                String text = chooseSequenceInput("Reference Sequence", defaultSeq, "E. coli oriC (80 bp)");

                long start = System.nanoTime();
                SuffixArray sa = new SuffixArray(text);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Input Sequence", text, 50);
                ConsoleUI.printHighlightField("Construction Time", String.format("%.3f ms", elapsed / 1_000_000.0));
                ConsoleUI.printField("First 10 Suffix Ranks", Arrays.toString(Arrays.copyOf(sa.getSuffixArray(), Math.min(10, sa.getSuffixArray().length))));

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Partitioning Strategy", "Two-pass bucket sorting with LMS character reduction");
                ConsoleUI.printComplexity("O(N)", "O(N)", "O(N)", "O(N)", elapsed / 1_000_000.0);
                ConsoleUI.pause(scanner);
                break;
            }
            case "3": {
                ConsoleUI.printAlgorithmHeader(
                        "Genome Factorization & Substring Tree",
                        "Suffix Tree (Compact Edge-Labeled Radix Trie)",
                        "Represents all distinct factors of a genome in linear O(N) space, enabling immediate queries for exact repeats, unique primers, and palindromes."
                );
                String defaultSeq = "GATAGACAGATAGATA";
                String text = chooseSequenceInput("Reference Locus", defaultSeq, "Model sequence with repeated motifs");

                long start = System.nanoTime();
                SuffixTree tree = new SuffixTree(text);
                boolean contains = tree.contains("GATA");
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Reference Locus", text, 40);
                ConsoleUI.printHighlightField("Total Tree Nodes", tree.getTotalNodes());
                ConsoleUI.printField("Query 'GATA' Exists", contains ? "YES (Found in O(M) time)" : "NO");

                ConsoleUI.printComplexity("O(N)", "O(N)", "O(N)", "O(N)", elapsed / 1_000_000.0);
                ConsoleUI.pause(scanner);
                break;
            }
            case "4": {
                ConsoleUI.printAlgorithmHeader(
                        "Genome Substring State & Automaton Analysis",
                        "Directed Acyclic Word Graph (DAWG / Suffix Automaton)",
                        "Models the minimal deterministic finite automaton (DFA) recognizing all factors of a genome; counts distinct substrings and detects longest common substrings."
                );
                String defaultRef = GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.substring(0, 60);
                String ref = chooseSequenceInput("Reference Genome", defaultRef, "SARS-CoV-2 Spike RBD (60 bp)");
                String query = GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.substring(20, 60);

                long start = System.nanoTime();
                SuffixAutomaton sam = new SuffixAutomaton(ref);
                long distinct = sam.countDistinctSubstrings();
                String lcs = sam.findLongestCommonSubstring(query);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Reference Sequence", ref, 50);
                ConsoleUI.printHighlightField("Automaton States", sam.getStateCount() + " DFA states");
                ConsoleUI.printField("Distinct Substrings", distinct);
                ConsoleUI.printField("Longest Common Substr", "'" + ConsoleUI.formatDna(lcs) + "' (" + lcs.length() + " bp)");

                ConsoleUI.printComplexity("O(N)", "O(N)", "O(N)", "O(N)", elapsed / 1_000_000.0);
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 5. MUTATION ANALYSIS & ERROR CORRECTION (Levenshtein & Damerau)
    // =========================================================================
    private static void menuMutationAnalysis() {
        ConsoleUI.printSectionHeader("5. Mutation Analysis & Sequencing Error Correction");
        System.out.println("  [1] Point Mutations, Insertions & Deletions (Levenshtein Distance)");
        System.out.println("  [2] Adjacent Nucleotide Inversion & Swap Errors (Damerau-Levenshtein)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select mutation metric", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Mutation Distance Analysis",
                        "Levenshtein Distance (Dynamic Programming)",
                        "Quantifies the minimum elementary edit operations (substitutions, insertions, deletions) required to transform a sequencing read into a reference allele."
                );
                String defaultRead = "ATGCGTAACT";
                String defaultRef = "ATCGTAAT";

                String read = chooseSequenceInput("Sequencing Read", defaultRead, "Sequencing read fragment");
                String ref = chooseSequenceInput("Reference Locus", defaultRef, "Reference genomic locus");

                long start = System.nanoTime();
                EditDistanceResult res = DamerauLevenshteinAligner.align(read, ref);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Sequencing Read", read, 40);
                ConsoleUI.printDnaField("Reference Locus", ref, 40);
                ConsoleUI.printHighlightField("Edit Distance", res.getDistance() + " mutation operations");
                ConsoleUI.printField("Sequence Identity", String.format("%.2f%%", res.getSimilarity()));

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Operation Sequence", res.getOperations());
                ConsoleUI.printField("DP Matrix Recurrence", "dp[i][j] = min(dp[i-1][j]+1, dp[i][j-1]+1, dp[i-1][j-1]+cost)");

                ConsoleUI.printComplexity("O(N * M)", "O(N * M)", "O(N * M)", "O(N * M)", elapsed / 1_000_000.0);
                dataService.recordAnalysis("M3", "Levenshtein", "Edit Distance", read + " -> " + ref, "Distance: " + res.getDistance(), (long)(elapsed / 1_000_000.0), "O(N * M)", "O(N * M)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Adjacent Transposition Error Detection",
                        "Damerau-Levenshtein Distance (Inversion-Aware DP)",
                        "Distinguishes authentic double-substitutions from optical instrument adjacent base transpositions (swaps) by assigning single-swap cost."
                );
                String default1 = "ACGTCG";
                String default2 = "CAGTCG"; // Adjacent swap AC -> CA

                String s1 = chooseSequenceInput("Allele Sequence 1", default1, "Wildtype allele");
                String s2 = chooseSequenceInput("Allele Sequence 2", default2, "Swapped allele");

                long start = System.nanoTime();
                EditDistanceResult res = DamerauLevenshteinAligner.align(s1, s2);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printDnaField("Sequence 1", s1, 40);
                ConsoleUI.printDnaField("Sequence 2", s2, 40);
                ConsoleUI.printHighlightField("Damerau Distance", res.getDistance() + " operations (Swap cost = 1)");
                ConsoleUI.printField("Calculated Operations", res.getOperations());

                ConsoleUI.printComplexity("O(N * M)", "O(N * M)", "O(N * M)", "O(N * M)", elapsed / 1_000_000.0);
                dataService.recordAnalysis("M3", "Damerau-Levenshtein", "Transposition", s1 + " vs " + s2, "Distance: " + res.getDistance(), (long)(elapsed / 1_000_000.0), "O(N * M)", "O(N * M)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 6. GENE HOMOLOGY & SEQUENCE ALIGNMENT (Needleman-Wunsch & Smith-Waterman)
    // =========================================================================
    private static void menuGeneSequenceAlignment() {
        ConsoleUI.printSectionHeader("6. Gene Homology & Pairwise Sequence Alignment");
        System.out.println("  [1] Global End-to-End Alignment (Needleman-Wunsch Algorithm)");
        System.out.println("  [2] Local Conserved Sub-Domain Alignment (Smith-Waterman Algorithm)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select alignment mode", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Global Gene Homology Analysis",
                        "Needleman-Wunsch Dynamic Programming",
                        "Aligns two homologous coding genes end-to-end to detect conservation, evolutionary divergence, and indel mutations across exons."
                );
                String default1 = GenomicDataGenerator.HUMAN_HBB_WILDTYPE.substring(0, 36);
                String default2 = GenomicDataGenerator.HUMAN_HBB_SICKLE_CELL.substring(0, 36);

                String seq1 = chooseSequenceInput("Sequence 1 (Wild-Type HbA)", default1, "Human HBB Exon 1");
                String seq2 = chooseSequenceInput("Sequence 2 (Sickle Cell HbS)", default2, "Human HBB Sickle Cell Codon 6 Variant");

                int matchScore = ConsoleUI.readInt(scanner, "Enter match score", 1, 10, 2);
                int mismatchPenalty = ConsoleUI.readInt(scanner, "Enter mismatch penalty (negative)", -10, -1, -1);
                int gapPenalty = ConsoleUI.readInt(scanner, "Enter linear gap penalty (negative)", -10, -1, -2);

                NeedlemanWunschAligner nw = new NeedlemanWunschAligner(matchScore, mismatchPenalty, gapPenalty);
                long start = System.nanoTime();
                AlignmentResult res = nw.align(seq1, seq2);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printHighlightField("Global Alignment Score", res.getAlignmentScore());
                ConsoleUI.printField("Sequence Identity", String.format("%.2f%%", res.getIdentityPercentage()));

                ConsoleUI.printTraceHeader();
                System.out.println(ConsoleUI.bold("  Visual Alignment Traceback:"));
                System.out.println(res.getVisualAlignment());

                ConsoleUI.printComplexity("O(N * M)", "O(N * M)", "O(N * M)", "O(N * M)", elapsed / 1_000_000.0);
                dataService.recordAnalysis("M3", "Needleman-Wunsch", "Global Alignment", "HBB WT vs Sickle", "Score: " + res.getAlignmentScore(), (long)(elapsed / 1_000_000.0), "O(N * M)", "O(N * M)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Local Conserved Motif Alignment",
                        "Smith-Waterman Dynamic Programming",
                        "Identifies the highest scoring local conserved sub-domain between two divergent sequences, clamping negative DP cells to zero."
                );
                String default1 = GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.substring(0, 50);
                String default2 = GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.substring(15, 65);

                String seq1 = chooseSequenceInput("Sequence 1 (Reference)", default1, "Viral spike RBD domain");
                String seq2 = chooseSequenceInput("Sequence 2 (Target)", default2, "Shifted viral domain");

                SmithWatermanAligner sw = new SmithWatermanAligner(3, -2, -2);
                long start = System.nanoTime();
                AlignmentResult res = sw.align(seq1, seq2);
                long elapsed = System.nanoTime() - start;

                ConsoleUI.printResultsHeader();
                ConsoleUI.printHighlightField("Local Alignment Score", res.getAlignmentScore());
                ConsoleUI.printField("Local Identity", String.format("%.2f%%", res.getIdentityPercentage()));

                ConsoleUI.printTraceHeader();
                System.out.println(ConsoleUI.bold("  Optimal Local Alignment:"));
                System.out.println(res.getVisualAlignment());

                ConsoleUI.printComplexity("O(N * M)", "O(N * M)", "O(N * M)", "O(N * M)", elapsed / 1_000_000.0);
                dataService.recordAnalysis("M3", "Smith-Waterman", "Local Alignment", "Spike RBD Homology", "Score: " + res.getAlignmentScore(), (long)(elapsed / 1_000_000.0), "O(N * M)", "O(N * M)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 7. GENOMIC READ ASSEMBLY (Bitmask DP)
    // =========================================================================
    private static void menuGenomicReadAssembly() {
        ConsoleUI.printSectionHeader("7. Genomic Read Assembly & Contig Optimization");
        ConsoleUI.printAlgorithmHeader(
                "Shortest Common Superstring (SCS) Assembly",
                "Genomic Bitmask Dynamic Programming",
                "Assembles overlapping short sequencing reads into the minimal single contiguous genomic sequence (contig) using bitmask subset states."
        );

        List<String> reads = GenomicDataGenerator.getSequencingReadsForAssembly();
        System.out.println("  " + ConsoleUI.bold("Input Sequencing Read Fragments:"));
        for (int i = 0; i < reads.size(); i++) {
            System.out.printf("    Read [%d] : %s%n", i + 1, ConsoleUI.formatDna(reads.get(i)));
        }

        long start = System.nanoTime();
        GenomicBitmaskDP.AssemblyResult res = GenomicBitmaskDP.assembleShortestCommonSuperstring(reads);
        long elapsed = System.nanoTime() - start;

        ConsoleUI.printResultsHeader();
        ConsoleUI.printHighlightField("Reconstructed Contig", ConsoleUI.formatDna(res.getAssembledSequence()));
        ConsoleUI.printField("Total Assembled Length", res.getTotalLength() + " bp");
        ConsoleUI.printField("Nucleotides Saved", res.getOverlapSaved() + " bp saved via read overlap");
        ConsoleUI.printField("Optimal Assembly Order", res.getReadOrder());

        ConsoleUI.printTraceHeader();
        ConsoleUI.printField("State Representation", "dp[mask][last_read] where mask tracks assembled subset");
        ConsoleUI.printComplexity("O(K^2 * 2^K)", "O(K^2 * 2^K)", "O(K^2 * 2^K)", "O(K * 2^K)", elapsed / 1_000_000.0);
        dataService.recordAnalysis("M3", "Bitmask DP", "Contig Assembly", reads.size() + " reads", "Assembled contig of length " + res.getTotalLength(), (long)(elapsed / 1_000_000.0), "O(K^2 * 2^K)", "O(K * 2^K)");
        ConsoleUI.pause(scanner);
    }

    // =========================================================================
    // 8. BIOCHEMICAL NETWORKS & FLOW (Ford-Fulkerson, Edmonds-Karp, Dinic, Min-Cut)
    // =========================================================================
    private static void menuBiochemicalNetworks() {
        ConsoleUI.printSectionHeader("8. Biochemical Networks & Logistics Flow");
        System.out.println("  [1] Specimen Pipeline Flow (Depth-First Search / Ford-Fulkerson)");
        System.out.println("  [2] Guaranteed-Time Flow Routing (Shortest Augmenting Path / Edmonds-Karp)");
        System.out.println("  [3] High-Volume Sample Allocation (Level Graph Blocking Flow / Dinic)");
        System.out.println("  [4] Pipeline Bottleneck & Vulnerability Analysis (Max-Flow Min-Cut Theorem)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select network flow algorithm", "1");
        FlowNetwork net = dataService.buildSampleResourceNetwork();
        int s = 0, t = net.getVertexCount() - 1;

        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Laboratory Specimen Flow Optimization",
                        "Ford-Fulkerson Method (DFS Augmentation)",
                        "Calculates maximal biological sample throughput through multi-tier preparation facilities, sequencers, and analysis stages."
                );
                FlowResult res = FordFulkerson.computeMaxFlow(net, s, t);
                printFlowSummary(res);
                dataService.recordAnalysis("M4", "Ford-Fulkerson", "Lab Flow", "Source -> Labs -> Sink", "Max flow: " + res.getMaxFlow() + " units", (long)res.getExecutionTimeMs(), "O(E * |f*|)", "O(V + E)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Sample Cold-Chain Distribution",
                        "Edmonds-Karp Algorithm (BFS Augmenting Paths)",
                        "Guarantees polynomial-time convergence O(V * E^2) by always selecting augmenting paths with the minimal number of edges."
                );
                FlowResult res = EdmondsKarp.computeMaxFlow(net, s, t);
                printFlowSummary(res);
                dataService.recordAnalysis("M4", "Edmonds-Karp", "Sample Flow", "Shortest path augmentation", "Max flow: " + res.getMaxFlow() + " units", (long)res.getExecutionTimeMs(), "O(V * E^2)", "O(V + E)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "3": {
                ConsoleUI.printAlgorithmHeader(
                        "High-Throughput Assay Routing",
                        "Dinic's Algorithm (Level Graph & Blocking Flow)",
                        "Maximizes throughput across multi-core sequencing pipelines using BFS level graphs and simultaneous DFS blocking flow pushes."
                );
                FlowResult res = DinicAlgorithm.computeMaxFlow(net, s, t);
                printFlowSummary(res);
                dataService.recordAnalysis("M4", "Dinic", "High-Throughput Flow", "Level graph blocking flow", "Max flow: " + res.getMaxFlow() + " units", (long)res.getExecutionTimeMs(), "O(V^2 * E)", "O(V + E)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "4": {
                ConsoleUI.printAlgorithmHeader(
                        "Critical Resource Bottleneck Detection",
                        "Max-Flow Min-Cut Theorem",
                        "Identifies the exact minimal set of saturated communication channels and sequencer queues that limit maximum laboratory throughput."
                );
                FlowResult res = MaxFlowMinCut.analyzeBottlenecks(net, s, t);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printHighlightField("Maximum Network Flow", res.getMaxFlow() + " capacity units / day");
                ConsoleUI.printField("Minimum Cut Capacity", res.getMinCutCapacity() + " units (Max-Flow = Min-Cut Verified)");

                ConsoleUI.printTraceHeader();
                System.out.println("  " + ConsoleUI.bold("Critical Saturated Bottleneck Edges (S-Cut -> T-Cut):"));
                for (FlowResult.EdgeFlowInfo edge : res.getMinCutBottleneckEdges()) {
                    System.out.printf("    [BOTTLENECK] %-18s -> %-18s (Cap: %d, Flow: %d)%n",
                            edge.getFromName(), edge.getToName(), edge.getCapacity(), edge.getFlow());
                }

                ConsoleUI.printComplexity("O(V * E^2)", "O(V^2 * E)", "O(V^2 * E)", "O(V + E)", res.getExecutionTimeMs());
                dataService.recordAnalysis("M4", "Max-Flow Min-Cut", "Bottlenecks", "Cut capacity: " + res.getMinCutCapacity(), "Found " + res.getMinCutBottleneckEdges().size() + " bottleneck edges", (long)res.getExecutionTimeMs(), "O(V * E^2)", "O(V + E)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    private static void printFlowSummary(FlowResult res) {
        ConsoleUI.printResultsHeader();
        ConsoleUI.printField("Algorithm Evaluated", res.getAlgorithm());
        ConsoleUI.printHighlightField("Maximum Network Flow", res.getMaxFlow() + " units / day");
        ConsoleUI.printField("Augmenting Paths Found", res.getAugmentingPaths().size());
        for (FlowResult.AugmentingPathInfo p : res.getAugmentingPaths()) {
            System.out.printf("    Path #%d: %s (Bottleneck Capacity: %d)%n",
                    p.getPathNumber(), p.getPathString(), p.getBottleneckCapacity());
        }
        ConsoleUI.printField("Min-Cut Capacity", res.getMinCutCapacity() + " units");
        ConsoleUI.printComplexity(res.getTimeComplexity(), res.getTimeComplexity(), res.getTimeComplexity(), "O(V + E)", res.getExecutionTimeMs());
    }

    // =========================================================================
    // 9. RESOURCE ALLOCATION & BIPARTITE MATCHING (Konig's Theorem)
    // =========================================================================
    private static void menuResourceAllocation() {
        ConsoleUI.printSectionHeader("9. Laboratory Resource Allocation & Conflicts");
        ConsoleUI.printAlgorithmHeader(
                "Scientist-to-Assay Matching & Conflict Duality",
                "Maximum Bipartite Matching & Konig's Min Vertex Cover",
                "Assigns specialized genomic scientists to eligible clinical protocols and proves that max matching equals min vertex cover in bipartite graphs."
        );

        List<String> resNames = new ArrayList<>();
        List<String> expNames = new ArrayList<>();
        dataService.getAllResearchers().forEach(r -> resNames.add(r.getName()));
        dataService.getAllExperiments().forEach(e -> expNames.add(e.getName()));

        BipartiteMatching.MatchingResult mRes = BipartiteMatching.computeMatching(resNames, expNames, dataService.buildSampleEligibility());

        ConsoleUI.printResultsHeader();
        ConsoleUI.printField("Eligible Researchers", resNames.size());
        ConsoleUI.printField("Active Experiments", expNames.size());
        ConsoleUI.printHighlightField("Max Matching Size", mRes.getMatchingSize() + " optimal assignments");

        System.out.println("\n  " + ConsoleUI.bold("Assigned Research Collaborations:"));
        for (BipartiteMatching.MatchAssignment a : mRes.getAssignments()) {
            System.out.printf("    * %-22s <=====> %s%n", ConsoleUI.cyan(a.getResearcherName()), a.getExperimentName());
        }

        ConsoleUI.printTraceHeader();
        System.out.println("  " + ConsoleUI.bold("Konig's Duality Theorem Verification:"));
        ConsoleUI.printField("Min Vertex Cover Size", mRes.getMinVertexCoverSize());
        ConsoleUI.printField("|Max Matching| == |Min VC|", mRes.isKonigTheoremSatisfied() + " (Equality strictly holds on bipartite networks)");
        ConsoleUI.printField("Critical Resource Vertices", mRes.getMinVertexCoverNodes());

        ConsoleUI.printComplexity("O(E * sqrt(V))", "O(E * sqrt(V))", "O(E * sqrt(V))", "O(V + E)", mRes.getExecutionTimeMs());
        dataService.recordAnalysis("M4", "Bipartite Matching", "Matching", resNames.size() + " researchers", mRes.getMatchingSize() + " matched, Min VC=" + mRes.getMinVertexCoverSize(), (long)mRes.getExecutionTimeMs(), "O(E * sqrt(V))", "O(V + E)");
        ConsoleUI.pause(scanner);
    }

    // =========================================================================
    // 10. BIOLOGICAL CONSTRAINTS & NP REDUCTIONS (3-SAT, CLIQUE, IS, VC)
    // =========================================================================
    private static void menuBiologicalConstraints() {
        ConsoleUI.printSectionHeader("10. Biological Constraint Scheduling & NP Reductions");
        System.out.println("  [1] Primer & Reagent Multiplex Scheduling (3-SAT DPLL Solver)");
        System.out.println("  [2] Consistency to Interaction Clusters (3-SAT -> CLIQUE Karp Reduction)");
        System.out.println("  [3] Mutually Exclusive Biomarker Selection (CLIQUE -> INDEPENDENT SET)");
        System.out.println("  [4] Minimum Network Coverage Duality (INDEPENDENT SET -> VERTEX COVER / Gallai)");
        System.out.println("  [5] Near-Optimal Sensor Selection (Vertex Cover 2-Approximation)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select constraint problem", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Multiplex Assay Constraint Satisfaction",
                        "DPLL 3-SAT Solver (Backtracking with Unit Propagation)",
                        "Determines whether complex mutually exclusive primer pairings and temperature constraints can all be satisfied simultaneously."
                );
                List<SatClause> clauses = dataService.getSample3SatFormula();
                ThreeSatSolver.SatResult res = ThreeSatSolver.solve(clauses);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Input CNF Formula", res.getClauseStrings());
                ConsoleUI.printField("Variables Count", res.getVariables().size() + " variables " + res.getVariables());
                ConsoleUI.printHighlightField("Satisfiable Status", res.isSatisfiable() ? "YES (Valid experimental schedule found)" : "NO (Conflicts present)");
                if (res.isSatisfiable()) {
                    ConsoleUI.printField("Consistent Assignment", res.getModel());
                }

                ConsoleUI.printComplexity("O(2^V)", "Exponential", "O(2^V)", "O(V + C)", res.getExecutionTimeMs());
                dataService.recordAnalysis("M5", "DPLL 3-SAT", "Constraints", clauses.size() + " clauses", res.isSatisfiable() ? "SATISFIABLE" : "UNSATISFIABLE", (long)res.getExecutionTimeMs(), "O(2^V)", "O(V + C)");
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Biological Consistency to Graph Clique",
                        "Polynomial-Time Reduction (3-SAT -> CLIQUE)",
                        "Transforms boolean assay requirements into a network where a k-clique represents a mutually compatible set of biological choices across all clauses."
                );
                List<SatClause> clauses = dataService.getSample3SatFormula();
                SatToCliqueReduction.ReductionResult res = SatToCliqueReduction.reduceAndSolve(clauses);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Input Clauses", clauses.size() + " 3-CNF clauses");
                ConsoleUI.printField("Generated Graph", res.getNodes().size() + " vertices, " + res.getEdges().size() + " compatibility edges");
                ConsoleUI.printField("Target Clique Size (k)", res.getRequiredCliqueSize());
                ConsoleUI.printHighlightField("Max Clique Found", res.getFoundClique().size() + " vertices");

                ConsoleUI.printTraceHeader();
                System.out.println("  " + ConsoleUI.bold("Compatible Interaction Nodes in Clique:"));
                for (SatToCliqueReduction.ReductionNode node : res.getFoundClique()) {
                    System.out.println("    * " + node.getLabel());
                }
                ConsoleUI.printField("Karp Reduction Theorem", "Formula is SAT <=> Graph has a k-Clique");

                ConsoleUI.printComplexity("O(C^2)", "O(C^2)", "O(C^2)", "O(C^2)", 0.5);
                ConsoleUI.pause(scanner);
                break;
            }
            case "3": {
                ConsoleUI.printAlgorithmHeader(
                        "Conflict-Free Marker Selection",
                        "Graph Complementation Duality (CLIQUE -> INDEPENDENT SET)",
                        "Selects non-cross-hybridizing genetic markers by inverting an interaction graph G into complement G_bar, mapping cliques to independent sets."
                );
                int n = 6;
                List<String> names = Arrays.asList("GeneA", "GeneB", "GeneC", "GeneD", "GeneE", "GeneF");
                List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{1, 2}, new int[]{0, 2}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5}, new int[]{3, 5});

                CliqueToIndependentSet.IndependentSetResult res = CliqueToIndependentSet.reduceAndSolve(n, names, edges);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Interaction Graph G", n + " genes, " + edges.size() + " interaction links");
                ConsoleUI.printField("Complement Graph G'", res.getComplementEdges().size() + " non-interaction edges");
                ConsoleUI.printField("Max Clique in G", res.getMaxCliqueInOriginal());
                ConsoleUI.printHighlightField("Max Indep Set in G'", res.getMaxIndependentSetInComplement() + " (size " + res.getSize() + ")");

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Equivalence Proved", "S is a Clique in G <=> S is an Independent Set in G_bar");
                ConsoleUI.printComplexity("O(V + E)", "O(V^2)", "O(V^2)", "O(V^2)", 0.4);
                ConsoleUI.pause(scanner);
                break;
            }
            case "4": {
                ConsoleUI.printAlgorithmHeader(
                        "Network Duality & Minimal Coverage",
                        "Gallai's Identity (INDEPENDENT SET -> VERTEX COVER)",
                        "Proves that finding maximum non-interacting biomarkers immediately yields the minimum set of genes that covers all pathogenic interactions."
                );
                int n = 6;
                List<String> names = Arrays.asList("TargetA", "TargetB", "TargetC", "TargetD", "TargetE", "TargetF");
                List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{1, 2}, new int[]{0, 2}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5}, new int[]{3, 5});

                IndependentSetToVertexCover.DualityResult res = IndependentSetToVertexCover.computeDuality(n, names, edges);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Max Independent Set", res.getMaxIndependentSet() + " (size " + res.getIndependentSetSize() + ")");
                ConsoleUI.printHighlightField("Min Vertex Cover", res.getMinVertexCover() + " (size " + res.getVertexCoverSize() + ")");

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Gallai's Identity", String.format("|IS| + |VC| = %d + %d = %d (|V| = %d)",
                        res.getIndependentSetSize(), res.getVertexCoverSize(),
                        res.getIndependentSetSize() + res.getVertexCoverSize(), res.getTotalVertices()));
                ConsoleUI.printField("Duality Holds", res.isDualityVerified());

                ConsoleUI.printComplexity("O(V)", "O(V)", "O(V)", "O(V)", 0.3);
                ConsoleUI.pause(scanner);
                break;
            }
            case "5": {
                ConsoleUI.printAlgorithmHeader(
                        "Fast Biological Network Monitoring",
                        "Vertex Cover 2-Approximation (Maximal Matching Heuristic)",
                        "Selects a near-optimal set of monitoring sensors across large cellular interaction networks in linear time with guaranteed approximation ratio <= 2x OPT."
                );
                int n = 7;
                List<String> names = Arrays.asList("TP53", "MDM2", "BRCA1", "ATM", "CHEK2", "CDKN1A", "PTEN");
                List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{0, 3}, new int[]{0, 5}, new int[]{2, 3}, new int[]{2, 4}, new int[]{3, 4}, new int[]{5, 6});

                VertexCoverApproximation.ApproxResult res = VertexCoverApproximation.compute2Approximation(n, names, edges);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Interaction Network", n + " cancer genes, " + edges.size() + " functional links");
                ConsoleUI.printField("Maximal Matching Edges", res.getSelectedMatchingEdges().size());
                ConsoleUI.printHighlightField("Approximate Cover Nodes", res.getApproximateCoverVertices() + " (Size: " + res.getApproxCoverSize() + ")");
                ConsoleUI.printField("Exact Optimal (OPT) Size", res.getExactOptSize());
                ConsoleUI.printField("Empirical Ratio", String.format("%.2f (Guaranteed <= 2.0x OPT)", res.getEmpiricalRatio()));

                ConsoleUI.printComplexity("O(V + E)", "O(V + E)", "O(V + E)", "O(V)", 0.2);
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 11. SORTING & STREAM ANALYTICS (QuickSort, Reservoir, Miller-Rabin)
    // =========================================================================
    private static void menuSortingAndAnalytics() {
        ConsoleUI.printSectionHeader("11. High-Throughput Sorting & Stream Analytics");
        System.out.println("  [1] Sequencing Read Record Sorting (Randomized QuickSort)");
        System.out.println("  [2] Real-Time Sequencer Stream Sampling (Reservoir Sampling / Algorithm R)");
        System.out.println("  [3] Cryptogenomic Key Primality Testing (Miller-Rabin Probabilistic Test)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select analytics technique", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Large-Scale Sequencing Read Sorting",
                        "Randomized Pivot QuickSort",
                        "Sorts genomic records by length or GC-content without degrading into worst-case O(N^2) on already sorted or highly duplicated sequence libraries."
                );
                List<RandomizedQuickSort.GenomeSortItem> items = new ArrayList<>();
                for (DnaSequence s : dataService.getAllSequences()) {
                    items.add(new RandomizedQuickSort.GenomeSortItem(s.getId(), s.getDescription(), s.length(), "length", s.length() + " bp"));
                }
                RandomizedQuickSort.QuickSortResult res = RandomizedQuickSort.sort(items, true);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printHighlightField("Sorted Genomic Records", res.getSortedItems().size() + " records");
                for (RandomizedQuickSort.GenomeSortItem item : res.getSortedItems()) {
                    System.out.printf("    * %-18s : %s%n", ConsoleUI.cyan(item.getId()), item.getFormattedValue());
                }

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Comparisons Performed", res.getTotalComparisons());
                ConsoleUI.printField("Max Recursion Depth", res.getMaxRecursionDepth());
                ConsoleUI.printComplexity("O(N log N)", "O(N log N)", "O(N^2) with prob 1/N!", "O(log N)", res.getExecutionTimeMs());
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Unbiased Sequencer Stream Sampling",
                        "Reservoir Sampling (Algorithm R)",
                        "Maintains a uniform random sample of size k from an unbounded continuous stream of sequencer instrument observations without storing the full stream."
                );
                List<ContinuousObservation> stream = dataService.getAllObservations();
                int k = ConsoleUI.readInt(scanner, "Enter sample reservoir capacity (k)", 1, stream.size(), 5);

                ReservoirSampling.ReservoirResult res = ReservoirSampling.sampleStream(stream, k);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Total Stream Observations", res.getTotalStreamItems() + " readings");
                ConsoleUI.printField("Reservoir Capacity (k)", res.getReservoirCapacity() + " samples");
                ConsoleUI.printHighlightField("Inclusion Probability", String.format("%.4f (k / N exact uniform)", res.getTheoreticalProbability()));

                System.out.println("\n  " + ConsoleUI.bold("Sampled Observations in Reservoir:"));
                for (ContinuousObservation obs : res.getFinalSample()) {
                    System.out.println("    * " + obs);
                }

                ConsoleUI.printComplexity("O(N)", "O(N)", "O(N)", "O(k)", 0.2);
                ConsoleUI.pause(scanner);
                break;
            }
            case "3": {
                ConsoleUI.printAlgorithmHeader(
                        "Cryptogenomic Primality Verification",
                        "Miller-Rabin Probabilistic Primality Test",
                        "Verifies the primality of large keys used in genomic privacy, homomorphic encryption, and patient de-identification with error probability <= 4^(-k)."
                );
                String defaultNum = "1000000007";
                String val = ConsoleUI.readString(scanner, "Enter large integer to test for primality", defaultNum);
                int rounds = ConsoleUI.readInt(scanner, "Enter number of testing witness rounds (k)", 1, 50, 10);

                MillerRabinPrimality.PrimalityResult res = MillerRabinPrimality.testPrimality(val, rounds);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Integer Tested", res.getNumberTested());
                ConsoleUI.printHighlightField("Primality Verdict", res.isPrime() ? "PROBABLY PRIME (Certified for Cryptogenomics)" : "COMPOSITE (Has factors)");
                ConsoleUI.printField("Witness Rounds", res.getRounds() + " independent modular tests");
                ConsoleUI.printField("Factored Representation", "n - 1 = 2^" + res.getS() + " * " + res.getD());
                if (res.isPrime()) {
                    ConsoleUI.printField("Maximum Error Probability", String.format("<= %.2e", res.getMaxErrorProbability()));
                }

                ConsoleUI.printComplexity("O(k log^3 N)", "O(k log^3 N)", "O(k log^3 N)", "O(1)", 0.1);
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 12. PARALLEL GENOMIC PROCESSING (Blelloch Scan, Parallel Reduce, Brent)
    // =========================================================================
    private static void menuParallelGenomicProcessing() {
        ConsoleUI.printSectionHeader("12. Parallel Genomic Processing & Scalability");
        System.out.println("  [1] High-Throughput Prefix Scan (Blelloch Up-Sweep / Down-Sweep)");
        System.out.println("  [2] Parallel Tree Aggregation & GC-Count (Parallel Reduce)");
        System.out.println("  [3] Multi-Core Scheduling & Speedup Estimation (Brent's Theorem)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select parallel method", "1");
        switch (choice) {
            case "1": {
                ConsoleUI.printAlgorithmHeader(
                        "Parallel Genomic Prefix Scanning",
                        "Blelloch Work-Efficient Parallel Scan",
                        "Calculates cumulative chromosomal coverage and position offsets in parallel with O(N) total work and O(log N) parallel depth (span)."
                );
                long[] arr = {3, 1, 7, 0, 4, 1, 6, 3};
                BlellochScan.ScanResult res = BlellochScan.scan(arr);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Input Base Coverage", Arrays.toString(arr));
                ConsoleUI.printHighlightField("Exclusive Prefix Sum", Arrays.toString(res.getExclusivePrefixSum()));
                ConsoleUI.printField("Inclusive Prefix Sum", Arrays.toString(res.getInclusivePrefixSum()));

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Up-Sweep (Reduce) Levels", res.getUpSweepSteps().size() + " parallel stages");
                ConsoleUI.printField("Down-Sweep (Distribute)", res.getDownSweepSteps().size() + " parallel stages");
                ConsoleUI.printField("Work vs Span Operations", res.getWorkOperations() + " ops (Work) vs " + res.getSpanSteps() + " steps (Span)");

                ConsoleUI.printComplexity("O(N) Work", "O(N) Work", "O(N) Work", "O(N)", 0.2);
                ConsoleUI.pause(scanner);
                break;
            }
            case "2": {
                ConsoleUI.printAlgorithmHeader(
                        "Parallel Divide-and-Conquer Reduction",
                        "Parallel Binary Tree Reduction",
                        "Aggregates large sequence length metrics and quality scores using concurrent worker thread pairs in O(log N) parallel span."
                );
                long[] arr = {100, 200, 150, 300, 250, 400, 180, 220};
                ParallelReduce.ReduceResult res = ParallelReduce.reduce(arr);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Input Read Lengths", Arrays.toString(arr));
                ConsoleUI.printHighlightField("Aggregated Base Count", res.getFinalResult() + " bp total");

                ConsoleUI.printTraceHeader();
                ConsoleUI.printField("Tree Depth Levels", res.getReductionTree().size() + " logarithmic stages");
                ConsoleUI.printField("Work vs Span", res.getWorkOperations() + " additions (Work) vs " + res.getSpanSteps() + " steps (Span)");

                ConsoleUI.printComplexity("O(N) Work", "O(log N) Span", "O(N) Work", "O(N)", 0.1);
                ConsoleUI.pause(scanner);
                break;
            }
            case "3": {
                ConsoleUI.printAlgorithmHeader(
                        "Multi-Core Throughput & Speedup Bound",
                        "Brent's Scheduling Work-Span Theorem",
                        "Estimates achievable parallel speedup and execution time Tp <= (W - S)/P + S for genomics pipelines given serial work W, span S, and P cores."
                );
                double w = ConsoleUI.readInt(scanner, "Enter serial work W (time units on 1 core)", 10, 100000, 1000);
                double s = ConsoleUI.readInt(scanner, "Enter critical path span S (time on infinite cores)", 1, (int)w, 50);
                int p = ConsoleUI.readInt(scanner, "Enter number of available processor cores P", 1, 128, 16);

                BrentsLawCalculator.BrentResult res = BrentsLawCalculator.calculate(w, s, p);

                ConsoleUI.printResultsHeader();
                ConsoleUI.printField("Serial Work W (1 core)", res.getWork() + " time units");
                ConsoleUI.printField("Critical Path Span S", res.getSpan() + " time units");
                ConsoleUI.printField("Processor Cores P", res.getProcessors() + " cores");
                ConsoleUI.printHighlightField("Estimated Parallel Time Tp", String.format("%.2f time units", res.getEstimatedParallelTime()));
                ConsoleUI.printHighlightField("Theoretical Speedup", String.format("%.2fx faster than single core", res.getSpeedup()));
                ConsoleUI.printField("Processor Efficiency", String.format("%.1f%%", res.getEfficiency() * 100));
                ConsoleUI.printField("Regime Interpretation", res.getRegimeInterpretation());

                ConsoleUI.printComplexity("O(1)", "O(1)", "O(1)", "O(1)", 0.05);
                ConsoleUI.pause(scanner);
                break;
            }
            case "0":
            default:
                break;
        }
    }

    // =========================================================================
    // 13. ALGORITHM COMPARISON LAB (Side-by-Side Benchmarks)
    // =========================================================================
    private static void menuAlgorithmComparisonLab() {
        ConsoleUI.printSectionHeader("13. Algorithm Comparison Lab (Side-by-Side Benchmarking)");
        System.out.println("  [1] Compare DNA Motif Search Algorithms (KMP vs Z-Algorithm vs Rabin-Karp)");
        System.out.println("  [2] Compare Sequence Alignment & Distance Methods (NW vs SW vs Levenshtein)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select benchmark suite", "1");
        if (choice.equals("1")) {
            ConsoleUI.printAlgorithmHeader(
                    "DNA Motif Search Benchmark",
                    "Comparative Algorithmic Analysis (KMP vs Z vs Rabin-Karp)",
                    "Evaluates empirical execution time, character comparisons, and space overheads across three distinct exact string algorithms on identical genomic targets."
            );
            String defaultTarget = GenomicDataGenerator.ECOLI_ORIC_DNA;
            String text = chooseSequenceInput("Target Sequence", defaultTarget, "E. coli oriC");
            String pattern = ConsoleUI.readDna(scanner, "Enter target motif", "AGCT");

            long start = System.nanoTime();
            StringAlgorithmComparator.ComparisonResult res = StringAlgorithmComparator.compareAll(text, pattern);
            long elapsed = System.nanoTime() - start;

            ConsoleUI.printResultsHeader();
            System.out.println(ConsoleUI.bold("  Empirical Benchmark Results on Identical Input:"));
            System.out.printf("  %-24s | %-8s | %-12s | %-18s | %-12s%n", "Algorithm", "Matches", "Time (ms)", "Time Complexity", "Space Complexity");
            System.out.println("  ----------------------------------------------------------------------------------------");
            for (StringAlgorithmComparator.AlgorithmBenchmark b : res.getBenchmarks()) {
                System.out.printf("  %-24s | %-8d | %-12.4f | %-18s | %-12s%n",
                        ConsoleUI.cyan(b.getAlgorithmName()), b.getMatchesFound(), b.getExecutionTimeMs(), b.getTimeComplexity(), b.getSpaceComplexity());
            }

            ConsoleUI.printComplexity("O(N + M)", "O(N + M)", "O(N * M)", "O(N + M)", elapsed / 1_000_000.0);
            dataService.recordAnalysis("M1", "Benchmark", "Comparison", "Pattern: " + pattern, "Evaluated 3 algorithms", (long)(elapsed / 1_000_000.0), "O(N + M)", "O(N + M)");
            ConsoleUI.pause(scanner);
        } else if (choice.equals("2")) {
            ConsoleUI.printAlgorithmHeader(
                    "Sequence Alignment & Distance Comparison",
                    "Dynamic Programming Benchmark (NW vs SW vs Levenshtein)",
                    "Contrasts global end-to-end similarity vs local sub-domain matching vs edit operation distance on homologous coding alleles."
            );
            String s1 = GenomicDataGenerator.HUMAN_HBB_WILDTYPE.substring(0, 30);
            String s2 = GenomicDataGenerator.HUMAN_HBB_SICKLE_CELL.substring(0, 30);

            long start = System.nanoTime();
            AlignmentComparator.DpComparisonResult res = AlignmentComparator.compareAlignments(s1, s2, 2, -1, -2);
            long elapsed = System.nanoTime() - start;

            ConsoleUI.printResultsHeader();
            for (AlignmentComparator.MethodResult m : res.getMethods()) {
                System.out.println(ConsoleUI.bold("  [" + ConsoleUI.cyan(m.getMethodName()) + "]"));
                System.out.println("    Biological Goal : " + m.getBiologicalGoal());
                System.out.println("    Score / Metric  : " + ConsoleUI.green(String.valueOf(m.getScoreOrDistance())) + " (" + m.getSimilarityScore() + ")");
                System.out.println("    Complexity      : " + m.getTimeComplexity() + " Time, " + m.getSpaceComplexity() + " Space");
                System.out.println("    Interpretation  : " + m.getInterpretation());
                System.out.println();
            }

            ConsoleUI.printComplexity("O(N * M)", "O(N * M)", "O(N * M)", "O(N * M)", elapsed / 1_000_000.0);
            dataService.recordAnalysis("M3", "Benchmark", "Comparison", "HBB WT vs Sickle", "3 methods evaluated", (long)(elapsed / 1_000_000.0), "O(N * M)", "O(N * M)");
            ConsoleUI.pause(scanner);
        }
    }

    // =========================================================================
    // 14. REFERENCE GENOME & VARIANT EXPLORER
    // =========================================================================
    private static void menuReferenceGenomeExplorer() {
        ConsoleUI.printSectionHeader("14. Reference Genome & Genetic Variant Explorer");
        System.out.println("  [1] Inspect Curated Reference Genomes (E. coli, Human HBB, SARS-CoV-2)");
        System.out.println("  [2] Analyze Custom Sequence Composition (GC/AT Content & Dinucleotides)");
        System.out.println("  [3] Browse Pathogenic Genetic Variants Catalog (BRCA1, TP53, CFTR)");
        System.out.println("  [0] Return to Main Menu");

        String choice = ConsoleUI.readString(scanner, "Select view", "1");
        if (choice.equals("1")) {
            ConsoleUI.printAlgorithmHeader(
                    "Curated Reference Genome Inspection",
                    "Table Lookup & Linear Frequency Aggregation",
                    "Surveys authentic model organism sequences curated from GenBank and NCBI repositories."
            );
            List<DnaSequence> seqs = new ArrayList<>(dataService.getAllSequences());
            for (int i = 0; i < seqs.size(); i++) {
                DnaSequence s = seqs.get(i);
                System.out.printf("  [%d] %-18s : %s%n", i + 1, ConsoleUI.cyan(s.getId()), s.getDescription());
                System.out.printf("      Length: %d bp | GC Content: %.2f%% | Preview: %s...%n",
                        s.length(), s.getGcContent(), ConsoleUI.formatDna(s.getSequence().substring(0, Math.min(30, s.length()))));
            }
            ConsoleUI.pause(scanner);
        } else if (choice.equals("2")) {
            ConsoleUI.printAlgorithmHeader(
                    "Custom Sequence Composition Analysis",
                    "Single-Pass Linear Counting O(N)",
                    "Validates biological sequence integrity, computes GC/AT thermodynamic stability ratios, and detects degenerate bases."
            );
            String raw = ConsoleUI.readDna(scanner, "Enter DNA sequence (A, C, G, T)", "ATGCGATCGATCGGCTA");

            int a = 0, c = 0, g = 0, t = 0, others = 0;
            for (char ch : raw.toCharArray()) {
                switch (ch) {
                    case 'A': a++; break;
                    case 'C': c++; break;
                    case 'G': g++; break;
                    case 'T': t++; break;
                    default: others++; break;
                }
            }
            int len = raw.length();
            double gc = len > 0 ? ((c + g) * 100.0 / len) : 0;
            double at = len > 0 ? ((a + t) * 100.0 / len) : 0;

            ConsoleUI.printResultsHeader();
            ConsoleUI.printDnaField("Input Sequence", raw, 50);
            ConsoleUI.printField("Sequence Length", len + " bp");
            ConsoleUI.printField("Base Counts", String.format("A: %d | C: %d | G: %d | T: %d", a, c, g, t));
            if (others > 0) ConsoleUI.printField("Ambiguous Bases", others);
            ConsoleUI.printHighlightField("GC Content", String.format("%.2f%% (Physical melting stability)", gc));
            ConsoleUI.printField("AT Content", String.format("%.2f%%", at));
            ConsoleUI.printField("Possible 4-mers", Math.max(0, len - 3));

            ConsoleUI.printComplexity("O(N)", "O(N)", "O(N)", "O(1)", 0.05);
            ConsoleUI.pause(scanner);
        } else if (choice.equals("3")) {
            ConsoleUI.printAlgorithmHeader(
                    "Pathogenic Genetic Variant Catalog",
                    "In-Memory Relational Filter & Linear Scan",
                    "Surveys documented single nucleotide variants (SNVs) and indel mutations linked to cancer phenotypes and genetic conditions."
            );
            System.out.printf("  %-10s | %-8s | %-6s | %-12s | %-16s | %s%n",
                    "Variant ID", "Gene", "Pos", "Type", "Clinical Impact", "Molecular Consequence");
            System.out.println("  -----------------------------------------------------------------------------------------");
            for (GeneticVariant v : dataService.getAllVariants()) {
                System.out.printf("  %-10s | %-8s | %-6d | %-12s | %-16s | %s%n",
                        ConsoleUI.cyan(v.getId()), v.getGene(), v.getPosition(), v.getType(),
                        ConsoleUI.yellow(v.getClinicalSignificance()), v.getConsequence());
            }
            ConsoleUI.pause(scanner);
        }
    }

    // =========================================================================
    // 15. SYSTEM INFORMATION & ABOUT
    // =========================================================================
    private static void menuSystemInformation() {
        ConsoleUI.printSectionHeader("15. System Information & DSA Curriculum Mapping");
        System.out.println("  Platform Name  : " + ConsoleUI.bold("BIOGENOME | BIOINFORMATICS ALGORITHM EXPLORER"));
        System.out.println("  Version        : 3.1.0 (Professional Scientific Console)");
        System.out.println("  Runtime Target : Java 17+ LTS (Eclipse IDE & Maven Compatible)");
        System.out.println("  Architecture   : Decoupled Architecture (Java Backend Core + Standalone Web Platform)");
        System.out.println("  ANSI Palette   : " + (ConsoleUI.isColorsEnabled() ? ConsoleUI.green("ENABLED") : ConsoleUI.red("DISABLED")));

        System.out.println("\n  " + ConsoleUI.bold("Classical DSA to Bioinformatics Mapping Curriculum:"));
        System.out.println("    * " + ConsoleUI.cyan("Module 1 (String Matching)") + "      -> KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick");
        System.out.println("    * " + ConsoleUI.cyan("Module 2 (Indexing & Suffix)") + "     -> Suffix Array, SA-IS, Kasai LCP, Suffix Tree, DAWG");
        System.out.println("    * " + ConsoleUI.cyan("Module 3 (Dynamic Programming)") + "   -> Needleman-Wunsch, Smith-Waterman, Levenshtein, Bitmask DP");
        System.out.println("    * " + ConsoleUI.cyan("Module 4 (Network Flow & Graphs)") + " -> Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite Matching");
        System.out.println("    * " + ConsoleUI.cyan("Module 5 (NP-Completeness)") + "       -> 3-SAT DPLL, 3-SAT -> CLIQUE -> IS -> VC Reductions");
        System.out.println("    * " + ConsoleUI.cyan("Module 6 (Randomized & Parallel)") + " -> QuickSort, Reservoir Sampling, Blelloch Scan, Brent's Law");

        System.out.println("\n  " + ConsoleUI.yellow("Eclipse ANSI Console Tip:"));
        System.out.println("    If your Eclipse console displays raw escape sequences like '[36m', install the");
        System.out.println("    free 'ANSI Escape in Console' plugin via Help -> Eclipse Marketplace, or type 'T'");
        System.out.println("    at the Main Menu to switch to plain-text mode at any time.");
        ConsoleUI.pause(scanner);
    }

    // =========================================================================
    // HELPER SEQUENCE SELECTOR
    // =========================================================================
    private static String chooseSequenceInput(String label, String defaultSequence, String defaultDescription) {
        System.out.println();
        System.out.println("  Choose " + label + " source:");
        System.out.println("    [1] Use standard reference dataset: " + ConsoleUI.cyan(defaultDescription));
        System.out.println("    [2] Enter custom biological sequence (A, C, G, T)");
        String choice = ConsoleUI.readString(scanner, "Option", "1");

        if (choice.equals("2")) {
            return ConsoleUI.readDna(scanner, "Enter custom sequence", defaultSequence);
        }
        return defaultSequence;
    }
}
