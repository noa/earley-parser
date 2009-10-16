package cs465;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

/* Read a .gr grammar file and hash its contents like:
 * 
 * Key       Value
 * ----------------------
 * String -> List<Rule>
 
 */
public class Grammar {
	HashMap<String,ArrayList<Rule>> map = new HashMap<String,ArrayList<Rule>>();
	
	public Grammar(String file_name) {
		read_grammar(file_name);
	}
	public void read_grammar(String file_name) {
		File f = new File(file_name);
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = bis.readLine()) != null) {
				System.out.println(line);
				String[] tokens = line.split(" ");
				if(tokens.length > 0) {
					String key = make_key(tokens);
					if(map.containsKey(key)) {
						// there is already one or more rewrite rules for this symbol, so:
						// 1. fetch the current rules
						ArrayList<Rule> rules = (ArrayList<Rule>) map.get(key);
						// 2. add new rule to dictionary
						rules.add(new Rule(tokens));
						// 3. update hash
						map.put(key,rules);
					}
				}
			}
			
		} catch (IOException e) {
			System.err.println("Problem reading grammar file:" + file_name);
		}
	}
	public Integer num_nonterminals() {
		return map.keySet().size();
	}
	private String make_key(String[] tokens) {
		return tokens[1];
	}
}