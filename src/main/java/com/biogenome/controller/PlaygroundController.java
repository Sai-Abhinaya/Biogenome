package com.biogenome.controller;

import com.biogenome.dp.AlignmentComparator;
import com.biogenome.flow.DinicAlgorithm;
import com.biogenome.flow.FlowNetwork;
import com.biogenome.flow.FlowResult;
import com.biogenome.model.ApiResponse;
import com.biogenome.model.DnaSequence;
import com.biogenome.np.ThreeSatSolver;
import com.biogenome.randparallel.MillerRabinPrimality;
import com.biogenome.randparallel.ParallelReduce;
import com.biogenome.service.BioGenomeDataService;
import com.biogenome.string.StringAlgorithmComparator;
import com.biogenome.suffix.RepeatedPatternExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller – Universal Algorithm Playground.
 * Central demonstration arena allowing execution across all 6 modules.
 * Base URL: /api/v1/playground
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/v1/playground")
@CrossOrigin(origins = "*")
public class PlaygroundController {

    private final BioGenomeDataService dataService;

    @Autowired
    public PlaygroundController(BioGenomeDataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/run")
    public ResponseEntity<ApiResponse> runPlayground(@RequestBody Map<String, Object> body) {
        try {
            String module = (String) body.getOrDefault("module", "M1");
            String algorithm = (String) body.getOrDefault("algorithm", "KMP");
            String datasetKey = (String) body.getOrDefault("dataset", "ECOLI_ORIC");
            String customInput = (String) body.getOrDefault("customInput", "");

            long start = System.currentTimeMillis();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("module", module);
            result.put("algorithm", algorithm);
            result.put("dataset", datasetKey);

            // Fetch sequence if applicable
            DnaSequence seqObj = dataService.getSequence(datasetKey);
            String seq = (seqObj != null) ? seqObj.getSequence() : (customInput.isEmpty() ? "ATGCGATCGATCGGCTA" : customInput);

            switch (algorithm.toUpperCase()) {
                case "KMP":
                case "Z-ALGORITHM":
                case "RABIN-KARP": {
                    String pattern = customInput.isEmpty() ? "AGCT" : customInput;
                    StringAlgorithmComparator.ComparisonResult comp = StringAlgorithmComparator.compareAll(seq, pattern);
                    result.put("input", "Sequence: " + seq.substring(0, Math.min(60, seq.length())) + "... | Pattern: " + pattern);
                    result.put("process", "Evaluated via " + algorithm + " across length " + seq.length());
                    result.put("data", comp);
                    result.put("complexity", "O(N + M) Time, O(M) Space");
                    result.put("biologicalInterpretation", "Exact genomic motif localization for restriction enzyme or transcription factor binding site identification.");
                    break;
                }
                case "SUFFIX ARRAY":
                case "KASAI LCP":
                case "REPEATS": {
                    RepeatedPatternExplorer.RepeatExplorerResult rRes = RepeatedPatternExplorer.exploreRepeats(seq, 3);
                    result.put("input", "Sequence (" + seq.length() + " bp)");
                    result.put("process", "SA-IS Linear Construction + Kasai LCP Lemma");
                    result.put("data", rRes);
                    result.put("complexity", "O(N) Linear Time & Space");
                    result.put("biologicalInterpretation", "Discovers tandem repeats, micro-satellites, and mobile genetic elements without prior motif knowledge.");
                    break;
                }
                case "NEEDLEMAN-WUNSCH":
                case "SMITH-WATERMAN":
                case "LEVENSHTEIN": {
                    String seq2 = customInput.isEmpty() ? (seq.length() > 20 ? seq.substring(2, 22) : "ATGACGTAGCT") : customInput;
                    AlignmentComparator.DpComparisonResult dpRes = AlignmentComparator.compareAlignments(seq.substring(0, Math.min(30, seq.length())), seq2, 2, -1, -2);
                    result.put("input", "Seq A (" + seq.length() + " bp) vs Seq B (" + seq2.length() + " bp)");
                    result.put("process", "2D Dynamic Programming Recurrence with Optimal Traceback");
                    result.put("data", dpRes);
                    result.put("complexity", "O(N * M) Time & Space");
                    result.put("biologicalInterpretation", "Quantifies evolutionary conservation, mutational distance, and homology between genetic sequences.");
                    break;
                }
                case "DINIC":
                case "FORD-FULKERSON":
                case "NETWORK FLOW": {
                    FlowNetwork net = dataService.buildSampleResourceNetwork();
                    FlowResult fRes = DinicAlgorithm.computeMaxFlow(net, 0, net.getVertexCount() - 1);
                    result.put("input", "10-Node Multi-Tier Bio-Resource Network");
                    result.put("process", "BFS Level Graph + DFS Blocking Flow Phases");
                    result.put("data", fRes);
                    result.put("complexity", "O(V^2 * E) Time");
                    result.put("biologicalInterpretation", "Optimizes laboratory reagent distribution and identifies critical throughput bottlenecks.");
                    break;
                }
                case "3-SAT":
                case "NP-COMPLETENESS": {
                    ThreeSatSolver.SatResult satRes = ThreeSatSolver.solve(dataService.getSample3SatFormula());
                    result.put("input", "Gene Regulatory Switch CNF (4 Clauses, 6 Variables)");
                    result.put("process", "DPLL Backtracking with Conflict-Driven Backtracking");
                    result.put("data", satRes);
                    result.put("complexity", "O(2^N * M) NP-Complete");
                    result.put("biologicalInterpretation", "Determines whether synthetic gene circuit feedback loops can achieve stable state expression.");
                    break;
                }
                case "MILLER-RABIN": {
                    String val = customInput.isEmpty() ? "1000000007" : customInput;
                    MillerRabinPrimality.PrimalityResult pRes = MillerRabinPrimality.testPrimality(val, 10);
                    result.put("input", "Integer n = " + val);
                    result.put("process", "Randomized Modular Exponentiation Witness Testing");
                    result.put("data", pRes);
                    result.put("complexity", "O(k * log^3 n) Time");
                    result.put("biologicalInterpretation", "Generates mathematically secure large prime moduli for genomic rolling hash indices.");
                    break;
                }
                case "PARALLEL REDUCE":
                default: {
                    long[] arr = {100, 200, 150, 300, 250, 400, 180, 220};
                    ParallelReduce.ReduceResult redRes = ParallelReduce.reduce(arr);
                    result.put("input", "Contig lengths: " + Arrays.toString(arr));
                    result.put("process", "Binary Tree Parallel Reduction across log2(N) levels");
                    result.put("data", redRes);
                    result.put("complexity", "Work O(N), Span O(log N)");
                    result.put("biologicalInterpretation", "Enables real-time genome-wide depth aggregation across distributed compute nodes.");
                    break;
                }
            }

            long elapsed = System.currentTimeMillis() - start;
            result.put("executionTimeMs", elapsed);

            dataService.recordAnalysis("Playground", algorithm, "Interactive Run",
                    datasetKey + (customInput.isEmpty() ? "" : " + Custom Input"),
                    "Executed in " + elapsed + " ms", elapsed, "Module Standard", "Module Standard");

            return ResponseEntity.ok(ApiResponse.ok("Playground Execution", elapsed, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Playground", e.getMessage()));
        }
    }
}
