package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 21:33
 */
public class RenewFamiliar extends LogGildedAltarTask {
	private long nextRun = 0;

	@Override
	public String toString() {
		return "RenewFamiliar";
	}

	public RenewFamiliar(LogGildedAltar script) {
		super(script);
	}

	@Override
	public boolean isValid() {
		if (options.useBOB && options.beastOfBurden.getBoBSpace() > 0 && System.currentTimeMillis() > nextRun) {
			if (ctx.summoning.getTimeLeft() <= 300 || !ctx.summoning.isFamiliarSummoned()) {
				return ctx.summoning.canSummon(options.beastOfBurden);
			}
		}
		return false;
	}

	@Override
	public void run() {
		renew(script);

		nextRun = System.currentTimeMillis() + Random.nextInt(30000, 60000);
		sleep(600, 1400);
	}

	public static void renew(LogGildedAltar script) {
		script.options.status = "Renewing familiar";

		if (script.ctx.summoning.isFamiliarSummoned()) {
			script.ctx.summoning.renewFamiliar();
		} else {
			script.ctx.summoning.summon(script.options.beastOfBurden);
		}
	}
}
