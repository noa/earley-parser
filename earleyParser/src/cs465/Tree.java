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
			if(dr.complete()) {
				System.out.print("(" + dr.rule.get_lhs() + " ");
			}
			if(dr.scan != null) {
				System.out.print(dr.scan);

			}
			print_entry(dr.previous_rule);
			print_entry(dr.completed_rule);
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