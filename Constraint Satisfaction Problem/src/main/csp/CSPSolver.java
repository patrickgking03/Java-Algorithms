package main.csp;

import java.time.LocalDate;
import java.util.*;

import test.csp.CSPTests;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSPSolver {

    // Backtracking CSP Solver
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {

        // Initialize a list to store the schedule meeting dates
    	List<LocalDate> scheduledMeetings = new ArrayList<>();
    	
    	// generate a list of objects based on number of meetings with start/end date
    	List<MeetingDomain> meetingDomains = CSPTests.generateDomains(nMeetings, rangeStart, rangeEnd);
    	
    	// Employ the node consistency on meeting domains using the given constraints
        nodeConsistency(meetingDomains, constraints);
        
        // Employ the arc consistency on meeting domains using the given constraints
        arcConsistency(meetingDomains, constraints);
    	
        // solve the CSP problem with the meeting domains, meeting times, constraints, number of meetings
        // and then return the result as a list of objects
        return cspMethod(meetingDomains, scheduledMeetings, nMeetings, constraints);   
    }
    
    // Helper Methods for 'solve'
    
    private static List<LocalDate> cspMethod (List<MeetingDomain> meetingCounter, List<LocalDate> meetingAttacher, int nMeetings, Set<DateConstraint> constraints){
    	
    	// Base case if size of list equals nMeetings
    	if (meetingAttacher.size() == nMeetings) {
    		
    		// Check if list satisfies all constraints
    		if (consistencyChecker(constraints, meetingAttacher)) {
    			
    			// return the list
    			return meetingAttacher;
    		}
    	}	
    	
    	// Loop through each meeting in the list
    	for (MeetingDomain thisMeeting : meetingCounter) {
	    	
    		// Loop through each domain value
    		for (LocalDate domainValue : thisMeeting.domainValues) {
	    		
    			// Add domain value to the list
    			meetingAttacher.add(domainValue);
	    		
    			// Check if list satisfies constraints
    			if (consistencyChecker(constraints, meetingAttacher)) {
	    			
    				// Using recursion, call the cspMethod with the updated list
    				List<LocalDate> result = cspMethod(meetingCounter, meetingAttacher, nMeetings, constraints);
	    			
    				// If recursive call returns a non-null result, return that result as a score
    				if (result != null) {
	    				return result;
	    			}	
	    		}
    			
    			// If domain value doesn't satisfy constraints, remove from list
	    		meetingAttacher.remove(meetingAttacher.size() - 1);
	    	}
    	}
    	
    	// If no valid solution is discovered, return null
    	return null;	
    }
    
    private static boolean inconsistentValues (List<MeetingDomain> domains, Arc arc) {
    	
    	// Initialize set to store consistent domain values
    	Set<LocalDate> consistentDomain = new HashSet<>();
    	
    	// Retrieve the tail domain and head domain from list using the arc's tail/head
    	MeetingDomain tailDomain = domains.get(arc.TAIL);
    	MeetingDomain headDomain = domains.get(arc.HEAD);

    	// Get the constraint associated with the arc
    	DateConstraint constraint = arc.CONSTRAINT;

    	// Loop through each value in tail domain
    	for (LocalDate tailVal : tailDomain.domainValues) {
    	    
    		// Loop through each value in head domain
    	    for (LocalDate headVal : headDomain.domainValues) {
    	        
    	    	// Check if constraint is binary
    	        if (constraint.ARITY == 2) {
    	            
    	        	// Cast constraint to a BinaryDateConstraint
    	            BinaryDateConstraint newBinaryConstraint = (BinaryDateConstraint)constraint;
    	            
    	            // Check if tail value and head value satisfy constraint
    	            if (newBinaryConstraint.isSatisfiedBy(tailVal, headVal)) {
    	                
    	            	// If constraint is satisfied, add tail value to 'result' set and break inner loop
    	            	consistentDomain.add(tailVal);
    	                break;
    	            }
    	        }
    	    }
    	}

    	// Check if size of 'result' set is different from the size of the tail domain values
    	boolean changer = consistentDomain.size() != tailDomain.domainValues.size();

    	// Update the tail domain values to the consistent values found in 'result' set
    	tailDomain.domainValues = consistentDomain;

    	// Return true if tail domain values have changed, otherwise return 'false'
    	return changer;	
    }
    
    private static boolean consistencyChecker (Set<DateConstraint> constraints, List<LocalDate> meetings) {
    	
    	// Loop through each constraint in set
    	for (DateConstraint constraint : constraints) {
    		
    		// Check if constraint is unary
    		if (constraint.ARITY == 1) {
    			
    			// Cast constraint
    			UnaryDateConstraint newUnaryConstraint = (UnaryDateConstraint)constraint;
    			
    			// Get right and left values from constraint
    			LocalDate right = newUnaryConstraint.R_VAL; 
    			int left = constraint.L_VAL;
    			
    			// If meetings list is greater than left
    			if (meetings.size() > left) {
    				
    				// If constraint is not satisfied, return false
	    			if (!(constraint.isSatisfiedBy(meetings.get(left), right))) {
	    				return false;
	    			}
    			}
    		}
    		
    		// Check if constraint is binary
    		if (constraint.ARITY == 2) {
    			
    			// Cast constraint to a binarydateconstraint
    			BinaryDateConstraint newBinaryConst = (BinaryDateConstraint)constraint;
    			
    			// Get right and left values from constraint
    			int right = newBinaryConst.R_VAL;
    			int left = constraint.L_VAL;
    			
    			// If meetings list size is greater than right and left, check constraint
    			if (meetings.size() > right && meetings.size() > left) {
	    			
    				// If constraint is not satisfied, return false
    				if (!(constraint.isSatisfiedBy(meetings.get(left), meetings.get(right)))) {
	    				return false;
	    			}
    			}
    		}		
    	}
    	
    	// If constraints are satisfied return true
    	return true;
    }
    
    // Filtering Operations
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Enforces node consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *unary* constraints! 
     */
    public static void nodeConsistency (List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
        
    	// Initialize a set to store consistent domain values
    	Set<LocalDate> answer = new HashSet<>();
    	
    	// Loop through each constraint
    	for (DateConstraint constraint : constraints) {
    		
    		// Check if constraint is empty
    		if (constraint.ARITY == 1) {
    			
    			// Cast constraint
    			UnaryDateConstraint unaryDateConstraint = (UnaryDateConstraint)constraint;
    			
    			// get right and left values from constraint
    			LocalDate right = unaryDateConstraint.R_VAL; 
    			int left = unaryDateConstraint.L_VAL;
    			
    			// Retrieve corresponding meeting domain
    			MeetingDomain newDomain = varDomains.get(left);
    			
    			// Loop through each date in the domain values of the meeting domain
    			for (LocalDate date : newDomain.domainValues ) {
    				
    				// Check if constraint is satisfied, add date if satisfied
    				if ((unaryDateConstraint.isSatisfiedBy(date, right))) {
    					answer.add(date);
    				}	
    			} 
    			
    			// Update domain values
    			newDomain.domainValues = answer;
    			
    			// Clear result set for next iteration
    			answer = new HashSet<>();
    		}
    	}
    }
    
    /**
     * Enforces arc consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *binary* constraints using the AC-3 algorithm! 
     */
    public static void arcConsistency (List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
        // [!] TODO!
        
    	// Initialize a set to store the binary constraint arcs
    	Set<Arc> arcQueue = new HashSet<>();
    	
    	// Loop through each constraint in the 'constraints' set
    	for (DateConstraint constraint : constraints) {
    	    
    		// Check if the constraint is binary
    	    if (constraint.ARITY == 2) {
    	        
    	    	// Cast the constraint to a BinaryDateConstraint
    	        BinaryDateConstraint newConstraint = (BinaryDateConstraint) constraint;

    	        // Get the left and right values from the constraint
    	        int right = newConstraint.R_VAL;
    	        int left = newConstraint.L_VAL;

    	        // Create two Arc objects, one for the original constraint and one for the backwards constraint
    	        Arc firstArc = new Arc(left, right, newConstraint);
    	        BinaryDateConstraint backwards = newConstraint.getReverse();
    	        Arc secondArc = new Arc(right, left, backwards);

    	        // Add both Arc objects to the set
    	        arcQueue.add(firstArc);
    	        arcQueue.add(secondArc);
    	    }
    	}

    	// Process each arc in the set
    	while (!arcQueue.isEmpty()) {
    	    
    		// Remove an arc from the set
    	    Arc arcRemover = arcQueue.iterator().next();
    	    arcQueue.remove(arcRemover);

    	    // Check if the removed arc has any inconsistent values
    	    if (inconsistentValues(varDomains, arcRemover)) {
    	        
    	    	// Loop through each constraint in the 'constraints' set
    	        for (DateConstraint constraint : constraints) {
    	            
    	        	// Check if the constraint is binary
    	            if (constraint.ARITY == 2) {
    	                BinaryDateConstraint binaryConstraint = (BinaryDateConstraint) constraint;
    	                BinaryDateConstraint backwards = binaryConstraint.getReverse();

    	                // Add new arcs for processing
    	                if (binaryConstraint.L_VAL == arcRemover.TAIL) {
    	                    Arc newArc = new Arc(binaryConstraint.R_VAL, arcRemover.TAIL, backwards);
    	                    arcQueue.add(newArc);
    	                }
    	                if (binaryConstraint.R_VAL == arcRemover.TAIL) {
    	                    Arc newArc = new Arc(binaryConstraint.L_VAL, arcRemover.TAIL, constraint);
    	                    arcQueue.add(newArc);
    	                }
    	            }
    	        }
    	    }
    	}
    }
    
    /**
     * Private helper class organizing Arcs as defined by the AC-3 algorithm, useful for implementing the
     * arcConsistency method.
     * [!] You may modify this class however you'd like, its basis is just a suggestion that will indeed work.
     */
    private static class Arc {
        
        public final DateConstraint CONSTRAINT;
        public final int TAIL, HEAD;
        
        /**
         * Constructs a new Arc (tail -> head) where head and tail are the meeting indexes
         * corresponding with Meeting variables and their associated domains.
         * @param tail Meeting index of the tail
         * @param head Meeting index of the head
         * @param c Constraint represented by this Arc.
         * [!] WARNING: A DateConstraint's isSatisfiedBy method is parameterized as:
         * isSatisfiedBy (LocalDate leftDate, LocalDate rightDate), meaning L_VAL for the first
         * parameter and R_VAL for the second. Be careful with this when creating Arcs that reverse
         * direction. You may find the BinaryDateConstraint's getReverse method useful here.
         */
        public Arc (int tail, int head, DateConstraint c) {
            this.TAIL = tail;
            this.HEAD = head;
            this.CONSTRAINT = c;
        }
        
        @Override
        public boolean equals (Object other) {
            if (this == other) { return true; }
            if (this.getClass() != other.getClass()) { return false; }
            Arc otherArc = (Arc) other;
            return this.TAIL == otherArc.TAIL && this.HEAD == otherArc.HEAD && this.CONSTRAINT.equals(otherArc.CONSTRAINT);
        }
        
        @Override
        public int hashCode () {
            return Objects.hash(this.TAIL, this.HEAD, this.CONSTRAINT);
        }
        
        @Override
        public String toString () {
            return "(" + this.TAIL + " -> " + this.HEAD + ")";
        }
        
    }
    
    
    
}
