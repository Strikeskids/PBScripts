package org.logicail.rsbot.scripts.rt6.loggildedaltar.gui;

import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.Path;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 15:36
 */
public class ListRenderer extends JLabel implements ListCellRenderer {
	public ListRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(" " + value.toString());
		if (isSelected) {
			setBackground(list.getSelectionBackground());
		} else {
			setBackground(list.getBackground());
		}

		if (value instanceof Path) {
			if (((Path) value).getLocation().getObeliskArea() != null) {
				setForeground(new Color(0, 102, 51));
			} else {
				setForeground(list.getForeground());
			}
		}

		return this;
	}
}
