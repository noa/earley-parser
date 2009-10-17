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
	HashMap<String,Set<String>> parents = new HashMap<String,Set<String>>();
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