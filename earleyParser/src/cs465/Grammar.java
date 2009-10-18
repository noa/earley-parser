package cs465;

import java.io.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/* Read a .gr grammar file and hash its contents like:
 * 
 * Key       Value
 * ----------------------
 * String -> List<Rule>
 
 */
public class Grammar {
	HashMap<String,ArrayList<Rule>> map = new HashMap<String,ArrayList<Rule>>();
	
	// For each symbol B in the grammar, what are the symbols A such that 
	//   A -> * B *
	// where * stands for zero or more other symbols
	HashMap<String,Set<String>> parents = new HashMap<String,Set<String>>();
	
	public Grammar(String file_name) {
		read_grammar(file_name);
	}
	
	public void read_grammar(String file_name) {
		System.err.println("Loading grammar: " + file_name);
		File f = new File(file_name);
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = bis.readLine()) != null) {
				System.err.println(line);
				String[] tokens = line.split("\\s+");
				if(tokens.length > 0) {
					
					// add symbol expansion
					String key = make_key(tokens);
					ArrayList<Rule> rules = map.containsKey(key) ? (ArrayList<Rule>) map.get(key) : new ArrayList<Rule>();
					rules.add(new Rule(tokens));
					map.put(key,rules);
					
					// add symbol parents
					for(int i=2;i<tokens.length;i++) {
						Set<String> curr_parents = parents.containsKey(tokens[i]) ? (Set<String>) parents.get(tokens[i]) : new HashSet<String>();
						curr_parents.add(tokens[1]);
						parents.put(tokens[i], curr_parents);
					}
				}
				else {
					System.err.println("WARNING: empty rule in grammar file");
				}
			}			
		} catch (IOException e) {
			System.err.println("Problem reading grammar file:" + file_name);
		}
	}
	public Set<String> rules_with_rhs(Set<String> B,Set<String> C) {
		Set<String> lhs = new HashSet<String>();
		for(String b : B) {
			for(String c : C) {
				for(String key : this.map.keySet()) {
					for(Rule r : this.map.get(key)) {
						String[] rhs = r.get_rhs();
						if(rhs != null && rhs.length > 1) {
							if(rhs[0].equals(b) && rhs[1].equals(c)) {
								lhs.add(r.get_lhs());
							}
						}
					}
				}
			}
		}
		return lhs;
	}
	public Set<String> parents(String token) {
		return this.parents.get(token);
	}
	public Integer num_nonterminals() {
		return this.map.keySet().size();
	}
	public Set<String> get_nonterminals() {
		return this.map.keySet();
	}
	private String make_key(String[] tokens) {
		return tokens[1];
	}
}