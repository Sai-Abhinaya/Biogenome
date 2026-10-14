package com.biogenome.test;

import com.biogenome.flow.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class Module4NetworkFlowTest {

    private FlowNetwork createSampleDiamondNetwork() {
        // 0: s, 1: u, 2: v, 3: t
        FlowNetwork net = new FlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 10);
        net.addEdge(2, 3, 10);
        return net;
    }

    @Test
    public void testFordFulkerson() {
        FlowNetwork net = createSampleDiamondNetwork();
        FlowResult res = FordFulkerson.computeMaxFlow(net, 0, 3);
        assertEquals(20, res.getMaxFlow());
        assertEquals(20, res.getMinCutCapacity());
    }

    @Test
    public void testEdmondsKarp() {
        FlowNetwork net = createSampleDiamondNetwork();
        FlowResult res = EdmondsKarp.computeMaxFlow(net, 0, 3);
        assertEquals(20, res.getMaxFlow());
        assertEquals(20, res.getMinCutCapacity());
    }

    @Test
    public void testDinic() {
        FlowNetwork net = createSampleDiamondNetwork();
        FlowResult res = DinicAlgorithm.computeMaxFlow(net, 0, 3);
        assertEquals(20, res.getMaxFlow());
        assertEquals(20, res.getMinCutCapacity());
    }

    @Test
    public void testBipartiteMatchingAndKonig() {
        List<String> researchers = Arrays.asList("R1", "R2", "R3");
        List<String> experiments = Arrays.asList("E1", "E2", "E3");
        Map<Integer, List<Integer>> eligibility = new HashMap<>();
        eligibility.put(0, Arrays.asList(0, 1));
        eligibility.put(1, Collections.singletonList(1));
        eligibility.put(2, Collections.singletonList(2));

        var res = BipartiteMatching.computeMatching(researchers, experiments, eligibility);
        assertEquals(3, res.getMatchingSize());
        assertTrue(res.isKonigTheoremSatisfied());
        assertEquals(3, res.getMinVertexCoverSize());
    }
}
