package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarSettings;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:52
 */
public class SummoningTask extends Branch {
	public static int nextPoints = -1;
	private static SummoningTask instance;

	public static SummoningTask getInstance() {
		return instance;
	}

	public SummoningTask(LogicailMethodContext ctx) {
		super(ctx);
		instance = this;

		boolean edgeville = false;


	}

	@Override
	public boolean branch() {
		if (!LogGildedAltarSettings.onlyHouseObelisk
				&& LogGildedAltarSettings.banking
				&& LogGildedAltarSettings.useBOB
				&& !ctx.bank.isOpen()
				&& (ctx.summoning.getTimeLeft() <= 300 || !ctx.summoning.isFamiliarSummoned())) {
			if (nextPoints == -1) {
				//nextPoints = Random.nextInt(LogGildedAltarSettings.beastOfBurden.getRequiredSummonPoints() + 1, LogGildedAltarSettings.beastOfBurden.getRequiredSummonPoints() * 2);
			}
			return ctx.summoning.getSummoningPoints() < nextPoints;
		}

		return false;
	}
}
