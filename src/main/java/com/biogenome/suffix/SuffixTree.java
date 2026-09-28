package com.biogenome.suffix;

import java.util.*;

/**
 * Compressed Suffix Tree for Genome Substring Analysis.
 * <p>
 * An edge-compressed trie where all suffixes of a DNA sequence are represented
 * as paths from the root to the leaves. Every internal node has at least two branches,
 * guaranteeing at most 2N nodes for a genome sequence of length N.
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Tree Node Count:</b> &le; 2N nodes</li>
 *   <li><b>Pattern Search Time:</b> O(M) where M = query motif length</li>
 *   <li><b>Space Complexity:</b> O(N) compressed edge intervals</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class SuffixTree {

    public static final class Node {
        public int start;
        public int end;
        public final Map<Character, Node> children = new TreeMap<>();
        public final List<Integer> suffixIndices = new ArrayList<>();
        public int depth = 0;

        public Node(int start, int end, int depth) {
            this.start = start;
            this.end = end;
            this.depth = depth;
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }

        public int edgeLength() {
            return end - start;
        }
    }

    private final String text;
    private final Node root;
    private int totalNodes = 1;

    public SuffixTree(String sequence) {
        String raw = sequence != null ? sequence : "";
        this.text = raw.endsWith("$") ? raw : (raw + "$");
        this.root = new Node(-1, -1, 0);
        buildTree();
    }

    private void buildTree() {
        if (text.isEmpty()) return;

        int n = text.length();
        for (int i = 0; i < n; i++) {
            insertSuffix(i);
        }
    }

    private void insertSuffix(int suffixIdx) {
        Node current = root;
        int i = suffixIdx;
        int n = text.length();

        while (i < n) {
            char edgeChar = text.charAt(i);
            Node child = current.children.get(edgeChar);

            if (child == null) {
                // Create new leaf node for remaining suffix
                Node leaf = new Node(i, n, current.depth + (n - i));
                leaf.suffixIndices.add(suffixIdx);
                current.children.put(edgeChar, leaf);
                totalNodes++;
                return;
            }

            // Traverse existing edge and match characters
            int edgeStart = child.start;
            int edgeEnd = child.end;
            int matchLen = 0;
            int edgeLen = edgeEnd - edgeStart;

            while (matchLen < edgeLen && i + matchLen < n &&
                   text.charAt(edgeStart + matchLen) == text.charAt(i + matchLen)) {
                matchLen++;
            }

            if (matchLen == edgeLen) {
                // Fully matched edge, descend into child
                current = child;
                i += edgeLen;
            } else {
                // Split edge
                Node internal = new Node(edgeStart, edgeStart + matchLen, current.depth + matchLen);
                totalNodes++;

                // Adjust child
                child.start = edgeStart + matchLen;
                internal.children.put(text.charAt(child.start), child);

                // Add new branch leaf
                if (i + matchLen < n) {
                    Node newLeaf = new Node(i + matchLen, n, internal.depth + (n - (i + matchLen)));
                    newLeaf.suffixIndices.add(suffixIdx);
                    internal.children.put(text.charAt(newLeaf.start), newLeaf);
                    totalNodes++;
                } else {
                    internal.suffixIndices.add(suffixIdx);
                }

                // Replace old edge in current with internal node
                current.children.put(edgeChar, internal);
                return;
            }
        }
    }

    /**
     * Checks if target motif exists in the suffix tree.
     *
     * @param motif Pattern to search
     * @return true if pattern is present
     */
    public boolean contains(String motif) {
        if (motif == null || motif.isEmpty()) return true;
        Node current = root;
        int i = 0;
        int m = motif.length();

        while (i < m) {
            char c = motif.charAt(i);
            Node child = current.children.get(c);
            if (child == null) return false;

            int edgeLen = child.end - child.start;
            for (int k = 0; k < edgeLen && i < m; k++, i++) {
                if (motif.charAt(i) != text.charAt(child.start + k)) {
                    return false;
                }
            }
            current = child;
        }
        return true;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public Node getRoot() {
        return root;
    }

    public String getText() {
        return text;
    }
}
