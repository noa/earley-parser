package cs465;

public class StochasticEarleyParser extends Parser {

	@Override
	public Tree parse(Grammar grammar, String[] sent) {
		// TODO Auto-generated method stub
//      
//      Psuedo Code for Earley Parser
//      
//      columns.length = text.split().length + 1
//
//      for(j=0; j<columns.length; j++) {
//        column = columns[j];
//        while(column.hasNext()) {
//          entry = column.next();
//        if (entry == i YYY.NXXX...) {
//          //Predict
//          if (N has not been predicted for this column) {
//            columns[j].add( j <all rules with LHS N>);
//            } else {
//            Do nothing;
//          }
//        } else if ( entry == i YYY.TXXX...) {
//          // Scan
//          if (text[i to i+1] == T) {
//            columns[j+1].add(i YYYT.XXX...); // with backpointers(entry, text[i to i+1])
//            } else {
//            Do nothing;
//          }
//        } else { // entry == i LYYY.Nothing) {
//
//          if (L == ROOT) {
//            continue;
//          }
//          // Attach
//          foreach(<l YYY.LXXX> in column[i]) {
//            // just shift the dot over
//            columns[j].add(i YYYL.XXX); // with backpointers (customer, entry) where customer = <l YYY.LXXX>
//          }
//        }
//        }
//      }
//
//      Look for <end ROOT XXX.>
      
		return null;
	}

	@Override
	public boolean recognize(Grammar grammar, String[] sent) {
		// TODO Auto-generated method stub
		return false;
	}
	
}