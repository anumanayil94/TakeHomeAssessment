package org.example;

import java.util.*;
import java.util.function.IntPredicate;


// The task:
// 1. Read and understand the Hierarchy data structure described in this file.
// 2. Implement filter() function.
// 3. Implement more test cases.
//
// The task should take 30-90 minutes.
//
// When assessing the submission, we will pay attention to:
// - correctness, efficiency, and clarity of the code;
// - the test cases.

/**
 * A `Hierarchy` stores an arbitrary _forest_ (an ordered collection of ordered trees)
 * as an array of node IDs in the order of DFS traversal, combined with a parallel array of node depths.
 *
 * Parent-child relationships are identified by the position in the array and the associated depth.
 * Each tree root has depth 0, its children have depth 1 and follow it in the array, their children have depth 2 and follow them, etc.
 *
 * Example:
 * nodeIds: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
 * depths:  0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2
 *
 * the forest can be visualized as follows:
 * 1
 * - 2
 * - - 3
 * - - - 4
 * - 5
 * 6
 * - 7
 * 8
 * - 9
 * - 10
 * - - 11
 *
 * 1 is a parent of 2 and 5, 2 is a parent of 3, etc. Note that depth is equal to the number of hyphens for each node.
 *
 * Invariants on the depths array:
 *  * Depth of the first element is 0.
 *  * If the depth of a node is `D`, the depth of the next node in the array can be:
 *      * `D + 1` if the next node is a child of this node;
 *      * `D` if the next node is a sibling of this node;
 *      * `d < D` - in this case the next node is not related to this node.
 */
public interface Hierarchy {
    /**
     * The number of nodes in the hierarchy.
     */
    int getSize();

    /**
     * Returns the unique ID of the node identified by the hierarchy index. The depth for this node will be `depth(index)`.
     * @param index must be non-negative and less than getSize()
     */
    int nodeId(int index);

    /**
     * Returns the depth of the node identified by the hierarchy index. The unique ID for this node will be `nodeId(index)`.
     * @param index must be non-negative and less than getSize()
     */
    int depth(int index);

    default String formatString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < getSize(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(nodeId(i)).append(":").append(depth(i));
        }
        sb.append("]");
        return sb.toString();
    }
}

/**
 * A node is present in the filtered hierarchy iff its node ID passes the predicate and all of its ancestors pass it as well.
 */
class HierarchyFilter {
    public static Hierarchy filter(Hierarchy hierarchy, IntPredicate nodeIdPredicate) {

        List<Integer> newNodeIds = new ArrayList<>();
        List<Integer> newDepths = new ArrayList<>();

        Map<Integer, Boolean> keepAtDepth = new HashMap<>();

        for (int i = 0; i < hierarchy.getSize(); i++) {

            int id = hierarchy.nodeId(i);
            int depth = hierarchy.depth(i);
            boolean parentKept;

            if (depth == 0) {
                parentKept = true;
            } else {
                parentKept = keepAtDepth.getOrDefault(depth - 1, false);
            }

            boolean keep = parentKept && nodeIdPredicate.test(id);
            keepAtDepth.put(depth, keep);
            if (keep) {
                newNodeIds.add(id);
                newDepths.add(depth);
            }
        }

        int[] idsArray = newNodeIds.stream().mapToInt(Integer::intValue).toArray();
        int[] depthArray = newDepths.stream().mapToInt(Integer::intValue).toArray();

        return new ArrayBasedHierarchy(idsArray, depthArray);
    }
}

class ArrayBasedHierarchy implements Hierarchy {
    private final int[] myNodeIds;
    private final int[] myDepths;

    public ArrayBasedHierarchy(int[] nodeIds, int[] depths) {
        this.myNodeIds = nodeIds;
        this.myDepths = depths;
    }

    @Override
    public int getSize() {
        return myDepths.length;
    }

    @Override
    public int nodeId(int index) {
        return myNodeIds[index];
    }

    @Override
    public int depth(int index) {
        return myDepths[index];
    }
}


