package cs465;

import java.util.Arrays;

public class Rule {
	public Double ruleWeight;
	public String[] symbols;	// symbols appearing in the rule, including the left hand side
	public int symbolsHashCode;	// pre-computed hash code for quick comparison
	
	public Rule(String[] tokens) {
		// assume first token is the weight
		ruleWeight = new Double(tokens[0]);
		symbols = new String[tokens.length-1];  
		for(int i = 1; i < tokens.length; i++) {
			symbols[i-1] = tokens[i]; 
		}
		symbolsHashCode = Arrays.deepHashCode(symbols);
	}
	
	//TODO: probably don't want to be creating new lists every time this is called
	public String[] get_rhs() {
		String[] rhs = new String[symbols.length-1];
		for(int i=1; i<symbols.length; i++) {
			rhs[i-1] = symbols[i];
		}
		return rhs;
	}
	
	public String get_lhs() {
		return symbols[0];
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(symbols[0]+"->");
		int i;
	    for(i=1; i<symbols.length; i++) {
	        sb.append(symbols[i]);
	        sb.append(" ");
	    }
	    return sb.toString();
	}
}