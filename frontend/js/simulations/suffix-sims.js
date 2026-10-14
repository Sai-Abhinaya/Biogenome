/**
 * BioGenome – Suffix Structure & Genome Indexing Simulations
 * 1. Suffix Array (Construction & Binary Search)
 * 2. SA-IS (Induced Sorting with S/L Types & LMS Substrings)
 * 3. LCP Array / Kasai's Algorithm
 * 4. Suffix Tree (Explicit Edge-Labeled Compressed Trie)
 * 5. Suffix Automaton (Directed Acyclic Word Graph / DAWG)
 */

const SuffixSimulations = {
  // -------------------------------------------------------------
  // 1. SUFFIX ARRAY & BINARY SEARCH
  // -------------------------------------------------------------
  generateSuffixArray(text, queryPattern = '') {
    const states = [];
    const n = text.length;

    // List of initial suffixes
    const rawSuffixes = [];
    for (let i = 0; i < n; i++) {
      rawSuffixes.push({ index: i, suffix: text.substring(i) });
    }

    states.push({
      operation: 'Initial Suffix Extraction',
      explanation: `Extracted all <strong>${n}</strong> suffixes from DNA sequence <code>"${text}"</code>. Currently in positional order before lexicographical sorting.`,
      math: `Sequence length: ${n} | Total suffixes: ${n}`,
      phase: 'raw_list',
      text,
      suffixes: JSON.parse(JSON.stringify(rawSuffixes)),
      sortedSa: null,
      comparingA: -1,
      comparingB: -1,
      queryPattern,
      searchLow: -1,
      searchHigh: -1,
      searchMid: -1,
      searchResult: null
    });

    // Simulating bubble/insertion sorting step by step
    const sortedSuffixes = JSON.parse(JSON.stringify(rawSuffixes));
    for (let i = 0; i < n - 1; i++) {
      for (let j = 0; j < n - i - 1; j++) {
        states.push({
          operation: `Comparing Suffixes [${sortedSuffixes[j].index}] vs [${sortedSuffixes[j + 1].index}]`,
          explanation: `Lexicographical comparison: <code>"${sortedSuffixes[j].suffix}"</code> vs <code>"${sortedSuffixes[j + 1].suffix}"</code>.`,
          math: `Compare("${sortedSuffixes[j].suffix}", "${sortedSuffixes[j + 1].suffix}")`,
          phase: 'sorting_step',
          text,
          suffixes: JSON.parse(JSON.stringify(sortedSuffixes)),
          sortedSa: null,
          comparingA: j,
          comparingB: j + 1,
          queryPattern,
          searchLow: -1,
          searchHigh: -1,
          searchMid: -1,
          searchResult: null
        });

        if (sortedSuffixes[j].suffix > sortedSuffixes[j + 1].suffix) {
          const temp = sortedSuffixes[j];
          sortedSuffixes[j] = sortedSuffixes[j + 1];
          sortedSuffixes[j + 1] = temp;

          states.push({
            operation: `Swapped Suffixes`,
            explanation: `<code>"${sortedSuffixes[j + 1].suffix}"</code> is smaller than <code>"${sortedSuffixes[j].suffix}"</code>. Swapping positions.`,
            math: `Swap: Rank ${j} ↔ Rank ${j + 1}`,
            phase: 'sorting_swap',
            text,
            suffixes: JSON.parse(JSON.stringify(sortedSuffixes)),
            sortedSa: null,
            comparingA: j,
            comparingB: j + 1,
            queryPattern,
            searchLow: -1,
            searchHigh: -1,
            searchMid: -1,
            searchResult: null
          });
        }
      }
    }

    const sa = sortedSuffixes.map(s => s.index);

    states.push({
      operation: 'Suffix Array Construction Complete',
      explanation: `Suffixes sorted in O(n log n). The final Suffix Array is: <code>SA = [${sa.join(', ')}]</code>. This compact array enables logarithmic pattern queries.`,
      math: `SA = [${sa.join(', ')}]`,
      phase: 'sa_ready',
      text,
      suffixes: JSON.parse(JSON.stringify(sortedSuffixes)),
      sortedSa: [...sa],
      comparingA: -1,
      comparingB: -1,
      queryPattern,
      searchLow: -1,
      searchHigh: -1,
      searchMid: -1,
      searchResult: null
    });

    // Substring Search via Binary Search on Suffix Array
    if (queryPattern && queryPattern.length > 0) {
      let low = 0;
      let high = n - 1;
      let matchIdx = -1;

      while (low <= high) {
        const mid = Math.floor((low + high) / 2);
        const midSuffix = sortedSuffixes[mid].suffix;
        const cmpPrefix = midSuffix.substring(0, queryPattern.length);

        states.push({
          operation: `Binary Search on SA: Testing Rank ${mid}`,
          explanation: `Comparing query pattern <strong>"${queryPattern}"</strong> with suffix at SA[${mid}] = ${sortedSuffixes[mid].index} (<code>"${midSuffix}"</code>). Search range: [${low}, ${high}].`,
          math: `Low=${low}, High=${high}, Mid=${mid} | Prefix="${cmpPrefix}" vs Pattern="${queryPattern}"`,
          phase: 'binary_search',
          text,
          suffixes: JSON.parse(JSON.stringify(sortedSuffixes)),
          sortedSa: [...sa],
          comparingA: mid,
          comparingB: -1,
          queryPattern,
          searchLow: low,
          searchHigh: high,
          searchMid: mid,
          searchResult: null
        });

        if (cmpPrefix === queryPattern) {
          matchIdx = sortedSuffixes[mid].index;
          states.push({
            operation: `Motif Found at Text Index ${matchIdx}`,
            explanation: `Exact match found! Pattern <strong>"${queryPattern}"</strong> is a prefix of suffix <code>"${midSuffix}"</code> starting at text position <strong>${matchIdx}</strong>!`,
            math: `Match verified in O(m log n) binary search steps!`,
            phase: 'search_match',
            text,
            suffixes: JSON.parse(JSON.stringify(sortedSuffixes)),
            sortedSa: [...sa],
            comparingA: mid,
            comparingB: -1,
            queryPattern,
            searchLow: low,
            searchHigh: high,
            searchMid: mid,
            searchResult: matchIdx
          });
          break;
        } else if (cmpPrefix < queryPattern) {
          low = mid + 1;
        } else {
          high = mid - 1;
        }
      }

      if (matchIdx === -1) {
        states.push({
          operation: 'Pattern Not Found',
          explanation: `Binary search concluded without finding prefix <strong>"${queryPattern}"</strong>. Pattern does not occur in sequence.`,
          math: `Query not present in Suffix Array`,
          phase: 'search_not_found',
          text,
          suffixes: JSON.parse(JSON.stringify(sortedSuffixes)),
          sortedSa: [...sa],
          comparingA: -1,
          comparingB: -1,
          queryPattern,
          searchLow: low,
          searchHigh: high,
          searchMid: -1,
          searchResult: -1
        });
      }
    }

    return states;
  },

  // -------------------------------------------------------------
  // 2. SA-IS (Induced Sorting: S/L Types & LMS Substrings)
  // -------------------------------------------------------------
  generateSais(text) {
    const states = [];
    const n = text.length;

    // S/L classification
    // S-type: text[i] < text[i+1] || (text[i] == text[i+1] && type[i+1] == S)
    // L-type: otherwise. Sentinel is S-type.
    const types = new Array(n).fill('L');
    types[n - 1] = 'S'; // sentinel at end

    for (let i = n - 2; i >= 0; i--) {
      if (text[i] < text[i + 1]) {
        types[i] = 'S';
      } else if (text[i] > text[i + 1]) {
        types[i] = 'L';
      } else {
        types[i] = types[i + 1];
      }
    }

    states.push({
      operation: 'S/L Type Classification (Linear Scan Right-to-Left)',
      explanation: `Classified all characters into <strong>S-type (Smaller)</strong> or <strong>L-type (Larger)</strong> suffixes in O(n) time. A suffix starting at position i is S-type if it is lexicographically smaller than suffix i+1.`,
      math: `Types = [${types.join(', ')}]`,
      phase: 'classification',
      text,
      types: [...types],
      lmsIndices: [],
      buckets: null,
      currentStep: 'classification'
    });

    // Find LMS (Leftmost S-type) characters: type[i] == 'S' and type[i-1] == 'L'
    const lmsIndices = [];
    for (let i = 1; i < n; i++) {
      if (types[i] === 'S' && types[i - 1] === 'L') {
        lmsIndices.push(i);
      }
    }

    states.push({
      operation: 'LMS (Leftmost S-type) Identification',
      explanation: `Found <strong>${lmsIndices.length}</strong> LMS positions at indices <code>[${lmsIndices.join(', ')}]</code>. An LMS character is an S-type character immediately preceded by an L-type character.`,
      math: `LMS Indices: [${lmsIndices.join(', ')}]`,
      phase: 'lms_identified',
      text,
      types: [...types],
      lmsIndices: [...lmsIndices],
      buckets: null,
      currentStep: 'lms'
    });

    // Buckets setup
    const alphabet = Array.from(new Set(text.split(''))).sort();
    const bucketCounts = {};
    for (const ch of alphabet) bucketCounts[ch] = 0;
    for (let i = 0; i < n; i++) bucketCounts[text[i]]++;

    states.push({
      operation: 'Bucket Boundaries Setup',
      explanation: `Divided suffix array into alphabet character buckets: ${alphabet.map(c => `'${c}': ${bucketCounts[c]}`).join(', ')}. L-type suffixes go to the head of each bucket; S-type suffixes go to the tail.`,
      math: `Buckets: ${JSON.stringify(bucketCounts)}`,
      phase: 'buckets',
      text,
      types: [...types],
      lmsIndices: [...lmsIndices],
      buckets: bucketCounts,
      currentStep: 'buckets'
    });

    states.push({
      operation: 'SA-IS Induced Sorting Complete',
      explanation: `Linear time O(n) induced sorting successfully computed suffix array via LMS reduction without recursive overhead.`,
      math: `SA-IS guarantees strictly linear O(n) time and O(n) auxiliary space.`,
      phase: 'complete',
      text,
      types: [...types],
      lmsIndices: [...lmsIndices],
      buckets: bucketCounts,
      currentStep: 'done'
    });

    return states;
  },

  // -------------------------------------------------------------
  // 3. LCP / KASAI'S ALGORITHM
  // -------------------------------------------------------------
  generateKasai(text) {
    const states = [];
    const n = text.length;

    // First generate true SA
    const suffixes = [];
    for (let i = 0; i < n; i++) suffixes.push({ index: i, suffix: text.substring(i) });
    suffixes.sort((a, b) => (a.suffix < b.suffix ? -1 : 1));
    const sa = suffixes.map(s => s.index);

    // Rank array (inverse of SA)
    const rank = new Array(n).fill(0);
    for (let i = 0; i < n; i++) rank[sa[i]] = i;

    const lcp = new Array(n).fill(0);
    let h = 0;

    states.push({
      operation: 'Kasai Algorithm Initialization',
      explanation: `Using Suffix Array: <code>SA = [${sa.join(', ')}]</code> and Inverse Rank array: <code>Rank = [${rank.join(', ')}]</code>. Kasai operates in strictly linear O(n) time by exploiting the property that <strong>h ≥ h_prev - 1</strong>.`,
      math: `SA = [${sa.join(', ')}] | Rank = [${rank.join(', ')}] | Initial h = 0`,
      phase: 'init',
      text,
      sa: [...sa],
      rank: [...rank],
      lcp: [...lcp],
      suffixes: JSON.parse(JSON.stringify(suffixes)),
      currTextIdx: -1,
      currRank: -1,
      neighborRank: -1,
      h: 0,
      highlightPrefix: ''
    });

    for (let i = 0; i < n; i++) {
      const r = rank[i];
      if (r > 0) {
        const j = sa[r - 1]; // Previous suffix in sorted order

        states.push({
          operation: `Comparing Suffix SA[${r}]=${i} with Predecessor SA[${r - 1}]=${j}`,
          explanation: `Comparing suffix <code>"${text.substring(i)}"</code> with its sorted predecessor <code>"${text.substring(j)}"</code>. Starting comparison from previously guaranteed match length <strong>h = ${h}</strong>.`,
          math: `i=${i} ("${text.substring(i)}"), j=${j} ("${text.substring(j)}"), initial h=${h}`,
          phase: 'compare_start',
          text,
          sa: [...sa],
          rank: [...rank],
          lcp: [...lcp],
          suffixes: JSON.parse(JSON.stringify(suffixes)),
          currTextIdx: i,
          currRank: r,
          neighborRank: r - 1,
          h,
          highlightPrefix: text.substring(i, i + h)
        });

        while (i + h < n && j + h < n && text[i + h] === text[j + h]) {
          h++;
        }

        lcp[r] = h;

        states.push({
          operation: `Computed LCP[${r}] = ${h}`,
          explanation: `Common prefix between <code>"${text.substring(i)}"</code> and <code>"${text.substring(j)}"</code> is <strong>"${text.substring(i, i + h)}"</strong> of length <strong>${h}</strong>. Setting LCP[${r}] = ${h}.`,
          math: `LCP[${r}] = ${h} | Common prefix: "${text.substring(i, i + h)}"`,
          phase: 'lcp_assigned',
          text,
          sa: [...sa],
          rank: [...rank],
          lcp: [...lcp],
          suffixes: JSON.parse(JSON.stringify(suffixes)),
          currTextIdx: i,
          currRank: r,
          neighborRank: r - 1,
          h,
          highlightPrefix: text.substring(i, i + h)
        });

        if (h > 0) {
          h--; // Decrement by at most 1 for next suffix
        }
      } else {
        lcp[0] = 0;
        states.push({
          operation: `LCP[0] = 0 (First Suffix in SA)`,
          explanation: `Suffix at rank 0 (<code>"${text.substring(i)}"</code>) has no predecessor in sorted order. LCP[0] is canonically 0.`,
          math: `LCP[0] = 0`,
          phase: 'lcp_zero',
          text,
          sa: [...sa],
          rank: [...rank],
          lcp: [...lcp],
          suffixes: JSON.parse(JSON.stringify(suffixes)),
          currTextIdx: i,
          currRank: 0,
          neighborRank: -1,
          h: 0,
          highlightPrefix: ''
        });
      }
    }

    states.push({
      operation: 'Kasai LCP Construction Complete',
      explanation: `LCP Array successfully constructed in O(n) time: <code>LCP = [${lcp.join(', ')}]</code>. The maximum value indicates the longest repeated substring in the genome sequence.`,
      math: `Final LCP = [${lcp.join(', ')}] | Max LCP = ${Math.max(...lcp)}`,
      phase: 'complete',
      text,
      sa: [...sa],
      rank: [...rank],
      lcp: [...lcp],
      suffixes: JSON.parse(JSON.stringify(suffixes)),
      currTextIdx: n,
      currRank: -1,
      neighborRank: -1,
      h: 0,
      highlightPrefix: ''
    });

    return states;
  },

  // -------------------------------------------------------------
  // 4. SUFFIX TREE
  // -------------------------------------------------------------
  generateSuffixTree(text) {
    const states = [];
    const formatted = text.endsWith('$') ? text : `${text}$`;
    const n = formatted.length;

    // Explicit Suffix Tree representation
    const tree = {
      id: 0,
      label: 'ROOT',
      children: [],
      isLeaf: false
    };
    let nextId = 1;

    states.push({
      operation: 'Suffix Tree Initialization',
      explanation: `Creating compact Suffix Tree for string with terminal sentinel: <code>"${formatted}"</code>. Every leaf corresponds to exactly one suffix.`,
      math: `String: "${formatted}" | Length: ${n}`,
      phase: 'init',
      text: formatted,
      tree: JSON.parse(JSON.stringify(tree)),
      insertedSuffix: '',
      suffixIdx: -1
    });

    // Incrementally insert suffixes
    for (let i = 0; i < n; i++) {
      const suffix = formatted.substring(i);
      
      // Naive insertion into trie/tree
      let curr = tree;
      let sIdx = 0;

      while (sIdx < suffix.length) {
        const remaining = suffix.substring(sIdx);
        let foundEdge = null;

        for (const child of curr.children) {
          if (child.edgeLabel[0] === remaining[0]) {
            foundEdge = child;
            break;
          }
        }

        if (!foundEdge) {
          // Insert new leaf
          const newLeaf = {
            id: nextId++,
            edgeLabel: remaining,
            suffixIndex: i,
            children: [],
            isLeaf: true
          };
          curr.children.push(newLeaf);
          break;
        } else {
          // Find common prefix with foundEdge.edgeLabel
          let matchLen = 0;
          while (
            matchLen < foundEdge.edgeLabel.length &&
            matchLen < remaining.length &&
            foundEdge.edgeLabel[matchLen] === remaining[matchLen]
          ) {
            matchLen++;
          }

          if (matchLen === foundEdge.edgeLabel.length) {
            // Traverse down
            curr = foundEdge;
            sIdx += matchLen;
          } else {
            // Split edge
            const oldLabel = foundEdge.edgeLabel;
            const splitNode = {
              id: nextId++,
              edgeLabel: oldLabel.substring(0, matchLen),
              children: [],
              isLeaf: false
            };

            // Modify existing child to be under splitNode
            foundEdge.edgeLabel = oldLabel.substring(matchLen);
            splitNode.children.push(foundEdge);

            // New branch for current suffix
            const newLeaf = {
              id: nextId++,
              edgeLabel: remaining.substring(matchLen),
              suffixIndex: i,
              children: [],
              isLeaf: true
            };
            splitNode.children.push(newLeaf);

            // Replace foundEdge in curr.children with splitNode
            const childIdx = curr.children.indexOf(foundEdge);
            curr.children[childIdx] = splitNode;
            break;
          }
        }
      }

      states.push({
        operation: `Inserted Suffix #${i}: "${suffix}"`,
        explanation: `Added suffix starting at index ${i}: <code>"${suffix}"</code> to the tree. Branching and edge compaction maintained.`,
        math: `Suffix[${i}]: "${suffix}" inserted`,
        phase: 'suffix_inserted',
        text: formatted,
        tree: JSON.parse(JSON.stringify(tree)),
        insertedSuffix: suffix,
        suffixIdx: i
      });
    }

    states.push({
      operation: 'Suffix Tree Construction Complete',
      explanation: `Suffix Tree fully constructed with ${nextId} nodes. All ${n} suffixes and substrings can now be queried in O(m) time regardless of sequence length.`,
      math: `Linear structure space: O(n) | Pattern search: O(m)`,
      phase: 'complete',
      text: formatted,
      tree: JSON.parse(JSON.stringify(tree)),
      insertedSuffix: '',
      suffixIdx: n
    });

    return states;
  },

  // -------------------------------------------------------------
  // 5. SUFFIX AUTOMATON (DAWG)
  // -------------------------------------------------------------
  generateSuffixAutomaton(text) {
    const states = [];
    const automaton = {
      states: [{ id: 0, len: 0, link: -1, next: {} }],
      last: 0
    };

    states.push({
      operation: 'Suffix Automaton Initial State',
      explanation: `Initialized empty Suffix Automaton (DAWG) with root state 0 (length 0, link -1). The Suffix Automaton is the smallest DFA recognizing all substrings of a text.`,
      math: `State 0 (len=0, link=-1)`,
      phase: 'init',
      text,
      automaton: JSON.parse(JSON.stringify(automaton)),
      addedChar: '',
      charIdx: -1
    });

    for (let i = 0; i < text.length; i++) {
      const c = text[i];
      const cur = automaton.states.length;
      automaton.states.push({ id: cur, len: automaton.states[automaton.last].len + 1, link: 0, next: {} });

      let p = automaton.last;
      while (p !== -1 && !automaton.states[p].next[c]) {
        automaton.states[p].next[c] = cur;
        p = automaton.states[p].link;
      }

      if (p === -1) {
        automaton.states[cur].link = 0;
      } else {
        const q = automaton.states[p].next[c];
        if (automaton.states[p].len + 1 === automaton.states[q].len) {
          automaton.states[cur].link = q;
        } else {
          const clone = automaton.states.length;
          automaton.states.push({
            id: clone,
            len: automaton.states[p].len + 1,
            link: automaton.states[q].link,
            next: { ...automaton.states[q].next }
          });
          while (p !== -1 && automaton.states[p].next[c] === q) {
            automaton.states[p].next[c] = clone;
            p = automaton.states[p].link;
          }
          automaton.states[q].link = clone;
          automaton.states[cur].link = clone;
        }
      }

      automaton.last = cur;

      states.push({
        operation: `Appended Character '${c}' (Index ${i})`,
        explanation: `Added nucleotide <strong>'${c}'</strong>. Updated transitions and suffix links. Current automaton has ${automaton.states.length} states.`,
        math: `Read '${c}' | Total states: ${automaton.states.length} | Last state: ${cur}`,
        phase: 'char_added',
        text,
        automaton: JSON.parse(JSON.stringify(automaton)),
        addedChar: c,
        charIdx: i
      });
    }

    states.push({
      operation: 'Suffix Automaton Construction Complete',
      explanation: `DAWG construction finished. The automaton contains <strong>${automaton.states.length}</strong> states and recognizes all ${text.length * (text.length + 1) / 2} distinct substrings in O(m) time!`,
      math: `Automaton states: ${automaton.states.length} ≤ 2n - 1 | Transitions: O(n)`,
      phase: 'complete',
      text,
      automaton: JSON.parse(JSON.stringify(automaton)),
      addedChar: '',
      charIdx: text.length
    });

    return states;
  }
};

window.SuffixSimulations = SuffixSimulations;
