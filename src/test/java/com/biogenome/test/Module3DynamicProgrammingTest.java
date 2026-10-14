package com.biogenome.test;

import com.biogenome.dp.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class Module3DynamicProgrammingTest {

    @Test
    public void testNeedlemanWunsch() {
        NeedlemanWunschAligner nw = new NeedlemanWunschAligner(2, -1, -2);
        var res = nw.align("ATGCA", "ATGA");
        assertNotNull(res);
        assertTrue(res.getAlignmentScore() > 0);
        assertTrue(res.getIdentityPercentage() > 50.0);
    }

    @Test
    public void testSmithWaterman() {
        SmithWatermanAligner sw = new SmithWatermanAligner(3, -2, -2);
        var res = sw.align("CCATGCGATT", "ATGCA");
        assertNotNull(res);
        assertTrue(res.getAlignmentScore() > 0);
    }

    @Test
    public void testDamerauLevenshtein() {
        var res = DamerauLevenshteinAligner.align("ATGC", "ATCG");
        assertNotNull(res);
        assertEquals(1, res.getDistance()); // 1 transposition
    }

    @Test
    public void testBitmaskDP() {
        List<String> reads = List.of("ATGC", "TGCA", "GCAT");
        var res = GenomicBitmaskDP.assembleShortestCommonSuperstring(reads);
        assertNotNull(res);
        assertEquals("ATGCAT", res.getAssembledSequence());
    }

    @Test
    public void testAlignmentComparator() {
        var res = AlignmentComparator.compareAlignments("ATGCA", "ATGA", 2, -1, -2);
        assertEquals(3, res.getMethods().size());
    }
}
