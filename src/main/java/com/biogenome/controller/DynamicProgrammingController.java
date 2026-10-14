package com.biogenome.controller;

import com.biogenome.dp.DamerauLevenshteinAligner;
import com.biogenome.dp.GenomicBitmaskDP;
import com.biogenome.dp.NeedlemanWunschAligner;
import com.biogenome.dp.SmithWatermanAligner;
import com.biogenome.model.AlignmentResult;
import com.biogenome.model.ApiResponse;
import com.biogenome.model.EditDistanceResult;
import com.biogenome.util.GenomicDataGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller – Module 3: Dynamic Programming Endpoints.
 * Base URL: /api/v1/align
 */
@RestController
@RequestMapping("/api/v1/align")
@CrossOrigin(origins = "*")
public class DynamicProgrammingController {

    // ===== 3.1 Needleman-Wunsch =====
    @PostMapping("/needleman-wunsch")
    public ResponseEntity<ApiResponse> runNeedlemanWunsch(@RequestBody Map<String, Object> body) {
        try {
            String seq1 = sanitize((String) body.getOrDefault("sequence1",
                    GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.substring(0, 20)));
            String seq2 = sanitize((String) body.getOrDefault("sequence2",
                    GenomicDataGenerator.SARS_COV2_SPIKE_RBD_VARIANT.substring(0, 20)));
            int matchScore  = parseIntSafe(body.get("matchScore"),  NeedlemanWunschAligner.DEFAULT_MATCH_SCORE);
            int mismatchPen = parseIntSafe(body.get("mismatchPenalty"), NeedlemanWunschAligner.DEFAULT_MISMATCH_PENALTY);
            int gapPen      = parseIntSafe(body.get("gapPenalty"), NeedlemanWunschAligner.DEFAULT_GAP_PENALTY);

            long start = System.currentTimeMillis();
            NeedlemanWunschAligner aligner = new NeedlemanWunschAligner(matchScore, mismatchPen, gapPen);
            AlignmentResult res = aligner.align(seq1, seq2);
            long elapsed = System.currentTimeMillis() - start;

            // Build mid-line string (pipe/dot/space)
            String midLine = buildMidLine(res.getAlignedSeq1(), res.getAlignedSeq2());

            // Cap DP grid to 40x40 for payload safety
            int[][] grid = null;
            if (seq1.length() <= 40 && seq2.length() <= 40) {
                grid = res.getScoreMatrix();
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence1", seq1);
            result.put("sequence2", seq2);
            result.put("alignedSeq1", res.getAlignedSeq1());
            result.put("alignedSeq2", res.getAlignedSeq2());
            result.put("midLine", midLine);
            result.put("score", res.getAlignmentScore());
            result.put("matchScore", matchScore);
            result.put("mismatchPenalty", mismatchPen);
            result.put("gapPenalty", gapPen);
            result.put("matches", res.getMatches());
            result.put("mismatches", res.getMismatches());
            result.put("gaps", res.getGaps());
            result.put("identity", String.format("%.1f%%", res.getIdentityPercentage()));
            result.put("cigar", res.getCigarString());
            if (grid != null) result.put("dpGrid", grid);
            result.put("complexity", "O(N * M) time & space");

            return ResponseEntity.ok(ApiResponse.ok("Needleman-Wunsch Global Alignment", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Needleman-Wunsch", e.getMessage()));
        }
    }

    // ===== 3.2 Smith-Waterman =====
    @PostMapping("/smith-waterman")
    public ResponseEntity<ApiResponse> runSmithWaterman(@RequestBody Map<String, Object> body) {
        try {
            String seq1 = sanitize((String) body.getOrDefault("sequence1",
                    GenomicDataGenerator.SARS_COV2_SPIKE_RBD_WT.substring(0, 30)));
            String seq2 = sanitize((String) body.getOrDefault("sequence2",
                    GenomicDataGenerator.SARS_COV2_SPIKE_RBD_VARIANT.substring(5, 35)));
            int matchScore  = parseIntSafe(body.get("matchScore"),  SmithWatermanAligner.DEFAULT_MATCH_SCORE);
            int mismatchPen = parseIntSafe(body.get("mismatchPenalty"), SmithWatermanAligner.DEFAULT_MISMATCH_PENALTY);
            int gapPen      = parseIntSafe(body.get("gapPenalty"), SmithWatermanAligner.DEFAULT_GAP_PENALTY);

            long start = System.currentTimeMillis();
            SmithWatermanAligner aligner = new SmithWatermanAligner(matchScore, mismatchPen, gapPen);
            AlignmentResult res = aligner.align(seq1, seq2);
            long elapsed = System.currentTimeMillis() - start;

            String midLine = buildMidLine(res.getAlignedSeq1(), res.getAlignedSeq2());
            int[][] grid = null;
            if (seq1.length() <= 40 && seq2.length() <= 40) {
                grid = res.getScoreMatrix();
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence1", seq1);
            result.put("sequence2", seq2);
            result.put("localSeq1", res.getAlignedSeq1());
            result.put("localSeq2", res.getAlignedSeq2());
            result.put("midLine", midLine);
            result.put("score", res.getAlignmentScore());
            result.put("localStartSeq1", res.getStartPosSeq1());
            result.put("localEndSeq1", res.getEndPosSeq1());
            result.put("localStartSeq2", res.getStartPosSeq2());
            result.put("localEndSeq2", res.getEndPosSeq2());
            result.put("matches", res.getMatches());
            result.put("mismatches", res.getMismatches());
            result.put("gaps", res.getGaps());
            result.put("identity", String.format("%.1f%%", res.getIdentityPercentage()));
            result.put("cigar", res.getCigarString());
            if (grid != null) result.put("dpGrid", grid);
            result.put("complexity", "O(N * M) time & space");

            return ResponseEntity.ok(ApiResponse.ok("Smith-Waterman Local Alignment", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Smith-Waterman", e.getMessage()));
        }
    }

    // ===== 3.3 Edit Distance (Damerau-Levenshtein) =====
    @PostMapping("/edit-distance")
    public ResponseEntity<ApiResponse> runEditDistance(@RequestBody Map<String, String> body) {
        try {
            String seq1 = sanitize(body.getOrDefault("sequence1", "ACGTACGT"));
            String seq2 = sanitize(body.getOrDefault("sequence2", "ACGTAGGT"));

            long start = System.currentTimeMillis();
            EditDistanceResult res = DamerauLevenshteinAligner.align(seq1, seq2);
            long elapsed = System.currentTimeMillis() - start;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sequence1", seq1);
            result.put("sequence2", seq2);
            result.put("editDistance", res.getDistance());
            result.put("alignedSeq1", res.getAlignedQuery());
            result.put("alignedSeq2", res.getAlignedTarget());
            result.put("cigar", res.getCigarString());
            result.put("similarity", String.format("%.1f%%", res.getSimilarity()));
            result.put("operations", res.getOperations().stream().map(Object::toString).toList());
            result.put("complexity", "O(N * M) time, O(min(N,M)) space");

            return ResponseEntity.ok(ApiResponse.ok("Damerau-Levenshtein Edit Distance", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Edit Distance", e.getMessage()));
        }
    }

    // ===== 3.4 Bitmask DP Fragment Assembly =====
    @PostMapping("/bitmask-assembly")
    public ResponseEntity<ApiResponse> runBitmaskAssembly(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<String> rawFragments = (List<String>) body.get("fragments");
            List<String> fragments;
            if (rawFragments != null && !rawFragments.isEmpty()) {
                fragments = rawFragments.stream().map(this::sanitize).toList();
            } else {
                fragments = List.of("ATCGGA", "GAATCG", "TCGGAA", "AATCGG");
            }

            long start = System.currentTimeMillis();
            GenomicBitmaskDP.AssemblyResult res = GenomicBitmaskDP.assembleShortestCommonSuperstring(fragments);
            long elapsed = System.currentTimeMillis() - start;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("fragments", fragments);
            result.put("fragmentCount", fragments.size());
            result.put("assembledGenome", res.getAssembledSequence());
            result.put("assemblyOrder", res.getReadOrder());
            result.put("assembledLength", res.getTotalLength());
            result.put("overlapSaved", res.getOverlapSaved());
            result.put("complexity", "O(2^K * K^2) – Bitmask DP / TSP formulation");

            return ResponseEntity.ok(ApiResponse.ok("Bitmask DP Fragment Assembly (SCS)", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Bitmask Assembly", e.getMessage()));
        }
    }

    // ===== 3.5 Alignment Comparison (Needleman-Wunsch vs Smith-Waterman vs Levenshtein) =====
    @PostMapping("/compare")
    public ResponseEntity<ApiResponse> compareAlignments(@RequestBody Map<String, Object> body) {
        try {
            String seq1 = sanitize((String) body.getOrDefault("sequence1",
                    GenomicDataGenerator.HUMAN_HBB_WILDTYPE.substring(0, 30)));
            String seq2 = sanitize((String) body.getOrDefault("sequence2",
                    GenomicDataGenerator.HUMAN_HBB_SICKLE_CELL.substring(0, 30)));
            int matchScore  = parseIntSafe(body.get("matchScore"),  2);
            int mismatchPen = parseIntSafe(body.get("mismatchPenalty"), -1);
            int gapPen      = parseIntSafe(body.get("gapPenalty"), -2);

            long start = System.currentTimeMillis();
            com.biogenome.dp.AlignmentComparator.DpComparisonResult res =
                    com.biogenome.dp.AlignmentComparator.compareAlignments(seq1, seq2, matchScore, mismatchPen, gapPen);
            long elapsed = System.currentTimeMillis() - start;

            return ResponseEntity.ok(ApiResponse.ok("Alignment Algorithm Comparison", elapsed, res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Alignment Comparison Failure", e.getMessage()));
        }
    }

    // ===== Helpers =====
    private String sanitize(String seq) {
        if (seq == null) return "";
        return seq.toUpperCase().replaceAll("[^ACGTN$-]", "").trim();
    }

    private int parseIntSafe(Object val, int defaultVal) {
        if (val == null) return defaultVal;
        try { return Integer.parseInt(val.toString()); }
        catch (NumberFormatException e) { return defaultVal; }
    }

    private String buildMidLine(String a1, String a2) {
        StringBuilder mid = new StringBuilder();
        int len = Math.min(a1.length(), a2.length());
        for (int i = 0; i < len; i++) {
            char c1 = a1.charAt(i);
            char c2 = a2.charAt(i);
            if (c1 != '-' && c1 == c2) mid.append('|');
            else if (c1 == '-' || c2 == '-') mid.append(' ');
            else mid.append('.');
        }
        return mid.toString();
    }
}
