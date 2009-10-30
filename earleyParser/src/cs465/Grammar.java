package cs465;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cs465.util.Logger;

import cs465.util.Pair;

/* Read a .gr grammar file and hash its contents like:
 * 
 * Key       Value
 * ----------------------
 * String -> List<Rule>
 
 */
public class Grammar {
	static final String ROOT = "ROOT";

	HashMap<String,ArrayList<Rule>> lhs_to_rules = new HashMap<String,ArrayList<Rule>>();
	// TODO: need to index into the rules; this will duplicate them
	HashMap<Pair<String,String>,ArrayList<Rule>> prefix_table = new HashMap<Pair<String,String>,ArrayList<Rule>>();
	HashMap<String,HashSet<String>> left_parent_table = new HashMap<String,HashSet<String>>();
	
	// Preterminals are handled as a special case in the Earley parser (for efficiency)
	Set<String> preterminals = new HashSet<String>();
	Set<String> terminals = new HashSet<String>();
	
	public Grammar(String file_name) {
		read_grammar(file_name);
	}
	
	public void read_grammar(String file_name) {
		Logger.println("Loading grammar: " + file_name);
		File f = new File(file_name);
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			Set<String> symbols = new HashSet<String>();
			while ((line = bis.readLine()) != null) {
				Logger.println(line);
				String[] tokens = line.split("\\s+");
				if(tokens.length > 0) {
					// keep track of what symbols we've found
					for(int i=1;i<tokens.length;i++) {
						symbols.add(tokens[i]);
					}
					
					// add rule to hash keyed on its left hand side
					// e.g. for "A -> B C", A will be the key 
					String key = make_key(tokens);
					ArrayList<Rule> rules = lhs_to_rules.containsKey(key) ? (ArrayList<Rule>) lhs_to_rules.get(key) : new ArrayList<Rule>();
					Rule new_rule = new Rule(tokens);
					if (new_rule.symbols.length == 2 && new_rule.symbols[0].equals(new_rule.symbols[1])) {
						// filter rules like NP --> NP
					} else {
						//TODO: decide whether to filter here
					}
					rules.add(new_rule);
					lhs_to_rules.put(key,rules);
					

					String A = tokens[1]; String B = tokens[2];
					Pair<String,String> pt_key = new Pair<String,String>(A,B);
					
					// update left parent table
					if( ! prefix_table.containsKey(pt_key) ) { 
			//				|| (prefix_table.containsKey(pt_key) && prefix_table.get(pt_key).isEmpty() == true) ) {
						if( left_parent_table.containsKey(B) ) {
							left_parent_table.get(B).add(A);
						} else {
							HashSet<String> new_set = new HashSet<String>();
							new_set.add(A);
							left_parent_table.put(B, new_set);
						}
					}
					
					// update prefix table
					if(prefix_table.containsKey(pt_key) == true) {
						prefix_table.get(pt_key).add(new Rule(tokens));
					} else {
						ArrayList<Rule> new_rule_list = new ArrayList<Rule>();
						new_rule_list.add(new Rule(tokens));
						prefix_table.put(pt_key, new_rule_list);
					}
					

				}
				else {
					Logger.println("WARNING: empty rule in grammar file");
				}
			}
			
			// build set of terminals (symbols never showing up in rule left hand sides)
			for(String symbol : symbols) {
				if(!lhs_to_rules.keySet().contains(symbol)) {
					terminals.add(symbol);
				}
			}
			
			// use terminals to figure out pre-terminals (e.g., parts of speech)
			// NOTE: there is probably a nicer way to do this
			for(String lhs : lhs_to_rules.keySet()) {
				boolean preterminal = true;
				for(Rule r : lhs_to_rules.get(lhs)) {
					Set<String> rule_rhs = new HashSet<String>();
					for(String token : r.get_rhs()) {
						rule_rhs.add(token);
					}
					if(!terminals.containsAll(rule_rhs)) {
						preterminal = false;
						break; //break early if we find this ever rewrites as non-terminals
					}
				}
				if(preterminal == true) {
					preterminals.add(lhs);
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Problem reading grammar file:" + file_name, e);
		}
	}
	
	public Set<String> rules_with_rhs(Set<String> B,Set<String> C) {
		Set<String> lhs = new HashSet<String>();
		for(String b : B) {
			for(String c : C) {
				for(String key : lhs_to_rules.keySet()) {
					for(Rule r : lhs_to_rules.get(key)) {
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
	
	public Integer num_nonterminals() {
		return lhs_to_rules.keySet().size();
	}
	
	public Set<String> get_nonterminals() {
		return lhs_to_rules.keySet();
	}
	
	public boolean is_preterminal(String symbol) {
		return preterminals.contains(symbol);
	}
	
	public boolean is_nonterminal(String symbol) {
		return !terminals.contains(symbol);
	}
	
	private String make_key(String[] tokens) {
		return tokens[1];
	}
	
	public ArrayList<Rule> get_rule_by_lhs(String symbol) {
		return lhs_to_rules.get(symbol);
	}

	public ArrayList<Rule> get_start_rules() {
		ArrayList<Rule> start_rules = lhs_to_rules.get(ROOT);
		return start_rules;
	}
}