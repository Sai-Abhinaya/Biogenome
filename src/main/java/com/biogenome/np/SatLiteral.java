package com.biogenome.np;

import java.util.Objects;

/**
 * Literal in Boolean propositional logic (e.g. x1 or NOT x1).
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class SatLiteral {

    private final String variable;
    private final boolean negated;

    public SatLiteral(String variable, boolean negated) {
        this.variable = Objects.requireNonNull(variable, "Variable cannot be null").trim();
        this.negated = negated;
    }

    public static SatLiteral parse(String token) {
        token = token.trim();
        if (token.startsWith("~") || token.startsWith("!") || token.startsWith("NOT ")) {
            String v = token.replaceFirst("^[~!]|NOT\\s*", "").trim();
            return new SatLiteral(v, true);
        }
        return new SatLiteral(token, false);
    }

    public String getVariable() {
        return variable;
    }

    public boolean isNegated() {
        return negated;
    }

    public boolean isOpposite(SatLiteral other) {
        return this.variable.equalsIgnoreCase(other.variable) && this.negated != other.negated;
    }

    public boolean evaluate(boolean varValue) {
        return negated ? !varValue : varValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SatLiteral that = (SatLiteral) o;
        return negated == that.negated && variable.equalsIgnoreCase(that.variable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable.toLowerCase(), negated);
    }

    @Override
    public String toString() {
        return negated ? "¬" + variable : variable;
    }
}
