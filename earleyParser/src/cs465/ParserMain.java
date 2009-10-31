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
			System.err.println("Usage:\n\tjava cs465.ParserMain foo.gr foo.sen [-debug] ");
			System.exit(1);
		}
		
		// Read optional arguments
		if (args.length > 2 && args[2].equals("-debug")) {
			Logger.setDebugMode(true);
			Logger.println("Debug Mode = true");
		}
		
		// initialize grammar
		Grammar grammar = new Grammar(args[0]);
		Logger.println("Grammar loaded successfully.");
		// read sentences to parse
		ArrayList<String> sents = read_sents(args[1]);
		
		// read in sentences to parse
		parse_sents(grammar,sents);
	}
	
	// return parse trees or NONE for each sentence
	public static void parse_sents(Grammar grammar, ArrayList<String> sents) {
		
		for(String sent : sents) {
			Parser parser = new EarleyParser(grammar);
			Tree tree = parser.parse(sent.split("\\s+"));
			
			Logger.println("");
			Logger.println(sent);
			System.out.println(sent);
			if(tree != null) {
				System.out.println(tree.toString());
			} else {
				// According to spec of hw3
				System.out.println("NONE");
			}
		}
	}
	
	// read sentences in the supplies .sen file
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