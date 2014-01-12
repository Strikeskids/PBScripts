package org.logicail.rsbot.scripts.test;

import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 12/01/14
 * Time: 19:08
 */
@Manifest(name = "PiersTestScript", description = "Test pFletcher gui on mac", hidden = true)
public class PiersTestScript extends PollingScript {
	@Override
	public int poll() {
		return 1000;
	}

	@Override
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new PiersGUI(ctx, PiersTestScript.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
