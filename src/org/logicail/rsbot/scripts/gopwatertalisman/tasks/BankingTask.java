package org.logicail.rsbot.scripts.gopwatertalisman.tasks;

import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.gopwatertalisman.GOPWaterTalisman;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Item;

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
	public boolean isValid() {
		return !ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).isEmpty()
				&& !ctx.objects.select().id(GOPWaterTalisman.DEPOSIT).isEmpty();
	}

	@Override
	public void run() {
		final Component closebutton = ctx.widgets.get(ExchangeTask.WIDGET_SHOP, ExchangeTask.WIDGET_SHOP_CLOSE);
		if (closebutton.isValid() && closebutton.interact("Close")) {
			if (!Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !closebutton.isValid();
				}
			}, Random.nextInt(111, 333), 10)) {
				return;
			}
		}

		final int startCount = ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).count();

		if (ctx.depositBox.isOpen() && ctx.depositBox.select().id(GOPWaterTalisman.WATER_TALISMAN).poll().interact("Deposit all", "Water talisman")) {
			script.banked.set(script.banked.get() + startCount);
			sleep(222, 888);
			return;
		}

		if (!ctx.chat.select().text("All").isEmpty() && ctx.chat.poll().select(Random.nextBoolean())) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).isEmpty();
				}
			}, Random.nextInt(333, 888), 8)) {
				script.banked.set(script.banked.get() + startCount);
			}
		} else {
			final GameObject depositBox = ctx.objects.poll();

			if (IMovement.Euclidean(ctx.players.local(), depositBox) > 7) {
				if (ctx.movement.stepTowards(depositBox.getLocation().randomize(3, 1, 3, 1))) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return depositBox.isOnScreen();
						}
					}, Random.nextInt(555, 999), 6);
				}
			}

			if (ctx.camera.prepare(depositBox)) {
				if (!Random.nextBoolean() || !ctx.depositBox.open()) {
					if (ctx.backpack.getSelectedItem().getId() != GOPWaterTalisman.WATER_TALISMAN) {
						final Item talisman = ctx.backpack.select().id(GOPWaterTalisman.WATER_TALISMAN).shuffle().poll();
						if (ctx.hud.view(Hud.Window.BACKPACK) && talisman.interact("Use", "Water talisman")) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ctx.backpack.getSelectedItem().getId() == GOPWaterTalisman.WATER_TALISMAN;
								}
							}, Random.nextInt(333, 888), 6);
						}
					}
					if (ctx.backpack.getSelectedItem().getId() == GOPWaterTalisman.WATER_TALISMAN) {
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
