package cs465;

public class Rule {
	public Double ruleWeight;
	public String[] symbols;
	
	public Rule(String[] tokens) {
		// assume first token is the weight
		ruleWeight = new Double(tokens[0]);
		symbols = new String[tokens.length-1];  
		for(int i = 1; i < tokens.length; i++) {
			symbols[i-1] = tokens[i]; 
		}
	}
	
	//TODO: remove unused constructor?
	public Rule(String rule_text) {
		// assume first token is the weight
		String [] tokens = rule_text.split("\\s+");
		ruleWeight = new Double(tokens[0]);
		symbols = new String[tokens.length-1];  
		for(int i = 1; i < tokens.length -1 ; i++) {
			symbols[i-1] = tokens[i]; 
		}
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
		int i;
	    for(i=1; i<symbols.length; i++) {
	        sb.append(symbols[i]);
	        sb.append(" ");
	    }
	    return sb.toString();
	}
}