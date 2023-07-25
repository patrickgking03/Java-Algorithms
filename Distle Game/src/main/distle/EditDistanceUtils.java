package main.distle;

import java.util.*;

public class EditDistanceUtils {
    
    /**
     * Returns the completed Edit Distance memoization structure, a 2D array
     * of ints representing the number of string manipulations required to minimally
     * turn each subproblem's string into the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return Completed Memoization structure for editDistance(s0, s1)
     */
    public static int[][] getEditDistTable (String s0, String s1) {
	// >> [TN] You can delete these TODOs once you've done the task
        // [!] TODO!
    	
    	// Initialize the array
	// >> [TN} spelling out distance table would strengthen this name since DT isn't a common acronym
    	int[][] editDT = new int[s0.length()+1][s1.length()+1];
        
    	// For loop (nested) to iterate through array
    	for (int r = 0; r < editDT.length; r++) {
        	for (int c = 0; c < editDT[r].length; c++) {
        		
        		// Base case fillers (only if gutter)
        		if (c == 0 || r == 0) {
        			editDT[r][c] = r + c;
        		}
        		
        		// No gutter
        		else {
        			
        			// Transposition
				// >> [TN] This logic could be cleaned up a bit. There are many values computed multiple times with
				// nested Math.mins that are getting hard to follow. Computing each possible edit once, adding it to a list,
				// and finding the smallest value would be simpler and easier to follow (-0.5)
        			if (r > 1) {
        				if (c > 1) {
        					if (s0.charAt(r - 1) == s1.charAt(c - 2) && s0.charAt(r - 2) == s1.charAt(c - 1) && s0.charAt(r - 1) == s1.charAt(c - 1)){
            					editDT[r][c] = Math.min(editDT[r - 2][c - 2] + 1, Math.min(editDT[r - 1][c - 1], Math.min(editDT[r - 1][c] + 1, editDT[r][c - 1] + 1)));
            					continue;
            				}
            				if (s0.charAt(r - 1) == s1.charAt(c - 2) && s0.charAt(r - 2) == s1.charAt(c - 1)){
            					editDT[r][c] = Math.min(editDT[r - 2][c - 2] + 1, Math.min(editDT[r - 1][c - 1] + 1, Math.min(editDT[r - 1][c] + 1, editDT[r][c - 1] + 1)));
            					continue;
            				}
        				}
        			}
        			
        			// Replacement
        			if (s0.charAt(r - 1) == s1.charAt(c - 1)) {
        				editDT[r][c] = Math.min(editDT[r - 1][c - 1], Math.min(editDT[r - 1][c] + 1, editDT[r][c - 1] + 1));
        				continue;
        			}
        			
        			else {
        				editDT[r][c] = Math.min(editDT[r - 1][c - 1] + 1, Math.min(editDT[r - 1][c] + 1, editDT[r][c - 1] + 1));
        			}
        		}
        	}
        }
    	
    	// Returning array at the end of the function
        return editDT; 
    }
    
    /**
     * Returns one possible sequence of transformations that turns String s0
     * into s1. The list is in top-down order (i.e., starting from the largest
     * subproblem in the memoization structure) and consists of Strings representing
     * the String manipulations of:
     * <ol>
     *   <li>"R" = Replacement</li>
     *   <li>"T" = Transposition</li>
     *   <li>"I" = Insertion</li>
     *   <li>"D" = Deletion</li>
     * </ol>
     * In case of multiple minimal edit distance sequences, returns a list with
     * ties in manipulations broken by the order listed above (i.e., replacements
     * preferred over transpositions, which in turn are preferred over insertions, etc.)
     * @param s0 String transforming into other
     * @param s1 Target of transformation
     * @param table Precomputed memoization structure for edit distance between s0, s1
     * @return List that represents a top-down sequence of manipulations required to
     * turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements followed
     * by a transposition, then insertion.
     */
    public static List<String> getTransformationList (String s0, String s1, int[][] table) {
        // [!] TODO!
        
    	// Returns the list thanks to the helper function below
    	return transformationHelper(s0.length(), s1.length(), new ArrayList<String>(), s0, s1);
    }
    
    // Helper function 'transformationHelper' to the function 'getTransformationList'
    private static List<String> transformationHelper(int r, int c, List<String> list, String s0, String s1) {
    	
    	// Initialize table
    	int[][] distleTable = getEditDistTable(s0, s1);
    	
    	// If rows/columns are 0, return list
    	if (r == 0) {
    		return list;
    	}
    	if (c == 0) {
    		return list;
    	}
    	
	// >> [TN] The logic here could really be simplified. You shouldn't need to check if it's a deletion three seperate 
	// times. If code starts getting like this, I'd recommend taking a step back and reevaluating your strategy. (-0.5)
    	// Add I if row is 0, D if column 0
    	else if (r == 0) {
    		list.add("I");
    		transformationHelper(r, c-1, list, s0, s1);
    	}
    	else if (c == 0) {
    		list.add("D");
    		transformationHelper(r-1, c, list, s0, s1);
    	}
    	
    	// Don't add same letters
    	else if (s0.charAt(r - 1) == s1.charAt(c - 1)) {
    		transformationHelper(r - 1, c - 1, list, s0, s1);
    	}
    	
    	// Transposition
    	else if (r > 1 && c > 1){
    		if (s0.charAt(r - 1) == s1.charAt(c - 2) && s0.charAt(r - 2) == s1.charAt(c - 1)){
				
    			// Transposition in the string
    			if (distleTable[r][c] == distleTable[r - 1][c - 1] + 1) {
					list.add("R");
					transformationHelper(r - 1, c - 1, list, s0, s1);
				}
				else if (distleTable[r][c] == distleTable[r - 2][c - 2] + 1) {
					list.add("T");
					transformationHelper(r - 2, c - 2, list, s0, s1);
				}
				else if (distleTable[r][c] == distleTable[r][c - 1] + 1) {
					list.add("I");
					transformationHelper(r, c - 1, list, s0, s1);
				}
				else if (distleTable[r][c] == distleTable[r - 1][c] + 1) {
					list.add("D");
					transformationHelper(r - 1, c, list, s0, s1);
				}
    		}
    		
    		// Transposition in specific characters
    		else {
    			if (distleTable[r][c] == distleTable[r - 1][c - 1] + 1) {
    				list.add("R");
    				transformationHelper(r - 1, c - 1, list, s0, s1);
    			}
    			else if (distleTable[r][c] == distleTable[r][c - 1] + 1) {
    				list.add("I");
    				transformationHelper(r, c - 1, list, s0, s1);
    			}
    			else if (distleTable[r][c] == distleTable[r - 1][c] + 1) {
    				list.add("D");
    				transformationHelper(r - 1, c, list, s0, s1);
    			}
    		}
    	}
    	
    	// More transposition in specific characters
    	else {
			if (distleTable[r][c] == distleTable[r - 1][c - 1] + 1) {
				list.add("R");
				transformationHelper(r - 1, c - 1, list, s0, s1);
			}
			else if (distleTable[r][c] == distleTable[r][c - 1] + 1) {
				list.add("I");
				transformationHelper(r, c - 1, list, s0, s1);
			}
			else if (distleTable[r][c] == distleTable[r - 1][c] + 1) {
				list.add("D");
				transformationHelper(r - 1, c, list, s0, s1);
			}
		}
		return list;
	}
    
    /**
     * Returns the edit distance between the two given strings: an int
     * representing the number of String manipulations (Insertions, Deletions,
     * Replacements, and Transpositions) minimally required to turn one into
     * the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return The minimal number of manipulations required to turn s0 into s1
     */
    public static int editDistance (String s0, String s1) {
        if (s0.equals(s1)) { return 0; }
        return getEditDistTable(s0, s1)[s0.length()][s1.length()];
    }
    
    /**
     * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
     */
    public static List<String> getTransformationList (String s0, String s1) {
        return getTransformationList(s0, s1, getEditDistTable(s0, s1));
    }

}

// ===================================================
// >>> [TN] Summary
// Code works pretty well for the most part. 
// EditDistanceUtils could've use a bit more testing but
// the player works great. Code style could be improved
// and some logic simplified but overall nice work. 
// ---------------------------------------------------
// >>> [TN] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [~] Variables and helper methods named and used well
// [X] Proper and consistent indentation and spacing
// [X] Proper JavaDocs provided for ALL methods
// [ ] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:          100 / 100
// -> EditDistUtils:      16 / 20  (-2 / missed test)
// -> DistlePlayer:      288 / 265 (-0.5 / below threshold; max -30)
// Style Penalty:         -1
// Total:                91 / 100
// ===================================================
