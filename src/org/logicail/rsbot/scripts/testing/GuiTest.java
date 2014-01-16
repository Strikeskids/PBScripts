package org.logicail.rsbot.scripts.testing;

import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/01/14
 * Time: 20:01
 */
@Manifest(name = "GuiTest", description = "Testing gui on mac", hidden = true)
public class GuiTest extends PollingScript {

	public GuiTest() {
		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new Gui1(GuiTest.this);
					}
				});
			}
		});
		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new Gui2();
					}
				});
			}
		});
		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new Gui3();
					}
				});
			}
		});
	}

	@Override
	public int poll() {
		return 1000;
	}

	class Gui1 extends JFrame {
		private final GuiTest guiTest;

		Gui1(GuiTest guiTest) {
			this.guiTest = guiTest;
			setSize(100, 100);
			setTitle("Gui1");
			add(new JLabel("Gui1"));
			setVisible(true);
		}
	}

	class Gui2 extends JFrame {
		Gui2() {
			setSize(100, 100);
			setTitle("Gui2");
			add(new JLabel("Gui2"));
			setVisible(true);
		}
	}

	class Gui3 extends JFrame {
		private GuiTest guiTest;

		Gui3() {
			setSize(100, 100);
			setTitle("Gui3");
			add(new JLabel("Gui3"));
			setVisible(true);
		}
	}
}
