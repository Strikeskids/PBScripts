package org.logicail.api.walking.webbuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Manner
 * Date: 6/25/13
 * Time: 11:16 AM
 */
public class GUI extends JFrame {

	private static final JPanel codePanel = new JPanel();
	private static final JTextArea code = new JTextArea(20, 55);
	private static final JButton copy = new JButton("Copy");
	private static final JButton back = new JButton("Back");

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		super("Manner's Map Utility v1.2");
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		codePanel.setVisible(false);
		setSize(720, 480);
		setMinimumSize(new Dimension(700, 440));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
		final MapPane map = new MapPane();
		add(map, BorderLayout.CENTER);
		add(new Controls(), BorderLayout.NORTH);
		map.getMap();
		setVisible(true);
		JScrollPane top = new JScrollPane(code);
		top.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel bottom = new JPanel();
		bottom.add(copy);
		bottom.add(back);
		codePanel.setLayout(new BorderLayout());
		codePanel.add(top, BorderLayout.NORTH);
		codePanel.add(bottom, BorderLayout.SOUTH);
		add(codePanel, BorderLayout.EAST);
		code.setLineWrap(true);
		code.setWrapStyleWord(true);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCode(false);
			}
		});
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TextCoppier().setClipboardContents(code.getText());
			}
		});
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					new TextCoppier().setClipboardContents("new Tile" + map.getTile() + "");
				}
			}
		});
	}

	public static void showCode(boolean show) {
		if (show) {
			if (MapPane.areaSelected) {
				String area = "Area myArea = new Area(new Tile[] {";
				for (int i = 0; i < MapPane.TILES.size(); i++) {
					area += " new Tile" + MapPane.TILES.get(i).toString();
					if (i != MapPane.TILES.size() - 1) area += ",";
					if (i != 0 && (i + 1) % 3 == 0 && i != MapPane.TILES.size() - 1) area += "\n\t";
				}
				area += " });";
				code.setText(area);
			} else {
				String area = "Tile[] myPath = new Tile[] {";
				for (int i = 0; i < MapPane.TILES.size(); i++) {
					area += " new Tile" + MapPane.TILES.get(i).toString();
					if (i != MapPane.TILES.size() - 1) area += ",";
					if (i != 0 && (i + 1) % 3 == 0 && i != MapPane.TILES.size() - 1) area += "\n\t";
				}
				area += " };";
				code.setText(area);
			}
			codePanel.setVisible(true);
		} else {
			codePanel.setVisible(false);
			Controls.getCode.setText("Get Code");
		}
	}

	private class TextCoppier implements ClipboardOwner {

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}

		public void setClipboardContents(String aString) {
			StringSelection stringSelection = new StringSelection(aString);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		}
	}
}
