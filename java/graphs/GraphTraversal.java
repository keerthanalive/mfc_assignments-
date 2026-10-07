package com.jeenify.algorithms.graphs;

import java.util.*;

/**
 * ============================================================
 *  GRAPH TRAVERSAL ALGORITHMS
 * ============================================================
 *
 *  Topics Covered:
 *    1. BFS — Breadth-First Search  (explores level by level)
 *    2. DFS — Depth-First Search    (explores as deep as possible)
 *
 *  Graph Representation: Adjacency List
 *
 *  Complexity:
 *  ┌──────────────────┬──────────────────┬──────────────────────────────┐
 *  │  Algorithm       │  Time            │  Space                       │
 *  ├──────────────────┼──────────────────┼──────────────────────────────┤
 *  │  BFS             │  O(V + E)        │  O(V) — visited + queue      │
 *  │  DFS             │  O(V + E)        │  O(V) — visited + call stack │
 *  └──────────────────┴──────────────────┴──────────────────────────────┘
 *  V = number of vertices, E = number of edges
 *
 * ============================================================
 */
public class GraphTraversal {

    // Number of vertices in the graph
    private int vertices;

    // Adjacency list representation of the graph
    // Each index stores a list of neighboring vertices
    private List<List<Integer>> adjList;

    /**
     * Constructor — initializes an undirected graph with 'vertices' nodes.
     * @param vertices  total number of nodes (0-indexed)
     */
    public GraphTraversal(int vertices) {
        this.vertices = vertices;
        this.adjList  = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    /**
     * Add an undirected edge between node u and node v.
     * Adding both directions makes it undirected.
     */
    public void addEdge(int u, int v) {
        adjList.get(u).add(v);
        adjList.get(v).add(u);  // Remove this line for directed graph
    }

    /**
     * Print the adjacency list representation of the graph.
     */
    public void printGraph() {
        System.out.println("Adjacency List Representation:");
        for (int i = 0; i < vertices; i++) {
            System.out.print("  Node " + i + " → ");
            System.out.println(adjList.get(i));
        }
    }


    // ============================================================
    //  1. BFS — BREADTH-FIRST SEARCH
    // ============================================================
    /**
     * Breadth-First Search (BFS)
     *
     * HOW IT WORKS:
     *   - Start at a source node.
     *   - Visit ALL neighbors at the current level (distance) before
     *     moving deeper. Uses a QUEUE (FIFO).
     *
     * ALGORITHM STEPS:
     *   1. Mark start node as visited; add to queue.
     *   2. Dequeue a node; process it.
     *   3. Enqueue all unvisited neighbors of that node.
     *   4. Repeat until queue is empty.
     *
     * VISUAL EXAMPLE:
     *   Graph:  0 - 1 - 3
     *           |   |
     *           2 - 4
     *
     *   BFS from 0:  0 → 1, 2 → 3, 4   (level by level)
     *
     * COMPLEXITY:
     *   - Time:  O(V + E)
     *   - Space: O(V) for visited array and queue
     *
     * APPLICATIONS:
     *   - Shortest path in unweighted graph.
     *   - Level-order traversal of trees.
     *   - Web crawlers (explore web links level by level).
     *   - Social network: finding friends-of-friends (degrees of separation).
     *   - Bipartite graph checking.
     *
     * @param start  the starting vertex
     */
    public void bfs(int start) {
        // Track visited nodes to avoid revisiting
        boolean[] visited = new boolean[vertices];

        // Queue for BFS (FIFO order)
        Queue<Integer> queue = new LinkedList<>();

        // Step 1: Mark start node visited and enqueue
        visited[start] = true;
        queue.add(start);

        System.out.print("BFS Traversal from node " + start + ": ");

        while (!queue.isEmpty()) {
            // Step 2: Dequeue a vertex and print it
            int node = queue.poll();
            System.out.print(node + " ");

            // Step 3: Explore all adjacent (neighboring) vertices
            for (int neighbor : adjList.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;  // Mark as visited before enqueueing
                    queue.add(neighbor);        // Enqueue for later processing
                }
            }
        }
        System.out.println();
    }

    /**
     * BFS that also tracks the SHORTEST PATH (number of edges) from source.
     * Only works correctly for unweighted graphs.
     *
     * @param start  source vertex
     * @return       array where dist[i] = shortest distance from start to vertex i
     *               (-1 if unreachable)
     */
    public int[] bfsShortestPath(int start) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, -1);

        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();

        visited[start] = true;
        dist[start] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : adjList.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    dist[neighbor] = dist[node] + 1;  // One edge further
                    queue.add(neighbor);
                }
            }
        }
        return dist;
    }


    // ============================================================
    //  2. DFS — DEPTH-FIRST SEARCH
    // ============================================================
    /**
     * Depth-First Search (DFS) — Recursive Implementation
     *
     * HOW IT WORKS:
     *   - Start at a source node.
     *   - Go as DEEP as possible along one path before backtracking.
     *   - Uses recursion (implicit stack) or an explicit STACK (LIFO).
     *
     * ALGORITHM STEPS:
     *   1. Mark current node as visited; process it.
     *   2. For each unvisited neighbor, recursively call DFS.
     *   3. Backtrack when all neighbors are visited.
     *
     * VISUAL EXAMPLE:
     *   Graph:  0 - 1 - 3
     *           |   |
     *           2 - 4
     *
     *   DFS from 0:  0 → 1 → 3 (backtrack) → 4 (backtrack) → 2  (deep first!)
     *
     * COMPLEXITY:
     *   - Time:  O(V + E)
     *   - Space: O(V) for visited array + O(h) recursion stack (h = max depth)
     *
     * APPLICATIONS:
     *   - Cycle detection in graphs.
     *   - Topological sorting (of DAGs).
     *   - Solving mazes and puzzles.
     *   - Finding strongly connected components (Tarjan's, Kosaraju's).
     *   - Checking graph connectivity.
     *
     * @param node     current vertex being processed
     * @param visited  boolean array tracking visited nodes
     */
    public void dfs(int node, boolean[] visited) {
        // Mark current node as visited and print it
        visited[node] = true;
        System.out.print(node + " ");

        // Recursively visit all unvisited neighbors (go deep)
        for (int neighbor : adjList.get(node)) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited);
            }
        }
    }

    /**
     * Public wrapper for DFS that initializes the visited array.
     *
     * @param start  starting vertex for DFS
     */
    public void dfsTraversal(int start) {
        boolean[] visited = new boolean[vertices];
        System.out.print("DFS Traversal from node " + start + ": ");
        dfs(start, visited);
        System.out.println();
    }

    /**
     * DFS — Iterative Implementation using explicit Stack.
     * Produces the same result as recursive DFS but avoids stack overflow
     * on very deep graphs.
     *
     * @param start  starting vertex
     */
    public void dfsIterative(int start) {
        boolean[] visited = new boolean[vertices];
        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(start);

        System.out.print("DFS Iterative from node " + start + ": ");

        while (!stack.isEmpty()) {
            int node = stack.pop();

            if (!visited[node]) {
                visited[node] = true;
                System.out.print(node + " ");

                // Push neighbors (reverse order to match recursive DFS)
                List<Integer> neighbors = adjList.get(node);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    if (!visited[neighbors.get(i)]) {
                        stack.push(neighbors.get(i));
                    }
                }
            }
        }
        System.out.println();
    }

    /**
     * Detect if the graph has a CYCLE using DFS.
     * Works for undirected graphs.
     *
     * @return true if cycle exists, false otherwise
     */
    public boolean hasCycle() {
        boolean[] visited = new boolean[vertices];

        for (int i = 0; i < vertices; i++) {
            if (!visited[i]) {
                if (dfsCycleCheck(i, visited, -1)) return true;
            }
        }
        return false;
    }

    private boolean dfsCycleCheck(int node, boolean[] visited, int parent) {
        visited[node] = true;

        for (int neighbor : adjList.get(node)) {
            if (!visited[neighbor]) {
                if (dfsCycleCheck(neighbor, visited, node)) return true;
            } else if (neighbor != parent) {
                // Visited neighbor that is not our parent → cycle!
                return true;
            }
        }
        return false;
    }

    /**
     * Check if graph is CONNECTED using DFS.
     * A graph is connected if all vertices are reachable from any vertex.
     *
     * @return true if graph is connected
     */
    public boolean isConnected() {
        boolean[] visited = new boolean[vertices];
        dfs(0, visited);

        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }


    // ============================================================
    //  MAIN — Demo & Testing
    // ============================================================
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("     GRAPH TRAVERSAL ALGORITHMS DEMONSTRATION");
        System.out.println("====================================================\n");

        /*
         * Graph Structure:
         *
         *     0 --- 1 --- 3
         *     |     |
         *     2 --- 4
         *
         * Edges: (0,1), (0,2), (1,3), (1,4), (2,4)
         */
        GraphTraversal graph = new GraphTraversal(5);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(2, 4);

        graph.printGraph();
        System.out.println();

        // --- BFS ---
        graph.bfs(0);
        System.out.println("  ↳ BFS explores level by level (shortest path order)");

        // --- BFS Shortest Path ---
        int[] distances = graph.bfsShortestPath(0);
        System.out.println("\nShortest distances from node 0:");
        for (int i = 0; i < distances.length; i++) {
            System.out.println("  Node 0 → Node " + i + " : " + distances[i] + " edge(s)");
        }

        // --- DFS Recursive ---
        System.out.println();
        graph.dfsTraversal(0);
        System.out.println("  ↳ DFS goes deep along one path before backtracking");

        // --- DFS Iterative ---
        graph.dfsIterative(0);

        // --- Cycle Detection ---
        System.out.println("\nCycle detected: " + graph.hasCycle());

        // --- Connectivity Check ---
        System.out.println("Graph is connected: " + graph.isConnected());

        System.out.println("\n====================================================");
        System.out.println("Complexity: BFS = O(V+E) | DFS = O(V+E)");
        System.out.println("Space: BFS = O(V) queue | DFS = O(V) stack/recursion");
        System.out.println("====================================================");
    }
}
