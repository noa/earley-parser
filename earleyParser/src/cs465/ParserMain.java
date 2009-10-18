package cs465;

import java.io.*;
import java.util.*;

// driver
public class ParserMain {
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage:\n\tjava cs465.ParserMain foo.gr foo.sen");
			System.exit(1);
		}
		// initialize grammar
		Grammar grammar = new Grammar(args[0]);
		// read sentences to parse
		ArrayList<String> sents = read_sents(args[1]);
		// read in sentences to parse
		CKYParser parser = new CKYParser();
		recognize_sents(grammar,parser,sents);
	}
	public static void recognize_sents(Grammar g, Parser p, ArrayList<String> sents) {
		for(String sent : sents) {
			boolean grammatical = p.recognize(g, sent.split("\\s+"));
			System.out.println("Grammatical = " + grammatical + ":\n\t" + sent);
		}
	}
	public static void parse_sents(Grammar g, Parser p, String[] sents) {
		for(String sent : sents) {
			Tree tree = p.parse(g, sent.split("\\s+"));
			tree.print(); // if no parse, return empty tree
		}
	}
	public static ArrayList<String> read_sents(String sent_file) {
		ArrayList<String> sents = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(sent_file));
			if (!in.ready())
				throw new IOException();
			String line;
			while ((line = in.readLine()) != null) {
				sents.add(line);
			}
			in.close();
		}
		catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		return sents;
	}
}