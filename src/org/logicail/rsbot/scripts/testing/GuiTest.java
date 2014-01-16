package org.logicail.rsbot.scripts.testing;

import org.powerbot.script.AbstractScript;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

	class GUI extends JFrame {
		private final AbstractScript script;

		public GUI(AbstractScript script) {
			this.script = script;

			setTitle(this.script.getName() + " v" + this.script.getVersion());
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setMinimumSize(new Dimension(500, 360));

			final JPanel contentPane = (JPanel) getContentPane();
			contentPane.setLayout(new BorderLayout());
			contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

			pack();
			setLocationRelativeTo(null);
			setVisible(true);
		}
	}
}
