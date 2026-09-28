package com.biogenome.controller;

import com.biogenome.model.ApiResponse;
import com.biogenome.model.DnaSequence;
import com.biogenome.model.SuffixArrayResult;
import com.biogenome.suffix.KasaiLCP;
import com.biogenome.suffix.SuffixArray;
import com.biogenome.suffix.SuffixAutomaton;
import com.biogenome.suffix.SuffixTree;
import com.biogenome.util.GenomicDataGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller – Module 2: Suffix Structure Endpoints.
 * Base URL: /api/v1/suffix
 */
@RestController
@RequestMapping("/api/v1/suffix")
@CrossOrigin(origins = "*")
public class SuffixStructureController {

    // ===== 2.1 Suffix Array + Binary Search =====
    @PostMapping("/array")
    public ResponseEntity<ApiResponse> buildSuffixArray(@RequestBody Map<String, String> body) {
        try {
            String sequence = sanitize(body.getOrDefault("sequence", GenomicDataGenerator.ECOLI_ORIC_DNA.substring(0, 80)));
            String query = sanitize(body.getOrDefault("query", "ACCAC"));

            long start = System.currentTimeMillis();
            SuffixArray sa = new SuffixArray(sequence);
            List<Integer> matches = sa.searchMotif(query);
            long elapsed = System.currentTimeMillis() - start;

            int[] saArr = sa.getSuffixArray();
            List<Map<String, Object>> suffixTable = new ArrayList<>();
            int limit = Math.min(saArr.length, 30);
            for (int i = 0; i < limit; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("rank", i);
                row.put("suffixStart", saArr[i]);
                String suffix = sequence.substring(saArr[i]);
                row.put("suffix", suffix.length() > 30 ? suffix.substring(0, 30) + "..." : suffix);
                suffixTable.add(row);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence", sequence);
            result.put("sequenceLength", sequence.length());
            result.put("suffixArrayPreview", Arrays.toString(Arrays.copyOf(saArr, Math.min(15, saArr.length))));
            result.put("suffixTable", suffixTable);
            result.put("query", query);
            result.put("queryMatches", matches);
            result.put("queryMatchCount", matches.size());
            result.put("buildComplexity", "O(N) SA-IS");
            result.put("queryComplexity", "O(M log N) Binary Search");

            return ResponseEntity.ok(ApiResponse.ok("Suffix Array (SA-IS)", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Suffix Array", e.getMessage()));
        }
    }

    // ===== 2.2 Kasai LCP Array =====
    @PostMapping("/lcp")
    public ResponseEntity<ApiResponse> buildLCPArray(@RequestBody Map<String, String> body) {
        try {
            String sequence = sanitize(body.getOrDefault("sequence", GenomicDataGenerator.ECOLI_ORIC_DNA.substring(0, 80)));
            DnaSequence dnaSeq = new DnaSequence("input", sequence);

            long start = System.currentTimeMillis();
            SuffixArrayResult saRes = KasaiLCP.analyze(dnaSeq);
            long elapsed = System.currentTimeMillis() - start;

            int[] lcp = saRes.getLcpArray();
            int[] saArr  = saRes.getSuffixArray();
            int limit = Math.min(lcp.length, 40);

            List<Map<String, Object>> lcpData = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("rank", i);
                row.put("suffixStart", saArr[i]);
                String suffix = sequence.substring(saArr[i]);
                row.put("suffix", suffix.length() > 25 ? suffix.substring(0, 25) : suffix);
                row.put("lcpValue", lcp[i]);
                lcpData.add(row);
            }

            int maxLcp = Arrays.stream(lcp).max().orElse(0);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence", sequence);
            result.put("sequenceLength", sequence.length());
            result.put("lcpHistogramData", lcpData);
            result.put("lcpArrayPreview", Arrays.copyOf(lcp, Math.min(15, lcp.length)));
            result.put("maxLcpValue", maxLcp);
            result.put("longestRepeatedSubstrings", saRes.getLongestRepeatedSubstrings());
            result.put("complexity", "O(N) Kasai's Algorithm");

            return ResponseEntity.ok(ApiResponse.ok("Kasai LCP Array", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("LCP Array", e.getMessage()));
        }
    }

    // ===== 2.3 Suffix Automaton (DAWG) =====
    @PostMapping("/automaton")
    public ResponseEntity<ApiResponse> runSuffixAutomaton(@RequestBody Map<String, String> body) {
        try {
            String seq1 = sanitize(body.getOrDefault("sequence1",
                    GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.substring(0, 80)));
            String seq2 = sanitize(body.getOrDefault("sequence2",
                    GenomicDataGenerator.SARS_COV2_SPIKE_RBD_VARIANT.substring(0, 80)));
            String querySubstring = sanitize(body.getOrDefault("query", "GCATCT"));

            long start = System.currentTimeMillis();
            SuffixAutomaton sam = new SuffixAutomaton(seq1);
            long distinct = sam.countDistinctSubstrings();
            long occurrences = sam.countOccurrences(querySubstring);
            boolean containsQuery = sam.contains(querySubstring);
            String lcs = sam.findLongestCommonSubstring(seq2);
            long elapsed = System.currentTimeMillis() - start;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence1", seq1);
            result.put("sequence2", seq2);
            result.put("seq1Length", seq1.length());
            result.put("stateCount", sam.getStateCount());
            result.put("maxStates", 2 * seq1.length() - 1);
            result.put("distinctSubstrings", distinct);
            result.put("query", querySubstring);
            result.put("queryOccurrences", occurrences);
            result.put("queryFound", containsQuery);
            result.put("longestCommonSubstring", lcs);
            result.put("lcsLength", lcs.length());
            result.put("buildComplexity", "O(N * |Sigma|)");
            result.put("queryComplexity", "O(M)");
            result.put("lcsComplexity", "O(N1 + N2)");

            return ResponseEntity.ok(ApiResponse.ok("Suffix Automaton (DAWG)", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Suffix Automaton", e.getMessage()));
        }
    }

    // ===== 2.4 Suffix Tree =====
    @PostMapping("/tree")
    public ResponseEntity<ApiResponse> runSuffixTree(@RequestBody Map<String, String> body) {
        try {
            String sequence = sanitize(body.getOrDefault("sequence", "GATAGACAGATAGATA"));
            String query = sanitize(body.getOrDefault("query", "GATA"));

            long start = System.currentTimeMillis();
            SuffixTree tree = new SuffixTree(sequence);
            boolean found = tree.contains(query);
            long elapsed = System.currentTimeMillis() - start;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence", sequence);
            result.put("sequenceLength", sequence.length());
            result.put("totalNodes", tree.getTotalNodes());
            result.put("query", query);
            result.put("queryFound", found);
            result.put("complexity", "O(N) space / O(M) query");

            return ResponseEntity.ok(ApiResponse.ok("Suffix Tree", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Suffix Tree", e.getMessage()));
        }
    }

    private String sanitize(String seq) {
        if (seq == null) return "";
        return seq.toUpperCase().replaceAll("[^ACGTN$]", "").trim();
    }
}
