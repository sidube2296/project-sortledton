package edu.uwm.cs351;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * UnrolledSkipList is an implementation of the Neighborhood interface, optimized
 * for handling large neighborhoods by maintaining a list of sorted blocks.
 * Each block contains a subset of neighbors, facilitating efficient scans and intersections.
 *
 * @param <T> The type of the vertex ID, must be Comparable.
 */
public class UnrolledSkipList<T extends Comparable<T>> implements Neighborhood<T> {
    /** The maximum number of elements per block. */
    private static final int BLOCK_SIZE = 128;

    /** List of sorted blocks, each block is a sorted list of neighbors. */
    private final List<List<T>> blocks;

    /** Comparator for sorting elements */
    private final Comparator<T> comparator;

    /**
     * Constructs an UnrolledSkipList with an empty set of blocks.
     */
    public UnrolledSkipList() {
        this.blocks = new ArrayList<>();
        this.comparator = new Comparator<T>() {
            @Override
            public int compare(T a, T b) {
                if (a instanceof String && b instanceof String) {
                    String s1 = (String) a;
                    String s2 = (String) b;
                    String prefix1 = extractPrefix(s1);
                    String prefix2 = extractPrefix(s2);

                    int prefixCmp = prefix1.compareTo(prefix2);
                    if (prefixCmp != 0) {
                        return prefixCmp;
                    }

                    Integer num1 = extractNumericSuffix(s1);
                    Integer num2 = extractNumericSuffix(s2);

                    if (num1 != null && num2 != null) {
                        return num1.compareTo(num2);
                    } else if (num1 != null) {
                        // s1 has a numeric suffix, s2 does not
                        return 1; // s2 comes before s1
                    } else if (num2 != null) {
                        // s2 has a numeric suffix, s1 does not
                        return -1; // s1 comes before s2
                    } else {
                        // Neither has a numeric suffix
                        return 0;
                    }
                } else {
                    // For non-String types, use natural ordering
                    return a.compareTo(b);
                }
            }

            /**
             * Extracts the prefix from a string (non-numeric part).
             *
             * @param s The input string.
             * @return The prefix part of the string.
             */
            private String extractPrefix(String s) {
                int i = 0;
                while (i < s.length() && !Character.isDigit(s.charAt(i))) {
                    i++;
                }
                return s.substring(0, i);
            }

            /**
             * Extracts the numeric suffix from a string.
             *
             * @param s The input string.
             * @return The numeric suffix as an Integer, or null if not present.
             */
            private Integer extractNumericSuffix(String s) {
                int i = 0;
                while (i < s.length() && !Character.isDigit(s.charAt(i))) {
                    i++;
                }
                if (i == s.length()) {
                    return null; // No numeric suffix
                }
                try {
                    return Integer.parseInt(s.substring(i));
                } catch (NumberFormatException e) {
                    return null; // Invalid numeric suffix
                }
            }
        };
        assert wellFormed() : "Invariant failed at end of constructor.";
    }

    /**
     * Ensures the internal invariants hold:
     * - Blocks list is not null.
     * - No block is null or empty.
     * - Each block is sorted in ascending order based on the comparator.
     * - No duplicates across all blocks.
     *
     * @return true if well-formed, false otherwise.
     */
    private boolean wellFormed() {
        if (blocks == null) return false;
        T previous = null;
        for (List<T> block : blocks) {
            if (block == null || block.isEmpty()) return false; // Blocks should not be empty
            for (T elem : block) {
                if (elem == null) return false;
                if (previous != null && comparator.compare(previous, elem) >= 0) {
                    return false;
                }
                previous = elem;
            }
            if (block.size() > BLOCK_SIZE) {
                return false; // Blocks should not exceed BLOCK_SIZE
            }
        }
        return true;
    }

    /**
     * Adds a neighbor to the neighborhood, maintaining sorted order and block sizes.
     * If the neighbor already exists, it is not added again.
     *
     * @param id The ID of the neighbor to add.
     * @throws IllegalArgumentException if the neighbor ID is null.
     */
    @Override
    public void addNeighbor(T id) {
        if (id == null) {
            throw new IllegalArgumentException("Neighbor ID cannot be null");
        }
        assert wellFormed() : "Invariant failed at start of addNeighbor.";

        if (blocks.isEmpty()) {
            List<T> newBlock = new ArrayList<>();
            newBlock.add(id);
            blocks.add(newBlock);
            assert wellFormed() : "Invariant failed at end of addNeighbor.";
            return;
        }

        // Binary search to find the correct block
        int blockIndex = findBlock(id);

        // Adjust blockIndex if it's beyond the current blocks
        if (blockIndex == blocks.size()) {
            blockIndex = blocks.size() - 1;
        }

        List<T> targetBlock = blocks.get(blockIndex);

        // Binary search within the block using the custom comparator
        int insertPos = Collections.binarySearch(targetBlock, id, comparator);
        if (insertPos >= 0) {
            // Element already exists; do not add duplicate
            assert wellFormed() : "Invariant failed at end of addNeighbor.";
            return;
        } else {
            insertPos = -insertPos - 1;
            targetBlock.add(insertPos, id);
        }

        // If the block exceeds BLOCK_SIZE, split it
        if (targetBlock.size() > BLOCK_SIZE) {
            List<T> newBlock = new ArrayList<>(targetBlock.subList(BLOCK_SIZE / 2, targetBlock.size()));
            // Remove the elements that have been moved to the new block
            targetBlock.subList(BLOCK_SIZE / 2, targetBlock.size()).clear();
            blocks.add(blockIndex + 1, newBlock);
        }

        assert wellFormed() : "Invariant failed at end of addNeighbor.";
    }

    /**
     * Removes a neighbor from the neighborhood if it exists, maintaining block sizes.
     *
     * @param id The ID of the neighbor to remove.
     * @throws IllegalArgumentException if the neighbor ID is null.
     */
    @Override
    public void removeNeighbor(T id) {
        if (id == null) {
            throw new IllegalArgumentException("Neighbor ID cannot be null");
        }
        assert wellFormed() : "Invariant failed at start of removeNeighbor.";

        if (blocks.isEmpty()) {
            assert wellFormed() : "Invariant failed at end of removeNeighbor.";
            return;
        }

        // Binary search to find the correct block
        int blockIndex = findBlock(id);
        if (blockIndex == blocks.size()) {
            // Element does not exist
            assert wellFormed() : "Invariant failed at end of removeNeighbor.";
            return;
        }

        List<T> targetBlock = blocks.get(blockIndex);
        // Binary search within the block using the custom comparator
        int pos = Collections.binarySearch(targetBlock, id, comparator);
        if (pos >= 0) {
            targetBlock.remove(pos);
            // If the block becomes too small, consider merging with adjacent blocks
            if (targetBlock.isEmpty()) {
                blocks.remove(blockIndex);
            } else if (targetBlock.size() < BLOCK_SIZE / 2 && blocks.size() > 1) {
                // Try to merge with previous or next block
                if (blockIndex > 0) {
                    List<T> prevBlock = blocks.get(blockIndex - 1);
                    // Ensure that the last element of prevBlock is less than the first of targetBlock
                    if (comparator.compare(prevBlock.get(prevBlock.size() - 1), targetBlock.get(0)) < 0
                            && (prevBlock.size() + targetBlock.size()) <= BLOCK_SIZE) {
                        prevBlock.addAll(targetBlock);
                        blocks.remove(blockIndex);
                    }
                } else if (blockIndex < blocks.size() - 1) {
                    List<T> nextBlock = blocks.get(blockIndex + 1);
                    // Ensure that the last element of targetBlock is less than the first of nextBlock
                    if (comparator.compare(targetBlock.get(targetBlock.size() - 1), nextBlock.get(0)) < 0
                            && (nextBlock.size() + targetBlock.size()) <= BLOCK_SIZE) {
                        targetBlock.addAll(nextBlock);
                        blocks.remove(blockIndex + 1);
                    }
                }
            }
        }

        assert wellFormed() : "Invariant failed at end of removeNeighbor.";
    }

    /**
     * Retrieves all neighbors in the neighborhood as a new sorted list.
     *
     * @return A list of IDs representing neighbors in sorted order.
     */
    @Override
    public List<T> getNeighbors() {
        assert wellFormed() : "Invariant failed at start of getNeighbors.";
        List<T> result = new ArrayList<>(size());
        for (List<T> block : blocks) {
            result.addAll(block);
        }
        // Assuming blocks are globally sorted, no need to sort
        assert wellFormed() : "Invariant failed at end of getNeighbors.";
        return result;
    }

    /**
     * Finds the intersection between this neighborhood and another unrolled skip list.
     * Utilizes the fact that both neighborhoods are sorted for efficient intersection.
     *
     * @param other The other UnrolledSkipList to intersect with.
     * @return A list of IDs representing common neighbors.
     * @throws IllegalArgumentException if the other neighborhood is null.
     */
    @Override
    public List<T> intersect(Neighborhood<T> other) {
        if (other == null) {
            throw new IllegalArgumentException("Other neighborhood cannot be null");
        }
        assert wellFormed() : "Invariant failed at start of intersect.";

        List<T> result = new ArrayList<>();
        List<T> otherNeighbors = other.getNeighbors();
        int i = 0, j = 0;
        List<T> thisNeighbors = getNeighbors();

        while (i < thisNeighbors.size() && j < otherNeighbors.size()) {
            T a = thisNeighbors.get(i);
            T b = otherNeighbors.get(j);
            int cmp = comparator.compare(a, b);
            if (cmp == 0) {
                result.add(a);
                i++;
                j++;
            } else if (cmp < 0) {
                i++;
            } else {
                j++;
            }
        }

        assert wellFormed() : "Invariant failed at end of intersect.";
        return result;
    }

    /**
     * Returns the total number of neighbors in the neighborhood.
     *
     * @return The size of the neighborhood.
     */
    public int size() {
        int total = 0;
        for (List<T> block : blocks) {
            total += block.size();
        }
        return total;
    }

    /**
     * Finds the appropriate block index for a given element using binary search.
     *
     * @param id The element to locate.
     * @return The index of the block where the element should reside.
     */
    private int findBlock(T id) {
        int low = 0;
        int high = blocks.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            List<T> midBlock = blocks.get(mid);
            T first = midBlock.get(0);
            T last = midBlock.get(midBlock.size() - 1);
            if (comparator.compare(id, first) < 0) {
                high = mid - 1;
            } else if (comparator.compare(id, last) > 0) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return low;
    }
    

    /**
     * Provides a string representation of the UnrolledSkipList for debugging purposes.
     *
     * @return A string representing the UnrolledSkipList.
     */
    @Override
    public String toString() {
        List<T> neighbors = getNeighbors();
        return "UnrolledSkipList" + neighbors.toString();
    }
    
    /**
     * Checks if a neighbor exists in this neighborhood using binary search across blocks.
     *
     * @param id The ID of the neighbor to check.
     * @return True if the neighbor exists, otherwise false.
     */
    @Override
    public boolean contains(T id) {
        if (id == null) return false;
        int blockIndex = findBlock(id);
        if (blockIndex == blocks.size()) return false;
        List<T> targetBlock = blocks.get(blockIndex);
        return Collections.binarySearch(targetBlock, id, comparator) >= 0;
    }
}
