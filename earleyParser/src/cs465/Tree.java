package cs465;

import cs465.util.Logger;

// these are returned by various parsers
// TODO: consider pushing these methods in DottedRule
public class Tree {
	DottedRule root = null;
	public Tree(DottedRule dr) {
		root = dr;
	}
	// backpointers must be enough to extract tree
	public static String dottedRuleToString(DottedRule dr) {
		StringBuilder sb = new StringBuilder();
		// how about rules like:
		//	NP -> NP and NP
		// with embedded terminals?
		if(dr != null) {
			if(dr.complete()) {	              // S -> NP VP.
				sb.append("(");
				sb.append(dr.rule.get_lhs());
				sb.append(" ");
				if (Logger.isDebugMode()) {
					sb.append(String.format("%.1f ", dr.treeWeight));
				}
			}
		
			sb.append(dottedRuleToString(dr.attachee_rule));
			
			if(dr.completed_rule == null && dr.attachee_rule != null) {
				sb.append(dr.attachee_rule.symbol_after_dot());
				sb.append(" ");
			} 
			
			sb.append(dottedRuleToString(dr.completed_rule));
			
			if(dr.complete()) {
				sb.append(")");
			}
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {	
		return dottedRuleToString(root) + "\n" + root.treeWeight;	
	}
}