package com.jeenify.algorithms.graphs;

import java.util.*;

/**
 * ============================================================
 *  MINIMUM SPANNING TREE (MST) ALGORITHMS
 * ============================================================
 *
 *  What is a Spanning Tree?
 *    A spanning tree of a graph G is a subgraph that:
 *      - Includes ALL vertices of G.
 *      - Is a TREE (connected and acyclic).
 *      - Contains exactly (V-1) edges.
 *
 *  What is a Minimum Spanning Tree?
 *    An MST is a spanning tree where the TOTAL EDGE WEIGHT is minimized.
 *
 *  Algorithms Covered:
 *    1. Prim's Algorithm    — Greedy: grow MST vertex by vertex
 *    2. Kruskal's Algorithm — Greedy: sort edges, add if no cycle (Union-Find)
 *
 *  Complexity:
 *  ┌─────────────────────┬──────────────────────────┬──────────────────────┐
 *  │  Algorithm          │  Time                    │  Space               │
 *  ├─────────────────────┼──────────────────────────┼──────────────────────┤
 *  │  Prim's (adj list + │  O(E log V) with MinHeap │  O(V + E)            │
 *  │  priority queue)    │                          │                      │
 *  │  Kruskal's          │  O(E log E)              │  O(V + E)            │
 *  └─────────────────────┴──────────────────────────┴──────────────────────┘
 *  V = vertices, E = edges
 *
 *  KEY DIFFERENCE:
 *    Prim's: Better for DENSE graphs (many edges).
 *    Kruskal's: Better for SPARSE graphs (few edges).
 *
 *  APPLICATIONS:
 *    - Network design: laying cable/road with minimum cost.
 *    - Cluster analysis.
 *    - Approximation algorithms for NP-hard problems (e.g., TSP).
 *
 * ============================================================
 */
public class MinimumSpanningTree {

    // ============================================================
    //  EDGE CLASS — Used by both Prim's and Kruskal's
    // ============================================================
    static class Edge implements Comparable<Edge> {
        int src, dest, weight;

        Edge(int src, int dest, int weight) {
            this.src    = src;
            this.dest   = dest;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }

        @Override
        public String toString() {
            return src + " -- " + dest + " [weight=" + weight + "]";
        }
    }


    // ============================================================
    //  1. PRIM'S ALGORITHM
    // ============================================================
    /**
     * Prim's Algorithm — Greedy MST
     *
     * IDEA:
     *   Start from any vertex and GROW the MST one vertex at a time.
     *   Always pick the minimum-weight edge connecting a vertex IN the MST
     *   to a vertex NOT YET in the MST.
     *
     * ALGORITHM STEPS:
     *   1. Start with an arbitrary vertex (e.g., vertex 0). Mark it as in MST.
     *   2. Consider all edges from MST vertices to non-MST vertices.
     *   3. Pick the minimum-weight such edge.
     *   4. Add its destination vertex to MST.
     *   5. Repeat steps 2–4 until all V vertices are in MST.
     *
     * IMPLEMENTATION:
     *   Uses a Min-Heap (Priority Queue) for efficiency.
     *   - key[v]    = minimum edge weight to connect v to MST
     *   - parent[v] = which vertex connects v to MST
     *   - inMST[v]  = whether v is already in the MST
     *
     * COMPLEXITY:
     *   - Time:  O(E log V) with adjacency list + Min-Heap
     *   - Space: O(V) for key, parent, inMST arrays
     *
     * WHY GREEDY WORKS?
     *   Prim's uses the "Cut Property": the minimum edge crossing a cut
     *   (partition of vertices into MST-set and non-MST-set) is always
     *   in some MST.
     */
    public static void primsAlgorithm(int[][] graph, int V) {
        // key[i] = minimum weight edge to connect vertex i to MST
        int[] key = new int[V];
        // parent[i] = parent of vertex i in MST
        int[] parent = new int[V];
        // inMST[i] = true if vertex i is included in MST
        boolean[] inMST = new boolean[V];

        // Initialize: all keys to infinity, no parent, no vertex in MST
        Arrays.fill(key, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        // Start from vertex 0
        key[0] = 0;

        // Priority Queue: [key, vertex], ordered by key (min-heap)
        // int[]{weight, vertex}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, 0});  // (key=0, vertex=0)

        System.out.println("Prim's Algorithm — MST Construction:");
        System.out.println("─────────────────────────────────────");

        int totalWeight = 0;

        while (!pq.isEmpty()) {
            // Step 3: Extract vertex with minimum key
            int[] curr = pq.poll();
            int u = curr[1];

            // Skip if already included in MST
            if (inMST[u]) continue;

            // Step 4: Add u to MST
            inMST[u] = true;

            // Print MST edge (skip source vertex 0 which has no parent)
            if (parent[u] != -1) {
                System.out.printf("  Edge: %d -- %d  |  Weight: %d%n",
                        parent[u], u, graph[parent[u]][u]);
                totalWeight += graph[parent[u]][u];
            }

            // Step 2: Update keys of adjacent vertices not yet in MST
            for (int v = 0; v < V; v++) {
                // graph[u][v] != 0 means there is an edge between u and v
                if (graph[u][v] != 0 && !inMST[v] && graph[u][v] < key[v]) {
                    key[v]    = graph[u][v];  // Update minimum weight
                    parent[v] = u;            // Track which vertex provides this weight
                    pq.offer(new int[]{key[v], v});
                }
            }
        }

        System.out.println("─────────────────────────────────────");
        System.out.println("Total MST Weight (Prim's): " + totalWeight);
    }


    // ============================================================
    //  2. KRUSKAL'S ALGORITHM
    // ============================================================
    /**
     * Kruskal's Algorithm — Greedy MST using Union-Find
     *
     * IDEA:
     *   Sort ALL edges by weight.
     *   Add edges to MST one by one (from smallest to largest),
     *   SKIPPING any edge that would create a CYCLE.
     *   A cycle is detected using UNION-FIND (Disjoint Set Union).
     *
     * ALGORITHM STEPS:
     *   1. Sort all edges by weight in ascending order.
     *   2. Initialize Union-Find with V separate sets (one per vertex).
     *   3. For each edge (u, v) in sorted order:
     *      a. If u and v are in DIFFERENT sets → add edge to MST, union the sets.
     *      b. If u and v are in SAME set → adding this edge would create a cycle → SKIP.
     *   4. Stop when MST has V-1 edges.
     *
     * UNION-FIND (Disjoint Set Union — DSU):
     *   - find(x):  returns the "representative" (root) of x's set.
     *   - union(x,y): merges the sets containing x and y.
     *   - Path Compression + Union by Rank → O(α(V)) ≈ O(1) per operation.
     *
     * COMPLEXITY:
     *   - Time:  O(E log E) — dominated by sorting edges
     *   - Space: O(V + E)
     *
     * WHY GREEDY WORKS?
     *   Kruskal's uses the "Cycle Property": the maximum-weight edge in any cycle
     *   is never in any MST. By adding minimum edges that don't form cycles,
     *   we guarantee an MST.
     */
    public static void kruskalsAlgorithm(int V, List<Edge> edges) {
        // Step 1: Sort all edges by weight
        Collections.sort(edges);

        // Initialize Union-Find structures
        int[] parent = new int[V];
        int[] rank   = new int[V];
        for (int i = 0; i < V; i++) parent[i] = i; // Each vertex is its own root

        List<Edge> mst = new ArrayList<>();
        int totalWeight = 0;

        System.out.println("Kruskal's Algorithm — MST Construction:");
        System.out.println("─────────────────────────────────────────");
        System.out.println("Sorted Edges: " + edges);
        System.out.println();

        // Step 3: Process edges in sorted order
        for (Edge edge : edges) {
            int rootSrc  = find(parent, edge.src);
            int rootDest = find(parent, edge.dest);

            // If src and dest are in different sets → no cycle → add to MST
            if (rootSrc != rootDest) {
                mst.add(edge);
                totalWeight += edge.weight;
                union(parent, rank, rootSrc, rootDest);
                System.out.println("  Added: " + edge);

                // MST is complete when it has V-1 edges
                if (mst.size() == V - 1) break;
            } else {
                System.out.println("  Skipped (would form cycle): " + edge);
            }
        }

        System.out.println("─────────────────────────────────────────");
        System.out.println("Total MST Weight (Kruskal's): " + totalWeight);
    }

    /**
     * UNION-FIND: Find with Path Compression.
     * Path compression flattens the tree for future fast lookups.
     */
    private static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);  // Path compression
        }
        return parent[x];
    }

    /**
     * UNION-FIND: Union by Rank.
     * Attaches smaller tree under the root of the taller tree.
     * Prevents the tree from becoming too tall (keeps find() fast).
     */
    private static void union(int[] parent, int[] rank, int x, int y) {
        if (rank[x] < rank[y]) {
            parent[x] = y;
        } else if (rank[x] > rank[y]) {
            parent[y] = x;
        } else {
            parent[y] = x;
            rank[x]++;
        }
    }


    // ============================================================
    //  MAIN — Demo & Testing
    // ============================================================
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("   MINIMUM SPANNING TREE ALGORITHMS DEMONSTRATION");
        System.out.println("====================================================");

        /*
         * Weighted Undirected Graph:
         *
         *       2       3
         *   0 ----- 1 ----- 2
         *   |  \    |    /  |
         *   6    8  5  4    7
         *   |      \|/      |
         *   3 ----- 4 ----- 5
         *       9       10
         *
         * Adjacency Matrix (0 = no edge):
         */
        int V = 5;
        int[][] graph = {
            //  0   1   2   3   4
            {   0,  2,  0,  6,  0 },  // 0
            {   2,  0,  3,  8,  5 },  // 1
            {   0,  3,  0,  0,  7 },  // 2
            {   6,  8,  0,  0,  9 },  // 3
            {   0,  5,  7,  9,  0 }   // 4
        };

        System.out.println("\nGraph (adjacency matrix, 0 = no edge):");
        System.out.println("   0  1  2  3  4");
        String[] rows = {"0", "1", "2", "3", "4"};
        for (int i = 0; i < V; i++) {
            System.out.print(rows[i] + " ");
            System.out.println(Arrays.toString(graph[i]));
        }

        // ---- PRIM'S ----
        System.out.println("\n" + "=".repeat(42));
        primsAlgorithm(graph, V);

        // ---- KRUSKAL'S ----
        System.out.println("\n" + "=".repeat(42));
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (graph[i][j] != 0) {
                    edges.add(new Edge(i, j, graph[i][j]));
                }
            }
        }
        kruskalsAlgorithm(V, edges);

        System.out.println("\n====================================================");
        System.out.println("Complexity: Prim's = O(E log V) | Kruskal's = O(E log E)");
        System.out.println("====================================================");
    }
}
