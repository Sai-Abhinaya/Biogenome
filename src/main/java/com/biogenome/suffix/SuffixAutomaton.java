package com.biogenome.suffix;

import java.util.*;

/**
 * Suffix Automaton (Directed Acyclic Word Graph - DAWG) for Genomic Substring Analysis.
 * <p>
 * A Suffix Automaton is the most compact and efficient data structure representing all
 * substrings of a genomic sequence. It contains at most 2N - 1 states and 3N - 4 transitions.
 * Supports exact substring recognition in O(M) time, distinct substring counting, and
 * Longest Common Substring (LCS) analysis between two genomes in O(N1 + N2) time.
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Construction Time:</b> O(N &times; &Sigma;) strictly linear</li>
 *   <li><b>Substring Query Time:</b> O(M) where M = pattern length</li>
 *   <li><b>Distinct Substring Counting:</b> O(N)</li>
 *   <li><b>Longest Common Substring (LCS):</b> O(N1 + N2)</li>
 *   <li><b>Space Complexity:</b> O(N &times; &Sigma;)</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class SuffixAutomaton {

    public static final class State {
        public final int id;
        public int len;
        public int link;
        public final Map<Character, Integer> next = new HashMap<>();
        public boolean isClone = false;
        public long endposCount = 0; // Number of occurrences in text

        public State(int id, int len, int link) {
            this.id = id;
            this.len = len;
            this.link = link;
        }
    }

    private final List<State> states;
    private int last;
    private final String originalText;

    public SuffixAutomaton(String text) {
        this.originalText = text != null ? text : "";
        this.states = new ArrayList<>();
        // Root state 0
        states.add(new State(0, 0, -1));
        this.last = 0;

        for (int i = 0; i < this.originalText.length(); i++) {
            extend(this.originalText.charAt(i));
        }

        computeEndposCounts();
    }

    private void extend(char c) {
        int cur = states.size();
        states.add(new State(cur, states.get(last).len + 1, 0));
        states.get(cur).endposCount = 1;

        int p = last;
        while (p != -1 && !states.get(p).next.containsKey(c)) {
            states.get(p).next.put(c, cur);
            p = states.get(p).link;
        }

        if (p == -1) {
            states.get(cur).link = 0;
        } else {
            int q = states.get(p).next.get(c);
            if (states.get(p).len + 1 == states.get(q).len) {
                states.get(cur).link = q;
            } else {
                int clone = states.size();
                State qState = states.get(q);
                State cloneState = new State(clone, states.get(p).len + 1, qState.link);
                cloneState.next.putAll(qState.next);
                cloneState.isClone = true;
                cloneState.endposCount = 0;
                states.add(cloneState);

                while (p != -1 && states.get(p).next.get(c) != null && states.get(p).next.get(c) == q) {
                    states.get(p).next.put(c, clone);
                    p = states.get(p).link;
                }

                qState.link = clone;
                states.get(cur).link = clone;
            }
        }

        last = cur;
    }

    private void computeEndposCounts() {
        // Sort states in descending order of length
        List<State> sorted = new ArrayList<>(states);
        sorted.sort((a, b) -> Integer.compare(b.len, a.len));

        for (State state : sorted) {
            if (state.link != -1) {
                states.get(state.link).endposCount += state.endposCount;
            }
        }
    }

    /**
     * Checks if a target motif exists as a substring of the indexed genome in O(M) time.
     *
     * @param motif Target nucleotide pattern
     * @return true if motif is present
     */
    public boolean contains(String motif) {
        if (motif == null || motif.isEmpty()) return true;
        int cur = 0;
        for (int i = 0; i < motif.length(); i++) {
            char c = motif.charAt(i);
            if (!states.get(cur).next.containsKey(c)) {
                return false;
            }
            cur = states.get(cur).next.get(c);
        }
        return true;
    }

    /**
     * Counts the exact number of occurrences of a motif in the sequence in O(M) time.
     *
     * @param motif Target nucleotide pattern
     * @return Frequency count (0 if not present)
     */
    public long countOccurrences(String motif) {
        if (motif == null || motif.isEmpty()) return 0;
        int cur = 0;
        for (int i = 0; i < motif.length(); i++) {
            char c = motif.charAt(i);
            if (!states.get(cur).next.containsKey(c)) {
                return 0;
            }
            cur = states.get(cur).next.get(c);
        }
        return states.get(cur).endposCount;
    }

    /**
     * Calculates the total number of distinct DNA substrings present in the genome.
     *
     * @return Total distinct substring count
     */
    public long countDistinctSubstrings() {
        long count = 0;
        for (int i = 1; i < states.size(); i++) {
            count += (states.get(i).len - states.get(states.get(i).link).len);
        }
        return count;
    }

    /**
     * Finds the Longest Common Substring (LCS) shared between this genome and a second genome.
     * Runs in strictly linear O(N1 + N2) time.
     *
     * @param otherSequence Second genomic sequence
     * @return The longest shared DNA sequence fragment
     */
    public String findLongestCommonSubstring(String otherSequence) {
        if (otherSequence == null || otherSequence.isEmpty() || originalText.isEmpty()) {
            return "";
        }

        int cur = 0;
        int curLen = 0;
        int maxLen = 0;
        int bestEndPos = 0;

        for (int i = 0; i < otherSequence.length(); i++) {
            char c = otherSequence.charAt(i);

            while (cur > 0 && !states.get(cur).next.containsKey(c)) {
                cur = states.get(cur).link;
                curLen = states.get(cur).len;
            }

            if (states.get(cur).next.containsKey(c)) {
                cur = states.get(cur).next.get(c);
                curLen++;
            }

            if (curLen > maxLen) {
                maxLen = curLen;
                bestEndPos = i;
            }
        }

        if (maxLen == 0) return "";
        return otherSequence.substring(bestEndPos - maxLen + 1, bestEndPos + 1);
    }

    public int getStateCount() {
        return states.size();
    }

    public List<State> getStates() {
        return Collections.unmodifiableList(states);
    }
}
