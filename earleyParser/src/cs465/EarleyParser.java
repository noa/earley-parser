package cs465;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cs465.util.LinkedListNode;
import cs465.util.Logger;
import cs465.util.OurLinkedList;

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
		
		// For each chart column (sent.length + 1)
		for(int i=0; i<chart.getNumColumns(); i++) {
			Logger.println("Processing column: " + i);
			OurLinkedList<DottedRule> column = chart.getColumn(i);
			LinkedListNode<DottedRule> entry = column.getFirst();
			HashSet<String> columnPredictions = new HashSet<String>();
			HashMap<DottedRule,DottedRule> columnAttachments = new HashMap<DottedRule,DottedRule>();
			
			while( entry != null) {
				DottedRule state = entry.getValue();
				Logger.print("State: " + state);
				if (state.complete()) {
					// e.g. S -> NP VP .
					Logger.println(" Action: attach");
					attach(state, i, columnAttachments);
				} else if (grammar.is_nonterminal(state.symbol_after_dot())) {
					// e.g. S -> . NP VP
					Logger.println(" Action: predict");
					predict(state, i, columnPredictions);
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
	
	// don't need to store backpointers for predictions
	private void predict(DottedRule state, int column, HashSet<String> columnPredictions) {
		String symbolAfterDot = state.symbol_after_dot();
		if (columnPredictions.contains(symbolAfterDot)) {
			return;
		}
		
		columnPredictions.add(symbolAfterDot);
		for(Rule r : grammar.get_rule_by_lhs(symbolAfterDot)) {
			chart.enqueue(new DottedRule(r,0,column, r.ruleWeight),column);
			Logger.println("Predicting a new rule.");
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
			Logger.println("Adding new rule for successful scan");
		}
	}
	
	private void attach(DottedRule state, int column, HashMap<DottedRule,DottedRule> columnAttachments) {

		ArrayList<DottedRule> attachableRules = chart.getAttachableRules(state);
		
		for(DottedRule r : attachableRules) {
			DottedRule new_rule = new DottedRule(r.rule,r.dot+1,r.start, state.treeWeight + r.treeWeight);
			new_rule.completed_rule = state;    // e.g. VP -> V .
			new_rule.attachee_rule  = r;	      // e.g. S  -> NP . VP
			
			if (columnAttachments.containsKey(new_rule)) {
				// TODO: remove this debug code and switch back to HashSet
				DottedRule existingRule = columnAttachments.get(new_rule);
				if (existingRule.treeWeight <= new_rule.treeWeight) {
					//Logger.println("Not adding equivalent higher weight rule: " + new_rule);
					continue;
				}
			}
			
			columnAttachments.put(new_rule, new_rule);
			chart.enqueue(new_rule, column);
			Logger.printf("Attaching new_rule=%s completed_rule=%s attachee_rule=%s\n", new_rule, state, r);
		}
	}

}