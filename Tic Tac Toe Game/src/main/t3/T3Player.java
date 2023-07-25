package main.t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {
    
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of the
     * game of Tic-Tac-Total. Note: In the event that multiple moves have
     * equivalently maximal minimax scores, ties are broken by move col, then row,
     * then move number in ascending order (see spec and unit tests for more info).
     * The agent will also always take an immediately winning move over a delayed
     * one (e.g., 2 moves in the future).
     * 
     * @param state
     *            The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
	public T3Action choose (T3State state) {
        Map<T3Action, T3State> transitions = state.getTransitions();
        int utilityScore = Integer.MIN_VALUE;
        T3Action returner = null; 
        
        for (Map.Entry<T3Action, T3State> entry : transitions.entrySet()) {
        	if (entry.getValue().isWin()) {
        		return entry.getKey();
        	}
        }
        
        for (Map.Entry<T3Action, T3State> entry : transitions.entrySet()) {
        	int minimax = alphabeta(Integer.MIN_VALUE, Integer.MAX_VALUE, false, entry.getValue());
        	if (minimax > utilityScore) {
        		utilityScore = minimax;
        		returner = entry.getKey();
        		if (utilityScore == 1) {
        			break;
        		}
        	}
        }
        return returner;
    }
    
    // TODO: Implement your alpha-beta pruning recursive helper here!
	
    private static int alphabeta (int alpha, int beta, boolean maxPlayer, T3State state) {
    	if (state.isWin() && !maxPlayer) {
            return 1;
        } 
    	else if (state.isWin() && maxPlayer) {
            return -1;
        } 
        else if (state.isTie()) {
            return 0;
        } 
        
        else {
            int var = maxPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for (Map.Entry<T3Action, T3State> entry : state.getTransitions().entrySet()) {
                int child = alphabeta(alpha, beta, !maxPlayer, entry.getValue());
                if (maxPlayer) {
                    var = Math.max(var, child);
                    alpha = Math.max(alpha, var);
                } else {
                    var = Math.min(var, child);
                    beta = Math.min(beta, var);
                }
                if (beta <= alpha) {
                    break;
                }
            }
            return var;
        }
    }
}