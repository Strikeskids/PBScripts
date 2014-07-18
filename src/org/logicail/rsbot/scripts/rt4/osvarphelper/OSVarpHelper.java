package org.logicail.rsbot.scripts.rt4.osvarphelper;

import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 18/07/2014
 * Time: 17:34
 */
@Script.Manifest(name = "OS Varp Helper", description = "Loads varps from the cache", properties = "topic=1200465;client=4")
public class OSVarpHelper extends PollingScript<IClientContext> {
	VarpHelperGUI gui = null;

	@Override
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui = new VarpHelperGUI(OSVarpHelper.this.ctx);
			}
		});
	}

	@Override
	public void poll() {
		Condition.sleep(500);
	}

	@Override
	public void stop() {
		if (gui != null) {
			gui.setVisible(false);
		}
	}
}
