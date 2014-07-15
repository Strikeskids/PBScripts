package org.logicail.rsbot.scripts.rt6.bankorganiser.gui;

import org.logicail.rsbot.scripts.rt6.loggildedaltar.gui.SortedListModel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/03/14
 * Time: 23:52
 */
public class ListTransferHandler extends TransferHandler {
	private int[] indices = null;
	private int addIndex = -1; //Location where items were added
	private int addCount = 0;  //Number of items added.
	private JList destination = null;
	private JList source = null;

	public boolean canImport(TransferHandler.TransferSupport info) {
		// Check for String flavor
		if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		return true;
	}

	protected Transferable createTransferable(JComponent c) {
		return new StringSelection(exportString(c));
	}

	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}

		destination = (JList) info.getComponent();
		DefaultListModel listModel = (DefaultListModel) destination.getModel();
		JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
		int index = dl.getIndex();
		boolean insert = dl.isInsert();

		// Get the string that is being dropped.
		Transferable t = info.getTransferable();
		String data;
		try {
			data = (String) t.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return false;
		}

//		final String[] values = data.split("\n");
//		for (String value : values) {
//			// Perform the actual import.
//			if (insert) {
//				listModel.add(index, value);
//			} else {
//				listModel.set(index, value);
//			}
//		}

		importString(info, data);

		return true;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
		cleanup(c, action == TransferHandler.MOVE);
	}

	//Bundle up the selected items in the list
	//as a single string, for export.
	protected String exportString(JComponent c) {
		JList list = (JList) c;
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

		return buff.toString();
	}

	private boolean cleaned = false;

	//Take the incoming string and wherever there is a
	//newline, break it into a separate item in the list.
	protected void importString(TransferSupport c, String str) {
		JList target = (JList) c.getComponent();
		DefaultListModel listModel = (DefaultListModel) target.getModel();
		JList.DropLocation dl = (JList.DropLocation) c.getDropLocation();
		int index = target.getSelectedIndex();

		cleaned = false;

		//Prevent the user from dropping data back on itself.
		//For example, if the user is moving items #4,#5,#6 and #7 and
		//attempts to insert the items after item #5, this would
		//be problematic when removing the original items.
		//So this is not allowed.
		if (indices != null && index >= indices[0] - 1 && index <= indices[indices.length - 1]) {
			if (!(listModel instanceof SortedListModel)) {
				index = dl.getIndex();

				List<Object> moving = new ArrayList<Object>();
				for (int i1 = indices.length - 1; i1 >= 0; i1--) {
					int i = indices[i1];
					if (i < index) {
						index--;
					}
					moving.add(listModel.remove(i));
				}

				for (int i = 0; i < moving.size(); i++) {
					listModel.add(index++, moving.get(i));
				}
				cleaned = true;
			}
			indices = null;
			return;
		}

		index = dl.getIndex();

		int max = listModel.getSize();
		if (index < 0) {
			index = max;
		} else {
			index++;
			if (index > max) {
				index = max;
			} else {
				index--;
			}
		}
		addIndex = index;
		String[] values = str.split("\n");
		addCount = values.length;
		for (int i = 0; i < values.length; i++) {
			listModel.add(index++, values[i]);
		}
	}

	//If the remove argument is true, the drop has been
	//successful and it's time to remove the selected items
	//from the list. If the remove argument is false, it
	//was a Copy operation and the original list is left
	//intact.
	protected void cleanup(JComponent c, boolean remove) {
		if (!cleaned && remove && indices != null) {
			source = (JList) c;
			DefaultListModel model = (DefaultListModel) source.getModel();
			//If we are moving items around in the same list, we
			//need to adjust the indices accordingly, since those
			//after the insertion point have moved.
			if (addCount > 0) {
				for (int i = 0; i < indices.length; i++) {
					if (indices[i] > addIndex) {
						indices[i] += addCount;
					}
				}
			}
			for (int i = indices.length - 1; i >= 0; i--) {
				try {
					model.remove(indices[i]);
				} catch (ArrayIndexOutOfBoundsException ignored) {
				}
			}
		}
		indices = null;
		addCount = 0;
		addIndex = -1;
	}
}
