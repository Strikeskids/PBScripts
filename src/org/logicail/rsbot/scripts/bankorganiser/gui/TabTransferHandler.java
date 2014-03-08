package org.logicail.rsbot.scripts.bankorganiser.gui;

import org.logicail.rsbot.scripts.loggildedaltar.gui.SortedListModel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/03/14
 * Time: 23:52
 */
public class TabTransferHandler extends TransferHandler {
	private int[] indices = null;
	private int addIndex = -1; //Location where items were added
	private int addCount = 0;  //Number of items added.

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		// Check for String flavor
		if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}

		JList list = (JList) support.getComponent();

		DefaultListModel listModel = (DefaultListModel) list.getModel();
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		int index = dl.getIndex();
		boolean insert = dl.isInsert();

		// Get the string that is being dropped.
		Transferable t = support.getTransferable();
		String data;
		try {
			data = (String) t.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return false;
		}

		// Wherever there is a newline in the incoming data,
		// break it into a separate item in the list.
		String[] values = data.split("\n");

		addIndex = index;
		addCount = values.length;

		// Perform the actual import.
		for (int i = 0; i < values.length; i++) {
			if (insert) {
				if (listModel instanceof SortedListModel) {
					listModel.addElement(values[i]);
				} else {
					listModel.add(index++, values[i]);
				}
			} else {
				if (listModel instanceof SortedListModel) {
					listModel.addElement(values[i]);
				} else {
					if (index < listModel.getSize()) {
						listModel.set(index++, values[i]);
					} else {
						listModel.add(index++, values[i]);
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		JList source = (JList) c;
		DefaultListModel listModel = (DefaultListModel) source.getModel();

		if (action == TransferHandler.MOVE) {
			for (Object o : source.getSelectedValues()) {
				final int i = listModel.lastIndexOf(o);
				if (i > -1) {
					if (listModel.indexOf(o) != i) {
						listModel.remove(i);
					}
				}
			}
		}

		source.clearSelection();

		indices = null;
		addCount = 0;
		addIndex = -1;
	}

	@Override
	public int getSourceActions(JComponent component) {
		return MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent component) {
		JList list = (JList) component;
		indices = list.getSelectedIndices();
		Object[] values = list.getSelectedValues();

		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < values.length; i++) {
			Object val = values[i];
			buff.append(val == null ? "" : val.toString());
			if (i != values.length - 1) {
				buff.append("\n");
			}
		}

		return new StringSelection(buff.toString());
	}
}
