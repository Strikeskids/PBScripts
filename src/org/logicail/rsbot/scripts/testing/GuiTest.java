package org.logicail.rsbot.scripts.testing;

import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;

import javax.swing.*;
import java.awt.*;

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
				gui = new GUI();
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

	class GUI extends JFrame {
		GUI() throws HeadlessException {
			setTitle("Testing");
			setSize(200, 200);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setVisible(true);
		}
	}
}
