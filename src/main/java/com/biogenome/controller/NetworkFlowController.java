package com.biogenome.controller;

import com.biogenome.flow.*;
import com.biogenome.model.ApiResponse;
import com.biogenome.service.BioGenomeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller – Module 4: Network Flow & Bipartite Matching.
 * Base URL: /api/v1/flow
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/v1/flow")
@CrossOrigin(origins = "*")
public class NetworkFlowController {

    private final BioGenomeDataService dataService;

    @Autowired
    public NetworkFlowController(BioGenomeDataService dataService) {
        this.dataService = dataService;
    }

    // ===== Default Network Topology for UI Canvas Graph =====
    @GetMapping("/default-network")
    public ResponseEntity<ApiResponse> getDefaultNetwork() {
        FlowNetwork net = dataService.buildSampleResourceNetwork();
        Map<String, Object> data = serializeNetwork(net);
        return ResponseEntity.ok(ApiResponse.ok("Default Laboratory Flow Network", 1, data));
    }

    // ===== 4.1 Ford-Fulkerson =====
    @PostMapping("/ford-fulkerson")
    public ResponseEntity<ApiResponse> runFordFulkerson(@RequestBody(required = false) Map<String, Object> body) {
        try {
            FlowNetwork net = parseOrCreateNetwork(body);
            int source = body != null && body.containsKey("source") ? (int) body.get("source") : 0;
            int sink = body != null && body.containsKey("sink") ? (int) body.get("sink") : net.getVertexCount() - 1;

            FlowResult res = FordFulkerson.computeMaxFlow(net, source, sink);
            dataService.recordAnalysis("M4 - Flow", "Ford-Fulkerson", "Laboratory Flow",
                    "Source: " + net.getVertexName(source) + " -> Sink: " + net.getVertexName(sink),
                    "Max Flow: " + res.getMaxFlow() + " units, " + res.getAugmentingPaths().size() + " augmenting paths",
                    Math.round(res.getExecutionTimeMs()), res.getTimeComplexity(), res.getSpaceComplexity());

            return ResponseEntity.ok(ApiResponse.ok("Ford-Fulkerson Max Flow", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Ford-Fulkerson", e.getMessage()));
        }
    }

    // ===== 4.2 Edmonds-Karp =====
    @PostMapping("/edmonds-karp")
    public ResponseEntity<ApiResponse> runEdmondsKarp(@RequestBody(required = false) Map<String, Object> body) {
        try {
            FlowNetwork net = parseOrCreateNetwork(body);
            int source = body != null && body.containsKey("source") ? (int) body.get("source") : 0;
            int sink = body != null && body.containsKey("sink") ? (int) body.get("sink") : net.getVertexCount() - 1;

            FlowResult res = EdmondsKarp.computeMaxFlow(net, source, sink);
            dataService.recordAnalysis("M4 - Flow", "Edmonds-Karp", "Sample Distribution",
                    "Source: " + net.getVertexName(source) + " -> Sink: " + net.getVertexName(sink),
                    "Max Flow: " + res.getMaxFlow() + " units, " + res.getAugmentingPaths().size() + " BFS paths",
                    Math.round(res.getExecutionTimeMs()), res.getTimeComplexity(), res.getSpaceComplexity());

            return ResponseEntity.ok(ApiResponse.ok("Edmonds-Karp Max Flow", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Edmonds-Karp", e.getMessage()));
        }
    }

    // ===== 4.3 Dinic =====
    @PostMapping("/dinic")
    public ResponseEntity<ApiResponse> runDinic(@RequestBody(required = false) Map<String, Object> body) {
        try {
            FlowNetwork net = parseOrCreateNetwork(body);
            int source = body != null && body.containsKey("source") ? (int) body.get("source") : 0;
            int sink = body != null && body.containsKey("sink") ? (int) body.get("sink") : net.getVertexCount() - 1;

            FlowResult res = DinicAlgorithm.computeMaxFlow(net, source, sink);
            dataService.recordAnalysis("M4 - Flow", "Dinic's Algorithm", "Resource Network",
                    "Source: " + net.getVertexName(source) + " -> Sink: " + net.getVertexName(sink),
                    "Max Flow: " + res.getMaxFlow() + " units via Level Graphs",
                    Math.round(res.getExecutionTimeMs()), res.getTimeComplexity(), res.getSpaceComplexity());

            return ResponseEntity.ok(ApiResponse.ok("Dinic Max Flow", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Dinic", e.getMessage()));
        }
    }

    // ===== 4.4 Bipartite Matching & König's Theorem =====
    @PostMapping("/bipartite-matching")
    public ResponseEntity<ApiResponse> runBipartiteMatching(@RequestBody(required = false) Map<String, Object> body) {
        try {
            List<String> researchers = new ArrayList<>();
            List<String> experiments = new ArrayList<>();
            Map<Integer, List<Integer>> eligibility = new LinkedHashMap<>();

            if (body != null && body.containsKey("researchers") && body.containsKey("experiments")) {
                @SuppressWarnings("unchecked")
                List<String> rList = (List<String>) body.get("researchers");
                @SuppressWarnings("unchecked")
                List<String> eList = (List<String>) body.get("experiments");
                researchers.addAll(rList);
                experiments.addAll(eList);

                @SuppressWarnings("unchecked")
                Map<String, List<Integer>> rawElig = (Map<String, List<Integer>>) body.get("eligibility");
                if (rawElig != null) {
                    for (Map.Entry<String, List<Integer>> ent : rawElig.entrySet()) {
                        eligibility.put(Integer.parseInt(ent.getKey()), ent.getValue());
                    }
                }
            } else {
                // Use default biological researchers & experiments
                dataService.getAllResearchers().forEach(r -> researchers.add(r.getName()));
                dataService.getAllExperiments().forEach(e -> experiments.add(e.getName()));
                eligibility.putAll(dataService.buildSampleEligibility());
            }

            BipartiteMatching.MatchingResult res = BipartiteMatching.computeMatching(researchers, experiments, eligibility);

            dataService.recordAnalysis("M4 - Flow", "Bipartite Matching & König", "Personnel Matching",
                    researchers.size() + " Researchers, " + experiments.size() + " Experiments",
                    "Max Matching: " + res.getMatchingSize() + " = Min Vertex Cover: " + res.getMinVertexCoverSize() +
                            " (König's Theorem: " + res.isKonigTheoremSatisfied() + ")",
                    Math.round(res.getExecutionTimeMs()), res.getComplexity(), "O(V + E)");

            return ResponseEntity.ok(ApiResponse.ok("Bipartite Matching & König's Theorem", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Bipartite Matching", e.getMessage()));
        }
    }

    // ===== 4.5 Max-Flow Min-Cut Bottleneck Analyzer =====
    @PostMapping("/min-cut")
    public ResponseEntity<ApiResponse> runMinCut(@RequestBody(required = false) Map<String, Object> body) {
        try {
            FlowNetwork net = parseOrCreateNetwork(body);
            int source = body != null && body.containsKey("source") ? (int) body.get("source") : 0;
            int sink = body != null && body.containsKey("sink") ? (int) body.get("sink") : net.getVertexCount() - 1;

            FlowResult res = MaxFlowMinCut.analyzeBottlenecks(net, source, sink);
            dataService.recordAnalysis("M4 - Flow", "Max-Flow Min-Cut", "Bottleneck Analysis",
                    "Source: " + net.getVertexName(source) + " -> Sink: " + net.getVertexName(sink),
                    "Min Cut Capacity: " + res.getMinCutCapacity() + " units, " + res.getMinCutBottleneckEdges().size() + " bottleneck cut edges",
                    Math.round(res.getExecutionTimeMs()), "O(V^2 * E) Dinic Cut", "O(V + E)");

            return ResponseEntity.ok(ApiResponse.ok("Max-Flow Min-Cut Bottleneck Analysis", Math.round(res.getExecutionTimeMs()), res));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Min-Cut", e.getMessage()));
        }
    }

    private FlowNetwork parseOrCreateNetwork(Map<String, Object> body) {
        if (body != null && body.containsKey("vertices") && body.containsKey("edges")) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> vList = (List<Map<String, String>>) body.get("vertices");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> eList = (List<Map<String, Object>>) body.get("edges");

            FlowNetwork net = new FlowNetwork(vList.size());
            for (int i = 0; i < vList.size(); i++) {
                Map<String, String> v = vList.get(i);
                net.setVertexInfo(i, v.getOrDefault("name", "V" + i), v.getOrDefault("type", "General"));
            }

            for (Map<String, Object> e : eList) {
                int from = (int) e.get("from");
                int to = (int) e.get("to");
                int cap = (int) e.get("capacity");
                net.addEdge(from, to, cap);
            }
            return net;
        }
        return dataService.buildSampleResourceNetwork();
    }

    private Map<String, Object> serializeNetwork(FlowNetwork net) {
        Map<String, Object> res = new LinkedHashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (int i = 0; i < net.getVertexCount(); i++) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", i);
            node.put("name", net.getVertexName(i));
            node.put("type", net.getVertexType(i));
            nodes.add(node);
        }

        List<Map<String, Object>> edges = new ArrayList<>();
        for (FlowEdge e : net.getAllEdges()) {
            Map<String, Object> edge = new LinkedHashMap<>();
            edge.put("from", e.getFrom());
            edge.put("to", e.getTo());
            edge.put("fromName", net.getVertexName(e.getFrom()));
            edge.put("toName", net.getVertexName(e.getTo()));
            edge.put("capacity", e.getCapacity());
            edge.put("flow", e.getFlow());
            edges.add(edge);
        }

        res.put("vertexCount", net.getVertexCount());
        res.put("nodes", nodes);
        res.put("edges", edges);
        return res;
    }
}
