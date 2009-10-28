package cs465;

import java.io.*;
import java.util.*;

// driver
public class ParserMain {
	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			System.err.println("Usage:\n\tjava cs465.ParserMain foo.gr foo.sen (parse|recognize)");
			System.exit(1);
		}
		// initialize grammar
		Grammar grammar = new Grammar(args[0]);
		// read sentences to parse
		ArrayList<String> sents = read_sents(args[1]);
		// read mode
		String mode = args[2];
		// read in sentences to parse
		Parser parser = new EarleyParser(grammar);
		
		if(mode.equals("recognize")) {
			recognize_sents(parser,sents);
		}
		else if (mode.equals("parse")) {
			parse_sents(parser,sents);
		}
	}
	public static void recognize_sents(Parser p, ArrayList<String> sents) {
		for(String sent : sents) {
			boolean grammatical = p.recognize(sent.split("\\s+"));
			System.out.println("Grammatical = " + grammatical + ":\n\t" + sent);
		}
	}
	public static void parse_sents(Parser p, ArrayList<String> sents) {
		for(String sent : sents) {
			Tree tree = p.parse(sent.split("\\s+"));
			if(tree != null) {
				System.err.println();
				tree.print();
				System.err.println(sent);
			} else {
				System.err.println("Not grammatical:" + sent);
			}
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