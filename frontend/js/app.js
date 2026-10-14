/**
 * BioGenome – Main Application Controller
 * Connects Biological Problems to Classical Data Structures & Algorithms.
 * 100% Client-Side – Zero Backend Required.
 *
 * Designed with a refined scientific mental model:
 * 1. Biological Problem (The real biological question)
 * 2. DSA Algorithm (The computational algorithm solving it)
 * 3. Step-by-step How It Works & Interactive Visual Execution
 */

// Comprehensive Registry of all 33 Algorithms Organized by Biological Problem
const ALGORITHM_REGISTRY = [
  // 1. Sequence Pattern Matching
  {
    id: 'kmp',
    bioProblem: 'DNA Sequence Matching',
    bioDomain: 'Sequence Pattern Matching',
    dsaAlgorithm: 'Knuth–Morris–Pratt (KMP)',
    purpose: 'Locates exact occurrences of a regulatory motif inside a long DNA sequence without backtracking, skipping redundant comparisons via a precomputed prefix fallback table.',
    bioContext: 'In molecular biology, transcription factors and restriction enzymes bind to specific conserved DNA motifs (e.g. Pribnow box TATAAT in bacteria, TATA box in eukaryotes). Locating these motifs in long chromosomes requires searching without quadratic backtracking upon partial mismatch.',
    dsaConnection: 'The KMP algorithm constructs a Longest Prefix Suffix (LPS / π) table in O(m) time. When a nucleotide mismatch occurs at pattern index j, the LPS table determines the longest proper prefix that is also a suffix, shifting the pattern to resume comparisons without re-examining matched characters in the target genome.',
    howItWorks: [
      'Compute the LPS (π) array for the query motif of length m by finding matching proper prefixes and suffixes.',
      'Initialize pointers i = 0 for the target DNA sequence and j = 0 for the motif pattern.',
      'Compare target[i] with pattern[j]. If they match, advance both pointers i and j.',
      'If a mismatch occurs after j matches, look up LPS[j-1] and set j = LPS[j-1] without decrementing i.',
      'When j reaches m, record a complete 0-based match at offset i - m and resume using LPS[j-1].'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Target DNA Sequence (Text T)', pat: 'Target Motif (Pattern P)' },
    outputDescription: 'Zero-based start positions of all occurrences, character comparisons performed, and LPS fallback array.',
    timeComplexity: 'O(n + m)',
    timeExplanation: 'Linear time: at most 2n comparisons on the target sequence plus m steps to build the LPS array.',
    spaceComplexity: 'O(m)',
    spaceExplanation: 'Requires storing the LPS array of size m proportional to the motif length.',
    defaultSeq: 'ATGCGATGACATGCG',
    defaultPat: 'ATG',
    defaultPreset: 'human-hbb-exon'
  },
  {
    id: 'z-alg',
    bioProblem: 'Tandem Repeat & Motif Search',
    bioDomain: 'Sequence Pattern Matching',
    dsaAlgorithm: 'Z-Algorithm (Linear Prefix Matching)',
    purpose: 'Discovers tandem repeats, microsatellites, and motif boundaries in linear time by computing longest common prefixes between sequence suffixes and the prefix.',
    bioContext: 'Genomes contain tandem repeats (microsatellites / STRs) associated with genetic disorders and DNA fingerprinting. The Z-algorithm computes the exact length of the longest substring starting at each base that matches the sequence prefix, identifying repetitive regions in one pass.',
    dsaConnection: 'By concatenating Pattern + "$" + Sequence and maintaining a sliding window or "Z-box" [L, R] of the furthest reaching match, the Z-algorithm reuses previously verified comparisons to calculate all Z-values in strictly O(n + m) time.',
    howItWorks: [
      'Construct concatenated sequence S = Pattern + "$" + Text, where "$" is a unique sentinel.',
      'Maintain an active Z-box interval [L, R] representing the rightmost substring matching a prefix of S.',
      'For each index i, if i > R, compute Z[i] by naive character comparisons starting at i.',
      'If i ≤ R, initialize Z[i] using the previously computed symmetric value Z[i - L], expanding beyond R only if necessary.',
      'Any index in the text where Z[i] equals the pattern length corresponds to a full motif occurrence.'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Target DNA Sequence (Text T)', pat: 'Query Motif (Pattern P)' },
    outputDescription: 'Z-array values for each position, Z-box interval [L, R], and 0-based match positions.',
    timeComplexity: 'O(n + m)',
    timeExplanation: 'Each character is compared at most twice because the right boundary R only moves forward.',
    spaceComplexity: 'O(n + m)',
    spaceExplanation: 'Stores the Z-array for the concatenated sequence of length n + m + 1.',
    defaultSeq: 'ATGCGATGACATGCG',
    defaultPat: 'ATG',
    defaultPreset: 'synthetic-tandem'
  },
  {
    id: 'rabin-karp',
    bioProblem: 'Rolling Hash Pattern Matching',
    bioDomain: 'Sequence Pattern Matching',
    dsaAlgorithm: 'Rabin–Karp (Rolling Polynomial Hash)',
    purpose: 'Rapidly screens sliding windows across continuous genomic sequences using a rolling polynomial hash, performing character verification only when hashes match.',
    bioContext: 'When analyzing long chromosomal contigs or continuous sequencer streams, hashing small sliding windows allows fast filtering of non-matching regions (e.g. scanning for CpG islands or specific k-mers) before performing expensive character-by-character checks.',
    dsaConnection: 'Uses a polynomial rolling hash modulo a large prime. Advancing the sliding window takes O(1) time by subtracting the outgoing nucleotide and adding the incoming nucleotide, achieving O(n + m) average time.',
    howItWorks: [
      'Calculate the polynomial hash of the query pattern of length m: H(P) = ∑ (P[i] · b^(m-1-i)) mod q.',
      'Calculate the initial hash of the first m characters in the target genome: H(W₀).',
      'Compare H(W) with H(P). If hashes match, verify character-by-character to rule out hash collisions.',
      'Slide the window by one base: H(W_{k+1}) = (b · (H(W_k) - T[k] · b^{m-1}) + T[k+m]) mod q in O(1) time.',
      'Record verified matches and report any spurious hash collisions encountered during execution.'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Target DNA Sequence (Text T)', pat: 'Target Motif (Pattern P)' },
    outputDescription: 'Rolling hash values for each window, collision logs, and 0-based match indices.',
    timeComplexity: 'O(n + m) Average | O(nm) Worst-Case',
    timeExplanation: 'Average time is linear with rare collisions; worst-case occurs if all windows produce spurious collisions.',
    spaceComplexity: 'O(1)',
    spaceExplanation: 'Only stores rolling hash integers and constants independent of sequence length.',
    defaultSeq: 'GCGCGCATGCGCGCGTAGCGC',
    defaultPat: 'CGCG',
    defaultPreset: 'gc-rich-cpg'
  },
  {
    id: 'aho-corasick',
    bioProblem: 'Multiple Sequence Pattern Search',
    bioDomain: 'Multi-Pattern Screening',
    dsaAlgorithm: 'Aho–Corasick Automaton',
    purpose: 'Simultaneously searches for multiple transcription factor binding sites or restriction enzyme recognition sites in a single pass over the genome.',
    bioContext: 'In restriction enzyme digest mapping and synthetic biology, multiple enzymes (e.g. EcoRI, BamHI, HindIII) are screened simultaneously. Querying each pattern individually is inefficient; Aho-Corasick matches an entire library of motifs in one single scan.',
    dsaConnection: 'Constructs a trie of all target motifs supplemented with BFS failure transitions (analogous to KMP LPS) and output links. Walking the target sequence through the automaton takes O(n + ∑m + z) time regardless of dictionary size.',
    howItWorks: [
      'Build a prefix tree (Trie) containing all query motifs in the dictionary.',
      'Using breadth-first search, compute failure links for each state to the longest proper suffix that exists in the trie.',
      'Establish dictionary output links so that sub-patterns ending at the same state are reported simultaneously.',
      'Stream the target DNA sequence character-by-character through the automaton.',
      'Follow trie edges on match, or follow failure links on mismatch without backing up the text pointer.'
    ],
    inputType: 'dna_and_multi_patterns',
    inputLabels: { seq: 'Target DNA Sequence (Text T)', pat: 'Comma-Separated Motifs (e.g. ATG, GGA, TGC, CAT)' },
    outputDescription: 'Automaton state transitions, failure links followed, and detected motifs with 0-based positions.',
    timeComplexity: 'O(n + ∑m + z)',
    timeExplanation: 'Linear in total text length n plus sum of pattern lengths ∑m, plus count of matches z.',
    spaceComplexity: 'O(∑m · |Σ|)',
    spaceExplanation: 'Memory proportional to the trie size across all patterns over nucleotide alphabet Σ={A,C,G,T}.',
    defaultSeq: 'ATGCGAATGCGATCCGATTTGCAATG',
    defaultPat: 'ATG, GGA, TGC, CAT',
    defaultPreset: 'transcription-factors'
  },

  // 2. Sequence Alignment & Similarity
  {
    id: 'levenshtein',
    bioProblem: 'Mutational Edit Distance',
    bioDomain: 'Sequence Alignment & Similarity',
    dsaAlgorithm: 'Levenshtein Distance (Dynamic Programming)',
    purpose: 'Measures the minimum number of single-nucleotide mutations (insertions, deletions, substitutions) needed to transform one gene sequence into another.',
    bioContext: 'Comparing sequencing reads against a reference genome reveals point mutations (SNPs) and insertions/deletions (indels). Levenshtein distance quantifies the mutational divergence between alleles or homologous sequences.',
    dsaConnection: 'Fills an (m+1) × (n+1) dynamic programming matrix where each cell dp[i][j] represents the minimum edit cost between prefixes s1[1..i] and s2[1..j], followed by traceback to recover the optimal mutational path.',
    howItWorks: [
      'Initialize matrix dp of size (m+1) × (n+1) with base cases dp[i][0] = i and dp[0][j] = j.',
      'For each base pair (s1[i-1], s2[j-1]), compute cost = 0 if identical, else 1.',
      'Calculate dp[i][j] = min(dp[i-1][j] + 1 [deletion], dp[i][j-1] + 1 [insertion], dp[i-1][j-1] + cost [substitution]).',
      'The bottom-right cell dp[m][n] holds the minimum mutational edit distance.',
      'Trace back from (m, n) to (0, 0) to determine the exact sequence of mutational events.'
    ],
    inputType: 'two_dna_sequences',
    inputLabels: { seq1: 'Reference DNA Sequence (WT)', seq2: 'Mutated / Query Sequence' },
    outputDescription: 'Total edit distance score, completed DP scoring matrix, and step-by-step mutational operations.',
    timeComplexity: 'O(m · n)',
    timeExplanation: 'Fills an (m+1) × (n+1) table where each cell takes constant O(1) time.',
    spaceComplexity: 'O(m · n)',
    spaceExplanation: 'Stores the full DP table to enable complete mutational traceback.',
    defaultSeq1: 'GATTACA',
    defaultSeq2: 'GCATGCT',
    defaultPreset: 'variant-pair'
  },
  {
    id: 'damerau',
    bioProblem: 'Transposition Mutation Analysis',
    bioDomain: 'Sequence Alignment & Similarity',
    dsaAlgorithm: 'Damerau–Levenshtein Distance',
    purpose: 'Quantifies mutational distance including adjacent base-pair swap transpositions as single mutational events alongside insertions, deletions, and substitutions.',
    bioContext: 'Adjacent nucleotide inversions and optical sequencing transpositions (e.g. AG ↔ GA) often occur as single biochemical or instrument error events. Damerau-Levenshtein distance accounts for these swaps with unit cost.',
    dsaConnection: 'Extends 2D dynamic programming by inspecting two consecutive characters. If s1[i-1] == s2[j-2] and s1[i-2] == s2[j-1], a fourth transition from dp[i-2][j-2] + 1 evaluates adjacent transposition.',
    howItWorks: [
      'Initialize (m+1) × (n+1) matrix with base costs for deletion and insertion.',
      'Evaluate standard insertion, deletion, and substitution costs as in Levenshtein distance.',
      'If i > 1 and j > 1 and s1[i-1] == s2[j-2] and s1[i-2] == s2[j-1], consider transposition cost dp[i-2][j-2] + 1.',
      'Assign the minimum of the four operations to cell dp[i][j].',
      'The final cell dp[m][n] yields the optimal distance incorporating adjacent transpositions.'
    ],
    inputType: 'two_dna_sequences',
    inputLabels: { seq1: 'Reference Sequence', seq2: 'Target Sequence with Transposition' },
    outputDescription: 'Damerau-Levenshtein distance, 4-operation DP matrix, and identified base swaps.',
    timeComplexity: 'O(m · n)',
    timeExplanation: 'Four constant-time evaluations per cell across the (m+1) × (n+1) matrix.',
    spaceComplexity: 'O(m · n)',
    spaceExplanation: 'Requires the complete DP matrix for optimal alignment recovery.',
    defaultSeq1: 'CA',
    defaultSeq2: 'AC',
    defaultPreset: 'variant-pair'
  },
  {
    id: 'needleman-wunsch',
    bioProblem: 'End-to-End Global Gene Homology',
    bioDomain: 'Sequence Alignment & Similarity',
    dsaAlgorithm: 'Needleman–Wunsch Alignment',
    purpose: 'Determines the optimal end-to-end global alignment between homologous gene sequences using match scores, mismatch penalties, and linear gap penalties.',
    bioContext: 'When comparing two full genes of common evolutionary descent (e.g. Human Alpha-Globin vs Beta-Globin), global alignment identifies conserved functional codons and evolutionary insertion/deletion gaps across their entire length.',
    dsaConnection: 'Dynamic programming maximizes an alignment similarity score over a scoring matrix M(i,j) = max(diag + match/mismatch, up + gap, left + gap), guaranteeing mathematically optimal global alignment.',
    howItWorks: [
      'Initialize matrix with gap penalty decrements along the first row and column: dp[i][0] = -i · d, dp[0][j] = -j · d.',
      'For each cell (i, j), calculate score for diagonal match/mismatch (s(a_i, b_j)), vertical gap, and horizontal gap.',
      'Store dp[i][j] = max(diagonal, vertical, horizontal) and record the backpointer.',
      'Cell dp[m][n] contains the maximum possible global alignment score.',
      'Trace back from bottom-right (m, n) to top-left (0, 0) to construct the aligned sequences with inserted gap characters "-".'
    ],
    inputType: 'two_dna_sequences',
    inputLabels: { seq1: 'Homolog Gene Sequence A', seq2: 'Homolog Gene Sequence B' },
    outputDescription: 'Optimal alignment score, completed scoring grid, and formatted global pairwise alignment string.',
    timeComplexity: 'O(m · n)',
    timeExplanation: 'Quadratic in sequence lengths m and n; each cell requires constant comparison of 3 predecessors.',
    spaceComplexity: 'O(m · n)',
    spaceExplanation: 'Stores the scoring table and traceback pointers for path reconstruction.',
    defaultSeq1: 'ATGGTGCACCTG',
    defaultSeq2: 'ATGCTGTCTCCG',
    defaultPreset: 'homolog-pair-1'
  },
  {
    id: 'smith-waterman',
    bioProblem: 'Conserved Catalytic Domain Discovery',
    bioDomain: 'Sequence Alignment & Similarity',
    dsaAlgorithm: 'Smith–Waterman Local Alignment',
    purpose: 'Identifies highest-scoring local catalytic motifs and conserved sub-segments without penalizing non-homologous flanking sequence via zero-clamped dynamic programming.',
    bioContext: 'Proteins and regulatory regions often share small functional domains (e.g. Zinc fingers, ATP-binding cassettes) surrounded by variable non-conserved sequences. Global alignment would obscure these domains; local alignment isolates them cleanly.',
    dsaConnection: 'Clamps negative alignment scores to zero: M(i, j) = max(0, diag + score, up + gap, left + gap). Traceback begins at the highest scoring cell anywhere in the matrix and terminates upon reaching a zero cell.',
    howItWorks: [
      'Initialize the first row and column entirely to 0 (no penalty for starting local alignment anywhere).',
      'Fill cell dp[i][j] using max(0, diagonal match/mismatch, vertical gap, horizontal gap).',
      'Track the maximum score cell (i*, j*) encountered anywhere across the entire matrix.',
      'Begin traceback at (i*, j*) following optimal parent directions.',
      'Terminate traceback immediately when reaching a cell with value 0, yielding the conserved local motif.'
    ],
    inputType: 'two_dna_sequences',
    inputLabels: { seq1: 'Sequence Containing Domain', seq2: 'Target Query Sequence' },
    outputDescription: 'Peak local alignment score, coordinates of the conserved domain, and isolated sub-alignment.',
    timeComplexity: 'O(m · n)',
    timeExplanation: 'Standard 2D dynamic programming grid filling with O(1) transitions per cell.',
    spaceComplexity: 'O(m · n)',
    spaceExplanation: 'Maintains the 2D matrix for identifying the global peak and tracing the local alignment.',
    defaultSeq1: 'TGTTACGG',
    defaultSeq2: 'GGTTGA',
    defaultPreset: 'homolog-pair-1'
  },
  {
    id: 'bitmask-dp',
    bioProblem: 'Contig Read Assembly via Overlap',
    bioDomain: 'Sequence Alignment & Similarity',
    dsaAlgorithm: 'Genomic Bitmask Dynamic Programming',
    purpose: 'Solves the Shortest Common Superstring (SCS) problem to assemble overlapping sequencing reads into minimal contiguous chromosome fragments (contigs).',
    bioContext: 'Shotgun sequencing breaks genomic DNA into millions of short reads. Reassembling the original chromosome requires finding the shortest common superstring that contains all overlapping fragments.',
    dsaConnection: 'Models the set of assembled reads as a bitmask integer where the k-th bit indicates whether read k has been included. Uses dynamic programming over 2^k subset states to find the minimum overlap cost path in O(k^2 · 2^k) time.',
    howItWorks: [
      'Compute pairwise suffix-prefix overlap lengths between all pairs of sequencing reads.',
      'Define state dp[mask][i] as the minimum length of a superstring containing reads in mask ending with read i.',
      'Iterate through bitmasks in increasing order of subset size.',
      'Transition by appending an unvisited read j, taking advantage of precomputed overlap(i, j).',
      'Trace back the optimal permutation to reconstruct the fully assembled genomic contig superstring.'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Fragment 1 (e.g. ATGC)', pat: 'Fragment 2 (e.g. GCAT)' },
    outputDescription: 'Minimum assembled contig superstring and bitmask subset state transition table.',
    timeComplexity: 'O(k² · 2^k)',
    timeExplanation: 'Exponential in the number of reads k, but significantly faster than naive O(k!) factorial enumeration.',
    spaceComplexity: 'O(k · 2^k)',
    spaceExplanation: 'Table size corresponds to all subsets of reads paired with their terminating read.',
    defaultSeq: 'ATGC',
    defaultPat: 'GCAT',
    defaultPreset: 'synthetic-tandem'
  },
  {
    id: 'matrix-chain',
    bioProblem: '3D Coordinate Transformation Ordering',
    bioDomain: 'Sequence Alignment & Similarity',
    dsaAlgorithm: 'Matrix Chain Multiplication DP',
    purpose: 'Minimizes computational scalar multiplications when applying a sequence of 3D macromolecular coordinate transformations during molecular dynamics simulation.',
    bioContext: 'Structural biology pipelines simulate protein folding by chaining coordinate rotation, translation, and scaling matrices. Because matrix multiplication is associative, choosing the optimal parenthesization drastically reduces computational floating-point operations.',
    dsaConnection: 'Computes cost table m[i, j] = min_{i ≤ k < j} (m[i, k] + m[k+1, j] + p_{i-1}p_k p_j) in O(n³) time using dynamic programming over increasing chain lengths.',
    howItWorks: [
      'Represent matrix dimensions as array p where matrix A_i has dimension p_{i-1} × p_i.',
      'Initialize diagonal m[i, i] = 0 for single matrices.',
      'Iterate through chain lengths L from 2 to n.',
      'For each subchain (i, j), test all split points k to find the minimal scalar multiplication cost.',
      'Reconstruct the optimal parenthesization using split table s[i, j].'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Matrix Dimensions (e.g. 10, 30, 5, 60)', pat: 'Ignored for Matrix Chain' },
    outputDescription: 'Minimum scalar multiplication operations and optimal parenthesized expression.',
    timeComplexity: 'O(n³)',
    timeExplanation: 'Three nested loops over chain length, starting index, and split point k.',
    spaceComplexity: 'O(n²)',
    spaceExplanation: 'Stores cost matrix m[n][n] and split index matrix s[n][n].',
    defaultSeq: '10, 30, 5, 60',
    defaultPat: '',
    defaultPreset: 'synthetic-tandem'
  },
  {
    id: 'optimal-bst',
    bioProblem: 'Weighted Gene Marker Query Optimization',
    bioDomain: 'Sequence Alignment & Similarity',
    dsaAlgorithm: 'Optimal Binary Search Tree (OBST)',
    purpose: 'Constructs a minimum-latency search tree weighted by genomic marker access probabilities from clinical diagnostic databases.',
    bioContext: 'In clinical genetics, certain mutation hot-spots (e.g. BRCA1, TP53, CFTR) are queried thousands of times more frequently than rare variants. An optimal binary search tree places high-frequency genes closer to the root to minimize average query latency.',
    dsaConnection: 'Dynamic programming evaluates subtrees over keys k_i..k_j, calculating e[i, j] = min_r (e[i, r-1] + e[r+1, j] + w(i, j)) in O(n³) time to minimize expected search depth.',
    howItWorks: [
      'Order genomic marker keys alphabetically with their empirical query probabilities p_i.',
      'Initialize base costs for single-key subtrees.',
      'For subtrees of length 2 to n, evaluate each candidate key as root.',
      'Add the sum of probabilities w(i, j) to account for increasing search depth by one.',
      'Record the optimal root for each subproblem in root table root[i, j].'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Marker Keys (e.g. BRCA1, CFTR, TP53)', pat: 'Ignored for OBST' },
    outputDescription: 'Minimum expected search cost and root hierarchy for the optimal marker query tree.',
    timeComplexity: 'O(n³)',
    timeExplanation: 'Standard dynamic programming over subtree intervals and root choices.',
    spaceComplexity: 'O(n²)',
    spaceExplanation: 'Requires cost table e[n][n] and root table root[n][n].',
    defaultSeq: 'BRCA1, CFTR, TP53',
    defaultPat: '',
    defaultPreset: 'synthetic-tandem'
  },

  // 3. Genome Indexing & Search
  {
    id: 'suffix-array',
    bioProblem: 'Whole-Genome Indexing & Binary Search',
    bioDomain: 'Genome Indexing & Search',
    dsaAlgorithm: 'Suffix Array with Binary Search',
    purpose: 'Enables sub-linear exact pattern matching across full chromosomes by sorting all genomic suffixes and binary searching without storing an explicit tree.',
    bioContext: 'Human chromosomes contain over 200 million base pairs. Constructing a full tree can consume excessive memory. A Suffix Array stores just sorted integer indices into the genome, allowing logarithmic query times with minimal memory footprint.',
    dsaConnection: 'Stores sorted 0-based start positions of all n suffixes in array SA. Suffix comparisons are lexicographical. Binary searching the SA bounds (low, high) pinpoints any query pattern of length m in O(m log n) time.',
    howItWorks: [
      'Extract all n suffixes of the genomic sequence along with their original start indices.',
      'Sort all suffixes lexicographically.',
      'Store their original start positions in array SA, where SA[i] is the starting index of the i-th lexicographical suffix.',
      'To locate query pattern P, perform binary search over SA using lexicographical prefix comparisons.',
      'The range [low, high] in SA directly identifies all genomic positions where pattern P occurs.'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Genomic Reference Sequence (T)', pat: 'Query Substring to Search' },
    outputDescription: 'Sorted suffix array SA, binary search interval steps, and occurrences in the reference genome.',
    timeComplexity: 'O(n log n) Sort | O(m log n) Search',
    timeExplanation: 'Logarithmic binary search over n suffixes with m character comparisons per probe.',
    spaceComplexity: 'O(n)',
    spaceExplanation: 'Compact representation requiring only n integer indices.',
    defaultSeq: 'ATGCAT',
    defaultPat: 'GCA',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'sais',
    bioProblem: 'Linear-Time Suffix Array Construction',
    bioDomain: 'Genome Indexing & Search',
    dsaAlgorithm: 'SA-IS (Induced Sorting)',
    purpose: 'Constructs the suffix array of whole chromosomes in strictly linear time O(n) using character S/L classifications and induced sorting of LMS substrings.',
    bioContext: 'Large genomic assemblies require fast indexing. Standard sorting takes O(n log n) or O(n²); the SA-IS algorithm indexes mammalian genomes in linear O(n) time, forming the foundation of tools like BWA and Bowtie.',
    dsaConnection: 'Classifies characters into S-type (smaller than next) and L-type (larger than next). Left-Most S-type (LMS) suffixes are recursively sorted and their order is used to induce the sorted order of all L and S suffixes.',
    howItWorks: [
      'Scan the genome from right to left to classify every character as S-type or L-type.',
      'Identify LMS (Left-Most S) characters whose predecessor is an L-type character.',
      'Place LMS suffixes into bucket ends and induce sort L-type then S-type suffixes into their respective buckets.',
      'Name LMS substrings; if names are not unique, solve recursively on the reduced string.',
      'Use sorted LMS indices to induce the final linear-time sorted suffix array in O(n) total operations.'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Genomic Sequence (T)', pat: 'Ignored for SA-IS' },
    outputDescription: 'Character S/L/LMS classifications, bucket partition passes, and final linear-time suffix array.',
    timeComplexity: 'O(n)',
    timeExplanation: 'Strictly linear time via induced sorting passes and geometric recursion reduction.',
    spaceComplexity: 'O(n)',
    spaceExplanation: 'Requires auxiliary character type array and bucket pointer arrays proportional to sequence length.',
    defaultSeq: 'GCGTGCCTG',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'kasai',
    bioProblem: 'Longest Repeated Substring Discovery',
    bioDomain: 'Genome Indexing & Search',
    dsaAlgorithm: 'Kasai’s LCP Algorithm',
    purpose: 'Computes Longest Common Prefix (LCP) lengths between lexicographically adjacent suffixes in strictly linear time O(n) to identify duplicated genes and repeats.',
    bioContext: 'Duplicated gene families, segmental duplications, and long interspersed nuclear elements (LINEs) are identified by finding repeated substrings. The maximum value in the LCP array directly reveals the longest repeat in the genome.',
    dsaConnection: 'Uses the key mathematical property: if suffix i has LCP h with its predecessor in the suffix array, then suffix i+1 must have LCP at least h - 1 with its predecessor. This prevents re-comparing characters, yielding O(n) total time.',
    howItWorks: [
      'Take the genome sequence and its precomputed Suffix Array SA.',
      'Construct Rank array where Rank[i] gives the lexicographical position of suffix i in SA.',
      'Iterate through suffixes in text order i = 0 to n-1, maintaining common prefix length h.',
      'Find the predecessor suffix in SA: j = SA[Rank[i] - 1].',
      'Compare suffixes starting from offset h, increment h on match, store LCP[Rank[i]] = h, and decrement h = max(0, h-1).'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Genomic Sequence (T)', pat: 'Ignored for Kasai LCP' },
    outputDescription: 'LCP array values, rank array, and identification of the longest repeated genomic motif.',
    timeComplexity: 'O(n)',
    timeExplanation: 'The variable h decrements at most n times and can never exceed n, bounding total comparisons to 2n.',
    spaceComplexity: 'O(n)',
    spaceExplanation: 'Stores the LCP array and Rank array of size n.',
    defaultSeq: 'ATGCGATGACATGCG',
    defaultPat: '',
    defaultPreset: 'synthetic-tandem'
  },
  {
    id: 'suffix-tree',
    bioProblem: 'Compressed Radix Tree Genome Indexing',
    bioDomain: 'Genome Indexing & Search',
    dsaAlgorithm: 'Suffix Tree (Compressed Trie)',
    purpose: 'Compacts all suffixes of a genome into an edge-labeled trie where every branching path represents unique genomic factors, enabling instant motif queries.',
    bioContext: 'Suffix trees allow solving almost every substring problem—including maximal repeats, all-pairs shortest unique substrings, and multi-genome comparisons—in linear time.',
    dsaConnection: 'Compacts non-branching internal paths into edge labels represented as pointer intervals [start, end]. Every suffix corresponds to a path from root to leaf, consuming O(n) total nodes.',
    howItWorks: [
      'Insert all suffixes into a trie with terminal sentinel "$".',
      'Compact linear single-child paths into single edges labeled by their substring interval.',
      'Establish internal branching nodes whenever two or more suffixes diverge.',
      'Annotate leaves with their original 0-based suffix start positions.',
      'Query substrings by traversing matching edge labels from root in O(m) time.'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Genomic Sequence (T)', pat: 'Query Motif to Match' },
    outputDescription: 'Compact suffix tree nodes, edge substring intervals, and matching subtree leaves.',
    timeComplexity: 'O(n) Construction | O(m) Query',
    timeExplanation: 'Constructible in linear time; query time depends solely on pattern length m.',
    spaceComplexity: 'O(n · |Σ|)',
    spaceExplanation: 'At most 2n - 1 nodes with edge interval pointers.',
    defaultSeq: 'BANANA',
    defaultPat: 'ANA',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'suffix-automaton',
    bioProblem: 'Minimal Deterministic Substring DFA',
    bioDomain: 'Genome Indexing & Search',
    dsaAlgorithm: 'Suffix Automaton (DAWG)',
    purpose: 'Smallest directed acyclic word graph recognizing all factors of a genome with at most 2n-1 states and 3n-4 transitions for ultra-fast factor counting.',
    bioContext: 'Counting the number of distinct substrings in a chromosome or verifying factor membership can be done in O(1) per character with minimal memory using a Suffix Automaton.',
    dsaConnection: 'Groups substrings into right-equivalence classes (endpos sets). Forms a minimal deterministic finite automaton accepting all genomic factors in strictly linear states.',
    howItWorks: [
      'Initialize root state representing the empty string.',
      'Process characters of the genome sequentially one by one.',
      'Create a new state for the current prefix and link backward via suffix links.',
      'Redirect transitions and clone states when equivalence classes diverge.',
      'The resulting DAWG recognizes all genomic factors with minimal state count.'
    ],
    inputType: 'dna_and_pattern',
    inputLabels: { seq: 'Genomic Sequence (T)', pat: 'Factor to Verify' },
    outputDescription: 'Automaton state count, suffix link transitions, and distinct substring statistics.',
    timeComplexity: 'O(n) Linear Time',
    timeExplanation: 'Each appended character adds at most 2 states and updates amortized constant transitions.',
    spaceComplexity: 'O(n · |Σ|)',
    spaceExplanation: 'Guaranteed at most 2n - 1 states and 3n - 4 transitions.',
    defaultSeq: 'ATGC',
    defaultPat: 'TG',
    defaultPreset: 'ecoli-promoter'
  },

  // 4. Laboratory Networks & Resource Allocation
  {
    id: 'ford-fulkerson',
    bioProblem: 'Diagnostic Specimen Pipeline Max Flow',
    bioDomain: 'Laboratory Networks & Resource Allocation',
    dsaAlgorithm: 'Ford–Fulkerson Algorithm (DFS)',
    purpose: 'Computes maximum specimen transport capacity across a network of clinical diagnostic laboratories using augmenting paths found via depth-first search.',
    bioContext: 'During epidemiological outbreaks, clinical diagnostic centers collect patient swab samples that must be routed through testing laboratories with varying daily throughput limits.',
    dsaConnection: 'Maintains a residual graph of available transfer capacities. Uses DFS to discover augmenting paths from source clinic to sink reference lab, pushing bottleneck flow until no path exists.',
    howItWorks: [
      'Initialize all edge flows to 0.',
      'Use depth-first search (DFS) to locate an augmenting path with available residual capacity from source S to sink T.',
      'Identify the bottleneck capacity c_f(p) = min residual capacity along the discovered path.',
      'Augment flow along the path by +c_f(p) and update reverse edges with -c_f(p).',
      'Repeat until no augmenting path exists; total pushed flow is the maximum network throughput.'
    ],
    inputType: 'network_graph',
    inputLabels: { seq: 'Network Specification', pat: 'Ignored' },
    outputDescription: 'Maximum specimen flow rate, augmenting paths, and residual capacity table.',
    timeComplexity: 'O(E · |f*|)',
    timeExplanation: 'Depends on the total maximum flow value |f*| multiplied by edge count E.',
    spaceComplexity: 'O(V + E)',
    spaceExplanation: 'Stores the residual adjacency matrix and parent traversal pointers.',
    defaultSeq: 'Specimen Lab Network A',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'edmonds-karp',
    bioProblem: 'Cold-Chain Routing with Minimum Transfers',
    bioDomain: 'Laboratory Networks & Resource Allocation',
    dsaAlgorithm: 'Edmonds–Karp Algorithm (BFS)',
    purpose: 'Guarantees polynomial-time specimen routing by finding shortest augmenting paths (minimum transfer hops) to preserve cold-chain specimen viability.',
    bioContext: 'RNA and live viral specimens degrade with repeated airport or courier transfers. Edmonds-Karp prioritizes routes with the fewest transfer hops to ensure biological specimen integrity.',
    dsaConnection: 'Replaces DFS with Breadth-First Search (BFS) to find augmenting paths with the minimum number of edges, guaranteeing convergence in O(V · E²) independent of capacity magnitudes.',
    howItWorks: [
      'Use BFS starting from source S to find the shortest augmenting path in terms of edge count.',
      'Determine the bottleneck residual capacity along this shortest path.',
      'Augment flow along forward edges and decrease residual flow along reverse edges.',
      'Because BFS guarantees monotonic distance increases, at most O(V · E) augmentations occur.',
      'Terminates in polynomial time with optimal cold-chain throughput.'
    ],
    inputType: 'network_graph',
    inputLabels: { seq: 'Network Specification', pat: 'Ignored' },
    outputDescription: 'Maximum flow value, BFS shortest paths, and preserved cold-chain transfer hops.',
    timeComplexity: 'O(V · E²)',
    timeExplanation: 'Polynomial bound: at most O(VE) augmenting paths, each found via BFS in O(E) time.',
    spaceComplexity: 'O(V + E)',
    spaceExplanation: 'Adjacency list and BFS queue memory.',
    defaultSeq: 'Cold Chain Routing Network',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'dinic',
    bioProblem: 'High-Throughput Clinical Specimen Distribution',
    bioDomain: 'Laboratory Networks & Resource Allocation',
    dsaAlgorithm: 'Dinic’s Blocking Flow Algorithm',
    purpose: 'Solves large-scale specimen distribution networks efficiently using BFS level graphs and DFS blocking flows along admissible edges.',
    bioContext: 'National testing consortia route tens of thousands of diagnostic specimens across hundreds of laboratories. Dinic’s algorithm provides fast, scalable network flow calculation.',
    dsaConnection: 'Constructs a BFS level graph partitioning vertices by distance from source, then pushes multiple blocking flows using DFS along admissible edges (level[v] = level[u] + 1) in O(V² · E) time.',
    howItWorks: [
      'Construct a level graph from residual capacities using BFS, assigning level[v] to each node.',
      'If sink T is unreachable in the level graph, terminate.',
      'Find blocking flows in the layered DAG using DFS, advancing only along admissible edges.',
      'Retract dead-end edges to avoid redundant search.',
      'Repeat level graph reconstruction until no more flow can reach sink T.'
    ],
    inputType: 'network_graph',
    inputLabels: { seq: 'Network Specification', pat: 'Ignored' },
    outputDescription: 'Maximum flow rate, level graph layers, and blocking flow push sequences.',
    timeComplexity: 'O(V² · E)',
    timeExplanation: 'At most V level graph phases, each taking O(VE) time to find blocking flows.',
    spaceComplexity: 'O(V + E)',
    spaceExplanation: 'Maintains level array and edge pointer indices.',
    defaultSeq: 'High-Throughput Lab Network',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'bipartite-matching',
    bioProblem: 'Clinical Personnel & Assay Allocation',
    bioDomain: 'Laboratory Networks & Resource Allocation',
    dsaAlgorithm: 'Bipartite Maximum Matching (Hopcroft–Karp)',
    purpose: 'Pairs certified clinical researchers with authorized diagnostic assays using augmenting alternating paths to maximize simultaneous clinical trials.',
    bioContext: 'In certified diagnostic facilities, clinical scientists hold specific regulatory qualifications for certain assays (PCR, NGS, Flow Cytometry). Matching assigns each scientist to an authorized assay without conflict.',
    dsaConnection: 'Models scientists (L) and assays (R) as a bipartite graph. Finding maximum cardinality matching translates to max-flow or alternating path augmentation via Hopcroft-Karp in O(E √V) time.',
    howItWorks: [
      'Partition vertices into two disjoint sets: Certified Scientists (L) and Diagnostic Assays (R).',
      'Create edges representing qualification certifications.',
      'Search for alternating paths that alternate between unmatched and matched edges.',
      'Invert edges along augmenting paths to increase total matching cardinality by 1.',
      'Concludes when no augmenting path exists, yielding maximum operational allocations.'
    ],
    inputType: 'network_graph',
    inputLabels: { seq: 'Personnel & Assay Graph', pat: 'Ignored' },
    outputDescription: 'Maximum paired assignments, matching cardinality, and alternating path steps.',
    timeComplexity: 'O(E · √V)',
    timeExplanation: 'Hopcroft-Karp algorithm finds multiple shortest augmenting paths concurrently in O(E) per phase.',
    spaceComplexity: 'O(V + E)',
    spaceExplanation: 'Stores bipartite adjacency lists and matching arrays.',
    defaultSeq: 'Personnel Allocation Graph',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'konig-theorem',
    bioProblem: 'Resource Conflict Resolution & Duality',
    bioDomain: 'Laboratory Networks & Resource Allocation',
    dsaAlgorithm: 'König’s Theorem Duality',
    purpose: 'Demonstrates the fundamental duality in bipartite networks: the size of maximum matching equals the size of minimum vertex cover to resolve trial monitoring conflicts.',
    bioContext: 'To supervise all active diagnostic testing lanes with the fewest supervisors, König’s theorem identifies the exact minimum set of personnel who can monitor every active assay.',
    dsaConnection: 'Constructs alternating trees from unmatched vertices in L. Vertices reachable via alternating paths determine the minimum vertex cover: (L \\ Reachable) ∪ (R ∩ Reachable), proving |Max Matching| = |Min Vertex Cover|.',
    howItWorks: [
      'Compute a maximum bipartite matching M.',
      'Orient matched edges from R to L and unmatched edges from L to R.',
      'Perform BFS/DFS from unmatched vertices in L to mark all reachable vertices.',
      'Construct minimum vertex cover: take unvisited vertices in L and visited vertices in R.',
      'Verify that every edge in the graph has at least one endpoint in this cover set, with |Cover| = |Matching|.'
    ],
    inputType: 'network_graph',
    inputLabels: { seq: 'Bipartite Resource Graph', pat: 'Ignored' },
    outputDescription: 'Maximum matching set, minimum vertex cover set, and visual proof of equality.',
    timeComplexity: 'O(V · E)',
    timeExplanation: 'Matching computation followed by a single linear BFS/DFS reachability scan.',
    spaceComplexity: 'O(V + E)',
    spaceExplanation: 'Adjacency list and reachability mark arrays.',
    defaultSeq: 'Trial Monitoring Network',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'min-cut',
    bioProblem: 'Laboratory Pipeline Bottleneck Diagnosis',
    bioDomain: 'Laboratory Networks & Resource Allocation',
    dsaAlgorithm: 'Max-Flow Min-Cut Theorem',
    purpose: 'Discovers the exact critical transfer links whose capacity limits total specimen throughput across diagnostic centers, pinpointing investment priorities.',
    bioContext: 'When a laboratory network cannot process incoming patient samples fast enough, the Min-Cut theorem identifies the exact bottleneck transfer pipelines that are fully saturated.',
    dsaConnection: 'Partitions vertices into set S (reachable from source in the residual graph) and set T (unreachable). Edges crossing from S to T constitute the minimum capacity cut, with Capacity(S, T) = Value(Max Flow).',
    howItWorks: [
      'Compute the maximum flow across the specimen transport network.',
      'Construct the final residual graph where edges have remaining capacity c_f(u, v) > 0.',
      'Run BFS from source S to identify all reachable vertices; this forms partition S.',
      'All remaining vertices form partition T.',
      'Identify all forward edges from S to T in the original graph; their capacities sum exactly to the max flow.'
    ],
    inputType: 'network_graph',
    inputLabels: { seq: 'Specimen Network', pat: 'Ignored' },
    outputDescription: 'Partition sets S and T, bottleneck critical edges, and total bottleneck capacity.',
    timeComplexity: 'O(V + E) Post-Flow',
    timeExplanation: 'Single BFS reachability traversal after maximum flow has converged.',
    spaceComplexity: 'O(V + E)',
    spaceExplanation: 'Stores reachability marks and bottleneck edge list.',
    defaultSeq: 'Epidemiological Transport Network',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },

  // 5. Genetic Constraints & Regulatory Logic
  {
    id: '3-sat',
    bioProblem: 'Gene Regulatory Circuit Logic',
    bioDomain: 'Genetic Constraints & Regulatory Logic',
    dsaAlgorithm: 'DPLL 3-SAT Solver',
    purpose: 'Evaluates boolean satisfiability for 3-literal disjunctive clauses modeling gene regulatory circuits, transcription factor interactions, and multiplex primer compatibility.',
    bioContext: 'Synthetic gene circuits use activator and repressor proteins that act like boolean logic gates (AND, OR, NOT). Determining whether a stable expression state exists for a target phenotype is equivalent to Boolean Satisfiability (SAT).',
    dsaConnection: 'Uses the Davis-Putnam-Logemann-Loveland (DPLL) backtracking algorithm with unit propagation and pure literal elimination to solve 3-CNF formulas.',
    howItWorks: [
      'Parse the regulatory formula into 3-literal clauses: (l₁ ∨ l₂ ∨ l₃) ∧ ...',
      'Unit Propagation: If a clause contains only one unassigned literal, force its assignment to TRUE.',
      'Pure Literal Elimination: If a variable appears only with one polarity, assign it to satisfy all containing clauses.',
      'Branching: Choose an unassigned variable, hypothesize TRUE, and recursively evaluate.',
      'Backtrack upon conflict until a satisfying genetic expression state is discovered or proven impossible.'
    ],
    inputType: 'boolean_clauses',
    inputLabels: { seq: 'Regulatory Circuit Clauses (e.g. (x1|x2|x3), (!x1|!x2|x3))', pat: 'Ignored' },
    outputDescription: 'Satisfiability status (SAT / UNSAT), variable truth assignments, and DPLL decision tree.',
    timeComplexity: 'O(2^n) Worst-Case',
    timeExplanation: 'Exponential in the number of regulatory variables n, pruned via unit propagation heuristics.',
    spaceComplexity: 'O(n)',
    spaceExplanation: 'Recursion stack depth bounded by the number of regulatory variables.',
    defaultSeq: '(x1 | x2 | x3) & (!x1 | !x2 | x3) & (!x1 | x2 | !x3)',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'sat-to-clique',
    bioProblem: 'Regulatory Constraints to Cooperation',
    bioDomain: 'Genetic Constraints & Regulatory Logic',
    dsaAlgorithm: '3-SAT → CLIQUE Reduction',
    purpose: 'Transforms regulatory logic constraints into vertex clusters and compatibility edges, where a k-clique proves the existence of a viable cooperative regulatory state.',
    bioContext: 'Demonstrates polynomial-time reductions in computational biology: verifying mutual cooperation among genetic components by converting logic constraints into graph cliques.',
    dsaConnection: 'Karp reduction: each clause of 3 literals becomes a cluster of 3 vertices. Edges connect vertices in different clusters that are logically compatible (not x and ¬x). A k-clique exists iff the formula is satisfiable.',
    howItWorks: [
      'For each clause C_i = (l_{i,1} ∨ l_{i,2} ∨ l_{i,3}), construct a triangular cluster of 3 vertices.',
      'Draw an edge between vertex u in cluster i and vertex v in cluster j (i ≠ j) iff they are mutually consistent (u ≠ ¬v).',
      'Never place edges between vertices in the same clause cluster.',
      'A clique of size k (where k is the number of clauses) requires selecting one compatible literal from every clause.',
      'The k-clique directly certifies a valid satisfying truth assignment for the regulatory circuit.'
    ],
    inputType: 'boolean_clauses',
    inputLabels: { seq: '3-CNF Clauses', pat: 'Target k Clauses' },
    outputDescription: 'Constructed vertex clusters, compatibility edges, and k-clique certification.',
    timeComplexity: 'O(k²) Reduction Time',
    timeExplanation: 'Polynomial time: for k clauses, evaluates compatibility between at most 3k pairs.',
    spaceComplexity: 'O(k²)',
    spaceExplanation: 'Constructed graph has 3k vertices and at most 9k(k-1)/2 edges.',
    defaultSeq: '(x1 | x2 | x3) & (!x1 | !x2 | x3)',
    defaultPat: '2',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'clique-to-is',
    bioProblem: 'Conflict-Free Biomarker Panel Selection',
    bioDomain: 'Genetic Constraints & Regulatory Logic',
    dsaAlgorithm: 'CLIQUE → Independent Set',
    purpose: 'Transforms cooperative clusters into conflict-free biomarker panels using graph complementation duality.',
    bioContext: 'Selecting diagnostic biomarkers requires choosing markers that do not cross-react or interfere with each other in multiplex PCR assays, corresponding to an Independent Set in an interference graph.',
    dsaConnection: 'Graph Complementation Duality: a subset of vertices S is a clique in graph G if and only if S is an independent set in the complement graph G\' (where edges and non-edges are inverted).',
    howItWorks: [
      'Given an interaction graph G = (V, E) where edges represent compatibility.',
      'Construct the complement graph G\' = (V, E\') containing all edges not present in G.',
      'A set of k vertices mutually connected in G (Clique) has zero edges between them in G\' (Independent Set).',
      'The independent set in G\' defines a non-conflicting, interference-free biomarker panel.',
      'Proves equivalence between cooperative and conflict-free optimization problems.'
    ],
    inputType: 'graph_reduction',
    inputLabels: { seq: 'Graph Specification', pat: 'Target k' },
    outputDescription: 'Original graph G, complement graph G\', and dual independent set.',
    timeComplexity: 'O(V²) Reduction Time',
    timeExplanation: 'Inverts adjacency matrix in quadratic time proportional to vertex pairs.',
    spaceComplexity: 'O(V²)',
    spaceExplanation: 'Stores the complement adjacency matrix.',
    defaultSeq: 'Biomarker Compatibility Graph',
    defaultPat: '3',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'is-to-vc',
    bioProblem: 'Interaction Network Sensor Coverage',
    bioDomain: 'Genetic Constraints & Regulatory Logic',
    dsaAlgorithm: 'Independent Set → Vertex Cover',
    purpose: 'Visualizes Gallai’s subset duality: in any graph, S is an independent set iff V \\ S is a vertex cover monitoring all cellular interactions.',
    bioContext: 'To monitor every protein interaction in a cellular pathway with minimal fluorescent biosensors, placing sensors on a vertex cover guarantees every interaction edge is observed.',
    dsaConnection: 'Gallai’s Theorem: |Independent Set| + |Vertex Cover| = |V|. Complementing an independent set directly yields a valid vertex cover of size |V| - k in linear O(V) time.',
    howItWorks: [
      'Identify an independent set S of size k (no two vertices in S share an edge).',
      'Construct complement subset C = V \\ S.',
      'Since no edge connects two vertices in S, every edge in G must touch at least one vertex outside S (in C).',
      'Therefore, C is a guaranteed vertex cover of size |V| - k.',
      'Demonstrates the exact complementary relationship between non-interacting and monitoring sets.'
    ],
    inputType: 'graph_reduction',
    inputLabels: { seq: 'Cellular Interaction Graph', pat: 'Target k' },
    outputDescription: 'Independent set S, dual vertex cover C = V \\ S, and Gallai identity verification.',
    timeComplexity: 'O(V) Reduction Time',
    timeExplanation: 'Linear set subtraction V \\ S takes O(V) time.',
    spaceComplexity: 'O(V)',
    spaceExplanation: 'Requires storing membership flags for vertices.',
    defaultSeq: 'Protein Interaction Graph',
    defaultPat: '2',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'vc-approx',
    bioProblem: 'Fast Interaction Network Monitoring',
    bioDomain: 'Genetic Constraints & Regulatory Logic',
    dsaAlgorithm: 'Vertex Cover 2-Approximation',
    purpose: 'Computes a polynomial-time 2-approximation for minimum monitoring sensors across complex protein-protein interaction networks.',
    bioContext: 'Minimum vertex cover is NP-hard, making exact solutions intractable on large whole-cell interactome graphs (tens of thousands of protein interactions). A 2-approximation provides a provably near-optimal sensor configuration in linear time.',
    dsaConnection: 'Finds a maximal matching by greedily selecting uncovered edges and adding both endpoints to the cover. Because any valid cover must pick at least one endpoint from each disjoint matching edge, |Approx Cover| ≤ 2 · |Optimal Cover|.',
    howItWorks: [
      'Initialize Cover C = ∅ and copy edge set E\' = E.',
      'While E\' is non-empty, pick an arbitrary uncovered edge (u, v).',
      'Add both endpoints u and v to cover C.',
      'Remove all edges incident to either u or v from E\'.',
      'Return cover C with the mathematical guarantee that |C| ≤ 2 · |C*|.'
    ],
    inputType: 'graph_reduction',
    inputLabels: { seq: 'Interaction Network', pat: 'Ignored' },
    outputDescription: 'Approximate sensor cover, maximal matching edges, and 2× approximation bound verification.',
    timeComplexity: 'O(V + E)',
    timeExplanation: 'Linear time: visits each vertex and edge at most once during maximal matching.',
    spaceComplexity: 'O(V + E)',
    spaceExplanation: 'Adjacency list and edge selection flags.',
    defaultSeq: 'Signaling Network Graph',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },

  // 6. High-Throughput Sequencing Analytics
  {
    id: 'random-qsort',
    bioProblem: 'Genomic Read Library Sorting',
    bioDomain: 'High-Throughput Sequencing Analytics',
    dsaAlgorithm: 'Randomized QuickSort',
    purpose: 'Sorts millions of high-throughput sequencing records (by chromosomal coordinate or GC-content) with expected O(n log n) runtime, avoiding quadratic worst-case slowdowns.',
    bioContext: 'Next-generation sequencers output millions of short read records. Pre-sorted or repetitive genomic regions cause deterministic quicksort to degenerate to O(n²); randomized pivot selection guarantees balanced partitions.',
    dsaConnection: 'Picks a uniform random pivot at each recursive stage. The expected depth of recursion is bounded to 2 ln(n), providing guaranteed expected O(n log n) comparisons on any input sequence.',
    howItWorks: [
      'Given an array of genomic read metrics (e.g. read quality scores or GC-percentages).',
      'Randomly select an index between low and high as the partition pivot.',
      'Swap the chosen pivot to the end and partition array into elements smaller and larger than the pivot.',
      'Recursively sort the left and right subarrays.',
      'Produces a fully sorted genomic record library in expected O(n log n) time.'
    ],
    inputType: 'numeric_array',
    inputLabels: { seq: 'Read Metrics (e.g. 52, 14, 88, 34, 62, 28, 91, 45)', pat: 'Ignored' },
    outputDescription: 'Sorted genomic records, partition recursion tree, and pivot choices.',
    timeComplexity: 'O(n log n) Expected',
    timeExplanation: 'Random pivots prevent adversarial worst-case partitioning, guaranteeing expected logarithmic depth.',
    spaceComplexity: 'O(log n)',
    spaceExplanation: 'Recursion stack frame memory.',
    defaultSeq: '52, 14, 88, 34, 62, 28, 91, 45',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'reservoir',
    bioProblem: 'Flow Cytometer Stream Sampling',
    bioDomain: 'High-Throughput Sequencing Analytics',
    dsaAlgorithm: 'Reservoir Sampling (Algorithm R)',
    purpose: 'Maintains an unbiased random sample of k single-cell records from continuous flow cytometer streams of unknown total volume in a single pass.',
    bioContext: 'Flow cytometers inspect hundreds of thousands of single cells per second in real time. Because the total stream length is unknown in advance, reservoir sampling maintains a statistically uniform representative sample in memory.',
    dsaConnection: 'Algorithm R: stores the first k items. For each subsequent i-th item (i > k), picks random integer j ∈ [0, i-1]. If j < k, replaces reservoir[j] with the current item, ensuring every streamed item has equal inclusion probability k/N.',
    howItWorks: [
      'Allocate memory buffer (reservoir) of size k.',
      'Fill reservoir with the first k single-cell reads from the sequencer stream.',
      'For each subsequent read at position i (i = k+1, k+2, ...):',
      'Generate random integer j between 1 and i.',
      'If j ≤ k, replace reservoir[j-1] with the current read; otherwise discard it.'
    ],
    inputType: 'streaming_data',
    inputLabels: { seq: 'Streamed Read Identifiers (e.g. Cell_1, Cell_2, ..., Cell_12)', pat: 'Reservoir Size k (e.g. 4)' },
    outputDescription: 'Final unbiased reservoir sample, replacement decisions, and mathematical inclusion probability.',
    timeComplexity: 'O(N)',
    timeExplanation: 'Single linear pass over the data stream, processing each item in O(1) time.',
    spaceComplexity: 'O(k)',
    spaceExplanation: 'Fixed memory footprint proportional to buffer size k, independent of total stream length N.',
    defaultSeq: 'Cell_1, Cell_2, Cell_3, Cell_4, Cell_5, Cell_6, Cell_7, Cell_8, Cell_9, Cell_10',
    defaultPat: '4',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'miller-rabin',
    bioProblem: 'Cryptogenomic Key Certification',
    bioDomain: 'High-Throughput Sequencing Analytics',
    dsaAlgorithm: 'Miller–Rabin Primality Test',
    purpose: 'Demonstrates randomized computational testing with modular exponentiation witnesses to verify large prime keys used in genomic privacy encryption.',
    bioContext: 'Patient genomic sequences constitute sensitive identifiable medical data. Cryptographic schemes protecting electronic health records rely on certified large prime numbers.',
    dsaConnection: 'Based on Fermat’s Little Theorem and square roots of unity modulo prime. Testing k random bases achieves error probability at most (1/4)^k, providing near-certain primality certification.',
    howItWorks: [
      'Write n - 1 = 2^s · d where d is odd.',
      'Choose a random base witness a ∈ [2, n-2].',
      'Compute x = a^d mod n using fast modular exponentiation.',
      'If x == 1 or x == n - 1, the test passes for this witness.',
      'Square x repeatedly s - 1 times; if x == n - 1, test passes; otherwise n is certified composite.'
    ],
    inputType: 'numeric_array',
    inputLabels: { seq: 'Candidate Integer n (e.g. 104729)', pat: 'Witness Rounds k (e.g. 5)' },
    outputDescription: 'Primality verdict (Composite / Probable Prime), witness trials, and error probability bound.',
    timeComplexity: 'O(k · log³ n)',
    timeExplanation: 'k rounds of modular exponentiation with cubic bit-complexity.',
    spaceComplexity: 'O(log n)',
    spaceExplanation: 'Stores multi-precision integer operands.',
    defaultSeq: '104729',
    defaultPat: '5',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'blelloch-scan',
    bioProblem: 'Chromosomal Coverage Prefix Scan',
    bioDomain: 'High-Throughput Sequencing Analytics',
    dsaAlgorithm: 'Blelloch Parallel Prefix Scan',
    purpose: 'Computes exclusive prefix sums of chromosomal read coverage counts across processor cores in logarithmic parallel span using up-sweep and down-sweep trees.',
    bioContext: 'To calculate cumulative sequencing coverage or find genomic regions with coverage above a threshold, prefix sums allow instant range queries in O(1) time after parallel precomputation.',
    dsaConnection: 'Work-efficient parallel algorithm: Up-Sweep (reduce) tree computes partial sums in O(log n) span, followed by Down-Sweep tree distributing prefix sums, executing O(n) total work in O(log n) parallel steps.',
    howItWorks: [
      'Up-Sweep Phase: Pairwise sum adjacent elements in a binary tree from leaves to root.',
      'Root holds the total sum of all chromosomal coverage values.',
      'Set root value to 0 to begin exclusive prefix scan.',
      'Down-Sweep Phase: For each node, pass left child value to right child, and set left child to parent value.',
      'Leaves now hold the exact exclusive prefix sums in O(log n) parallel time steps.'
    ],
    inputType: 'numeric_array',
    inputLabels: { seq: 'Coverage Array (e.g. 3, 1, 7, 0, 4, 1, 6, 3)', pat: 'Ignored' },
    outputDescription: 'Exclusive prefix sum array, up-sweep tree hierarchy, and down-sweep distribution values.',
    timeComplexity: 'O(n) Work | O(log n) Span',
    timeExplanation: 'Work is linear O(n) operations; parallel depth (span) is logarithmic O(log n).',
    spaceComplexity: 'O(n)',
    spaceExplanation: 'In-place binary tree array representation.',
    defaultSeq: '3, 1, 7, 0, 4, 1, 6, 3',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'parallel-reduce',
    bioProblem: 'Parallel Quality Metric Reduction',
    bioDomain: 'High-Throughput Sequencing Analytics',
    dsaAlgorithm: 'Parallel Tree Reduction',
    purpose: 'Aggregates genomic read quality scores across multiple CPU/GPU cores in logarithmic parallel time steps using divide-and-conquer binary trees.',
    bioContext: 'Quality control pipelines must calculate average Phred quality scores and GC-percentage across billions of sequencing reads. Parallel reduction aggregates metrics across compute clusters with minimal latency.',
    dsaConnection: 'Uses an associative binary operator (sum, max, min). Each parallel step pairs elements and combines them concurrently, reducing n elements to a single scalar in log₂(n) parallel steps.',
    howItWorks: [
      'Distribute the array of genomic quality values across worker threads.',
      'In step 1, worker i combines elements array[2i] and array[2i + 1].',
      'Halve the active element count at each subsequent tree depth.',
      'Repeat for log₂(n) parallel rounds.',
      'The root of the tree contains the final aggregated metric.'
    ],
    inputType: 'numeric_array',
    inputLabels: { seq: 'Metric Values (e.g. 32, 28, 41, 35, 19, 44, 27, 39)', pat: 'Ignored' },
    outputDescription: 'Parallel tree reduction stages, intermediate pair combinations, and total sum.',
    timeComplexity: 'O(n) Work | O(log n) Span',
    timeExplanation: 'Total operations equal n - 1 (work O(n)), completed in log₂(n) parallel time rounds.',
    spaceComplexity: 'O(n)',
    spaceExplanation: 'Requires storage for tree level buffers.',
    defaultSeq: '32, 28, 41, 35, 19, 44, 27, 39',
    defaultPat: '',
    defaultPreset: 'ecoli-promoter'
  },
  {
    id: 'brent-theorem',
    bioProblem: 'Multi-Core Pipeline Speedup Bounds',
    bioDomain: 'High-Throughput Sequencing Analytics',
    dsaAlgorithm: 'Brent’s Theorem Calculator',
    purpose: 'Interactive theoretical model calculating the bounded parallel runtime and speedup of genomic pipelines: T_p ≤ (T₁ - T_∞)/p + T_∞.',
    bioContext: 'Bioinformatics researchers must determine how many processor cores to provision on cloud clusters for genome alignment before diminishing returns occur according to Amdahl’s law.',
    dsaConnection: 'Brent’s Scheduling Theorem provides a formal upper bound on the execution time of any parallel algorithm on p processors with total work T₁ and critical path span T_∞.',
    howItWorks: [
      'Input total work T₁ (total operations on a single processor).',
      'Input critical path span T_∞ (minimum time with infinite processors).',
      'Input available processor core count p.',
      'Calculate bounded parallel execution time: T_p ≤ (T₁ - T_∞)/p + T_∞.',
      'Compute theoretical speedup S_p = T₁ / T_p and parallel efficiency E_p = S_p / p.'
    ],
    inputType: 'brent_inputs',
    inputLabels: { work: 'Total Work T₁', span: 'Critical Path Span T_∞', cores: 'Processor Cores p' },
    outputDescription: 'Bounded parallel runtime T_p, speedup ratio, and parallel efficiency curve.',
    timeComplexity: 'Analytical Closed Formula O(1)',
    timeExplanation: 'Direct algebraic calculation using Brent’s inequality.',
    spaceComplexity: 'O(1)',
    spaceExplanation: 'No data structures required.',
    defaultSeq: '1000',
    defaultPat: '20',
    defaultPreset: 'ecoli-promoter'
  }
];

// Main App Controller
class AppController {
  constructor() {
    this.currentTab = 'tab-home';
    this.selectedAlgoId = 'kmp';
    this.activeEngine = null;
    this.currentCategoryFilter = 'all';
  }

  init() {
    this.initNavigation();
    this.initHomeTasks();
    this.initExplorer();
    this.initComparisonLab();
    this.initTheme();

    // Default to Home tab
    this.switchTab('tab-home');
  }

  // Navigation management
  initNavigation() {
    document.querySelectorAll('.nav-item').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const target = link.getAttribute('data-target');
        this.switchTab(target);
      });
    });

    const heroExploreBtn = document.getElementById('hero-explore-btn');
    if (heroExploreBtn) {
      heroExploreBtn.addEventListener('click', () => {
        this.switchTab('tab-explorer');
      });
    }

    const heroHowBtn = document.getElementById('hero-how-btn');
    if (heroHowBtn) {
      heroHowBtn.addEventListener('click', () => {
        this.switchTab('tab-how-it-works');
      });
    }
  }

  initTheme() {
    const themeBtn = document.getElementById('theme-toggle-btn');
    if (themeBtn) {
      themeBtn.addEventListener('click', () => {
        const current = document.documentElement.getAttribute('data-theme');
        const next = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        themeBtn.innerHTML = next === 'dark' ? '☀️' : '🌙';
        themeBtn.setAttribute('title', next === 'dark' ? 'Switch to warm light theme' : 'Switch to scientific dark theme');
      });
    }
  }

  switchTab(tabId, targetAlgoId = null) {
    if (this.activeEngine) {
      this.activeEngine.pause();
    }

    this.currentTab = tabId;

    document.querySelectorAll('.nav-item').forEach(link => {
      if (link.getAttribute('data-target') === tabId) link.classList.add('active');
      else link.classList.remove('active');
    });

    document.querySelectorAll('.tab-section').forEach(sec => {
      if (sec.id === tabId) sec.classList.add('active');
      else sec.classList.remove('active');
    });

    window.scrollTo({ top: 0, behavior: 'smooth' });

    if (targetAlgoId) {
      this.selectAlgorithm(targetAlgoId);
    }
  }

  // 1. Home Page: Render Curated Biological Tasks
  initHomeTasks() {
    const grid = document.getElementById('home-tasks-grid');
    if (!grid) return;

    // Curated high-impact biological use cases for the landing page
    const homeAlgos = [
      'kmp',             // DNA Sequence Matching
      'aho-corasick',    // Multiple Sequence Pattern Search
      'levenshtein',     // Mutational Edit Distance
      'needleman-wunsch',// Global Sequence Homology
      'suffix-array',    // Whole-Genome Indexing & Binary Search
      'dinic',           // Diagnostic Specimen Flow
      '3-sat',           // Gene Regulatory Circuit Logic
      'reservoir'        // Flow Cytometer Stream Sampling
    ];

    const tasks = ALGORITHM_REGISTRY.filter(a => homeAlgos.includes(a.id));

    grid.innerHTML = tasks.map(task => `
      <div class="task-card" data-algo-id="${task.id}">
        <div class="task-header">
          <div class="task-icon">🧬</div>
          <span class="task-dsa-badge">${task.dsaAlgorithm.split('(')[1] ? task.dsaAlgorithm.split('(')[1].replace(')', '') : task.dsaAlgorithm}</span>
        </div>
        <h3 class="task-title">${task.bioProblem}</h3>
        <p class="task-desc">${task.purpose}</p>
        <div class="task-footer">
          <span class="task-complexity">${task.timeComplexity}</span>
          <span class="task-link">Explore Task ➔</span>
        </div>
      </div>
    `).join('');

    grid.querySelectorAll('.task-card').forEach(card => {
      card.addEventListener('click', () => {
        const id = card.getAttribute('data-algo-id');
        this.switchTab('tab-explorer', id);
      });
    });
  }

  // 2. Algorithm Explorer Workspace
  initExplorer() {
    this.renderCategoryChips();
    this.populateAlgorithmDropdown();
    this.initExplorerEvents();

    // Load initial algorithm
    this.selectAlgorithm(this.selectedAlgoId);
  }

  renderCategoryChips() {
    const bar = document.getElementById('explorer-category-chips');
    if (!bar) return;

    const categories = [
      { id: 'all', label: 'All Biological Problems' },
      { id: 'Sequence Pattern Matching', label: '🧬 Sequence Pattern Matching' },
      { id: 'Multi-Pattern Screening', label: '🔬 Multi-Pattern Screening' },
      { id: 'Sequence Alignment & Similarity', label: '📐 Alignment & Similarity' },
      { id: 'Genome Indexing & Search', label: '📚 Genome Indexing' },
      { id: 'Laboratory Networks & Resource Allocation', label: '🕸️ Networks & Resource Allocation' },
      { id: 'Genetic Constraints & Regulatory Logic', label: '🧠 Genetic Regulatory Logic' },
      { id: 'High-Throughput Sequencing Analytics', label: '⚡ Sequencing Analytics' }
    ];

    bar.innerHTML = categories.map(c => `
      <button class="category-chip ${c.id === this.currentCategoryFilter ? 'active' : ''}" data-cat="${c.id}">
        ${c.label}
      </button>
    `).join('');

    bar.querySelectorAll('.category-chip').forEach(btn => {
      btn.addEventListener('click', () => {
        this.currentCategoryFilter = btn.getAttribute('data-cat');
        bar.querySelectorAll('.category-chip').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        this.populateAlgorithmDropdown();
      });
    });
  }

  populateAlgorithmDropdown() {
    const select = document.getElementById('explorer-algo-select');
    if (!select) return;

    let items = ALGORITHM_REGISTRY;
    if (this.currentCategoryFilter !== 'all') {
      items = items.filter(a => a.bioDomain === this.currentCategoryFilter);
    }

    select.innerHTML = items.map(algo => `
      <option value="${algo.id}" ${algo.id === this.selectedAlgoId ? 'selected' : ''}>
        ${algo.bioProblem} — [${algo.dsaAlgorithm}]
      </option>
    `).join('');

    // If current selected algo is not in filtered list, pick the first
    if (!items.some(a => a.id === this.selectedAlgoId) && items.length > 0) {
      this.selectAlgorithm(items[0].id);
    }
  }

  initExplorerEvents() {
    const select = document.getElementById('explorer-algo-select');
    if (select) {
      select.addEventListener('change', (e) => {
        this.selectAlgorithm(e.target.value);
      });
    }

    const runBtn = document.getElementById('explorer-run-btn');
    if (runBtn) {
      runBtn.addEventListener('click', () => {
        this.executeAlgorithm(false);
      });
    }

    const simToggleBtn = document.getElementById('explorer-sim-toggle-btn');
    if (simToggleBtn) {
      simToggleBtn.addEventListener('click', () => {
        this.executeAlgorithm(true);
      });
    }

    const presetSelect = document.getElementById('explorer-preset-select');
    if (presetSelect && window.EDUCATIONAL_DATASETS) {
      presetSelect.addEventListener('change', (e) => {
        this.loadPresetData(e.target.value);
      });
    }
  }

  selectAlgorithm(algoId) {
    const algo = ALGORITHM_REGISTRY.find(a => a.id === algoId);
    if (!algo) return;

    this.selectedAlgoId = algoId;

    // Sync dropdown
    const select = document.getElementById('explorer-algo-select');
    if (select && select.value !== algoId) {
      select.value = algoId;
    }

    // Step 2: Render Algorithm Meta Card (Biological Problem, DSA Algorithm, Purpose, Context, Steps, Complexity)
    const titleEl = document.getElementById('algo-display-title');
    const dsaEl = document.getElementById('algo-dsa-name');
    const domainBadge = document.getElementById('algo-domain-badge');
    const purposeEl = document.getElementById('algo-purpose-text');
    const contextEl = document.getElementById('algo-context-text');
    const dsaConnEl = document.getElementById('algo-dsa-connection-text');
    const stepsListEl = document.getElementById('algo-steps-list');
    const timeValEl = document.getElementById('algo-time-complexity');
    const timeExpEl = document.getElementById('algo-time-explanation');
    const spaceValEl = document.getElementById('algo-space-complexity');
    const spaceExpEl = document.getElementById('algo-space-explanation');
    const inputDescEl = document.getElementById('algo-input-desc');
    const outputDescEl = document.getElementById('algo-output-desc');

    if (titleEl) titleEl.textContent = algo.bioProblem;
    if (dsaEl) dsaEl.textContent = algo.dsaAlgorithm;
    if (domainBadge) domainBadge.textContent = algo.bioDomain;
    if (purposeEl) purposeEl.textContent = algo.purpose;
    if (contextEl) contextEl.textContent = algo.bioContext;
    if (dsaConnEl) dsaConnEl.textContent = algo.dsaConnection;
    if (timeValEl) timeValEl.textContent = algo.timeComplexity;
    if (timeExpEl) timeExpEl.textContent = algo.timeExplanation;
    if (spaceValEl) spaceValEl.textContent = algo.spaceComplexity;
    if (spaceExpEl) spaceExpEl.textContent = algo.spaceExplanation;
    if (inputDescEl) inputDescEl.textContent = algo.inputDescription;
    if (outputDescEl) outputDescEl.textContent = algo.outputDescription;

    if (stepsListEl) {
      stepsListEl.innerHTML = algo.howItWorks.map(step => `<li>${step}</li>`).join('');
    }

    // Step 3: Render Adaptive Inputs
    this.renderAdaptiveInputs(algo);

    // Populate educational presets
    this.populatePresets(algo);

    // Clear previous results or run default
    this.executeAlgorithm(false);
  }

  renderAdaptiveInputs(algo) {
    const container = document.getElementById('adaptive-inputs-container');
    if (!container) return;

    if (algo.inputType === 'dna_and_pattern' || algo.inputType === 'dna_and_multi_patterns') {
      container.innerHTML = `
        <div class="form-grid">
          <div class="form-group">
            <label class="form-label">
              <span>${algo.inputLabels.seq}</span>
              <span class="form-label-hint">Valid nucleotides: A, T, G, C</span>
            </label>
            <input type="text" id="input-seq-1" class="form-input mono" value="${algo.defaultSeq}" placeholder="e.g. ATGCGATGACATGCG">
          </div>
          <div class="form-group">
            <label class="form-label">
              <span>${algo.inputLabels.pat}</span>
              <span class="form-label-hint">Target search motif</span>
            </label>
            <input type="text" id="input-seq-2" class="form-input mono" value="${algo.defaultPat}" placeholder="e.g. ATG">
          </div>
        </div>
      `;
    } else if (algo.inputType === 'two_dna_sequences') {
      container.innerHTML = `
        <div class="form-grid" style="grid-template-columns: 1fr 1fr;">
          <div class="form-group">
            <label class="form-label">
              <span>${algo.inputLabels.seq1}</span>
              <span class="form-label-hint">Sequence 1 (A, T, G, C)</span>
            </label>
            <input type="text" id="input-seq-1" class="form-input mono" value="${algo.defaultSeq1 || 'GATTACA'}" placeholder="e.g. GATTACA">
          </div>
          <div class="form-group">
            <label class="form-label">
              <span>${algo.inputLabels.seq2}</span>
              <span class="form-label-hint">Sequence 2 (A, T, G, C)</span>
            </label>
            <input type="text" id="input-seq-2" class="form-input mono" value="${algo.defaultSeq2 || 'GCATGCT'}" placeholder="e.g. GCATGCT">
          </div>
        </div>
      `;
    } else if (algo.inputType === 'brent_inputs') {
      container.innerHTML = `
        <div class="form-grid" style="grid-template-columns: repeat(3, 1fr);">
          <div class="form-group">
            <label class="form-label"><span>${algo.inputLabels.work}</span></label>
            <input type="number" id="input-brent-work" class="form-input mono" value="1000" min="10" max="100000">
          </div>
          <div class="form-group">
            <label class="form-label"><span>${algo.inputLabels.span}</span></label>
            <input type="number" id="input-brent-span" class="form-input mono" value="20" min="1" max="1000">
          </div>
          <div class="form-group">
            <label class="form-label"><span>${algo.inputLabels.cores}</span></label>
            <input type="number" id="input-brent-cores" class="form-input mono" value="8" min="1" max="128">
          </div>
        </div>
      `;
    } else {
      // General single input / array / formula input
      container.innerHTML = `
        <div class="form-grid">
          <div class="form-group">
            <label class="form-label">
              <span>${algo.inputLabels.seq}</span>
            </label>
            <input type="text" id="input-seq-1" class="form-input mono" value="${algo.defaultSeq}" placeholder="Enter values">
          </div>
          ${algo.inputLabels.pat && algo.inputLabels.pat !== 'Ignored' ? `
            <div class="form-group">
              <label class="form-label"><span>${algo.inputLabels.pat}</span></label>
              <input type="text" id="input-seq-2" class="form-input mono" value="${algo.defaultPat}" placeholder="Parameter">
            </div>
          ` : ''}
        </div>
      `;
    }
  }

  populatePresets(algo) {
    const presetSelect = document.getElementById('explorer-preset-select');
    if (!presetSelect || !window.EDUCATIONAL_DATASETS) return;

    if (algo.inputType === 'two_dna_sequences') {
      presetSelect.innerHTML = '<option value="">-- Choose Homolog / Variant Pair --</option>' +
        window.EDUCATIONAL_DATASETS.alignmentPairs.map(p => `
          <option value="${p.id}">${p.name}</option>
        `).join('');
    } else if (algo.id === 'aho-corasick') {
      presetSelect.innerHTML = '<option value="">-- Choose Multi-Motif Callset --</option>' +
        window.EDUCATIONAL_DATASETS.multiMotifs.map(m => `
          <option value="${m.id}">${m.name}</option>
        `).join('');
    } else {
      presetSelect.innerHTML = '<option value="">-- Choose Biological DNA Preset --</option>' +
        window.EDUCATIONAL_DATASETS.dna.map(d => `
          <option value="${d.id}">[${d.category}] ${d.name}</option>
        `).join('');
    }
  }

  loadPresetData(presetId) {
    if (!presetId || !window.EDUCATIONAL_DATASETS) return;

    const algo = ALGORITHM_REGISTRY.find(a => a.id === this.selectedAlgoId);
    if (!algo) return;

    if (algo.inputType === 'two_dna_sequences') {
      const pair = window.EDUCATIONAL_DATASETS.alignmentPairs.find(p => p.id === presetId);
      if (pair) {
        const inp1 = document.getElementById('input-seq-1');
        const inp2 = document.getElementById('input-seq-2');
        if (inp1) inp1.value = pair.seqA;
        if (inp2) inp2.value = pair.seqB_dna || pair.seqB;
        this.executeAlgorithm(false);
      }
    } else if (algo.id === 'aho-corasick') {
      const item = window.EDUCATIONAL_DATASETS.multiMotifs.find(m => m.id === presetId);
      if (item) {
        const inp1 = document.getElementById('input-seq-1');
        const inp2 = document.getElementById('input-seq-2');
        if (inp1) inp1.value = item.sequence;
        if (inp2) inp2.value = item.patterns.join(', ');
        this.executeAlgorithm(false);
      }
    } else {
      const dna = window.EDUCATIONAL_DATASETS.dna.find(d => d.id === presetId);
      if (dna) {
        const inp1 = document.getElementById('input-seq-1');
        const inp2 = document.getElementById('input-seq-2');
        if (inp1) inp1.value = dna.sequence;
        if (inp2 && dna.defaultPattern) inp2.value = dna.defaultPattern;
        this.executeAlgorithm(false);
      }
    }
  }

  // DNA Input Validator
  validateDna(seq) {
    const cleaned = (seq || '').trim().toUpperCase();
    if (!cleaned) return { valid: false, error: 'Please enter a biological sequence.' };
    const invalid = cleaned.split('').find(c => !['A', 'T', 'G', 'C'].includes(c));
    if (invalid) {
      return {
        valid: false,
        error: `Sequence contains non-nucleotide character '${invalid}'. Please enter valid A, T, G, C characters.`
      };
    }
    return { valid: true, sequence: cleaned };
  }

  // Step 4 & 5: Execute Algorithm & Render Clean Results Card
  executeAlgorithm(launchPlayer = false) {
    const algo = ALGORITHM_REGISTRY.find(a => a.id === this.selectedAlgoId);
    if (!algo) return;

    const errorEl = document.getElementById('explorer-validation-error');
    if (errorEl) errorEl.textContent = '';

    const inp1 = document.getElementById('input-seq-1');
    const inp2 = document.getElementById('input-seq-2');

    let val1 = inp1 ? inp1.value.trim() : '';
    let val2 = inp2 ? inp2.value.trim() : '';

    // Validate DNA sequences if applicable
    if (['kmp', 'z-alg', 'rabin-karp', 'suffix-array', 'sais', 'kasai'].includes(algo.id)) {
      const check1 = this.validateDna(val1);
      if (!check1.valid) {
        if (errorEl) errorEl.textContent = check1.error;
        return;
      }
      val1 = check1.sequence;

      if (['kmp', 'z-alg', 'rabin-karp', 'suffix-array'].includes(algo.id) && val2) {
        const check2 = this.validateDna(val2);
        if (!check2.valid) {
          if (errorEl) errorEl.textContent = check2.error;
          return;
        }
        val2 = check2.sequence;
      }
    }

    if (['levenshtein', 'damerau', 'needleman-wunsch', 'smith-waterman'].includes(algo.id)) {
      const check1 = this.validateDna(val1);
      const check2 = this.validateDna(val2);
      if (!check1.valid) { if (errorEl) errorEl.textContent = check1.error; return; }
      if (!check2.valid) { if (errorEl) errorEl.textContent = check2.error; return; }
      val1 = check1.sequence;
      val2 = check2.sequence;
    }

    // Generate real algorithm states from our simulation libraries
    let states = [];
    const t0 = performance.now();

    // Default data structures for networks & reductions
    const defaultNetwork = {
      nodes: ['Clinic_A', 'Transit_1', 'Transit_2', 'Central_Lab'],
      edges: [
        { u: 'Clinic_A', v: 'Transit_1', cap: 10 },
        { u: 'Clinic_A', v: 'Transit_2', cap: 5 },
        { u: 'Transit_1', v: 'Transit_2', cap: 15 },
        { u: 'Transit_1', v: 'Central_Lab', cap: 10 },
        { u: 'Transit_2', v: 'Central_Lab', cap: 10 }
      ],
      source: 'Clinic_A',
      sink: 'Central_Lab'
    };

    const defaultMatching = {
      left: ['Dr_Alice', 'Dr_Bob', 'Dr_Carol', 'Dr_David'],
      right: ['PCR_Assay', 'NGS_Sequencing', 'CRISPR_Screen', 'Flow_Cytometry'],
      edges: [
        { u: 0, v: 0 },
        { u: 0, v: 1 },
        { u: 1, v: 1 },
        { u: 1, v: 2 },
        { u: 2, v: 0 },
        { u: 2, v: 3 },
        { u: 3, v: 2 }
      ]
    };

    const defaultGraph = {
      nodes: ['M1', 'M2', 'M3', 'M4', 'M5', 'M6'],
      edges: [
        { u: 'M1', v: 'M2' },
        { u: 'M1', v: 'M3' },
        { u: 'M2', v: 'M3' },
        { u: 'M3', v: 'M4' },
        { u: 'M4', v: 'M5' },
        { u: 'M5', v: 'M6' },
        { u: 'M1', v: 'M6' }
      ]
    };

    try {
      if (algo.id === 'kmp') {
        states = window.StringSimulations.generateKmp(val1, val2);
      } else if (algo.id === 'z-alg') {
        states = window.StringSimulations.generateZ(val1, val2);
      } else if (algo.id === 'rabin-karp') {
        states = window.StringSimulations.generateRabinKarp(val1, val2);
      } else if (algo.id === 'aho-corasick') {
        const patterns = val2 ? val2.split(',').map(p => p.trim().toUpperCase()).filter(Boolean) : ['ATG', 'GGA'];
        states = window.StringSimulations.generateAhoCorasick(val1, patterns);
      } else if (algo.id === 'suffix-array') {
        states = window.SuffixSimulations.generateSuffixArray(val1, val2);
      } else if (algo.id === 'sais') {
        states = window.SuffixSimulations.generateSais(val1);
      } else if (algo.id === 'kasai') {
        states = window.SuffixSimulations.generateKasai(val1);
      } else if (algo.id === 'suffix-tree') {
        states = window.SuffixSimulations.generateSuffixTree(val1, val2);
      } else if (algo.id === 'suffix-automaton') {
        states = window.SuffixSimulations.generateSuffixAutomaton(val1, val2);
      } else if (algo.id === 'levenshtein') {
        states = window.DpSimulations.generateLevenshtein(val1, val2);
      } else if (algo.id === 'damerau') {
        states = window.DpSimulations.generateDamerau(val1, val2);
      } else if (algo.id === 'needleman-wunsch') {
        states = window.DpSimulations.generateNeedlemanWunsch(val1, val2);
      } else if (algo.id === 'smith-waterman') {
        states = window.DpSimulations.generateSmithWaterman(val1, val2);
      } else if (algo.id === 'bitmask-dp') {
        states = window.DpSimulations.generateBitmaskDp([val1 || 'ATGC', val2 || 'GCAT', 'TGCG']);
      } else if (algo.id === 'matrix-chain') {
        states = window.DpSimulations.generateMatrixChain([10, 30, 5, 60]);
      } else if (algo.id === 'optimal-bst') {
        states = window.DpSimulations.generateOptimalBst(['BRCA1', 'CFTR', 'TP53'], [0.5, 0.3, 0.2]);
      } else if (algo.id === 'ford-fulkerson') {
        states = window.FlowSimulations.generateNetworkFlow(defaultNetwork, 'ford-fulkerson');
      } else if (algo.id === 'edmonds-karp') {
        states = window.FlowSimulations.generateNetworkFlow(defaultNetwork, 'edmonds-karp');
      } else if (algo.id === 'dinic') {
        states = window.FlowSimulations.generateDinic(defaultNetwork);
      } else if (algo.id === 'bipartite-matching' || algo.id === 'konig-theorem') {
        states = window.FlowSimulations.generateBipartiteMatching(defaultMatching);
      } else if (algo.id === 'min-cut') {
        states = window.FlowSimulations.generateNetworkFlow(defaultNetwork, 'edmonds-karp');
      } else if (algo.id === '3-sat') {
        states = window.NpSimulations.generateThreeSat(
          ['x1', 'x2', 'x3'],
          [['x1', 'x2', 'x3'], ['~x1', '~x2', 'x3'], ['~x1', 'x2', '~x3']],
          { x1: true, x2: false, x3: true }
        );
      } else if (algo.id === 'sat-to-clique') {
        states = window.NpSimulations.generateThreeSatToClique(
          ['x1', 'x2', 'x3'],
          [['x1', 'x2', 'x3'], ['~x1', '~x2', 'x3']]
        );
      } else if (algo.id === 'clique-to-is') {
        states = window.NpSimulations.generateCliqueToIndependentSet(defaultGraph);
      } else if (algo.id === 'is-to-vc') {
        states = window.NpSimulations.generateIndependentSetToVertexCover(defaultGraph);
      } else if (algo.id === 'vc-approx') {
        states = window.NpSimulations.generateVertexCoverApprox(defaultGraph);
      } else if (algo.id === 'random-qsort') {
        const arr = val1 ? val1.split(',').map(x => parseInt(x.trim())).filter(x => !isNaN(x)) : [52, 14, 88, 34, 62, 28, 91, 45];
        states = window.RandomizedSimulations.generateRandomizedQuickSort(arr);
      } else if (algo.id === 'reservoir') {
        const stream = val1 ? val1.split(',').map(s => s.trim()).filter(Boolean) : ['Cell_1', 'Cell_2', 'Cell_3', 'Cell_4', 'Cell_5', 'Cell_6', 'Cell_7', 'Cell_8'];
        const k = parseInt(val2) || 4;
        states = window.RandomizedSimulations.generateReservoir(stream, k);
      } else if (algo.id === 'miller-rabin') {
        const n = parseInt(val1) || 104729;
        const k = parseInt(val2) || 5;
        states = window.RandomizedSimulations.generateMillerRabin(n, k);
      } else if (algo.id === 'blelloch-scan') {
        const arr = val1 ? val1.split(',').map(x => parseInt(x.trim())).filter(x => !isNaN(x)) : [3, 1, 7, 0, 4, 1, 6, 3];
        states = window.ParallelSimulations.generateBlellochScan(arr);
      } else if (algo.id === 'parallel-reduce') {
        const arr = val1 ? val1.split(',').map(x => parseInt(x.trim())).filter(x => !isNaN(x)) : [32, 28, 41, 35, 19, 44, 27, 39];
        states = window.ParallelSimulations.generateParallelReduce(arr);
      } else if (algo.id === 'brent-theorem') {
        const w = parseInt((document.getElementById('input-brent-work') || {}).value) || 1000;
        const s = parseInt((document.getElementById('input-brent-span') || {}).value) || 20;
        const p = parseInt((document.getElementById('input-brent-cores') || {}).value) || 8;
        states = window.ParallelSimulations.generateBrentBounds(w, s, p);
      }
    } catch (e) {
      console.error(e);
      if (errorEl) errorEl.textContent = 'Execution error: ' + e.message;
      return;
    }

    const t1 = performance.now();
    const runtimeMs = (t1 - t0).toFixed(2);

    if (!states || states.length === 0) {
      states = [{ operation: 'Completed', explanation: 'Algorithm executed.', math: '' }];
    }

    const finalState = states[states.length - 1];

    // Render Clean Dedicated Results Card (Step 5, Requirement #8)
    this.renderResultsCard(algo, val1, val2, finalState, states.length, runtimeMs);

    // Setup visual canvas and player
    const visualContainer = document.getElementById('explorer-visual-stage');
    const playerControls = document.getElementById('explorer-player-controls');

    if (playerControls) {
      playerControls.style.display = launchPlayer ? 'flex' : 'none';
    }

    const engine = new SimulationEngine({
      speed: 'normal',
      counterEl: document.getElementById('explorer-step-counter'),
      progressFill: document.getElementById('explorer-progress-fill'),
      operationEl: document.getElementById('explorer-operation-title'),
      explanationEl: document.getElementById('explorer-step-explanation'),
      mathEl: document.getElementById('explorer-math-formula'),
      prevBtn: document.getElementById('explorer-prev-btn'),
      nextBtn: document.getElementById('explorer-next-btn'),
      playBtn: document.getElementById('explorer-play-btn'),
      resetBtn: document.getElementById('explorer-reset-btn'),
      endBtn: document.getElementById('explorer-end-btn'),
      speedSelect: document.getElementById('explorer-speed-select'),
      bindControls: true,
      onStateChange: (state) => {
        this.renderVisualState(algo.id, visualContainer, state);
      }
    });

    this.activeEngine = engine;
    engine.loadStates(states);

    if (launchPlayer) {
      engine.play();
    } else {
      // Jump directly to final result
      engine.goToStep(states.length - 1);
    }
  }

  // Clean Dedicated Result Presentation (Requirement #8)
  renderResultsCard(algo, input1, input2, finalState, totalSteps, runtimeMs) {
    const summaryBox = document.getElementById('results-summary-content');
    const metricsStrip = document.getElementById('results-metrics-strip');
    const resultsCard = document.getElementById('explorer-results-card');

    if (!summaryBox || !resultsCard) return;
    resultsCard.style.display = 'block';

    // Metrics Strip
    if (metricsStrip) {
      metricsStrip.innerHTML = `
        <span class="metric-pill">Algorithm: <strong>${algo.dsaAlgorithm}</strong></span>
        <span class="metric-pill">Execution Steps: <strong>${totalSteps}</strong></span>
        <span class="metric-pill">Time Complexity: <strong>${algo.timeComplexity}</strong></span>
        <span class="metric-pill">Space: <strong>${algo.spaceComplexity}</strong></span>
        <span class="metric-pill">Browser Demo Runtime: <strong>${runtimeMs} ms</strong></span>
      `;
    }

    // Specific Output formatting
    let outputHtml = '';

    if (['kmp', 'z-alg', 'rabin-karp'].includes(algo.id)) {
      const matches = finalState.matches || [];
      const matchIndices = matches.length > 0
        ? matches.map(m => `<span class="index-badge">Index ${m} (0-based)</span>`).join(' ')
        : '<span style="color: var(--text-light);">No exact occurrences found</span>';

      outputHtml = `
        <div class="summary-row">
          <span class="summary-label">Target DNA:</span>
          <span class="summary-value mono" style="letter-spacing: 0.1em; word-break: break-all;">${input1}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Query Motif:</span>
          <span class="summary-value mono" style="font-weight: 700; color: var(--forest-green);">${input2}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Match Positions:</span>
          <div class="match-indices-list">${matchIndices}</div>
        </div>
        <div class="summary-row">
          <span class="summary-label">Character Checks:</span>
          <span class="summary-value"><strong>${finalState.comparisons || 0}</strong> comparisons performed</span>
        </div>
      `;
    } else if (algo.id === 'aho-corasick') {
      const found = finalState.foundMatches || [];
      outputHtml = `
        <div class="summary-row">
          <span class="summary-label">Target Sequence:</span>
          <span class="summary-value mono" style="letter-spacing: 0.1em;">${input1}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Screened Motifs:</span>
          <span class="summary-value"><strong>${input2}</strong></span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Matches Found:</span>
          <div class="match-indices-list">
            ${found.length > 0 ? found.map(f => `<span class="index-badge">"${f.pattern}" at index ${f.index}</span>`).join(' ') : 'None'}
          </div>
        </div>
      `;
    } else if (['levenshtein', 'damerau'].includes(algo.id)) {
      const dist = finalState.dp ? finalState.dp[input1.length][input2.length] : 0;
      outputHtml = `
        <div class="summary-row">
          <span class="summary-label">Sequence 1 (WT):</span>
          <span class="summary-value mono">${input1}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Sequence 2 (Query):</span>
          <span class="summary-value mono">${input2}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Mutational Distance:</span>
          <span class="summary-value" style="font-size: 1.15rem; font-weight: 700; color: var(--forest-green);">${dist} edit operations</span>
        </div>
      `;
    } else if (['needleman-wunsch', 'smith-waterman'].includes(algo.id)) {
      outputHtml = `
        <div class="summary-row">
          <span class="summary-label">Aligned Seq 1:</span>
          <span class="summary-value mono" style="font-weight: 700; color: var(--forest-green);">${finalState.alignA || input1}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Aligned Seq 2:</span>
          <span class="summary-value mono" style="font-weight: 700; color: var(--forest-green);">${finalState.alignB || input2}</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Optimal Score:</span>
          <span class="summary-value"><strong>${finalState.maxVal !== undefined ? finalState.maxVal : (finalState.dp ? finalState.dp[input1.length][input2.length] : 0)}</strong></span>
        </div>
      `;
    } else {
      // General output summary
      outputHtml = `
        <div class="summary-row">
          <span class="summary-label">Biological Task:</span>
          <span class="summary-value"><strong>${algo.bioProblem}</strong></span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Status:</span>
          <span class="summary-value" style="color: var(--forest-green); font-weight: 600;">✓ Calculated successfully</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">Final Output:</span>
          <span class="summary-value">${finalState.explanation || 'Completed'}</span>
        </div>
      `;
    }

    summaryBox.innerHTML = outputHtml;
  }

  // Render Visual State via VisualRenderers
  renderVisualState(algoId, container, state) {
    if (!container || !state || !window.VisualRenderers) return;

    if (algoId === 'kmp') window.VisualRenderers.renderKmp(container, state);
    else if (algoId === 'z-alg') window.VisualRenderers.renderZ(container, state);
    else if (algoId === 'rabin-karp') window.VisualRenderers.renderRabinKarp(container, state);
    else if (algoId === 'aho-corasick') window.VisualRenderers.renderAhoCorasick(container, state);
    else if (algoId === 'suffix-array' || algoId === 'sais' || algoId === 'suffix-tree' || algoId === 'suffix-automaton') window.VisualRenderers.renderSuffixArray(container, state);
    else if (algoId === 'kasai') window.VisualRenderers.renderKasai(container, state);
    else if (['levenshtein', 'damerau', 'needleman-wunsch', 'smith-waterman'].includes(algoId)) window.VisualRenderers.renderDpMatrix(container, state);
    else if (['ford-fulkerson', 'edmonds-karp', 'dinic', 'bipartite-matching', 'konig-theorem', 'min-cut'].includes(algoId)) window.VisualRenderers.renderNetworkFlow(container, state);
    else if (['3-sat', 'sat-to-clique', 'clique-to-is', 'is-to-vc', 'vc-approx'].includes(algoId)) window.VisualRenderers.renderNpGraph(container, state);
    else if (algoId === 'random-qsort') window.VisualRenderers.renderQuickSort(container, state);
    else if (algoId === 'reservoir') window.VisualRenderers.renderReservoir(container, state);
    else if (algoId === 'miller-rabin') window.VisualRenderers.renderMillerRabin(container, state);
    else if (algoId === 'blelloch-scan') window.VisualRenderers.renderBlellochScan(container, state);
    else if (algoId === 'parallel-reduce') window.VisualRenderers.renderParallelReduce(container, state);
    else if (algoId === 'brent-theorem') window.VisualRenderers.renderBrentTheorem(container, state);
    else {
      container.innerHTML = `<div class="sim-visual-box"><p>${state.explanation || 'Visualization state'}</p></div>`;
    }
  }

  // 3. Algorithm Comparison Lab (Side-by-side benchmark)
  initComparisonLab() {
    const runBtn = document.getElementById('comparison-run-btn');
    const tbody = document.getElementById('comparison-results-tbody');
    if (!runBtn || !tbody) return;

    runBtn.addEventListener('click', () => {
      const text = 'ATGGTGCACCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAG';
      const pattern = 'ATG';

      const results = window.ComparisonLab.runMotifSearchComparison(text, pattern);

      tbody.innerHTML = results.map(r => `
        <tr>
          <td><strong>${r.name}</strong><br><span style="font-size: 0.75rem; color: var(--text-light);">${r.technique}</span></td>
          <td><span class="badge badge-success">${r.matches} matches</span></td>
          <td><strong>${r.comparisons}</strong></td>
          <td>${r.totalSteps}</td>
          <td><span style="font-family: var(--font-mono); color: var(--forest-green);">${r.demoTimeMs} ms</span></td>
          <td><code>${r.timeComplexity}</code></td>
          <td><code>${r.spaceComplexity}</code></td>
        </tr>
      `).join('');
    });
  }
}

// Global bootstrap
document.addEventListener('DOMContentLoaded', () => {
  window.bioGenomeApp = new AppController();
  window.bioGenomeApp.init();
});
