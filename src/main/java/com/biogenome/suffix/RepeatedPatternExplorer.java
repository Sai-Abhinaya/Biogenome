package com.biogenome.suffix;

import java.util.*;

/**
 * Repeated Pattern & Tandem Repeat Explorer using Suffix Structures.
 * <p>
 * Transforms Module 2 suffix algorithms (SA-IS Suffix Array and Kasai LCP) into an active
 * bioinformatics investigative tool. Discovers recurrent nucleotide motifs, tandem repeats,
 * and high-frequency DNA elements in linear time O(N).
 * </p>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class RepeatedPatternExplorer {

    public static final class RepeatEntry {
        private final String pattern;
        private final int length;
        private final int occurrences;
        private final List<Integer> startPositions;
        private final String repeatType; // "Maximal Repeat", "Longest Repeat", "Frequent Motif", "Tandem Repeat"

        public RepeatEntry(String pattern, int length, int occurrences, List<Integer> startPositions, String repeatType) {
            this.pattern = pattern;
            this.length = length;
            this.occurrences = occurrences;
            this.startPositions = startPositions;
            this.repeatType = repeatType;
        }

        public String getPattern() { return pattern; }
        public int getLength() { return length; }
        public int getOccurrences() { return occurrences; }
        public List<Integer> getStartPositions() { return startPositions; }
        public String getRepeatType() { return repeatType; }
    }

    public static final class RepeatExplorerResult {
        private final String sequence;
        private final int sequenceLength;
        private final int minRepeatLength;
        private final List<RepeatEntry> repeats;
        private final int maxLcpValue;
        private final String longestRepeat;
        private final long executionTimeNs;

        public RepeatExplorerResult(String sequence, int minRepeatLength, List<RepeatEntry> repeats,
                                    int maxLcpValue, String longestRepeat, long executionTimeNs) {
            this.sequence = sequence;
            this.sequenceLength = sequence.length();
            this.minRepeatLength = minRepeatLength;
            this.repeats = repeats;
            this.maxLcpValue = maxLcpValue;
            this.longestRepeat = longestRepeat;
            this.executionTimeNs = executionTimeNs;
        }

        public String getSequence() { return sequence; }
        public int getSequenceLength() { return sequenceLength; }
        public int getMinRepeatLength() { return minRepeatLength; }
        public List<RepeatEntry> getRepeats() { return repeats; }
        public int getMaxLcpValue() { return maxLcpValue; }
        public String getLongestRepeat() { return longestRepeat; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "O(N) SA-IS + Kasai LCP Construction"; }
    }

    public static RepeatExplorerResult exploreRepeats(String sequence, int minRepeatLength) {
        long start = System.nanoTime();
        if (sequence == null || sequence.length() < 2) {
            return new RepeatExplorerResult(sequence != null ? sequence : "", minRepeatLength,
                    Collections.emptyList(), 0, "", 0);
        }

        if (minRepeatLength <= 0) minRepeatLength = 3;

        SuffixArray sa = new SuffixArray(sequence);
        int[] saArr = sa.getSuffixArray();
        int[] lcp = KasaiLCP.computeLCPArray(sequence, saArr);

        int maxLcp = 0;
        String longestRepeat = "";

        Map<String, Set<Integer>> repeatOccurrences = new LinkedHashMap<>();

        for (int i = 1; i < lcp.length; i++) {
            int lcpLen = lcp[i];
            if (lcpLen >= minRepeatLength) {
                int pos1 = saArr[i - 1];
                int pos2 = saArr[i];
                String sub = sequence.substring(pos2, pos2 + lcpLen);

                if (lcpLen > maxLcp) {
                    maxLcp = lcpLen;
                    longestRepeat = sub;
                }

                repeatOccurrences.computeIfAbsent(sub, k -> new TreeSet<>()).add(pos1);
                repeatOccurrences.get(sub).add(pos2);
            }
        }

        List<RepeatEntry> repeatList = new ArrayList<>();
        for (Map.Entry<String, Set<Integer>> entry : repeatOccurrences.entrySet()) {
            String pat = entry.getKey();
            List<Integer> positions = new ArrayList<>(entry.getValue());
            String type = "Repeated Motif";
            if (pat.equals(longestRepeat)) {
                type = "Longest Repeated Substring";
            } else if (isTandem(positions, pat.length())) {
                type = "Tandem Repeat / Micro-satellite";
            } else if (positions.size() >= 3) {
                type = "High-Frequency Repeat";
            }

            repeatList.add(new RepeatEntry(pat, pat.length(), positions.size(), positions, type));
        }

        // Sort by length descending, then occurrences descending
        repeatList.sort((a, b) -> {
            int c = Integer.compare(b.getLength(), a.getLength());
            if (c != 0) return c;
            return Integer.compare(b.getOccurrences(), a.getOccurrences());
        });

        long elapsedNs = System.nanoTime() - start;
        return new RepeatExplorerResult(sequence, minRepeatLength, repeatList, maxLcp, longestRepeat, elapsedNs);
    }

    private static boolean isTandem(List<Integer> pos, int len) {
        if (pos.size() < 2) return false;
        for (int i = 0; i < pos.size() - 1; i++) {
            if (pos.get(i) + len == pos.get(i + 1)) {
                return true;
            }
        }
        return false;
    }
}
