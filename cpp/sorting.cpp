/*
 * ============================================================
 *  SORTING ALGORITHMS — C++ Complete Implementation
 * ============================================================
 *
 *  Algorithms:
 *    1. Quick Sort
 *    2. Merge Sort
 *    3. Heap Sort
 *    4. Insertion Sort
 *
 *  Complexity Summary:
 *  ┌──────────────────┬──────────────┬──────────────┬──────────────┬────────────┬────────┐
 *  │  Algorithm       │  Best Case   │  Avg Case    │  Worst Case  │  Space     │ Stable │
 *  ├──────────────────┼──────────────┼──────────────┼──────────────┼────────────┼────────┤
 *  │  Quick Sort      │  O(n log n)  │  O(n log n)  │  O(n²)       │ O(log n)   │  No    │
 *  │  Merge Sort      │  O(n log n)  │  O(n log n)  │  O(n log n)  │ O(n)       │  Yes   │
 *  │  Heap Sort       │  O(n log n)  │  O(n log n)  │  O(n log n)  │ O(1)       │  No    │
 *  │  Insertion Sort  │  O(n)        │  O(n²)       │  O(n²)       │ O(1)       │  Yes   │
 *  └──────────────────┴──────────────┴──────────────┴──────────────┴────────────┴────────┘
 *
 *  Compile: g++ -std=c++17 -o sort sorting.cpp
 *  Run:     ./sort
 * ============================================================
 */

#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

// ─────────────────────────────────────────────────────────────
//  UTILITY: Print array
// ─────────────────────────────────────────────────────────────
void printArray(const vector<int>& arr, const string& label) {
    cout << label << ": [";
    for (int i = 0; i < (int)arr.size(); i++) {
        cout << arr[i];
        if (i < (int)arr.size() - 1) cout << ", ";
    }
    cout << "]\n";
}


// ============================================================
//  1. QUICK SORT
// ============================================================
/*
 * HOW IT WORKS:
 *   - Choose a pivot (last element — Lomuto partition scheme).
 *   - Partition: all elements < pivot go left, > pivot go right.
 *   - Recursively sort both halves.
 *
 * COMPLEXITY:
 *   Time:  Best/Avg O(n log n) | Worst O(n²) [sorted input, naive pivot]
 *   Space: O(log n) recursion stack
 *   Stable: NO
 *
 * NOTES:
 *   - Fastest in practice for large random datasets.
 *   - std::sort in C++ STL uses IntroSort (QuickSort + HeapSort + InsertionSort hybrid).
 */

// Partition helper using Lomuto scheme
int partition(vector<int>& arr, int low, int high) {
    int pivot = arr[high];   // pivot = last element
    int i = low - 1;         // index of smaller element

    for (int j = low; j < high; j++) {
        if (arr[j] <= pivot) {
            i++;
            swap(arr[i], arr[j]);
        }
    }
    swap(arr[i + 1], arr[high]);  // place pivot in correct position
    return i + 1;
}

void quickSort(vector<int>& arr, int low, int high) {
    if (low < high) {
        int pi = partition(arr, low, high);
        quickSort(arr, low, pi - 1);   // sort left partition
        quickSort(arr, pi + 1, high);  // sort right partition
    }
}


// ============================================================
//  2. MERGE SORT
// ============================================================
/*
 * HOW IT WORKS:
 *   - Divide array into two halves.
 *   - Recursively sort each half.
 *   - Merge the two sorted halves.
 *
 * COMPLEXITY:
 *   Time:  O(n log n) always — all cases
 *   Space: O(n) auxiliary arrays
 *   Stable: YES
 *
 * NOTES:
 *   - Guaranteed O(n log n) regardless of input.
 *   - Preferred when stability is required.
 *   - Excellent for linked lists and external sorting (disk-based).
 */

// Merge helper: merges arr[left..mid] and arr[mid+1..right]
void merge(vector<int>& arr, int left, int mid, int right) {
    // Create temp sub-arrays
    vector<int> L(arr.begin() + left, arr.begin() + mid + 1);
    vector<int> R(arr.begin() + mid + 1, arr.begin() + right + 1);

    int i = 0, j = 0, k = left;

    while (i < (int)L.size() && j < (int)R.size()) {
        if (L[i] <= R[j]) arr[k++] = L[i++];  // stable: <=
        else               arr[k++] = R[j++];
    }
    while (i < (int)L.size()) arr[k++] = L[i++];
    while (j < (int)R.size()) arr[k++] = R[j++];
}

void mergeSort(vector<int>& arr, int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;  // avoid overflow vs (l+r)/2
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }
}


// ============================================================
//  3. HEAP SORT
// ============================================================
/*
 * HOW IT WORKS:
 *   Phase 1 — Build a Max-Heap from the array (O(n)).
 *   Phase 2 — Repeatedly extract max (root), place at end,
 *              then heapify the remaining heap (O(n log n)).
 *
 * HEAPIFY: Ensures subtree rooted at i satisfies max-heap property.
 *   - Compare node with its left (2i+1) and right (2i+2) children.
 *   - Swap with the largest child if needed, recurse down.
 *
 * COMPLEXITY:
 *   Time:  O(n log n) always
 *   Space: O(1) — in-place!
 *   Stable: NO
 *
 * NOTES:
 *   - Guaranteed O(n log n) and O(1) space — best when memory is tight.
 *   - Not cache-friendly (non-sequential memory access).
 */

void heapify(vector<int>& arr, int n, int i) {
    int largest = i;           // assume root is largest
    int left    = 2 * i + 1;  // left child index
    int right   = 2 * i + 2;  // right child index

    if (left  < n && arr[left]  > arr[largest]) largest = left;
    if (right < n && arr[right] > arr[largest]) largest = right;

    if (largest != i) {
        swap(arr[i], arr[largest]);
        heapify(arr, n, largest);  // recursively heapify affected subtree
    }
}

void heapSort(vector<int>& arr) {
    int n = arr.size();

    // Phase 1: Build max-heap (start from last non-leaf)
    for (int i = n / 2 - 1; i >= 0; i--)
        heapify(arr, n, i);

    // Phase 2: Extract elements from heap one by one
    for (int i = n - 1; i > 0; i--) {
        swap(arr[0], arr[i]);    // move current max to end
        heapify(arr, i, 0);     // heapify reduced heap
    }
}


// ============================================================
//  4. INSERTION SORT
// ============================================================
/*
 * HOW IT WORKS:
 *   - Maintains a sorted sub-array on the left.
 *   - Picks next element (key) and inserts it into the correct
 *     position in the sorted part by shifting larger elements right.
 *
 * ANALOGY: Like sorting playing cards in your hand.
 *
 * COMPLEXITY:
 *   Time:  Best O(n) [sorted] | Avg/Worst O(n²)
 *   Space: O(1) — in-place
 *   Stable: YES
 *
 * NOTES:
 *   - Best for small or nearly-sorted arrays.
 *   - Adaptive: fewer operations when input is partially sorted.
 *   - Online: can sort as elements arrive one at a time.
 *   - Used as the base case in hybrid sorts (TimSort, IntroSort).
 */

void insertionSort(vector<int>& arr) {
    int n = arr.size();
    for (int i = 1; i < n; i++) {
        int key = arr[i];   // element to insert in correct position
        int j   = i - 1;

        // Shift elements greater than key one position to the right
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;   // insert key at correct position
    }
}


// ============================================================
//  MAIN — Demo & Testing
// ============================================================
int main() {
    cout << "====================================================\n";
    cout << "       SORTING ALGORITHMS DEMONSTRATION (C++)\n";
    cout << "====================================================\n\n";

    vector<int> original = {64, 34, 25, 12, 22, 11, 90};
    printArray(original, "Original Array     ");

    // --- Quick Sort ---
    vector<int> arr1 = original;
    quickSort(arr1, 0, arr1.size() - 1);
    printArray(arr1, "Quick Sort         ");
    cout << "  Complexity: Time O(n log n) avg | Space O(log n)\n\n";

    // --- Merge Sort ---
    vector<int> arr2 = original;
    mergeSort(arr2, 0, arr2.size() - 1);
    printArray(arr2, "Merge Sort         ");
    cout << "  Complexity: Time O(n log n) always | Space O(n)\n\n";

    // --- Heap Sort ---
    vector<int> arr3 = original;
    heapSort(arr3);
    printArray(arr3, "Heap Sort          ");
    cout << "  Complexity: Time O(n log n) always | Space O(1)\n\n";

    // --- Insertion Sort ---
    vector<int> arr4 = original;
    insertionSort(arr4);
    printArray(arr4, "Insertion Sort     ");
    cout << "  Complexity: Time O(n^2) avg | Best O(n) | Space O(1)\n\n";

    cout << "====================================================\n";
    return 0;
}
