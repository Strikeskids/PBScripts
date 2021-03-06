package org.logicail.rsbot.scripts.rt6.gopwatertalisman.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.rt6.gopwatertalisman.GOPWaterTalisman;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Npc;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/01/14
 * Time: 19:35
 */
public class ExchangeTask extends Node<GOPWaterTalisman> {
	public static final int WIDGET_SHOP = 779;
	public static final int WIDGET_SHOP_WATER_TALISMAN = 15;
	public static final int WIDGET_SHOP_CLOSE = 131;

	public ExchangeTask(GOPWaterTalisman script) {
		super(script);
	}

	@Override
	public boolean valid() {
		return !ctx.backpack.isFull()
				&& ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).isEmpty()
				&& !ctx.backpack.select().id(GOPWaterTalisman.TOKENS).isEmpty()
				&& !ctx.npcs.select().id(GOPWaterTalisman.ELRISS).isEmpty();
	}

	@Override
	public void run() {
		final Component shop = ctx.widgets.component(WIDGET_SHOP, WIDGET_SHOP_WATER_TALISMAN);
		if (shop.valid()) {
			if (shop.interact("Buy X", "Water talisman")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.isInputWidgetOpen();
					}
				}, Random.nextInt(333, 888), 6)) {
					int space = 28 - ctx.backpack.select().count();
					if (ctx.chat.sendInput(Integer.toString(space))) {
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.widgets.component(WIDGET_SHOP, 141).text().contains("Water talisman");
							}
						}, Random.nextInt(333, 888), 6)) {
							if (ctx.widgets.component(WIDGET_SHOP, 130).interact("Confirm")) {
								Condition.wait(new Callable<Boolean>() {
									@Override
									public Boolean call() throws Exception {
										return !ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).isEmpty();
									}
								}, Random.nextInt(333, 888), 6);
							}
						}
					}
				}
			}
		} else {
			if (ctx.depositBox.opened()) {
				ctx.depositBox.close();
				Condition.sleep(333);
				return;
			}

			final Npc elriss = ctx.npcs.poll();
			if (ctx.camera.prepare(elriss) && elriss.interact("Exchange")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return shop.valid();
					}
				}, Random.nextInt(333, 888), 8);
			}
		}
	}
}
