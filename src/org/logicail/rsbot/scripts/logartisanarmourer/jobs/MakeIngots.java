package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;

import java.util.concurrent.Callable;


public class MakeIngots extends ArtisanArmourerTask {
	private final SmithAnvil smithAnvil;

	public MakeIngots(LogArtisanArmourer script, SmithAnvil smithAnvil) {
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
				|| (!ctx.backpack.isFull() && (ctx.backpack.select().id(script.getIngotID()).isEmpty() && ctx.backpack.select().id(MakeSword.HEATED_INGOTS).isEmpty()));
	}

	@Override
	public void run() {
		script.options.isSmithing = false;

		if (ctx.skillingInterface.getAction().equals("Smelt")) {
			if (script.options.mode == Mode.CEREMONIAL_SWORDS) {
				if (!ctx.skillingInterface.select(smithAnvil.getCategoryName(), script.getIngotID(), !ctx.backpack.select().id(MakeSword.TONGS).isEmpty() ? -1 : 26 - ctx.backpack.select().count())) {
					return;
				}
			} else {
				//Context.get().getScriptHandler().log.info("Ingot: " + LogArtisanArmourer.getIngotID());
				if (!ctx.skillingInterface.select(smithAnvil.getCategoryName(), script.getIngotID())) {
					return;
				}
			}

			script.options.status = "Making ingots";

			if (!ctx.skillingInterface.canStart()) {
				script.options.status = "Can't start";
				++script.options.failedConsecutiveWithdrawals;
				if (script.options.failedConsecutiveWithdrawals >= 3) {
					ctx.skillingInterface.close();
					ctx.stop("Ran out of ore... stopping!");
				}
			} else {
				script.options.failedConsecutiveWithdrawals = 0;
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
			for (GameObject smelter : ctx.objects.select().id(script.options.mode == Mode.BURIAL_ARMOUR ? LogArtisanArmourer.ID_SMELTER : LogArtisanArmourer.ID_SMELTER_SWORDS).nearest().first()) {
				if (ctx.camera.prepare(smelter)) {
					script.options.status = "Clicking on smelter";
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
