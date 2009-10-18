package cs465;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

// non-stochastic CKY parser
public class Chart {
	ArrayList<ArrayList<Set<String>>> chart = null;
	public Chart(Integer sentlen) {
		this.chart = new ArrayList<ArrayList<Set<String>>>(sentlen);
		for(int i=0; i < sentlen; i++) {
			ArrayList<Set<String>> column = new ArrayList<Set<String>>(sentlen); 
			for(int j=0; j < sentlen; j++) {
				column.add(new HashSet<String>());
			}
			this.chart.set(i, column);
		}
	}
	public void initialize_cell(Integer i, Integer j, Set<String> symbols) {
		this.chart.get(i).set(j, symbols);
	}
	public void add_to_cell(Integer i, Integer j, String symbol) {
		this.chart.get(i).get(j).add(symbol);
	}
	public void add_to_cell(Integer i, Integer j, Set<String> symbols) {
		this.chart.get(i).get(j).addAll(symbols);
	}
	public Set<String> get_cell(Integer i, Integer j) {
		return this.chart.get(i).get(j);
	}
}