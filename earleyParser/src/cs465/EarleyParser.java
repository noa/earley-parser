package cs465;

import java.util.ArrayList;

import cs465.util.LinkedListNode;
import cs465.util.Logger;
import cs465.util.OurLinkedList;
import cs465.util.Pair;

//TODO: consider a better way of constructing DottedRules
public class EarleyParser extends Parser {
	private Chart chart;
	private Grammar grammar;
	private double threshold;
	private int numAttachments;
	
	public EarleyParser(Grammar grammar) {
		this.grammar = grammar;
		numAttachments = 0;
	}

	@Override
	public Tree parse(String[] sent) {
		threshold = sent.length * 7;
		chart = new Chart(grammar, sent);
		
		// if this sentence is grammatical
		if(recognize(sent)) {
			System.out.println("# numAttachments = " + numAttachments);
			
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

	private boolean recognize(String[] sent) {
		int numPruned;
		do {
			numPruned = fill_chart(sent);
			System.out.println("# Threshold = " + threshold);  //TODO: switch to Logger
			System.out.println("# numPruned = " + numPruned); //TODO: switch to Logger
			
			// if the special rule exists in the last chart column with a dot at the end, this sentence is grammatical
			for (DottedRule dr : chart.getColumn(chart.getNumColumns()-1)) {
				if(dr.rule.get_lhs().equals(Grammar.ROOT) && dr.complete()) {
					return true;
				}
			}
			
			updateThreshold(sent);
		} while (numPruned > 0);
		return false;
	}

	private void updateThreshold(String[] sent) {
		threshold += sent.length;
	}
	
	/**
	 * Returns the number of rules that were pruned because they were over the threshold.
	 */
	private int fill_chart(String[] sent) {
		
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
		
		int numPrunedRules = 0;
		// For each chart column (sent.length + 1)
		for(int i=0; i<chart.getNumColumns(); i++) {
			Logger.println("Processing column: " + i);
			OurLinkedList<DottedRule> column = chart.getColumn(i);
			LinkedListNode<DottedRule> entry = column.getFirst();
			
			// while there are states in the linked list
			while( entry != null) {
				DottedRule state = entry.getValue();
				
				Logger.print("Column " + i + ": State: " + state);
				
				if (state.treeWeight > threshold) {
					// TODO: this might not be right: we end up processing each high
					// weight entry once per iteration until it is below the threshold
					// this seems like it might break the O(n^3) bound).
					
					// skip
					Logger.println("Action: pruning");
					numPrunedRules++;
					entry = entry.getNext();
					continue;
				}

				LinkedListNode<DottedRule> nextEntry;
				if (state.complete()) {
					// e.g. S -> NP VP .
					Logger.println(" Action: attach");
					numPrunedRules += attach(state, i);
					nextEntry = entry.getNext();
				} else if (grammar.is_nonterminal(state.symbol_after_dot())) {
					// e.g. S -> . NP VP
					Logger.println(" Action: predict");
					predict(state, i);
					// Once a predict entry has been processed we keep it in the 
					// indexed columns (making it available for attachment), but remove
					// it from the queue so that it is not reprocessed.
					nextEntry = entry.getNext();
					chart.dequeue(entry, i);
				} else {
					// e.g. NP -> . Det N     (pre-terminal after dot)
					//  or
					// e.g. NP -> NP . and NP (terminal after dot) 
					Logger.println(" Action: scan");
					scan(state, sent, i);
					// Once a predict entry has been processed we keep it in the 
					// indexed columns (making it available for attachment), but remove
					// it from the queue so that it is not reprocessed.
					nextEntry = entry.getNext();
					chart.dequeue(entry, i);
				}
				entry = nextEntry;
			}
		}
		return numPrunedRules;
	}
	
	// don't need to store back-pointers for predictions
	private void predict(DottedRule state, int column) {
		String symbolToPredict = state.symbol_after_dot();
		 // don't predict the same symbol twice
		if (chart.columnPredictions[column].contains(symbolToPredict)) {
			return;
		}
		
		chart.columnPredictions[column].add(symbolToPredict);
		
		// constrain predictions using the left ancestor pair table
		if(chart.left_ancestor_pair_tables[column] != null) {
			if(chart.left_ancestor_pair_tables[column].containsKey(symbolToPredict)) {
				for(String B : chart.left_ancestor_pair_tables[column].get(symbolToPredict)) {
					Pair<String,String> key = new Pair<String,String>(symbolToPredict,B);
					for(Rule r : grammar.prefix_table.get(key)) {
						chart.enqueue(new DottedRule(r,0,column, r.ruleWeight),column);
						Logger.println("Predicting a new rule.");
					}
				}
				chart.left_ancestor_pair_tables[column].put(symbolToPredict, null);
			}
		} else { // first column of the chart (no string in sentence)
			for(Rule r : grammar.get_rule_by_lhs(symbolToPredict)) {
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
	private int attach(DottedRule state, int column) {
		ArrayList<DottedRule> attachableRules = chart.getAttachableRules(state);
		
		int numPruned = 0;
		if (attachableRules != null) {
			for(DottedRule r : attachableRules) {
				double newWeight = state.treeWeight + r.treeWeight;
				
				if (newWeight > threshold) {
					numPruned++;
					continue;
				}
				
				DottedRule new_rule = new DottedRule(r.rule,r.dot+1,r.start, newWeight);
				new_rule.completed_rule = state;    // e.g. VP -> V .
				new_rule.attachee_rule  = r;	      // e.g. S  -> NP . VP
				
				
				numAttachments++;
				
				DottedRule existingRule = chart.columnAttachments[column].get(new_rule);
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
				
				chart.columnAttachments[column].put(new_rule, new_rule);
				chart.enqueue(new_rule, column);
				Logger.printf("Column " + column + ": Attaching new_rule=%s completed_rule=%s attachee_rule=%s\n", new_rule, state, r);
				Logger.printf("Attaching new_rule=%s completed_rule=%s attachee_rule=%s\n", new_rule, state, r);
			}
		}
		
		return numPruned;
	}

}