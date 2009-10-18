package cs465;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Iterator;

public class EarleyParser extends Parser {

	// I think LinkedHashSet does what we want (preserves insertion-order and uniqueness),
	// but does it do so efficiently?  I guess we'll find out...
	ArrayList<LinkedHashSet<DottedRule>> chart = null;
	
	@Override
	public Tree parse(Grammar grammar, String[] sent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean recognize(Grammar grammar, String[] sent) {

		// Initialize the chart
		this.initialize_chart(sent.length);
		
		// For each chart column (sent.length + 1)
		for(int i=0; i<=sent.length; i++) {
			
			// NOTE: does this work?  can you grow a list while Iterating through it?
			Iterator<DottedRule> it = chart.get(i).iterator();
			while( it.hasNext() ) {
				DottedRule state = it.next();
				if(state.incomplete() && !grammar.is_preterminal(state.symbol_after_dot())) {
					this.Predictor(state, grammar);
				}
				else if (state.incomplete() && grammar.is_preterminal(state.symbol_after_dot())) {
					this.Scanner(state, grammar, sent);
				}
				else {
					this.Completer(state, grammar);
				}
			}
			
		}
		
		// if the special rule exists in the last chart column with a dot at the end, this sentence is grammatical
		for (DottedRule dr : chart.get(chart.size()-1)) {
			if(dr.rule.get_lhs() == "ROOT" && !dr.incomplete()) {
				return true;
			}
		}
		return false;
	}
	
	private void Predictor(DottedRule state, Grammar grammar) {
		for(Rule r : grammar.rewrites(state.symbol_after_dot())) {
			this.Enqueue(new DottedRule(r,0,state.stop,state.stop),state.stop);
		}
	}
	
	private void Scanner(DottedRule state, Grammar grammar, String[] sent) {
		// if the symbol after the dot expands to the current word in the sentence
		if(grammar.parents(sent[state.stop]).contains(state.symbol_after_dot())) {
			for(Rule r : grammar.rewrites(state.symbol_after_dot())) {
				if(r.get_rhs()[0] == sent[state.stop]) {
					this.Enqueue(new DottedRule(r,0,state.stop,state.stop+1),state.stop+1);
					return;
				}
			}
		}
	}
	
	private void Completer(DottedRule state, Grammar grammar) {
		// for all states in chart[state.start] expecting a completed state ending at state.stop, advance them to chart[state.stop] with dot+=1
		for(DottedRule r : this.chart.get(state.start)) {
			if(r.symbol_after_dot() == state.rule.get_lhs() &&
					r.stop == state.start) {
				this.Enqueue(new DottedRule(r.rule,r.dot+1,r.start,state.stop),state.stop);
			}
		}
	}
	
	private void Enqueue(DottedRule rule, Integer column) {
		this.chart.get(column).add(rule);
	}
	
	private void initialize_chart(Integer sent_length) {
		this.chart = new ArrayList<LinkedHashSet<DottedRule>>(sent_length);
		for(int i=0; i<sent_length; i++) {
			LinkedHashSet<DottedRule> column = new LinkedHashSet<DottedRule>();
			if(i==0) {
				// Enqueue special start rule
				DottedRule start = new DottedRule(new Rule("1.0 ROOT S"),0,0,0);
				column.add(start);
			}
			this.chart.add(column);
		}
	}
}