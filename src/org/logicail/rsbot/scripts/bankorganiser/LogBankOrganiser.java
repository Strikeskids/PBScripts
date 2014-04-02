package org.logicail.rsbot.scripts.bankorganiser;

import org.logicail.rsbot.scripts.bankorganiser.gui.BankOrganiserInterface;
import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.Game;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 14:05
 */
@Script.Manifest(
		name = "Log Bank Organiser",
		description = "Organises your bank",
		properties = "hidden=true;client=6;")
public class LogBankOrganiser extends LogicailScript<LogBankOrganiser> {
	public String status = "";

	@Override
	public LinkedHashMap<Object, Object> getPaintInfo() {
		final LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();

		map.put("Status", status);
		map.put("Number of Tabs", ctx.bank.getNumberOfTabs());

		return map;
	}

	@Override
	public void poll() {
		try {
			if (ctx.game.clientState() == Game.INDEX_MAP_LOADED) {
				tree.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ctx.sleep(Random.nextInt(50, 150));
	}

	@Override
	public void start() {
		ItemData.getId(0);
		if (!ItemData.loaded()) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						new ErrorDialog("Error", "Failed to load category date");
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			});
			ctx.controller().stop();
			return;
		}

		ctx.controller().offer(new Task<LogBankOrganiser>(this) {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							gui = new BankOrganiserInterface(LogBankOrganiser.this);
						} catch (Exception exception) {
							exception.printStackTrace();
							ctx.controller().stop();
						}
					}
				});
			}
		});
	}
}
