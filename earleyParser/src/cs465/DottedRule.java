package cs465;

// note: used with Earley parsers
// note: might want to represent dotted rules as just lists of elements that have not yet been matched
public class DottedRule {
	public short dot;
	public Rule rule = null;
	public DottedRule(short dot, Rule rule) {
		this.dot = dot;
		this.rule = rule;
	}
}