package cs465;

import java.util.ArrayList;

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
		this.initialize_chart(grammar,sent.length);
		
		// For each chart column (sent.length + 1)
		for(int i=0; i<chart.size(); i++) {
			
			// NOTE: does this work?  can you grow a list while Iterating through it?
			
			OurLinkedList<DottedRule> column = chart.get(i);
			LinkedListNode<DottedRule> entry = column.getFirst();
			while( entry != null) {
				DottedRule state = entry.getValue();
				if      (state.incomplete() && grammar.is_nonterminal(state.symbol_after_dot())) {
					this.Predictor(state, grammar, i);
				}
				//else if (state.incomplete() && grammar.is_preterminal(state.symbol_after_dot())) {
				else if (state.incomplete() && !grammar.is_nonterminal(state.symbol_after_dot())) {
					this.Scanner(state, grammar, sent, i);
				}
				else {
					this.Completer(state, grammar, i);
				}
				entry = entry.getNext();
			}
			
		}
		
		
		// if the special rule exists in the last chart column with a dot at the end, this sentence is grammatical
		for (DottedRule dr : chart.get(chart.size()-1)) {
			if(dr.rule.get_lhs().equals(Grammar.ROOT) && !dr.incomplete()) {
				return true;
			}
		}
		return false;
	}
	
	private void Predictor(DottedRule state, Grammar grammar, int column) {
		for(Rule r : grammar.rewrites(state.symbol_after_dot())) {
			this.Enqueue(new DottedRule(r,0,column),column);
		}
	}
	
	private void Scanner(DottedRule state, Grammar grammar, String[] sent, int column) {
		// if the symbol after the dot expands to the current word in the sentence
		
		if(sent[column].equals(state.symbol_after_dot())) {
			this.Enqueue(new DottedRule(state.rule,state.dot+1,state.start),column+1);
		}
		
		/*
		if(grammar.parents(sent[column]).contains(state.symbol_after_dot())) {
			for(Rule r : grammar.rewrites(state.symbol_after_dot())) {
				if(r.get_rhs()[0] == sent[state.stop]) {
					this.Enqueue(new DottedRule(r,0,state.stop,state.stop+1),state.stop+1);
					return;
				}
			}
		}
		*/
	}
	
	private void Completer(DottedRule state, Grammar grammar, int column) {
		// for all states in chart[state.start] expecting a completed state ending at state.stop, advance them to chart[state.stop] with dot+=1
		for(DottedRule r : this.chart.get(state.start)) {
			if(r.symbol_after_dot() == state.rule.get_lhs()) {
				this.Enqueue(new DottedRule(r.rule,r.dot+1,r.start), column);
			}
		}
	}
	
	private void Enqueue(DottedRule rule, Integer column) {
		this.chart.get(column).add(rule);
	}
	
	private void initialize_chart(Grammar grammar, Integer sent_length) {
		this.chart = new ArrayList<OurLinkedList<DottedRule>>();
		for(int i=0; i<sent_length+1; i++) {
			OurLinkedList<DottedRule> column = new OurLinkedList<DottedRule>();
			if(i==0) {
				// Enqueue special start rule
				Rule r = grammar.get_start_rule();
				DottedRule start = new DottedRule(r,0,0);
				column.add(start);
			}
			this.chart.add(column);
		}
	}
}