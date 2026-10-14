package com.biogenome.controller;

import com.biogenome.model.AnalysisRecord;
import com.biogenome.model.ApiResponse;
import com.biogenome.service.BioGenomeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller – Analysis Session History.
 * Base URL: /api/v1/history
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/v1/history")
@CrossOrigin(origins = "*")
public class AnalysisHistoryController {

    private final BioGenomeDataService dataService;

    @Autowired
    public AnalysisHistoryController(BioGenomeDataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getHistory(@RequestParam(defaultValue = "50") int limit) {
        List<AnalysisRecord> history = dataService.getRecentHistory(limit);
        return ResponseEntity.ok(ApiResponse.ok("Analysis History", history.size(), history));
    }

    @PostMapping("/clear")
    public ResponseEntity<ApiResponse> clearHistory() {
        dataService.clearHistory();
        return ResponseEntity.ok(ApiResponse.ok("History Cleared", 0, "All analysis session logs cleared."));
    }
}
