package cs465;


public abstract class Parser {
	public abstract Tree parse(String[] sent);
	public abstract boolean recognize(String[] sent);
}
