package com.jeenify.algorithms.trees;

import java.util.LinkedList;
import java.util.Queue;

/**
 * ============================================================
 *  TREE DATA STRUCTURES & ALGORITHMS
 * ============================================================
 *
 *  Topics Covered:
 *    1. Binary Tree  — General tree where each node has at most 2 children
 *    2. Binary Search Tree (BST) — Ordered Binary Tree
 *    3. In-order Traversal   (Left → Root → Right)
 *    4. Pre-order Traversal  (Root → Left → Right)
 *    5. Post-order Traversal (Left → Right → Root)
 *
 *  Complexity Summary (BST):
 *  ┌────────────────────┬──────────────┬──────────────┐
 *  │  Operation         │  Average     │  Worst Case  │
 *  ├────────────────────┼──────────────┼──────────────┤
 *  │  Search            │  O(log n)    │  O(n)        │
 *  │  Insert            │  O(log n)    │  O(n)        │
 *  │  Delete            │  O(log n)    │  O(n)        │
 *  │  Traversal         │  O(n)        │  O(n)        │
 *  └────────────────────┴──────────────┴──────────────┘
 *  (Worst case O(n) occurs for a skewed/degenerate tree)
 *
 * ============================================================
 */
public class TreeAlgorithms {

    // ============================================================
    //  NODE DEFINITION
    // ============================================================
    /**
     * A single node in a Binary Tree / BST.
     * Each node stores an integer value and references to left and right children.
     */
    static class Node {
        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data  = data;
            this.left  = null;
            this.right = null;
        }
    }


    // ============================================================
    //  BINARY TREE
    // ============================================================
    /**
     * Binary Tree — General tree where each node has at most 2 children.
     *
     * PROPERTIES:
     *   - No ordering constraint between parent and children.
     *   - Height of a balanced tree with n nodes = O(log n).
     *   - Maximum nodes at level L = 2^L.
     *   - Maximum nodes in tree of height h = 2^(h+1) − 1.
     *
     * NOTES:
     *   - All BSTs are Binary Trees, but not vice versa.
     *   - Used in: expression trees, Huffman coding, decision trees.
     */
    static class BinaryTree {
        Node root;

        BinaryTree() {
            this.root = null;
        }

        /**
         * Insert a node into the Binary Tree using Level-Order (BFS) insertion.
         * Fills the tree level by level (left to right) — keeps tree complete.
         */
        public void insert(int data) {
            Node newNode = new Node(data);

            if (root == null) {
                root = newNode;
                return;
            }

            // BFS to find the first node that has an empty child slot
            Queue<Node> queue = new LinkedList<>();
            queue.add(root);

            while (!queue.isEmpty()) {
                Node temp = queue.poll();

                if (temp.left == null) {
                    temp.left = newNode;
                    return;
                } else {
                    queue.add(temp.left);
                }

                if (temp.right == null) {
                    temp.right = newNode;
                    return;
                } else {
                    queue.add(temp.right);
                }
            }
        }

        /**
         * Calculate the height of the binary tree.
         * Height = number of edges on the longest path from root to leaf.
         *
         * @param node  root of the subtree
         * @return      height of the subtree
         */
        public int height(Node node) {
            if (node == null) return -1;  // -1 for edge-count definition
            int leftHeight  = height(node.left);
            int rightHeight = height(node.right);
            return 1 + Math.max(leftHeight, rightHeight);
        }

        /**
         * Count total number of nodes in the tree.
         */
        public int countNodes(Node node) {
            if (node == null) return 0;
            return 1 + countNodes(node.left) + countNodes(node.right);
        }
    }


    // ============================================================
    //  BINARY SEARCH TREE (BST)
    // ============================================================
    /**
     * Binary Search Tree (BST)
     *
     * BST PROPERTY:
     *   For every node N:
     *     - All values in N's LEFT subtree  < N.data
     *     - All values in N's RIGHT subtree > N.data
     *
     * WHY BST?
     *   - Enables efficient binary search in O(log n) on average.
     *   - Ordered structure: in-order traversal gives sorted sequence.
     *
     * APPLICATIONS:
     *   - Implementing sets and maps (TreeSet, TreeMap in Java).
     *   - Database indexing.
     *   - Auto-complete / spell-check (Trie variant).
     *
     * LIMITATIONS:
     *   - Can degenerate to O(n) linked-list if data is inserted in sorted order.
     *   - Solution: Use self-balancing trees (AVL Tree, Red-Black Tree).
     */
    static class BinarySearchTree {
        Node root;

        BinarySearchTree() {
            this.root = null;
        }

        // ----------------------------------------------------------
        //  INSERT
        // ----------------------------------------------------------
        /**
         * Insert a value into the BST maintaining BST property.
         *
         * ALGORITHM:
         *   1. If tree is empty, new node becomes root.
         *   2. Compare value with current node.
         *   3. If value < node, go LEFT; if value > node, go RIGHT.
         *   4. Repeat until an empty spot is found.
         *
         * Complexity: Time O(log n) avg | O(n) worst | Space O(log n) recursion stack
         */
        public Node insert(Node root, int data) {
            // Base case: empty subtree — create and return new node
            if (root == null) {
                return new Node(data);
            }

            if (data < root.data) {
                // Insert in left subtree
                root.left = insert(root.left, data);
            } else if (data > root.data) {
                // Insert in right subtree
                root.right = insert(root.right, data);
            }
            // Duplicate values are ignored

            return root;
        }

        // ----------------------------------------------------------
        //  SEARCH
        // ----------------------------------------------------------
        /**
         * Search for a value in BST.
         *
         * ALGORITHM:
         *   1. Compare key with root.
         *   2. If equal, found!
         *   3. If key < root, search left subtree.
         *   4. If key > root, search right subtree.
         *   5. If null reached, not found.
         *
         * Complexity: Time O(log n) avg | O(n) worst | Space O(log n) recursion
         */
        public boolean search(Node root, int key) {
            if (root == null) return false;       // Not found
            if (root.data == key) return true;    // Found!

            if (key < root.data) {
                return search(root.left, key);    // Search left
            } else {
                return search(root.right, key);   // Search right
            }
        }

        // ----------------------------------------------------------
        //  DELETE
        // ----------------------------------------------------------
        /**
         * Delete a node from BST.
         *
         * THREE CASES:
         *   Case 1 — Node has NO children (leaf): simply remove it.
         *   Case 2 — Node has ONE child: replace node with its child.
         *   Case 3 — Node has TWO children:
         *            Find in-order successor (smallest in right subtree),
         *            copy its value to current node, delete successor.
         *
         * Complexity: Time O(log n) avg | O(n) worst | Space O(log n)
         */
        public Node delete(Node root, int key) {
            if (root == null) return null;

            if (key < root.data) {
                root.left = delete(root.left, key);
            } else if (key > root.data) {
                root.right = delete(root.right, key);
            } else {
                // Node to delete found

                // Case 1 & 2: No child or one child
                if (root.left == null)  return root.right;
                if (root.right == null) return root.left;

                // Case 3: Two children
                // Find in-order successor (minimum in right subtree)
                Node successor = findMin(root.right);
                root.data = successor.data;                     // Copy successor's value
                root.right = delete(root.right, successor.data); // Delete successor
            }

            return root;
        }

        /** Find the minimum value node in a subtree (leftmost node). */
        private Node findMin(Node node) {
            while (node.left != null) node = node.left;
            return node;
        }
    }


    // ============================================================
    //  TREE TRAVERSALS
    // ============================================================
    /**
     * TREE TRAVERSAL — Visiting every node exactly once.
     *
     * THREE DFS (Depth-First Search) Traversal Orders:
     *
     *  ┌────────────────────┬──────────────────────────────────────────────────┐
     *  │  Traversal         │  Order              │  Use Case                  │
     *  ├────────────────────┼─────────────────────┼────────────────────────────┤
     *  │  In-order          │  L → Root → R       │  BST sorted output         │
     *  │  Pre-order         │  Root → L → R       │  Copy/serialize a tree     │
     *  │  Post-order        │  L → R → Root       │  Delete tree, eval exprs   │
     *  └────────────────────┴─────────────────────┴────────────────────────────┘
     *
     *  All traversals: Time O(n) | Space O(h) where h = height of tree
     */

    /**
     * IN-ORDER TRAVERSAL: Left → Root → Right
     *
     * KEY PROPERTY:
     *   For a BST, In-order traversal visits nodes in ASCENDING SORTED ORDER.
     *
     * USE CASES:
     *   - Print BST elements in sorted order.
     *   - Validate if a binary tree is a BST.
     *
     * Example tree:
     *       4
     *      / \
     *     2   6
     *    / \ / \
     *   1  3 5  7
     *
     * In-order: 1, 2, 3, 4, 5, 6, 7  ← sorted!
     */
    public static void inOrder(Node root) {
        if (root == null) return;

        inOrder(root.left);            // Visit left subtree
        System.out.print(root.data + " ");  // Visit root
        inOrder(root.right);           // Visit right subtree
    }

    /**
     * PRE-ORDER TRAVERSAL: Root → Left → Right
     *
     * KEY PROPERTY:
     *   Root is visited FIRST before its subtrees.
     *
     * USE CASES:
     *   - Create a copy/clone of a tree.
     *   - Serialize a tree to file (can reconstruct from pre-order + in-order).
     *   - Print directory/file structure (parent before children).
     *
     * Example tree above → Pre-order: 4, 2, 1, 3, 6, 5, 7
     */
    public static void preOrder(Node root) {
        if (root == null) return;

        System.out.print(root.data + " ");  // Visit root FIRST
        preOrder(root.left);           // Visit left subtree
        preOrder(root.right);          // Visit right subtree
    }

    /**
     * POST-ORDER TRAVERSAL: Left → Right → Root
     *
     * KEY PROPERTY:
     *   Root is visited LAST after both subtrees.
     *
     * USE CASES:
     *   - Delete a tree (children before parent — safe deletion).
     *   - Evaluate expression trees (operators after operands).
     *   - Calculate directory sizes (process files before folder).
     *
     * Example tree above → Post-order: 1, 3, 2, 5, 7, 6, 4
     */
    public static void postOrder(Node root) {
        if (root == null) return;

        postOrder(root.left);          // Visit left subtree
        postOrder(root.right);         // Visit right subtree
        System.out.print(root.data + " ");  // Visit root LAST
    }


    // ============================================================
    //  MAIN — Demo & Testing
    // ============================================================
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("   BINARY TREE & BST ALGORITHMS DEMONSTRATION");
        System.out.println("====================================================\n");

        // --- Binary Tree Demo ---
        System.out.println("--- Binary Tree (Level-Order Insertion) ---");
        BinaryTree bt = new BinaryTree();
        for (int val : new int[]{1, 2, 3, 4, 5, 6, 7}) bt.insert(val);
        System.out.println("Tree structure (Level-order inserted: 1,2,3,4,5,6,7)");
        System.out.println("Height: "   + bt.height(bt.root));
        System.out.println("Nodes:  "   + bt.countNodes(bt.root));
        System.out.println("\nIn-order   (L→Root→R): "); inOrder(bt.root);
        System.out.println("\nPre-order  (Root→L→R): "); preOrder(bt.root);
        System.out.println("\nPost-order (L→R→Root): "); postOrder(bt.root);

        // --- BST Demo ---
        System.out.println("\n\n--- Binary Search Tree (BST) ---");
        BinarySearchTree bst = new BinarySearchTree();
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        System.out.print("Inserting values: ");
        for (int v : values) {
            System.out.print(v + " ");
            bst.root = bst.insert(bst.root, v);
        }

        System.out.println("\nBST Tree:");
        System.out.println("         50");
        System.out.println("        /  \\");
        System.out.println("       30   70");
        System.out.println("      / \\ / \\");
        System.out.println("     20 40 60 80");

        System.out.println("\nIn-order (sorted):    "); inOrder(bst.root);
        System.out.println("\nPre-order:            "); preOrder(bst.root);
        System.out.println("\nPost-order:           "); postOrder(bst.root);

        System.out.println("\n\nSearch 40: " + bst.search(bst.root, 40));
        System.out.println("Search 99: " + bst.search(bst.root, 99));

        System.out.println("\nDelete node 30 (has 2 children — replaced by in-order successor 40):");
        bst.root = bst.delete(bst.root, 30);
        System.out.print("In-order after delete: "); inOrder(bst.root);

        System.out.println("\n\n====================================================");
        System.out.println("Complexity: Insert/Search/Delete = O(log n) avg, O(n) worst");
        System.out.println("Traversals = O(n) time, O(h) space (h = tree height)");
        System.out.println("====================================================");
    }
}
