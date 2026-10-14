package com.biogenome.controller;

import com.biogenome.model.*;
import com.biogenome.service.BioGenomeDataService;
import com.biogenome.string.KMPMotifFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller – Genome Explorer & Analytical Data Services.
 * Base URL: /api/v1/genome
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/v1/genome")
@CrossOrigin(origins = "*")
public class GenomeExplorerController {

    private final BioGenomeDataService dataService;

    @Autowired
    public GenomeExplorerController(BioGenomeDataService dataService) {
        this.dataService = dataService;
    }

    // ===== Sequences =====
    @GetMapping("/sequences")
    public ResponseEntity<ApiResponse> getAllSequences() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DnaSequence seq : dataService.getAllSequences()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", seq.getId());
            item.put("description", seq.getDescription());
            item.put("length", seq.length());
            item.put("gcContent", String.format("%.2f%%", seq.getGcContent()));
            item.put("sequence", seq.getSequence());
            list.add(item);
        }
        return ResponseEntity.ok(ApiResponse.ok("Genomic Sequences", 1, list));
    }

    // ===== DNA Sequence Analysis =====
    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse> analyzeSequence(@RequestBody Map<String, String> body) {
        try {
            long start = System.currentTimeMillis();
            String raw = body.getOrDefault("sequence", "");
            String id = body.getOrDefault("id", "QUERY_SEQ");
            String desc = body.getOrDefault("description", "User Analysis Input");

            if (raw == null || raw.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Validation", "DNA sequence cannot be empty."));
            }

            // DnaSequence constructor validates IUPAC nucleotide characters
            DnaSequence seq = new DnaSequence(id, desc, raw);
            Map<Character, Integer> counts = seq.getNucleotideCounts();
            int len = seq.length();

            int a = counts.getOrDefault('A', 0);
            int c = counts.getOrDefault('C', 0);
            int g = counts.getOrDefault('G', 0);
            int t = counts.getOrDefault('T', 0);
            int n = counts.getOrDefault('N', 0);

            double gcPct = len > 0 ? ((g + c) * 100.0) / len : 0.0;
            double atPct = len > 0 ? ((a + t) * 100.0) / len : 0.0;

            // Search standard core motifs (TATA-box, CpG dinucleotide, Start/Stop codons)
            int tataMatches = KMPMotifFinder.searchMotif(seq.getSequence(), "TATAAA").size();
            int cpgMatches = KMPMotifFinder.searchMotif(seq.getSequence(), "CG").size();
            int startCodons = KMPMotifFinder.searchMotif(seq.getSequence(), "ATG").size();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("id", seq.getId());
            result.put("description", seq.getDescription());
            result.put("sequence", seq.getSequence());
            result.put("length", len);
            result.put("countA", a);
            result.put("countC", c);
            result.put("countG", g);
            result.put("countT", t);
            result.put("countN", n);
            result.put("gcContent", String.format("%.2f%%", gcPct));
            result.put("atContent", String.format("%.2f%%", atPct));
            result.put("gcPercentage", gcPct);
            result.put("atPercentage", atPct);
            result.put("reverseComplement", seq.getReverseComplement().getSequence());
            result.put("transcribedRna", seq.transcribeToRna());
            result.put("tataBoxCount", tataMatches);
            result.put("cpgIslandCount", cpgMatches);
            result.put("startCodonCount", startCodons);

            long elapsed = System.currentTimeMillis() - start;
            dataService.recordAnalysis("Explorer", "DNA Analysis", "Sequence",
                    id + " (" + len + " bp)", String.format("GC: %.1f%%, AT: %.1f%%", gcPct, atPct),
                    elapsed, "O(N)", "O(N)");

            return ResponseEntity.ok(ApiResponse.ok("DNA Analysis", elapsed, result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Analysis Failure", e.getMessage()));
        }
    }

    // ===== Global Statistics =====
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getPlatformStatistics() {
        Collection<DnaSequence> seqs = dataService.getAllSequences();
        long totalBases = 0;
        double sumGc = 0;
        double maxGc = 0;
        double minGc = 100;

        for (DnaSequence s : seqs) {
            totalBases += s.length();
            double gc = s.getGcContent();
            sumGc += gc;
            if (gc > maxGc) maxGc = gc;
            if (gc < minGc) minGc = gc;
        }

        double avgLength = seqs.isEmpty() ? 0 : (double) totalBases / seqs.size();
        double avgGc = seqs.isEmpty() ? 0 : sumGc / seqs.size();
        double avgAt = 100.0 - avgGc;

        List<GeneticVariant> varList = dataService.getAllVariants();
        long subs = varList.stream().filter(v -> v.getType().equalsIgnoreCase("SNP") || v.getType().equalsIgnoreCase("Missense")).count();
        long ins = varList.stream().filter(v -> v.getType().equalsIgnoreCase("Insertion")).count();
        long dels = varList.stream().filter(v -> v.getType().equalsIgnoreCase("Deletion") || v.getType().equalsIgnoreCase("Frameshift")).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalSequences", seqs.size());
        stats.put("totalBases", totalBases);
        stats.put("averageSequenceLength", Math.round(avgLength));
        stats.put("averageGcContent", String.format("%.2f%%", avgGc));
        stats.put("averageAtContent", String.format("%.2f%%", avgAt));
        stats.put("highestGcContent", String.format("%.2f%%", maxGc));
        stats.put("lowestGcContent", String.format("%.2f%%", minGc));
        stats.put("totalVariants", varList.size());
        stats.put("substitutionVariants", subs);
        stats.put("insertionVariants", ins);
        stats.put("deletionVariants", dels);
        stats.put("totalResearchers", dataService.getAllResearchers().size());
        stats.put("totalExperiments", dataService.getAllExperiments().size());
        stats.put("totalResources", dataService.getAllResources().size());
        stats.put("totalObservations", dataService.getAllObservations().size());
        stats.put("algorithmsAvailable", 18);

        return ResponseEntity.ok(ApiResponse.ok("Platform Statistics", 1, stats));
    }

    // ===== Genetic Variants Explorer =====
    @GetMapping("/variants")
    public ResponseEntity<ApiResponse> getVariants(
            @RequestParam(required = false) String gene,
            @RequestParam(required = false) String type) {

        List<GeneticVariant> all = dataService.getAllVariants();
        List<GeneticVariant> filtered = all.stream()
                .filter(v -> gene == null || gene.isEmpty() || v.getGene().equalsIgnoreCase(gene))
                .filter(v -> type == null || type.isEmpty() || v.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

        long snpCount = all.stream().filter(v -> v.getType().equalsIgnoreCase("SNP") || v.getType().equalsIgnoreCase("Missense")).count();
        long insCount = all.stream().filter(v -> v.getType().equalsIgnoreCase("Insertion")).count();
        long delCount = all.stream().filter(v -> v.getType().equalsIgnoreCase("Deletion") || v.getType().equalsIgnoreCase("Frameshift")).count();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("variants", filtered);
        data.put("totalVariants", all.size());
        data.put("filteredCount", filtered.size());
        data.put("substitutions", snpCount);
        data.put("insertions", insCount);
        data.put("deletions", delCount);

        return ResponseEntity.ok(ApiResponse.ok("Genetic Variants", 1, data));
    }

    // ===== Personnel & Resources =====
    @GetMapping("/personnel-resources")
    public ResponseEntity<ApiResponse> getPersonnelAndResources() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("researchers", dataService.getAllResearchers());
        data.put("experiments", dataService.getAllExperiments());
        data.put("resources", dataService.getAllResources());
        data.put("observations", dataService.getAllObservations());
        return ResponseEntity.ok(ApiResponse.ok("Personnel & Resources", 1, data));
    }

    // ===== Global Search =====
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> globalSearch(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Search", "Query parameter 'q' required"));
        }
        String query = q.trim().toUpperCase();

        List<Map<String, String>> results = new ArrayList<>();

        // 1. Search Sequences
        for (DnaSequence s : dataService.getAllSequences()) {
            if (s.getId().toUpperCase().contains(query) || s.getDescription().toUpperCase().contains(query)) {
                results.add(Map.of("category", "Sequence", "title", s.getId(), "subtitle", s.getDescription()));
            } else if (query.matches("^[ACGTN]+$") && s.getSequence().contains(query)) {
                int count = KMPMotifFinder.searchMotif(s.getSequence(), query).size();
                results.add(Map.of("category", "DNA Motif Hit", "title", s.getId(),
                        "subtitle", "Contains " + count + " occurrences of motif " + query));
            }
        }

        // 2. Search Variants
        for (GeneticVariant v : dataService.getAllVariants()) {
            if (v.getId().toUpperCase().contains(query) || v.getGene().toUpperCase().contains(query)
                    || v.getConsequence().toUpperCase().contains(query) || v.getType().toUpperCase().contains(query)) {
                results.add(Map.of("category", "Variant", "title", v.getId() + " (" + v.getGene() + ")",
                        "subtitle", v.getChangeNotation() + " - " + v.getConsequence()));
            }
        }

        // 3. Search Researchers
        for (Researcher r : dataService.getAllResearchers()) {
            if (r.getName().toUpperCase().contains(query) || r.getSpecialization().toUpperCase().contains(query)) {
                results.add(Map.of("category", "Researcher", "title", r.getName(),
                        "subtitle", r.getSpecialization() + " (" + r.getExperienceLevel() + ")"));
            }
        }

        // 4. Search Experiments
        for (Experiment e : dataService.getAllExperiments()) {
            if (e.getName().toUpperCase().contains(query) || e.getRequiredSpecialization().toUpperCase().contains(query)) {
                results.add(Map.of("category", "Experiment", "title", e.getName(),
                        "subtitle", "Req: " + e.getRequiredSpecialization() + " [Priority: " + e.getPriority() + "]"));
            }
        }

        return ResponseEntity.ok(ApiResponse.ok("Global Search Results", 1, results));
    }
}
