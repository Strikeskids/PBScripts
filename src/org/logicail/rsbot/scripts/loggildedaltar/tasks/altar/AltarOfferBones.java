package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.OpenHouse;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 19:17
 */
public class AltarOfferBones extends AltarAbstract {
	public static final int ANIMATION_OFFERING = 3705;
	private int nextOfferingTime = Random.nextInt(4000, 6000);

	public AltarOfferBones(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Offer bones";
	}

	@Override
	public boolean isValid() {
		return AnimationMonitor.timeSinceAnimation(ANIMATION_OFFERING) > nextOfferingTime
				&& !getBackpackOffering().isEmpty()
				&& ctx.players.local().getAnimation() != ANIMATION_OFFERING;
	}

	@Override
	public void run() {
		nextOfferingTime = Random.nextGaussian(4000, 6000, 1000);

		// Check if current house has altar
		if (options.useOtherHouse && ctx.game.getClientState() == Game.INDEX_MAP_LOADED) {
			OpenHouse currentHouse = script.houseHandler.getCurrentHouse();
			if (currentHouse != null) {
				currentHouse.setHasObelisk(!script.altarTask.getMiniObelisk().isEmpty());
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
		ctx.log.info(options.status);

		for (GameObject altar : altarTask.getAltar()) {
			if (selectBone()) {
				Item selectedItem = ctx.backpack.getSelectedItem();
				if (!altar.isOnScreen()) {
					ctx.camera.turnTo(altar);
				}
				if (selectedItem.getId() == options.offering.getId() && altar.interact("Use", selectedItem.getName() + " -> Altar")) {
					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.players.local().getAnimation() == ANIMATION_OFFERING;
						}
					})) {
						AnimationMonitor.put(ANIMATION_OFFERING);
					}
				}
			}
		}
	}

	public boolean selectBone() {
		if (ctx.backpack.getSelectedItem().getId() != options.offering.getId()) {
			if (ctx.backpack.isItemSelected() && ctx.menu.click(Menu.filter("Cancel"))) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.backpack.isItemSelected();
					}
				}, 300, 8);
			}

			for (Item item : getBackpackOffering().first()) {
				if (!ctx.hud.isVisible(Hud.Window.BACKPACK) && ctx.hud.view(Hud.Window.BACKPACK)) {
					sleep(100, 500);
				}

				if (!ctx.backpack.isItemSelected() && ctx.backpack.isCollapsed() && ctx.backpack.scroll(item) && item.interact("Use")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.backpack.getSelectedItem().getId() == options.offering.getId();
						}
					}, 300, 8);
				}
			}
		}

		return ctx.backpack.getSelectedItem().getId() == options.offering.getId();
	}
}