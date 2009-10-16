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
}