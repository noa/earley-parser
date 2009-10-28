package cs465;

// these are returned by various parsers
// TODO: consider pushing these methods in DottedRule
public class Tree {
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
				// TODO: add a log method
				System.out.printf("%.1f ", dr.treeWeight);
			}
		
			print_entry(dr.attachee_rule);
			
			if(dr.completed_rule == null && dr.attachee_rule != null) {
				System.out.print(dr.attachee_rule.symbol_after_dot());
			}
			
			print_entry(dr.completed_rule);
			
			if(dr.complete()) {
				System.out.print(")");
			}
		}
	}
	void print() {
		System.out.println("treeWeight = " + root.treeWeight);		
		print_entry(root);
		System.out.println();
	}
}