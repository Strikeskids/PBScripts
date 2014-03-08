package org.logicail.rsbot.scripts.bankorganiser.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/03/14
 * Time: 23:14
 */
public class MenuAdapter extends MouseAdapter {
	private final BankOrganiserInterface gui;
	private final JList tab;
	private final List<JList> tabs;
	private final int index;

	public MenuAdapter(BankOrganiserInterface gui, JList tab, List<JList> tabs, int index) {
		this.gui = gui;
		this.tab = tab;
		this.tabs = tabs;
		this.index = index;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println(e.isPopupTrigger()); // never works
		if (e.getButton() == MouseEvent.BUTTON3 && tab.getSelectedValue() != null) {
			JPopupMenu menu = new JPopupMenu();

			for (int i = 0; i < tabs.size(); i++) {
				final JList destination = tabs.get(i);
				JMenuItem item = new JMenuItem("Move to Tab " + i);
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						gui.moveToTab(tab, destination);
					}
				});
				if (index == i) {
					item.setEnabled(false);
				}
				menu.add(item);
			}

			menu.show(tab, e.getX(), e.getY());
		}
	}
}
