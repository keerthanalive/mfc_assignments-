/*
 * ============================================================
 *  TREE DATA STRUCTURES & ALGORITHMS — C++ Complete Implementation
 * ============================================================
 *
 *  Topics:
 *    1. Binary Tree  (level-order insertion)
 *    2. Binary Search Tree (BST) — insert, search, delete
 *    3. In-order   Traversal  (L → Root → R)  → gives sorted output for BST
 *    4. Pre-order  Traversal  (Root → L → R)  → copy / serialize a tree
 *    5. Post-order Traversal  (L → R → Root)  → delete tree / evaluate expr
 *
 *  Complexity (BST):
 *  ┌────────────────────┬──────────────┬──────────────┐
 *  │  Operation         │  Average     │  Worst Case  │
 *  ├────────────────────┼──────────────┼──────────────┤
 *  │  Search / Insert   │  O(log n)    │  O(n)        │
 *  │  Delete            │  O(log n)    │  O(n)        │
 *  │  Traversal         │  O(n)        │  O(n)        │
 *  └────────────────────┴──────────────┴──────────────┘
 *  (Worst case O(n) on a skewed / degenerate tree)
 *
 *  Compile: g++ -std=c++17 -o trees trees.cpp
 *  Run:     ./trees
 * ============================================================
 */

#include <iostream>
#include <queue>
#include <string>
using namespace std;

// ─────────────────────────────────────────────────────────────
//  NODE DEFINITION
// ─────────────────────────────────────────────────────────────
struct Node {
    int   data;
    Node* left;
    Node* right;

    Node(int val) : data(val), left(nullptr), right(nullptr) {}
};


// ============================================================
//  BINARY TREE
// ============================================================
/*
 * A Binary Tree where each node has at most 2 children.
 * No ordering constraint — use Level-Order (BFS) insertion to
 * keep the tree COMPLETE (filled left-to-right level by level).
 *
 * Properties:
 *   - Max nodes at level L : 2^L
 *   - Max nodes of height h: 2^(h+1) - 1
 *   - All BSTs are binary trees, not vice versa.
 *
 * Applications: expression trees, Huffman coding, decision trees.
 */

class BinaryTree {
public:
    Node* root;
    BinaryTree() : root(nullptr) {}

    // Level-Order (BFS) insert — keeps tree complete
    void insert(int data) {
        Node* newNode = new Node(data);
        if (!root) { root = newNode; return; }

        queue<Node*> q;
        q.push(root);
        while (!q.empty()) {
            Node* curr = q.front(); q.pop();
            if (!curr->left)  { curr->left  = newNode; return; }
            else q.push(curr->left);
            if (!curr->right) { curr->right = newNode; return; }
            else q.push(curr->right);
        }
    }

    // Height of tree (number of edges on longest root-to-leaf path)
    int height(Node* node) {
        if (!node) return -1;
        return 1 + max(height(node->left), height(node->right));
    }

    // Count total nodes
    int countNodes(Node* node) {
        if (!node) return 0;
        return 1 + countNodes(node->left) + countNodes(node->right);
    }
};


// ============================================================
//  BINARY SEARCH TREE (BST)
// ============================================================
/*
 * BST PROPERTY:
 *   For every node N:
 *     - All values in LEFT  subtree < N.data
 *     - All values in RIGHT subtree > N.data
 *
 * WHY BST?
 *   - Average O(log n) for search, insert, delete.
 *   - In-order traversal gives SORTED output.
 *
 * LIMITATION:
 *   - Degenerates to O(n) linked list on sorted input.
 *   - Solution: AVL Tree or Red-Black Tree (self-balancing).
 */

class BST {
public:
    Node* root;
    BST() : root(nullptr) {}

    // ── INSERT ──────────────────────────────────────────────
    // Compare and go left (<) or right (>) until null spot found.
    // Time: O(log n) avg | O(n) worst
    Node* insert(Node* root, int data) {
        if (!root) return new Node(data);
        if      (data < root->data) root->left  = insert(root->left,  data);
        else if (data > root->data) root->right = insert(root->right, data);
        // duplicates ignored
        return root;
    }

    // ── SEARCH ──────────────────────────────────────────────
    // Compare key with node; recurse left or right.
    // Time: O(log n) avg | O(n) worst
    bool search(Node* root, int key) {
        if (!root)              return false;
        if (root->data == key)  return true;
        if (key < root->data)   return search(root->left,  key);
        return                         search(root->right, key);
    }

    // ── DELETE ──────────────────────────────────────────────
    /*
     * Three cases:
     *   Case 1: Leaf node         → simply remove.
     *   Case 2: One child         → replace with child.
     *   Case 3: Two children      → find in-order successor (min of right
     *                               subtree), copy value, delete successor.
     * Time: O(log n) avg | O(n) worst
     */
    Node* findMin(Node* node) {
        while (node->left) node = node->left;
        return node;
    }

    Node* deleteNode(Node* root, int key) {
        if (!root) return nullptr;

        if      (key < root->data) root->left  = deleteNode(root->left,  key);
        else if (key > root->data) root->right = deleteNode(root->right, key);
        else {
            // Node found
            if (!root->left)  { Node* tmp = root->right; delete root; return tmp; }
            if (!root->right) { Node* tmp = root->left;  delete root; return tmp; }

            // Two children: replace with in-order successor
            Node* successor = findMin(root->right);
            root->data  = successor->data;
            root->right = deleteNode(root->right, successor->data);
        }
        return root;
    }
};


// ============================================================
//  TREE TRAVERSALS
// ============================================================
/*
 *  ┌────────────────────┬──────────────────────────────────────┐
 *  │  Traversal         │  Order              │  Key Use Case  │
 *  ├────────────────────┼─────────────────────┼────────────────┤
 *  │  In-order          │  L → Root → R       │  BST sorted    │
 *  │  Pre-order         │  Root → L → R       │  Copy/serialize│
 *  │  Post-order        │  L → R → Root       │  Delete tree   │
 *  └────────────────────┴─────────────────────┴────────────────┘
 *  All: Time O(n) | Space O(h) where h = tree height
 */

// IN-ORDER (L → Root → R)
// KEY: For BST gives elements in ASCENDING SORTED ORDER.
// Use: print sorted BST, validate BST, generate sorted sequence.
void inOrder(Node* root) {
    if (!root) return;
    inOrder(root->left);
    cout << root->data << " ";
    inOrder(root->right);
}

// PRE-ORDER (Root → L → R)
// KEY: Root visited FIRST before subtrees.
// Use: clone/copy a tree, serialize to file, print directory structure.
void preOrder(Node* root) {
    if (!root) return;
    cout << root->data << " ";
    preOrder(root->left);
    preOrder(root->right);
}

// POST-ORDER (L → R → Root)
// KEY: Root visited LAST after both subtrees.
// Use: safely delete tree (children freed before parent),
//      evaluate expression trees (operands before operator),
//      compute directory sizes.
void postOrder(Node* root) {
    if (!root) return;
    postOrder(root->left);
    postOrder(root->right);
    cout << root->data << " ";
}

// Free all memory — post-order deletion
void deleteTree(Node* root) {
    if (!root) return;
    deleteTree(root->left);
    deleteTree(root->right);
    delete root;
}


// ============================================================
//  MAIN — Demo & Testing
// ============================================================
int main() {
    cout << "====================================================\n";
    cout << "   BINARY TREE & BST ALGORITHMS DEMONSTRATION (C++)\n";
    cout << "====================================================\n\n";

    // ── Binary Tree ──
    cout << "--- Binary Tree (Level-Order Insertion: 1,2,3,4,5,6,7) ---\n";
    BinaryTree bt;
    for (int v : {1, 2, 3, 4, 5, 6, 7}) bt.insert(v);
    cout << "Height : " << bt.height(bt.root)     << "\n";
    cout << "Nodes  : " << bt.countNodes(bt.root) << "\n";
    cout << "In-order   (L->Root->R): "; inOrder(bt.root);   cout << "\n";
    cout << "Pre-order  (Root->L->R): "; preOrder(bt.root);  cout << "\n";
    cout << "Post-order (L->R->Root): "; postOrder(bt.root); cout << "\n";

    // ── BST ──
    cout << "\n--- Binary Search Tree (BST) ---\n";
    BST bst;
    int values[] = {50, 30, 70, 20, 40, 60, 80};
    cout << "Inserting: ";
    for (int v : values) {
        cout << v << " ";
        bst.root = bst.insert(bst.root, v);
    }
    cout << "\n";
    cout << "BST structure:\n";
    cout << "        50\n";
    cout << "       /  \\\n";
    cout << "      30   70\n";
    cout << "     / \\ / \\\n";
    cout << "    20 40 60 80\n\n";

    cout << "In-order  (sorted): ";  inOrder(bst.root);   cout << "\n";
    cout << "Pre-order:          ";  preOrder(bst.root);  cout << "\n";
    cout << "Post-order:         ";  postOrder(bst.root); cout << "\n\n";

    cout << "Search 40: " << (bst.search(bst.root, 40) ? "Found" : "Not found") << "\n";
    cout << "Search 99: " << (bst.search(bst.root, 99) ? "Found" : "Not found") << "\n";

    cout << "\nDelete 30 (2 children -> replaced by in-order successor 40):\n";
    bst.root = bst.deleteNode(bst.root, 30);
    cout << "In-order after delete: "; inOrder(bst.root); cout << "\n";

    // Cleanup
    deleteTree(bt.root);
    deleteTree(bst.root);

    cout << "\n====================================================\n";
    cout << "Complexity: Insert/Search/Delete = O(log n) avg, O(n) worst\n";
    cout << "Traversals = O(n) time, O(h) space\n";
    cout << "====================================================\n";
    return 0;
}
