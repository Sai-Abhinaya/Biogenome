/**
 * BioGenome – String Matching Algorithm Simulations
 * 1. KMP (Knuth-Morris-Pratt)
 * 2. Z-Algorithm
 * 3. Rabin-Karp (Rolling Polynomial Hash)
 * 4. Aho-Corasick (Trie with Failure Links)
 *
 * Real algorithm logic yielding discrete, playable visualization states.
 */

const StringSimulations = {
  // -------------------------------------------------------------
  // 1. KMP (Knuth-Morris-Pratt)
  // -------------------------------------------------------------
  generateKmp(text, pattern) {
    const states = [];
    const n = text.length;
    const m = pattern.length;
    let comparisons = 0;
    const matches = [];

    // Step 0: Compute LPS (Longest Prefix Suffix) Array
    const lps = new Array(m).fill(0);
    states.push({
      operation: 'LPS Table Initialization',
      explanation: `Constructing the π (LPS) fallback table for pattern <strong>"${pattern}"</strong> of length ${m}. The LPS array prevents redundant re-examinations upon mismatch.`,
      math: `LPS = [${lps.join(', ')}]`,
      phase: 'lps_start',
      text,
      pattern,
      lps: [...lps],
      textIdx: -1,
      patIdx: -1,
      patShift: 0,
      comparisons: 0,
      matches: []
    });

    let len = 0;
    let i = 1;
    while (i < m) {
      if (pattern[i] === pattern[len]) {
        len++;
        lps[i] = len;
        states.push({
          operation: `LPS Match at i=${i}`,
          explanation: `pattern[${i}] ('${pattern[i]}') == pattern[${len - 1}] ('${pattern[len - 1]}'). Current prefix length extended to ${len}. Setting LPS[${i}] = ${len}.`,
          math: `LPS[${i}] = ${len} | LPS = [${lps.join(', ')}]`,
          phase: 'lps_build',
          text,
          pattern,
          lps: [...lps],
          lpsI: i,
          lpsLen: len,
          textIdx: -1,
          patIdx: -1,
          patShift: 0,
          comparisons: 0,
          matches: []
        });
        i++;
      } else {
        if (len !== 0) {
          const oldLen = len;
          len = lps[len - 1];
          states.push({
            operation: `LPS Fallback at i=${i}`,
            explanation: `pattern[${i}] ('${pattern[i]}') != pattern[${oldLen}] ('${pattern[oldLen]}'). Falling back prefix length to LPS[${oldLen - 1}] = ${len}.`,
            math: `Fallback length: ${oldLen} → ${len}`,
            phase: 'lps_build',
            text,
            pattern,
            lps: [...lps],
            lpsI: i,
            lpsLen: len,
            textIdx: -1,
            patIdx: -1,
            patShift: 0,
            comparisons: 0,
            matches: []
          });
        } else {
          lps[i] = 0;
          states.push({
            operation: `LPS Zero at i=${i}`,
            explanation: `No proper prefix match found for pattern[${i}] ('${pattern[i]}'). Setting LPS[${i}] = 0.`,
            math: `LPS[${i}] = 0 | LPS = [${lps.join(', ')}]`,
            phase: 'lps_build',
            text,
            pattern,
            lps: [...lps],
            lpsI: i,
            lpsLen: 0,
            textIdx: -1,
            patIdx: -1,
            patShift: 0,
            comparisons: 0,
            matches: []
          });
          i++;
        }
      }
    }

    states.push({
      operation: 'LPS Construction Complete',
      explanation: `LPS Table computed: <code>[${lps.join(', ')}]</code>. Starting nucleotide motif scan on DNA sequence.`,
      math: `LPS = [${lps.join(', ')}] | Ready for linear scan O(n + m)`,
      phase: 'scan_ready',
      text,
      pattern,
      lps: [...lps],
      textIdx: 0,
      patIdx: 0,
      patShift: 0,
      comparisons: 0,
      matches: []
    });

    // Step 1: Scan Text with Pattern
    let txtIdx = 0;
    let patIdx = 0;

    while (txtIdx < n) {
      comparisons++;
      const isCharMatch = pattern[patIdx] === text[txtIdx];

      if (isCharMatch) {
        states.push({
          operation: `Character Match (T[${txtIdx}] == P[${patIdx}])`,
          explanation: `Comparing DNA nucleotide <strong>Text[${txtIdx}] = '${text[txtIdx]}'</strong> with <strong>Pattern[${patIdx}] = '${pattern[patIdx]}'</strong>. <strong>Match found!</strong> Advancing both pointers.`,
          math: `Text[${txtIdx}] == Pattern[${patIdx}] ('${text[txtIdx]}') | Comparisons: ${comparisons}`,
          phase: 'matching',
          text,
          pattern,
          lps: [...lps],
          textIdx: txtIdx,
          patIdx: patIdx,
          patShift: txtIdx - patIdx,
          status: 'match',
          comparisons,
          matches: [...matches]
        });

        txtIdx++;
        patIdx++;

        if (patIdx === m) {
          const matchPos = txtIdx - patIdx;
          matches.push(matchPos);
          const fallbackLps = lps[patIdx - 1];
          states.push({
            operation: `Full Motif Occurrence Detected at index ${matchPos}`,
            explanation: `Complete pattern match confirmed starting at sequence position <strong>${matchPos}</strong>! Using LPS[${patIdx - 1}] = ${fallbackLps} to shift pattern without rolling back text index.`,
            math: `Match index: ${matchPos} | Total Matches: ${matches.length} | Fallback LPS[${patIdx - 1}] = ${fallbackLps}`,
            phase: 'motif_found',
            text,
            pattern,
            lps: [...lps],
            textIdx: txtIdx - 1,
            patIdx: m - 1,
            patShift: matchPos,
            status: 'full_match',
            comparisons,
            matches: [...matches]
          });
          patIdx = fallbackLps;
        }
      } else {
        // Mismatch
        if (patIdx !== 0) {
          const fallbackLps = lps[patIdx - 1];
          const newShift = txtIdx - fallbackLps;
          states.push({
            operation: `Mismatch at T[${txtIdx}], P[${patIdx}]`,
            explanation: `Mismatch: <strong>Pattern[${patIdx}] = '${pattern[patIdx]}'</strong> != <strong>Text[${txtIdx}] = '${text[txtIdx]}'</strong>. Using LPS[${patIdx - 1}] = ${fallbackLps}. Pattern shifts right by ${patIdx - fallbackLps} positions.`,
            math: `Mismatch! LPS fallback: ${patIdx} → ${fallbackLps}. Shift: ${newShift}`,
            phase: 'mismatch',
            text,
            pattern,
            lps: [...lps],
            textIdx: txtIdx,
            patIdx: patIdx,
            patShift: txtIdx - patIdx,
            status: 'mismatch',
            comparisons,
            matches: [...matches]
          });
          patIdx = fallbackLps;
        } else {
          states.push({
            operation: `Mismatch at T[${txtIdx}], P[0]`,
            explanation: `First pattern character '${pattern[0]}' did not match DNA nucleotide '${text[txtIdx]}'. Shifting pattern 1 step right.`,
            math: `Mismatch at P[0]. Text pointer advances: ${txtIdx} → ${txtIdx + 1}`,
            phase: 'mismatch',
            text,
            pattern,
            lps: [...lps],
            textIdx: txtIdx,
            patIdx: 0,
            patShift: txtIdx,
            status: 'mismatch',
            comparisons,
            matches: [...matches]
          });
          txtIdx++;
        }
      }
    }

    // Final state
    states.push({
      operation: 'KMP Execution Complete',
      explanation: `Scan finished. Found <strong>${matches.length}</strong> occurrence(s) at positions: <code>[${matches.join(', ') || 'None'}]</code> with ${comparisons} comparisons.`,
      math: `Matches: [${matches.join(', ')}] | Comparisons: ${comparisons} | Time: O(n+m) | Space: O(m)`,
      phase: 'complete',
      text,
      pattern,
      lps: [...lps],
      textIdx: n,
      patIdx: -1,
      patShift: n,
      status: 'done',
      comparisons,
      matches: [...matches]
    });

    return states;
  },

  // -------------------------------------------------------------
  // 2. Z-ALGORITHM
  // -------------------------------------------------------------
  generateZ(text, pattern) {
    const states = [];
    const concat = `${pattern}$${text}`;
    const len = concat.length;
    const z = new Array(len).fill(0);
    let l = 0;
    let r = 0;
    let comparisons = 0;
    const matches = [];

    states.push({
      operation: 'Concatenated Sequence Initialization',
      explanation: `Prepared combined sequence: <strong>Pattern + '$' + DNA Sequence</strong> = <code>"${concat}"</code>. Constructing Z-array where Z[i] is the length of the longest substring starting at index i that matches a prefix of concat.`,
      math: `Concatenated: "${concat}" | Length: ${len} | Target Pattern Length: ${pattern.length}`,
      phase: 'init',
      concat,
      text,
      pattern,
      z: [...z],
      l: 0,
      r: 0,
      currIdx: 0,
      comparisons: 0,
      matches: []
    });

    for (let i = 1; i < len; i++) {
      let isInsideBox = i <= r;
      let k = i - l;

      if (!isInsideBox) {
        // Outside the Z-box: compare naively
        l = i;
        r = i;
        states.push({
          operation: `i=${i} Outside Z-Box [${l}, ${r}]`,
          explanation: `Index ${i} is outside the current Z-box. Opening new Z-box at [${i}, ${i}] and comparing characters directly with prefix.`,
          math: `i > r (${i} > ${r - 1}). Set L = ${i}, R = ${i}`,
          phase: 'naive_start',
          concat,
          text,
          pattern,
          z: [...z],
          l,
          r,
          currIdx: i,
          comparisons,
          matches: [...matches]
        });

        while (r < len && concat[r] === concat[r - l]) {
          comparisons++;
          r++;
        }
        if (r < len) comparisons++; // the mismatch comparison
        z[i] = r - l;
        r--; // R is closed interval [L, R]

        states.push({
          operation: `Z[${i}] Computed = ${z[i]}`,
          explanation: `Extended Z-box from L=${l} to R=${r}. Longest prefix match starting at index ${i} has length <strong>${z[i]}</strong> ("${concat.substring(i, i + z[i])}").`,
          math: `Z[${i}] = ${z[i]} | New Z-box: [${l}, ${r}]`,
          phase: 'computed',
          concat,
          text,
          pattern,
          z: [...z],
          l,
          r: Math.max(l, r),
          currIdx: i,
          comparisons,
          matches: [...matches]
        });
      } else {
        // Inside Z-box
        if (z[k] < r - i + 1) {
          // Can copy directly from Z[k]
          z[i] = z[k];
          states.push({
            operation: `Z[${i}] Copied from Z[${k}] = ${z[k]}`,
            explanation: `Index ${i} is inside Z-box [${l}, ${r}]. Since Z[k=${k}] (${z[k]}) < remaining box length (${r - i + 1}), we directly set Z[${i}] = ${z[k]} in O(1) without new comparisons!`,
            math: `Z[${i}] = Z[${k}] = ${z[i]} (Inside Box optimization)`,
            phase: 'box_copy',
            concat,
            text,
            pattern,
            z: [...z],
            l,
            r,
            currIdx: i,
            comparisons,
            matches: [...matches]
          });
        } else {
          // May exceed Z-box boundary, expand starting from R + 1
          l = i;
          states.push({
            operation: `i=${i} Boundary Touch, Expanding Box`,
            explanation: `Z[${k}] touches the boundary of [${l}, ${r}]. Resetting L=${i} and continuing character comparisons beyond R=${r}.`,
            math: `Z[${k}] >= ${r - i + 1}. Set L=${i}, scan from R=${r + 1}`,
            phase: 'expand_start',
            concat,
            text,
            pattern,
            z: [...z],
            l,
            r,
            currIdx: i,
            comparisons,
            matches: [...matches]
          });

          while (r < len && concat[r] === concat[r - l]) {
            comparisons++;
            r++;
          }
          if (r < len) comparisons++;
          z[i] = r - l;
          r--;

          states.push({
            operation: `Z[${i}] Computed after Expansion = ${z[i]}`,
            explanation: `Expanded Z-box to R=${r}. Z[${i}] is computed as <strong>${z[i]}</strong>.`,
            math: `Z[${i}] = ${z[i]} | Z-box: [${l}, ${r}]`,
            phase: 'computed',
            concat,
            text,
            pattern,
            z: [...z],
            l,
            r: Math.max(l, r),
            currIdx: i,
            comparisons,
            matches: [...matches]
          });
        }
      }

      // Check if match occurred in text part
      if (i > pattern.length && z[i] === pattern.length) {
        const textMatchIdx = i - pattern.length - 1;
        matches.push(textMatchIdx);
        states.push({
          operation: `Pattern Match Detected at Text Index ${textMatchIdx}`,
          explanation: `Z[${i}] == Pattern Length (${pattern.length}). Full motif occurrence identified at text index <strong>${textMatchIdx}</strong>!`,
          math: `Match at Text[${textMatchIdx}..${textMatchIdx + pattern.length - 1}]`,
          phase: 'match_found',
          concat,
          text,
          pattern,
          z: [...z],
          l,
          r,
          currIdx: i,
          comparisons,
          matches: [...matches]
        });
      }
    }

    states.push({
      operation: 'Z-Algorithm Complete',
      explanation: `Z-array computation complete: <code>[${z.join(', ')}]</code>. Total motif matches identified: <strong>${matches.length}</strong>.`,
      math: `Matches: [${matches.join(', ')}] | Comparisons: ${comparisons} | Time: O(n+m)`,
      phase: 'complete',
      concat,
      text,
      pattern,
      z: [...z],
      l,
      r,
      currIdx: len,
      comparisons,
      matches: [...matches]
    });

    return states;
  },

  // -------------------------------------------------------------
  // 3. RABIN-KARP (Rolling Polynomial Hash)
  // -------------------------------------------------------------
  generateRabinKarp(text, pattern) {
    const states = [];
    const n = text.length;
    const m = pattern.length;
    const d = 256; // base radix
    const q = 101; // prime modulus
    let comparisons = 0;
    const matches = [];

    // Calculate d^(m-1) % q
    let h = 1;
    for (let i = 0; i < m - 1; i++) {
      h = (h * d) % q;
    }

    // Initial hash of pattern and first window
    let pHash = 0;
    let tHash = 0;
    for (let i = 0; i < m; i++) {
      pHash = (d * pHash + pattern.charCodeAt(i)) % q;
      tHash = (d * tHash + text.charCodeAt(i)) % q;
    }

    states.push({
      operation: 'Hash Initialization (Window 0)',
      explanation: `Calculated pattern polynomial hash: <strong>H(P) = ${pHash}</strong>. Computed initial text window 0 ('${text.substring(0, m)}') hash: <strong>H(W₀) = ${tHash}</strong> (mod ${q}).`,
      math: `H(P) = ${pHash} | H(W) = ${tHash} | Prime Modulus = ${q} | Window = "${text.substring(0, m)}"`,
      phase: 'init',
      text,
      pattern,
      windowIdx: 0,
      windowStr: text.substring(0, m),
      pHash,
      tHash,
      isHashMatch: pHash === tHash,
      isCharVerified: false,
      isExactMatch: false,
      comparisons: 0,
      matches: []
    });

    for (let i = 0; i <= n - m; i++) {
      const windowStr = text.substring(i, i + m);
      const isHashMatch = pHash === tHash;

      if (isHashMatch) {
        // Hash Match! Now perform character verification
        states.push({
          operation: `HASH MATCH at Window ${i}! [H=${tHash}]`,
          explanation: `Window hash <strong>${tHash}</strong> equals Pattern hash <strong>${pHash}</strong>! Entering character verification phase to confirm match and prevent false-positive spurious collisions.`,
          math: `H(W_${i}) == H(P) (${tHash} == ${pHash}) → Character Verification Triggered`,
          phase: 'hash_match',
          text,
          pattern,
          windowIdx: i,
          windowStr,
          pHash,
          tHash,
          isHashMatch: true,
          isCharVerified: false,
          isExactMatch: false,
          comparisons,
          matches: [...matches]
        });

        let exactMatch = true;
        for (let j = 0; j < m; j++) {
          comparisons++;
          if (text[i + j] !== pattern[j]) {
            exactMatch = false;
            break;
          }
        }

        if (exactMatch) {
          matches.push(i);
          states.push({
            operation: `Confirmed Match at Index ${i}`,
            explanation: `All characters matched! DNA motif <strong>"${pattern}"</strong> verified at position <strong>${i}</strong>.`,
            math: `Exact Match Verified! Comparisons so far: ${comparisons}`,
            phase: 'exact_match',
            text,
            pattern,
            windowIdx: i,
            windowStr,
            pHash,
            tHash,
            isHashMatch: true,
            isCharVerified: true,
            isExactMatch: true,
            comparisons,
            matches: [...matches]
          });
        } else {
          states.push({
            operation: `Spurious Hash Collision at Index ${i}`,
            explanation: `<strong>Spurious Collision!</strong> Hashes matched (${tHash}), but character verification failed. False alarm resolved without false positives.`,
            math: `Hash Match != Exact Match (Collision resolved)`,
            phase: 'collision',
            text,
            pattern,
            windowIdx: i,
            windowStr,
            pHash,
            tHash,
            isHashMatch: true,
            isCharVerified: true,
            isExactMatch: false,
            comparisons,
            matches: [...matches]
          });
        }
      } else {
        // Hash mismatch: shift window directly without checking characters
        states.push({
          operation: `Hash Mismatch at Window ${i}`,
          explanation: `Window hash <strong>${tHash}</strong> != Pattern hash <strong>${pHash}</strong>. Window discarded in O(1) time without character comparisons.`,
          math: `H("${windowStr}") = ${tHash} != ${pHash} → Fast Shift`,
          phase: 'hash_mismatch',
          text,
          pattern,
          windowIdx: i,
          windowStr,
          pHash,
          tHash,
          isHashMatch: false,
          isCharVerified: false,
          isExactMatch: false,
          comparisons,
          matches: [...matches]
        });
      }

      // Roll hash for next window
      if (i < n - m) {
        const charOut = text.charCodeAt(i);
        const charIn = text.charCodeAt(i + m);
        const oldHash = tHash;
        tHash = (d * (tHash - charOut * h) + charIn) % q;
        if (tHash < 0) tHash = (tHash + q);

        states.push({
          operation: `Rolling Hash to Window ${i + 1}`,
          explanation: `Rolling hash: Subtract leading char '${text[i]}' (${charOut}), multiply by base ${d}, add trailing char '${text[i + m]}' (${charIn}), modulo ${q}. New hash: <strong>${tHash}</strong>.`,
          math: `H_{next} = (${d} * (${oldHash} - ${charOut}*${h}) + ${charIn}) mod ${q} = ${tHash}`,
          phase: 'rolling',
          text,
          pattern,
          windowIdx: i + 1,
          windowStr: text.substring(i + 1, i + 1 + m),
          pHash,
          tHash,
          charOut: text[i],
          charIn: text[i + m],
          isHashMatch: pHash === tHash,
          isCharVerified: false,
          isExactMatch: false,
          comparisons,
          matches: [...matches]
        });
      }
    }

    states.push({
      operation: 'Rabin-Karp Execution Complete',
      explanation: `Search finished. Total matches: <strong>${matches.length}</strong> at indices <code>[${matches.join(', ') || 'None'}]</code>. Total char verifications: ${comparisons}.`,
      math: `Matches: [${matches.join(', ')}] | Average Time: O(n + m) | Space: O(1)`,
      phase: 'complete',
      text,
      pattern,
      windowIdx: n - m,
      windowStr: text.substring(n - m),
      pHash,
      tHash,
      isHashMatch: false,
      isCharVerified: false,
      isExactMatch: false,
      comparisons,
      matches: [...matches]
    });

    return states;
  },

  // -------------------------------------------------------------
  // 4. AHO-CORASICK (Trie with Failure & Output Links)
  // -------------------------------------------------------------
  generateAhoCorasick(text, patterns) {
    const states = [];
    
    // Step 0: Build Trie
    const root = { id: 0, children: {}, fail: 0, output: [], char: 'ROOT' };
    const nodes = [root];
    let nextNodeId = 1;

    for (const pat of patterns) {
      let curr = root;
      for (const ch of pat) {
        if (!curr.children[ch]) {
          const newNode = { id: nextNodeId++, children: {}, fail: 0, output: [], char: ch, parent: curr.id };
          curr.children[ch] = newNode.id;
          nodes.push(newNode);
        }
        curr = nodes[curr.children[ch]];
      }
      curr.output.push(pat);
    }

    // Step 1: Compute Failure Links (BFS)
    const queue = [];
    for (const ch in root.children) {
      const childId = root.children[ch];
      nodes[childId].fail = 0;
      queue.push(childId);
    }

    while (queue.length > 0) {
      const currId = queue.shift();
      const currNode = nodes[currId];

      for (const ch in currNode.children) {
        const nextId = currNode.children[ch];
        queue.push(nextId);

        let f = currNode.fail;
        while (f !== 0 && !nodes[f].children[ch]) {
          f = nodes[f].fail;
        }
        nodes[nextId].fail = nodes[f].children[ch] ? nodes[f].children[ch] : 0;
        nodes[nextId].output = [...nodes[nextId].output, ...nodes[nodes[nextId].fail].output];
      }
    }

    states.push({
      operation: 'Aho-Corasick Automaton Built',
      explanation: `Constructed trie with ${nodes.length} states and BFS failure links for ${patterns.length} patterns: <code>${patterns.join(', ')}</code>.`,
      math: `Automaton: ${nodes.length} states | Failure transitions compiled`,
      phase: 'trie_ready',
      text,
      patterns,
      nodes: JSON.parse(JSON.stringify(nodes)),
      currentStateId: 0,
      charIdx: -1,
      currChar: '',
      foundMatches: []
    });

    // Step 2: Stream text through automaton
    let currState = 0;
    const foundMatches = [];

    for (let i = 0; i < text.length; i++) {
      const ch = text[i];

      // Follow failure links if no transition exists
      let transitionState = currState;
      let failureFollowed = false;

      while (transitionState !== 0 && !nodes[transitionState].children[ch]) {
        transitionState = nodes[transitionState].fail;
        failureFollowed = true;
      }

      const nextState = nodes[transitionState].children[ch] ? nodes[transitionState].children[ch] : 0;

      states.push({
        operation: `Reading Char '${ch}' at Text[${i}]`,
        explanation: failureFollowed 
          ? `No branch for '${ch}' at state ${currState}. Followed failure link to state ${transitionState}, then transitioned to state ${nextState}.`
          : `Transitioning from state ${currState} → state ${nextState} on nucleotide '${ch}'.`,
        math: `State: ${currState} → ${nextState} | Read: Text[${i}] = '${ch}'`,
        phase: 'traverse',
        text,
        patterns,
        nodes: JSON.parse(JSON.stringify(nodes)),
        currentStateId: nextState,
        charIdx: i,
        currChar: ch,
        foundMatches: [...foundMatches]
      });

      currState = nextState;

      // Check outputs
      if (nodes[currState].output.length > 0) {
        for (const matchedPat of nodes[currState].output) {
          const startIdx = i - matchedPat.length + 1;
          foundMatches.push({ pattern: matchedPat, start: startIdx, end: i });
        }

        states.push({
          operation: `Pattern Match Detected! [${nodes[currState].output.join(', ')}]`,
          explanation: `State ${currState} emitted pattern match: <strong>"${nodes[currState].output.join('", "')}"</strong> ending at text index ${i}!`,
          math: `Matches emitted: ${nodes[currState].output.join(', ')} at position ${i - nodes[currState].output[0].length + 1}`,
          phase: 'match_emit',
          text,
          patterns,
          nodes: JSON.parse(JSON.stringify(nodes)),
          currentStateId: currState,
          charIdx: i,
          currChar: ch,
          foundMatches: [...foundMatches]
        });
      }
    }

    states.push({
      operation: 'Aho-Corasick Multi-Pattern Search Complete',
      explanation: `Finished scanning DNA sequence of length ${text.length}. Total motif occurrences recognized: <strong>${foundMatches.length}</strong>.`,
      math: `Total matches: ${foundMatches.length} | Complexity: O(n + sum(m) + z)`,
      phase: 'complete',
      text,
      patterns,
      nodes: JSON.parse(JSON.stringify(nodes)),
      currentStateId: currState,
      charIdx: text.length,
      currChar: '',
      foundMatches: [...foundMatches]
    });

    return states;
  }
};

window.StringSimulations = StringSimulations;
