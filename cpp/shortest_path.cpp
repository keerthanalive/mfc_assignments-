/*
 * ============================================================
 *  SHORTEST PATH ALGORITHMS — C++ Complete Implementation
 * ============================================================
 *
 *  Algorithms:
 *    1. Dijkstra's Algorithm    — Single source, non-negative weights
 *    2. Bellman-Ford Algorithm  — Single source, handles negative weights
 *    3. Floyd-Warshall Algorithm — All-pairs shortest paths (DP)
 *
 *  Complexity:
 *  ┌──────────────────────┬────────────────┬──────────┬──────────────────┐
 *  │  Algorithm           │  Time          │  Space   │  Neg Weights?    │
 *  ├──────────────────────┼────────────────┼──────────┼──────────────────┤
 *  │  Dijkstra's (heap)   │  O(E log V)    │  O(V)    │  NO              │
 *  │  Bellman-Ford        │  O(VE)         │  O(V)    │  YES + neg cycle │
 *  │  Floyd-Warshall      │  O(V³)         │  O(V²)   │  YES (no cycle)  │
 *  └──────────────────────┴────────────────┴──────────┴──────────────────┘
 *  V = vertices, E = edges
 *
 *  Compile: g++ -std=c++17 -o shortest shortest_path.cpp
 *  Run:     ./shortest
 * ============================================================
 */

#include <iostream>
#include <vector>
#include <queue>
#include <climits>
#include <algorithm>
using namespace std;

const int INF = INT_MAX / 2;  // Use /2 to safely add without overflow

// ─────────────────────────────────────────────────────────────
//  EDGE for Dijkstra's adjacency list
// ─────────────────────────────────────────────────────────────
struct Edge {
    int dest, weight;
};


// ============================================================
//  1. DIJKSTRA'S ALGORITHM
// ============================================================
/*
 * IDEA (Greedy):
 *   Greedily pick the unvisited vertex with the smallest known distance,
 *   then RELAX all its outgoing edges:
 *     dist[v] = min(dist[v], dist[u] + w(u,v))
 *
 * ALGORITHM:
 *   1. dist[src] = 0; dist[all others] = INF.
 *   2. Push {0, src} into Min-Heap.
 *   3. While heap not empty:
 *      a. Extract (d, u) with min d.
 *      b. Skip if d > dist[u] (stale entry).
 *      c. Relax all edges from u.
 *   4. dist[] contains shortest distances from src.
 *
 * COMPLEXITY:
 *   Time:  O(E log V)
 *   Space: O(V)
 *
 * LIMITATION: Does NOT work with NEGATIVE weights!
 *
 * APPLICATIONS:
 *   GPS navigation, OSPF routing, A* (with heuristic).
 */
vector<int> dijkstra(const vector<vector<Edge>>& adj, int V, int src) {
    vector<int> dist(V, INF);
    dist[src] = 0;

    // Min-Heap: {distance, vertex}
    priority_queue<pair<int,int>,
                   vector<pair<int,int>>,
                   greater<pair<int,int>>> pq;
    pq.push({0, src});

    while (!pq.empty()) {
        auto [d, u] = pq.top(); pq.pop();

        if (d > dist[u]) continue;  // stale entry — skip

        for (const Edge& e : adj[u]) {
            int newDist = dist[u] + e.weight;
            if (newDist < dist[e.dest]) {
                dist[e.dest] = newDist;
                pq.push({dist[e.dest], e.dest});
            }
        }
    }
    return dist;
}


// ============================================================
//  2. BELLMAN-FORD ALGORITHM
// ============================================================
/*
 * IDEA (Dynamic Programming):
 *   Relax ALL edges V-1 times.
 *   After k iterations, dist[v] = shortest path using ≤ k edges.
 *   Any simple shortest path uses at most V-1 edges.
 *
 * ALGORITHM:
 *   1. dist[src] = 0; dist[all others] = INF.
 *   2. Repeat V-1 times:
 *        For each edge (u, v, w): relax if dist[u] + w < dist[v].
 *   3. Negative Cycle Detection (V-th pass):
 *        If any dist can still decrease → NEGATIVE CYCLE!
 *
 * COMPLEXITY:
 *   Time:  O(VE)
 *   Space: O(V)
 *
 * ADVANTAGES over Dijkstra's:
 *   ✓ Handles negative edge weights.
 *   ✓ Detects negative cycles.
 *
 * APPLICATIONS:
 *   Currency arbitrage, RIP routing protocol.
 */
void bellmanFord(int V, const vector<tuple<int,int,int>>& edges, int src) {
    vector<int> dist(V, INF);
    dist[src] = 0;

    // Relax all edges V-1 times
    for (int iter = 1; iter <= V - 1; iter++) {
        bool updated = false;
        for (auto& [u, v, w] : edges) {
            if (dist[u] != INF && dist[u] + w < dist[v]) {
                dist[v]  = dist[u] + w;
                updated  = true;
            }
        }
        if (!updated) {
            cout << "  [Optimization] Converged at iteration " << iter << "\n";
            break;
        }
    }

    // V-th pass: detect negative cycle
    bool negCycle = false;
    for (auto& [u, v, w] : edges) {
        if (dist[u] != INF && dist[u] + w < dist[v]) {
            negCycle = true; break;
        }
    }

    cout << "Bellman-Ford from vertex " << src << ":\n";
    cout << string(40, '-') << "\n";
    if (negCycle) {
        cout << "  !! NEGATIVE CYCLE DETECTED — distances unreliable !!\n";
    } else {
        for (int i = 0; i < V; i++) {
            cout << "  Vertex " << src << " -> " << i << " : ";
            if (dist[i] == INF) cout << "INF (unreachable)\n";
            else                cout << dist[i] << "\n";
        }
    }
}


// ============================================================
//  3. FLOYD-WARSHALL ALGORITHM
// ============================================================
/*
 * IDEA (All-Pairs DP):
 *   For each intermediate vertex k, update shortest paths between
 *   all pairs (i, j) by checking if going through k is shorter:
 *
 *     dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])
 *
 * ALGORITHM:
 *   1. Init dist[i][j] = weight(i,j) if edge exists, INF otherwise.
 *      dist[i][i] = 0.
 *   2. For k = 0 to V-1:
 *        For all pairs (i, j): relax via k.
 *   3. Negative cycle: if dist[i][i] < 0 for any i.
 *
 * COMPLEXITY:
 *   Time:  O(V³)
 *   Space: O(V²) — distance matrix
 *
 * ADVANTAGE: All pairs in one run.
 * APPLICATIONS:
 *   Network latency, transitive closure, all-city route planning.
 */
void floydWarshall(vector<vector<int>> dist, int V) {
    // dist[][] passed by value — already initialized

    for (int k = 0; k < V; k++)
        for (int i = 0; i < V; i++)
            for (int j = 0; j < V; j++)
                if (dist[i][k] != INF && dist[k][j] != INF)
                    dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j]);

    // Negative cycle detection
    for (int i = 0; i < V; i++) {
        if (dist[i][i] < 0) {
            cout << "!! NEGATIVE CYCLE DETECTED (Floyd-Warshall) !!\n";
            return;
        }
    }

    cout << "Floyd-Warshall — All-Pairs Shortest Path Matrix:\n";
    cout << string(46, '-') << "\n";
    cout << "     ";
    for (int j = 0; j < V; j++) cout << "  " << j << "   ";
    cout << "\n";

    for (int i = 0; i < V; i++) {
        cout << "  " << i << "  ";
        for (int j = 0; j < V; j++) {
            if (dist[i][j] == INF) cout << "  INF ";
            else                   printf("  %-4d", dist[i][j]);
        }
        cout << "\n";
    }
}


// ============================================================
//  MAIN — Demo & Testing
// ============================================================
int main() {
    cout << "====================================================\n";
    cout << "   SHORTEST PATH ALGORITHMS DEMONSTRATION (C++)\n";
    cout << "====================================================\n\n";

    // ─── DIJKSTRA'S ───────────────────────────────────────────
    cout << "--- DIJKSTRA'S ALGORITHM ---\n";
    cout << "Graph (directed, non-negative weights):\n";
    cout << "  0->1(10), 0->3(5), 1->2(1), 1->3(2), 2->4(4)\n";
    cout << "  3->1(3),  3->2(9), 3->4(2), 4->0(7), 4->2(6)\n\n";

    int Vd = 5;
    vector<vector<Edge>> adjD(Vd);
    auto addD = [&](int u, int v, int w){ adjD[u].push_back({v, w}); };
    addD(0,1,10); addD(0,3,5);
    addD(1,2,1);  addD(1,3,2);
    addD(2,4,4);
    addD(3,1,3);  addD(3,2,9);  addD(3,4,2);
    addD(4,0,7);  addD(4,2,6);

    vector<int> dDist = dijkstra(adjD, Vd, 0);
    cout << "Shortest distances from vertex 0:\n";
    cout << string(40, '-') << "\n";
    for (int i = 0; i < Vd; i++)
        cout << "  Vertex 0 -> " << i << " : " << dDist[i] << "\n";
    cout << "  Complexity: O(E log V) | No negative weights!\n";

    // ─── BELLMAN-FORD ──────────────────────────────────────────
    cout << "\n--- BELLMAN-FORD ALGORITHM ---\n";
    cout << "Graph (directed, with negative edge):\n";
    cout << "  0->1(4), 0->2(5), 1->2(-3), 1->3(6), 2->4(2), 3->4(1)\n\n";

    int Vb = 5;
    // edges as (src, dest, weight)
    vector<tuple<int,int,int>> edgesBF = {
        {0,1, 4}, {0,2, 5},
        {1,2,-3}, {1,3, 6},
        {2,4, 2}, {3,4, 1}
    };
    bellmanFord(Vb, edgesBF, 0);
    cout << "  Complexity: O(VE) | Handles negative weights!\n";

    // ─── FLOYD-WARSHALL ────────────────────────────────────────
    cout << "\n--- FLOYD-WARSHALL ALGORITHM ---\n";
    cout << "Graph (4 vertices, directed, weighted):\n\n";

    int Vf = 4;
    vector<vector<int>> graphFW = {
        {0,   3,   INF, 7  },
        {8,   0,   2,   INF},
        {5,   INF, 0,   1  },
        {2,   INF, INF, 0  }
    };
    floydWarshall(graphFW, Vf);
    cout << "  Complexity: O(V^3) | ALL pairs of shortest paths!\n";

    cout << "\n====================================================\n";
    cout << "Summary:\n";
    cout << "  Dijkstra's     = O(E log V)  -- Non-negative, single source\n";
    cout << "  Bellman-Ford   = O(VE)       -- Negative weights, neg cycle detect\n";
    cout << "  Floyd-Warshall = O(V^3)      -- All pairs, handles neg weights\n";
    cout << "====================================================\n";
    return 0;
}
