package org.logicail.rsbot.scripts.rt6.gopwatertalisman.tasks;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.IMovement;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.framework.wrappers.ITile;
import org.logicail.rsbot.scripts.rt6.gopwatertalisman.GOPWaterTalisman;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Hud;
import org.powerbot.script.rt6.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/01/14
 * Time: 20:22
 */
public class BankingTask extends Node<GOPWaterTalisman> {
	public BankingTask(GOPWaterTalisman script) {
		super(script);
	}

	@Override
	public boolean valid() {
		return !ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).isEmpty()
				&& !ctx.objects.select().id(GOPWaterTalisman.DEPOSIT).isEmpty();
	}

	@Override
	public void run() {
		final Component closebutton = ctx.widgets.component(ExchangeTask.WIDGET_SHOP, ExchangeTask.WIDGET_SHOP_CLOSE);
		if (closebutton.valid() && closebutton.interact("Close")) {
			if (!Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !closebutton.valid();
				}
			}, 200, 10)) {
				return;
			}
		}

		final int startCount = ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).count();

		if (ctx.depositBox.opened() && ctx.depositBox.select().id(GOPWaterTalisman.WATER_TALISMAN).shuffle().poll().interact("Deposit-All", "Water talisman")) {
			script.banked.set(script.banked.get() + startCount);
			ctx.sleep(400);
			return;
		}

		if (!ctx.chat.select().text("All").isEmpty() && ctx.chat.poll().select(Random.nextBoolean())) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).isEmpty();
				}
			}, 400, 8)) {
				script.banked.set(script.banked.get() + startCount);
			}
		} else {
			final GameObject depositBox = ctx.objects.poll();

			if (IMovement.Euclidean(ctx.players.local(), depositBox) > 7) {
				if (ctx.movement.step(ITile.randomize(depositBox.tile(), 3, 3))) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return depositBox.inViewport();
						}
					}, Random.nextInt(555, 999), 6);
				}
			}

			if (ctx.camera.prepare(depositBox)) {
				if (!Random.nextBoolean() || !ctx.depositBox.open()) {
					if (ctx.backpack.selectedItem().id() != GOPWaterTalisman.WATER_TALISMAN) {
						final Item talisman = ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).shuffle().poll();
						if (ctx.hud.open(Hud.Window.BACKPACK) && ctx.backpack.scroll(talisman) && talisman.interact("Use", "Water talisman")) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ctx.backpack.selectedItem().id() == GOPWaterTalisman.WATER_TALISMAN;
								}
							}, Random.nextInt(333, 888), 6);
						}
					}
					if (ctx.backpack.selectedItem().id() == GOPWaterTalisman.WATER_TALISMAN) {
						if (depositBox.interact("Use", "Water talisman -> Deposit box")) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return !ctx.chat.select().isEmpty();
								}
							}, Random.nextInt(333, 888), 6);
						}
					}
				}
			}
		}
	}
}
