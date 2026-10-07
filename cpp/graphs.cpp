/*
 * ============================================================
 *  GRAPH TRAVERSAL — BFS & DFS — C++ Complete Implementation
 * ============================================================
 *
 *  Topics:
 *    1. BFS — Breadth-First Search  (level by level, uses Queue)
 *    2. DFS — Depth-First Search    (deep first, uses Stack/Recursion)
 *
 *  Graph Representation: Adjacency List  (vector<vector<int>>)
 *
 *  Complexity:
 *  ┌──────────────────┬──────────────────┬──────────────────────────────┐
 *  │  Algorithm       │  Time            │  Space                       │
 *  ├──────────────────┼──────────────────┼──────────────────────────────┤
 *  │  BFS             │  O(V + E)        │  O(V) — visited + queue      │
 *  │  DFS             │  O(V + E)        │  O(V) — visited + call stack │
 *  └──────────────────┴──────────────────┴──────────────────────────────┘
 *  V = vertices, E = edges
 *
 *  Compile: g++ -std=c++17 -o graphs graphs.cpp
 *  Run:     ./graphs
 * ============================================================
 */

#include <iostream>
#include <vector>
#include <queue>
#include <stack>
#include <climits>
using namespace std;


// ─────────────────────────────────────────────────────────────
//  GRAPH CLASS — Adjacency List (Undirected)
// ─────────────────────────────────────────────────────────────
class Graph {
private:
    int V;                        // Number of vertices
    vector<vector<int>> adjList;  // Adjacency list

public:
    Graph(int vertices) : V(vertices), adjList(vertices) {}

    // Add undirected edge between u and v
    void addEdge(int u, int v) {
        adjList[u].push_back(v);
        adjList[v].push_back(u);  // Remove for directed graph
    }

    void printGraph() {
        cout << "Adjacency List:\n";
        for (int i = 0; i < V; i++) {
            cout << "  Node " << i << " -> ";
            for (int nb : adjList[i]) cout << nb << " ";
            cout << "\n";
        }
    }


    // ============================================================
    //  1. BFS — BREADTH-FIRST SEARCH
    // ============================================================
    /*
     * HOW IT WORKS:
     *   Start at source. Visit ALL neighbors at current level before
     *   going deeper. Uses a QUEUE (FIFO).
     *
     * ALGORITHM:
     *   1. Mark source visited; enqueue.
     *   2. Dequeue node; process it.
     *   3. Enqueue all unvisited neighbors.
     *   4. Repeat until queue empty.
     *
     * COMPLEXITY:
     *   Time:  O(V + E)
     *   Space: O(V)
     *
     * KEY PROPERTY:
     *   BFS finds SHORTEST PATH (min edges) in unweighted graphs.
     *
     * APPLICATIONS:
     *   - Shortest path (unweighted).
     *   - Level-order traversal of trees.
     *   - Web crawlers, social network analysis, bipartite checking.
     */
    void bfs(int start) {
        vector<bool> visited(V, false);
        queue<int> q;

        visited[start] = true;
        q.push(start);

        cout << "BFS from node " << start << ": ";
        while (!q.empty()) {
            int node = q.front(); q.pop();
            cout << node << " ";

            for (int neighbor : adjList[node]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    q.push(neighbor);
                }
            }
        }
        cout << "\n";
    }

    // BFS with shortest path tracking (unweighted graph)
    // Returns dist[] where dist[i] = min edges from start to i (-1 = unreachable)
    vector<int> bfsShortestPath(int start) {
        vector<int>  dist(V, -1);
        vector<bool> visited(V, false);
        queue<int>   q;

        visited[start] = true;
        dist[start]    = 0;
        q.push(start);

        while (!q.empty()) {
            int node = q.front(); q.pop();
            for (int nb : adjList[node]) {
                if (!visited[nb]) {
                    visited[nb] = true;
                    dist[nb]    = dist[node] + 1;
                    q.push(nb);
                }
            }
        }
        return dist;
    }


    // ============================================================
    //  2. DFS — DEPTH-FIRST SEARCH (Recursive)
    // ============================================================
    /*
     * HOW IT WORKS:
     *   Start at source. Go as DEEP as possible along one path,
     *   then BACKTRACK and explore the next path. Uses recursion
     *   (implicit call stack) or explicit Stack.
     *
     * ALGORITHM:
     *   1. Mark current node visited; process it.
     *   2. Recursively visit each unvisited neighbor.
     *   3. Backtrack when all neighbors are visited.
     *
     * COMPLEXITY:
     *   Time:  O(V + E)
     *   Space: O(V) visited + O(h) recursion stack (h = max depth)
     *
     * APPLICATIONS:
     *   - Cycle detection.
     *   - Topological sorting (DAG).
     *   - Finding strongly connected components.
     *   - Maze/puzzle solving (backtracking).
     */
    void dfsHelper(int node, vector<bool>& visited) {
        visited[node] = true;
        cout << node << " ";
        for (int nb : adjList[node]) {
            if (!visited[nb]) dfsHelper(nb, visited);
        }
    }

    void dfsRecursive(int start) {
        vector<bool> visited(V, false);
        cout << "DFS Recursive from node " << start << ": ";
        dfsHelper(start, visited);
        cout << "\n";
    }

    // DFS — Iterative (explicit Stack)
    // Avoids stack overflow on very deep graphs.
    void dfsIterative(int start) {
        vector<bool> visited(V, false);
        stack<int>   stk;

        stk.push(start);
        cout << "DFS Iterative from node " << start << ": ";

        while (!stk.empty()) {
            int node = stk.top(); stk.pop();
            if (!visited[node]) {
                visited[node] = true;
                cout << node << " ";
                // Push in reverse so left neighbor processed first
                for (int i = (int)adjList[node].size() - 1; i >= 0; i--) {
                    if (!visited[adjList[node][i]])
                        stk.push(adjList[node][i]);
                }
            }
        }
        cout << "\n";
    }

    // ─── Cycle Detection (Undirected Graph) using DFS ──────────
    // A visited neighbor that is not our parent → back edge → cycle!
    bool dfsCycleHelper(int node, vector<bool>& visited, int parent) {
        visited[node] = true;
        for (int nb : adjList[node]) {
            if (!visited[nb]) {
                if (dfsCycleHelper(nb, visited, node)) return true;
            } else if (nb != parent) {
                return true;  // Found cycle
            }
        }
        return false;
    }

    bool hasCycle() {
        vector<bool> visited(V, false);
        for (int i = 0; i < V; i++)
            if (!visited[i])
                if (dfsCycleHelper(i, visited, -1)) return true;
        return false;
    }

    // ─── Connectivity Check using DFS ──────────────────────────
    bool isConnected() {
        vector<bool> visited(V, false);
        dfsHelper(0, visited);
        for (bool v : visited)
            if (!v) return false;
        return true;
    }
};


// ============================================================
//  MAIN — Demo & Testing
// ============================================================
int main() {
    cout << "====================================================\n";
    cout << "  GRAPH TRAVERSAL (BFS & DFS) DEMONSTRATION (C++)\n";
    cout << "====================================================\n\n";

    /*
     * Graph:
     *   0 --- 1 --- 3
     *   |     |
     *   2 --- 4
     */
    Graph g(5);
    g.addEdge(0, 1);
    g.addEdge(0, 2);
    g.addEdge(1, 3);
    g.addEdge(1, 4);
    g.addEdge(2, 4);

    g.printGraph();
    cout << "\n";

    // BFS
    g.bfs(0);
    cout << "  -> BFS explores level by level (shortest path order)\n\n";

    // BFS Shortest Path
    vector<int> dist = g.bfsShortestPath(0);
    cout << "Shortest distances from node 0:\n";
    for (int i = 0; i < 5; i++)
        cout << "  Node 0 -> Node " << i << " : " << dist[i] << " edge(s)\n";

    cout << "\n";

    // DFS Recursive
    g.dfsRecursive(0);
    cout << "  -> DFS goes deep first, then backtracks\n\n";

    // DFS Iterative
    g.dfsIterative(0);

    cout << "\nCycle detected  : " << (g.hasCycle()    ? "Yes" : "No") << "\n";
    cout << "Graph connected : " << (g.isConnected() ? "Yes" : "No") << "\n";

    cout << "\n====================================================\n";
    cout << "Complexity: BFS = O(V+E) | DFS = O(V+E)\n";
    cout << "Space:      BFS = O(V) queue | DFS = O(V) stack\n";
    cout << "====================================================\n";
    return 0;
}
