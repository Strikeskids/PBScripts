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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui1(GuiTest.this);
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui2();
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui3();
			}
		});
	}

	@Override
	public int poll() {
		return 1000;
	}
}
