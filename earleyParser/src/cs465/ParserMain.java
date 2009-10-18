package cs465;

import java.io.IOException;

// driver
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
	public void recognize_sents(Grammar g, Parser p, String[] sents) {
		for(String sent : sents) {
			boolean grammatical = p.recognize(g, sent.split(" "));
			System.out.println(grammatical);
		}
	}
	public void parse_sents(Grammar g, Parser p, String[] sents) {
		for(String sent : sents) {
			Tree tree = p.parse(g, sent.split(" "));
			tree.print(); // if no parse, return empty tree
		}
	}
}