package cs465;

import java.util.ArrayList;
import java.util.HashMap;

import cs465.util.OurLinkedList;

public class Chart {
	ArrayList<OurLinkedList<DottedRule>> columns = null;
	// List of symbolAfterDotToRuleMaps
	ArrayList<HashMap<String,ArrayList<DottedRule>>> indexedColumns = new ArrayList<HashMap<String,ArrayList<DottedRule>>>();
	
	public OurLinkedList<DottedRule> getColumn(int i) {
		return columns.get(i);
	}
	
	public int getNumColumns() {
		return columns.size();
	}

	public void enqueue(final DottedRule rule, int column) {
		//TODO: make sure we are always using this and not OurLinkedList.add()
		getColumn(column).add(rule);
		HashMap<String,ArrayList<DottedRule>> indexedColumn = indexedColumns.get(column);
		
		if (!rule.complete()) {
			ArrayList<DottedRule> indexed_rules;
			if (indexedColumn.containsKey(rule.symbol_after_dot())) {
				indexed_rules = indexedColumn.get(rule.symbol_after_dot());
			} else {
				indexed_rules = new ArrayList<DottedRule>();
				indexedColumn.put(rule.symbol_after_dot(), indexed_rules );
			}
			indexed_rules.add(rule);
		}
	}
	
	// initialize the chart based on the length of the sentence being parsed
	public void initialize(Grammar grammar, Integer sent_length) {
		columns = new ArrayList<OurLinkedList<DottedRule>>();
		
		for(int i=0; i<sent_length+1; i++) {
			OurLinkedList<DottedRule> column = new OurLinkedList<DottedRule>();
			if(i==0) {
				
			}
			columns.add(column);
			HashMap<String,ArrayList<DottedRule>> indexedColumn = new HashMap<String,ArrayList<DottedRule>>();
			indexedColumns.add(indexedColumn);
		}
		
		// Enqueue special start rule
		for(Rule r : grammar.get_start_rules()) {
			DottedRule start = new DottedRule(r,0,0, r.ruleWeight);
			enqueue(start, 0);
		}
	}

	/**
	 * Gets all the rules in column with index state.start whose symbol_after_dot
	 * is the lhs of state.
	 */
	public ArrayList<DottedRule> getAttachableRules(DottedRule state) {
		HashMap<String,ArrayList<DottedRule>> indexedColumn = indexedColumns.get(state.start);
		
		return indexedColumn.get(state.rule.get_lhs());
	}

}
