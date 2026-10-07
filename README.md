# MFC Assignments - Data Structures & Algorithms

This repository contains implementations, algorithm complexity analysis, and LaTeX documentation for key Computer Science algorithms:

## 📂 Repository Structure

### 1.  Sorting (`cpp/sorting.cpp`, `latex/sorting.tex`, `java/sorting/SortingAlgorithms.java`)
- **Quick Sort** — Divide and conquer, Lomuto partitioning scheme ($O(n \log n)$ average, $O(n^2)$ worst).
- **Merge Sort** — Guaranteed stable divide and conquer ($O(n \log n)$ time, $O(n)$ space).
- **Heap Sort** — Binary Max-Heap in-place sorting ($O(n \log n)$ time, $O(1)$ space).
- **Insertion Sort** — Efficient for small/nearly-sorted data ($O(n)$ best, $O(n^2)$ worst).

### 2. Trees (`cpp/trees.cpp`, `latex/trees.tex`, `java/trees/TreeAlgorithms.java`)
- **Binary Tree** — Level-order (BFS) complete tree insertion.
- **Binary Search Tree (BST)** — Search, insert, and 3-case node deletion.
- **Traversals**:
  - In-order (Left $\rightarrow$ Root $\rightarrow$ Right) — Returns BST elements in sorted order.
  - Pre-order (Root $\rightarrow$ Left $\rightarrow$ Right) — Used for tree cloning & serialization.
  - Post-order (Left $\rightarrow$ Right $\rightarrow$ Root) — Used for memory cleanup & expression evaluation.

### 3. Graph Traversal (`cpp/graphs.cpp`, `latex/graphs.tex`, `java/graphs/GraphTraversal.java`)
- **BFS (Breadth-First Search)** — Level-by-level search using Queue ($O(V+E)$), computes unweighted shortest path.
- **DFS (Depth-First Search)** — Deep search using Recursion/Stack ($O(V+E)$), cycle detection, and connectivity check.

### 4. Minimum Spanning Tree (`cpp/minimum_spanning_tree.cpp`, `latex/minimum_spanning_tree.tex`, `java/graphs/MinimumSpanningTree.java`)
- **Prim's Algorithm** — Greedy MST construction using Priority Queue (Min-Heap) ($O(E \log V)$).
- **Kruskal's Algorithm** — Greedy edge selection with Disjoint Set Union (DSU) using path compression and union by rank ($O(E \log E)$).

### 5. Shortest Path (`cpp/shortest_path.cpp`, `latex/shortest_path.tex`, `java/graphs/ShortestPath.java`)
- **Dijkstra's Algorithm** — Single-source shortest path for non-negative edge weights using Min-Heap ($O(E \log V)$).
- **Bellman-Ford Algorithm** — Single-source shortest path handling negative edge weights & negative cycle detection ($O(VE)$).
- **Floyd-Warshall Algorithm** — Dynamic programming all-pairs shortest paths matrix ($O(V^3)$).
