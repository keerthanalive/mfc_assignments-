/*
 * ============================================================
 *  MINIMUM SPANNING TREE — C++ Complete Implementation
 * ============================================================
 *
 *  Algorithms:
 *    1. Prim's Algorithm   — Grow MST vertex by vertex (Min-Heap)
 *    2. Kruskal's Algorithm — Sort edges + Union-Find (DSU)
 *
 *  What is MST?
 *    A spanning tree of graph G that connects ALL vertices with
 *    minimum total edge weight, using exactly V-1 edges.
 *
 *  Complexity:
 *  ┌─────────────────────┬──────────────────┬───────────────┐
 *  │  Algorithm          │  Time            │  Space        │
 *  ├─────────────────────┼──────────────────┼───────────────┤
 *  │  Prim's (Min-Heap)  │  O(E log V)      │  O(V + E)     │
 *  │  Kruskal's          │  O(E log E)      │  O(V + E)     │
 *  └─────────────────────┴──────────────────┴───────────────┘
 *  V = vertices, E = edges
 *
 *  Compile: g++ -std=c++17 -o mst minimum_spanning_tree.cpp
 *  Run:     ./mst
 * ============================================================
 */

#include <iostream>
#include <vector>
#include <queue>
#include <algorithm>
#include <climits>
using namespace std;


// ─────────────────────────────────────────────────────────────
//  EDGE STRUCT — used by Kruskal's
// ─────────────────────────────────────────────────────────────
struct Edge {
    int src, dest, weight;
    // Sort by weight (for Kruskal's)
    bool operator<(const Edge& other) const {
        return weight < other.weight;
    }
};


// ============================================================
//  1. PRIM'S ALGORITHM
// ============================================================
/*
 * IDEA (Greedy):
 *   Start from any vertex. Repeatedly add the MINIMUM-WEIGHT edge
 *   that connects a vertex already IN the MST to one NOT YET in it.
 *   Uses a Min-Heap (priority_queue) for efficiency.
 *
 * ALGORITHM:
 *   1. key[v] = min weight edge to connect v to MST. Init = INF.
 *   2. key[src] = 0. Insert all into Min-Heap.
 *   3. Extract min-key vertex u. Add to MST.
 *   4. For each neighbor v of u:
 *        if w(u,v) < key[v]: update key[v] = w(u,v), parent[v] = u.
 *   5. Repeat until all vertices in MST.
 *
 * GREEDY JUSTIFICATION: Cut Property —
 *   The minimum edge crossing any cut (S, V\S) belongs to some MST.
 *
 * COMPLEXITY:
 *   Time:  O(E log V) with adjacency list + priority_queue
 *   Space: O(V)
 */
void primsAlgorithm(const vector<vector<pair<int,int>>>& adj, int V) {
    vector<int>  key(V, INT_MAX);    // min weight to reach vertex
    vector<int>  parent(V, -1);      // MST parent
    vector<bool> inMST(V, false);    // in MST?

    key[0] = 0;

    // Min-Heap: {weight, vertex}
    priority_queue<pair<int,int>, vector<pair<int,int>>, greater<>> pq;
    pq.push({0, 0});

    cout << "Prim's Algorithm - MST Edges:\n";
    cout << string(40, '-') << "\n";

    int totalWeight = 0;

    while (!pq.empty()) {
        int u = pq.top().second; pq.pop();

        if (inMST[u]) continue;
        inMST[u] = true;

        if (parent[u] != -1) {
            cout << "  Edge: " << parent[u] << " -- " << u
                 << "  |  Weight: " << key[u] << "\n";
            totalWeight += key[u];
        }

        for (auto [w, v] : adj[u]) {
            if (!inMST[v] && w < key[v]) {
                key[v]    = w;
                parent[v] = u;
                pq.push({key[v], v});
            }
        }
    }
    cout << string(40, '-') << "\n";
    cout << "Total MST Weight (Prim's): " << totalWeight << "\n";
}


// ============================================================
//  2. KRUSKAL'S ALGORITHM
// ============================================================
/*
 * IDEA (Greedy + DSU):
 *   Sort ALL edges by weight. Add each edge to MST if it does NOT
 *   create a cycle. Cycle detection uses Disjoint Set Union (DSU).
 *
 * ALGORITHM:
 *   1. Sort all edges ascending by weight.
 *   2. Initialize DSU: each vertex is its own set.
 *   3. For each edge (u,v,w) in sorted order:
 *        If find(u) != find(v): add to MST, union(u,v).
 *        Else: skip (would form cycle).
 *   4. Stop when MST has V-1 edges.
 *
 * DSU OPTIMIZATIONS:
 *   - Path Compression : during find(), flatten the tree.
 *   - Union by Rank    : always attach shorter tree under taller.
 *   → O(α(V)) ≈ O(1) per operation (α = inverse Ackermann).
 *
 * GREEDY JUSTIFICATION: Cycle Property —
 *   The max-weight edge in any cycle never belongs to any MST.
 *
 * COMPLEXITY:
 *   Time:  O(E log E) dominated by sorting
 *   Space: O(V + E)
 */

// DSU — Find with Path Compression
int find(vector<int>& parent, int x) {
    if (parent[x] != x)
        parent[x] = find(parent, parent[x]);  // path compression
    return parent[x];
}

// DSU — Union by Rank
void unite(vector<int>& parent, vector<int>& rank, int x, int y) {
    int rx = find(parent, x), ry = find(parent, y);
    if (rx == ry) return;
    if      (rank[rx] < rank[ry]) parent[rx] = ry;
    else if (rank[rx] > rank[ry]) parent[ry] = rx;
    else { parent[ry] = rx; rank[rx]++; }
}

void kruskalsAlgorithm(int V, vector<Edge>& edges) {
    sort(edges.begin(), edges.end());  // sort by weight

    vector<int> parent(V), rank_(V, 0);
    for (int i = 0; i < V; i++) parent[i] = i;

    vector<Edge> mst;
    int totalWeight = 0;

    cout << "Kruskal's Algorithm - MST Edges:\n";
    cout << string(42, '-') << "\n";
    cout << "Sorted edges: ";
    for (auto& e : edges)
        cout << "[" << e.src << "-" << e.dest << "," << e.weight << "] ";
    cout << "\n\n";

    for (auto& edge : edges) {
        int ru = find(parent, edge.src);
        int rv = find(parent, edge.dest);

        if (ru != rv) {  // different components → no cycle
            mst.push_back(edge);
            totalWeight += edge.weight;
            unite(parent, rank_, ru, rv);
            cout << "  Added  : " << edge.src << " -- " << edge.dest
                 << "  [weight=" << edge.weight << "]\n";
            if ((int)mst.size() == V - 1) break;  // MST complete
        } else {
            cout << "  Skipped: " << edge.src << " -- " << edge.dest
                 << "  [weight=" << edge.weight << "] (would form cycle)\n";
        }
    }
    cout << string(42, '-') << "\n";
    cout << "Total MST Weight (Kruskal's): " << totalWeight << "\n";
}


// ============================================================
//  MAIN — Demo & Testing
// ============================================================
int main() {
    cout << "====================================================\n";
    cout << " MINIMUM SPANNING TREE DEMONSTRATION (C++)\n";
    cout << "====================================================\n\n";

    /*
     * Graph (5 vertices, weighted undirected):
     *       2       3
     *   0 ----- 1 ----- 2
     *   |   \   |       |
     *   6    8  5       7
     *   |       |       |
     *   3 ----- 4 ------+
     *       9
     */
    int V = 5;

    // For Prim's — adjacency list of {weight, neighbor}
    vector<vector<pair<int,int>>> adj(V);
    auto addEdge = [&](int u, int v, int w) {
        adj[u].push_back({w, v});
        adj[v].push_back({w, u});
    };
    addEdge(0, 1, 2);
    addEdge(1, 2, 3);
    addEdge(0, 3, 6);
    addEdge(1, 3, 8);
    addEdge(1, 4, 5);
    addEdge(2, 4, 7);
    addEdge(3, 4, 9);

    primsAlgorithm(adj, V);

    cout << "\n" << string(44, '=') << "\n\n";

    // For Kruskal's — edge list
    vector<Edge> edges = {
        {0,1,2}, {1,2,3}, {0,3,6}, {1,3,8},
        {1,4,5}, {2,4,7}, {3,4,9}
    };
    kruskalsAlgorithm(V, edges);

    cout << "\n====================================================\n";
    cout << "Complexity: Prim's = O(E log V) | Kruskal's = O(E log E)\n";
    cout << "====================================================\n";
    return 0;
}
