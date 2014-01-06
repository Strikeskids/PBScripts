package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 21:33
 */
public class RenewFamiliar extends LogGildedAltarTask {
	private long nextRun = 0;

	public RenewFamiliar(LogGildedAltar script) {
		super(script);
	}

	@Override
	public boolean isValid() {
		if (System.currentTimeMillis() > nextRun) {
			if (options.useBOB && options.beastOfBurden.getBoBSpace() > 0 && options.setupFinished && ctx.players.local().isIdle() && !ctx.bank.isOpen()) {
				if (ctx.summoning.getTimeLeft() <= 300 || !ctx.summoning.isFamiliarSummoned()) {
					if (ctx.summoning.canSummon(options.beastOfBurden)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		options.status = "Renewing familiar";

		if (ctx.summoning.isFamiliarSummoned()) {
			ctx.summoning.renewFamiliar(); // TODO: Check orb
			if (ctx.summoning.interactOrb("Renew Familiar")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.summoning.canSummon(options.beastOfBurden);
					}
				}, Random.nextInt(100, 200), 10);
			}
		} else {
			final Item pouch = ctx.backpack.select().id(options.beastOfBurden.getPouchId()).shuffle().poll();
			if (pouch.isValid() && ctx.hud.view(Hud.Window.BACKPACK)) {
				if (pouch.interact("Summon")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.summoning.isFamiliarSummoned();
						}
					}, Random.nextInt(100, 200), 15);
				}
			}
		}

		nextRun = System.currentTimeMillis() + Random.nextInt(60000, 120000);
	}
}
