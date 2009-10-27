package cs465;

// note: used with Earley parsers
// note: might want to represent dotted rules as just lists of elements that have not yet been matched
public class DottedRule {
	int start;
	int dot;
	Rule rule = null;
	DottedRule completed_rule = null;
	DottedRule previous_rule = null;
	String scan = null;
	public DottedRule(Rule rule, Integer dot, Integer start) {
		this.dot = dot;
		this.rule = rule;
		this.start = start;
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
}