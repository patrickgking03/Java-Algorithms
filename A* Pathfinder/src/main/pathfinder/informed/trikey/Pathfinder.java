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
        // Create new list to store the path found and to make a frontier queue
    	List<String> pathfinder = new LinkedList<String>();
    	Queue<SearchTreeNode> frontier = new LinkedList<>();
    	
    	// store the roots found
    	SearchTreeNode roots = new SearchTreeNode(problem.getInitial(), null, null);
    	
    	// store the roots and add them to the frontier
    	frontier.add(roots);
    	
    	// check when not the frontier is empty
    	while (!(frontier.isEmpty())) {
    		
    		// new node for expanded node
    		SearchTreeNode expandedNode = frontier.poll();
            // >> [AF] Remove print statements before submission in the future; they will substantially
            // slow your solution down! (-0.25) 
    		System.out.println(expandedNode.state);
    		
    		// track transitions in the maze for the pathfinder
    		Map<String, MazeState> transition = problem.getTransitions(expandedNode.state);
    		for (Map.Entry<String, MazeState> entry : transition.entrySet()) {
    			
    			// key states
                // >> [AF] entry.getValue() is a next state, but problem.getKeyStates() is a set,
                // so this will never return a solution
    			if (entry.getValue().equals(problem.getKeyStates())) {
    				pathfinder.add(entry.getKey());
    			}
    			
    			// child of the trees
    			SearchTreeNode child = new SearchTreeNode(entry.getValue(), entry.getKey(), expandedNode);
    			frontier.add(child);
    		}
    	}
    	
    	// return null
    	return null;
    }

    /**
     * SearchTreeNode private static nested class that is used in the Search
     * algorithm to construct the Search tree.
     * [!] You may do whatever you want with this class -- in fact, you'll need 
     * to add a lot for a successful and efficient solution!
     */
    private static class SearchTreeNode {

        MazeState state;
        String action;
        SearchTreeNode parent;

        /**
         * Constructs a new SearchTreeNode to be used in the Search Tree.
         * 
         * @param state  The MazeState (row, col) that this node represents.
         * @param action The action that *led to* this state / node.
         * @param parent Reference to parent SearchTreeNode in the Search Tree.
         */
        SearchTreeNode(MazeState state, String action, SearchTreeNode parent) {
            this.state = state;
            this.action = action;
            this.parent = parent;
        }

    }

}

// ===================================================
// >>> [AF] Summary
// A decent start, you were wise to begin with the
// skeleton that the CW2 solution provided, but it looks
// like you ran out of time to adapt it to the current
// assignment's implementation of A* and in particular,
// to its requirement of finding sub-goals (which you
// could have gotten some partial credit doing with just
// breadth-first). Because you submitted an *unaltered*
// CW2 solution, I can't give you any credit.
//
// Remember that these assignments take a lot of time to 
// think about, rethink, sleep-on, debug, ask questions about, 
// etc. so don't hamstring yourself by trying to do all of 
// that near the deadline. You'll get the next one indeed,
// I believe!
// ---------------------------------------------------
// >>> [AF] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [X] Variables and helper methods named and used well
// [X] Proper and consistent indentation and spacing
// [X] Proper JavaDocs provided for ALL methods
// [X] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:         0 / 100 (-1.5 / missed unit test)
//   -> Refunded 2 grading tests, so will be out of 28
//      not 30
// Style Penalty:       -0
// Total:               0 / 100
// ===================================================
