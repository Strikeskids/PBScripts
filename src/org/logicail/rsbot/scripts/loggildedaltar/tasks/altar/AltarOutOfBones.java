package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 19:51
 */
public class AltarOutOfBones extends AltarAbstract {
	private int nextTakeBoB = Random.nextInt(1, 8);

	public AltarOutOfBones(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Check out of bones";
	}

	@Override
	public boolean isValid() {
		if (getBackpackOffering().count() < nextTakeBoB) {
			nextTakeBoB = Random.nextGaussian(2, 8, 2);
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		if (options.useBOB && !options.usedBOB && ctx.summoning.isFamiliarSummoned() && options.beastOfBurden.getBoBSpace() > 0) {
			options.status = "Taking items from BoB";
			ctx.log.info(options.status);

			final int inventoryCount = ctx.backpack.select().count();

			if (ctx.summoning.takeBoB()) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return options.usedBOB || ctx.backpack.select().count() > inventoryCount;
					}
				});
				sleep(100, 600);
			}
		} else if (getBackpackOffering().isEmpty()) {
			options.status = ("Out of bones banking");
			ctx.log.info(options.status);
			script.bankingTask.banking.setBanking(true);
		}
	}
}