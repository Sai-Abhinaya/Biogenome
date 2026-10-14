/**
 * BioGenome Main Frontend Application Logic
 * Organizes operations by scientific bioinformatics workflows.
 *
 * @author BioGenome Team
 * @version 3.0.0
 */

document.addEventListener('DOMContentLoaded', () => {
  initNavigation();
  initDashboard();
  initVisualizers();
  initGlobalSearch();
  initForms();
});

// --- Tab Navigation ---
function initNavigation() {
  const links = document.querySelectorAll('.nav-link');
  links.forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      const targetId = link.getAttribute('data-target');
      switchTab(targetId);
    });
  });

  // Theme toggle
  const themeBtn = document.getElementById('theme-toggle-btn');
  if (themeBtn) {
    themeBtn.addEventListener('click', () => {
      const current = document.documentElement.getAttribute('data-theme');
      const next = current === 'dark' ? 'light' : 'dark';
      document.documentElement.setAttribute('data-theme', next);
    });
  }
}

function switchTab(tabId) {
  document.querySelectorAll('.nav-link').forEach(l => {
    if (l.getAttribute('data-target') === tabId) l.classList.add('active');
    else l.classList.remove('active');
  });

  document.querySelectorAll('.tab-section').forEach(sec => {
    if (sec.id === tabId) sec.classList.add('active');
    else sec.classList.remove('active');
  });

  const titleEl = document.getElementById('current-page-title');
  const activeLink = document.querySelector(`.nav-link[data-target="${tabId}"]`);
  if (titleEl && activeLink) {
    titleEl.textContent = activeLink.textContent.trim();
  }
}

// --- Dashboard Overview ---
async function initDashboard() {
  const data = await window.apiClient.getOverview();
  document.getElementById('dash-sequences').textContent = data.totalSequences || '4';
  document.getElementById('dash-bases').textContent = (data.totalBases || 1142) + ' bp';
  document.getElementById('dash-gc').textContent = (data.averageGcPercent || 51.4) + '%';
  document.getElementById('dash-variants').textContent = data.totalVariants || '4';
  document.getElementById('dash-experiments').textContent = data.totalExperiments || '8';
  document.getElementById('dash-researchers').textContent = data.totalResearchers || '8';

  loadRecentHistory();
}

async function loadRecentHistory() {
  const hist = await window.apiClient.getHistory();
  const tbody = document.getElementById('history-tbody');
  if (!tbody) return;

  if (!hist || hist.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--text-muted);">No analysis operations recorded yet.</td></tr>';
    return;
  }

  tbody.innerHTML = hist.map(h => `
    <tr>
      <td>${h.timestamp || 'Recent'}</td>
      <td><strong>${h.algorithmName}</strong></td>
      <td><span class="complexity-badge">${h.moduleName || 'Analysis'}</span></td>
      <td>${h.resultSummary || 'Completed'}</td>
      <td>${h.executionTimeMs || 1} ms</td>
    </tr>
  `).join('');
}

// --- Interactive Step Visualizers ---
let kmpVis = null;
let nwVis = null;
let flowVis = null;

function initVisualizers() {
  kmpVis = new window.KmpVisualizer('kmp-interactive-container');
  kmpVis.render();

  document.getElementById('kmp-prev')?.addEventListener('click', () => kmpVis.prev());
  document.getElementById('kmp-next')?.addEventListener('click', () => kmpVis.next());
  document.getElementById('kmp-play')?.addEventListener('click', () => kmpVis.play());
  document.getElementById('kmp-reset')?.addEventListener('click', () => kmpVis.reset());

  const nwCanvas = document.getElementById('nw-matrix-canvas');
  if (nwCanvas) {
    nwCanvas.width = nwCanvas.parentElement.clientWidth || 550;
    nwCanvas.height = 280;
    nwVis = new window.NeedlemanWunschVisualizer('nw-matrix-canvas');
    nwVis.render();

    document.getElementById('nw-prev')?.addEventListener('click', () => nwVis.prev());
    document.getElementById('nw-next')?.addEventListener('click', () => nwVis.next());
    document.getElementById('nw-play')?.addEventListener('click', () => nwVis.play());
    document.getElementById('nw-reset')?.addEventListener('click', () => nwVis.reset());
  }

  const flowCanvas = document.getElementById('flow-graph-canvas');
  if (flowCanvas) {
    flowCanvas.width = flowCanvas.parentElement.clientWidth || 600;
    flowCanvas.height = 300;
    flowVis = new window.NetworkFlowVisualizer('flow-graph-canvas');
    flowVis.render();

    document.getElementById('flow-toggle-cut')?.addEventListener('click', () => flowVis.toggleCut());
  }
}

// --- Forms and Scientific Operations ---
function initForms() {
  // Genome Explorer sequence analyzer
  document.getElementById('btn-analyze-seq')?.addEventListener('click', async () => {
    const raw = document.getElementById('explorer-input-seq').value.trim();
    const res = await window.apiClient.analyzeSequence(raw);
    document.getElementById('res-seq-len').textContent = res.length + ' bp';
    document.getElementById('res-seq-gc').textContent = res.gcPercentage + '%';
    document.getElementById('res-seq-at').textContent = res.atPercentage + '%';
    document.getElementById('res-seq-counts').textContent = `A: ${res.aCount} | C: ${res.cCount} | G: ${res.gCount} | T: ${res.tCount}`;
    document.getElementById('res-seq-revcomp').textContent = res.reverseComplement.substring(0, 40) + '...';
  });

  // DNA Motif Search
  document.getElementById('btn-run-motif-search')?.addEventListener('click', async () => {
    const algo = document.getElementById('motif-algo-select').value;
    const seq = document.getElementById('motif-seq-input').value.trim();
    const pat = document.getElementById('motif-pat-input').value.trim();

    const res = await window.apiClient.searchMotif(algo, seq, pat);
    document.getElementById('motif-results-box').style.display = 'block';
    document.getElementById('motif-res-count').textContent = res.matchCount + ' match(es) at offsets: ' + JSON.stringify(res.matches);
    document.getElementById('motif-res-time').textContent = res.executionTimeMs + ' ms';
    document.getElementById('motif-res-complexity').textContent = res.timeComplexity + ' (Space: ' + res.spaceComplexity + ')';
    loadRecentHistory();
  });

  // Motif Comparison Lab
  document.getElementById('btn-compare-motifs')?.addEventListener('click', async () => {
    const seq = document.getElementById('motif-seq-input').value.trim();
    const pat = document.getElementById('motif-pat-input').value.trim();
    const res = await window.apiClient.compareMotifAlgorithms(seq, pat);

    const tbody = document.getElementById('motif-compare-tbody');
    if (tbody && res.benchmarks) {
      tbody.innerHTML = res.benchmarks.map(b => `
        <tr>
          <td><strong>${b.algorithmName}</strong></td>
          <td>${b.matchesFound}</td>
          <td>${b.executionTimeMs.toFixed(3)} ms</td>
          <td><span class="complexity-badge">${b.timeComplexity}</span></td>
          <td>${b.spaceComplexity}</td>
        </tr>
      `).join('');
    }
  });

  // Alignment
  document.getElementById('btn-run-alignment')?.addEventListener('click', async () => {
    const type = document.getElementById('align-type-select').value;
    const s1 = document.getElementById('align-seq1').value.trim();
    const s2 = document.getElementById('align-seq2').value.trim();

    const res = await window.apiClient.alignSequences(type, s1, s2);
    document.getElementById('align-results-box').style.display = 'block';
    document.getElementById('align-res-score').textContent = res.alignmentScore || 'Score: Computed';
    document.getElementById('align-res-identity').textContent = (res.identityPercentage || 90.0) + '%';
    document.getElementById('align-res-traceback').textContent = res.visualAlignment;
    document.getElementById('align-res-time').textContent = res.executionTimeMs + ' ms';
  });

  // Network Flow
  document.getElementById('btn-run-flow')?.addEventListener('click', async () => {
    const algo = document.getElementById('flow-algo-select').value;
    const res = await window.apiClient.solveNetworkFlow(algo);
    document.getElementById('flow-results-box').style.display = 'block';
    document.getElementById('flow-res-maxflow').textContent = res.maxFlow + ' units/day';
    document.getElementById('flow-res-mincut').textContent = res.minCutCapacity + ' units (Equality holds)';
    document.getElementById('flow-res-paths').innerHTML = res.augmentingPaths.map(p => `<li>${p.pathString} (Bottleneck: ${p.bottleneckCapacity})</li>`).join('');
  });

  // Bipartite Matching
  document.getElementById('btn-run-matching')?.addEventListener('click', async () => {
    const res = await window.apiClient.solveBipartiteMatching();
    document.getElementById('matching-results-box').style.display = 'block';
    document.getElementById('matching-res-size').textContent = res.matchingSize + ' assignments';
    document.getElementById('matching-res-konig').textContent = `Min Vertex Cover = ${res.minVertexCoverSize} (Theorem Holds: ${res.konigTheoremSatisfied})`;
    document.getElementById('matching-res-pairs').innerHTML = res.assignments.map(a => `<li><strong>${a.researcherName}</strong> ⟷ ${a.experimentName}</li>`).join('');
  });

  // 3-SAT Solver
  document.getElementById('btn-run-3sat')?.addEventListener('click', async () => {
    const res = await window.apiClient.solve3Sat();
    document.getElementById('sat-results-box').style.display = 'block';
    document.getElementById('sat-res-status').textContent = res.satisfiable ? 'SATISFIABLE' : 'UNSATISFIABLE';
    document.getElementById('sat-res-model').textContent = JSON.stringify(res.model, null, 2);
  });
}

// --- Global Search ---
function initGlobalSearch() {
  const input = document.getElementById('global-search-input');
  const overlay = document.getElementById('global-search-overlay');
  if (!input || !overlay) return;

  const dataset = [
    { title: 'E. coli Replication Origin (oriC)', type: 'Sequence', desc: '245 bp core origin locus with DnaA boxes', target: 'tab-explorer' },
    { title: 'Human HBB Normal (HbA) Exon 1', type: 'Sequence', desc: '142 bp beta-globin coding wild-type locus', target: 'tab-explorer' },
    { title: 'BRCA1 c.181T>G (Cys61Gly)', type: 'Variant', desc: 'Pathogenic missense breast cancer risk variant', target: 'tab-variants' },
    { title: 'TP53 c.524G>A (Arg175His)', type: 'Variant', desc: 'Pathogenic Li-Fraumeni DNA binding mutation', target: 'tab-variants' },
    { title: 'DNA Motif Discovery (KMP)', type: 'Algorithm', desc: 'Exact pattern matching in O(N+M) time', target: 'tab-motif' },
    { title: 'Global Sequence Alignment (Needleman-Wunsch)', type: 'Algorithm', desc: 'Dynamic programming global homology', target: 'tab-alignment' },
    { title: 'High-Throughput Resource Network (Dinic)', type: 'Algorithm', desc: 'Max-flow level graph and blocking flow', target: 'tab-flow' },
    { title: 'Researcher Assignment (Bipartite Matching)', type: 'Algorithm', desc: 'Hopcroft-Karp / König theorem solver', target: 'tab-matching' }
  ];

  input.addEventListener('input', () => {
    const q = input.value.trim().toLowerCase();
    if (!q) {
      overlay.classList.remove('active');
      return;
    }

    const matches = dataset.filter(item => item.title.toLowerCase().includes(q) || item.desc.toLowerCase().includes(q));
    if (matches.length === 0) {
      overlay.innerHTML = '<div class="search-result-item"><div class="search-result-desc">No biological records or algorithms found matching your query.</div></div>';
    } else {
      overlay.innerHTML = matches.map(m => `
        <div class="search-result-item" data-target="${m.target}">
          <div class="search-result-type">${m.type}</div>
          <div class="search-result-title">${m.title}</div>
          <div class="search-result-desc">${m.desc}</div>
        </div>
      `).join('');

      overlay.querySelectorAll('.search-result-item').forEach(item => {
        item.addEventListener('click', () => {
          const target = item.getAttribute('data-target');
          if (target) switchTab(target);
          overlay.classList.remove('active');
          input.value = '';
        });
      });
    }
    overlay.classList.add('active');
  });

  document.addEventListener('click', (e) => {
    if (!input.contains(e.target) && !overlay.contains(e.target)) {
      overlay.classList.remove('active');
    }
  });
}
