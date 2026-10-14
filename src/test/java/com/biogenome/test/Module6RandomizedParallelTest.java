package com.biogenome.test;

import com.biogenome.model.ContinuousObservation;
import com.biogenome.randparallel.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class Module6RandomizedParallelTest {

    @Test
    public void testRandomizedQuickSort() {
        List<RandomizedQuickSort.GenomeSortItem> list = new ArrayList<>();
        list.add(new RandomizedQuickSort.GenomeSortItem("1", "G1", 50, "length", "50"));
        list.add(new RandomizedQuickSort.GenomeSortItem("2", "G2", 20, "length", "20"));
        list.add(new RandomizedQuickSort.GenomeSortItem("3", "G3", 80, "length", "80"));
        list.add(new RandomizedQuickSort.GenomeSortItem("4", "G4", 10, "length", "10"));

        var res = RandomizedQuickSort.sort(list, true);
        assertEquals(4, res.getSortedItems().size());
        assertEquals(10, (int) res.getSortedItems().get(0).getSortValue());
        assertEquals(80, (int) res.getSortedItems().get(3).getSortValue());
        assertTrue(res.getTotalComparisons() > 0);
    }

    @Test
    public void testReservoirSampling() {
        List<ContinuousObservation> stream = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            stream.add(new ContinuousObservation("OBS-" + i, "Q-Score", 35.0 + i, "Q", "2026-10-07", "NovaSeq"));
        }
        var res = ReservoirSampling.sampleStream(stream, 5);
        assertEquals(5, res.getFinalSample().size());
        assertEquals(20, res.getTotalStreamItems());
        assertEquals(0.25, res.getTheoreticalProbability(), 0.001);
    }

    @Test
    public void testMillerRabin() {
        var res1 = MillerRabinPrimality.testPrimality("1000000007", 10);
        assertTrue(res1.isPrime());

        var res2 = MillerRabinPrimality.testPrimality("1000000008", 10);
        assertFalse(res2.isPrime());
    }

    @Test
    public void testBlellochScan() {
        long[] input = {3, 1, 7, 0, 4, 1, 6, 3};
        var res = BlellochScan.scan(input);
        assertNotNull(res.getExclusivePrefixSum());
        assertEquals(0, res.getExclusivePrefixSum()[0]);
        assertEquals(3, res.getExclusivePrefixSum()[1]); // prefix before index 1
        assertEquals(4, res.getExclusivePrefixSum()[2]); // 3 + 1
        assertEquals(25, res.getInclusivePrefixSum()[7]); // 3+1+7+0+4+1+6+3 = 25
    }

    @Test
    public void testParallelReduce() {
        long[] input = {100, 200, 150, 300};
        var res = ParallelReduce.reduce(input);
        assertEquals(750, res.getFinalResult());
        assertTrue(res.getSpanSteps() > 0);
    }

    @Test
    public void testBrentsLaw() {
        var res = BrentsLawCalculator.calculate(1000, 50, 16);
        assertNotNull(res);
        assertTrue(res.getEstimatedParallelTime() > 50);
        assertTrue(res.getSpeedup() > 1.0);
        assertTrue(res.getEfficiency() <= 1.0);
    }
}
