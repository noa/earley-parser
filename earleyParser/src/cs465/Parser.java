package cs465;

import cs465.Tree;
import cs465.Grammar;

public abstract class Parser {
	public abstract Tree parse(Grammar grammar, String[] sent);
	public abstract boolean recognize(Grammar grammar, String[] sent);
}
