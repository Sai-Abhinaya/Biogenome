package com.biogenome.controller;

import com.biogenome.model.ApiResponse;
import com.biogenome.np.*;
import com.biogenome.service.BioGenomeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller – Module 5: NP-Completeness Reductions & Approximation Algorithms.
 * Base URL: /api/v1/np
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/v1/np")
@CrossOrigin(origins = "*")
public class NpCompletenessController {

    private final BioGenomeDataService dataService;

    @Autowired
    public NpCompletenessController(BioGenomeDataService dataService) {
        this.dataService = dataService;
    }

    // ===== 5.1 SAT / 3-SAT Solver =====
    @PostMapping("/3sat")
    public ResponseEntity<ApiResponse> solve3Sat(@RequestBody(required = false) Map<String, String> body) {
        try {
            List<SatClause> clauses;
            if (body != null && body.containsKey("cnf")) {
                clauses = ThreeSatSolver.parseCnf(body.get("cnf"));
            } else {
                clauses = dataService.getSample3SatFormula();
            }

            ThreeSatSolver.SatResult res = ThreeSatSolver.solve(clauses);
            dataService.recordAnalysis("M5 - NP", "3-SAT Solver", "Biological Constraints",
                    clauses.size() + " clauses, " + res.getVariables().size() + " variables",
                    res.isSatisfiable() ? "SATISFIABLE (Model: " + res.getModel() + ")" : "UNSATISFIABLE",
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(N + M)");

            return ResponseEntity.ok(ApiResponse.ok("3-SAT Constraint Solver", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("3-SAT", e.getMessage()));
        }
    }

    // ===== 5.2 3-SAT -> CLIQUE Reduction =====
    @PostMapping("/reduction/sat-to-clique")
    public ResponseEntity<ApiResponse> reduceSatToClique(@RequestBody(required = false) Map<String, String> body) {
        try {
            List<SatClause> clauses;
            if (body != null && body.containsKey("cnf")) {
                clauses = ThreeSatSolver.parseCnf(body.get("cnf"));
            } else {
                clauses = dataService.getSample3SatFormula();
            }

            SatToCliqueReduction.ReductionResult res = SatToCliqueReduction.reduceAndSolve(clauses);
            dataService.recordAnalysis("M5 - NP", "3-SAT -> CLIQUE", "Karp Reduction",
                    clauses.size() + " clauses -> " + res.getNodes().size() + " vertices, " + res.getEdges().size() + " edges",
                    "Required Clique: k = " + res.getRequiredCliqueSize() + " | Found: " + res.getFoundClique().size() +
                            (res.isSatisfied() ? " (SAT)" : " (UNSAT)"),
                    Math.round(res.getExecutionTimeMs()), "O(M^2) Reduction", "O(M^2)");

            return ResponseEntity.ok(ApiResponse.ok("3-SAT to CLIQUE Reduction", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("SAT to CLIQUE", e.getMessage()));
        }
    }

    // ===== 5.3 CLIQUE -> INDEPENDENT SET Reduction =====
    @PostMapping("/reduction/clique-to-is")
    public ResponseEntity<ApiResponse> reduceCliqueToIs(@RequestBody(required = false) Map<String, Object> body) {
        try {
            int n = 6;
            List<String> names = Arrays.asList("GeneA", "GeneB", "GeneC", "GeneD", "GeneE", "GeneF");
            List<int[]> edges = new ArrayList<>();
            // Sample biological interaction graph
            edges.add(new int[]{0, 1});
            edges.add(new int[]{1, 2});
            edges.add(new int[]{0, 2}); // Triangle {0, 1, 2}
            edges.add(new int[]{2, 3});
            edges.add(new int[]{3, 4});
            edges.add(new int[]{4, 5});
            edges.add(new int[]{3, 5}); // Triangle {3, 4, 5}

            if (body != null && body.containsKey("edges") && body.containsKey("vertexCount")) {
                n = (int) body.get("vertexCount");
                @SuppressWarnings("unchecked")
                List<String> vNames = (List<String>) body.get("vertexNames");
                if (vNames != null) names = vNames;

                @SuppressWarnings("unchecked")
                List<List<Integer>> rawEdges = (List<List<Integer>>) body.get("edges");
                edges.clear();
                for (List<Integer> e : rawEdges) {
                    edges.add(new int[]{e.get(0), e.get(1)});
                }
            }

            CliqueToIndependentSet.IndependentSetResult res = CliqueToIndependentSet.reduceAndSolve(n, names, edges);
            dataService.recordAnalysis("M5 - NP", "CLIQUE -> Independent Set", "Graph Duality",
                    n + " Vertices, " + edges.size() + " Edges -> Complement: " + res.getComplementEdges().size() + " Edges",
                    "Max Clique: " + res.getMaxCliqueInOriginal().size() + " <=> Max Independent Set: " + res.getSize(),
                    Math.round(res.getExecutionTimeMs()), res.getReductionComplexity(), "O(V^2)");

            return ResponseEntity.ok(ApiResponse.ok("CLIQUE to Independent Set Reduction", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("CLIQUE to Independent Set", e.getMessage()));
        }
    }

    // ===== 5.4 INDEPENDENT SET -> VERTEX COVER Duality =====
    @PostMapping("/reduction/is-to-vc")
    public ResponseEntity<ApiResponse> reduceIsToVc(@RequestBody(required = false) Map<String, Object> body) {
        try {
            int n = 6;
            List<String> names = Arrays.asList("TargetA", "TargetB", "TargetC", "TargetD", "TargetE", "TargetF");
            List<int[]> edges = new ArrayList<>();
            edges.add(new int[]{0, 1});
            edges.add(new int[]{1, 2});
            edges.add(new int[]{0, 2});
            edges.add(new int[]{2, 3});
            edges.add(new int[]{3, 4});
            edges.add(new int[]{4, 5});
            edges.add(new int[]{3, 5});

            if (body != null && body.containsKey("edges") && body.containsKey("vertexCount")) {
                n = (int) body.get("vertexCount");
                @SuppressWarnings("unchecked")
                List<String> vNames = (List<String>) body.get("vertexNames");
                if (vNames != null) names = vNames;

                @SuppressWarnings("unchecked")
                List<List<Integer>> rawEdges = (List<List<Integer>>) body.get("edges");
                edges.clear();
                for (List<Integer> e : rawEdges) {
                    edges.add(new int[]{e.get(0), e.get(1)});
                }
            }

            IndependentSetToVertexCover.DualityResult res = IndependentSetToVertexCover.computeDuality(n, names, edges);
            dataService.recordAnalysis("M5 - NP", "Independent Set -> Vertex Cover", "Gallai Theorem",
                    n + " Vertices, " + edges.size() + " Edges",
                    "|IS|: " + res.getIndependentSetSize() + " + |VC|: " + res.getVertexCoverSize() +
                            " = |V|: " + res.getTotalVertices() + " (Verified: " + res.isDualityVerified() + ")",
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(V + E)");

            return ResponseEntity.ok(ApiResponse.ok("Independent Set to Vertex Cover Duality", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("IS to VC", e.getMessage()));
        }
    }

    // ===== 5.5 Vertex Cover 2-Approximation =====
    @PostMapping("/vertex-cover-approx")
    public ResponseEntity<ApiResponse> runVertexCoverApprox(@RequestBody(required = false) Map<String, Object> body) {
        try {
            int n = 7;
            List<String> names = Arrays.asList("TP53", "MDM2", "BRCA1", "ATM", "CHEK2", "CDKN1A", "PTEN");
            List<int[]> edges = new ArrayList<>();
            edges.add(new int[]{0, 1}); // TP53 - MDM2
            edges.add(new int[]{0, 3}); // TP53 - ATM
            edges.add(new int[]{0, 5}); // TP53 - CDKN1A
            edges.add(new int[]{2, 3}); // BRCA1 - ATM
            edges.add(new int[]{2, 4}); // BRCA1 - CHEK2
            edges.add(new int[]{3, 4}); // ATM - CHEK2
            edges.add(new int[]{5, 6}); // CDKN1A - PTEN

            if (body != null && body.containsKey("edges") && body.containsKey("vertexCount")) {
                n = (int) body.get("vertexCount");
                @SuppressWarnings("unchecked")
                List<String> vNames = (List<String>) body.get("vertexNames");
                if (vNames != null) names = vNames;

                @SuppressWarnings("unchecked")
                List<List<Integer>> rawEdges = (List<List<Integer>>) body.get("edges");
                edges.clear();
                for (List<Integer> e : rawEdges) {
                    edges.add(new int[]{e.get(0), e.get(1)});
                }
            }

            VertexCoverApproximation.ApproxResult res = VertexCoverApproximation.compute2Approximation(n, names, edges);
            dataService.recordAnalysis("M5 - NP", "Vertex Cover 2-Approx", "Maximal Matching Heuristic",
                    n + " Pathway Hubs, " + edges.size() + " Interactions",
                    "Cover Size: " + res.getApproxCoverSize() + " (OPT: " + res.getExactOptSize() +
                            ", Ratio: " + String.format("%.2f", res.getEmpiricalRatio()) + " <= 2.0)",
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(V)");

            return ResponseEntity.ok(ApiResponse.ok("Vertex Cover 2-Approximation", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Vertex Cover 2-Approx", e.getMessage()));
        }
    }

    // ===== Sample NP Benchmarks =====
    @GetMapping("/sample-instances")
    public ResponseEntity<ApiResponse> getSampleInstances() {
        Map<String, Object> samples = new LinkedHashMap<>();
        samples.put("geneRegulatoryCnf", "(TP53 | MDM2 | ~CDKN1A) & (~TP53 | BAX | BCL2) & (~MDM2 | ~BAX | PTEN) & (CDKN1A | ~BCL2 | EGFR)");
        samples.put("crisprSwitchCnf", "(Cas9 | GuideRNA1 | ~OffTarget) & (~Cas9 | GuideRNA2 | Cleavage) & (~GuideRNA1 | ~GuideRNA2 | PAM)");
        samples.put("unsatConflictCnf", "(A | B | C) & (~A | B | C) & (~B | C | A) & (~C | A | B) & (~A | ~B | ~C)");
        return ResponseEntity.ok(ApiResponse.ok("Sample NP Benchmark Instances", 1, samples));
    }
}
