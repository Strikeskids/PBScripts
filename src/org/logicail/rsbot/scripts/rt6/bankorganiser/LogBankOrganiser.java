package org.logicail.rsbot.scripts.rt6.bankorganiser;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.rt6.bankorganiser.gui.BankOrganiserInterface;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.Game;
import org.powerbot.script.rt6.Interactive;
import org.powerbot.script.rt6.Item;

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
		properties = "topic=1174066;client=6;version=1.00;hidden=true")
public class LogBankOrganiser extends LogicailScript<LogBankOrganiser> {
	public String status = "";
	public ItemCategoriser itemCategoriser;

	public LogBankOrganiser() {
		super();
	}

	@Override
	public LinkedHashMap<Object, Object> getPaintInfo() {
		final LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();

		map.put("Status", status);
		//map.put("Number of Tabs", ctx.bank.getNumberOfTabs());

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
		ctx.sleep(70);
	}

	@Override
	public void start() {
		itemCategoriser = new ItemCategoriser(ctx);

		ctx.controller.offer(new Task<LogBankOrganiser>(this) {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							gui = new BankOrganiserInterface(LogBankOrganiser.this);
						} catch (Exception exception) {
							exception.printStackTrace();
							ctx.controller.stop();
						}
					}
				});
			}
		});
	}

	@Override
	public void repaint(Graphics g) {
		super.repaint(g);

		for (Item item : ctx.bank.select().select(Interactive.areInViewport())) {
			org.powerbot.script.rt6.Component component = item.component();
			component.draw(g);
			Point point = component.screenPoint();
			g.drawString(itemCategoriser.category(item.id()), point.x, point.y);
		}
	}
}
