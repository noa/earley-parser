package cs465;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cs465.util.Logger;

// driver
public class ParserMain {
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage:\n\tjava cs465.ParserMain foo.gr foo.sen [parse|recognize] [-debug] ");
			System.exit(1);
		}
		
		// Read optional arguments
		String mode = "parse";
		if (args.length > 2) {
			// read mode
			mode = args[2];
			if (args.length > 3 && args[3].equals("-debug")) {
				Logger.setDebugMode(true);
				Logger.println("Debug Mode = true");
			}
		}
		
		// initialize grammar
		Grammar grammar = new Grammar(args[0]);
		Logger.println("Grammar loaded successfully.");
		// read sentences to parse
		ArrayList<String> sents = read_sents(args[1]);

		
		// read in sentences to parse
		Parser parser = new EarleyParser(grammar);
		
		if(mode.equals("recognize")) {
			recognize_sents(parser,sents);
		}
		else if (mode.equals("parse")) {
			parse_sents(parser,sents);
		} else {
			throw new RuntimeException("Unrecognized option for (parse|recognize): " + mode);
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
			
			Logger.println("");
			Logger.println(sent);
			if(tree != null) {
				System.out.println(tree.toString());
			} else {
				// According to spec of hw3
				System.out.println("NONE");
			}
		}
	}
	public static ArrayList<String> read_sents(String sent_file) {
		ArrayList<String> sents = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(sent_file));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.matches("\\s*")) {
					// Ignore empty lines since arith.par does so.
				} else {
					sents.add(line);
				}
			}
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return sents;
	}
}