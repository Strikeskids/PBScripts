package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.api.methods.Shutdown;
import org.logicail.api.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.Options;
import org.logicail.scripts.logartisanarmourer.tasks.swords.MakeSword;
import org.logicail.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:24
 */
public class MakeIngots extends Node {
	public MakeIngots(MyMethodContext ctx) {
		super(ctx);
	}

	@Override
	public void execute() {
		Options options = LogArtisanArmourer.instance.options;
		options.isSmithing = false;

		if (ctx.skillingInterface.getAction().equals("Smelt")) {
			if (options.mode == Mode.CEREMONIAL_SWORDS) {
				if (!ctx.skillingInterface.select(getCategoryName(), options.getIngotID(), ctx.backpack.select().id(MakeSword.TONGS).isEmpty() ? 26 - ctx.backpack.select().count() : -1)) {
					return;
				}
			} else {
				if (!ctx.skillingInterface.select(getCategoryName(), options.getIngotID())) {
					return;
				}
			}

			if (!ctx.skillingInterface.canStart()) {
				options.failedConsecutiveWithdrawals++;
				sleep(1000, 2500);
				if (options.failedConsecutiveWithdrawals >= 3) {
					ctx.skillingInterface.close();
					new Shutdown(ctx).stop("Ran out of ore... stopping!");
				}
			} else {
				options.failedConsecutiveWithdrawals = 0;
				if (ctx.skillingInterface.start()) {
					ctx.waiting.wait(3000, new Condition() {
						@Override
						public boolean validate() {
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

			for (GameObject smelter : ctx.objects.select().id(options.mode == Mode.BURIAL_ARMOUR ? LogArtisanArmourer.ID_SMELTER : LogArtisanArmourer.ID_SMELTER_SWORDS).nearest().first()) {
				if (ctx.camera.turnTo(smelter)) {
					if (smelter.interact("Withdraw-ingots", "Smelter")) {
						ctx.waiting.wait(6000, new Condition() {
							@Override
							public boolean validate() {
								return ctx.skillingInterface.isOpen();
							}
						});
						sleep(100, 500);
					}
				} else {
					Tile tile = ctx.movement.reachableNear(smelter);
					if (tile != Tile.NIL && ctx.movement.findPath(tile).traverse()) {
						sleep(500, 1000);
					}
				}
			}
		}
	}

	@Override
	public boolean activate() {
		return (ctx.skillingInterface.getAction().equals("Smelt") && !ctx.skillingInterface.select().id(MakeSword.HEATED_INGOTS[0]).isEmpty())
				|| (!ctx.backpack.isFull()
				&& (ctx.backpack.select().id(LogArtisanArmourer.instance.options.getIngotID()).isEmpty() && ctx.backpack.select().id(MakeSword.HEATED_INGOTS).isEmpty()));
	}

	private String getCategoryName() {
		if (LogArtisanArmourer.instance.options.mode == Mode.BURIAL_ARMOUR) {
			switch (LogArtisanArmourer.instance.options.ingotGrade) {
				case TWO:
					return "Ingots, Tier II";
				case THREE:
					return "Ingots, Tier III";
			}
			return "Ingots, Tier I";
		}
		return "Ingots, Tier IV";
	}
}
