package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.GameObject;

import java.util.concurrent.Callable;


public class MakeIngots extends ArtisanArmourerTask {
	private final SmithAnvil smithAnvil;

	public MakeIngots(LogArtisanWorkshop script, SmithAnvil smithAnvil) {
		super(script);
		this.smithAnvil = smithAnvil;
	}

	@Override
	public String toString() {
		return "Make ingots";
	}

	@Override
	public boolean valid() {
		return super.valid()
				&& (ctx.skillingInterface.getAction().equals("Smelt") && !ctx.skillingInterface.select().id(MakeSword.HEATED_INGOTS[0]).isEmpty())
				|| (!ctx.backpack.isFull() && (ctx.backpack.select().id(options.getIngotId()).isEmpty() && ctx.backpack.select().id(MakeSword.HEATED_INGOTS).isEmpty()));
	}

	@Override
	public void run() {
		options.isSmithing = false;

		if (ctx.skillingInterface.getAction().equals("Smelt")) {
			if (options.mode == Mode.CEREMONIAL_SWORDS) {
				int quanity = 0;
				if (ctx.backpack.select().id(MakeSword.SWORD_PLANS).isEmpty()) {
					quanity = 28 - ctx.backpack.select().count() - 1;
				}

				if (!ctx.skillingInterface.select(smithAnvil.getCategoryIndex(), options.getIngotId(), quanity)) {
					script.log.info("Couldn't select for swords");
					return;
				}
			} else {
				//Context.get().getScriptHandler().log.info("Ingot: " + LogArtisanWorkshop.getIngotID());
				// Anvil and Furnace have different categorys
				if (!ctx.skillingInterface.select(smithAnvil.getCategoryIndex(), options.getIngotId())) {
					return;
				}
			}

			options.status = "Making ingots";

			if (!ctx.skillingInterface.canStart()) {
				options.status = "Can't start";
				++options.failedConsecutiveWithdrawals;
				if (options.failedConsecutiveWithdrawals >= 3) {
					ctx.skillingInterface.close();
					ctx.stop("Ran out of ore... stopping!");
				}
			} else {
				options.failedConsecutiveWithdrawals = 0;
				if (ctx.skillingInterface.start()) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !ctx.skillingInterface.opened();
						}
					});
				}
			}
		} else {
			if (ctx.skillingInterface.opened() && ctx.skillingInterface.close()) {
				return;
			}

			//ArtisanArmourer.setStatus("Search for smelter");
			for (GameObject smelter : ctx.objects.select().id(options.mode == Mode.BURIAL_ARMOUR ? LogArtisanWorkshop.ID_SMELTER : LogArtisanWorkshop.ID_SMELTER_SWORDS).nearest().first()) {
				if (ctx.camera.prepare(smelter)) {
					options.status = "Clicking on smelter";
					if (smelter.interact("Withdraw-ingots", "Smelter")) {
						if (Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.skillingInterface.opened();
							}
						}, 200, 10)) {
							ctx.sleep(200);
						}
					}
				}
			}
		}
	}
}
