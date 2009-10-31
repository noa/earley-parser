package cs465;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cs465.util.LinkedListNode;
import cs465.util.Logger;
import cs465.util.OurLinkedList;
import cs465.util.Pair;

//TODO: consider a better way of constructing DottedRules
public class EarleyParser extends Parser {
	private Chart chart;
	private Grammar grammar;
	
	public EarleyParser(Grammar grammar) {
		this.grammar = grammar;
		chart = new Chart();
	}

	@Override
	public Tree parse(String[] sent) {
		
		// if this sentence is grammatical
		if(recognize(sent) == true) {
			// recover the lowest weight parse from backpointers
			// Fill lowestDr with a dummy DottedRule
			DottedRule lowestDr = new DottedRule(null, 0, 0, Double.MAX_VALUE);
			for (DottedRule dr : chart.getColumn(chart.getNumColumns()-1)) {
				if(dr.rule.get_lhs().equals(Grammar.ROOT) && dr.complete()) {
					if (dr.treeWeight < lowestDr.treeWeight) {
						lowestDr = dr;
					}
					if (Logger.isDebugMode()) {
						Logger.println(new Tree(dr).toString());
					}
				}
			}
			// recognize() == true ensures that lowestDr will not be the dummy dotted rule.
			return new Tree(lowestDr);
		}
		
		return null;
	}

	@Override
	public boolean recognize(String[] sent) {

		chart.initialize(grammar, sent.length);
		fill_chart(sent);
		
		// if the special rule exists in the last chart column with a dot at the end, this sentence is grammatical
		for (DottedRule dr : chart.getColumn(chart.getNumColumns()-1)) {
			if(dr.rule.get_lhs().equals(Grammar.ROOT) && dr.complete()) {
				return true;
			}
		}
		return false;
	}
	
	private void fill_chart(String[] sent) {
		
		/* DEBUG */
		Logger.println("Prefix table:");
		for(Pair<String,String> key : grammar.prefix_table.keySet()) {
			Logger.print("R("+key.get1()+","+key.get2()+")={");
			for(Rule r : grammar.prefix_table.get(key)) {
				Logger.print(r.toString()+", ");
			}
			Logger.println("}");
		}
		Logger.println("\nLeft parent table:");
		for(String key : grammar.left_parent_table.keySet()) {
			Logger.print("P("+key+")={");
			for(String parent : grammar.left_parent_table.get(key)) {
				Logger.print(parent+" ");
			}
			Logger.println("}");
		}
		/* DEBUG */
		
		// For each chart column (sent.length + 1)
		for(int i=0; i<chart.getNumColumns(); i++) {
			Logger.println("Processing column: " + i);
			OurLinkedList<DottedRule> column = chart.getColumn(i);
			LinkedListNode<DottedRule> entry = column.getFirst();
			HashSet<String> columnPredictions = new HashSet<String>();
			HashMap<DottedRule,DottedRule> columnAttachments = new HashMap<DottedRule,DottedRule>();

			HashMap<String,HashSet<String>> left_ancestor_pair_table = null;
						
			// there is no word corresponding to the first column of the chart
			if(i < sent.length ) {
				left_ancestor_pair_table = create_ancestor_pair_table(grammar,sent[i]);
				
				/* DEBUG */
				for(String key : left_ancestor_pair_table.keySet()) {
					Logger.print("A_" + i + "(" + key + ")={");
					for(String ancestor : left_ancestor_pair_table.get(key) )
						Logger.print(ancestor + ", ");
					Logger.println("}");
				}
				/* DEBUG */
			}

			// while there are states in the linked list
			while( entry != null) {
				DottedRule state = entry.getValue();
				Logger.print("Column " + i + ": State: " + state);
				if (state.complete()) {
					// e.g. S -> NP VP .
					Logger.println(" Action: attach");
					attach(state, i, columnAttachments);
				} else if (grammar.is_nonterminal(state.symbol_after_dot())) {
					// e.g. S -> . NP VP
					Logger.println(" Action: predict");
					predict(state, i, columnPredictions, left_ancestor_pair_table);
				} else {
					// e.g. NP -> . Det N     (pre-terminal after dot)
					//  or
					// e.g. NP -> NP . and NP (terminal after dot) 
					Logger.println(" Action: scan");
					scan(state, sent, i);
				}
				
				entry = entry.getNext();
			}
			
		}
	}
	
	// create ancestor pair table, starting from word Y in the sentence
	private HashMap<String,HashSet<String>> create_ancestor_pair_table(Grammar grammar, String Y) {
		//TODO: consider not using a HashSet?
		HashMap<String,HashSet<String>> ancestors = new HashMap<String,HashSet<String>>();
		HashSet<String> processed_symbols = new HashSet<String>();
		
		// DFS
		process_Y(grammar,ancestors,processed_symbols,Y);
		
		return ancestors;
	}

	// recursively populate left ancestor pair table 
	private void process_Y(Grammar grammar, HashMap<String,HashSet<String>> ancestors, HashSet<String> processed_symbols, String Y) {
		processed_symbols.add(Y); // don't process any symbol more than once
		HashSet<String> parents = grammar.left_parent_table.get(Y);

		if(parents != null) {
			// for each parent X of Y
			for(String X : parents) {
				
				// either create the hash of ancestors for this symbol or add to it
				if(ancestors.containsKey(X)) {
					ancestors.get(X).add(Y);
				} else {
					HashSet<String> ancestors_of_X = new HashSet<String>();
					ancestors_of_X.add(Y);
					ancestors.put(X, ancestors_of_X);
				}
				
				if(!processed_symbols.contains(X)) {
					process_Y(grammar,ancestors,processed_symbols,X);
				}
			}
		}
	}

	// don't need to store back-pointers for predictions
	private void predict(DottedRule state, int column, HashSet<String> columnPredictions, HashMap<String,HashSet<String>> left_ancestor_pair_table) {
		String predictedSymbol = state.symbol_after_dot();
		if (columnPredictions.contains(predictedSymbol)) { // don't predict the same symbol twice
			return;
		}
		
		columnPredictions.add(predictedSymbol);
		
		// constrain predictions using the left ancestor pair table
		if(left_ancestor_pair_table != null) {
			if(left_ancestor_pair_table.containsKey(predictedSymbol)) {
				for(String B : left_ancestor_pair_table.get(predictedSymbol)) {
					Pair<String,String> key = new Pair<String,String>(predictedSymbol,B);
					for(Rule r : grammar.prefix_table.get(key)) {
						chart.enqueue(new DottedRule(r,0,column, r.ruleWeight),column);
						Logger.println("Predicting a new rule.");
					}
				}
				left_ancestor_pair_table.put(predictedSymbol, null);
			}
		} else { // first column of the chart (no string in sentence)
			for(Rule r : grammar.get_rule_by_lhs(predictedSymbol)) {
				chart.enqueue(new DottedRule(r,0,column, r.ruleWeight),column);
				Logger.println("Column " + column + ": Predicting a new rule.");
			}
		}
	}
	
	private void scan(DottedRule state, String[] sent, int column) {
		// if the symbol after the dot expands to the current word in the sentence
		// Only scan if there is text remaining in the sentence
		if(column < sent.length && sent[column].equals(state.symbol_after_dot())) {
			// Only change the position of the dot
			DottedRule scanned_rule = new DottedRule(state.rule,state.dot+1,state.start, state.treeWeight);
			// TODO ?does this work for the rule (NP -> NP and . NP, i)
			scanned_rule.completed_rule = null;
			scanned_rule.attachee_rule  = state; // NP -> NP . and NP
			chart.enqueue(scanned_rule,column+1);
			Logger.println("Column " + column + ": Adding new rule for successful scan");
		}
	}
	
	// attach completed constituent to customers
	private void attach(DottedRule state, int column, HashMap<DottedRule,DottedRule> columnAttachments) {

		ArrayList<DottedRule> attachableRules = chart.getAttachableRules(state);
		
		if (attachableRules != null) {
			for(DottedRule r : attachableRules) {
				DottedRule new_rule = new DottedRule(r.rule,r.dot+1,r.start, state.treeWeight + r.treeWeight);
				new_rule.completed_rule = state;    // e.g. VP -> V .
				new_rule.attachee_rule  = r;	      // e.g. S  -> NP . VP
				
				DottedRule existingRule = columnAttachments.get(new_rule);
				if (existingRule != null) {
					// TODO: remove this debug code and switch back to HashSet
					if (existingRule.treeWeight > new_rule.treeWeight) {
						//Logger.println("Not adding equivalent higher weight rule: " + new_rule);
						// TODO: figure out what to do with this bug
						existingRule.treeWeight = new_rule.treeWeight;
						existingRule.completed_rule = new_rule.completed_rule;
						existingRule.attachee_rule = new_rule.attachee_rule;
					} else {
						// Either the new_rule has higher weight or we've hit the lower weight completed
						// consistuent bug.
						// TODO: resolve this for extra credit
						
					}
					continue;
				}
				
				columnAttachments.put(new_rule, new_rule);
				chart.enqueue(new_rule, column);
				Logger.printf("Column " + column + ": Attaching new_rule=%s completed_rule=%s attachee_rule=%s\n", new_rule, state, r);
				Logger.printf("Attaching new_rule=%s completed_rule=%s attachee_rule=%s\n", new_rule, state, r);
			}
		}
	}

}