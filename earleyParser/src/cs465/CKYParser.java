package cs465;

// non-stochastic CKY parser
public class CKYParser extends Parser {
	boolean P[][][]; // chart
	
	public boolean recognize(Grammar gr, String[] sent) {
		int n = sent.length;
		int r = gr.num_nonterminals();
		this.init_chart(false, n, r);
		
		return false;
	}
	
	public Tree parse(Grammar gr, String[] sent) {
		int n = sent.length;
		int r = gr.num_nonterminals();
		this.init_chart(false, n, r);
		
		
		return new Tree();
	}
	
	private void init_chart(boolean value, int n, int r) {
		this.P = new boolean[n][n][r];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				for(int k = 0; k < r; k++) {
					P[i][j][k] = value;
				}
			}
		}
	}
}