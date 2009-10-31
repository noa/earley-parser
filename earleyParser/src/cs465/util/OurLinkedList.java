package cs465.util;

import java.util.Iterator;


public class OurLinkedList<T> implements Iterable<T> {
	private LinkedListNode<T> first;
	private LinkedListNode<T> last;
	
	/**
	 * Adds a new LinkedListNode<T> to the end of the list
	 * with the value passed in.
	 */
	public void add(T value) {
		LinkedListNode<T> node = new LinkedListNode<T>(value);
		if (first == null || last == null) {
			first = node;
			last = node;
		} else {
			last.setNext(node);
			node.setPrev(last);
			last = node;
		}
	}
	
	/**
	 * Removes the specified node from this list.
	 * 
	 * If LinkedListNode<T> is not contained in this linked list,
	 * the behavior of this method is undefined.
	 */
	public void remove(LinkedListNode<T> node) {
		LinkedListNode<T> prev = node.getPrev();
		LinkedListNode<T> next = node.getNext();
		
		if (node == first) {
			first = next;
		}
		if (node == last) {
			last = prev;
		}
		
		if (prev != null) {
			prev.setNext(next);
		}
		if (next != null) {
			next.setPrev(prev);
		}
		
		node.setNext(null);
		node.setPrev(null);
	}

	public LinkedListNode<T> getFirst() {
		return first;
	}

	@Override
	public String toString() {
		if (first == null) {
			return "[]";
		}
		return "[" + first.toString() + "]";
	}
	
	@Override
	public Iterator<T> iterator() {
		return new OurListIterator();
	}
	
	private class OurListIterator implements Iterator<T> {
		private LinkedListNode<T> current;
		
		public OurListIterator() {
			current = first;
		}
		
		@Override
		public boolean hasNext() {
			return current != null; //current.hasNext();
		}

		@Override
		public T next() {
			if(current == null) {
				return null;
			}
			T value = current.getValue();
			current = current.getNext();
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove is not implemented");
		}
		
	}
	
}
