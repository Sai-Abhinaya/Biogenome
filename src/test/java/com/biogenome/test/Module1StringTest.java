package com.biogenome.test;

import com.biogenome.string.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class Module1StringTest {

    @Test
    public void testKMP() {
        String text = "AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC";
        String pattern = "AGCT";
        int[] pi = KMPMotifFinder.computePrefixFunction(pattern);
        assertNotNull(pi);
        assertEquals(4, pi.length);
        List<Integer> matches = KMPMotifFinder.searchMotif(text, pattern);
        assertFalse(matches.isEmpty());
        assertEquals(0, matches.get(0));
    }

    @Test
    public void testZAlgorithm() {
        String text = "AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC";
        String pattern = "AGCT";
        List<Integer> matches = ZAlgorithm.searchMotif(text, pattern);
        assertFalse(matches.isEmpty());
        assertEquals(0, matches.get(0));
    }

    @Test
    public void testRabinKarp() {
        String text = "AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC";
        String pattern = "AGCT";
        RabinKarpMatcher.SearchStats stats = RabinKarpMatcher.searchWithDetailedStats(text, pattern);
        assertFalse(stats.getMatches().isEmpty());
        assertEquals(0, stats.getMatches().get(0));
    }

    @Test
    public void testAhoCorasick() {
        AhoCorasickMatcher matcher = new AhoCorasickMatcher();
        matcher.addMotif("EcoRI", "GAATTC");
        matcher.addMotif("AluI", "AGCT");
        matcher.buildAutomaton();
        String text = "AGCTTTTCATTCTGACTGCAAGAATTC";
        var matches = matcher.search(text);
        assertEquals(2, matches.size());
    }

    @Test
    public void testStringAlgorithmComparator() {
        String text = "ATGCGATCGATCGGCTA";
        String pattern = "GATC";
        var res = StringAlgorithmComparator.compareAll(text, pattern);
        assertTrue(res.isAllMatchesConsistent());
        assertEquals(3, res.getBenchmarks().size());
    }
}
