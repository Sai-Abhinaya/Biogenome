package com.biogenome.np;

import java.util.*;

/**
 * Disjunctive clause in CNF Boolean logic (l1 OR l2 OR ... OR lk).
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class SatClause {

    private final List<SatLiteral> literals;

    public SatClause(List<SatLiteral> literals) {
        if (literals == null || literals.isEmpty()) {
            throw new IllegalArgumentException("Clause cannot be empty");
        }
        this.literals = Collections.unmodifiableList(new ArrayList<>(literals));
    }

    public List<SatLiteral> getLiterals() {
        return literals;
    }

    public int size() {
        return literals.size();
    }

    public boolean isSatisfied(Map<String, Boolean> assignment) {
        for (SatLiteral lit : literals) {
            Boolean val = assignment.get(lit.getVariable());
            if (val != null && lit.evaluate(val)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < literals.size(); i++) {
            if (i > 0) sb.append(" ∨ ");
            sb.append(literals.get(i));
        }
        sb.append(")");
        return sb.toString();
    }
}
