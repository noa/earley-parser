package cs465.util;

public class Pair<X,Y> {

	private X x;
	private Y y;
	
	public Pair(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public X get1() { 
		return x;
	}
	public Y get2() {
		return y;
	}
	
	@Override
	public boolean equals(Object o) { 
		if (o instanceof Pair<?,?>) {
			Pair<?,?> p = (Pair<?,?>)o;
			if (safeEquals(x, p.get1()) && safeEquals(y, p.get2())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37*result + (x == null ? 0 : x.hashCode());
		result = 37*result + (y == null ? 0 : y.hashCode());
		return result;
	}


	private static boolean safeEquals(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return o1 == o2;
		} else {
			return o1.equals(o2);
		}
	}
	
}
