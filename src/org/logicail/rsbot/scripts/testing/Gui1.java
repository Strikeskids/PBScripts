package org.logicail.rsbot.scripts.testing;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/01/14
 * Time: 20:25
 */
public class Gui1 extends JFrame {
	private final GuiTest guiTest;

	Gui1(GuiTest guiTest) {
		this.guiTest = guiTest;
		setSize(100, 100);
		setTitle("Gui1");
		add(new JLabel("Gui1"));
		setVisible(true);
	}
}
