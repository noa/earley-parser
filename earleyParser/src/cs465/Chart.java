package cs465;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cs465.util.LinkedListNode;
import cs465.util.Logger;
import cs465.util.OurLinkedList;

public class Chart {
	//private Column[] columns;
	
	//private static class Column {
		OurLinkedList<DottedRule>[] belowThreshColumns;
		private OurLinkedList<DottedRule>[] aboveThreshColumns;
		// List of symbolAfterDotToRuleMaps
		private HashMap<String,ArrayList<DottedRule>>[] indexedColumns;

		HashSet<String>[] columnPredictions;
		HashMap<DottedRule,DottedRule>[] columnAttachments;
		HashMap<String,HashSet<String>>[] left_ancestor_pair_tables;
		
	//}
	
	public Chart(Grammar grammar, String[] sent) {
		initialize(grammar, sent);
	}
	
	public OurLinkedList<DottedRule> getColumn(int i) {
		return belowThreshColumns[i];
	}
	
	public int getNumColumns() {
		return belowThreshColumns.length;
	}

	public void enqueue(final DottedRule rule, int column) {
		//TODO: make sure we are always using this and not OurLinkedList.add()
		getColumn(column).add(rule);
		indexRule(rule, column);
	}

	private void indexRule(final DottedRule rule, int column) {
		HashMap<String,ArrayList<DottedRule>> indexedColumn = indexedColumns[column];
		
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

	public void dequeue(LinkedListNode<DottedRule> entry, int column) {
		belowThreshColumns[column].remove(entry);
	}
	
	// initialize the chart based on the length of the sentence being parsed
	private void initialize(Grammar grammar, String[] sent) {
		int numColumns = sent.length + 1;
		belowThreshColumns = new OurLinkedList[numColumns];
		columnPredictions = new HashSet[numColumns];
		columnAttachments = new HashMap[numColumns];
		left_ancestor_pair_tables = new HashMap[numColumns];
		for(int i=0; i<numColumns; i++) {
			belowThreshColumns[i] = new OurLinkedList<DottedRule>();
			indexedColumns[i] = new HashMap<String,ArrayList<DottedRule>>();
			columnPredictions[i] = new HashSet<String>();
			columnAttachments[i] = new HashMap<DottedRule,DottedRule>();
						
			// there is no word corresponding to the first column of the chart
			if(i < sent.length ) {
				left_ancestor_pair_tables[i] = create_ancestor_pair_table(grammar,sent[i]);
				
				/* DEBUG */
				for(String key : left_ancestor_pair_tables[i].keySet()) {
					Logger.print("A_" + i + "(" + key + ")={");
					for(String ancestor : left_ancestor_pair_tables[i].get(key) )
						Logger.print(ancestor + ", ");
					Logger.println("}");
				}
				/* DEBUG */
			}
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
		HashMap<String,ArrayList<DottedRule>> indexedColumn = indexedColumns[state.start];
		
		return indexedColumn.get(state.rule.get_lhs());
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
//		sb.append("column sizes:\t");
//		for(int i=0; i<columns.size(); i++) {
//			sb.append(columns.get(i).size());
//			sb.append("\t");
//		}
//		sb.append("\n");
		
		int sum;
		
		sum = 0;
		sb.append("    indexedColumn sizes:\t");
		for(int i=0; i<belowThreshColumns.length; i++) {
			sb.append(indexedColumns[i].size());
			sb.append("\t");
			sum += indexedColumns[i].size();
		}
		sb.append("\t[");
		sb.append(sum);
		sb.append("]");
		sb.append("\n");
		
		sb.append("columnPredictions sizes:\t");
		for(int i=0; i<belowThreshColumns.length; i++) {
			sb.append(columnPredictions[i].size());
			sb.append("\t");
		}
		sb.append("\n");
		
		sb.append("columnAttachments sizes:\t");
		for(int i=0; i<belowThreshColumns.length; i++) {
			sb.append(columnAttachments[i].size());
			sb.append("\t");
		}
		sb.append("\n");
		
		sb.append("left_ancestor_pair sizes:\t");
		for(int i=0; i<belowThreshColumns.length - 1; i++) {
			sb.append(left_ancestor_pair_tables[i].size());
			sb.append("\t");
		}
		sb.append("\n");
		
		return sb.toString();
	}

}
