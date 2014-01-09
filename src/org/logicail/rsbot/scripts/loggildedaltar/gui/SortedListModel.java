package org.logicail.rsbot.scripts.loggildedaltar.gui;

import javax.swing.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 15:35
 */
public class SortedListModel<T> extends AbstractListModel {
	// Define a SortedSet
	final SortedSet<T> model;

	public SortedListModel() {
		model = new TreeSet<T>();
	}

	public SortedListModel(Comparator<T> comparator) {
		model = new TreeSet<T>(comparator);
	}

	// Other methods
	public void add(T element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	// ListModel methods
	public int getSize() {
		// Return the model size
		return model.size();
	}

	public void addAll(T elements[]) {
		Collection c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(T element) {
		return model.contains(element);
	}

	public T firstElement() {
		// Return the appropriate element
		return model.first();
	}

	public Object getElementAt(int index) {
		// Return the appropriate element
		return model.toArray()[index];
	}

	public boolean isEmpty() {
		return model.isEmpty();
	}

	public Iterator iterator() {
		return model.iterator();
	}

	public T lastElement() {
		// Return the appropriate element
		return model.last();
	}

	public boolean removeElement(T element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}

	public Object[] toArray() {
		return model.toArray();
	}
}
