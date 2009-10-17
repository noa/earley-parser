package cs465;

import cs465.Chart;

// non-stochastic CKY parser
public class CKYParser extends Parser {
	Chart chart = null;
	
	public boolean recognize(Grammar gr, String[] sent) {
		int n = sent.length;
		int r = gr.num_nonterminals();
		chart = new Chart(sent.length);
		
		// for each word in the sentence
		for(int j = 1; j < sent.length; j++) {
			// assign all possible pre-terminals for this word to the diagonal entry (j-1,j)
			chart.initialize(j-1, j, gr.parents(sent[j]));
		}
		
		return false;
	}
	
	public Tree parse(Grammar gr, String[] sent) {
		int n = sent.length;
		int r = gr.num_nonterminals();
		chart = new Chart(sent.length);
		
		return new Tree();
	}
}