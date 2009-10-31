package cs465.util;


public class LinkedListNode<T> {

	private T value;
	private LinkedListNode<T> next;
	private LinkedListNode<T> prev;
	
	public LinkedListNode<T> getPrev() {
		return prev;
	}

	public void setPrev(LinkedListNode<T> prev) {
		this.prev = prev;
	}

	public LinkedListNode(T value) { 
		this.value = value;
	}
	
	public void setNext(LinkedListNode<T> next) {
		this.next = next;
	}

	public LinkedListNode<T> getNext() {
		return next;
	}
	
	public T getValue() {
		return value;
	}

	public boolean hasNext() {
		return next != null;
	}
	
	@Override
	public String toString() {
		String str = value.toString();
		if (hasNext()) {
			str += "; " + next.toString();
		}
		return str;
	}

}
