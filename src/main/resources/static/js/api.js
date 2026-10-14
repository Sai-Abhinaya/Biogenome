/**
 * BioGenome API Client
 * Connects to the Spring Boot REST Backend (http://localhost:8080/api/v1).
 * If the Java backend is offline, gracefully provides simulated educational genomics data
 * so the frontend is 100% functional and testable in VS Code standalone.
 *
 * @author BioGenome Team
 * @version 3.0.0
 */

const API_BASE_URL = 'http://localhost:8080/api/v1';

class BioGenomeApiClient {
  constructor() {
    this.isBackendOnline = false;
    this.checkHealth();
  }

  async checkHealth() {
    try {
      const res = await fetch(`${API_BASE_URL}/genome/overview`, { method: 'GET', signal: AbortSignal.timeout(1500) });
      if (res.ok) {
        this.isBackendOnline = true;
        this.updateOnlineBadge(true);
      } else {
        this.updateOnlineBadge(false);
      }
    } catch (e) {
      this.isBackendOnline = false;
      this.updateOnlineBadge(false);
    }
  }

  updateOnlineBadge(online) {
    const el = document.getElementById('backend-status');
    if (!el) return;
    if (online) {
      el.innerHTML = '<span class="status-dot" style="background-color: var(--green-accent)"></span> Backend Live (Port 8080)';
    } else {
      el.innerHTML = '<span class="status-dot" style="background-color: var(--amber-accent)"></span> Standalone Demo (Offline)';
    }
  }

  // --- 1. Genome Explorer ---
  async getOverview() {
    try {
      const res = await fetch(`${API_BASE_URL}/genome/overview`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    // Educational Fallback
    return {
      totalSequences: 4,
      totalBases: 1142,
      averageGcPercent: 51.4,
      totalVariants: 4,
      substitutions: 2,
      deletions: 1,
      insertions: 1,
      totalResearchers: 8,
      totalExperiments: 8,
      totalResources: 8,
      activeObservations: 12
    };
  }

  async getSequences() {
    try {
      const res = await fetch(`${API_BASE_URL}/genome/sequences`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return [
      {
        id: 'ECOLI_ORIC',
        description: 'E. coli K-12 Replication Origin (oriC core)',
        length: 245,
        gcContent: 51.43,
        sequence: 'CGTTTTTTCATTGACACCACCCACACCACACCACCGGTTTCACCGGCACCAC'
      },
      {
        id: 'HUMAN_HBB_WT',
        description: 'Human Beta-Globin (HBB) Normal Adult Allele (HbA)',
        length: 142,
        gcContent: 52.11,
        sequence: 'ATGGTGCACCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAG'
      },
      {
        id: 'HUMAN_HBB_SICKLE',
        description: 'Human Beta-Globin Sickle Cell Allele (HbS, Glu6Val)',
        length: 142,
        gcContent: 52.11,
        sequence: 'ATGGTGCACCTGACTCCTGTGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAG'
      },
      {
        id: 'SARS2_RBD_WT',
        description: 'SARS-CoV-2 Spike Receptor-Binding Domain (RBD)',
        length: 210,
        gcContent: 47.62,
        sequence: 'AATTGTTACTTTCCTTTACAATCATATGGTTTCCAACCCACTTATGGTGTTGGTT'
      }
    ];
  }

  async analyzeSequence(sequence) {
    try {
      const res = await fetch(`${API_BASE_URL}/genome/analyze`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sequence })
      });
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    // Pure clientside calculation fallback
    const upper = (sequence || '').toUpperCase().replace(/[^ACGTN]/g, '');
    let a = 0, c = 0, g = 0, t = 0;
    for (const ch of upper) {
      if (ch === 'A') a++;
      else if (ch === 'C') c++;
      else if (ch === 'G') g++;
      else if (ch === 'T') t++;
    }
    const len = upper.length || 1;
    return {
      sequence: upper,
      length: upper.length,
      aCount: a,
      cCount: c,
      gCount: g,
      tCount: t,
      gcPercentage: ((c + g) * 100 / len).toFixed(2),
      atPercentage: ((a + t) * 100 / len).toFixed(2),
      reverseComplement: upper.split('').reverse().map(b => b === 'A' ? 'T' : b === 'T' ? 'A' : b === 'C' ? 'G' : b === 'G' ? 'C' : 'N').join('')
    };
  }

  // --- 2. DNA Motif Discovery (Module 1) ---
  async searchMotif(algorithm, sequence, pattern) {
    try {
      let endpoint = '/search/kmp';
      if (algorithm === 'Z-Algorithm') endpoint = '/search/z';
      else if (algorithm === 'Rabin-Karp') endpoint = '/search/rabin-karp';

      const res = await fetch(`${API_BASE_URL}${endpoint}?sequence=${encodeURIComponent(sequence)}&pattern=${encodeURIComponent(pattern)}`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    // Simulated fallback
    const matches = [];
    let idx = sequence.indexOf(pattern);
    while (idx !== -1) {
      matches.push(idx);
      idx = sequence.indexOf(pattern, idx + 1);
    }
    return {
      algorithm,
      pattern,
      sequenceLength: sequence.length,
      matches,
      matchCount: matches.length,
      executionTimeMs: 0.12,
      timeComplexity: algorithm === 'Rabin-Karp' ? 'Average O(N + M)' : 'O(N + M)',
      spaceComplexity: algorithm === 'KMP' ? 'O(M)' : 'O(N + M)'
    };
  }

  async compareMotifAlgorithms(sequence, pattern) {
    try {
      const res = await fetch(`${API_BASE_URL}/search/compare?sequence=${encodeURIComponent(sequence)}&pattern=${encodeURIComponent(pattern)}`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    // Fallback simulation
    const matches = [];
    let idx = sequence.indexOf(pattern);
    while (idx !== -1) {
      matches.push(idx);
      idx = sequence.indexOf(pattern, idx + 1);
    }
    return {
      benchmarks: [
        { algorithmName: 'Knuth-Morris-Pratt (KMP)', matchesFound: matches.length, executionTimeMs: 0.045, timeComplexity: 'O(N + M)', spaceComplexity: 'O(M)', algorithmicApproach: 'Prefix-Function Failure Links' },
        { algorithmName: 'Z-Algorithm', matchesFound: matches.length, executionTimeMs: 0.052, timeComplexity: 'O(N + M)', spaceComplexity: 'O(N + M)', algorithmicApproach: 'Z-Box Preprocessing' },
        { algorithmName: 'Rabin-Karp Rolling Hash', matchesFound: matches.length, executionTimeMs: 0.068, timeComplexity: 'Avg O(N + M)', spaceComplexity: 'O(1)', algorithmicApproach: 'Rolling Polynomial Hash' }
      ]
    };
  }

  // --- 3. Genome Indexing (Module 2) ---
  async getSuffixRepeats(sequence, minLength = 4) {
    try {
      const res = await fetch(`${API_BASE_URL}/suffix/repeats?sequence=${encodeURIComponent(sequence)}&minLength=${minLength}`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      sequenceLength: sequence.length,
      maxLcpValue: 6,
      longestRepeat: 'ACCAC',
      repeats: [
        { pattern: 'ACCAC', length: 5, occurrences: 4, startPositions: [12, 17, 22, 45], repeatType: 'Tandem Repeat' },
        { pattern: 'CACC', length: 4, occurrences: 5, startPositions: [11, 16, 21, 38, 44], repeatType: 'Direct Repeat' }
      ],
      executionTimeMs: 0.18
    };
  }

  // --- 4. Sequence Alignment (Module 3) ---
  async alignSequences(type, seq1, seq2) {
    try {
      let endpoint = '/align/needleman-wunsch';
      if (type === 'smith-waterman') endpoint = '/align/smith-waterman';
      else if (type === 'levenshtein') endpoint = '/align/levenshtein';
      else if (type === 'damerau') endpoint = '/align/damerau-levenshtein';

      const res = await fetch(`${API_BASE_URL}${endpoint}?seq1=${encodeURIComponent(seq1)}&seq2=${encodeURIComponent(seq2)}`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      alignmentScore: 54,
      identityPercentage: 88.5,
      visualAlignment: `Seq1: ${seq1}\n      ||||||*|||\nSeq2: ${seq2}`,
      executionTimeMs: 0.22,
      timeComplexity: 'O(N * M)',
      spaceComplexity: 'O(N * M)'
    };
  }

  async compareAlignments(seq1, seq2) {
    try {
      const res = await fetch(`${API_BASE_URL}/align/compare?seq1=${encodeURIComponent(seq1)}&seq2=${encodeURIComponent(seq2)}`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      methods: [
        { methodName: 'Needleman-Wunsch', biologicalGoal: 'Global Sequence Homology', scoreOrDistance: 'Score: 48', similarityScore: '89.2%', timeComplexity: 'O(N * M)', spaceComplexity: 'O(N * M)', interpretation: 'End-to-end global homology across full coding region.' },
        { methodName: 'Smith-Waterman', biologicalGoal: 'Local Conserved Domain', scoreOrDistance: 'Score: 56', similarityScore: '94.0%', timeComplexity: 'O(N * M)', spaceComplexity: 'O(N * M)', interpretation: 'High-scoring conserved functional subdomain.' },
        { methodName: 'Damerau-Levenshtein', biologicalGoal: 'Point Mutation & Transposition Distance', scoreOrDistance: 'Distance: 2', similarityScore: '93.3%', timeComplexity: 'O(N * M)', spaceComplexity: 'O(N * M)', interpretation: 'Identified 1 substitution and 1 adjacent inversion.' }
      ]
    };
  }

  // --- 5. Network Flow (Module 4) ---
  async solveNetworkFlow(algorithm) {
    try {
      let endpoint = '/flow/dinic';
      if (algorithm === 'ford-fulkerson') endpoint = '/flow/ford-fulkerson';
      else if (algorithm === 'edmonds-karp') endpoint = '/flow/edmonds-karp';

      const res = await fetch(`${API_BASE_URL}${endpoint}`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      algorithm: algorithm.toUpperCase(),
      maxFlow: 65,
      minCutCapacity: 65,
      augmentingPaths: [
        { pathNumber: 1, pathString: 'Source -> Lab A -> Sequencer X -> Exp 1 -> Sink', bottleneckCapacity: 25 },
        { pathNumber: 2, pathString: 'Source -> Lab B -> Cryo-EM Suite -> Exp 2 -> Sink', bottleneckCapacity: 20 },
        { pathNumber: 3, pathString: 'Source -> Lab C -> Compute Cluster -> Exp 1 -> Sink', bottleneckCapacity: 20 }
      ],
      minCutBottleneckEdges: [
        { fromName: 'Lab A', toName: 'Sequencer X', capacity: 25, flow: 25 },
        { fromName: 'Lab B', toName: 'Cryo-EM Suite', capacity: 20, flow: 20 },
        { fromName: 'Lab C', toName: 'Compute Cluster', capacity: 20, flow: 20 }
      ],
      executionTimeMs: 0.35,
      timeComplexity: algorithm === 'dinic' ? 'O(V^2 * E)' : 'O(V * E^2)'
    };
  }

  async solveBipartiteMatching() {
    try {
      const res = await fetch(`${API_BASE_URL}/flow/bipartite-matching`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      matchingSize: 8,
      minVertexCoverSize: 8,
      konigTheoremSatisfied: true,
      assignments: [
        { researcherName: 'Dr. Rosalind Franklin', experimentName: 'Titan Cryo-EM Suite' },
        { researcherName: 'Dr. Jennifer Doudna', experimentName: 'CRISPR Oncogene Trial' },
        { researcherName: 'Dr. Francis Collins', experimentName: 'Pan-Genomics Locus' },
        { researcherName: 'Dr. Craig Venter', experimentName: 'Synthetic Minimal Genome' },
        { researcherName: 'Dr. Svante Pääbo', experimentName: 'Neanderthal Ancient DNA' },
        { researcherName: 'Dr. Feng Zhang', experimentName: 'Diagnostic Sensor' },
        { researcherName: 'Dr. Emmanuelle Charpentier', experimentName: 'Sickle Cell Edit' },
        { researcherName: 'Dr. David Baker', experimentName: 'De Novo Protein Design' }
      ],
      minVertexCoverNodes: ['Cryo-EM', 'CRISPR Trial', 'Pan-Genomics', 'Synthetic Genome', 'Neanderthal Locus', 'Diagnostic Sensor', 'Sickle Edit', 'Protein Design'],
      executionTimeMs: 0.42
    };
  }

  // --- 6. NP-Completeness (Module 5) ---
  async solve3Sat() {
    try {
      const res = await fetch(`${API_BASE_URL}/np/3sat-solve`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      satisfiable: true,
      clauseStrings: ['(TP53 | MDM2 | ~CDKN1A)', '(~TP53 | BAX | BCL2)', '(~MDM2 | ~BAX | PTEN)', '(CDKN1A | ~BCL2 | EGFR)'],
      variables: ['TP53', 'MDM2', 'CDKN1A', 'BAX', 'BCL2', 'PTEN', 'EGFR'],
      model: { TP53: true, MDM2: false, CDKN1A: false, BAX: true, BCL2: false, PTEN: true, EGFR: true },
      executionTimeMs: 0.45,
      complexity: 'DPLL Backtracking (Exponential Worst-Case O(2^V))'
    };
  }

  async getSatToCliqueReduction() {
    try {
      const res = await fetch(`${API_BASE_URL}/np/sat-to-clique`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      requiredCliqueSize: 4,
      foundClique: [
        { label: 'C1: TP53=true' },
        { label: 'C2: BAX=true' },
        { label: 'C3: PTEN=true' },
        { label: 'C4: EGFR=true' }
      ],
      nodes: [
        { id: 0, label: 'C1: TP53' }, { id: 1, label: 'C1: MDM2' }, { id: 2, label: 'C1: ~CDKN1A' },
        { id: 3, label: 'C2: ~TP53' }, { id: 4, label: 'C2: BAX' }, { id: 5, label: 'C2: BCL2' },
        { id: 6, label: 'C3: ~MDM2' }, { id: 7, label: 'C3: ~BAX' }, { id: 8, label: 'C3: PTEN' },
        { id: 9, label: 'C4: CDKN1A' }, { id: 10, label: 'C4: ~BCL2' }, { id: 11, label: 'C4: EGFR' }
      ],
      edges: [
        { from: 0, to: 4 }, { from: 0, to: 6 }, { from: 0, to: 8 }, { from: 4, to: 8 }, { from: 4, to: 11 }, { from: 8, to: 11 }
      ],
      theoremNote: 'Formula is Satisfiable <=> Target Graph contains a Clique of size k=4'
    };
  }

  // --- 7. Randomized & Parallel (Module 6) ---
  async runReservoirSampling(k = 5) {
    try {
      const res = await fetch(`${API_BASE_URL}/randparallel/reservoir?k=${k}`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return {
      totalStreamItems: 25,
      reservoirCapacity: k,
      theoreticalProbability: 0.20,
      finalSample: [
        'Flow cell temperature anomaly: 37.4°C at 10:14:02',
        'Single base substitution detected at chr17:41244400',
        'Reagent fluid pressure normalized: 14.2 psi',
        'Optical sensor Q30 score: 92.4% quality',
        'Sample barcode 08 validated at chamber 3'
      ]
    };
  }

  // --- 8. Variants ---
  async getVariants() {
    try {
      const res = await fetch(`${API_BASE_URL}/genome/variants`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return [
      { id: 'VAR-001', gene: 'BRCA1', position: 41244421, refBase: 'T', altBase: 'G', type: 'Missense', clinicalSignificance: 'Pathogenic', consequence: 'Cys61Gly missense substitution in RING finger' },
      { id: 'VAR-002', gene: 'TP53', position: 7577120, refBase: 'G', altBase: 'A', type: 'Missense', clinicalSignificance: 'Pathogenic', consequence: 'Arg175His hot-spot DNA binding disruption' },
      { id: 'VAR-003', gene: 'CFTR', position: 117199644, refBase: 'CTT', altBase: '-', type: 'Deletion', clinicalSignificance: 'Pathogenic', consequence: 'p.Phe508del (DeltaF508) in-frame cystic fibrosis mutation' },
      { id: 'VAR-004', gene: 'EGFR', position: 55242465, refBase: '-', altBase: 'GCC', type: 'Insertion', clinicalSignificance: 'Likely Pathogenic', consequence: 'Exon 20 insertion activating kinase signaling' }
    ];
  }

  // --- 9. History ---
  async getHistory() {
    try {
      const res = await fetch(`${API_BASE_URL}/history/recent?limit=20`);
      if (res.ok) {
        const json = await res.json();
        if (json.success) return json.data;
      }
    } catch (e) {}
    return [
      { timestamp: 'Just now', algorithmName: 'KMP', moduleName: 'DNA Motif Discovery', resultSummary: '2 exact promoter occurrences found', executionTimeMs: 1 },
      { timestamp: '1 min ago', algorithmName: 'Needleman-Wunsch', moduleName: 'Sequence Alignment', resultSummary: 'Global homology score 54, 88.5% identity', executionTimeMs: 2 },
      { timestamp: '3 mins ago', algorithmName: 'Dinic', moduleName: 'Biological Networks', resultSummary: 'Max flow computed: 65 units/day', executionTimeMs: 1 }
    ];
  }
}

window.apiClient = new BioGenomeApiClient();
