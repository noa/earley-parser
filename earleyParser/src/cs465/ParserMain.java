package cs465;

import java.io.IOException;

public class ParserMain {
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage:\n\tjava cs465.Parser foo.gr foo.sen");
		}
		// initialize grammar
		Grammar grammar = new Grammar(args[1]);
		// read in sentences to parse
		CKYParser parser = new CKYParser();
		Tree parse = parser.parse(grammar, args[2].split(" "));
		parse.print();
	}
	
}