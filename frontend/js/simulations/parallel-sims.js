/**
 * BioGenome – Parallel Algorithms & Work-Span Simulations
 * 1. Blelloch Scan (Up-sweep Reduction & Down-sweep Distribution)
 * 2. Parallel Reduce (Parallel Reduction Tree with Worker Assignments)
 * 3. Brent's Theorem / Work-Span Bound Calculator & Visualizer
 */

const ParallelSimulations = {
  // -------------------------------------------------------------
  // 1. BLELLOCH SCAN (Exclusive Prefix Sum)
  // -------------------------------------------------------------
  generateBlellochScan(inputArray) {
    const states = [];
    // Ensure power of 2 length for clean tree
    let len = 1;
    while (len < inputArray.length) len *= 2;
    const arr = [...inputArray];
    while (arr.length < len) arr.push(0);

    const working = [...arr];

    states.push({
      operation: 'Blelloch Scan Initialization (Input Array)',
      explanation: `Loaded array of length ${arr.length}: <code>[${arr.join(', ')}]</code>. Blelloch scan computes exclusive prefix sum in <strong>O(n) Work</strong> and <strong>O(log n) Span</strong> via Up-Sweep (reduce) followed by Down-Sweep.`,
      math: `Array: [${arr.join(', ')}] | Length: ${len} (2^${Math.log2(len)})`,
      phase: 'init',
      array: [...working],
      phaseType: 'init',
      activeIndices: [],
      level: 0
    });

    // Up-Sweep (Reduce)
    for (let stride = 1; stride < len; stride *= 2) {
      const active = [];
      for (let i = 2 * stride - 1; i < len; i += 2 * stride) {
        working[i] = working[i] + working[i - stride];
        active.push(i);
      }

      states.push({
        operation: `Up-Sweep (Stride = ${stride})`,
        explanation: `Summed pairs at stride ${stride}. Nodes at indices [${active.join(', ')}] now hold subtree partial sums. Tree height is logarithmic.`,
        math: `Up-Sweep step: working[i] = working[i] + working[i - ${stride}]`,
        phase: 'upsweep',
        array: [...working],
        phaseType: 'upsweep',
        activeIndices: [...active],
        stride
      });
    }

    // Set root to 0
    const totalSum = working[len - 1];
    working[len - 1] = 0;

    states.push({
      operation: 'Up-Sweep Finished → Root Zeroed for Down-Sweep',
      explanation: `Total sum of array was <strong>${totalSum}</strong>. Replaced root element (index ${len - 1}) with <strong>0</strong> to initialize exclusive prefix sum down-sweep.`,
      math: `Root set to 0 | Ready for Down-Sweep`,
      phase: 'root_zero',
      array: [...working],
      phaseType: 'downsweep_start',
      activeIndices: [len - 1],
      totalSum
    });

    // Down-Sweep
    for (let stride = Math.floor(len / 2); stride > 0; stride = Math.floor(stride / 2)) {
      const active = [];
      for (let i = 2 * stride - 1; i < len; i += 2 * stride) {
        const leftIdx = i - stride;
        const rightIdx = i;
        const t = working[leftIdx];
        working[leftIdx] = working[rightIdx];
        working[rightIdx] = working[rightIdx] + t;
        active.push(leftIdx, rightIdx);
      }

      states.push({
        operation: `Down-Sweep (Stride = ${stride})`,
        explanation: `Distributed values downward: left child gets parent value; right child gets parent value + left child's previous value. Indices active: [${active.join(', ')}].`,
        math: `Left ← Parent, Right ← Parent + Old_Left`,
        phase: 'downsweep',
        array: [...working],
        phaseType: 'downsweep',
        activeIndices: [...active],
        stride
      });
    }

    states.push({
      operation: 'Blelloch Scan Complete (Exclusive Prefix Sum)',
      explanation: `Exclusive prefix sum successfully computed in parallel: <code>[${working.join(', ')}]</code>. Total work: O(n), Critical path: O(log n).`,
      math: `Result: [${working.join(', ')}] | Work: O(n) | Span: O(log n)`,
      phase: 'complete',
      array: [...working],
      phaseType: 'complete',
      activeIndices: [],
      totalSum
    });

    return states;
  },

  // -------------------------------------------------------------
  // 2. PARALLEL REDUCE
  // -------------------------------------------------------------
  generateParallelReduce(items) {
    const states = [];
    const arr = [...items];
    const n = arr.length;

    states.push({
      operation: 'Parallel Reduction Tree Initialization',
      explanation: `Array of <strong>${n}</strong> elements prepared for parallel associative aggregation (summation). Processors combine adjacent pairs concurrently in rounds.`,
      math: `Array: [${arr.join(', ')}] | Processors: ${Math.floor(n / 2)} in Round 1`,
      phase: 'init',
      levels: [[...arr]],
      currentRound: 0,
      activePairs: []
    });

    let current = [...arr];
    const allLevels = [current];
    let round = 1;

    while (current.length > 1) {
      const nextLevel = [];
      const pairs = [];
      const numWorkers = Math.ceil(current.length / 2);

      for (let i = 0; i < current.length; i += 2) {
        if (i + 1 < current.length) {
          const sum = current[i] + current[i + 1];
          nextLevel.push(sum);
          pairs.push({ worker: `Worker ${pairs.length + 1}`, left: current[i], right: current[i + 1], res: sum });
        } else {
          nextLevel.push(current[i]);
          pairs.push({ worker: `Worker ${pairs.length + 1}`, left: current[i], right: null, res: current[i] });
        }
      }

      current = nextLevel;
      allLevels.push(current);

      states.push({
        operation: `Round ${round}: Executed ${numWorkers} Parallel Reductions`,
        explanation: `${numWorkers} workers executed in parallel: ${pairs.map(p => `${p.worker}: (${p.left}${p.right !== null ? ` + ${p.right}` : ''} = ${p.res})`).join(', ')}. Remaining values: <code>[${current.join(', ')}]</code>.`,
        math: `Round ${round}: Processed ${pairs.length} pairs in 1 timestep | Array size: ${current.length}`,
        phase: 'round_complete',
        levels: JSON.parse(JSON.stringify(allLevels)),
        currentRound: round,
        activePairs: pairs
      });

      round++;
    }

    states.push({
      operation: 'Parallel Reduction Complete',
      explanation: `Final reduction scalar obtained: <strong>${current[0]}</strong>. Required <strong>${round - 1}</strong> parallel time steps (depth O(log n)) instead of ${n - 1} sequential steps.`,
      math: `Scalar Result = ${current[0]} | Steps = ${round - 1} (⌈log₂(${n})⌉)`,
      phase: 'complete',
      levels: JSON.parse(JSON.stringify(allLevels)),
      currentRound: round - 1,
      activePairs: []
    });

    return states;
  },

  // -------------------------------------------------------------
  // 3. BRENT'S THEOREM / WORK-SPAN
  // -------------------------------------------------------------
  calculateBrent(work, span, processors) {
    // Brent's Theorem bound:
    // T_p <= (T_1 - T_infty) / p + T_infty
    // Speedup = T_1 / T_p
    // Efficiency = Speedup / p
    const t1 = work;
    const tInf = span;
    const p = Math.max(1, processors);

    const brentBound = Math.ceil((t1 - tInf) / p + tInf);
    const lowerBound = Math.max(Math.ceil(t1 / p), tInf);
    const speedup = (t1 / brentBound).toFixed(2);
    const efficiency = ((t1 / (p * brentBound)) * 100).toFixed(1);

    return {
      work: t1,
      span: tInf,
      processors: p,
      brentBound,
      lowerBound,
      speedup,
      efficiency,
      theoreticalMaxSpeedup: (t1 / tInf).toFixed(2)
    };
  }
};

window.ParallelSimulations = ParallelSimulations;
