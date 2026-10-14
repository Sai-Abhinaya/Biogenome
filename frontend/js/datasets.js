/**
 * BioGenome – Simulated Educational Datasets
 * All datasets are strictly educational approximations and synthetic sequences for DSA demonstrations.
 * Label: "Simulated Educational Dataset"
 */

const EDUCATIONAL_DATASETS = {
  dna: [
    {
      id: 'ecoli-promoter',
      name: 'E. coli Promoter Sequence (-35 to -10 Region)',
      category: 'Bacterial sample DNA',
      badge: 'Simulated Educational Dataset',
      description: 'Synthetic promoter region containing canonical -35 (TTGACA) and Pribnow box -10 (TATAAT) motifs.',
      sequence: 'TTGACATGCATCGATCGATTATAATGCGTA',
      defaultPattern: 'TATAAT',
      notes: 'Demonstrates exact motif localization in prokaryotic transcriptional initiation sites.'
    },
    {
      id: 'human-hbb-exon',
      name: 'Human HBB Beta-Globin Exon 1 Fragment',
      category: 'Human-like sample DNA',
      badge: 'Simulated Educational Dataset',
      description: 'Educational synthetic sequence approximating human hemoglobin beta chain coding sequence with start codon ATG.',
      sequence: 'ATGGTGCACCTGACTCCTGAGGAGAAGTCT',
      defaultPattern: 'ATG',
      notes: 'Standard educational reference for start codon identification and sickle-cell point mutation analysis.'
    },
    {
      id: 'sarscov2-cleavage',
      name: 'Spike Glycoprotein Furin Cleavage Site',
      category: 'Viral sample DNA',
      badge: 'Simulated Educational Dataset',
      description: 'Synthetic DNA segment corresponding to PRRA multi-basic cleavage motif insertion.',
      sequence: 'CCTCGGCGGGCACGTTCAGTG',
      defaultPattern: 'CGGGCAC',
      notes: 'Used to model restriction enzyme cleavage site and polybasic insertion identification.'
    },
    {
      id: 'synthetic-tandem',
      name: 'Tandem Microsatellite Repeat (STR)',
      category: 'Synthetic DNA',
      badge: 'Simulated Educational Dataset',
      description: 'Repetitive genomic region designed to stress-test string matching algorithms with recurring prefixes.',
      sequence: 'ATGCGATGACATGCG',
      defaultPattern: 'ATG',
      notes: 'Contains overlapping substrings to demonstrate KMP LPS fallback vs naive redundant comparisons.'
    },
    {
      id: 'gc-rich-cpg',
      name: 'CpG Island Methylation Region',
      category: 'Synthetic DNA',
      badge: 'Simulated Educational Dataset',
      description: 'Synthetic high GC-density sequence mimicking eukaryotic gene promoter epigenetic islands.',
      sequence: 'GCGCGCATGCGCGCGTAGCGC',
      defaultPattern: 'CGCG',
      notes: 'Demonstrates worst-case naive matching behavior where Rabin-Karp and Z-Algorithm exhibit superior performance.'
    }
  ],

  multiMotifs: [
    {
      id: 'transcription-factors',
      name: 'Regulatory Binding Motif Callset',
      badge: 'Simulated Educational Dataset',
      sequence: 'ATGCGAATGCGATCCGATTTGCAATG',
      patterns: ['ATG', 'GGA', 'TGC', 'CAT'],
      description: 'Simultaneous search for multiple promoter binding motifs using Aho-Corasick automaton.'
    },
    {
      id: 'restriction-enzymes',
      name: 'Standard Restriction Endonuclease Recognition Sites',
      badge: 'Simulated Educational Dataset',
      sequence: 'GAATTCGGATCCGTCGACAAGCTT',
      patterns: ['GAATTC', 'GGATCC', 'GTCGAC', 'AAGCTT'],
      description: 'EcoRI, BamHI, SalI, and HindIII recognition sites in cloning vector multiple cloning site (MCS).'
    }
  ],

  alignmentPairs: [
    {
      id: 'homolog-pair-1',
      name: 'Beta-Globin vs Alpha-Globin Fragment',
      badge: 'Simulated Educational Dataset',
      seqA: 'ATGGTGCACCTG',
      seqB: 'ATGCTGTCTCCG',
      description: 'Demonstrating global Needleman-Wunsch alignment across homologous paralogs with mismatches and gaps.'
    },
    {
      id: 'variant-pair',
      name: 'Wild-Type vs Mutated Sequence (Indel & Mismatch)',
      badge: 'Simulated Educational Dataset',
      seqA: 'GATTACA',
      seqB: 'GCATGCU',
      seqB_dna: 'GCATGCT',
      description: 'Comparison showing insertions, deletions, substitutions, and edit distance costs.'
    },
    {
      id: 'local-domain',
      name: 'Zinc-Finger Conserved Catalytic Core',
      badge: 'Simulated Educational Dataset',
      seqA: 'TTTGACATGCATCGATCGATTATAATGCG',
      seqB: 'CGATTATAATG',
      description: 'Local alignment demonstrating Smith-Waterman identification of the highly conserved internal box.'
    }
  ],

  flowNetworks: [
    {
      id: 'lab-reagent-flow',
      name: 'Genomic Reagent & Specimen Pipeline',
      badge: 'Simulated Educational Dataset',
      source: 'Central_ColdStorage',
      sink: 'Sequencing_Cluster',
      nodes: ['Central_ColdStorage', 'Prep_Lab_A', 'Prep_Lab_B', 'Quality_Assurance', 'Sequencing_Cluster'],
      edges: [
        { u: 'Central_ColdStorage', v: 'Prep_Lab_A', cap: 14 },
        { u: 'Central_ColdStorage', v: 'Prep_Lab_B', cap: 12 },
        { u: 'Prep_Lab_A', v: 'Prep_Lab_B', cap: 5 },
        { u: 'Prep_Lab_A', v: 'Quality_Assurance', cap: 10 },
        { u: 'Prep_Lab_B', v: 'Sequencing_Cluster', cap: 14 },
        { u: 'Quality_Assurance', v: 'Sequencing_Cluster', cap: 10 }
      ],
      description: 'Models reagent routing and pipetting throughput from storage to next-generation sequencing instruments.'
    },
    {
      id: 'crispr-experiment-matching',
      name: 'Postdoc Researchers to Sequencing Projects',
      badge: 'Simulated Educational Dataset',
      left: ['Dr. Aris (CRISPR)', 'Dr. Chen (Transcriptomics)', 'Dr. Smith (Proteomics)', 'Dr. Patel (Epigenetics)'],
      right: ['Project 1 (RNA-Seq)', 'Project 2 (Hi-C)', 'Project 3 (ChIP-Seq)', 'Project 4 (PacBio LongRead)'],
      edges: [
        { u: 0, v: 0 },
        { u: 0, v: 1 },
        { u: 1, v: 0 },
        { u: 1, v: 2 },
        { u: 2, v: 2 },
        { u: 2, v: 3 },
        { u: 3, v: 1 },
        { u: 3, v: 3 }
      ],
      description: 'Bipartite maximum matching demonstrating laboratory personnel resource allocation.'
    }
  ],

  satFormulas: [
    {
      id: 'gene-circuit-sat',
      name: 'Synthetic Gene Circuit Logic (Repressilator)',
      badge: 'Simulated Educational Dataset',
      variables: ['A (TetR)', 'B (LacI)', 'C (cI)'],
      clauses: [
        ['A', 'B', 'C'],
        ['~A', 'B', '~C'],
        ['~B', 'C', 'A'],
        ['~C', '~A', 'B']
      ],
      description: '3-SAT formulation of bi-stable transcriptional repressor cascade feasibility.'
    }
  ],

  streamingObservations: [
    {
      id: 'fluorescence-stream',
      name: 'Real-time Single-Cell Flow Cytometer Stream',
      badge: 'Simulated Educational Dataset',
      items: [
        { id: 1, label: 'Cell #1 (GFP+ 124 RFU)' },
        { id: 2, label: 'Cell #2 (GFP- 14 RFU)' },
        { id: 3, label: 'Cell #3 (GFP+ 310 RFU)' },
        { id: 4, label: 'Cell #4 (GFP+ 89 RFU)' },
        { id: 5, label: 'Cell #5 (GFP- 18 RFU)' },
        { id: 6, label: 'Cell #6 (GFP+ 450 RFU)' },
        { id: 7, label: 'Cell #7 (GFP+ 220 RFU)' },
        { id: 8, label: 'Cell #8 (GFP- 12 RFU)' },
        { id: 9, label: 'Cell #9 (GFP+ 605 RFU)' },
        { id: 10, label: 'Cell #10 (GFP+ 180 RFU)' },
        { id: 11, label: 'Cell #11 (GFP- 22 RFU)' },
        { id: 12, label: 'Cell #12 (GFP+ 390 RFU)' }
      ],
      sampleSize: 4,
      description: 'Streaming uniform sampling (Reservoir Sampling Algorithm) without knowing total cell count in advance.'
    }
  ]
};

window.EDUCATIONAL_DATASETS = EDUCATIONAL_DATASETS;
