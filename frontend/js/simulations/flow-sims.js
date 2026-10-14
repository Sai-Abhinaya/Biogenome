/**
 * BioGenome – Network Flow & Matching Simulations
 * 1. Ford-Fulkerson (DFS Augmenting Paths)
 * 2. Edmonds-Karp (BFS Shortest Augmenting Paths)
 * 3. Dinic's Algorithm (Level Graph & Blocking Flow)
 * 4. Bipartite Matching (Researcher-Experiment Allocation)
 * 5. König's Theorem (Max Matching ↔ Min Vertex Cover Equivalence)
 * 6. Max-Flow Min-Cut Theorem (Residual Reachability & Cut Capacity)
 */

const FlowSimulations = {
  // -------------------------------------------------------------
  // 1. FORD-FULKERSON & EDMONDS-KARP
  // -------------------------------------------------------------
  generateNetworkFlow(networkData, algorithm = 'edmonds-karp') {
    const states = [];
    const { nodes, edges, source, sink } = networkData;

    // Build adjacency list & capacity matrix
    const adj = {};
    const cap = {};
    const flow = {};

    for (const u of nodes) {
      adj[u] = [];
      cap[u] = {};
      flow[u] = {};
      for (const v of nodes) {
        cap[u][v] = 0;
        flow[u][v] = 0;
      }
    }

    for (const e of edges) {
      adj[e.u].push(e.v);
      adj[e.v].push(e.u); // reverse residual edge
      cap[e.u][e.v] = e.cap;
    }

    let totalFlow = 0;
    const isBfs = algorithm === 'edmonds-karp';

    states.push({
      operation: `Network Flow Initial State (${isBfs ? 'Edmonds-Karp BFS' : 'Ford-Fulkerson DFS'})`,
      explanation: `Initialized biological distribution network with <strong>${nodes.length}</strong> facilities and <strong>${edges.length}</strong> transfer pipelines from <strong>Source: ${source}</strong> to <strong>Sink: ${sink}</strong>.`,
      math: `Total Flow = 0 | Method: ${isBfs ? 'BFS Shortest Augmenting Path' : 'DFS Augmenting Path'}`,
      phase: 'init',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      flow: JSON.parse(JSON.stringify(flow)),
      totalFlow: 0,
      activePath: [],
      bottleneck: 0,
      minCutS: [],
      minCutT: []
    });

    while (true) {
      // Find augmenting path
      const parent = {};
      for (const u of nodes) parent[u] = null;
      parent[source] = source;

      let found = false;

      if (isBfs) {
        // BFS
        const queue = [source];
        while (queue.length > 0) {
          const u = queue.shift();
          if (u === sink) {
            found = true;
            break;
          }
          for (const v of adj[u]) {
            const residual = cap[u][v] - flow[u][v];
            if (parent[v] === null && residual > 0) {
              parent[v] = u;
              queue.push(v);
            }
          }
        }
      } else {
        // DFS
        const stack = [source];
        while (stack.length > 0) {
          const u = stack.pop();
          if (u === sink) {
            found = true;
            break;
          }
          for (const v of adj[u]) {
            const residual = cap[u][v] - flow[u][v];
            if (parent[v] === null && residual > 0) {
              parent[v] = u;
              stack.push(v);
            }
          }
        }
      }

      if (!found) break;

      // Reconstruct path
      const path = [];
      let curr = sink;
      let bottleneck = Infinity;

      while (curr !== source) {
        const prev = parent[curr];
        path.unshift({ u: prev, v: curr });
        const residual = cap[prev][curr] - flow[prev][curr];
        if (residual < bottleneck) bottleneck = residual;
        curr = prev;
      }

      states.push({
        operation: `Augmenting Path Located (${isBfs ? 'BFS Queue' : 'DFS Search'})`,
        explanation: `Identified augmenting path: <code>${path.map(p => `${p.u} → ${p.v}`).join(', ')}</code>.<br>
          Bottleneck residual capacity on this path is <strong>${bottleneck}</strong> units.`,
        math: `Path: ${path.map(p => p.u).join(' → ')} → ${sink} | Bottleneck = ${bottleneck}`,
        phase: 'path_found',
        nodes: [...nodes],
        edges: JSON.parse(JSON.stringify(edges)),
        flow: JSON.parse(JSON.stringify(flow)),
        totalFlow,
        activePath: [...path],
        bottleneck,
        minCutS: [],
        minCutT: []
      });

      // Augment flow
      for (const p of path) {
        flow[p.u][p.v] += bottleneck;
        flow[p.v][p.u] -= bottleneck;
      }
      totalFlow += bottleneck;

      states.push({
        operation: `Flow Pushed (+${bottleneck} units)`,
        explanation: `Augmented ${bottleneck} units along the path. Residual capacities decreased forward and increased backward. Total network flow is now <strong>${totalFlow}</strong> units.`,
        math: `Total Flow = ${totalFlow} (added ${bottleneck})`,
        phase: 'flow_augmented',
        nodes: [...nodes],
        edges: JSON.parse(JSON.stringify(edges)),
        flow: JSON.parse(JSON.stringify(flow)),
        totalFlow,
        activePath: [...path],
        bottleneck,
        minCutS: [],
        minCutT: []
      });
    }

    // Identify Min-Cut (Nodes reachable from source in residual graph)
    const visited = {};
    for (const u of nodes) visited[u] = false;
    const q = [source];
    visited[source] = true;

    while (q.length > 0) {
      const u = q.shift();
      for (const v of adj[u]) {
        if (!visited[v] && cap[u][v] - flow[u][v] > 0) {
          visited[v] = true;
          q.push(v);
        }
      }
    }

    const minCutS = nodes.filter(u => visited[u]);
    const minCutT = nodes.filter(u => !visited[u]);

    states.push({
      operation: 'Maximum Flow & Minimum Cut Theorem Verified',
      explanation: `No further augmenting paths exist in the residual network.<br>
        <strong>Maximum Flow:</strong> <strong>${totalFlow}</strong> units.<br>
        <strong>Source Partition (S):</strong> [${minCutS.join(', ')}]<br>
        <strong>Sink Partition (T):</strong> [${minCutT.join(', ')}]<br>
        By the <strong>Max-Flow Min-Cut Theorem</strong>, the capacity of the edges separating S and T is exactly equal to the maximum flow (${totalFlow}).`,
      math: `Max Flow = ${totalFlow} ≡ Min Cut Capacity = ${totalFlow}`,
      phase: 'complete',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      flow: JSON.parse(JSON.stringify(flow)),
      totalFlow,
      activePath: [],
      bottleneck: 0,
      minCutS,
      minCutT
    });

    return states;
  },

  // -------------------------------------------------------------
  // 2. DINIC'S ALGORITHM (Level Graph & Blocking Flow)
  // -------------------------------------------------------------
  generateDinic(networkData) {
    const states = [];
    const { nodes, edges, source, sink } = networkData;

    // Adjacency and capacity
    const adj = {};
    const cap = {};
    const flow = {};
    for (const u of nodes) {
      adj[u] = [];
      cap[u] = {};
      flow[u] = {};
      for (const v of nodes) {
        cap[u][v] = 0;
        flow[u][v] = 0;
      }
    }
    for (const e of edges) {
      adj[e.u].push(e.v);
      adj[e.v].push(e.u);
      cap[e.u][e.v] = e.cap;
    }

    let totalFlow = 0;
    let iteration = 1;

    states.push({
      operation: "Dinic's Algorithm Initialization",
      explanation: `Dinic accelerates flow computation by decomposing the network into <strong>Level Graphs</strong> via BFS, then pushing <strong>Blocking Flows</strong> along admissible edges via DFS in O(V²E) time.`,
      math: `Total Flow = 0 | Iteration: 1`,
      phase: 'init',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      flow: JSON.parse(JSON.stringify(flow)),
      levels: {},
      totalFlow: 0
    });

    while (true) {
      // Step A: Build Level Graph via BFS
      const level = {};
      for (const u of nodes) level[u] = -1;
      level[source] = 0;
      const q = [source];

      while (q.length > 0) {
        const u = q.shift();
        for (const v of adj[u]) {
          if (level[v] < 0 && cap[u][v] - flow[u][v] > 0) {
            level[v] = level[u] + 1;
            q.push(v);
          }
        }
      }

      if (level[sink] < 0) break; // Sink unreachable in residual network

      states.push({
        operation: `Level Graph #${iteration} Constructed (BFS)`,
        explanation: `BFS constructed level assignments: ${nodes.map(u => `${u}: Level ${level[u]}`).join(', ')}.<br>
          Only edges satisfying <code>level[v] == level[u] + 1</code> are admissible for blocking flow.`,
        math: `Sink ${sink} reached at Level ${level[sink]}`,
        phase: 'level_graph',
        nodes: [...nodes],
        edges: JSON.parse(JSON.stringify(edges)),
        flow: JSON.parse(JSON.stringify(flow)),
        levels: { ...level },
        totalFlow
      });

      // Step B: Push Blocking Flow via DFS
      const ptr = {};
      for (const u of nodes) ptr[u] = 0;

      function dfs(u, pushed) {
        if (pushed === 0 || u === sink) return pushed;
        for (let cid = ptr[u]; cid < adj[u].length; cid++) {
          ptr[u] = cid;
          const v = adj[u][cid];
          const tr = cap[u][v] - flow[u][v];
          if (level[v] === level[u] + 1 && tr > 0) {
            const trPushed = dfs(v, Math.min(pushed, tr));
            if (trPushed > 0) {
              flow[u][v] += trPushed;
              flow[v][u] -= trPushed;
              return trPushed;
            }
          }
        }
        return 0;
      }

      let iterationPushed = 0;
      while (true) {
        const pushed = dfs(source, Infinity);
        if (pushed <= 0) break;
        iterationPushed += pushed;
        totalFlow += pushed;
      }

      states.push({
        operation: `Blocking Flow Pushed (+${iterationPushed} units)`,
        explanation: `DFS pushed a maximal blocking flow of <strong>${iterationPushed}</strong> units along admissible DAG levels. Total flow is now <strong>${totalFlow}</strong>.`,
        math: `Blocking Flow: +${iterationPushed} | Total Flow: ${totalFlow}`,
        phase: 'blocking_flow',
        nodes: [...nodes],
        edges: JSON.parse(JSON.stringify(edges)),
        flow: JSON.parse(JSON.stringify(flow)),
        levels: { ...level },
        totalFlow
      });

      iteration++;
    }

    states.push({
      operation: "Dinic's Algorithm Complete",
      explanation: `No further admissible level paths to sink exist. Maximum flow converged to <strong>${totalFlow}</strong> units in strictly O(V²E) time.`,
      math: `Max Flow = ${totalFlow}`,
      phase: 'complete',
      nodes: [...nodes],
      edges: JSON.parse(JSON.stringify(edges)),
      flow: JSON.parse(JSON.stringify(flow)),
      levels: {},
      totalFlow
    });

    return states;
  },

  // -------------------------------------------------------------
  // 3. BIPARTITE MATCHING & KÖNIG'S THEOREM
  // -------------------------------------------------------------
  generateBipartiteMatching(matchingData) {
    const states = [];
    const { left, right, edges } = matchingData;

    // Matching arrays
    const matchL = new Array(left.length).fill(-1);
    const matchR = new Array(right.length).fill(-1);

    states.push({
      operation: 'Bipartite Matching Initialization',
      explanation: `Initialized matching problem: <strong>${left.length} Researchers</strong> (Left Set U) to be matched with <strong>${right.length} Experimental Projects</strong> (Right Set V).`,
      math: `|U| = ${left.length}, |V| = ${right.length}, |E| = ${edges.length}`,
      phase: 'init',
      left: [...left],
      right: [...right],
      edges: JSON.parse(JSON.stringify(edges)),
      matchL: [...matchL],
      matchR: [...matchR],
      activeEdge: null,
      konigCover: []
    });

    // Augmenting path algorithm
    function tryKuhn(u, visited) {
      for (const e of edges) {
        if (e.u === u) {
          const v = e.v;
          if (!visited[v]) {
            visited[v] = true;
            if (matchR[v] < 0 || tryKuhn(matchR[v], visited)) {
              matchL[u] = v;
              matchR[v] = u;
              return true;
            }
          }
        }
      }
      return false;
    }

    let matchCount = 0;
    for (let u = 0; u < left.length; u++) {
      const visited = new Array(right.length).fill(false);
      states.push({
        operation: `Searching Alternating Path for: ${left[u]}`,
        explanation: `Attempting to find an augmenting path starting at unmatched researcher <strong>"${left[u]}"</strong>.`,
        math: `Testing Researcher #${u}: "${left[u]}"`,
        phase: 'search_augment',
        left: [...left],
        right: [...right],
        edges: JSON.parse(JSON.stringify(edges)),
        matchL: [...matchL],
        matchR: [...matchR],
        activeEdge: { u, v: -1 },
        konigCover: []
      });

      if (tryKuhn(u, visited)) {
        matchCount++;
        states.push({
          operation: `Match Established! (${left[u]} ↔ ${right[matchL[u]]})`,
          explanation: `Augmenting path found! Assigned <strong>"${left[u]}"</strong> to <strong>"${right[matchL[u]]}"</strong>. Total matches: <strong>${matchCount}</strong>.`,
          math: `Matches = ${matchCount}`,
          phase: 'match_augmented',
          left: [...left],
          right: [...right],
          edges: JSON.parse(JSON.stringify(edges)),
          matchL: [...matchL],
          matchR: [...matchR],
          activeEdge: { u, v: matchL[u] },
          konigCover: []
        });
      }
    }

    // König's Theorem: Minimum Vertex Cover from Maximum Matching
    // Find alternating reachability
    const zLeft = new Set();
    const zRight = new Set();
    const freeLeft = [];
    for (let u = 0; u < left.length; u++) {
      if (matchL[u] < 0) freeLeft.push(u);
    }

    const q = [...freeLeft];
    freeLeft.forEach(u => zLeft.add(u));

    while (q.length > 0) {
      const u = q.shift();
      for (const e of edges) {
        if (e.u === u && matchL[u] !== e.v) {
          const v = e.v;
          if (!zRight.has(v)) {
            zRight.add(v);
            const matchedPartner = matchR[v];
            if (matchedPartner >= 0 && !zLeft.has(matchedPartner)) {
              zLeft.add(matchedPartner);
              q.push(matchedPartner);
            }
          }
        }
      }
    }

    // Min Vertex Cover = (Left \ Z_Left) U (Right ∩ Z_Right)
    const coverLeft = [];
    for (let u = 0; u < left.length; u++) {
      if (!zLeft.has(u)) coverLeft.push(`Left: ${left[u]}`);
    }
    const coverRight = [];
    for (const v of zRight) {
      coverRight.push(`Right: ${right[v]}`);
    }
    const konigCover = [...coverLeft, ...coverRight];

    states.push({
      operation: "Maximum Matching & König's Theorem Complete",
      explanation: `Maximum Cardinality Bipartite Matching found <strong>${matchCount}</strong> pairs.<br>
        By <strong>König's Theorem</strong>, in bipartite graphs: <strong>Size of Max Matching (${matchCount}) == Size of Min Vertex Cover (${konigCover.length})</strong>.<br>
        Minimum vertex cover nodes: <code>[${konigCover.join(', ')}]</code>.`,
      math: `|Max Matching| = ${matchCount} ≡ |Min Vertex Cover| = ${konigCover.length}`,
      phase: 'complete',
      left: [...left],
      right: [...right],
      edges: JSON.parse(JSON.stringify(edges)),
      matchL: [...matchL],
      matchR: [...matchR],
      activeEdge: null,
      konigCover
    });

    return states;
  }
};

window.FlowSimulations = FlowSimulations;
