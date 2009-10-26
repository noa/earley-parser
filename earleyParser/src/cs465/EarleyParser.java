package cs465;

import java.util.ArrayList;
import java.util.HashSet;

import cs465.util.LinkedListNode;
import cs465.util.OurLinkedList;

public class EarleyParser extends Parser {
	//TODO: maybe remove usage of state.stop 
	
	// I think LinkedHashSet does what we want (preserves insertion-order and uniqueness),
	// but does it do so efficiently?  I guess we'll find out...
	ArrayList<OurLinkedList<DottedRule>> chart = null;
	
	@Override
	public Tree parse(Grammar grammar, String[] sent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean recognize(Grammar grammar, String[] sent) {

		// Initialize the chart
		initialize_chart(grammar,sent.length);
		
		// For each chart column (sent.length + 1)
		for(int i=0; i<chart.size(); i++) {
			
			// NOTE: does this work?  can you grow a list while Iterating through it?
			
			OurLinkedList<DottedRule> column = chart.get(i);
			LinkedListNode<DottedRule> entry = column.getFirst();
			HashSet<String> columnPredictions = new HashSet<String>();
			while( entry != null) {
				DottedRule state = entry.getValue();
				System.err.print("State: " + state);
				if (state.complete()) {
					System.err.println(" Action: attach");
					attach(state, grammar, i);
				} else if (grammar.is_nonterminal(state.symbol_after_dot())) {
					System.err.println(" Action: predict");
					predict(state, grammar, i, columnPredictions);
				} else {
					System.err.println(" Action: scan");
					scan(state, grammar, sent, i);
				}
				
				entry = entry.getNext();
			}
			
		}
		
		
		// if the special rule exists in the last chart column with a dot at the end, this sentence is grammatical
		for (DottedRule dr : chart.get(chart.size()-1)) {
			if(dr.rule.get_lhs().equals(Grammar.ROOT) && dr.complete()) {
				return true;
			}
		}
		return false;
	}
	
	private void predict(DottedRule state, Grammar grammar, int column, HashSet<String> columnPredictions) {
		String symbolAfterDot = state.symbol_after_dot();
		if (columnPredictions.contains(symbolAfterDot)) {
			return;
		}
		
		columnPredictions.add(symbolAfterDot);
		for(Rule r : grammar.rewrites(symbolAfterDot)) {
			enqueue(new DottedRule(r,0,column),column);
		}
	}
	
	private void scan(DottedRule state, Grammar grammar, String[] sent, int column) {
		// if the symbol after the dot expands to the current word in the sentence
		
		if(sent[column].equals(state.symbol_after_dot())) {
			enqueue(new DottedRule(state.rule,state.dot+1,state.start),column+1);
		}
		
		/*
		if(grammar.parents(sent[column]).contains(state.symbol_after_dot())) {
			for(Rule r : grammar.rewrites(state.symbol_after_dot())) {
				if(r.get_rhs()[0] == sent[state.stop]) {
					Enqueue(new DottedRule(r,0,state.stop,state.stop+1),state.stop+1);
					return;
				}
			}
		}
		*/
	}
	
	private void attach(DottedRule state, Grammar grammar, int column) {
		// for all states in chart[state.start] expecting a completed state ending at state.stop, advance them to chart[state.stop] with dot+=1
		for(DottedRule r : chart.get(state.start)) {
			if(r.symbol_after_dot() == state.rule.get_lhs()) {
				enqueue(new DottedRule(r.rule,r.dot+1,r.start), column);
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
				Rule r = grammar.get_start_rule();
				DottedRule start = new DottedRule(r,0,0);
				column.add(start);
			}
			chart.add(column);
		}
	}
}