package cs465;

// these are returned by various parsers
public class Tree {
	double likelihood;
	DottedRule root = null;
	public Tree(DottedRule dr) {
		root = dr;
	}
	// backpointers must be enough to extract tree
	public static void print_entry(DottedRule dr) {
		// how about rules like:
		//	NP -> NP and NP
		// with embedded terminals?
		if(dr != null) {
			if(dr.complete()) {	              // S -> NP VP.
				System.out.print("(" + dr.rule.get_lhs() + " ");
			}
		
			print_entry(dr.attachee_rule);
			
			if(dr.complete_rule == null && dr.attachee_rule != null) {
				System.out.print(dr.attachee_rule.symbol_after_dot());
			}
			
			print_entry(dr.complete_rule);
			
			if(dr.complete()) {
				System.out.print(")");
			}
		}
	}
	void print() {
		print_entry(root);
		System.out.println();
	}
}