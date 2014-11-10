package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.LogGildedAltarTask;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.OpenHouse;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 19:17
 */
public class AltarOfferBones extends LogGildedAltarTask {
	private static final int ANIMATION_OFFERING = 24897;

	public AltarOfferBones(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Offer bones";
	}

	@Override
	public boolean valid() {
		return !getBackpackOffering().isEmpty()
				&& ctx.players.local().animation() != ANIMATION_OFFERING;
	}

	@Override
	public void run() {
		if (Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ctx.players.local().animation() == ANIMATION_OFFERING;
			}
		}, 100, 20)) {
			options.TimeLastOffering.set(System.currentTimeMillis());
			ctx.sleep(300);
			return;
		}

		// Check if current house has altar
		if (options.useOtherHouse.get() && ctx.game.clientState() == Game.INDEX_MAP_LOADED) {
			OpenHouse currentHouse = script.houseHandler.getCurrentHouse();
			if (currentHouse != null) {
				currentHouse.setHasObelisk(script.altarTask.getMiniObelisk().valid());
			}
		}

		// TODO: Clan Avatar
//		if (options.useBOB && options.beastOfBurden == Summoning.Familiar.CLAN_AVATAR && org.powerbot.game.api.methods.Settings.get(448) > 0) {
//			Summoning.Familiar familiar = ctx.summoning.getFamiliar();
//			NPC familiarNPC = familiar == null ? null : familiar.getNPC();
//			//LogHandler.print("Familiar: " + familiar.getName() + " NPC: " + familiarNPC == null ? "null" : familiarNPC.getName());
//			if (familiar == null || (familiarNPC != null && Calculations.distanceTo(familiarNPC) > 20)) {
//				instance.setStatus("Call clan avatar");
//				if (Summoning.doCallFollower()) {
//					Waiting.waitFor(2000, 5000, new Condition() {
//						@Override
//						public boolean validate() {
//							Summoning.Familiar familiar = Summoning.getFamiliar();
//							NPC familiarNPC = familiar != null ? familiar.getNPC() : null;
//							return familiarNPC != null && Calculations.distanceTo(familiarNPC) <= 20;
//						}
//					});
//				}
//			} else {
//				//LogHandler.print("Avatar not summoned: Settings[448]=" + org.powerbot.game.api.methods.Settings.get(448) + " SecondsLeft=" + Summoning.getSecondsLeft(), Color.RED);
//			}
//		}

		options.status = "Use bones on altar";
		script.log.info(options.status);

		final GameObject altar = script.altarTask.getAltar();
		if (altar.valid() && !altar.inViewport()) {
			ctx.camera.turnTo(altar);
		}

		if (Random.nextBoolean() && selectBone()) {
			Item selectedItem = ctx.backpack.selectedItem();
			if (selectedItem.id() == options.offering.getId()) {
				if (altar.interact("Use", selectedItem.name() + " -> Altar") || altar.tile().matrix(ctx).interact("Use", selectedItem.name() + " -> Altar")) {
					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.players.local().animation() == ANIMATION_OFFERING;
						}
					}, 100, 20)) {
						options.TimeLastOffering.set(System.currentTimeMillis());
						options.status = "Offering bones";
					}
				} else {
					script.log.info("Couldn't interact with altar");
				}
			}
		} else if (altar.interact("Offer", "Altar")) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.players.local().animation() == ANIMATION_OFFERING;
				}
			}, 100, 20)) {
				options.TimeLastOffering.set(System.currentTimeMillis());
				options.status = "Offering bones";
			}
		}
	}

	private boolean selectBone() {
		if (ctx.hud.open(Hud.Window.BACKPACK)) {
			if (ctx.backpack.selectedItem().id() != options.offering.getId()) {
				if (ctx.backpack.itemSelected() && ctx.menu.click(Menu.filter("Cancel"))) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !ctx.backpack.itemSelected();
						}
					}, 100, 10);
				}

				for (Item item : getBackpackOffering().first()) {
					if (ctx.hud.open(Hud.Window.BACKPACK)) {
						if (!ctx.backpack.itemSelected() && ctx.backpack.scroll(item) && item.valid() && item.interact("Use")) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ctx.backpack.selectedItem().id() == options.offering.getId();
								}
							}, 250, 5);
							ctx.sleep(300);
						}
					}
				}
			}
		}

		return ctx.backpack.selectedItem().id() == options.offering.getId();
	}
}
