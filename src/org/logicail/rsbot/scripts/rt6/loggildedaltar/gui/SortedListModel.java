package org.logicail.rsbot.scripts.rt6.loggildedaltar.gui;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 15:35
 */
public class SortedListModel extends DefaultListModel {
	// We override addElement in the DefaultListModel class
	// so that the JList remains sorted.
	// Note that in a more complete application, add()
	// set() and setElementAt() should also be overridden.
	public void addElement(Object obj) {
		String str = obj.toString();
		int index = getSize();
		while (index > 0 && elementAt(index - 1).toString().compareTo(str) >= 0) {
			index--;
		}
		super.add(index, obj);
	}

	@Override
	public void add(int index, Object element) {
		if (contains(element)) {
			return;
		}
		addElement(element);
	}
}