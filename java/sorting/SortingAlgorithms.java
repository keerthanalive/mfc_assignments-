package com.jeenify.algorithms.sorting;

import java.util.Arrays;

/**
 * ============================================================
 *  SORTING ALGORITHMS — Complete Implementation
 * ============================================================
 *
 *  Algorithms Covered:
 *    1. Quick Sort
 *    2. Merge Sort
 *    3. Heap Sort
 *    4. Insertion Sort
 *
 *  Complexity Summary:
 *  ┌──────────────────┬──────────────┬──────────────┬──────────────┬────────┐
 *  │  Algorithm       │  Best Case   │  Avg Case    │  Worst Case  │ Space  │
 *  ├──────────────────┼──────────────┼──────────────┼──────────────┼────────┤
 *  │  Quick Sort      │  O(n log n)  │  O(n log n)  │  O(n²)       │ O(log n│
 *  │  Merge Sort      │  O(n log n)  │  O(n log n)  │  O(n log n)  │ O(n)   │
 *  │  Heap Sort       │  O(n log n)  │  O(n log n)  │  O(n log n)  │ O(1)   │
 *  │  Insertion Sort  │  O(n)        │  O(n²)       │  O(n²)       │ O(1)   │
 *  └──────────────────┴──────────────┴──────────────┴──────────────┴────────┘
 *
 * ============================================================
 */
public class SortingAlgorithms {

    // ============================================================
    //  1. QUICK SORT
    // ============================================================
    /**
     * Quick Sort — Divide and Conquer
     *
     * HOW IT WORKS:
     *   - Choose a 'pivot' element from the array.
     *   - Partition: rearrange so all elements < pivot go left, > pivot go right.
     *   - Recursively apply to left and right sub-arrays.
     *
     * ALGORITHM STEPS:
     *   1. Pick pivot (here: last element).
     *   2. Partition array around pivot.
     *   3. Recursively sort left partition.
     *   4. Recursively sort right partition.
     *
     * COMPLEXITY:
     *   - Time:  Best/Avg O(n log n) | Worst O(n²) [sorted/reverse sorted array]
     *   - Space: O(log n) [recursion stack]
     *   - Stable: NO
     *
     * NOTES:
     *   - Fastest in practice for large random datasets.
     *   - Poor on already-sorted data unless randomized pivot is used.
     *   - In-place sort (no extra array needed).
     */
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // pi is partitioning index — arr[pi] is now in correct place
            int pi = partition(arr, low, high);

            // Recursively sort elements before and after partition
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    /**
     * Partition helper for Quick Sort using Lomuto partition scheme.
     * Pivot = last element. Moves all smaller elements to the left.
     */
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];  // Pivot element
        int i = low - 1;        // Index of smaller element

        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (arr[j] <= pivot) {
                i++;
                // Swap arr[i] and arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Place pivot in correct position
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }


    // ============================================================
    //  2. MERGE SORT
    // ============================================================
    /**
     * Merge Sort — Divide and Conquer
     *
     * HOW IT WORKS:
     *   - Divide array into two halves.
     *   - Recursively sort each half.
     *   - Merge the two sorted halves back together.
     *
     * ALGORITHM STEPS:
     *   1. Find midpoint of array.
     *   2. Recursively mergeSort left half.
     *   3. Recursively mergeSort right half.
     *   4. Merge both sorted halves.
     *
     * COMPLEXITY:
     *   - Time:  O(n log n) always (all cases)
     *   - Space: O(n) [auxiliary arrays for merge]
     *   - Stable: YES
     *
     * NOTES:
     *   - Preferred when stability is required.
     *   - Works well on linked lists (no random access needed).
     *   - Used in Java's Arrays.sort() for Object arrays (TimSort variant).
     */
    public static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            // Sort first and second halves
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            // Merge the sorted halves
            merge(arr, left, mid, right);
        }
    }

    /**
     * Merge helper: merges two sorted sub-arrays arr[left..mid] and arr[mid+1..right].
     */
    private static void merge(int[] arr, int left, int mid, int right) {
        // Sizes of two sub-arrays to be merged
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Temp arrays
        int[] L = new int[n1];
        int[] R = new int[n2];

        // Copy data into temp arrays
        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[mid + 1 + j];

        // Merge temp arrays back into arr[left..right]
        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements of L[] (if any)
        while (i < n1) { arr[k++] = L[i++]; }

        // Copy remaining elements of R[] (if any)
        while (j < n2) { arr[k++] = R[j++]; }
    }


    // ============================================================
    //  3. HEAP SORT
    // ============================================================
    /**
     * Heap Sort — Comparison-based using a Binary Heap
     *
     * HOW IT WORKS:
     *   - Build a Max-Heap from the input array.
     *   - Repeatedly extract the maximum element (root) and place it at the end.
     *   - Reduce heap size by 1 and heapify the root again.
     *
     * ALGORITHM STEPS:
     *   1. Build max-heap from all n elements.
     *   2. Swap root (max) with last element.
     *   3. Heapify on reduced heap (size n-1).
     *   4. Repeat steps 2–3 until heap size = 1.
     *
     * COMPLEXITY:
     *   - Time:  O(n log n) always (all cases)
     *   - Space: O(1) [in-place]
     *   - Stable: NO
     *
     * NOTES:
     *   - No extra memory needed (in-place).
     *   - Not cache-friendly due to non-sequential memory access.
     *   - Used in algorithms like finding k-th largest element.
     */
    public static void heapSort(int[] arr) {
        int n = arr.length;

        // Step 1: Build max-heap
        // Start from last non-leaf node and heapify down
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Step 2: Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            // Move current root (max) to end
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            // Heapify the reduced heap (exclude sorted elements)
            heapify(arr, i, 0);
        }
    }

    /**
     * Heapify helper: maintains max-heap property for subtree rooted at index i.
     *
     * @param arr  the array representing the heap
     * @param n    the size of the heap
     * @param i    root index of subtree to heapify
     */
    private static void heapify(int[] arr, int n, int i) {
        int largest = i;        // Initialize largest as root
        int left   = 2 * i + 1; // Left child index
        int right  = 2 * i + 2; // Right child index

        // If left child is larger than root
        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }

        // If right child is larger than current largest
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }

        // If largest is not root, swap and continue heapifying
        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;

            // Recursively heapify the affected subtree
            heapify(arr, n, largest);
        }
    }


    // ============================================================
    //  4. INSERTION SORT
    // ============================================================
    /**
     * Insertion Sort — Simple Comparison-Based Sort
     *
     * HOW IT WORKS:
     *   - Maintain a sorted sub-array on the left.
     *   - Pick the next element and insert it into the correct position
     *     in the sorted part by shifting larger elements right.
     *
     * ALGORITHM STEPS:
     *   1. Start from index 1 (first element is trivially sorted).
     *   2. Pick arr[i] as 'key'.
     *   3. Compare key with each element in sorted region (going left).
     *   4. Shift elements > key one position to the right.
     *   5. Insert key at the correct position.
     *   6. Repeat for all elements.
     *
     * COMPLEXITY:
     *   - Time:  Best O(n) [already sorted] | Avg/Worst O(n²)
     *   - Space: O(1) [in-place]
     *   - Stable: YES
     *
     * NOTES:
     *   - Efficient for small or nearly-sorted arrays.
     *   - Adaptive: performs well when input is partially sorted.
     *   - Used as the base case in hybrid sorts (e.g., TimSort uses it for small chunks).
     *   - Simple to implement and good for online sorting (data received one at a time).
     */
    public static void insertionSort(int[] arr) {
        int n = arr.length;

        for (int i = 1; i < n; i++) {
            int key = arr[i];  // Element to be inserted in correct position
            int j = i - 1;

            // Shift elements of arr[0..i-1] that are greater than key
            // one position ahead of their current position
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }

            // Insert key at correct position
            arr[j + 1] = key;
        }
    }


    // ============================================================
    //  MAIN — Demo & Testing
    // ============================================================
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("       SORTING ALGORITHMS DEMONSTRATION");
        System.out.println("====================================================\n");

        int[] original = {64, 34, 25, 12, 22, 11, 90};

        // --- Quick Sort ---
        int[] arr1 = Arrays.copyOf(original, original.length);
        System.out.println("Original Array:      " + Arrays.toString(arr1));
        quickSort(arr1, 0, arr1.length - 1);
        System.out.println("Quick Sort Result:   " + Arrays.toString(arr1));
        System.out.println("  Complexity: Time O(n log n) avg | Space O(log n)\n");

        // --- Merge Sort ---
        int[] arr2 = Arrays.copyOf(original, original.length);
        mergeSort(arr2, 0, arr2.length - 1);
        System.out.println("Merge Sort Result:   " + Arrays.toString(arr2));
        System.out.println("  Complexity: Time O(n log n) always | Space O(n)\n");

        // --- Heap Sort ---
        int[] arr3 = Arrays.copyOf(original, original.length);
        heapSort(arr3);
        System.out.println("Heap Sort Result:    " + Arrays.toString(arr3));
        System.out.println("  Complexity: Time O(n log n) always | Space O(1)\n");

        // --- Insertion Sort ---
        int[] arr4 = Arrays.copyOf(original, original.length);
        insertionSort(arr4);
        System.out.println("Insertion Sort Result: " + Arrays.toString(arr4));
        System.out.println("  Complexity: Time O(n²) avg | Best O(n) | Space O(1)\n");

        System.out.println("====================================================");
    }
}
