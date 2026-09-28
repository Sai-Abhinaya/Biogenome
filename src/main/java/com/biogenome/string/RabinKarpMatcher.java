package com.biogenome.string;

import com.biogenome.model.DnaSequence;
import com.biogenome.model.MotifMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rabin-Karp Hash-based DNA Sequence and Motif Matcher.
 * <p>
 * Employs a polynomial rolling hash function to compute the numeric fingerprint of a
 * sliding window of length M across a genomic sequence of length N. Transitions take O(1)
 * time per window shift. Spurious hash collisions are resolved via exact string verification.
 * </p>
 * 
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Average Time Complexity:</b> O(N + M)</li>
 *   <li><b>Worst-Case Time Complexity:</b> O(N &times; M) (mitigated by high-prime modulo)</li>
 *   <li><b>Space Complexity:</b> O(1) auxiliary</li>
 * </ul>
 * 
 * @author BioGenome Team
 * @version 1.0.0
 */
public final class RabinKarpMatcher {

    // Base radix for nucleotide encoding (A, C, G, T, N)
    private static final long RADIX = 257L;
    // Large prime modulus to minimize probability of hash collisions
    private static final long MODULUS = 1_000_000_007L;

    /**
     * Statistics container for empirical analysis and viva presentation.
     */
    public static final class SearchStats {
        private final List<Integer> matches;
        private final int spuriousHits;
        private final long hashComparisons;

        public SearchStats(List<Integer> matches, int spuriousHits, long hashComparisons) {
            this.matches = matches;
            this.spuriousHits = spuriousHits;
            this.hashComparisons = hashComparisons;
        }

        public List<Integer> getMatches() {
            return matches;
        }

        public int getSpuriousHits() {
            return spuriousHits;
        }

        public long getHashComparisons() {
            return hashComparisons;
        }
    }

    /**
     * Locates all occurrences of a target motif in a DNA sequence using rolling hash.
     *
     * @param dnaSequence Genomic DNA text (length N)
     * @param targetMotif Nucleotide pattern (length M)
     * @return List of 0-based start indices
     */
    public static List<Integer> searchMotif(String dnaSequence, String targetMotif) {
        return searchWithDetailedStats(dnaSequence, targetMotif).getMatches();
    }

    /**
     * Executes Rabin-Karp matching while tracking collision metrics and spurious hits.
     *
     * @param dnaSequence Target genomic DNA string
     * @param targetMotif Nucleotide motif
     * @return SearchStats object containing indices and collision counts
     */
    public static SearchStats searchWithDetailedStats(String dnaSequence, String targetMotif) {
        if (dnaSequence == null || targetMotif == null) {
            return new SearchStats(Collections.emptyList(), 0, 0);
        }

        int n = dnaSequence.length();
        int m = targetMotif.length();

        if (m == 0 || n == 0 || m > n) {
            return new SearchStats(Collections.emptyList(), 0, 0);
        }

        List<Integer> matches = new ArrayList<>();
        int spuriousHits = 0;
        long hashComparisons = 0;

        // Compute high-order multiplier: h = (RADIX^(M-1)) % MODULUS
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * RADIX) % MODULUS;
        }

        // Compute initial hash values for motif and first window of text
        long motifHash = 0;
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            motifHash = (motifHash * RADIX + targetMotif.charAt(i)) % MODULUS;
            windowHash = (windowHash * RADIX + dnaSequence.charAt(i)) % MODULUS;
        }

        // Slide window across DNA sequence
        for (int i = 0; i <= n - m; i++) {
            hashComparisons++;
            if (motifHash == windowHash) {
                // Potential hit - verify exact characters to eliminate spurious collision
                if (checkExactMatch(dnaSequence, i, targetMotif, m)) {
                    matches.add(i);
                } else {
                    spuriousHits++;
                }
            }

            // Slide window to next position (compute rolling hash for window i+1)
            if (i < n - m) {
                long leadingCharTerm = (dnaSequence.charAt(i) * h) % MODULUS;
                windowHash = (windowHash - leadingCharTerm + MODULUS) % MODULUS;
                windowHash = (windowHash * RADIX + dnaSequence.charAt(i + m)) % MODULUS;
            }
        }

        return new SearchStats(matches, spuriousHits, hashComparisons);
    }

    private static boolean checkExactMatch(String text, int startIdx, String pattern, int patternLen) {
        for (int j = 0; j < patternLen; j++) {
            if (text.charAt(startIdx + j) != pattern.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Biological search wrapper.
     */
    public static List<MotifMatch> searchWithMatches(DnaSequence dnaSequence, String motifName, String targetMotif) {
        if (dnaSequence == null || targetMotif == null) return Collections.emptyList();

        List<Integer> indices = searchMotif(dnaSequence.getSequence(), targetMotif);
        List<MotifMatch> matches = new ArrayList<>(indices.size());
        String seq = dnaSequence.getSequence();
        int mLen = targetMotif.length();

        for (int idx : indices) {
            int ctxStart = Math.max(0, idx - 4);
            int ctxEnd = Math.min(seq.length(), idx + mLen + 4);
            String snippet = seq.substring(ctxStart, ctxEnd);
            matches.add(new MotifMatch(motifName, targetMotif, idx, idx + mLen, 1.0, snippet));
        }

        return matches;
    }
}
