package cs465;

// note: used with Earley parsers
// note: might want to represent dotted rules as just lists of elements that have not yet been matched
public class DottedRule {
	Integer start;
	Integer stop;
	Integer dot;
	Rule rule = null;
	public DottedRule(Rule rule, Integer dot, Integer start, Integer stop) {
		this.dot = dot;
		this.rule = rule;
	}
	public String symbol_after_dot() {
		return rule.symbols[dot];
	}
	public boolean incomplete() {
		return (dot < rule.symbols.length) ? true : false;
	}
}