package cs465;

import java.util.ArrayList;
import java.util.HashMap;

import cs465.util.OurLinkedList;

public class Chart {
	ArrayList<OurLinkedList<DottedRule>> columns = null;
	HashMap<String,DottedRule> symbolAfterDotToRuleMap = new HashMap<String,DottedRule>();	
	
	public OurLinkedList<DottedRule> getColumn(int i) {
		return columns.get(i);
	}
	
	public int getNumColumns() {
		return columns.size();
	}

	public void enqueue(DottedRule rule, Integer column) {
		getColumn(column).add(rule);
	}
	
	public void initialize(Grammar grammar, Integer sent_length) {
		columns = new ArrayList<OurLinkedList<DottedRule>>();
		for(int i=0; i<sent_length+1; i++) {
			OurLinkedList<DottedRule> column = new OurLinkedList<DottedRule>();
			if(i==0) {
				// Enqueue special start rule
				for(Rule r : grammar.get_start_rules()) {
					DottedRule start = new DottedRule(r,0,0, r.ruleWeight);
					column.add(start);
				}
			}
			columns.add(column);
		}
	}

	/**
	 * Gets all the rules in column with index state.start whose symbol_after_dot
	 * is the lhs of state.
	 */
	public ArrayList<DottedRule> getAttachableRules(DottedRule state) {
		ArrayList<DottedRule> attachableRules = new ArrayList<DottedRule>();
		for(DottedRule r : getColumn(state.start)) {
			if(!r.complete() && r.symbol_after_dot().equals(state.rule.get_lhs())) { 
				attachableRules.add(r);
			}
		}
		return attachableRules;
	}

}
