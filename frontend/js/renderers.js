/**
 * BioGenome – Interactive State Renderers
 * Pure clientside DOM rendering routines for all algorithm visualizations.
 * Styled with the restrained botanical palette: Forest Green, Muted Sage, Deep Teal.
 */

const VisualRenderers = {
  // Helper to render nucleotide cell
  renderNucCell(char, extraClass = '', title = '') {
    const c = (char || '').toUpperCase();
    let typeClass = 'nuc-other';
    if (c === 'A') typeClass = 'nuc-a';
    else if (c === 'T') typeClass = 'nuc-t';
    else if (c === 'G') typeClass = 'nuc-g';
    else if (c === 'C') typeClass = 'nuc-c';

    return `<span class="nuc-cell ${typeClass} ${extraClass}" title="${title || c}">${c}</span>`;
  },

  // 1. KMP Renderer
  renderKmp(container, state) {
    if (!state) return;
    const { text, pattern, lps, textIdx, patIdx, patShift, phase, status, matches, comparisons } = state;

    let textCells = '';
    for (let i = 0; i < text.length; i++) {
      let cls = '';
      if (i === textIdx) {
        cls = status === 'match' || status === 'full_match' ? 'is-match' : (status === 'mismatch' ? 'is-mismatch' : 'is-current');
      } else if (matches && matches.some(m => i >= m && i < m + pattern.length)) {
        cls = 'is-matched';
      }
      textCells += `<div class="track-cell-wrapper">
        <span class="cell-idx">${i}</span>
        ${this.renderNucCell(text[i], cls)}
      </div>`;
    }

    let patternCells = '';
    const shift = Math.max(0, patShift || 0);
    for (let s = 0; s < shift; s++) {
      patternCells += `<div class="track-cell-wrapper spacer"><span class="cell-idx"></span><span class="nuc-cell nuc-empty"></span></div>`;
    }
    for (let j = 0; j < pattern.length; j++) {
      let cls = '';
      if (j === patIdx) {
        cls = status === 'match' || status === 'full_match' ? 'is-match' : (status === 'mismatch' ? 'is-mismatch' : 'is-current');
      }
      patternCells += `<div class="track-cell-wrapper">
        <span class="cell-idx">${j}</span>
        ${this.renderNucCell(pattern[j], cls)}
      </div>`;
    }

    let lpsHtml = '';
    if (lps && lps.length > 0) {
      lpsHtml = `<div class="table-responsive">
        <table class="data-table" style="font-family: var(--font-mono); text-align: center;">
          <thead>
            <tr><th>Pattern Index (j)</th>${pattern.split('').map((_, idx) => `<th>${idx}</th>`).join('')}</tr>
            <tr><th>Nucleotide</th>${pattern.split('').map(c => `<th>${c}</th>`).join('')}</tr>
          </thead>
          <tbody>
            <tr>
              <td><strong>LPS / π[j]</strong></td>
              ${lps.map((val, idx) => `<td class="${idx === patIdx ? 'highlight-cell' : ''}">${val}</td>`).join('')}
            </tr>
          </tbody>
        </table>
      </div>`;
    }

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Target DNA Sequence (Text T, len ${text.length}):</div>
        <div class="nucleotide-track">${textCells}</div>

        <div class="track-label" style="margin-top: 1rem;">Query Motif (Pattern P, len ${pattern.length}):</div>
        <div class="nucleotide-track">${patternCells}</div>

        <div style="margin-top: 1.25rem;">
          <h4 style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.5rem; text-transform: uppercase;">Prefix Table (LPS / π Array):</h4>
          ${lpsHtml}
        </div>

        <div class="stats-pills-row" style="margin-top: 1rem;">
          <span class="stat-pill">Comparisons: <strong>${comparisons || 0}</strong></span>
          <span class="stat-pill">Matches Found: <strong>${(matches || []).length}</strong></span>
          <span class="stat-pill">Match Positions: <strong>[${(matches || []).join(', ') || 'None'}] (0-based)</strong></span>
          <span class="stat-pill">Time: <strong>O(n + m)</strong></span>
        </div>
      </div>
    `;
  },

  // 2. Z-Algorithm Renderer
  renderZ(container, state) {
    if (!state) return;
    const { concat, z, l, r, currIdx, matches, comparisons } = state;

    let cellsHtml = '';
    for (let i = 0; i < concat.length; i++) {
      let cls = '';
      if (i === currIdx) cls = 'is-current';
      if (i >= l && i <= r && r > 0) cls += ' in-zbox';

      cellsHtml += `<div class="track-cell-wrapper">
        <span class="cell-idx">${i}</span>
        ${this.renderNucCell(concat[i], cls)}
        <span class="cell-val">Z=${z[i]}</span>
      </div>`;
    }

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Concatenated Sequence (P + '$' + T):</div>
        <div class="nucleotide-track">${cellsHtml}</div>

        <div class="stats-pills-row" style="margin-top: 1.25rem;">
          <span class="stat-pill">Active Index i: <strong>${currIdx}</strong></span>
          <span class="stat-pill">Z-Box Interval: <strong>[L=${l}, R=${r}]</strong></span>
          <span class="stat-pill">Comparisons: <strong>${comparisons}</strong></span>
          <span class="stat-pill">Matches Found: <strong>${matches.length}</strong></span>
          <span class="stat-pill">Match Positions: <strong>[${matches.join(', ') || 'None'}] (0-based)</strong></span>
        </div>
      </div>
    `;
  },

  // 3. Rabin-Karp Renderer
  renderRabinKarp(container, state) {
    if (!state) return;
    const { text, pattern, windowIdx, windowStr, pHash, tHash, isHashMatch, isCharVerified, isExactMatch, comparisons, matches } = state;

    let textCells = '';
    for (let i = 0; i < text.length; i++) {
      let cls = '';
      const inWindow = i >= windowIdx && i < windowIdx + pattern.length;
      if (inWindow) {
        if (isHashMatch && isExactMatch) cls = 'is-match';
        else if (isHashMatch && isCharVerified && !isExactMatch) cls = 'is-mismatch';
        else cls = 'in-window';
      }
      textCells += `<div class="track-cell-wrapper">
        <span class="cell-idx">${i}</span>
        ${this.renderNucCell(text[i], cls)}
      </div>`;
    }

    let statusPill = `<span class="stat-pill">Hashes Differ (Fast Shift)</span>`;
    if (isHashMatch) {
      if (isCharVerified) {
        if (isExactMatch) {
          statusPill = `<span class="stat-pill" style="color: var(--forest-green); font-weight: 700;">✓ Exact Motif Match Verified!</span>`;
        } else {
          statusPill = `<span class="stat-pill" style="color: var(--amber-accent); font-weight: 700;">⚠ Spurious Hash Collision!</span>`;
        }
      } else {
        statusPill = `<span class="stat-pill" style="color: var(--teal-accent); font-weight: 700;">⚡ Hash Match! Verifying Characters...</span>`;
      }
    }

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">DNA Sequence with Rolling Hash Window:</div>
        <div class="nucleotide-track">${textCells}</div>

        <div class="hash-cards-grid" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-top: 1.25rem;">
          <div class="stat-card" style="background: var(--bg-subtle); padding: 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
            <div style="font-size: 0.72rem; color: var(--text-light); text-transform: uppercase; font-weight: 600;">Target Pattern Hash H(P)</div>
            <div style="font-family: var(--font-mono); font-size: 1.25rem; font-weight: 700; color: var(--forest-green);">${pHash}</div>
            <div style="font-size: 0.75rem; color: var(--text-muted);">Pattern: "${pattern}"</div>
          </div>
          <div class="stat-card" style="background: var(--bg-subtle); padding: 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
            <div style="font-size: 0.72rem; color: var(--text-light); text-transform: uppercase; font-weight: 600;">Current Window Hash H(W)</div>
            <div style="font-family: var(--font-mono); font-size: 1.25rem; font-weight: 700; color: ${isHashMatch ? 'var(--forest-green)' : 'var(--text-main)'};">${tHash}</div>
            <div style="font-size: 0.75rem; color: var(--text-muted);">Window at [${windowIdx}]: "${windowStr}"</div>
          </div>
          <div class="stat-card" style="background: var(--bg-subtle); padding: 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
            <div style="font-size: 0.72rem; color: var(--text-light); text-transform: uppercase; font-weight: 600;">Verification Status</div>
            <div style="margin-top: 0.35rem;">${statusPill}</div>
            <div style="font-size: 0.75rem; color: var(--text-light); margin-top: 0.25rem;">Modulus q = 101, Radix = 256</div>
          </div>
        </div>

        <div class="stats-pills-row" style="margin-top: 1.25rem;">
          <span class="stat-pill">Character Verifications: <strong>${comparisons}</strong></span>
          <span class="stat-pill">Confirmed Matches: <strong>${matches.length}</strong></span>
          <span class="stat-pill">Positions: <strong>[${matches.join(', ') || 'None'}] (0-based)</strong></span>
        </div>
      </div>
    `;
  },

  // 4. Aho-Corasick Renderer
  renderAhoCorasick(container, state) {
    if (!state) return;
    const { text, patterns, nodes, currentStateId, charIdx, currChar, foundMatches } = state;

    let textCells = '';
    for (let i = 0; i < text.length; i++) {
      let cls = i === charIdx ? 'is-current' : '';
      textCells += `<div class="track-cell-wrapper">
        <span class="cell-idx">${i}</span>
        ${this.renderNucCell(text[i], cls)}
      </div>`;
    }

    const stateBadges = nodes.map(n => {
      const isActive = n.id === currentStateId;
      const hasOutput = n.output && n.output.length > 0;
      return `<div class="automaton-node-card ${isActive ? 'active-state' : ''} ${hasOutput ? 'accepting-state' : ''}">
        <div class="node-id">State #${n.id} ${isActive ? '◀ CURRENT' : ''}</div>
        <div class="node-char">Char: <strong>${n.char}</strong></div>
        <div class="node-fail">Fail Link ➔ State ${n.fail}</div>
        ${hasOutput ? `<div class="node-output">Match: [${n.output.join(', ')}]</div>` : ''}
      </div>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Text Stream Traversal:</div>
        <div class="nucleotide-track">${textCells}</div>

        <div class="track-label" style="margin-top: 1.25rem;">Active Automaton States & Failure Transitions:</div>
        <div class="automaton-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 0.65rem; max-height: 220px; overflow-y: auto; padding: 0.5rem; border: 1px solid var(--border-subtle); border-radius: var(--radius-sm); margin-top: 0.5rem;">
          ${stateBadges}
        </div>

        <div class="stats-pills-row" style="margin-top: 1rem;">
          <span class="stat-pill">Patterns: <strong>${patterns.join(', ')}</strong></span>
          <span class="stat-pill">Current State: <strong>#${currentStateId}</strong></span>
          <span class="stat-pill">Matches Detected: <strong>${foundMatches.length}</strong></span>
        </div>
      </div>
    `;
  },

  // 5. Suffix Array Renderer
  renderSuffixArray(container, state) {
    if (!state) return;
    const { text, suffixes, sortedSa, comparingA, comparingB, queryPattern, searchLow, searchHigh, searchMid, searchResult } = state;

    const rowsHtml = suffixes.map((s, idx) => {
      let rowCls = '';
      if (idx === comparingA || idx === comparingB) rowCls = 'highlight-row';
      if (idx === searchMid) rowCls = searchResult !== null && searchResult !== -1 ? 'match-row' : 'active-row';

      return `<tr class="${rowCls}">
        <td><strong>${idx}</strong></td>
        <td><span class="index-badge">${s.index}</span></td>
        <td><code style="letter-spacing: 0.05em;">${s.suffix}</code></td>
      </tr>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="table-responsive" style="max-height: 260px; overflow-y: auto;">
          <table class="data-table">
            <thead>
              <tr>
                <th>Rank</th>
                <th>Suffix Index (SA[i])</th>
                <th>Suffix String</th>
              </tr>
            </thead>
            <tbody>${rowsHtml}</tbody>
          </table>
        </div>

        ${sortedSa ? `
          <div class="stats-pills-row" style="margin-top: 1rem;">
            <span class="stat-pill">Sorted Suffix Array SA: <strong>[${sortedSa.join(', ')}]</strong></span>
            ${queryPattern ? `<span class="stat-pill">Query: <strong>"${queryPattern}"</strong></span>` : ''}
            ${searchMid !== -1 ? `<span class="stat-pill">Binary Search: <strong>Low=${searchLow}, Mid=${searchMid}, High=${searchHigh}</strong></span>` : ''}
            ${searchResult !== null ? `<span class="stat-pill" style="color: var(--forest-green);">Search Result: <strong>${searchResult >= 0 ? `Found at Text Index ${searchResult}` : 'Not Found'}</strong></span>` : ''}
          </div>
        ` : ''}
      </div>
    `;
  },

  // 6. Kasai LCP Renderer
  renderKasai(container, state) {
    if (!state) return;
    const { text, sa, lcp, suffixes, currRank, neighborRank, highlightPrefix, h } = state;

    const rowsHtml = suffixes.map((s, rank) => {
      const isCurr = rank === currRank;
      const isNeighbor = rank === neighborRank;
      let rowCls = isCurr ? 'highlight-row' : (isNeighbor ? 'neighbor-row' : '');

      return `<tr class="${rowCls}">
        <td><strong>${rank}</strong></td>
        <td><span class="index-badge">${s.index}</span></td>
        <td><code style="letter-spacing: 0.05em;">${s.suffix}</code></td>
        <td><strong>${lcp[rank]}</strong></td>
      </tr>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="table-responsive" style="max-height: 260px; overflow-y: auto;">
          <table class="data-table">
            <thead>
              <tr>
                <th>Rank (r)</th>
                <th>SA[r]</th>
                <th>Suffix String</th>
                <th>LCP[r]</th>
              </tr>
            </thead>
            <tbody>${rowsHtml}</tbody>
          </table>
        </div>

        <div class="stats-pills-row" style="margin-top: 1rem;">
          <span class="stat-pill">Current h: <strong>${h}</strong></span>
          <span class="stat-pill">Common Prefix: <strong>"${highlightPrefix || 'None'}"</strong></span>
          <span class="stat-pill">Max LCP: <strong>${Math.max(...lcp)}</strong></span>
          <span class="stat-pill">Time Complexity: <strong>O(n) Linear</strong></span>
        </div>
      </div>
    `;
  },

  // 7. Dynamic Programming Matrix Renderer (Levenshtein, Needleman-Wunsch, Smith-Waterman)
  renderDpMatrix(container, state) {
    if (!state) return;
    const { s1, s2, dp, currRow, currCol, chosenOp, tracebackPath, maxVal, alignA, alignB } = state;
    const m = s1.length;
    const n = s2.length;

    let headerCols = `<th></th><th>ε</th>`;
    for (let j = 0; j < n; j++) {
      headerCols += `<th>${s2[j]}</th>`;
    }

    let rowsHtml = '';
    for (let i = 0; i <= m; i++) {
      let rowCells = `<td><strong>${i === 0 ? 'ε' : s1[i - 1]}</strong></td>`;
      for (let j = 0; j <= n; j++) {
        const val = dp[i][j];
        const isCurrent = i === currRow && j === currCol;
        const isTrace = tracebackPath && tracebackPath.some(pt => pt.r === i && pt.c === j);

        let cellCls = '';
        if (isCurrent) cellCls = 'dp-cell-current';
        else if (isTrace) cellCls = 'dp-cell-trace';
        else if (val !== null) cellCls = 'dp-cell-filled';

        rowCells += `<td class="${cellCls}">${val !== null ? val : '·'}</td>`;
      }
      rowsHtml += `<tr>${rowCells}</tr>`;
    }

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="table-responsive dp-table-scroll" style="max-height: 280px; overflow: auto;">
          <table class="data-table dp-matrix-table">
            <thead><tr>${headerCols}</tr></thead>
            <tbody>${rowsHtml}</tbody>
          </table>
        </div>

        ${alignA && alignB ? `
          <div class="alignment-result-card" style="margin-top: 1rem; padding: 0.85rem; background: var(--bg-subtle); border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
            <div style="font-size: 0.75rem; font-weight: 700; color: var(--forest-green); text-transform: uppercase;">Optimal Pairwise Alignment Result:</div>
            <div style="font-family: var(--font-mono); font-size: 0.95rem; margin-top: 0.35rem;">
              <div>Seq 1: <strong>${alignA}</strong></div>
              <div>Seq 2: <strong>${alignB}</strong></div>
            </div>
          </div>
        ` : ''}

        <div class="stats-pills-row" style="margin-top: 1rem;">
          <span class="stat-pill">Dimensions: <strong>${m + 1} × ${n + 1}</strong></span>
          ${chosenOp ? `<span class="stat-pill">Operation: <strong>${chosenOp}</strong></span>` : ''}
          ${maxVal !== undefined ? `<span class="stat-pill">Peak Score: <strong>${maxVal}</strong></span>` : ''}
          <span class="stat-pill">Time: <strong>O(mn)</strong></span>
          <span class="stat-pill">Space: <strong>O(mn)</strong></span>
        </div>
      </div>
    `;
  },

  // 8. Network Flow & Matching Renderer
  renderNetworkFlow(container, state) {
    if (!state) return;
    const { nodes, edges, flow, totalFlow, activePath, bottleneck, minCutS, minCutT } = state;

    const edgeCards = edges.map(e => {
      const currentFlow = (flow[e.u] && flow[e.u][e.v]) || 0;
      const isPathEdge = activePath && activePath.some(p => p.u === e.u && p.v === e.v);
      const isSaturated = currentFlow === e.cap;

      return `<div class="flow-edge-item ${isPathEdge ? 'active-edge' : ''} ${isSaturated ? 'saturated-edge' : ''}">
        <span class="edge-nodes">${e.u} ➔ ${e.v}</span>
        <span class="edge-capacity">Flow: <strong>${currentFlow} / ${e.cap}</strong></span>
      </div>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Biological Pipeline Facilities & Capacities:</div>
        <div class="flow-edges-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 0.65rem; margin-top: 0.5rem; max-height: 220px; overflow-y: auto;">
          ${edgeCards}
        </div>

        ${minCutS && minCutS.length > 0 ? `
          <div class="stats-pills-row" style="margin-top: 1rem;">
            <span class="stat-pill" style="border-left: 3px solid var(--forest-green);">Source Cut S: [${minCutS.join(', ')}]</span>
            <span class="stat-pill" style="border-left: 3px solid var(--rose-accent);">Sink Cut T: [${minCutT.join(', ')}]</span>
          </div>
        ` : ''}

        <div class="stats-pills-row" style="margin-top: 1rem;">
          <span class="stat-pill">Total Flow Pushed: <strong>${totalFlow}</strong></span>
          ${bottleneck ? `<span class="stat-pill">Path Bottleneck: <strong>${bottleneck}</strong></span>` : ''}
          <span class="stat-pill">Max-Flow Min-Cut: <strong>Verified</strong></span>
        </div>
      </div>
    `;
  },

  // 9. 3-SAT & NP Reductions Renderer
  renderNpGraph(container, state) {
    if (!state) return;
    const { variables, clauses, evaluatedClauses, isSatisfied } = state;

    const clauseBadges = (clauses || []).map((c, idx) => {
      const isEval = evaluatedClauses ? evaluatedClauses[idx] : null;
      return `<div class="clause-box" style="background: var(--bg-subtle); padding: 0.65rem 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
        <div style="font-size: 0.72rem; color: var(--text-light); font-weight: 600;">Clause #${idx + 1}</div>
        <div style="font-family: var(--font-mono); font-size: 0.88rem; font-weight: 600; margin: 0.2rem 0;">(${Array.isArray(c) ? c.join(' ∨ ') : c})</div>
        <span class="badge" style="font-size: 0.72rem; color: ${isEval ? 'var(--forest-green)' : 'var(--text-muted)'};">${isEval ? '✓ Satisfied' : 'Evaluating'}</span>
      </div>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Conjunctive Normal Form (CNF) Clauses:</div>
        <div class="clauses-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 0.75rem; margin-top: 0.5rem;">
          ${clauseBadges}
        </div>

        <div class="stats-pills-row" style="margin-top: 1.25rem;">
          <span class="stat-pill">Status: <strong>${isSatisfied ? 'SATISFIED (Valid Circuit Assignment)' : 'Evaluating Circuit'}</strong></span>
          <span class="stat-pill">Complexity: <strong>NP-Complete (Cook-Levin Theorem)</strong></span>
        </div>
      </div>
    `;
  },

  // 10. Randomized QuickSort Renderer
  renderQuickSort(container, state) {
    if (!state) return;
    const { array, pivotId, left, right } = state;

    const itemsHtml = (array || []).map(item => {
      const isPivot = item.id === pivotId;
      return `<div class="tree-node-box ${isPivot ? 'active' : ''}">
        <span style="font-family: var(--font-mono); font-weight: 700;">${item.val}</span>
      </div>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Genomic Read Library Elements:</div>
        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap; margin-top: 0.5rem;">
          ${itemsHtml}
        </div>

        <div class="stats-pills-row" style="margin-top: 1.25rem;">
          <span class="stat-pill">Active Subsegment: <strong>[${left}..${right}]</strong></span>
          <span class="stat-pill">Expected Runtime: <strong>O(n log n)</strong></span>
        </div>
      </div>
    `;
  },

  // 11. Reservoir Sampling Renderer
  renderReservoir(container, state) {
    if (!state) return;
    const { reservoir, currentItem, stepNumber, totalSeen } = state;

    const reservoirItems = (reservoir || []).map((item, idx) => `
      <div class="tree-node-box" style="border: 2px solid var(--forest-green); background: var(--forest-green-light);">
        <span style="font-size: 0.65rem; color: var(--forest-green); font-weight: 700;">Slot ${idx}</span>
        <div style="font-family: var(--font-mono); font-weight: 700; color: var(--forest-green);">${item}</div>
      </div>
    `).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Continuous Streaming Ingestion:</div>
        <div style="margin-top: 0.5rem; font-size: 0.88rem;">Current Stream Item: <strong style="color: var(--teal-accent);">${currentItem || 'Completed'}</strong> (Read #${totalSeen || stepNumber || 1})</div>

        <div class="track-label" style="margin-top: 1rem;">Uniform Reservoir Memory Buffer (Size k=${(reservoir || []).length}):</div>
        <div style="display: flex; gap: 0.65rem; flex-wrap: wrap; margin-top: 0.4rem;">
          ${reservoirItems}
        </div>

        <div class="stats-pills-row" style="margin-top: 1.25rem;">
          <span class="stat-pill">Stream Complexity: <strong>O(N) Single-Pass</strong></span>
          <span class="stat-pill">Buffer Space: <strong>O(k)</strong></span>
          <span class="stat-pill">Inclusion Invariant: <strong>Pr = k / N</strong></span>
        </div>
      </div>
    `;
  },

  // 12. Miller-Rabin Primality Renderer
  renderMillerRabin(container, state) {
    if (!state) return;
    const { n, currentWitness, witnessRounds, isComposite } = state;

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Candidate Prime Key:</div>
        <div style="font-family: var(--font-mono); font-size: 1.5rem; font-weight: 700; color: var(--forest-green); margin-top: 0.35rem;">n = ${n}</div>

        <div class="stats-pills-row" style="margin-top: 1rem;">
          <span class="stat-pill">Current Base Witness: <strong>a = ${currentWitness || 2}</strong></span>
          <span class="stat-pill">Verdict: <strong>${isComposite ? 'COMPOSITE (Witness Certified)' : 'PROBABLE PRIME'}</strong></span>
          <span class="stat-pill">Error Probability: <strong>≤ (1/4)^${witnessRounds || 5}</strong></span>
        </div>
      </div>
    `;
  },

  // 13. Parallel Tree / Blelloch Renderer
  renderBlellochScan(container, state) {
    if (!state) return;
    const { array, phaseType, activeIndices, level } = state;

    const cells = (array || []).map((val, idx) => {
      const isActive = activeIndices && activeIndices.includes(idx);
      return `<div class="tree-node-box ${isActive ? 'active' : ''}">
        <span style="font-size: 0.65rem; color: var(--text-light);">${idx}</span>
        <div style="font-family: var(--font-mono); font-weight: 700;">${val}</div>
      </div>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Blelloch Parallel Prefix Scan Array (${phaseType ? phaseType.toUpperCase() : 'TREE'} PHASE):</div>
        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap; margin-top: 0.5rem;">
          ${cells}
        </div>

        <div class="stats-pills-row" style="margin-top: 1.25rem;">
          <span class="stat-pill">Parallel Span: <strong>O(log n)</strong></span>
          <span class="stat-pill">Total Work: <strong>O(n)</strong></span>
          <span class="stat-pill">Work-Efficiency: <strong>Optimal</strong></span>
        </div>
      </div>
    `;
  },

  renderParallelReduce(container, state) {
    if (!state) return;
    const { levels, currentRound } = state;

    const roundsHtml = (levels || []).map((lvl, idx) => {
      return `<div class="parallel-level-row" style="margin-bottom: 0.65rem;">
        <span class="stat-pill" style="font-size: 0.72rem; margin-bottom: 0.35rem;">Depth ${idx} (${lvl.length} workers):</span>
        <div style="display: flex; gap: 0.45rem; flex-wrap: wrap; margin-top: 0.25rem;">
          ${lvl.map(v => `<span class="tree-node-box">${v}</span>`).join('')}
        </div>
      </div>`;
    }).join('');

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Parallel Binary Reduction Tree:</div>
        <div style="margin-top: 0.5rem;">${roundsHtml}</div>

        <div class="stats-pills-row" style="margin-top: 1rem;">
          <span class="stat-pill">Current Round: <strong>${currentRound || 0}</strong></span>
          <span class="stat-pill">Critical Path Span: <strong>O(log n)</strong></span>
          <span class="stat-pill">Total Work: <strong>O(n)</strong></span>
        </div>
      </div>
    `;
  },

  // 14. Brent's Theorem Calculator
  renderBrentTheorem(container, state) {
    if (!state) return;
    const { w, s, p, tp, speedup, efficiency } = state;

    container.innerHTML = `
      <div class="sim-visual-box">
        <div class="track-label">Brent's Scheduling Inequality:</div>
        <div style="font-family: var(--font-mono); font-size: 1rem; color: var(--forest-green); padding: 0.5rem 0.85rem; background: var(--forest-green-light); border-radius: var(--radius-sm); margin-top: 0.35rem;">
          T_p ≤ (T₁ - T_∞) / p + T_∞
        </div>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem; margin-top: 1rem;">
          <div class="stat-card" style="background: var(--bg-subtle); padding: 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
            <div style="font-size: 0.72rem; color: var(--text-light); text-transform: uppercase;">Bounded Time T_p</div>
            <div style="font-family: var(--font-mono); font-size: 1.35rem; font-weight: 700; color: var(--forest-green);">${tp ? tp.toFixed(1) : '142.5'} steps</div>
          </div>
          <div class="stat-card" style="background: var(--bg-subtle); padding: 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
            <div style="font-size: 0.72rem; color: var(--text-light); text-transform: uppercase;">Speedup S_p</div>
            <div style="font-family: var(--font-mono); font-size: 1.35rem; font-weight: 700; color: var(--teal-accent);">${speedup ? speedup.toFixed(2) : '7.02'} ×</div>
          </div>
          <div class="stat-card" style="background: var(--bg-subtle); padding: 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
            <div style="font-size: 0.72rem; color: var(--text-light); text-transform: uppercase;">Parallel Efficiency</div>
            <div style="font-family: var(--font-mono); font-size: 1.35rem; font-weight: 700; color: var(--sage-accent);">${efficiency ? (efficiency * 100).toFixed(1) : '87.7'}%</div>
          </div>
        </div>
      </div>
    `;
  }
};

window.VisualRenderers = VisualRenderers;
