/**
 * BioGenome – NP-Completeness Reductions & Approximation Simulations
 * 1. 3-SAT (Clause Evaluation & Truth Assignments)
 * 2. 3-SAT → CLIQUE (Polynomial Time Reduction)
 * 3. CLIQUE → INDEPENDENT SET (Graph Complement Transformation)
 * 4. INDEPENDENT SET → VERTEX COVER (Complementary Subset Duality)
 * 5. Vertex Cover 2-Approximation (Maximal Matching Heuristic)
 */

const NpSimulations = {
  // -------------------------------------------------------------
  // 1. 3-SAT
  // -------------------------------------------------------------
  generateThreeSat(variables, clauses, assignment = {}) {
    const states = [];
    const varKeys = Object.keys(assignment);

    // Initial state
    states.push({
      operation: '3-SAT Formula Configuration',
      explanation: `Configured Boolean Satisfiability instance with <strong>${variables.length}</strong> variables and <strong>${clauses.length}</strong> disjunctive clauses. All clauses must evaluate to True simultaneously.`,
      math: clauses.map(c => `(${c.join(' ∨ ')})`).join(' ∧ '),
      phase: 'init',
      variables,
      clauses,
      assignment: { ...assignment },
      evaluatedClauses: clauses.map(() => null),
      isSatisfied: false
    });

    const evaluated = [];
    for (let i = 0; i < clauses.length; i++) {
      const clause = clauses[i];
      let clauseTrue = false;
      let trueLiteral = null;

      for (const lit of clause) {
        const isNeg = lit.startsWith('~');
        const varName = isNeg ? lit.substring(1) : lit;
        const val = assignment[varName];

        if (val !== undefined) {
          const litVal = isNeg ? !val : val;
          if (litVal) {
            clauseTrue = true;
            trueLiteral = lit;
            break;
          }
        }
      }

      evaluated.push(clauseTrue);

      states.push({
        operation: `Evaluating Clause #${i + 1}: (${clause.join(' ∨ ')})`,
        explanation: clauseTrue
          ? `Clause #${i + 1} satisfied by literal <strong>'${trueLiteral}' = True</strong>.`
          : `Clause #${i + 1} evaluates to <strong>False</strong> under current variable assignments.`,
        math: `Clause ${i + 1}: ${clauseTrue ? 'SATISFIED (True)' : 'UNSATISFIED (False)'}`,
        phase: 'eval_clause',
        variables,
        clauses,
        assignment: { ...assignment },
        evaluatedClauses: [...evaluated],
        isSatisfied: false
      });
    }

    const allSatisfied = evaluated.every(e => e === true);

    states.push({
      operation: allSatisfied ? 'Formula Satisfied (SAT)' : 'Formula Unsatisfied',
      explanation: allSatisfied
        ? `All ${clauses.length} clauses satisfied! Current truth assignment is a valid certifying witness for the NP-complete 3-SAT decision problem.`
        : `At least one clause remains unsatisfied. Adjust variable toggles to seek a satisfying assignment.`,
      math: `Result: ${allSatisfied ? 'SAT (Witness Valid)' : 'UNSAT'}`,
      phase: 'complete',
      variables,
      clauses,
      assignment: { ...assignment },
      evaluatedClauses: [...evaluated],
      isSatisfied: allSatisfied
    });

    return states;
  },

  // -------------------------------------------------------------
  // 2. 3-SAT → CLIQUE REDUCTION
  // -------------------------------------------------------------
  generateThreeSatToClique(variables, clauses) {
    const states = [];
    const k = clauses.length; // target clique size

    // Construct vertices: for each clause, 3 vertices
    const vertices = [];
    let vId = 0;
    for (let c = 0; c < clauses.length; c++) {
      for (let p = 0; p < clauses[c].length; p++) {
        vertices.push({
          id: vId++,
          clauseIdx: c,
          literal: clauses[c][p],
          name: `C${c + 1}:${clauses[c][p]}`
        });
      }
    }

    states.push({
      operation: 'Step 1: Construct Clause Clusters (Triplets)',
      explanation: `Karp's reduction creates 3 vertices for each of the ${k} clauses, resulting in ${vertices.length} total vertices. Vertices within the same clause form an independent set (no intra-clause edges).`,
      math: `|V| = 3k = ${vertices.length} vertices | Target Clique Size k = ${k}`,
      phase: 'construct_vertices',
      clauses,
      vertices: JSON.parse(JSON.stringify(vertices)),
      edges: [],
      cliqueVertices: []
    });

    // Edges between compatible literals in DIFFERENT clauses
    const edges = [];
    for (let i = 0; i < vertices.length; i++) {
      for (let j = i + 1; j < vertices.length; j++) {
        const u = vertices[i];
        const v = vertices[j];

        // Different clauses and not contradictory
        if (u.clauseIdx !== v.clauseIdx) {
          const isOpposite = (u.literal === `~${v.literal}`) || (`~${u.literal}` === v.literal);
          if (!isOpposite) {
            edges.push({ u: u.id, v: v.id, uLit: u.literal, vLit: v.literal });
          }
        }
      }
    }

    states.push({
      operation: 'Step 2: Connect Compatible Inter-Clause Literals',
      explanation: `Added <strong>${edges.length}</strong> edges. Edges connect vertices in <em>different</em> clauses that are not logically contradictory (i.e. x is never connected to ~x).`,
      math: `Compatible edges added: ${edges.length}`,
      phase: 'construct_edges',
      clauses,
      vertices: JSON.parse(JSON.stringify(vertices)),
      edges: JSON.parse(JSON.stringify(edges)),
      cliqueVertices: []
    });

    // Identify a k-clique if one exists (taking first literal of each clause if compatible)
    const candidateClique = [];
    // Greedy heuristic to find 1 vertex per clause that are all connected
    for (let c = 0; c < clauses.length; c++) {
      const clusterVerts = vertices.filter(v => v.clauseIdx === c);
      for (const v of clusterVerts) {
        // check compatibility with all currently picked
        const allComp = candidateClique.every(cand => {
          return edges.some(e => (e.u === cand.id && e.v === v.id) || (e.u === v.id && e.v === cand.id));
        });
        if (allComp) {
          candidateClique.push(v);
          break;
        }
      }
    }

    states.push({
      operation: `Step 3: Locate k-Clique of Size k=${k}`,
      explanation: candidateClique.length === k
        ? `Found <strong>k-Clique</strong> of size ${k}: <code>[${candidateClique.map(c => c.name).join(', ')}]</code>! This clique directly corresponds to a satisfying assignment where each selected literal is True!`
        : `Located compatible subset of size ${candidateClique.length}. A k-clique exists if and only if the original 3-SAT formula is satisfiable.`,
      math: `k-Clique size = ${candidateClique.length} / ${k} target`,
      phase: 'complete',
      clauses,
      vertices: JSON.parse(JSON.stringify(vertices)),
      edges: JSON.parse(JSON.stringify(edges)),
      cliqueVertices: candidateClique.map(c => c.id)
    });

    return states;
  },

  // -------------------------------------------------------------
  // 3. CLIQUE → INDEPENDENT SET (Graph Complement)
  // -------------------------------------------------------------
  generateCliqueToIndependentSet(graphData) {
    const states = [];
    const { nodes, edges } = graphData;

    states.push({
      operation: 'Input Graph G: Searching for Maximum Clique',
      explanation: `Given graph G with <strong>${nodes.length}</strong> vertices and <strong>${edges.length}</strong> edges. A CLIQUE is a subset of vertices where every pair is connected by an edge.`,
      math: `Graph G: |V| = ${nodes.length}, |E| = ${edges.length}`,
      phase: 'original_graph',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      isComplement: false,
      selectedSet: []
    });

    // Complement graph G'
    const compEdges = [];
    for (let i = 0; i < nodes.length; i++) {
      for (let j = i + 1; j < nodes.length; j++) {
        const u = nodes[i];
        const v = nodes[j];
        const hasEdge = edges.some(e => (e.u === u && e.v === v) || (e.u === v && e.v === u));
        if (!hasEdge) {
          compEdges.push({ u, v });
        }
      }
    }

    states.push({
      operation: "Construct Complement Graph G' (Edge Inversion)",
      explanation: `Constructed complement graph G'. Edges that were present in G are removed; pairs that were non-adjacent in G are now connected by an edge.`,
      math: `Complement |E'| = (${nodes.length}×${nodes.length - 1})/2 - ${edges.length} = ${compEdges.length}`,
      phase: 'complement_graph',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(compEdges)),
      isComplement: true,
      selectedSet: []
    });

    states.push({
      operation: "Equivalence: Clique in G ≡ Independent Set in G'",
      explanation: `Every k-clique in G corresponds to an <strong>Independent Set of size k</strong> in G', because no two vertices in a clique can share a complement edge!`,
      math: `CLIQUE(G, k) ⇔ INDEPENDENT_SET(G', k)`,
      phase: 'complete',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(compEdges)),
      isComplement: true,
      selectedSet: nodes.slice(0, 3)
    });

    return states;
  },

  // -------------------------------------------------------------
  // 4. INDEPENDENT SET → VERTEX COVER
  // -------------------------------------------------------------
  generateIndependentSetToVertexCover(graphData) {
    const states = [];
    const { nodes, edges } = graphData;

    states.push({
      operation: 'Input Graph G for Subset Complementarity',
      explanation: `Demonstrating the exact mathematical duality: <strong>S is an Independent Set if and only if V \\ S is a Vertex Cover</strong>.`,
      math: `|V| = ${nodes.length}, |E| = ${edges.length}`,
      phase: 'init',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      independentSet: [],
      vertexCover: []
    });

    // Pick an independent set (e.g. non-adjacent nodes)
    const indep = [];
    for (const u of nodes) {
      const adjacentToIndep = indep.some(x => {
        return edges.some(e => (e.u === x && e.v === u) || (e.u === u && e.v === x));
      });
      if (!adjacentToIndep) indep.push(u);
    }

    const vCover = nodes.filter(u => !indep.includes(u));

    states.push({
      operation: `Independent Set S Selected (|S| = ${indep.length})`,
      explanation: `Selected Independent Set S = <code>[${indep.join(', ')}]</code>. No two vertices in S share an edge.`,
      math: `Independent Set size: k = ${indep.length}`,
      phase: 'independent_set',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      independentSet: [...indep],
      vertexCover: []
    });

    states.push({
      operation: `Vertex Cover V \\ S Derived (|V \\ S| = ${vCover.length})`,
      explanation: `The complement set <strong>V \\ S = [${vCover.join(', ')}]</strong> is guaranteed to be a valid <strong>Vertex Cover</strong> of size ${vCover.length}! Every edge in the graph has at least one endpoint in V \\ S.`,
      math: `Vertex Cover Size: |V| - k = ${nodes.length} - ${indep.length} = ${vCover.length}`,
      phase: 'complete',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      independentSet: [...indep],
      vertexCover: [...vCover]
    });

    return states;
  },

  // -------------------------------------------------------------
  // 5. VERTEX COVER 2-APPROXIMATION
  // -------------------------------------------------------------
  generateVertexCoverApprox(graphData) {
    const states = [];
    const { nodes, edges } = graphData;
    const cover = new Set();
    const activeEdges = JSON.parse(JSON.stringify(edges));
    const chosenEdges = [];

    states.push({
      operation: 'Vertex Cover 2-Approximation Initialization',
      explanation: `Executing the classical polynomial-time <strong>2-Approximation Algorithm</strong>. Iteratively picks an arbitrary uncovered edge (u, v) and adds <em>both</em> endpoints to the cover.`,
      math: `Guarantee: |Cover| ≤ 2 × |OPT| | Current Cover = Ø`,
      phase: 'init',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      cover: [],
      chosenEdges: []
    });

    while (activeEdges.length > 0) {
      const e = activeEdges.shift();
      if (!cover.has(e.u) && !cover.has(e.v)) {
        cover.add(e.u);
        cover.add(e.v);
        chosenEdges.push(e);

        states.push({
          operation: `Selected Edge (${e.u}, ${e.v}) → Added Both Endpoints`,
          explanation: `Uncovered edge <strong>(${e.u}, ${e.v})</strong> found. Adding both <strong>'${e.u}'</strong> and <strong>'${e.v}'</strong> to vertex cover. All incident edges now eliminated.`,
          math: `Cover: [${Array.from(cover).join(', ')}] (Size: ${cover.size})`,
          phase: 'add_edge_nodes',
          nodes: [...nodes],
          edges: JSON.parse(JSON.stringify(edges)),
          cover: Array.from(cover),
          chosenEdges: [...chosenEdges]
        });
      }
    }

    states.push({
      operation: '2-Approximation Complete',
      explanation: `All edges covered! Final vertex cover size: <strong>${cover.size}</strong>: <code>[${Array.from(cover).join(', ')}]</code>.<br>
        <strong>Note:</strong> This is a guaranteed 2-approximation (not necessarily strictly minimal OPT).`,
      math: `Final Size = ${cover.size} ≤ 2 × OPT | Polynomial Time: O(V + E)`,
      phase: 'complete',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      cover: Array.from(cover),
      chosenEdges: [...chosenEdges]
    });

    return states;
  }
};

window.NpSimulations = NpSimulations;
