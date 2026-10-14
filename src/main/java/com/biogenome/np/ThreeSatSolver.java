package com.biogenome.np;

import java.util.*;

/**
 * DPLL / Backtracking Solver for 3-SAT Genomic & Biological Constraints.
 * <p>
 * Evaluates whether combinatorial biological conditions (such as gene knockout
 * viability, transcriptional pathway switches, and CRISPR guide RNA compatibility)
 * are simultaneously satisfiable.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(2<sup>N</sup>) worst case (NP-Complete)</li>
 *   <li><b>Space Complexity:</b> O(N + M) for recursion call tree</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class ThreeSatSolver {

    public static final class SatResult {
        private final boolean satisfiable;
        private final Map<String, Boolean> model;
        private final List<String> clauseStrings;
        private final Set<String> variables;
        private final int decisionsMade;
        private final long executionTimeNs;

        public SatResult(boolean satisfiable, Map<String, Boolean> model, List<String> clauseStrings,
                         Set<String> variables, int decisionsMade, long executionTimeNs) {
            this.satisfiable = satisfiable;
            this.model = model != null ? model : Collections.emptyMap();
            this.clauseStrings = clauseStrings;
            this.variables = variables;
            this.decisionsMade = decisionsMade;
            this.executionTimeNs = executionTimeNs;
        }

        public boolean isSatisfiable() { return satisfiable; }
        public Map<String, Boolean> getModel() { return model; }
        public List<String> getClauseStrings() { return clauseStrings; }
        public Set<String> getVariables() { return variables; }
        public int getDecisionsMade() { return decisionsMade; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "O(2^N * M) DPLL Backtracking (NP-Complete)"; }
    }

    public static SatResult solve(List<SatClause> clauses) {
        long start = System.nanoTime();

        Set<String> varSet = new TreeSet<>();
        List<String> clauseStrs = new ArrayList<>();
        for (SatClause c : clauses) {
            clauseStrs.add(c.toString());
            for (SatLiteral lit : c.getLiterals()) {
                varSet.add(lit.getVariable());
            }
        }

        List<String> varList = new ArrayList<>(varSet);
        Map<String, Boolean> assignment = new LinkedHashMap<>();
        int[] decisionCounter = new int[1];

        boolean sat = dpll(clauses, varList, 0, assignment, decisionCounter);
        long elapsed = System.nanoTime() - start;

        if (sat) {
            // Fill any unassigned variables with true by default
            for (String v : varList) {
                assignment.putIfAbsent(v, true);
            }
            return new SatResult(true, assignment, clauseStrs, varSet, decisionCounter[0], elapsed);
        } else {
            return new SatResult(false, null, clauseStrs, varSet, decisionCounter[0], elapsed);
        }
    }

    private static boolean dpll(List<SatClause> clauses, List<String> varList, int index,
                                Map<String, Boolean> currentAssignment, int[] decisionCounter) {
        decisionCounter[0]++;

        // Check if any clause is already falsified under partial assignment
        for (SatClause c : clauses) {
            boolean hasUndetermined = false;
            boolean satisfied = false;
            for (SatLiteral lit : c.getLiterals()) {
                Boolean val = currentAssignment.get(lit.getVariable());
                if (val == null) {
                    hasUndetermined = true;
                } else if (lit.evaluate(val)) {
                    satisfied = true;
                    break;
                }
            }
            if (!satisfied && !hasUndetermined) {
                return false; // Conflict found (clause is all-false)
            }
        }

        // Check if all clauses are satisfied
        boolean allSatisfied = true;
        for (SatClause c : clauses) {
            if (!c.isSatisfied(currentAssignment)) {
                allSatisfied = false;
                break;
            }
        }
        if (allSatisfied) {
            return true;
        }

        if (index >= varList.size()) {
            return false;
        }

        String nextVar = varList.get(index);

        // Try TRUE branch
        currentAssignment.put(nextVar, true);
        if (dpll(clauses, varList, index + 1, currentAssignment, decisionCounter)) {
            return true;
        }

        // Try FALSE branch
        currentAssignment.put(nextVar, false);
        if (dpll(clauses, varList, index + 1, currentAssignment, decisionCounter)) {
            return true;
        }

        // Backtrack
        currentAssignment.remove(nextVar);
        return false;
    }

    /**
     * Parses standard CNF string format:
     * e.g. "(TP53 | BRCA1 | ~EGFR) & (~TP53 | MDM2 | PTEN) & (~BRCA1 | ~MDM2 | EGFR)"
     */
    public static List<SatClause> parseCnf(String cnfText) {
        List<SatClause> clauses = new ArrayList<>();
        if (cnfText == null || cnfText.trim().isEmpty()) {
            return clauses;
        }

        String[] rawClauses = cnfText.split("&|\\band\\b");
        for (String rawClause : rawClauses) {
            String trimmed = rawClause.trim().replaceAll("[()]", "");
            if (trimmed.isEmpty()) continue;

            String[] tokens = trimmed.split("\\||\\bor\\b|\\s*,\\s*");
            List<SatLiteral> literals = new ArrayList<>();
            for (String tok : tokens) {
                tok = tok.trim();
                if (!tok.isEmpty()) {
                    literals.add(SatLiteral.parse(tok));
                }
            }
            if (!literals.isEmpty()) {
                clauses.add(new SatClause(literals));
            }
        }
        return clauses;
    }
}
