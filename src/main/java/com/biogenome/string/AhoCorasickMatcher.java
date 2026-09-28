package com.biogenome.string;

import com.biogenome.model.DnaSequence;
import com.biogenome.model.MotifMatch;

import java.util.*;

/**
 * Aho-Corasick Multi-Pattern Automaton for Simultaneous Genomic Motif Detection.
 * <p>
 * Constructs a trie with failure links (longest proper suffix that exists in the trie)
 * and output/dictionary links. It scans a genomic DNA sequence of length N in linear time O(N)
 * to simultaneously find all occurrences of K arbitrary length motifs in a single pass.
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Trie & Automaton Construction:</b> O(&sum; M<sub>i</sub> &times; &Sigma;) where &Sigma; is alphabet size</li>
 *   <li><b>Search Time:</b> O(N + Z) where N is text length, Z is total number of matches</li>
 *   <li><b>Space Complexity:</b> O(&sum; M<sub>i</sub> &times; &Sigma;)</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class AhoCorasickMatcher {

    /**
     * Internal Trie Node with transitions, failure link, and pattern output lists.
     */
    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        Node dictLink = null;
        final List<PatternEntry> outputs = new ArrayList<>();
    }

    public static final class PatternEntry {
        private final String name;
        private final String pattern;

        public PatternEntry(String name, String pattern) {
            this.name = name;
            this.pattern = pattern;
        }

        public String getName() {
            return name;
        }

        public String getPattern() {
            return pattern;
        }
    }

    private final Node root;
    private boolean isBuilt;
    private final List<PatternEntry> registeredMotifs;

    public AhoCorasickMatcher() {
        this.root = new Node();
        this.isBuilt = false;
        this.registeredMotifs = new ArrayList<>();
    }

    /**
     * Adds a target DNA motif to the dictionary before building the automaton.
     *
     * @param motifName Biological identifier (e.g., "EcoRI", "BamHI", "TATA-box")
     * @param pattern   Nucleotide pattern (e.g., "GAATTC")
     * @return this matcher for fluent builder pattern
     */
    public AhoCorasickMatcher addMotif(String motifName, String pattern) {
        if (motifName == null || pattern == null || pattern.isEmpty()) {
            return this;
        }
        String normalizedPattern = pattern.toUpperCase().trim();
        registeredMotifs.add(new PatternEntry(motifName, normalizedPattern));

        Node current = root;
        for (int i = 0; i < normalizedPattern.length(); i++) {
            char c = normalizedPattern.charAt(i);
            current = current.children.computeIfAbsent(c, k -> new Node());
        }
        current.outputs.add(new PatternEntry(motifName, normalizedPattern));
        isBuilt = false;
        return this;
    }

    /**
     * Compiles the Trie by calculating failure links and output links using BFS.
     */
    public void buildAutomaton() {
        Queue<Node> queue = new ArrayDeque<>();

        // Initialize depth-1 children failure links to root
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        // BFS to set failure transitions and dictionary links
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();
                queue.add(child);

                Node failCandidate = current.fail;
                while (failCandidate != null && !failCandidate.children.containsKey(ch)) {
                    failCandidate = failCandidate.fail;
                }

                if (failCandidate != null) {
                    child.fail = failCandidate.children.get(ch);
                } else {
                    child.fail = root;
                }

                // Setup dictionary output links
                if (child.fail != null && !child.fail.outputs.isEmpty()) {
                    child.dictLink = child.fail;
                } else if (child.fail != null) {
                    child.dictLink = child.fail.dictLink;
                }
            }
        }

        this.isBuilt = true;
    }

    /**
     * Searches the given genomic DNA string for all registered motifs simultaneously.
     *
     * @param dnaSequence Target DNA text
     * @return List of all discovered MotifMatch records sorted by position
     */
    public List<MotifMatch> search(String dnaSequence) {
        if (dnaSequence == null || dnaSequence.isEmpty()) {
            return Collections.emptyList();
        }

        if (!isBuilt) {
            buildAutomaton();
        }

        List<MotifMatch> results = new ArrayList<>();
        Node current = root;
        int n = dnaSequence.length();

        for (int i = 0; i < n; i++) {
            char c = Character.toUpperCase(dnaSequence.charAt(i));

            // Follow failure links until matching transition or root is reached
            while (current != root && !current.children.containsKey(c)) {
                current = current.fail;
            }

            if (current.children.containsKey(c)) {
                current = current.children.get(c);
            } else {
                current = root;
            }

            // Collect direct matches at current node
            collectMatches(current, i, dnaSequence, results);

            // Collect matches along dictionary links
            Node dictNode = current.dictLink;
            while (dictNode != null) {
                collectMatches(dictNode, i, dnaSequence, results);
                dictNode = dictNode.dictLink;
            }
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Overloaded search method accepting {@link DnaSequence} domain record.
     */
    public List<MotifMatch> search(DnaSequence dnaSequence) {
        if (dnaSequence == null) return Collections.emptyList();
        return search(dnaSequence.getSequence());
    }

    private void collectMatches(Node node, int currentEndIdx, String text, List<MotifMatch> results) {
        for (PatternEntry pe : node.outputs) {
            int pLen = pe.getPattern().length();
            int startIdx = currentEndIdx - pLen + 1;
            int ctxStart = Math.max(0, startIdx - 4);
            int ctxEnd = Math.min(text.length(), currentEndIdx + 5);
            String snippet = text.substring(ctxStart, ctxEnd);

            results.add(new MotifMatch(pe.getName(), pe.getPattern(), startIdx, currentEndIdx + 1, 1.0, snippet));
        }
    }

    public List<PatternEntry> getRegisteredMotifs() {
        return Collections.unmodifiableList(registeredMotifs);
    }
}
