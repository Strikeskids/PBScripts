package org.logicail.rsbot.scripts.bankorganiser;

import org.logicail.rsbot.scripts.bankorganiser.gui.BankOrganiserInterface;
import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.util.ErrorDialog;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 14:05
 */
@Manifest(name = "Log Bank Organiser", description = "Organises your bank", hidden = true)
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
	public int poll() {
		try {
			if (ctx.game.getClientState() == Game.INDEX_MAP_LOADED) {
				tree.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Random.nextInt(100, 300);
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
			getController().stop();
			return;
		}

		getController().getExecutor().offer(new Task<LogBankOrganiser>(this) {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							gui = new BankOrganiserInterface(LogBankOrganiser.this);
						} catch (Exception exception) {
							exception.printStackTrace();
							getController().stop();
						}
					}
				});
			}
		});
	}
}
