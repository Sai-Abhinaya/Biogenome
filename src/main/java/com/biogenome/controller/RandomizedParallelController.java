package com.biogenome.controller;

import com.biogenome.model.ApiResponse;
import com.biogenome.model.ContinuousObservation;
import com.biogenome.model.DnaSequence;
import com.biogenome.randparallel.*;
import com.biogenome.service.BioGenomeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller – Module 6: Randomized & Parallel Algorithms.
 * Base URL: /api/v1/randparallel
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/v1/randparallel")
@CrossOrigin(origins = "*")
public class RandomizedParallelController {

    private final BioGenomeDataService dataService;

    @Autowired
    public RandomizedParallelController(BioGenomeDataService dataService) {
        this.dataService = dataService;
    }

    // ===== 6.1 Randomized QuickSort =====
    @PostMapping("/quicksort")
    public ResponseEntity<ApiResponse> runRandomizedQuickSort(@RequestBody(required = false) Map<String, Object> body) {
        try {
            String criterion = body != null ? (String) body.getOrDefault("criterion", "length") : "length";
            boolean ascending = body == null || (boolean) body.getOrDefault("ascending", true);

            List<RandomizedQuickSort.GenomeSortItem> items = new ArrayList<>();
            for (DnaSequence s : dataService.getAllSequences()) {
                double val;
                String formatted;
                switch (criterion.toLowerCase()) {
                    case "gc":
                        val = s.getGcContent();
                        formatted = String.format("%.2f%% GC", val);
                        break;
                    case "mutations":
                        // Fictional synthetic mutation estimate
                        val = (s.length() % 7) + 1;
                        formatted = String.format("%.0f mutations", val);
                        break;
                    case "confidence":
                        val = 90.0 + (s.length() % 100) / 10.0;
                        formatted = String.format("%.1f%% Q-Score", val);
                        break;
                    case "length":
                    default:
                        val = s.length();
                        formatted = String.format("%d bp", s.length());
                        break;
                }
                items.add(new RandomizedQuickSort.GenomeSortItem(s.getId(), s.getDescription(), val, criterion, formatted));
            }

            RandomizedQuickSort.QuickSortResult res = RandomizedQuickSort.sort(items, ascending);
            dataService.recordAnalysis("M6 - Rand/Parallel", "Randomized QuickSort", "Genome Records",
                    items.size() + " records sorted by " + criterion,
                    res.getTotalComparisons() + " comparisons, recursion depth: " + res.getMaxRecursionDepth(),
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(log N)");

            return ResponseEntity.ok(ApiResponse.ok("Randomized QuickSort", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("QuickSort", e.getMessage()));
        }
    }

    // ===== 6.2 Reservoir Sampling =====
    @PostMapping("/reservoir")
    public ResponseEntity<ApiResponse> runReservoirSampling(@RequestBody(required = false) Map<String, Object> body) {
        try {
            int k = 5;
            if (body != null && body.containsKey("capacity")) {
                k = (int) body.get("capacity");
            }

            List<ContinuousObservation> stream = dataService.getAllObservations();
            ReservoirSampling.ReservoirResult res = ReservoirSampling.sampleStream(stream, k);

            dataService.recordAnalysis("M6 - Rand/Parallel", "Reservoir Sampling", "Continuous Stream",
                    stream.size() + " observations, reservoir size k = " + k,
                    "Sampled " + res.getFinalSample().size() + " items, uniform probability = " +
                            String.format("%.4f", res.getTheoreticalProbability()),
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(k)");

            return ResponseEntity.ok(ApiResponse.ok("Reservoir Sampling", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Reservoir Sampling", e.getMessage()));
        }
    }

    // ===== 6.3 Miller-Rabin Primality Test =====
    @PostMapping("/miller-rabin")
    public ResponseEntity<ApiResponse> runMillerRabin(@RequestBody(required = false) Map<String, Object> body) {
        try {
            String number = "1000000007";
            int rounds = 10;
            if (body != null) {
                if (body.containsKey("number")) number = body.get("number").toString();
                if (body.containsKey("rounds")) rounds = Integer.parseInt(body.get("rounds").toString());
            }

            MillerRabinPrimality.PrimalityResult res = MillerRabinPrimality.testPrimality(number, rounds);
            dataService.recordAnalysis("M6 - Rand/Parallel", "Miller-Rabin Test", "Numerical Primality",
                    "n = " + number + " (" + rounds + " rounds)",
                    res.isPrime() ? "PROBABLY PRIME (Error <= " + String.format("%.2e", res.getMaxErrorProbability()) + ")"
                            : "COMPOSITE (Witness: " + res.getWitness() + ")",
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(log n)");

            return ResponseEntity.ok(ApiResponse.ok("Miller-Rabin Primality Test", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Miller-Rabin", e.getMessage()));
        }
    }

    // ===== 6.4 Blelloch Parallel Prefix Scan =====
    @PostMapping("/blelloch-scan")
    public ResponseEntity<ApiResponse> runBlellochScan(@RequestBody(required = false) Map<String, Object> body) {
        try {
            long[] input = {3, 1, 7, 0, 4, 1, 6, 3}; // Sample base read depths
            if (body != null && body.containsKey("values")) {
                @SuppressWarnings("unchecked")
                List<Number> list = (List<Number>) body.get("values");
                input = new long[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    input[i] = list.get(i).longValue();
                }
            }

            BlellochScan.ScanResult res = BlellochScan.scan(input);
            dataService.recordAnalysis("M6 - Rand/Parallel", "Blelloch Parallel Scan", "Genomic Array",
                    input.length + " values (padded to " + res.getPaddedLength() + ")",
                    "Work: " + res.getWorkOperations() + " ops, Span: " + res.getSpanSteps() + " steps",
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(N)");

            return ResponseEntity.ok(ApiResponse.ok("Blelloch Parallel Prefix Scan", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Blelloch Scan", e.getMessage()));
        }
    }

    // ===== 6.5 Parallel Reduce =====
    @PostMapping("/parallel-reduce")
    public ResponseEntity<ApiResponse> runParallelReduce(@RequestBody(required = false) Map<String, Object> body) {
        try {
            long[] input = {100, 200, 150, 300, 250, 400, 180, 220}; // Sample sequence lengths
            if (body != null && body.containsKey("values")) {
                @SuppressWarnings("unchecked")
                List<Number> list = (List<Number>) body.get("values");
                input = new long[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    input[i] = list.get(i).longValue();
                }
            }

            ParallelReduce.ReduceResult res = ParallelReduce.reduce(input);
            dataService.recordAnalysis("M6 - Rand/Parallel", "Parallel Reduce", "Sequence Lengths",
                    input.length + " items reduced",
                    "Final Sum: " + res.getFinalResult() + " (Work: " + res.getWorkOperations() +
                            ", Span: " + res.getSpanSteps() + ")",
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(N)");

            return ResponseEntity.ok(ApiResponse.ok("Parallel Tree Reduce", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Parallel Reduce", e.getMessage()));
        }
    }

    // ===== 6.6 Brent's Theorem Calculator =====
    @PostMapping("/brents-law")
    public ResponseEntity<ApiResponse> runBrentsLaw(@RequestBody(required = false) Map<String, Object> body) {
        try {
            double w = 1000.0;
            double s = 50.0;
            int p = 16;
            if (body != null) {
                if (body.containsKey("work")) w = Double.parseDouble(body.get("work").toString());
                if (body.containsKey("span")) s = Double.parseDouble(body.get("span").toString());
                if (body.containsKey("processors")) p = Integer.parseInt(body.get("processors").toString());
            }

            BrentsLawCalculator.BrentResult res = BrentsLawCalculator.calculate(w, s, p);
            dataService.recordAnalysis("M6 - Rand/Parallel", "Brent's Theorem", "Cluster Sizing",
                    "W = " + w + ", S = " + s + ", P = " + p + " cores",
                    "Tp = " + String.format("%.2f", res.getEstimatedParallelTime()) +
                            ", Speedup = " + String.format("%.2fx", res.getSpeedup()) +
                            ", Efficiency = " + String.format("%.1f%%", res.getEfficiency() * 100),
                    1, res.getFormula(), "O(1)");

            return ResponseEntity.ok(ApiResponse.ok("Brent's Theorem Calculation", 1, res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Brent's Law", e.getMessage()));
        }
    }
}
