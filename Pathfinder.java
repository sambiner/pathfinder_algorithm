/*
 Filename: Pathfinder.java
 Description: Create a maze pathfinding problem that implements an informed, A* search algorithm for 
                multiple subgoals
 Author: Sam Biner
 Date: 1/23/2023
 */

package main.pathfinder.informed.trikey;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first
 * tree search.
 */
public class Pathfinder {

    /**
     * Given a MazeProblem, which specifies the actions and transitions available in
     * the search, returns a solution to the problem as a sequence of actions that
     * leads from the initial state to the collection of all three key pieces.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return A List of Strings representing actions that solve the problem of the
     *         format: ["R", "R", "L", ...]
     */
    public static List<String> solve(MazeProblem problem) {

        // Initializing List of strings for the result
        List<String> result = new ArrayList<>();

        // Initializing the graveyard for visited nodes
        Set<SearchTreeNode> graveyard = new HashSet<>();

        // Initializing the frontier for un-expanded nodes and overriding the
        // compareTo function
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<>();

        // Initialize the root node for the search tree
        SearchTreeNode root = new SearchTreeNode(problem.getInitial(), null, null, 0);

        // Initialize the keysCollected HashSet to track acquired keys
        root.keysCollected = new HashSet<>();

        // Add the unexpanded root to the frontier
        frontier.add(root);

        // While the frontier is not empty, search for a solution
        while (!frontier.isEmpty()) {

            // Poll the first node in the frontier to expand
            SearchTreeNode curr = frontier.poll();

            // Checking if the state's keyPiece is not null
            if (curr.state.keyPiece() != null) {
                // If it isn't, add that state to the keysCollected HashSet
                curr.keysCollected.add(curr.state);
            }

            // Goal test: If the keysCollected Set is the same size as the MazeProblem's
            // getKeyStates Set
            if (curr.keysCollected.size() == problem.getKeyStates().size()) {
                // and while current's parent is not null
                while (curr.parent != null) {
                    // add current's action to the result list
                    result.add(0, curr.action);
                    // make curr equal to it's parent and loop through until we reach the root node
                    curr = curr.parent;
                }
                // return the result
                return result;
            }

            // Check if the graveyard contains the current state
            if (!graveyard.contains(curr)) {
                // If it doesn't, add it to the graveyard
                graveyard.add(curr);
                // For each action in the problem's transitions, generate a child node
                for (Map.Entry<String, MazeState> action : problem.getTransitions(curr.state).entrySet()) {
                    SearchTreeNode child = new SearchTreeNode(action.getValue(), action.getKey(), curr,
                            curr.cost + problem.getCost(action.getValue()));
                    frontier.add(child);
                }
            }
        }
        // When the frontier runs out, return null since all nodes have been searched
        return null;
    }

    /**
     * SearchTreeNode private static nested class that is used in the Search
     * algorithm to construct the Search tree.
     * [!] You may do whatever you want with this class -- in fact, you'll need
     * to add a lot for a successful and efficient solution!
     */
    private static class SearchTreeNode implements Comparable<SearchTreeNode> {

        MazeState state;
        String action;
        SearchTreeNode parent;
        Set<MazeState> keysCollected;
        int cost;

        /**
         * Constructs a new SearchTreeNode to be used in the Search Tree.
         * 
         * @param state  The MazeState (row, col) that this node represents.
         * @param action The action that *led to* this state / node.
         * @param parent Reference to parent SearchTreeNode in the Search Tree.
         */
        SearchTreeNode(MazeState state, String action, SearchTreeNode parent, int cost) {
            this.state = state;
            this.action = action;
            this.parent = parent;
            if (parent != null) {
                this.keysCollected = new HashSet<>(parent.keysCollected);
            } else {
                this.keysCollected = new HashSet<>();
            }
            this.cost = cost;
        }

        @Override
        public int compareTo(SearchTreeNode other) {
            return this.cost - other.cost;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;

            if (!(o instanceof SearchTreeNode)) {
                return false;
            }
            SearchTreeNode searchTreeNode = (SearchTreeNode) o;
            return state.equals(searchTreeNode.state)
                    && Objects.equals(keysCollected, searchTreeNode.keysCollected);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, keysCollected);
        }

    }

}
