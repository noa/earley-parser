package cs465;

public class Rule {
	public Double weight = null;
	public String[] symbols = null;
	public Rule(String[] tokens) {
		// assume first token is the weight
		this.weight = new Double(tokens[0]);
		this.symbols = new String[tokens.length-1];  
		for(int i = 1; i < tokens.length; i++) {
			this.symbols[i] = tokens[i]; 
		}
	}
	// probably don't want to be creating new lists every time this is called
	public String[] get_rhs() {
		String[] rhs = new String[this.symbols.length-1];
		for(int i=1; i<this.symbols.length; i++) {
			rhs[i-1] = this.symbols[i];
		}
		return rhs;
	}
	public String get_lhs() {
		return this.symbols[0];
	}
}