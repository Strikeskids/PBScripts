package org.logicail.rsbot.scripts.testing;

import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/01/14
 * Time: 17:00
 */
@Manifest(name = "Test GUI on Mac", description = "testing", hidden = true)
public class GuiTest extends PollingScript {
	private GUI gui;

	public GuiTest() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui = new GUI(GuiTest.this);
			}
		});
	}

	@Override
	public void stop() {
		if (gui != null) {
			gui.dispose();
		}
	}

	@Override
	public int poll() {
		return 1000;
	}
}
