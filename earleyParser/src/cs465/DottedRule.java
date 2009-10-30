package cs465;

import java.util.Arrays;

// note: used with Earley parsers
// note: might want to represent dotted rules as just lists of elements that have not yet been matched
public class DottedRule {
	int start;
	int dot;
	Rule rule = null;
	double treeWeight;
	// Backpointers
	DottedRule completed_rule = null;
	DottedRule attachee_rule = null;
	//String scan = null;
	
	public DottedRule(Rule rule, Integer dot, Integer start, double treeWeight) {
		this.dot = dot;
		this.rule = rule;
		this.start = start;
		this.treeWeight = treeWeight;
	}
	
	public String symbol_after_dot() {
		return rule.symbols[dot+1];
	}
	
	public boolean complete() {
		// example: S NP VP .
		return dot >= rule.symbols.length - 1;
	}
	
	public Tree toTree() {
		return new Tree(this);
	}
	
	@Override 
	public String toString() { 
	    StringBuilder sb = new StringBuilder();
	    sb.append(start);
	    sb.append(" ");
	    sb.append(rule.symbols[0]);
	    sb.append(" --> ");
	    // Append RHS
	    int i;
	    for(i=1; i<rule.symbols.length; i++) {
	        if (dot+1 == i) {
	            sb.append(". ");
	        }
	        sb.append(rule.symbols[i]);
	        sb.append(" ");
	    }
	    if (dot+1 == i) {
	        sb.append(". ");
	    }
		return sb.toString();
	}

	/**
	 * Note that we define a loose definition of hashCode that only
	 * considers start, dot, and rule.symbols.
	 * TODO: does this cause any other problems?
	 */
	@Override
	public int hashCode() {
		 int result = 17;
         result = 37*result + start;
         result = 37*result + dot;
         result = 37*result + (rule == null ? 0 : Arrays.deepHashCode(rule.symbols));
         return result;
	}
	
	/**
	 * Note that we define a loose definition of equals that only
	 * considers start, dot, and rule.symbols.
	 * TODO: does this cause any other problems?
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof DottedRule) {
			DottedRule other = (DottedRule)o;
			if (start == other.start &&
				dot == other.dot &&
				(rule == other.rule || Arrays.deepEquals(rule.symbols, other.rule.symbols))) {
				return true;
			}
		}
		return false;
	}
}