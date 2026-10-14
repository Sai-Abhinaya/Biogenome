# 🌿 BioGenome – Computational Biology & DSA Platform

> **Modern Bioinformatics Learning Platform**  
> *Demonstrating classical Data Structures and Algorithms through genuine biological problems.*

---

## 📌 Design Concept & Philosophy

BioGenome redesigns algorithmic visualization into a **clean, aesthetic, beginner-friendly bioinformatics platform**. Biology is the primary language of the interface, while formal DSA terminology appears alongside it as the computational engine.

At a glance, every module answers three essential questions:
1. **What biological problem are we solving?**
2. **Which DSA algorithm solves it?**
3. **How does the algorithm work?**

### 🎨 Visual Identity & Scientific Theme
* **Warm Ivory Backgrounds** (`#FAF9F5`, `#F4F1EA`) — Soft on the eyes for extended laboratory and classroom study.
* **Forest Green & Deep Teal Accents** (`#1E3F30`, `#235F5B`) — Calm, prestigious botanical and scientific palette.
* **Muted Sage Highlights** (`#4D735F`, `#E4EDE7`) — Clean card borders and contextual active states.
* **Refined Nucleotide Typography** — Styled pills for Adenine (A), Thymine (T), Guanine (G), and Cytosine (C) with subtle, accessible hues and high-contrast charcoal text.
* **Uncluttered Layout** — Generous whitespace, modest corner radii, subtle borders, and zero neon or gaming effects.

---

## 🚀 How to Run

BioGenome is a **pure frontend web application** that runs 100% in modern browsers with zero external dependencies, build tools, or server requirements:

### Option 1: VS Code Live Server (Recommended)
1. Open this repository in **Visual Studio Code**.
2. Right-click [`frontend/index.html`](file:///d:/2nd%20year/DSA3/BioGenome_Web/frontend/index.html).
3. Select **"Open with Live Server"**.
4. The application will open at `http://127.0.0.1:5500/frontend/index.html`.

### Option 2: Python Local HTTP Server
```powershell
cd "d:\2nd year\DSA3\BioGenome_Web\frontend"
python -m http.server 8000
```
Open `http://localhost:8000/` in your browser.

### Option 3: Direct File Opening
Double-click [`frontend/index.html`](file:///d:/2nd%20year/DSA3/BioGenome_Web/frontend/index.html) in Windows File Explorer to open directly in Google Chrome, Microsoft Edge, Firefox, or Safari.

---

## 🧭 Navigation & Information Architecture

The redesigned user interface features a clean, unified single-page layout:

1. **Home / Overview**:
   * Hero section introducing the core philosophy: *"Explore biological sequences through algorithms."*
   * Curated biological use cases with one-click **"Launch in Explorer"** shortcuts.
   * Educational pillars explaining the direct relationship between molecular biology and computational algorithms.

2. **Algorithm Explorer (Primary Workspace)**:
   * **Step 1 — Biological Task Selection**: Filterable by biological category or dropdown.
   * **Step 2 — Biological Context & Algorithm Mapping**: Displays biological context, DSA connection, numbered procedure, input/output specifications, and verified asymptotic complexities.
   * **Step 3 — Interactive Biological Input**: Tailored input fields for sequences, motifs, or matrices, with real biological preset buttons.
   * **Step 4 — Execution & Interactive Trace**: Run instantly or step through with `Previous`, `Play/Pause`, `Next`, `Reset`, and speed controls.
   * **Step 5 — Clean Results Presentation**: Clear 0-based match positions, aligned strings, flow values, or reduction graphs, with an expandable technical details drawer.

3. **How It Works (Biological ↔ Algorithm Matrix)**:
   * Comprehensive reference table mapping all 33 implemented algorithms to their genuine biological use cases.

4. **Comparison Lab**:
   * Side-by-side benchmark comparing multiple algorithms on identical biological sequences.

5. **About**:
   * Project architecture, educational goals, and verification details.

---

## 🧬 Catalog of All 33 Implemented Algorithms

| Category | Biological Task | Formal DSA Algorithm | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :--- | :--- |
| **Sequence Pattern Matching** | DNA Sequence Matching | Knuth–Morris–Pratt (KMP) | $O(n + m)$ | $O(m)$ |
| **Sequence Pattern Matching** | Conserved Sequence Motif Search | Z-Algorithm | $O(n + m)$ | $O(n + m)$ |
| **Sequence Pattern Matching** | High-Throughput Sequence Matching | Rabin–Karp Rolling Hash | $O(n + m)$ avg | $O(1)$ |
| **Sequence Pattern Matching** | Multiple Regulatory Motif Search | Aho–Corasick Automaton | $O(n + \sum m_i + z)$ | $O(\sum m_i \cdot \Sigma)$ |
| **Genome Indexing & Search** | Whole-Genome Indexing | Suffix Array Construction | $O(n \log^2 n)$ | $O(n)$ |
| **Genome Indexing & Search** | Linear-Time Genome Indexing | SA-IS Induced Sorting | $O(n)$ | $O(n)$ |
| **Genome Indexing & Search** | Maximal Repeat Detection | Kasai LCP Array | $O(n)$ | $O(n)$ |
| **Genome Indexing & Search** | Full Suffix Hierarchy Index | Suffix Tree (Ukkonen/Naive) | $O(n)$ | $O(n \cdot \Sigma)$ |
| **Genome Indexing & Search** | Compact Substring Index | Directed Acyclic Word Graph (DAWG) | $O(n)$ | $O(n)$ |
| **Sequence Alignment & DP** | Mutation Distance Analysis | Levenshtein Distance | $O(nm)$ | $O(nm)$ |
| **Sequence Alignment & DP** | Inversion & Mutation Detection | Damerau–Levenshtein Distance | $O(nm)$ | $O(nm)$ |
| **Sequence Alignment & DP** | Global Sequence Alignment | Needleman–Wunsch Algorithm | $O(nm)$ | $O(nm)$ |
| **Sequence Alignment & DP** | Local Sub-Sequence Alignment | Smith–Waterman Algorithm | $O(nm)$ | $O(nm)$ |
| **Sequence Alignment & DP** | Lab Sample Optimal Ordering | Bitmask Dynamic Programming | $O(2^n \cdot n^2)$ | $O(2^n \cdot n)$ |
| **Sequence Alignment & DP** | Multi-Stage Bioprocess Optimization | Matrix Chain Multiplication | $O(n^3)$ | $O(n^2)$ |
| **Sequence Alignment & DP** | Frequency-Weighted Marker Search | Optimal Binary Search Tree (OBST) | $O(n^3)$ | $O(n^2)$ |
| **Biochemical Networks & Flow**| Bioreactor Pipeline Throughput | Ford–Fulkerson Method | $O(E \cdot \lvert f \rvert)$ | $O(V + E)$ |
| **Biochemical Networks & Flow**| Guaranteed-Time Network Routing | Edmonds–Karp Algorithm | $O(V \cdot E^2)$ | $O(V + E)$ |
| **Biochemical Networks & Flow**| High-Volume Sample Allocation | Dinic's Blocking Flow | $O(V^2 E)$ | $O(V + E)$ |
| **Biochemical Networks & Flow**| Sample-to-Sequencer Assignment | Hopcroft–Karp / Bipartite Matching | $O(E \sqrt{V})$ | $O(V + E)$ |
| **Biochemical Networks & Flow**| Critical Specimen Bottleneck Identification | König's Theorem Min-Vertex-Cover | $O(V + E)$ post-flow | $O(V)$ |
| **Biochemical Networks & Flow**| Minimum Assay Elimination Cut | Max-Flow Min-Cut Theorem | $O(V + E)$ post-flow | $O(V)$ |
| **Genomic Optimization & NP** | Gene Regulatory Network Consistency | 3-Conjunctive Normal Form SAT | $O(2^n \cdot m)$ | $O(n + m)$ |
| **Genomic Optimization & NP** | Regulatory Constraint to Interaction Cluster | 3-SAT to CLIQUE Reduction | $O(m^2)$ | $O(m^2)$ |
| **Genomic Optimization & NP** | Target Cluster to Mutually Exclusive Genes | CLIQUE to Independent Set Reduction | $O(V + E)$ | $O(V^2)$ |
| **Genomic Optimization & NP** | Mutation-Free Sets to Essential Regulatory Genes | Independent Set to Vertex Cover | $O(V + E)$ | $O(V)$ |
| **Genomic Optimization & NP** | Approximation for Essential Gene Detection | Vertex Cover 2-Approximation | $O(E)$ | $O(V)$ |
| **Randomized Algorithms** | Large-Scale Sequencing Read Sorting | Randomized QuickSort | $O(n \log n)$ avg | $O(\log n)$ |
| **Randomized Algorithms** | Unbiased Single-Cell Observation Sampling | Reservoir Sampling (Algorithm R) | $O(N)$ | $O(k)$ |
| **Randomized Algorithms** | Genomic Hash Key Primality Verification | Miller–Rabin Primality Test | $O(k \log^3 n)$ | $O(1)$ |
| **Parallel Computing** | High-Throughput Genomic Prefix Scanning | Blelloch Work-Efficient Parallel Scan | Span $O(\log n)$, Work $O(n)$ | $O(n)$ |
| **Parallel Computing** | Distributed GC-Content Reduction | Parallel Tree Reduction | Span $O(\log n)$, Work $O(n)$ | $O(n)$ |
| **Parallel Computing** | Multi-Core Throughput Evaluation | Brent's Scheduling Work-Span Theorem | $O(1)$ evaluation | $O(1)$ |

---

## 🛠 Project Structure

```
BioGenome_Web/
├── frontend/
│   ├── index.html           # Unified modern bioinformatics shell
│   ├── README.md            # Frontend documentation and usage guide
│   ├── css/
│   │   ├── main.css         # Warm ivory, forest green, typography & responsive layout
│   │   └── simulations.css  # Controls, progress, nucleotide pills, DP tables & graphs
│   └── js/
│       ├── engine.js        # Universal discrete simulation step engine
│       ├── datasets.js      # Realistic educational biological sequences & presets
│       ├── renderers.js     # Botanical DOM state renderers with 0-based indices
│       ├── app.js           # Biological metadata registry & 5-step controller
│       └── simulations/     # Real algorithm mathematical implementations
│           ├── string-sims.js     # KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick
│           ├── suffix-sims.js     # Suffix Array, SA-IS, Kasai, Suffix Tree, DAWG
│           ├── dp-sims.js         # Levenshtein, Damerau, Needleman-Wunsch, Smith-Waterman, etc.
│           ├── flow-sims.js       # Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite, Min-Cut
│           ├── np-sims.js         # 3-SAT and polynomial reductions
│           ├── randomized-sims.js # Randomized QuickSort, Reservoir, Miller-Rabin
│           ├── parallel-sims.js   # Blelloch Scan, Parallel Reduce, Brent's Theorem
│           └── comparison-sims.js # Multi-algorithm side-by-side benchmark runner
└── src/main/java/           # Independent Java DSA implementation
```

---

## 🎓 Evaluation & Presentation Guide

When demonstrating this project to instructors, reviewers, or students:
1. **Highlight the Biology-First Mental Model**: Note that students search for *"DNA Sequence Matching"* or *"Mutation Distance Analysis"* rather than needing to remember technical acronyms upfront.
2. **Demonstrate Algorithm Ground Truth**: Run KMP or Needleman-Wunsch — observe that the LPS table and DP matrix are computed dynamically using actual character comparisons and math, not static mock animations.
3. **Show Step-by-Step Control**: Use the player controls (`Play`, `Pause`, `Step Next`, `Step Prev`) to walk through how a rolling hash slides or how a dynamic programming cell selects its minimum edit operation.
4. **Inspect the Educational Mapping**: Navigate to the **"How It Works"** tab to see all 33 algorithms cross-referenced against their biological problems and complexities.
