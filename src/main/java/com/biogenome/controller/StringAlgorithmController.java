package com.biogenome.controller;

import com.biogenome.model.ApiResponse;
import com.biogenome.string.AhoCorasickMatcher;
import com.biogenome.string.KMPMotifFinder;
import com.biogenome.string.RabinKarpMatcher;
import com.biogenome.string.ZAlgorithm;
import com.biogenome.util.GenomicDataGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller – Module 1: String Algorithm Endpoints.
 * Base URL: /api/v1/search
 */
@RestController
@RequestMapping("/api/v1/search")
@CrossOrigin(origins = "*")
public class StringAlgorithmController {

    // ===== 1.1 KMP =====
    @PostMapping("/kmp")
    public ResponseEntity<ApiResponse> runKMP(@RequestBody Map<String, String> body) {
        try {
            String sequence = sanitize(body.getOrDefault("sequence", GenomicDataGenerator.ECOLI_ORIC_DNA));
            String motif = sanitize(body.getOrDefault("motif", "AGCT"));

            long start = System.currentTimeMillis();
            int[] piTable = KMPMotifFinder.computePrefixFunction(motif);
            List<Integer> matches = KMPMotifFinder.searchMotif(sequence, motif);
            long elapsed = System.currentTimeMillis() - start;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence", sequence);
            result.put("motif", motif);
            result.put("sequenceLength", sequence.length());
            result.put("motifLength", motif.length());
            result.put("piTable", piTable);
            result.put("matchIndices", matches);
            result.put("matchCount", matches.size());
            result.put("complexity", "O(N + M)");
            result.put("highlighted", buildHighlightedSegments(sequence, matches, motif.length()));

            return ResponseEntity.ok(ApiResponse.ok("Knuth-Morris-Pratt (KMP)", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("KMP", e.getMessage()));
        }
    }

    // ===== 1.2 Z-Algorithm =====
    @PostMapping("/z-algorithm")
    public ResponseEntity<ApiResponse> runZAlgorithm(@RequestBody Map<String, String> body) {
        try {
            String sequence = sanitize(body.getOrDefault("sequence", GenomicDataGenerator.ECOLI_ORIC_DNA));
            String motif = sanitize(body.getOrDefault("motif", "ACCAC"));

            long start = System.currentTimeMillis();
            List<Integer> matches = ZAlgorithm.searchMotif(sequence, motif);
            String concat = motif + "$" + sequence;
            int[] zArray = ZAlgorithm.calculateZArray(concat);
            long elapsed = System.currentTimeMillis() - start;

            // Build Z-array for the sequence alone (excluding the motif prefix)
            int[] seqZArray = Arrays.copyOfRange(zArray, motif.length() + 1, Math.min(zArray.length, motif.length() + 1 + 60));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence", sequence);
            result.put("motif", motif);
            result.put("sequenceLength", sequence.length());
            result.put("zArrayPreview", seqZArray);
            result.put("matchIndices", matches);
            result.put("matchCount", matches.size());
            result.put("complexity", "O(N + M)");
            result.put("highlighted", buildHighlightedSegments(sequence, matches, motif.length()));

            return ResponseEntity.ok(ApiResponse.ok("Z-Algorithm", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Z-Algorithm", e.getMessage()));
        }
    }

    // ===== 1.3 Rabin-Karp =====
    @PostMapping("/rabin-karp")
    public ResponseEntity<ApiResponse> runRabinKarp(@RequestBody Map<String, String> body) {
        try {
            String sequence = sanitize(body.getOrDefault("sequence", GenomicDataGenerator.ECOLI_ORIC_DNA));
            String motif = sanitize(body.getOrDefault("motif", "AGCT"));

            long start = System.currentTimeMillis();
            RabinKarpMatcher.SearchStats stats = RabinKarpMatcher.searchWithDetailedStats(sequence, motif);
            long elapsed = System.currentTimeMillis() - start;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence", sequence);
            result.put("motif", motif);
            result.put("sequenceLength", sequence.length());
            result.put("matchIndices", stats.getMatches());
            result.put("matchCount", stats.getMatches().size());
            result.put("spuriousHits", stats.getSpuriousHits());
            result.put("hashComparisons", stats.getHashComparisons());
            result.put("complexity", "O(N + M) average, O(N*M) worst");
            result.put("highlighted", buildHighlightedSegments(sequence, stats.getMatches(), motif.length()));

            return ResponseEntity.ok(ApiResponse.ok("Rabin-Karp", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Rabin-Karp", e.getMessage()));
        }
    }

    // ===== 1.4 Aho-Corasick =====
    @PostMapping("/aho-corasick")
    public ResponseEntity<ApiResponse> runAhoCorasick(@RequestBody Map<String, Object> body) {
        try {
            String sequence = sanitize((String) body.getOrDefault("sequence", GenomicDataGenerator.ECOLI_ORIC_DNA));
            @SuppressWarnings("unchecked")
            List<Map<String, String>> motifList = (List<Map<String, String>>) body.get("motifs");

            AhoCorasickMatcher matcher = new AhoCorasickMatcher();
            if (motifList != null && !motifList.isEmpty()) {
                for (Map<String, String> m : motifList) {
                    matcher.addMotif(m.getOrDefault("name", "Motif"), sanitize(m.getOrDefault("pattern", "")));
                }
            } else {
                // Default: standard restriction enzymes
                GenomicDataGenerator.getRestrictionEnzymes().forEach(matcher::addMotif);
            }

            long start = System.currentTimeMillis();
            matcher.buildAutomaton();
            List<com.biogenome.model.MotifMatch> matches = matcher.search(sequence);
            long elapsed = System.currentTimeMillis() - start;

            List<Map<String, Object>> matchData = new ArrayList<>();
            for (var m : matches) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", m.getMotifName());
                item.put("pattern", m.getMotifPattern());
                item.put("startIndex", m.getStartIndex());
                item.put("endIndex", m.getEndIndex());
                item.put("oneBasedIndex", m.getOneBasedIndex());
                item.put("context", m.getContextSnippet());
                matchData.add(item);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence", sequence);
            result.put("sequenceLength", sequence.length());
            result.put("totalMatches", matches.size());
            result.put("matches", matchData);
            result.put("complexity", "O(N + sum(M_i) + Z)");

            return ResponseEntity.ok(ApiResponse.ok("Aho-Corasick", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Aho-Corasick", e.getMessage()));
        }
    }

    // ===== Datasets endpoint =====
    @GetMapping("/datasets")
    public ResponseEntity<Map<String, Object>> getDatasets() {
        Map<String, Object> datasets = new LinkedHashMap<>();
        datasets.put("ecoli_oric", Map.of("name", "E. coli K-12 oriC", "sequence", GenomicDataGenerator.ECOLI_ORIC_DNA, "length", GenomicDataGenerator.ECOLI_ORIC_DNA.length()));
        datasets.put("sars_cov2_spike", Map.of("name", "SARS-CoV-2 Spike RBD", "sequence", GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT, "length", GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.length()));
        datasets.put("hbb_wildtype", Map.of("name", "Human HBB Wildtype", "sequence", GenomicDataGenerator.HUMAN_HBB_WILDTYPE, "length", GenomicDataGenerator.HUMAN_HBB_WILDTYPE.length()));
        datasets.put("restriction_enzymes", GenomicDataGenerator.getRestrictionEnzymes());
        return ResponseEntity.ok(datasets);
    }

    // ===== 1.5 Algorithm Comparison (KMP vs Z vs Rabin-Karp) =====
    @PostMapping("/compare")
    public ResponseEntity<ApiResponse> compareAlgorithms(@RequestBody Map<String, String> body) {
        try {
            String sequence = sanitize(body.getOrDefault("sequence", GenomicDataGenerator.ECOLI_ORIC_DNA));
            String motif = sanitize(body.getOrDefault("motif", "AGCT"));

            long start = System.currentTimeMillis();
            com.biogenome.string.StringAlgorithmComparator.ComparisonResult res =
                    com.biogenome.string.StringAlgorithmComparator.compareAll(sequence, motif);
            long elapsed = System.currentTimeMillis() - start;

            return ResponseEntity.ok(ApiResponse.ok("Motif Search Algorithm Comparison", elapsed, res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Comparison Failure", e.getMessage()));
        }
    }

    // ===== Helpers =====
    private String sanitize(String seq) {
        if (seq == null) return "";
        return seq.toUpperCase().replaceAll("[^ACGTN$]", "").trim();
    }

    private List<Map<String, Object>> buildHighlightedSegments(String seq, List<Integer> matchIndices, int motifLen) {
        List<Map<String, Object>> segments = new ArrayList<>();
        Set<Integer> matchSet = new HashSet<>(matchIndices);
        int n = seq.length();
        int i = 0;
        while (i < n) {
            if (matchSet.contains(i)) {
                Map<String, Object> seg = new LinkedHashMap<>();
                seg.put("type", "match");
                seg.put("start", i);
                seg.put("end", i + motifLen);
                seg.put("text", seq.substring(i, Math.min(n, i + motifLen)));
                segments.add(seg);
                i += motifLen;
            } else {
                int nextMatch = Integer.MAX_VALUE;
                for (int idx : matchIndices) {
                    if (idx > i) { nextMatch = Math.min(nextMatch, idx); break; }
                }
                int end = nextMatch == Integer.MAX_VALUE ? n : nextMatch;
                Map<String, Object> seg = new LinkedHashMap<>();
                seg.put("type", "normal");
                seg.put("start", i);
                seg.put("end", end);
                seg.put("text", seq.substring(i, end));
                segments.add(seg);
                i = end;
            }
        }
        return segments;
    }
}
