package elements;

public class OrderedPair<E extends Comparable<E>> {
	E a;
	E b;
	
	public OrderedPair(E a, E b) {
		this.a = a;
		this.b = b;
	}
	
	public E getA() {
		return a;
	}
	
	public E getB() {
		return b;
	}
	
	public E getMin() {
		return a.compareTo(b) < 0 ? a : b;
	}
	
	public E getMax() {
		return a.compareTo(b) > 0 ? a : b;
	}
}
