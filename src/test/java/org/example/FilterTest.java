package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the Hierarchy filter functionality.
 */
public class FilterTest {
    @Test
    public void testFilter() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
            new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
            new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );

        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 3 != 0);

        Hierarchy filteredExpected = new ArrayBasedHierarchy(
            new int[]{1, 2, 5, 8, 10, 11},
            new int[]{0, 1, 1, 0, 1, 2}
        );

        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testKeepAll() {
        // Predicate that accepts everything — output must equal input.
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId > 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testRemoveAll() {
        // Predicate that rejects everything — output must be empty.
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId > 50);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{},
                new int[]{}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());

    }

    @Test
    void testEmptyHierarchy() {
        // Empty input → empty output regardless of predicate.
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{},
                new int[]{}
        );
        assertEquals(0, HierarchyFilter.filter(unfiltered, nodeId -> true).getSize());;
    }

    @Test
    void testRootRemovedCascadesToAllDescendants() {
        // Removing the only root must remove every node in its subtree.
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 2, 3}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId != 1);
        assertEquals(0, HierarchyFilter.filter(unfiltered, id -> id != 1).getSize());;
    }

    @Test
    void testChildRemovedButNotSibling() {
        // Tree: 1 -> 2 -> 3
        //         -> 4
        // Remove 2: node 3 (child of 2) is also dropped, but node 4 (sibling of 2) stays.
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 2, 1}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId != 2);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 4},
                new int[]{0, 1});
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());

    }

    @Test
    void testMultipleRoots() {
        // Forest: root 1 (no children), root 2 (child 3), root 4 (child 5).
        // Remove node 3: root 2 survives but loses its child.
        Hierarchy unfiltered   = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5},
                new int[]{0, 0, 1, 0, 1});
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered,(id -> id != 3));
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 4, 5},
                new int[]{0, 0, 0, 1});
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testDepthAdjustedAfterMiddleLayerRemoved() {
        // Chain: 1(d0) -> 2(d1) -> 3(d2).  Remove 2 → 3 also gone, only 1 remains.
        Hierarchy unfiltered   = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 2});
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered,(id -> id != 2));
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1},
                new int[]{0});
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void testSingleNode() {
        Hierarchy unfiltered =new ArrayBasedHierarchy
                (new int[]{42}, new int[]{0});
        assertEquals(unfiltered.formatString(), HierarchyFilter.filter(unfiltered,id -> true).formatString());
        assertEquals(0, HierarchyFilter.filter(unfiltered,(id -> false)).getSize());
    }
}

