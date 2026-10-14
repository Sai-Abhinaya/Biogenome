package com.biogenome.test;

import com.biogenome.model.DnaSequence;
import com.biogenome.suffix.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class Module2SuffixTest {

    @Test
    public void testSuffixArraySAIS() {
        String seq = "GATAGACA";
        SuffixArray sa = new SuffixArray(seq);
        assertEquals(8, sa.length());
        List<Integer> matches = sa.searchMotif("GATA");
        assertEquals(1, matches.size());
        assertEquals(0, matches.get(0));
    }

    @Test
    public void testKasaiLCP() {
        String seq = "GATAGACAGATAGATA";
        SuffixArray sa = new SuffixArray(seq);
        int[] lcp = KasaiLCP.computeLCPArray(seq, sa.getSuffixArray());
        assertNotNull(lcp);
        assertEquals(seq.length(), lcp.length);
        List<String> repeats = KasaiLCP.findLongestRepeatedSubstring(seq);
        assertFalse(repeats.isEmpty());
    }

    @Test
    public void testRepeatedPatternExplorer() {
        String seq = "CAGCAGCAGCAGCAGCAG";
        var res = RepeatedPatternExplorer.exploreRepeats(seq, 3);
        assertFalse(res.getRepeats().isEmpty());
        assertTrue(res.getMaxLcpValue() >= 3);
    }

    @Test
    public void testSuffixAutomaton() {
        SuffixAutomaton sam = new SuffixAutomaton("ATGCGATC");
        assertTrue(sam.contains("GCGA"));
        assertFalse(sam.contains("TTTT"));
        assertTrue(sam.countDistinctSubstrings() > 0);
    }
}
