package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;

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
	public boolean activate() {
		return super.activate()
				&& !ctx.widgets.get(13).isValid() // Bank pin
				&& (ctx.skillingInterface.getAction().equals("Smelt") && !ctx.skillingInterface.select().id(MakeSword.HEATED_INGOTS[0]).isEmpty())
				|| (!ctx.backpack.isFull() && (ctx.backpack.select().id(options.getIngotId()).isEmpty() && ctx.backpack.select().id(MakeSword.HEATED_INGOTS).isEmpty()));
	}

	@Override
	public void run() {
		options.isSmithing = false;

		if (ctx.skillingInterface.getAction().equals("Smelt")) {
			if (options.mode == Mode.CEREMONIAL_SWORDS) {
				if (!ctx.skillingInterface.select(smithAnvil.getCategoryIndex(), options.getIngotId(), !ctx.backpack.select().id(MakeSword.TONGS).isEmpty() ? -1 : 26 - ctx.backpack.select().count())) {
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
							return !ctx.skillingInterface.isOpen();
						}
					});
				}
			}
		} else {
			if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
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
								return ctx.skillingInterface.isOpen();
							}
						})) {
							sleep(100, 500);
						}
					}
				}
			}
		}
	}
}
