package org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;

import java.util.concurrent.Callable;

public class DepositArmour extends ArtisanArmourerTask {
	public DepositArmour(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Deposit Armour";
	}

	private static final int ID_CHUTE = 29396;

	@Override
	public boolean activate() {
		return super.activate()
				&& ctx.backpack.select().id(options.getIngotId()).isEmpty()
				&& !ctx.backpack.select().id(LogArtisanWorkshop.ARMOUR_ID_LIST).isEmpty();
	}

	@Override
	public void run() {
		options.isSmithing = false;

		if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
			return;
		}

		//ArtisanArmourer.setStatus("Search for chute");

		// Deposit current
		for (GameObject chute : ctx.objects.select().id(ID_CHUTE).nearest().first()) {
			if (ctx.camera.prepare(chute)) {
				options.status = "Clicking on chute";
				if (chute.interact("Deposit-armour", "Chute")) {
					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.backpack.select().id(LogArtisanWorkshop.ARMOUR_ID_LIST).isEmpty();
						}
					})) {
						sleep(200, 600);
					}
				}
			} else {
				options.status = "Walking to chute";
				sleep(500, 2000);
			}
		}
	}
}
