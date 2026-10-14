package com.biogenome.test;

import com.biogenome.np.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class Module5NpCompletenessTest {

    @Test
    public void test3SatSolver() {
        String cnf = "(A | B | ~C) & (~A | B | C) & (~B | ~C | A)";
        List<SatClause> clauses = ThreeSatSolver.parseCnf(cnf);
        var res = ThreeSatSolver.solve(clauses);
        assertTrue(res.isSatisfiable());
        assertNotNull(res.getModel());
    }

    @Test
    public void testSatToCliqueReduction() {
        String cnf = "(A | B | C) & (~A | B | C)";
        List<SatClause> clauses = ThreeSatSolver.parseCnf(cnf);
        var res = SatToCliqueReduction.reduceAndSolve(clauses);
        assertEquals(2, res.getRequiredCliqueSize());
        assertTrue(res.isSatisfied());
        assertEquals(2, res.getFoundClique().size());
    }

    @Test
    public void testCliqueToIndependentSet() {
        int n = 3;
        List<String> names = Arrays.asList("A", "B", "C");
        List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{1, 2}, new int[]{0, 2}); // Triangle (Clique of size 3)
        var res = CliqueToIndependentSet.reduceAndSolve(n, names, edges);
        assertEquals(3, res.getMaxCliqueInOriginal().size());
        assertEquals(3, res.getSize());
    }

    @Test
    public void testIndependentSetToVertexCover() {
        int n = 4;
        List<String> names = Arrays.asList("A", "B", "C", "D");
        List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{1, 2}, new int[]{2, 3});
        var res = IndependentSetToVertexCover.computeDuality(n, names, edges);
        assertTrue(res.isDualityVerified());
        assertEquals(4, res.getIndependentSetSize() + res.getVertexCoverSize());
    }

    @Test
    public void testVertexCover2Approximation() {
        int n = 4;
        List<String> names = Arrays.asList("A", "B", "C", "D");
        List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{1, 2}, new int[]{2, 3});
        var res = VertexCoverApproximation.compute2Approximation(n, names, edges);
        assertTrue(res.getApproxCoverSize() <= 2 * res.getExactOptSize());
        assertTrue(res.getEmpiricalRatio() <= 2.0);
    }
}
