package cs465.util;

import java.util.Iterator;


public class OurLinkedList<T> implements Iterable<T> {
	private LinkedListNode<T> first;
	private LinkedListNode<T> last;
	
	public void add(T value) {
		LinkedListNode<T> node = new LinkedListNode<T>(value);
		if (first == null || last == null) {
			first = node;
			last = first;
		}
		last.setNext(node);
	}

	public LinkedListNode<T> getFirst() {
		return first;
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
