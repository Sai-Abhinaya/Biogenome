/**
 * BioGenome – Dynamic Programming & Sequence Analysis Simulations
 * 1. Levenshtein Distance (Detailed Insertion/Deletion/Substitution breakdown)
 * 2. Damerau-Levenshtein Distance (Adjacent Transposition detection)
 * 3. Needleman-Wunsch (Global Sequence Alignment & Traceback)
 * 4. Smith-Waterman (Local Sequence Alignment & Highest Cell Traceback)
 * 5. Bitmask Dynamic Programming (Subset Selection & Binary Visualization)
 * 6. Matrix Chain Multiplication (Optimal Parenthesization)
 * 7. Optimal Binary Search Tree (Gene Expression Access Optimization)
 */

const DpSimulations = {
  // -------------------------------------------------------------
  // 1. LEVENSHTEIN DISTANCE
  // -------------------------------------------------------------
  generateLevenshtein(s1, s2) {
    const states = [];
    const m = s1.length;
    const n = s2.length;

    // Initialize DP matrix (m+1) x (n+1)
    const dp = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(null));

    // Base cases
    for (let i = 0; i <= m; i++) dp[i][0] = i;
    for (let j = 0; j <= n; j++) dp[0][j] = j;

    states.push({
      operation: 'Matrix Initialization & Base Conditions',
      explanation: `Initialized Levenshtein matrix for <code>"${s1}"</code> (len ${m}) and <code>"${s2}"</code> (len ${n}). Base rows and columns reflect deleting all characters to match an empty string.`,
      math: `dp[i][0] = i, dp[0][j] = j | Dimensions: ${m + 1} × ${n + 1}`,
      phase: 'init',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: -1,
      currCol: -1,
      insertionCost: null,
      deletionCost: null,
      subCost: null,
      chosenOp: null,
      tracebackPath: []
    });

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        const char1 = s1[i - 1];
        const char2 = s2[j - 1];
        const cost = char1 === char2 ? 0 : 1;

        const delCost = dp[i - 1][j] + 1;
        const insCost = dp[i][j - 1] + 1;
        const subCostVal = dp[i - 1][j - 1] + cost;

        const minVal = Math.min(delCost, insCost, subCostVal);
        let chosenOp = 'match';
        if (char1 !== char2) {
          if (minVal === subCostVal) chosenOp = 'substitution';
          else if (minVal === delCost) chosenOp = 'deletion';
          else chosenOp = 'insertion';
        }

        states.push({
          operation: `Calculating cell [${i}, ${j}] ('${char1}' vs '${char2}')`,
          explanation: `Comparing <strong>s1[${i - 1}] = '${char1}'</strong> with <strong>s2[${j - 1}] = '${char2}'</strong>.<br>
            • Deletion: dp[${i - 1}][${j}] + 1 = <strong>${delCost}</strong><br>
            • Insertion: dp[${i}][${j - 1}] + 1 = <strong>${insCost}</strong><br>
            • Substitution: dp[${i - 1}][${j - 1}] + ${cost} = <strong>${subCostVal}</strong><br>
            Selected minimum: <strong>${minVal}</strong> via <strong>${chosenOp}</strong>.`,
          math: `min(del=${delCost}, ins=${insCost}, sub=${subCostVal}) = ${minVal} [${chosenOp}]`,
          phase: 'compute_cell',
          s1,
          s2,
          dp: JSON.parse(JSON.stringify(dp)),
          currRow: i,
          currCol: j,
          insertionCost: insCost,
          deletionCost: delCost,
          subCost: subCostVal,
          chosenOp,
          tracebackPath: []
        });

        dp[i][j] = minVal;
      }
    }

    // Traceback
    const traceback = [];
    let r = m;
    let c = n;
    traceback.push({ r, c });

    while (r > 0 || c > 0) {
      if (r > 0 && c > 0) {
        const cost = s1[r - 1] === s2[c - 1] ? 0 : 1;
        if (dp[r][c] === dp[r - 1][c - 1] + cost) {
          r--;
          c--;
          traceback.push({ r, c });
          continue;
        }
      }
      if (r > 0 && dp[r][c] === dp[r - 1][c] + 1) {
        r--;
        traceback.push({ r, c });
      } else if (c > 0) {
        c--;
        traceback.push({ r, c });
      }
    }

    states.push({
      operation: 'Traceback & Final Edit Distance',
      explanation: `Minimum Edit Distance between <code>"${s1}"</code> and <code>"${s2}"</code> is <strong>${dp[m][n]}</strong>. Highlighted optimal edit alignment path from cell [0,0] to [${m},${n}].`,
      math: `Edit Distance = ${dp[m][n]} | Time: O(mn) | Space: O(mn)`,
      phase: 'complete',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: m,
      currCol: n,
      insertionCost: null,
      deletionCost: null,
      subCost: null,
      chosenOp: null,
      tracebackPath: traceback
    });

    return states;
  },

  // -------------------------------------------------------------
  // 2. DAMERAU-LEVENSHTEIN DISTANCE
  // -------------------------------------------------------------
  generateDamerau(s1, s2) {
    const states = [];
    const m = s1.length;
    const n = s2.length;
    const dp = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(null));

    for (let i = 0; i <= m; i++) dp[i][0] = i;
    for (let j = 0; j <= n; j++) dp[0][j] = j;

    states.push({
      operation: 'Damerau-Levenshtein Initialization',
      explanation: `Damerau-Levenshtein allows 4 edit operations: <strong>Insertion, Deletion, Substitution, and Transposition of two adjacent characters</strong>.`,
      math: `Allows adjacent transposition (e.g., 'AT' ↔ 'TA') with cost 1`,
      phase: 'init',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: -1,
      currCol: -1,
      transpositionTriggered: false
    });

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        const cost = s1[i - 1] === s2[j - 1] ? 0 : 1;
        let minVal = Math.min(
          dp[i - 1][j] + 1,       // deletion
          dp[i][j - 1] + 1,       // insertion
          dp[i - 1][j - 1] + cost // substitution
        );

        let transposition = false;
        // Check transposition condition
        if (
          i > 1 &&
          j > 1 &&
          s1[i - 1] === s2[j - 2] &&
          s1[i - 2] === s2[j - 1]
        ) {
          const transCost = dp[i - 2][j - 2] + 1;
          if (transCost < minVal) {
            minVal = transCost;
            transposition = true;
          }
        }

        states.push({
          operation: transposition 
            ? `Transposition Detected at [${i}, ${j}]!` 
            : `Calculating Cell [${i}, ${j}]`,
          explanation: transposition
            ? `Adjacent transposition identified: characters <strong>'${s1[i - 2]}${s1[i - 1]}'</strong> swapped to <strong>'${s2[j - 2]}${s2[j - 1]}'</strong>! Transposition cost is 1. Cell value: <strong>${minVal}</strong>.`
            : `Standard edit cost evaluated for '${s1[i - 1]}' vs '${s2[j - 1]}'. Minimum cost: <strong>${minVal}</strong>.`,
          math: transposition ? `Transposition: dp[${i - 2}][${j - 2}] + 1 = ${minVal}` : `dp[${i}][${j}] = ${minVal}`,
          phase: transposition ? 'transposition' : 'cell',
          s1,
          s2,
          dp: JSON.parse(JSON.stringify(dp)),
          currRow: i,
          currCol: j,
          transpositionTriggered: transposition
        });

        dp[i][j] = minVal;
      }
    }

    states.push({
      operation: 'Damerau-Levenshtein Complete',
      explanation: `Final Damerau-Levenshtein edit distance: <strong>${dp[m][n]}</strong>. Successfully handled adjacent swaps as single atomic mutations.`,
      math: `Final Distance = ${dp[m][n]}`,
      phase: 'complete',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: m,
      currCol: n,
      transpositionTriggered: false
    });

    return states;
  },

  // -------------------------------------------------------------
  // 3. NEEDLEMAN-WUNSCH (Global Alignment)
  // -------------------------------------------------------------
  generateNeedlemanWunsch(s1, s2, matchScore = 1, mismatchScore = -1, gapScore = -1) {
    const states = [];
    const m = s1.length;
    const n = s2.length;
    const dp = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(null));

    // Base initializations with gap penalty
    for (let i = 0; i <= m; i++) dp[i][0] = i * gapScore;
    for (let j = 0; j <= n; j++) dp[0][j] = j * gapScore;

    states.push({
      operation: 'Scoring Matrix Initialization (Global Alignment)',
      explanation: `Initialized Needleman-Wunsch matrix for <code>"${s1}"</code> vs <code>"${s2}"</code>. Match: <strong>+${matchScore}</strong>, Mismatch: <strong>${mismatchScore}</strong>, Gap penalty: <strong>${gapScore}</strong>.`,
      math: `dp[i][0] = ${gapScore}*i, dp[0][j] = ${gapScore}*j`,
      phase: 'init',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: -1,
      currCol: -1,
      matchScore,
      mismatchScore,
      gapScore,
      traceback: []
    });

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        const diagScore = dp[i - 1][j - 1] + (s1[i - 1] === s2[j - 1] ? matchScore : mismatchScore);
        const upScore = dp[i - 1][j] + gapScore;
        const leftScore = dp[i][j - 1] + gapScore;

        const maxVal = Math.max(diagScore, upScore, leftScore);

        states.push({
          operation: `Filling Cell [${i}, ${j}] ('${s1[i - 1]}' vs '${s2[j - 1]}')`,
          explanation: `Evaluating alignment alternatives:<br>
            • Diagonal (Align '${s1[i - 1]}' with '${s2[j - 1]}'): dp[${i - 1}][${j - 1}] + ${s1[i - 1] === s2[j - 1] ? matchScore : mismatchScore} = <strong>${diagScore}</strong><br>
            • Up (Gap in Seq B): dp[${i - 1}][${j}] + ${gapScore} = <strong>${upScore}</strong><br>
            • Left (Gap in Seq A): dp[${i}][${j - 1}] + ${gapScore} = <strong>${leftScore}</strong><br>
            Optimal score: <strong>${maxVal}</strong>.`,
          math: `max(diag=${diagScore}, up=${upScore}, left=${leftScore}) = ${maxVal}`,
          phase: 'compute',
          s1,
          s2,
          dp: JSON.parse(JSON.stringify(dp)),
          currRow: i,
          currCol: j,
          diagScore,
          upScore,
          leftScore,
          matchScore,
          mismatchScore,
          gapScore,
          traceback: []
        });

        dp[i][j] = maxVal;
      }
    }

    // Global Traceback from [m, n]
    const traceback = [];
    let r = m;
    let c = n;
    let alignA = '';
    let alignB = '';

    while (r > 0 || c > 0) {
      traceback.push({ r, c });
      if (r > 0 && c > 0) {
        const score = s1[r - 1] === s2[c - 1] ? matchScore : mismatchScore;
        if (dp[r][c] === dp[r - 1][c - 1] + score) {
          alignA = s1[r - 1] + alignA;
          alignB = s2[c - 1] + alignB;
          r--;
          c--;
          continue;
        }
      }
      if (r > 0 && dp[r][c] === dp[r - 1][c] + gapScore) {
        alignA = s1[r - 1] + alignA;
        alignB = '-' + alignB;
        r--;
      } else {
        alignA = '-' + alignA;
        alignB = s2[c - 1] + alignB;
        c--;
      }
    }
    traceback.push({ r: 0, c: 0 });

    let matchCount = 0;
    let mismatchCount = 0;
    let gapCount = 0;
    for (let k = 0; k < alignA.length; k++) {
      if (alignA[k] === '-' || alignB[k] === '-') gapCount++;
      else if (alignA[k] === alignB[k]) matchCount++;
      else mismatchCount++;
    }

    states.push({
      operation: 'Traceback Complete: Global Alignment Formed',
      explanation: `Traceback complete! Final Global Alignment Score: <strong>${dp[m][n]}</strong>.<br>
        <strong>Seq A:</strong> <code>${alignA}</code><br>
        <strong>Seq B:</strong> <code>${alignB}</code><br>
        Matches: <strong>${matchCount}</strong> | Mismatches: <strong>${mismatchCount}</strong> | Gaps: <strong>${gapCount}</strong>`,
      math: `Score = ${dp[m][n]} | Matches: ${matchCount}, Mismatches: ${mismatchCount}, Gaps: ${gapCount}`,
      phase: 'complete',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: m,
      currCol: n,
      matchScore,
      mismatchScore,
      gapScore,
      alignA,
      alignB,
      matchCount,
      mismatchCount,
      gapCount,
      traceback
    });

    return states;
  },

  // -------------------------------------------------------------
  // 4. SMITH-WATERMAN (Local Alignment)
  // -------------------------------------------------------------
  generateSmithWaterman(s1, s2, matchScore = 2, mismatchScore = -1, gapScore = -1) {
    const states = [];
    const m = s1.length;
    const n = s2.length;
    const dp = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(0));

    let maxVal = 0;
    let maxR = 0;
    let maxC = 0;

    states.push({
      operation: 'Smith-Waterman Initialization (Zero Lower Bound)',
      explanation: `Smith-Waterman finds the optimal <strong>local alignment</strong> between conserved sub-regions. Base rows and columns are zeroed; negative scores are clamped to 0 to terminate poor local alignments.`,
      math: `dp[i][j] = max(0, diag, up, left) | Match=+${matchScore}, Mismatch=${mismatchScore}, Gap=${gapScore}`,
      phase: 'init',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: -1,
      currCol: -1,
      maxVal: 0,
      maxR: 0,
      maxC: 0,
      traceback: []
    });

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        const diagScore = dp[i - 1][j - 1] + (s1[i - 1] === s2[j - 1] ? matchScore : mismatchScore);
        const upScore = dp[i - 1][j] + gapScore;
        const leftScore = dp[i][j - 1] + gapScore;

        const val = Math.max(0, diagScore, upScore, leftScore);
        dp[i][j] = val;

        if (val > maxVal) {
          maxVal = val;
          maxR = i;
          maxC = j;
        }

        states.push({
          operation: `Local Cell [${i}, ${j}] Computed = ${val}`,
          explanation: `Evaluated cell [${i}, ${j}]. Options: Diag=${diagScore}, Up=${upScore}, Left=${leftScore}, 0. Computed score: <strong>${val}</strong>.${val === 0 ? ' (Score clamped to 0)' : ''}`,
          math: `dp[${i}][${j}] = max(0, ${diagScore}, ${upScore}, ${leftScore}) = ${val} | Current Peak: ${maxVal}`,
          phase: 'compute',
          s1,
          s2,
          dp: JSON.parse(JSON.stringify(dp)),
          currRow: i,
          currCol: j,
          maxVal,
          maxR,
          maxC,
          traceback: []
        });
      }
    }

    // Local Traceback from peak cell (maxR, maxC) until score hits 0
    const traceback = [];
    let r = maxR;
    let c = maxC;
    let alignA = '';
    let alignB = '';

    while (r > 0 && c > 0 && dp[r][c] > 0) {
      traceback.push({ r, c });
      const score = s1[r - 1] === s2[c - 1] ? matchScore : mismatchScore;
      if (dp[r][c] === dp[r - 1][c - 1] + score) {
        alignA = s1[r - 1] + alignA;
        alignB = s2[c - 1] + alignB;
        r--;
        c--;
      } else if (dp[r][c] === dp[r - 1][c] + gapScore) {
        alignA = s1[r - 1] + alignA;
        alignB = '-' + alignB;
        r--;
      } else {
        alignA = '-' + alignA;
        alignB = s2[c - 1] + alignB;
        c--;
      }
    }
    if (r >= 0 && c >= 0) traceback.push({ r, c });

    states.push({
      operation: `Local Traceback from Peak [${maxR}, ${maxC}] Score ${maxVal}`,
      explanation: `Highest scoring local motif starts at cell [${maxR}, ${maxC}] with score <strong>${maxVal}</strong>. Traceback stopped upon encountering 0.<br>
        <strong>Conserved Substring A:</strong> <code>${alignA}</code><br>
        <strong>Conserved Substring B:</strong> <code>${alignB}</code><br>
        Unlike global alignment, non-homologous flanking sequence is naturally discarded.`,
      math: `Optimal Local Score = ${maxVal} | Conserved Region: "${alignA}" vs "${alignB}"`,
      phase: 'complete',
      s1,
      s2,
      dp: JSON.parse(JSON.stringify(dp)),
      currRow: maxR,
      currCol: maxC,
      maxVal,
      maxR,
      maxC,
      alignA,
      alignB,
      traceback
    });

    return states;
  },

  // -------------------------------------------------------------
  // 5. BITMASK DYNAMIC PROGRAMMING
  // -------------------------------------------------------------
  generateBitmaskDp(resources) {
    const states = [];
    const n = Math.min(resources.length, 5); // 5 resources -> 32 states for clear visualization
    const items = resources.slice(0, n);
    const numStates = 1 << n;
    const dp = new Array(numStates).fill(0);

    states.push({
      operation: 'Bitmask DP Initialization',
      explanation: `Configured selection optimization for <strong>${n}</strong> experimental resources: ${items.map((it, idx) => `[Bit ${idx}: ${it.name} ($${it.cost})]`).join(', ')}. Total states = 2^${n} = <strong>${numStates}</strong> bitmasks.`,
      math: `n = ${n} | State space: [00000₂ ... 11111₂]`,
      phase: 'init',
      items,
      dp: [...dp],
      currMask: 0,
      currItem: -1
    });

    for (let mask = 0; mask < numStates; mask++) {
      const binStr = mask.toString(2).padStart(n, '0');
      
      for (let i = 0; i < n; i++) {
        if ((mask & (1 << i)) === 0) {
          const nextMask = mask | (1 << i);
          const nextBin = nextMask.toString(2).padStart(n, '0');
          const valueGained = items[i].value || (i + 1) * 15;
          const candidateScore = dp[mask] + valueGained;

          if (candidateScore > dp[nextMask]) {
            dp[nextMask] = candidateScore;

            states.push({
              operation: `Transition: Mask ${binStr} → ${nextBin}`,
              explanation: `Adding resource <strong>"${items[i].name}"</strong> (Bit ${i}) to current subset <code>${binStr}</code>.<br>
                New mask: <code>${nextBin}</code>. Updated cumulative score: <strong>${dp[nextMask]}</strong>.`,
              math: `dp[${nextBin}] = max(dp[${nextBin}], dp[${binStr}] + ${valueGained}) = ${dp[nextMask]}`,
              phase: 'transition',
              items,
              dp: [...dp],
              currMask: nextMask,
              currItem: i
            });
          }
        }
      }
    }

    const fullMask = (1 << n) - 1;
    const fullBin = fullMask.toString(2).padStart(n, '0');

    states.push({
      operation: 'Optimal Subset Selection Achieved',
      explanation: `All 2^${n} bitmask configurations evaluated. Maximum value for full combination <code>${fullBin}</code> is <strong>${dp[fullMask]}</strong>.`,
      math: `Final Optimal Score: ${dp[fullMask]} for state 11111₂`,
      phase: 'complete',
      items,
      dp: [...dp],
      currMask: fullMask,
      currItem: -1
    });

    return states;
  },

  // -------------------------------------------------------------
  // 6. MATRIX CHAIN MULTIPLICATION
  // -------------------------------------------------------------
  generateMatrixChain(dims) {
    const states = [];
    const n = dims.length - 1; // number of matrices
    const m = Array.from({ length: n + 1 }, () => new Array(n + 1).fill(0));
    const s = Array.from({ length: n + 1 }, () => new Array(n + 1).fill(0));

    states.push({
      operation: 'Matrix Chain Initialization',
      explanation: `Minimizing scalar multiplications for ${n} genomic transformation matrices with dimensions: [${dims.join(', ')}]. Base cases m[i][i] = 0.`,
      math: `m[i][i] = 0 | Chain lengths L = 2 to ${n}`,
      phase: 'init',
      dims,
      m: JSON.parse(JSON.stringify(m)),
      s: JSON.parse(JSON.stringify(s)),
      currI: -1,
      currJ: -1,
      currK: -1
    });

    for (let L = 2; L <= n; L++) {
      for (let i = 1; i <= n - L + 1; i++) {
        const j = i + L - 1;
        m[i][j] = Infinity;

        for (let k = i; k < j; k++) {
          const cost = m[i][k] + m[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
          if (cost < m[i][j]) {
            m[i][j] = cost;
            s[i][j] = k;
          }

          states.push({
            operation: `Testing Split at k=${k} for Subchain A${i}..A${j}`,
            explanation: `Parenthesization (A${i}..A${k})(A${k + 1}..A${j}) cost: m[${i}][${k}] (${m[i][k]}) + m[${k + 1}][${j}] (${m[k + 1][j]}) + ${dims[i - 1]}×${dims[k]}×${dims[j]} = <strong>${cost}</strong> operations. Current min: <strong>${m[i][j]}</strong>.`,
            math: `m[${i}][${j}] = min(...) = ${m[i][j]} at split k=${s[i][j]}`,
            phase: 'evaluate_k',
            dims,
            m: JSON.parse(JSON.stringify(m)),
            s: JSON.parse(JSON.stringify(s)),
            currI: i,
            currJ: j,
            currK: k
          });
        }
      }
    }

    // Helper for parenthesization string
    function printOptimalParens(sArr, i, j) {
      if (i === j) return `A${i}`;
      return `(${printOptimalParens(sArr, i, sArr[i][j])} × ${printOptimalParens(sArr, sArr[i][j] + 1, j)})`;
    }
    const optimalParens = printOptimalParens(s, 1, n);

    states.push({
      operation: 'Optimal Parenthesization Found',
      explanation: `Minimum scalar multiplication cost: <strong>${m[1][n]}</strong> operations.<br>Optimal evaluation order: <strong>${optimalParens}</strong>.`,
      math: `Cost = ${m[1][n]} | Tree: ${optimalParens}`,
      phase: 'complete',
      dims,
      m: JSON.parse(JSON.stringify(m)),
      s: JSON.parse(JSON.stringify(s)),
      currI: 1,
      currJ: n,
      currK: s[1][n],
      optimalParens
    });

    return states;
  },

  // -------------------------------------------------------------
  // 7. OPTIMAL BINARY SEARCH TREE (OBST)
  // -------------------------------------------------------------
  generateOptimalBst(keys, pFreq, qFreq) {
    const states = [];
    const n = keys.length;
    const e = Array.from({ length: n + 2 }, () => new Array(n + 1).fill(0));
    const w = Array.from({ length: n + 2 }, () => new Array(n + 1).fill(0));
    const root = Array.from({ length: n + 1 }, () => new Array(n + 1).fill(0));

    // Base cases
    for (let i = 1; i <= n + 1; i++) {
      e[i][i - 1] = qFreq[i - 1];
      w[i][i - 1] = qFreq[i - 1];
    }

    states.push({
      operation: 'Optimal BST Base Frequency Matrix',
      explanation: `Configured OBST for ${n} genomic markers: [${keys.join(', ')}]. Base search costs e[i][i-1] initialized with dummy search frequencies q.`,
      math: `keys: [${keys.join(', ')}] | p: [${pFreq.join(', ')}] | q: [${qFreq.join(', ')}]`,
      phase: 'init',
      keys,
      e: JSON.parse(JSON.stringify(e)),
      w: JSON.parse(JSON.stringify(w)),
      root: JSON.parse(JSON.stringify(root)),
      currI: -1,
      currJ: -1,
      currR: -1
    });

    for (let L = 1; L <= n; L++) {
      for (let i = 1; i <= n - L + 1; i++) {
        const j = i + L - 1;
        e[i][j] = Infinity;
        w[i][j] = w[i][j - 1] + pFreq[j - 1] + qFreq[j];

        for (let r = i; r <= j; r++) {
          const t = e[i][r - 1] + e[r + 1][j] + w[i][j];
          if (t < e[i][j]) {
            e[i][j] = t;
            root[i][j] = r;
          }

          states.push({
            operation: `Testing Root k=${r} ('${keys[r - 1]}') for Subtree [${i}..${j}]`,
            explanation: `Subtree with root '${keys[r - 1]}' has cost e[${i}][${r - 1}] + e[${r + 1}][${j}] + w[${i}][${j}] = <strong>${t.toFixed(2)}</strong>. Current optimal root: <strong>'${keys[root[i][j] - 1]}'</strong>.`,
            math: `e[${i}][${j}] = ${e[i][j].toFixed(2)} (root = ${keys[root[i][j] - 1]})`,
            phase: 'root_eval',
            keys,
            e: JSON.parse(JSON.stringify(e)),
            w: JSON.parse(JSON.stringify(w)),
            root: JSON.parse(JSON.stringify(root)),
            currI: i,
            currJ: j,
            currR: r
          });
        }
      }
    }

    states.push({
      operation: 'Optimal BST Construction Complete',
      explanation: `Expected search cost minimized to <strong>${e[1][n].toFixed(2)}</strong> comparisons. Root of overall tree is <strong>"${keys[root[1][n] - 1]}"</strong>.`,
      math: `Optimal Search Expected Cost = ${e[1][n].toFixed(2)} | Tree Root = ${keys[root[1][n] - 1]}`,
      phase: 'complete',
      keys,
      e: JSON.parse(JSON.stringify(e)),
      w: JSON.parse(JSON.stringify(w)),
      root: JSON.parse(JSON.stringify(root)),
      currI: 1,
      currJ: n,
      currR: root[1][n]
    });

    return states;
  }
};

window.DpSimulations = DpSimulations;
