package org.logicail.api.walking.webbuilder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Manner
 * Date: 6/25/13
 * Time: 11:27 AM
 */
public class Controls extends JPanel {

	public static final JButton getCode = new JButton("Get Code");
	private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(5, 1, 99, 1));
	private static final JLabel tileInfo = new JLabel("Tile: (0, 0, 0)");

	public Controls() {
		JRadioButton path = new JRadioButton("Path", true);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(path);
		JRadioButton area = new JRadioButton("Area");
		buttonGroup.add(area);
		tileInfo.setFont(tileInfo.getFont().deriveFont(16f));
		JButton undo = new JButton("Undo");
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = MapPane.TILES.size() - 1;
				if (index >= 0) {
					MapPane.UNDOED.add(MapPane.TILES.get(index));
					MapPane.TILES.remove(index);
					getParent().repaint();
				}

			}
		});
		JButton redo = new JButton("Redo");
		redo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = MapPane.UNDOED.size() - 1;
				if (index >= 0) {
					MapPane.TILES.add(MapPane.UNDOED.get(index));
					MapPane.UNDOED.remove(index);
					getParent().repaint();
				}
			}
		});
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUI.showCode(false);
				MapPane.TILES.clear();
				getParent().repaint();
			}
		});
		area.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUI.showCode(false);
				MapPane.areaSelected = true;
				getParent().repaint();
			}
		});
		path.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUI.showCode(false);
				MapPane.areaSelected = false;
				getParent().repaint();
			}
		});
		getCode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getCode.getText().equalsIgnoreCase("Get Code")) {
					GUI.showCode(true);
					getCode.setText("Hide Code");
				} else {
					GUI.showCode(false);
				}
			}
		});
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MapPane.dragSpacing = (Integer) spinner.getValue();
			}
		});
		setLayout(new BorderLayout());
		JPanel left = new JPanel();
		JPanel center = new JPanel();
		JPanel right = new JPanel();
		left.add(path);
		left.add(area);
		JLabel label = new JLabel("    Drag Spacing:");
		left.add(label);
		left.add(spinner);
		center.add(tileInfo);
		right.add(undo);
		right.add(redo);
		right.add(clear);
		right.add(getCode);
		add(left, BorderLayout.WEST);
		add(center, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);

	}

	public static void setTile(MapTile t) {
		tileInfo.setText("Tile: " + t);
	}
}
