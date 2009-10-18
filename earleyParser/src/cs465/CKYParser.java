package cs465;

import cs465.Chart;
import java.util.Set;

// non-stochastic CKY parser
public class CKYParser extends Parser {
	Chart chart = null;
	
	@Override
	public boolean recognize(Grammar gr, String[] sent) {
		chart = new Chart(sent.length);
		
		// for each word in the sentence
		for(int j = 1; j < sent.length; j++) {
			// assign all possible pre-terminals for this word to the diagonal entry (j-1,j)
			chart.initialize_cell(j-1, j, gr.parents(sent[j]));
			// look for things to add to cell (i,j)
			for(int i = j-2; i > -1; i--) {
				for(int k = i+1; k<j; k++) {
					Set<String> B = chart.get_cell(i, k);
					Set<String> C = chart.get_cell(k, j);
					chart.add_to_cell(i, j, gr.rules_with_rhs(B,C));
				}
			}
		}
				
		return false;
	}
	
	@Override
	public Tree parse(Grammar gr, String[] sent) {
		chart = new Chart(sent.length);
		return new Tree();
	}
}