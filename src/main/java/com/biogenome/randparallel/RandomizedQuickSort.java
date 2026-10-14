package com.biogenome.randparallel;

import java.util.*;

/**
 * Randomized QuickSort for Genomic Records.
 * <p>
 * Sorts biological entities (e.g. gene sequences, mutations, contigs) by customizable metrics
 * (Length, GC-Content, Mutation Count, Confidence Score) using uniformly random pivot selection.
 * Guarantees expected O(N log N) runtime and shields against worst-case O(N&sup2;) degradation
 * on already-sorted or patterned genomic sequences.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Expected Time Complexity:</b> O(N log N)</li>
 *   <li><b>Worst-Case Time Complexity:</b> O(N&sup2;) (probability &le; 2<sup>-N</sup>)</li>
 *   <li><b>Auxiliary Space Complexity:</b> O(log N) recursive call stack</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class RandomizedQuickSort {

    public static final class GenomeSortItem {
        private final String id;
        private final String name;
        private final double sortValue;
        private final String metricName;
        private final String formattedValue;

        public GenomeSortItem(String id, String name, double sortValue, String metricName, String formattedValue) {
            this.id = id;
            this.name = name;
            this.sortValue = sortValue;
            this.metricName = metricName;
            this.formattedValue = formattedValue;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public double getSortValue() { return sortValue; }
        public String getMetricName() { return metricName; }
        public String getFormattedValue() { return formattedValue; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", id, name, formattedValue);
        }
    }

    public static final class SortTrace {
        private final int depth;
        private final int low;
        private final int high;
        private final int pivotIndex;
        private final double pivotValue;
        private final String pivotItemName;

        public SortTrace(int depth, int low, int high, int pivotIndex, double pivotValue, String pivotItemName) {
            this.depth = depth;
            this.low = low;
            this.high = high;
            this.pivotIndex = pivotIndex;
            this.pivotValue = pivotValue;
            this.pivotItemName = pivotItemName;
        }

        public int getDepth() { return depth; }
        public int getLow() { return low; }
        public int getHigh() { return high; }
        public int getPivotIndex() { return pivotIndex; }
        public double getPivotValue() { return pivotValue; }
        public String getPivotItemName() { return pivotItemName; }
    }

    public static final class QuickSortResult {
        private final List<GenomeSortItem> originalItems;
        private final List<GenomeSortItem> sortedItems;
        private final List<SortTrace> pivotTraces;
        private final int totalComparisons;
        private final int maxRecursionDepth;
        private final long executionTimeNs;
        private final String metric;

        public QuickSortResult(List<GenomeSortItem> originalItems, List<GenomeSortItem> sortedItems,
                              List<SortTrace> pivotTraces, int totalComparisons, int maxRecursionDepth,
                              long executionTimeNs, String metric) {
            this.originalItems = originalItems;
            this.sortedItems = sortedItems;
            this.pivotTraces = pivotTraces;
            this.totalComparisons = totalComparisons;
            this.maxRecursionDepth = maxRecursionDepth;
            this.executionTimeNs = executionTimeNs;
            this.metric = metric;
        }

        public List<GenomeSortItem> getOriginalItems() { return originalItems; }
        public List<GenomeSortItem> getSortedItems() { return sortedItems; }
        public List<SortTrace> getPivotTraces() { return pivotTraces; }
        public int getTotalComparisons() { return totalComparisons; }
        public int getMaxRecursionDepth() { return maxRecursionDepth; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getMetric() { return metric; }
        public String getComplexity() { return "Expected O(N log N), Space O(log N)"; }
    }

    private static final Random random = new Random();

    public static QuickSortResult sort(List<GenomeSortItem> items, boolean ascending) {
        long start = System.nanoTime();
        List<GenomeSortItem> original = new ArrayList<>(items);
        List<GenomeSortItem> working = new ArrayList<>(items);
        List<SortTrace> traces = new ArrayList<>();
        int[] comparisons = new int[1];
        int[] maxDepth = new int[1];

        if (working.size() > 1) {
            randomizedQuickSort(working, 0, working.size() - 1, ascending, traces, comparisons, 1, maxDepth);
        }

        long elapsedNs = System.nanoTime() - start;
        String metric = items.isEmpty() ? "Value" : items.get(0).getMetricName();
        return new QuickSortResult(original, working, traces, comparisons[0], maxDepth[0], elapsedNs, metric);
    }

    private static void randomizedQuickSort(List<GenomeSortItem> list, int low, int high, boolean ascending,
                                            List<SortTrace> traces, int[] comparisons, int depth, int[] maxDepth) {
        if (depth > maxDepth[0]) {
            maxDepth[0] = depth;
        }

        if (low < high) {
            // Pick a uniformly random pivot between low and high
            int randomIndex = low + random.nextInt(high - low + 1);
            swap(list, randomIndex, high);

            GenomeSortItem pivotItem = list.get(high);
            traces.add(new SortTrace(depth, low, high, randomIndex, pivotItem.getSortValue(), pivotItem.getName()));

            int partitionIdx = partition(list, low, high, ascending, comparisons);

            randomizedQuickSort(list, low, partitionIdx - 1, ascending, traces, comparisons, depth + 1, maxDepth);
            randomizedQuickSort(list, partitionIdx + 1, high, ascending, traces, comparisons, depth + 1, maxDepth);
        }
    }

    private static int partition(List<GenomeSortItem> list, int low, int high, boolean ascending, int[] comparisons) {
        double pivotVal = list.get(high).getSortValue();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            comparisons[0]++;
            boolean condition = ascending
                    ? list.get(j).getSortValue() <= pivotVal
                    : list.get(j).getSortValue() >= pivotVal;

            if (condition) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static void swap(List<GenomeSortItem> list, int i, int j) {
        GenomeSortItem tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }
}
