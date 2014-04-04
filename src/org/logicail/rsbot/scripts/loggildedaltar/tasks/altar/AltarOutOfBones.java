package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Summoning;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 19:51
 */
public class AltarOutOfBones extends LogGildedAltarTask {
	private int nextTakeBoB = Random.nextInt(1, 8);

	public AltarOutOfBones(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Check out of bones";
	}

	@Override
	public boolean valid() {
		if (getBackpackOffering().count() < nextTakeBoB) {
			nextTakeBoB = Random.nextInt(1, 8);
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		if (options.useBOB.get() && !options.usedBOB.get() && ctx.summoning.summoned() && options.beastOfBurden.bobSpace() > 0) {
			options.status = "Taking items from BoB";
			script.log.info(options.status);

			final int inventoryCount = ctx.backpack.select().count();

			if (ctx.summoning.select(Summoning.Option.TAKE_BOB)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return options.usedBOB.get() || ctx.backpack.select().count() > inventoryCount;
					}
				}, Random.nextInt(300, 600), 5);
				sleep(333);
			}
		} else if (getBackpackOffering().isEmpty()) {
			options.status = ("Out of bones banking");
			script.log.info(options.status);
			script.bankingTask.setBanking();
		}
	}
}
