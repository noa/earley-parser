package cs465;

import java.util.ArrayList;
import java.util.HashSet;

import cs465.util.LinkedListNode;
import cs465.util.OurLinkedList;

//TODO: add a constructor which takes the grammar as an argument
//TODO: consider a better way of constructing DottedRules
public class EarleyParser extends Parser {
	ArrayList<OurLinkedList<DottedRule>> chart = null;
	
	@Override
	public Tree parse(Grammar grammar, String[] sent) {
		
		// if this sentence is grammatical
		if(recognize(grammar,sent) == true) {
			// recover the lowest weight parse from backpointers
			// Fill lowestDr with a dummy DottedRule
			DottedRule lowestDr = new DottedRule(null, 0, 0, Double.MAX_VALUE);
			for (DottedRule dr : chart.get(chart.size()-1)) {
				if(dr.rule.get_lhs().equals(Grammar.ROOT) && dr.complete() &&
						dr.treeWeight < lowestDr.treeWeight) {
					lowestDr = dr;
				}
			}
			// recognize() == true ensures that lowestDr will not be the dummy dotted rule.
			return new Tree(lowestDr);
		}
		
		return null;
	}

	@Override
	public boolean recognize(Grammar grammar, String[] sent) {

		initialize_chart(grammar,sent.length);
		fill_chart(grammar,sent);
		
		// if the special rule exists in the last chart column with a dot at the end, this sentence is grammatical
		for (DottedRule dr : chart.get(chart.size()-1)) {
			if(dr.rule.get_lhs().equals(Grammar.ROOT) && dr.complete()) {
				return true;
			}
		}
		return false;
	}
	
	private void fill_chart(Grammar grammar, String[] sent) {
		
		// For each chart column (sent.length + 1)
		for(int i=0; i<chart.size(); i++) {
			OurLinkedList<DottedRule> column = chart.get(i);
			LinkedListNode<DottedRule> entry = column.getFirst();
			HashSet<String> columnPredictions = new HashSet<String>();
			
			while( entry != null) {
				DottedRule state = entry.getValue();
				System.err.print("State: " + state);
				if (state.complete()) {
					// e.g. S -> NP VP .
					System.err.println(" Action: attach");
					attach(state, grammar, i);
				} else if (grammar.is_nonterminal(state.symbol_after_dot())) {
					// e.g. S -> . NP VP
					System.err.println(" Action: predict");
					predict(state, grammar, i, columnPredictions);
				} else {
					// e.g. NP -> . Det N     (pre-terminal after dot)
					//  or
					// e.g. NP -> NP . and NP (terminal after dot) 
					System.err.println(" Action: scan");
					scan(state, grammar, sent, i);
				}
				
				entry = entry.getNext();
			}
			
		}
	}
	
	// don't need to store backpointers for predictions
	private void predict(DottedRule state, Grammar grammar, int column, HashSet<String> columnPredictions) {
		String symbolAfterDot = state.symbol_after_dot();
		if (columnPredictions.contains(symbolAfterDot)) {
			return;
		}
		
		columnPredictions.add(symbolAfterDot);
		for(Rule r : grammar.rewrites(symbolAfterDot)) {
			enqueue(new DottedRule(r,0,column, r.ruleWeight),column);
		}
	}
	
	private void scan(DottedRule state, Grammar grammar, String[] sent, int column) {
		// if the symbol after the dot expands to the current word in the sentence
		// Only scan if there is text remaining in the sentence
		if(column < sent.length && sent[column].equals(state.symbol_after_dot())) {
			// Only change the position of the dot
			DottedRule scanned_rule = new DottedRule(state.rule,state.dot+1,state.start, state.treeWeight);
			// TODO ?does this work for the rule (NP -> NP and . NP, i)
			scanned_rule.completed_rule = null;
			scanned_rule.attachee_rule  = state; // NP -> NP . and NP
			scanned_rule.scan = sent[column];
			enqueue(scanned_rule,column+1);
		}
	}
	
	private void attach(DottedRule state, Grammar grammar, int column) {
		for(DottedRule r : chart.get(state.start)) {
			if(!r.complete() && r.symbol_after_dot().equals(state.rule.get_lhs())) { // problem
				DottedRule new_rule = new DottedRule(r.rule,r.dot+1,r.start, state.treeWeight + r.treeWeight);
				new_rule.completed_rule = state;    // e.g. VP -> V .
				new_rule.attachee_rule  = r;	      // e.g. S  -> NP . VP
				enqueue(new_rule, column);
			}
		}
	}
	
	private void enqueue(DottedRule rule, Integer column) {
		chart.get(column).add(rule);
	}
	
	private void initialize_chart(Grammar grammar, Integer sent_length) {
		chart = new ArrayList<OurLinkedList<DottedRule>>();
		for(int i=0; i<sent_length+1; i++) {
			OurLinkedList<DottedRule> column = new OurLinkedList<DottedRule>();
			if(i==0) {
				// Enqueue special start rule
				for(Rule r : grammar.get_start_rules()) {
					DottedRule start = new DottedRule(r,0,0, r.ruleWeight);
					column.add(start);
				}
			}
			chart.add(column);
		}
	}
}