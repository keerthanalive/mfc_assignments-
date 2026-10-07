package com.jeenify.algorithms.graphs;

import java.util.*;

/**
 * ============================================================
 *  SHORTEST PATH ALGORITHMS
 * ============================================================
 *
 *  Topics Covered:
 *    1. Dijkstra's Algorithm   — Single-source, non-negative weights
 *    2. Bellman-Ford Algorithm — Single-source, handles negative weights/cycles
 *    3. Floyd-Warshall Algorithm — All-pairs shortest paths
 *
 *  Complexity Comparison:
 *  ┌─────────────────────────┬────────────────┬──────────┬──────────────────────────┐
 *  │  Algorithm              │  Time          │  Space   │  Negative Weights?       │
 *  ├─────────────────────────┼────────────────┼──────────┼──────────────────────────┤
 *  │  Dijkstra's (heap)      │  O(E log V)    │  O(V)    │  NO (must be ≥ 0)        │
 *  │  Bellman-Ford           │  O(VE)         │  O(V)    │  YES + detects neg cycle │
 *  │  Floyd-Warshall         │  O(V³)         │  O(V²)   │  YES (no neg cycles)     │
 *  └─────────────────────────┴────────────────┴──────────┴──────────────────────────┘
 *  V = vertices, E = edges
 *
 * ============================================================
 */
public class ShortestPath {

    // ============================================================
    //  HELPER: Edge for adjacency list representation
    // ============================================================
    static class Edge {
        int dest, weight;

        Edge(int dest, int weight) {
            this.dest   = dest;
            this.weight = weight;
        }
    }

    static final int INF = Integer.MAX_VALUE / 2;  // Use /2 to avoid overflow in addition


    // ============================================================
    //  1. DIJKSTRA'S ALGORITHM
    // ============================================================
    /**
     * Dijkstra's Algorithm — Single-Source Shortest Path
     *
     * IDEA:
     *   Greedily picks the vertex with the SMALLEST known distance,
     *   then RELAXES all its outgoing edges (updates neighbors' distances
     *   if a shorter path is found through current vertex).
     *
     * ALGORITHM STEPS:
     *   1. Initialize dist[source] = 0, dist[all others] = ∞.
     *   2. Insert all vertices into a Min-Heap (Priority Queue) keyed by dist.
     *   3. Extract vertex u with minimum dist[u] from heap.
     *   4. For each neighbor v of u:
     *      If dist[u] + weight(u,v) < dist[v], RELAX: update dist[v].
     *      Add v to heap with new dist[v].
     *   5. Repeat until heap is empty.
     *
     * EDGE RELAXATION:
     *   dist[v] = min(dist[v],  dist[u] + weight(u,v))
     *
     * COMPLEXITY:
     *   - Time:  O(E log V) with adjacency list + Min-Heap
     *   - Space: O(V) for distance array
     *
     * LIMITATION:
     *   ⚠ Does NOT work with NEGATIVE edge weights!
     *   (Greedy assumption breaks: a shorter path might exist through a negative edge)
     *
     * APPLICATIONS:
     *   - GPS / Maps (Google Maps, Waze).
     *   - Network routing protocols (OSPF).
     *   - Game AI pathfinding (with A* extension).
     *
     * @param adjList  adjacency list representation of the graph
     * @param V        total number of vertices
     * @param src      source vertex
     * @return         dist[] where dist[i] = shortest distance from src to i
     */
    public static int[] dijkstra(List<List<Edge>> adjList, int V, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // Min-Heap: {distance, vertex}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, src});

        while (!pq.isEmpty()) {
            int[] curr     = pq.poll();
            int currDist   = curr[0];
            int u          = curr[1];

            // Skip outdated entries in the heap (stale distance)
            if (currDist > dist[u]) continue;

            // Relax all edges from u
            for (Edge edge : adjList.get(u)) {
                int v = edge.dest;
                int newDist = dist[u] + edge.weight;

                if (newDist < dist[v]) {
                    dist[v] = newDist;            // RELAX edge
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }

        return dist;
    }


    // ============================================================
    //  2. BELLMAN-FORD ALGORITHM
    // ============================================================
    /**
     * Bellman-Ford Algorithm — Single-Source Shortest Path
     *
     * IDEA:
     *   Relax ALL edges V-1 times.
     *   After k iterations, dist[v] gives the shortest path using at most k edges.
     *   After V-1 iterations, all shortest paths are found (since any shortest
     *   path in a graph without negative cycles has at most V-1 edges).
     *
     * ALGORITHM STEPS:
     *   1. Initialize dist[source] = 0, dist[all others] = ∞.
     *   2. Repeat V-1 times:
     *      For EVERY edge (u, v, w):
     *        If dist[u] + w < dist[v], relax: dist[v] = dist[u] + w.
     *   3. NEGATIVE CYCLE DETECTION:
     *      Do one more (V-th) relaxation pass.
     *      If any dist[v] can still be improved → NEGATIVE CYCLE exists!
     *
     * COMPLEXITY:
     *   - Time:  O(VE) — V-1 passes, each relaxing E edges
     *   - Space: O(V)
     *
     * KEY ADVANTAGES OVER DIJKSTRA'S:
     *   ✅ Handles NEGATIVE edge weights.
     *   ✅ Detects NEGATIVE CYCLES (Dijkstra's cannot).
     *
     * APPLICATIONS:
     *   - Financial arbitrage detection (negative cycles = profit loop).
     *   - Network distance vector routing protocols (RIP protocol).
     *   - Currency exchange rate calculations.
     *
     * @param V       number of vertices
     * @param edges   list of all edges [src, dest, weight]
     * @param src     source vertex
     */
    public static void bellmanFord(int V, int[][] edges, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // Step 2: Relax ALL edges V-1 times
        for (int iteration = 1; iteration <= V - 1; iteration++) {
            boolean updated = false;

            for (int[] edge : edges) {
                int u = edge[0], v = edge[1], w = edge[2];

                if (dist[u] != INF && dist[u] + w < dist[v]) {
                    dist[v]  = dist[u] + w;
                    updated  = true;
                }
            }

            // Optimization: if no update happened, we can stop early
            if (!updated) {
                System.out.println("  [Optimization] Converged after " + iteration + " iterations");
                break;
            }
        }

        // Step 3: Detect negative cycle (one more pass)
        boolean hasNegCycle = false;
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], w = edge[2];
            if (dist[u] != INF && dist[u] + w < dist[v]) {
                hasNegCycle = true;
                break;
            }
        }

        // Print results
        System.out.println("Bellman-Ford from vertex " + src + ":");
        System.out.println("─────────────────────────────────────");
        if (hasNegCycle) {
            System.out.println("  ⚠ NEGATIVE CYCLE DETECTED! Distances are unreliable.");
        } else {
            for (int i = 0; i < V; i++) {
                String d = (dist[i] == INF) ? "∞ (unreachable)" : String.valueOf(dist[i]);
                System.out.println("  Vertex " + src + " → " + i + " : " + d);
            }
        }
    }


    // ============================================================
    //  3. FLOYD-WARSHALL ALGORITHM
    // ============================================================
    /**
     * Floyd-Warshall Algorithm — All-Pairs Shortest Paths
     *
     * IDEA:
     *   Use DYNAMIC PROGRAMMING.
     *   Consider each vertex k as an "intermediate" vertex.
     *   Check if going through k gives a shorter path from i to j.
     *
     *   dist[i][j] = min(dist[i][j],  dist[i][k] + dist[k][j])
     *
     * ALGORITHM STEPS:
     *   1. Initialize dist[i][j] = weight of edge (i,j) if it exists, else ∞.
     *      dist[i][i] = 0 (distance to self is zero).
     *   2. For each intermediate vertex k (0 to V-1):
     *      For each pair (i, j):
     *        If dist[i][k] + dist[k][j] < dist[i][j]:
     *          Update dist[i][j] = dist[i][k] + dist[k][j].
     *   3. After all vertices have been tried as intermediates,
     *      dist[i][j] contains the shortest path from i to j.
     *   4. NEGATIVE CYCLE DETECTION: if dist[i][i] < 0 for any i → negative cycle.
     *
     * COMPLEXITY:
     *   - Time:  O(V³)  — three nested loops
     *   - Space: O(V²)  — V×V distance matrix
     *
     * ADVANTAGE: Gives shortest paths between ALL pairs of vertices in one run.
     * LIMITATION: Expensive for large graphs (V > ~500 becomes slow).
     *
     * APPLICATIONS:
     *   - Finding shortest routes between all city pairs.
     *   - Transitive closure of a directed graph.
     *   - Network latency measurement.
     *
     * @param graph  V×V adjacency matrix (INF if no direct edge, 0 on diagonal)
     * @param V      number of vertices
     */
    public static void floydWarshall(int[][] graph, int V) {
        // Create a copy of graph as dist matrix
        int[][] dist = new int[V][V];
        for (int i = 0; i < V; i++) {
            dist[i] = Arrays.copyOf(graph[i], V);
        }

        // Step 2: Try every vertex k as intermediate
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    // Relax: path i→k→j vs direct i→j
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }

        // Step 4: Check for negative cycles
        for (int i = 0; i < V; i++) {
            if (dist[i][i] < 0) {
                System.out.println("⚠ NEGATIVE CYCLE DETECTED in Floyd-Warshall!");
                return;
            }
        }

        // Print the distance matrix
        System.out.println("Floyd-Warshall — All-Pairs Shortest Path Matrix:");
        System.out.println("─────────────────────────────────────────────────");
        System.out.print("      ");
        for (int i = 0; i < V; i++) System.out.printf("  %-5d", i);
        System.out.println();

        for (int i = 0; i < V; i++) {
            System.out.printf("  %d  ", i);
            for (int j = 0; j < V; j++) {
                if (dist[i][j] == INF) {
                    System.out.printf("  %-5s", "INF");
                } else {
                    System.out.printf("  %-5d", dist[i][j]);
                }
            }
            System.out.println();
        }
    }


    // ============================================================
    //  MAIN — Demo & Testing
    // ============================================================
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("      SHORTEST PATH ALGORITHMS DEMONSTRATION");
        System.out.println("====================================================\n");

        int V = 5;

        // ---- DIJKSTRA'S ----
        System.out.println("─── DIJKSTRA'S ALGORITHM ───");
        System.out.println("Graph (directed, weighted, non-negative):");
        System.out.println("  0→1(10), 0→3(5), 1→2(1), 1→3(2), 2→4(4), 3→1(3), 3→2(9), 3→4(2), 4→0(7), 4→2(6)");

        List<List<Edge>> adjList = new ArrayList<>();
        for (int i = 0; i < V; i++) adjList.add(new ArrayList<>());
        adjList.get(0).add(new Edge(1, 10));
        adjList.get(0).add(new Edge(3, 5));
        adjList.get(1).add(new Edge(2, 1));
        adjList.get(1).add(new Edge(3, 2));
        adjList.get(2).add(new Edge(4, 4));
        adjList.get(3).add(new Edge(1, 3));
        adjList.get(3).add(new Edge(2, 9));
        adjList.get(3).add(new Edge(4, 2));
        adjList.get(4).add(new Edge(0, 7));
        adjList.get(4).add(new Edge(2, 6));

        int[] dijkDist = dijkstra(adjList, V, 0);
        System.out.println("Dijkstra's from vertex 0:");
        System.out.println("─────────────────────────────────────");
        for (int i = 0; i < V; i++) {
            System.out.println("  Vertex 0 → " + i + " : " + dijkDist[i]);
        }
        System.out.println("  Complexity: O(E log V) | No negative weights!");

        // ---- BELLMAN-FORD ----
        System.out.println("\n─── BELLMAN-FORD ALGORITHM ───");
        System.out.println("Graph (directed, with negative weights):");
        System.out.println("  0→1(4), 0→2(5), 1→2(-3), 1→3(6), 2→4(2), 3→4(1)");

        int Vbf = 5;
        int[][] edgesBF = {
            {0, 1,  4},
            {0, 2,  5},
            {1, 2, -3},  // Negative edge!
            {1, 3,  6},
            {2, 4,  2},
            {3, 4,  1}
        };
        bellmanFord(Vbf, edgesBF, 0);
        System.out.println("  Complexity: O(VE) | Handles negative weights!");

        // ---- FLOYD-WARSHALL ----
        System.out.println("\n─── FLOYD-WARSHALL ALGORITHM ───");
        System.out.println("Graph (4 vertices, directed, weighted):");

        int Vfw = 4;
        int[][] graphFW = {
            {0,   3,   INF, 7  },
            {8,   0,   2,   INF},
            {5,   INF, 0,   1  },
            {2,   INF, INF, 0  }
        };
        floydWarshall(graphFW, Vfw);
        System.out.println("  Complexity: O(V³) | ALL pairs of shortest paths!");

        System.out.println("\n====================================================");
        System.out.println("Summary:");
        System.out.println("  Dijkstra's   = O(E log V)  — Non-negative weights, single source");
        System.out.println("  Bellman-Ford = O(VE)       — Negative weights, detects neg cycles");
        System.out.println("  Floyd-Warshall = O(V³)     — All pairs, handles neg weights");
        System.out.println("====================================================");
    }
}
