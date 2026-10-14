# 🧬 BioGenome – Genome Research & Analysis Platform

> **A Scientific Bioinformatics Platform Powered by Classical Data Structures and Algorithms (DSA)**  
> *Academic Project for Data Structures & Algorithms (DSA-3) Viva & Laboratory Demonstration*

---

## 📌 Scientific Mental Model & Philosophy

In **BioGenome**, the user primarily interacts with **biological and computational genomics tasks**, while the underlying Data Structure or Algorithm is explicitly presented as the computational engine:

$$\begin{matrix}
\textbf{BIOLOGICAL / SCIENTIFIC TASK} \\
\Downarrow \\
\textbf{BIOINFORMATICS OPERATION} \\
\Downarrow \\
\textbf{DSA ALGORITHM USED (in brackets)}
\end{matrix}$$

### Examples:
- **Target DNA Motif Discovery** $\longrightarrow$ Exact Pattern Matching (**KMP Algorithm**)
- **Conserved Domain Alignment** $\longrightarrow$ Local Sequence Alignment (**Smith-Waterman DP**)
- **Sequencing Read Error Correction** $\longrightarrow$ Minimum Edit Distance (**Levenshtein / Damerau-Levenshtein**)
- **Whole-Genome Indexing** $\longrightarrow$ Linear-Time Suffix Array Construction (**SA-IS Algorithm**)
- **Laboratory Specimen Routing** $\longrightarrow$ Maximum Network Flow (**Dinic's Blocking Flow Algorithm**)
- **Personnel Credential Matching** $\longrightarrow$ Maximum Bipartite Matching (**Hopcroft-Karp / König's Theorem**)
- **Multiplex Assay Scheduling** $\longrightarrow$ Constraint Satisfaction (**DPLL 3-SAT Solver**)
- **Conflict-Free Marker Selection** $\longrightarrow$ Polynomial-Time Karp Reduction (**3-SAT $\to$ CLIQUE $\to$ Independent Set**)
- **Continuous Instrument Sampling** $\longrightarrow$ Streaming Sampling (**Reservoir Sampling Algorithm R**)
- **Chromosomal Coverage Prefix Scan** $\longrightarrow$ Work-Efficient Parallel Prefix Sum (**Blelloch Scan**)

---

## 🏛️ Decoupled Architecture

The application is engineered with an independent Java engine and a dedicated scientific frontend:

```
BioGenome_Web/
│
├── backend/ (Root Java Maven project: src/main/java)
│   ├── com.biogenome.string/       # Exact String Matching Algorithms (M1)
│   ├── com.biogenome.suffix/       # Suffix Structures & Indexing (M2)
│   ├── com.biogenome.dp/           # Dynamic Programming & Alignment (M3)
│   ├── com.biogenome.flow/         # Network Flow, Bipartite Matching & König (M4)
│   ├── com.biogenome.np/           # 3-SAT, Karp Reductions & 2-Approximation (M5)
│   ├── com.biogenome.randparallel/ # Randomized & Parallel Algorithms (M6)
│   ├── com.biogenome.controller/   # REST API Controllers (Spring Boot)
│   ├── com.biogenome.model/        # Biological & Algorithmic Domain Models
│   ├── com.biogenome.service/      # In-Memory Biological Data & History Service
│   └── com.biogenome.main/
│       └── Main.java               # 15-Option Interactive Scientific CLI Runner
│
└── frontend/                       # Standalone Modern Scientific Web Interface
    ├── index.html                  # Scientific Dashboard & Laboratory Views
    ├── css/style.css               # Restrained Scientific Theme (Soft white/slate, navy text, teal)
    ├── js/api.js                   # REST API Client with Offline Educational Fallback
    ├── js/visualizers.js           # Step-by-step educational player visualizers
    ├── js/app.js                   # Application controller & global search
    └── README.md                   # Dedicated VS Code execution guide
```

---

## 🔬 Scientific Modules & Computational Method Mapping

| Scientific Category | Bioinformatics Operation | Computational Method (DSA) |
| :--- | :--- | :--- |
| **1. Genome Explorer** | Nucleotide composition, GC/AT ratios, primer validation | Single-Pass Linear Array Scan $\mathcal{O}(N)$ |
| **2. DNA Motif Discovery** | Exact promoter & restriction motif search | Exact Pattern Matching (**KMP Algorithm**) $\mathcal{O}(N+M)$ |
| **3. Genome Pattern Analysis** | Tandem repeat discovery & periodic sequence detection | Longest Common Prefix Matching (**Z-Algorithm**) $\mathcal{O}(N+M)$ |
| **3. Genome Pattern Analysis** | Sliding window dinucleotide & CpG island frequency | Rolling Polynomial Hash (**Rabin-Karp**) $\text{Avg } \mathcal{O}(N+M)$ |
| **3. Genome Pattern Analysis** | Simultaneous restriction enzyme screening library | Trie-Based Failure Automaton (**Aho-Corasick**) $\mathcal{O}(N + \sum M_i)$ |
| **4. Genome Indexing** | Sub-linear whole-genome pattern retrieval | Suffix Array & Binary Search (**Prefix Doubling / SA-IS**) |
| **4. Genome Indexing** | Linear-time whole-chromosome indexing | Induced Sorting Algorithm (**SA-IS**) $\mathcal{O}(N)$ |
| **4. Genome Indexing** | Tandem repeat & duplicated domain identification | Longest Common Prefix Array (**Kasai's Algorithm**) $\mathcal{O}(N)$ |
| **4. Genome Indexing** | All-substring topological factor analysis | Compact Radix Tree (**Suffix Tree**) $\mathcal{O}(N)$ |
| **4. Genome Indexing** | Minimal factor language & distinct substring counting | Directed Acyclic Word Graph (**Suffix Automaton / SAM**) $\mathcal{O}(N)$ |
| **5. Sequence Alignment** | End-to-end global gene homology (HbA vs HbS) | Dynamic Programming Global Alignment (**Needleman-Wunsch**) |
| **5. Sequence Alignment** | Conserved regulatory sub-segment identification | Dynamic Programming Local Alignment (**Smith-Waterman**) |
| **5. Sequence Alignment** | Minimal overlap contig read assembly | Shortest Common Superstring (**Genomic Bitmask DP**) $\mathcal{O}(K^2 2^K)$ |
| **6. Sequence Error Analysis** | Point mutation & indel edit distance | Minimum Edit Distance DP (**Levenshtein Distance**) $\mathcal{O}(NM)$ |
| **6. Sequence Error Analysis** | Adjacent inversion & optical transposition correction | Modified Edit Distance DP (**Damerau-Levenshtein**) $\mathcal{O}(NM)$ |
| **7. Variant Analysis** | Filter pathogenic SNPs, missense mutations, indels | In-Memory Relational Filter & Population Aggregation |
| **8. Biological Networks** | Specimen throughput in multi-tier laboratories | Depth-First Residual Augmentation (**Ford-Fulkerson**) |
| **8. Biological Networks** | Cold-chain specimen routing with minimum transfers | Shortest Augmenting Path BFS (**Edmonds-Karp**) $\mathcal{O}(VE^2)$ |
| **8. Biological Networks** | Large-scale high-throughput clinical networks | Layered Level Graph & Blocking Flow (**Dinic's Algorithm**) $\mathcal{O}(V^2E)$ |
| **8. Biological Networks** | Pipeline bottleneck & capacity limit diagnosis | Saturated Cut Detection (**Max-Flow Min-Cut Theorem**) |
| **9. Resource Allocation** | Assigning certified scientists to clinical trials | Maximum Bipartite Matching (**Hopcroft-Karp / Dinic**) $\mathcal{O}(E\sqrt{V})$ |
| **9. Resource Allocation** | Platform & resource conflict resolution | Minimal Resource Cover (**König's Theorem:** $\lvert\text{Max Match}\rvert = \lvert\text{Min VC}\rvert$) |
| **10. Constraint Analysis** | Multiplex assay primer compatibility verification | Davis-Putnam-Logemann-Loveland (**DPLL 3-SAT Solver**) |
| **10. Constraint Analysis** | Transforming constraints to mutual cooperation | Polynomial-Time Karp Reduction (**3-SAT $\to$ CLIQUE**) $\mathcal{O}(C^2)$ |
| **10. Constraint Analysis** | Conflict-free biomarker selection | Graph Complementation Duality (**CLIQUE $\to$ INDEPENDENT SET**) |
| **10. Constraint Analysis** | Target network minimal interaction coverage | Gallai's Identity (**INDEPENDENT SET $\to$ VERTEX COVER Duality**) |
| **10. Constraint Analysis** | Fast cellular interaction network sensor coverage | Maximal Matching Heuristic (**Vertex Cover 2-Approximation**) |
| **11. Genome Analytics** | High-throughput sequencing record library sorting | Randomized Pivot QuickSort (**Randomized QuickSort**) $\text{Avg } \mathcal{O}(N\log N)$ |
| **11. Genome Analytics** | Unbiased sampling of continuous sequencer data | Streaming Uniform Sampling (**Reservoir Sampling Algorithm R**) $\mathcal{O}(N)$ |
| **11. Genome Analytics** | Cryptogenomic key security & prime certification | Probabilistic Primality Verification (**Miller-Rabin Test**) $\mathcal{O}(k\log^3 N)$ |
| **11. Genome Analytics** | Chromosomal base coverage cumulative offset scan | Work-Efficient Parallel Prefix Sum (**Blelloch Scan**) $\mathcal{O}(N) \text{ work}$ |
| **11. Genome Analytics** | Divide-and-conquer genomic metric reduction | Binary Tree Reduction (**Parallel Tree Reduce**) $\mathcal{O}(\log N) \text{ span}$ |
| **11. Genome Analytics** | Multi-core pipeline speedup & scheduling analysis | Parallel Scaling Bounds (**Brent's Theorem Calculator**) $T_p \le \frac{W-S}{P}+S$ |
| **12. Algorithm Comparison** | Empirical benchmark evaluation on identical inputs | Side-by-side execution timer & character comparison profiler |

---

## ⚡ Big-O Complexity Summary

| Algorithm | Best Case | Average Case | Worst Case | Space Complexity |
| :--- | :---: | :---: | :---: | :---: |
| **Knuth-Morris-Pratt (KMP)** | $\mathcal{O}(N + M)$ | $\mathcal{O}(N + M)$ | $\mathcal{O}(N + M)$ | $\mathcal{O}(M)$ |
| **Z-Algorithm** | $\mathcal{O}(N + M)$ | $\mathcal{O}(N + M)$ | $\mathcal{O}(N + M)$ | $\mathcal{O}(N + M)$ |
| **Rabin-Karp Rolling Hash** | $\mathcal{O}(N + M)$ | $\mathcal{O}(N + M)$ | $\mathcal{O}(NM)$ | $\mathcal{O}(1)$ |
| **Aho-Corasick Automaton** | $\mathcal{O}(N + \sum M_i)$ | $\mathcal{O}(N + \sum M_i + K)$ | $\mathcal{O}(N + \sum M_i + K)$ | $\mathcal{O}(\sum M_i \cdot \lvert\Sigma\rvert)$ |
| **SA-IS Suffix Array** | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ |
| **Kasai's LCP Algorithm** | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ |
| **Needleman-Wunsch Alignment** | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ |
| **Smith-Waterman Alignment** | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ |
| **Damerau-Levenshtein Distance** | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ | $\mathcal{O}(NM)$ |
| **Genomic Bitmask DP** | $\mathcal{O}(K^2 2^K)$ | $\mathcal{O}(K^2 2^K)$ | $\mathcal{O}(K^2 2^K)$ | $\mathcal{O}(K \cdot 2^K)$ |
| **Ford-Fulkerson Max-Flow** | $\mathcal{O}(E)$ | $\mathcal{O}(E \cdot \lvert f^*\rvert)$ | $\mathcal{O}(E \cdot \lvert f^*\rvert)$ | $\mathcal{O}(V + E)$ |
| **Edmonds-Karp Max-Flow** | $\mathcal{O}(VE)$ | $\mathcal{O}(VE^2)$ | $\mathcal{O}(VE^2)$ | $\mathcal{O}(V + E)$ |
| **Dinic's Algorithm** | $\mathcal{O}(E \sqrt{V})$ (unit) | $\mathcal{O}(V^2 E)$ | $\mathcal{O}(V^2 E)$ | $\mathcal{O}(V + E)$ |
| **Hopcroft-Karp / Matching** | $\mathcal{O}(E)$ | $\mathcal{O}(E \sqrt{V})$ | $\mathcal{O}(E \sqrt{V})$ | $\mathcal{O}(V + E)$ |
| **DPLL 3-SAT Solver** | $\mathcal{O}(V)$ | Exponential | $\mathcal{O}(2^V)$ | $\mathcal{O}(V + C)$ |
| **Vertex Cover 2-Approximation** | $\mathcal{O}(V + E)$ | $\mathcal{O}(V + E)$ | $\mathcal{O}(V + E)$ | $\mathcal{O}(V)$ |
| **Randomized QuickSort** | $\mathcal{O}(N \log N)$ | $\mathcal{O}(N \log N)$ | $\mathcal{O}(N^2)$ (prob $1/N!$) | $\mathcal{O}(\log N)$ |
| **Reservoir Sampling (Alg R)** | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | $\mathcal{O}(k)$ |
| **Miller-Rabin Primality** | $\mathcal{O}(k \log^2 N)$ | $\mathcal{O}(k \log^3 N)$ | $\mathcal{O}(k \log^3 N)$ | $\mathcal{O}(1)$ |
| **Blelloch Parallel Scan** | $\mathcal{O}(N)$ work | $\mathcal{O}(N)$ work / $\mathcal{O}(\log N)$ span | $\mathcal{O}(N)$ work | $\mathcal{O}(N)$ |
| **Parallel Tree Reduce** | $\mathcal{O}(N)$ work | $\mathcal{O}(N)$ work / $\mathcal{O}(\log N)$ span | $\mathcal{O}(N)$ work | $\mathcal{O}(N)$ |
| **Brent's Theorem Calculator** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ |

---

## 💻 How to Run the Java Backend in Eclipse IDE

1. **Import the Project into Eclipse**:
   - Open Eclipse IDE $\to$ **File** $\to$ **Import...**
   - Select **Maven** $\to$ **Existing Maven Projects** $\to$ **Next**.
   - Browse to `BioGenome_Web` and click **Finish**.
2. **Option A: Run the Web Dashboard from Eclipse**:
   - Expand `src/main/java` $\to$ `com.biogenome.web` $\to$ `BioGenomeApplication.java`.
   - Right-click $\to$ **Run As** $\to$ **Java Application** (or **Spring Boot App**).
   - Check the Eclipse console:
     ```
     =================================================
       BioGenome Web Dashboard running at:
       http://localhost:8080
       REST API Base: http://localhost:8080/api/v1
     =================================================
     ```
   - Open your browser to 👉 **`http://localhost:8080`**.
3. **Option B: Run the Scientific Viva Console (CLI) in Eclipse**:
   - Expand `src/main/java` $\to$ `com.biogenome.main` $\to$ `Main.java`.
   - Right-click $\to$ **Run As** $\to$ **Java Application**.
   - Interact directly via the Eclipse Terminal using the 15-option scientific workflow menu.
4. **Option C: Run Automated Tests**:
   - Right-click `src/test/java` $\to$ **Run As** $\to$ **JUnit Test**.
   - Confirm **29/29 tests pass** with green bar.

---

## 🖥️ How to Run the Frontend Independently in VS Code

The `frontend/` directory is completely decoupled:

1. Open `BioGenome_Web` in Visual Studio Code.
2. In the file explorer, navigate to `frontend/index.html`.
3. **Option 1 (Live Server)**: Right-click `index.html` $\to$ **"Open with Live Server"**.
4. **Option 2 (Terminal)**:
   ```bash
   cd frontend
   python -m http.server 3000
   ```
   Visit `http://localhost:3000`.
5. **Option 3**: Double-click `frontend/index.html` in Windows Explorer to open directly in Chrome/Edge/Firefox.
6. The frontend automatically connects to the Java backend at `http://localhost:8080/api/v1` when online, or gracefully runs in standalone demonstration mode with built-in educational data when offline.

---

## 🧪 Testing & Verification Status

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.biogenome.test.Module1StringTest           (5 tests)  - OK
[INFO] Running com.biogenome.test.Module2SuffixTest           (4 tests)  - OK
[INFO] Running com.biogenome.test.Module3DynamicProgrammingTest (5 tests)  - OK
[INFO] Running com.biogenome.test.Module4NetworkFlowTest      (4 tests)  - OK
[INFO] Running com.biogenome.test.Module5NpCompletenessTest   (5 tests)  - OK
[INFO] Running com.biogenome.test.Module6RandomizedParallelTest (6 tests)  - OK
[INFO] 
[INFO] Results:
[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## ⚠️ Academic & Educational Disclaimer

**BioGenome** is strictly an educational simulation platform designed for university **Data Structures and Algorithms (DSA-3)** coursework, viva examinations, and laboratory demonstrations. All genomic sequences, variants, and resource logistics graphs are educational models and must **NOT** be used for clinical diagnosis, patient treatment decisions, or medical advice.
