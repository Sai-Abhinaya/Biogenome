/**
 * BioGenome – Algorithm Comparison Lab
 * Runs multiple algorithms on identical inputs to contrast step counts,
 * character comparisons, and browser demonstration execution time.
 * Note: Clearly labeled "Browser demonstration time".
 */

const ComparisonLab = {
  runMotifSearchComparison(text, pattern) {
    const results = [];

    // 1. KMP
    const t0Kmp = performance.now();
    const kmpStates = window.StringSimulations.generateKmp(text, pattern);
    const t1Kmp = performance.now();
    const kmpFinal = kmpStates[kmpStates.length - 1];

    results.push({
      id: 'kmp',
      name: 'KMP (Knuth-Morris-Pratt)',
      category: 'String Matching',
      matches: kmpFinal.matches.length,
      matchIndices: [...kmpFinal.matches],
      comparisons: kmpFinal.comparisons,
      totalSteps: kmpStates.length,
      demoTimeMs: (t1Kmp - t0Kmp).toFixed(2),
      timeComplexity: 'O(n + m)',
      spaceComplexity: 'O(m)',
      technique: 'Prefix Function (LPS array fallback)'
    });

    // 2. Z-Algorithm
    const t0Z = performance.now();
    const zStates = window.StringSimulations.generateZ(text, pattern);
    const t1Z = performance.now();
    const zFinal = zStates[zStates.length - 1];

    results.push({
      id: 'z-alg',
      name: 'Z-Algorithm',
      category: 'String Matching',
      matches: zFinal.matches.length,
      matchIndices: [...zFinal.matches],
      comparisons: zFinal.comparisons,
      totalSteps: zStates.length,
      demoTimeMs: (t1Z - t0Z).toFixed(2),
      timeComplexity: 'O(n + m)',
      spaceComplexity: 'O(n + m)',
      technique: 'Z-Box interval reuse'
    });

    // 3. Rabin-Karp
    const t0Rk = performance.now();
    const rkStates = window.StringSimulations.generateRabinKarp(text, pattern);
    const t1Rk = performance.now();
    const rkFinal = rkStates[rkStates.length - 1];

    results.push({
      id: 'rabin-karp',
      name: 'Rabin-Karp',
      category: 'String Matching',
      matches: rkFinal.matches.length,
      matchIndices: [...rkFinal.matches],
      comparisons: rkFinal.comparisons,
      totalSteps: rkStates.length,
      demoTimeMs: (t1Rk - t0Rk).toFixed(2),
      timeComplexity: 'O(n + m) avg / O(nm) worst',
      spaceComplexity: 'O(1)',
      technique: 'Polynomial rolling hash modulo prime'
    });

    // 4. Aho-Corasick (Single motif query)
    const t0Ac = performance.now();
    const acStates = window.StringSimulations.generateAhoCorasick(text, [pattern]);
    const t1Ac = performance.now();
    const acFinal = acStates[acStates.length - 1];

    results.push({
      id: 'aho-corasick',
      name: 'Aho-Corasick',
      category: 'Multi-Pattern Matching',
      matches: acFinal.foundMatches.length,
      matchIndices: acFinal.foundMatches.map(m => m.start),
      comparisons: text.length,
      totalSteps: acStates.length,
      demoTimeMs: (t1Ac - t0Ac).toFixed(2),
      timeComplexity: 'O(n + m + z)',
      spaceComplexity: 'O(m × Σ)',
      technique: 'Automaton with failure & output transitions'
    });

    return results;
  },

  runAlignmentComparison(s1, s2) {
    const results = [];

    // 1. Levenshtein
    const t0Lev = performance.now();
    const levStates = window.DpSimulations.generateLevenshtein(s1, s2);
    const t1Lev = performance.now();
    const levFinal = levStates[levStates.length - 1];

    results.push({
      id: 'levenshtein',
      name: 'Levenshtein Distance',
      category: 'Sequence Distance',
      score: levFinal.dp[s1.length][s2.length],
      scoreLabel: 'Edit Distance',
      steps: levStates.length,
      demoTimeMs: (t1Lev - t0Lev).toFixed(2),
      timeComplexity: 'O(mn)',
      spaceComplexity: 'O(mn)',
      focus: 'Minimum atomic unit mutations (Ins/Del/Sub)'
    });

    // 2. Damerau-Levenshtein
    const t0Dam = performance.now();
    const damStates = window.DpSimulations.generateDamerau(s1, s2);
    const t1Dam = performance.now();
    const damFinal = damStates[damStates.length - 1];

    results.push({
      id: 'damerau',
      name: 'Damerau-Levenshtein',
      category: 'Sequence Distance',
      score: damFinal.dp[s1.length][s2.length],
      scoreLabel: 'Transposition Distance',
      steps: damStates.length,
      demoTimeMs: (t1Dam - t0Dam).toFixed(2),
      timeComplexity: 'O(mn)',
      spaceComplexity: 'O(mn)',
      focus: 'Includes adjacent base-swap transpositions'
    });

    // 3. Needleman-Wunsch
    const t0Nw = performance.now();
    const nwStates = window.DpSimulations.generateNeedlemanWunsch(s1, s2, 1, -1, -1);
    const t1Nw = performance.now();
    const nwFinal = nwStates[nwStates.length - 1];

    results.push({
      id: 'needleman-wunsch',
      name: 'Needleman-Wunsch',
      category: 'Global Sequence Alignment',
      score: nwFinal.dp[s1.length][s2.length],
      scoreLabel: 'Global Alignment Score',
      steps: nwStates.length,
      demoTimeMs: (t1Nw - t0Nw).toFixed(2),
      timeComplexity: 'O(mn)',
      spaceComplexity: 'O(mn)',
      focus: 'End-to-end full global sequence homology'
    });

    // 4. Smith-Waterman
    const t0Sw = performance.now();
    const swStates = window.DpSimulations.generateSmithWaterman(s1, s2, 2, -1, -1);
    const t1Sw = performance.now();
    const swFinal = swStates[swStates.length - 1];

    results.push({
      id: 'smith-waterman',
      name: 'Smith-Waterman',
      category: 'Local Sequence Alignment',
      score: swFinal.maxVal,
      scoreLabel: 'Local Alignment Peak Score',
      steps: swStates.length,
      demoTimeMs: (t1Sw - t0Sw).toFixed(2),
      timeComplexity: 'O(mn)',
      spaceComplexity: 'O(mn)',
      focus: 'Conserved high-scoring local motif identification'
    });

    return results;
  }
};

window.ComparisonLab = ComparisonLab;
