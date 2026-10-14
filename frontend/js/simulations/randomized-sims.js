/**
 * BioGenome – Randomized Algorithms Simulations
 * 1. Randomized QuickSort (Random Pivot & 3-Way Partitioning)
 * 2. Reservoir Sampling (Streaming Single-Cell Data)
 * 3. Miller-Rabin Primality Testing (Modular Exponentiation & Witness Tests)
 */

const RandomizedSimulations = {
  // -------------------------------------------------------------
  // 1. RANDOMIZED QUICKSORT
  // -------------------------------------------------------------
  generateRandomizedQuickSort(items) {
    const states = [];
    const arr = items.map((it, idx) => ({ id: idx, val: it.val !== undefined ? it.val : it, label: it.label || `${it}` }));

    states.push({
      operation: 'Randomized QuickSort Initialization',
      explanation: `Loaded <strong>${arr.length}</strong> genomic sequence records to be sorted. Randomized pivot selection guarantees expected O(n log n) time and eliminates pathological worst-case inputs.`,
      math: `Array: [${arr.map(a => a.val).join(', ')}] | Expected: O(n log n)`,
      phase: 'init',
      array: JSON.parse(JSON.stringify(arr)),
      pivotId: -1,
      left: 0,
      right: arr.length - 1,
      partition: { less: [], equal: [], greater: [] }
    });

    function qsort(low, high) {
      if (low >= high) return;

      // Pick random pivot between low and high
      const pivotIdx = low + Math.floor(Math.random() * (high - low + 1));
      const pivotItem = arr[pivotIdx];

      states.push({
        operation: `Random Pivot Picked: '${pivotItem.label}' (Value: ${pivotItem.val})`,
        explanation: `Randomly selected pivot at index ${pivotIdx} with value <strong>${pivotItem.val}</strong> from subsegment [${low}..${high}].`,
        math: `Random Pivot index: ${pivotIdx} | Value: ${pivotItem.val}`,
        phase: 'pivot_selected',
        array: JSON.parse(JSON.stringify(arr)),
        pivotId: pivotItem.id,
        left: low,
        right: high,
        partition: { less: [], equal: [], greater: [] }
      });

      // 3-way partition
      const less = [];
      const equal = [];
      const greater = [];

      for (let i = low; i <= high; i++) {
        if (arr[i].val < pivotItem.val) less.push(arr[i]);
        else if (arr[i].val === pivotItem.val) equal.push(arr[i]);
        else greater.push(arr[i]);
      }

      states.push({
        operation: `Partitioned Subsegment [${low}..${high}]`,
        explanation: `Divided elements around pivot <strong>${pivotItem.val}</strong>:<br>
          • Less (${less.length}): [${less.map(x => x.val).join(', ')}]<br>
          • Equal (${equal.length}): [${equal.map(x => x.val).join(', ')}]<br>
          • Greater (${greater.length}): [${greater.map(x => x.val).join(', ')}]`,
        math: `|Less|=${less.length}, |Equal|=${equal.length}, |Greater|=${greater.length}`,
        phase: 'partitioned',
        array: JSON.parse(JSON.stringify(arr)),
        pivotId: pivotItem.id,
        left: low,
        right: high,
        partition: {
          less: less.map(x => x.id),
          equal: equal.map(x => x.id),
          greater: greater.map(x => x.id)
        }
      });

      // Re-place into arr
      let writeIdx = low;
      for (const x of less) arr[writeIdx++] = x;
      const equalStart = writeIdx;
      for (const x of equal) arr[writeIdx++] = x;
      const equalEnd = writeIdx - 1;
      for (const x of greater) arr[writeIdx++] = x;

      states.push({
        operation: `Subsegment Reassembled around Pivot`,
        explanation: `Array updated in-place. Pivot element(s) now occupy their final sorted indices [${equalStart}..${equalEnd}].`,
        math: `Pivot finalized at range [${equalStart}..${equalEnd}]`,
        phase: 'subsegment_written',
        array: JSON.parse(JSON.stringify(arr)),
        pivotId: pivotItem.id,
        left: low,
        right: high,
        partition: { less: [], equal: [], greater: [] }
      });

      qsort(low, equalStart - 1);
      qsort(equalEnd + 1, high);
    }

    qsort(0, arr.length - 1);

    states.push({
      operation: 'Randomized QuickSort Complete',
      explanation: `Entire genomic dataset successfully sorted in expected O(n log n) time: <code>[${arr.map(a => a.val).join(', ')}]</code>.`,
      math: `Sorted Array: [${arr.map(a => a.val).join(', ')}] | Space: O(log n)`,
      phase: 'complete',
      array: JSON.parse(JSON.stringify(arr)),
      pivotId: -1,
      left: 0,
      right: arr.length - 1,
      partition: { less: [], equal: [], greater: [] }
    });

    return states;
  },

  // -------------------------------------------------------------
  // 2. RESERVOIR SAMPLING
  // -------------------------------------------------------------
  generateReservoirSampling(stream, k = 3) {
    const states = [];
    const reservoir = [];

    states.push({
      operation: `Reservoir Sampling Initialized (k = ${k})`,
      explanation: `Configured streaming sample buffer of capacity <strong>k = ${k}</strong>. The stream length is unknown in advance; every incoming observation has an exact <strong>k / N</strong> probability of being retained in the final sample.`,
      math: `Capacity k = ${k} | Stream Items = ${stream.length}`,
      phase: 'init',
      stream: JSON.parse(JSON.stringify(stream)),
      reservoir: [],
      currItem: null,
      streamIdx: -1,
      randJ: -1,
      action: 'init'
    });

    for (let i = 0; i < stream.length; i++) {
      const item = stream[i];

      if (i < k) {
        // First k items fill reservoir unconditionally
        reservoir.push(item);
        states.push({
          operation: `Stream Item #${i + 1} Enters (Direct Fill)`,
          explanation: `Reservoir not yet full (${i + 1}/${k}). Added observation <strong>"${item.label || item}"</strong> directly into slot ${i}.`,
          math: `Slot ${i} ← Item #${i + 1}`,
          phase: 'fill_reservoir',
          stream: JSON.parse(JSON.stringify(stream)),
          reservoir: JSON.parse(JSON.stringify(reservoir)),
          currItem: item,
          streamIdx: i,
          randJ: i,
          action: 'added_direct'
        });
      } else {
        // Probability k / (i + 1)
        const j = Math.floor(Math.random() * (i + 1));
        const retained = j < k;

        if (retained) {
          const replaced = reservoir[j];
          reservoir[j] = item;
          states.push({
            operation: `Item #${i + 1} Retained! (Replaced Slot ${j})`,
            explanation: `Generated random integer j = <strong>${j}</strong> in range [0..${i}]. Since j < ${k}, item <strong>"${item.label || item}"</strong> replaces slot ${j} (previous: "${replaced.label || replaced}").`,
            math: `Random j = ${j} < k (${k}) → Replaced slot ${j}. Retention Prob = ${k}/${i + 1} (${((k / (i + 1)) * 100).toFixed(1)}%)`,
            phase: 'replace',
            stream: JSON.parse(JSON.stringify(stream)),
            reservoir: JSON.parse(JSON.stringify(reservoir)),
            currItem: item,
            streamIdx: i,
            randJ: j,
            action: 'replaced'
          });
        } else {
          states.push({
            operation: `Item #${i + 1} Discarded (j = ${j} ≥ k)`,
            explanation: `Generated random integer j = <strong>${j}</strong> in range [0..${i}]. Since j ≥ ${k}, observation <strong>"${item.label || item}"</strong> is discarded. Current sample preserved.`,
            math: `Random j = ${j} ≥ k (${k}) → Discarded. Discard Prob = ${(100 - (k / (i + 1)) * 100).toFixed(1)}%`,
            phase: 'discard',
            stream: JSON.parse(JSON.stringify(stream)),
            reservoir: JSON.parse(JSON.stringify(reservoir)),
            currItem: item,
            streamIdx: i,
            randJ: j,
            action: 'discarded'
          });
        }
      }
    }

    states.push({
      operation: 'Streaming Sample Finalized',
      explanation: `Streaming pipeline terminated. Final unbiased sample of size ${k}: <code>[${reservoir.map(r => r.label || r).join(', ')}]</code>. Every item from the stream of length ${stream.length} had identical retention probability: <strong>${((k / stream.length) * 100).toFixed(1)}%</strong>.`,
      math: `Unbiased Sample Probability: P(retain) = ${k}/${stream.length} = ${(k / stream.length).toFixed(3)} | Space: O(k)`,
      phase: 'complete',
      stream: JSON.parse(JSON.stringify(stream)),
      reservoir: JSON.parse(JSON.stringify(reservoir)),
      currItem: null,
      streamIdx: stream.length,
      randJ: -1,
      action: 'done'
    });

    return states;
  },

  // -------------------------------------------------------------
  // 3. MILLER-RABIN PRIMALITY TESTING
  // -------------------------------------------------------------
  generateMillerRabin(n, iterations = 3) {
    const states = [];

    // Disclaimer
    const disclaimer = 'This algorithm is being demonstrated as a randomized computational technique; it is not a biological diagnosis.';

    states.push({
      operation: 'Miller-Rabin Initialization',
      explanation: `Testing whether integer <strong>n = ${n}</strong> is prime. <em>Note: ${disclaimer}</em>`,
      math: `n = ${n} | Testing with k = ${iterations} independent random bases`,
      phase: 'init',
      n,
      s: 0,
      d: 0,
      witness: -1,
      isProbablePrime: null
    });

    if (n <= 1) {
      states.push({
        operation: 'Trivial Composite',
        explanation: `${n} is less than or equal to 1, hence composite.`,
        math: `n ≤ 1 → Composite`,
        phase: 'complete',
        n, s: 0, d: 0, witness: -1, isProbablePrime: false
      });
      return states;
    }
    if (n <= 3) {
      states.push({
        operation: 'Trivial Prime',
        explanation: `${n} is prime.`,
        math: `n = ${n} → Prime`,
        phase: 'complete',
        n, s: 0, d: 0, witness: -1, isProbablePrime: true
      });
      return states;
    }
    if (n % 2 === 0) {
      states.push({
        operation: 'Even Number Check',
        explanation: `${n} is even and greater than 2, hence composite.`,
        math: `${n} mod 2 = 0 → Composite`,
        phase: 'complete',
        n, s: 0, d: 0, witness: 2, isProbablePrime: false
      });
      return states;
    }

    // Factor n - 1 = 2^s * d with d odd
    let d = n - 1;
    let s = 0;
    while (d % 2 === 0) {
      d = Math.floor(d / 2);
      s++;
    }

    states.push({
      operation: `Factored n - 1 = 2^${s} × ${d}`,
      explanation: `Decomposed <code>n - 1 = ${n - 1}</code> into <strong>2^${s} × ${d}</strong>, where d = ${d} is odd.`,
      math: `s = ${s}, d = ${d} (odd)`,
      phase: 'factored',
      n, s, d, witness: -1, isProbablePrime: null
    });

    // Helper for modular exponentiation: (base^exp) % mod
    function powerMod(base, exp, mod) {
      let res = 1;
      let b = base % mod;
      let e = exp;
      while (e > 0) {
        if (e % 2 === 1) res = (res * b) % mod;
        b = (b * b) % mod;
        e = Math.floor(e / 2);
      }
      return res;
    }

    let isPrime = true;
    for (let i = 0; i < iterations; i++) {
      // Pick random base a in [2, n - 2]
      const a = 2 + Math.floor(Math.random() * (n - 3));
      let x = powerMod(a, d, n);

      states.push({
        operation: `Iteration ${i + 1}: Testing Random Base a = ${a}`,
        explanation: `Selected random base a = <strong>${a}</strong>. Computed modular exponentiation x = a^d mod n = ${a}^${d} mod ${n} = <strong>${x}</strong>.`,
        math: `x = ${a}^${d} mod ${n} = ${x}`,
        phase: 'base_test',
        n, s, d, witness: a, isProbablePrime: null, x
      });

      if (x === 1 || x === n - 1) {
        states.push({
          operation: `Base a = ${a} Passes Initial Test`,
          explanation: `x is ${x} (≡ ±1 mod n). Base a = ${a} is not a witness for compositeness; candidate survives round ${i + 1}.`,
          math: `x ≡ ±1 mod ${n} → Round ${i + 1} passed`,
          phase: 'round_passed',
          n, s, d, witness: a, isProbablePrime: null, x
        });
        continue;
      }

      let passedSquaring = false;
      for (let r = 1; r < s; r++) {
        x = (x * x) % n;
        states.push({
          operation: `Squaring Step ${r}: x = x² mod n = ${x}`,
          explanation: `Squaring intermediate: x² mod ${n} = <strong>${x}</strong>.`,
          math: `x = x² mod ${n} = ${x}`,
          phase: 'squaring',
          n, s, d, witness: a, isProbablePrime: null, x
        });

        if (x === n - 1) {
          passedSquaring = true;
          states.push({
            operation: `Base a = ${a} Passes via Squaring (x ≡ -1 mod n)`,
            explanation: `Intermediate reached ${n - 1} (≡ -1 mod n). Round ${i + 1} passed.`,
            math: `x = ${n - 1} ≡ -1 mod ${n}`,
            phase: 'round_passed',
            n, s, d, witness: a, isProbablePrime: null, x
          });
          break;
        }
      }

      if (!passedSquaring) {
        isPrime = false;
        states.push({
          operation: `Composite Witness Confirmed! (Base a = ${a})`,
          explanation: `Base a = <strong>${a}</strong> is an unambiguous witness to the compositeness of <strong>${n}</strong>. ${n} is definitely composite!`,
          math: `Witness a = ${a} proves ${n} is COMPOSITE`,
          phase: 'composite_witness',
          n, s, d, witness: a, isProbablePrime: false, x
        });
        break;
      }
    }

    states.push({
      operation: isPrime ? `Result: Probably Prime` : `Result: Definitely Composite`,
      explanation: isPrime
        ? `Surviving ${iterations} rounds, <strong>${n} is prime</strong> with error probability ≤ (1/4)^${iterations} = ${(Math.pow(0.25, iterations) * 100).toFixed(4)}%.`
        : `<strong>${n} is definitely composite</strong>.`,
      math: isPrime ? `PROBABLY PRIME (Error ≤ 4^-${iterations})` : `COMPOSITE`,
      phase: 'complete',
      n, s, d, witness: -1, isProbablePrime: isPrime
    });

    return states;
  }
};

window.RandomizedSimulations = RandomizedSimulations;
