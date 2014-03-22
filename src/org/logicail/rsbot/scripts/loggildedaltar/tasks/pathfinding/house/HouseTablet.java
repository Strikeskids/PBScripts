package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.MagicTablet;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 12:26
 */
public class HouseTablet extends MagicTablet {
	public HouseTablet(LogGildedAltar script, int tabletID) {
		super(script, Path.HOME_TABLET, tabletID);
	}

	@Override
	public String toString() {
		return "HouseTablet";
	}

	@Override
	public boolean isValid() {
		return super.isValid()
				&& script.housePortal.getPortalLocation() == null;
	}

	@Override
	public void run() {
		if (!script.houseTask.setHouseTeleportMode()) {
			script.log.info("Wait for game settings to close");
			return;
		}

		final Action poll = ctx.combatBar.select().id(tabletID).poll();
		if (poll.isValid()) {
			if (ctx.combatBar.setExpanded(true) && poll.getComponent().interact("Break")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.game.getClientState() == Game.INDEX_MAP_LOADED && ctx.players.local().getAnimation() == -1 && (script.houseTask.isInHouse() || script.housePortal.getPortalLocation() != null);
					}
				});
				return;
			}
		}

		final Item tablet = ctx.backpack.select().id(tabletID).poll();
		// Move the tablet to 3 slot (after marrentil)
			/*final int index = ctx.backpack.indexOf(tabletID);
			final Item itemAt = ctx.backpack.getItemAt(4);

			if (index >= 0 && index != 3 && itemAt == null || !itemAt.getName().contains("port")) {
				ctx.backpack.dragItem(tabletID, 3);
				Waiting.waitFor(2000, new Condition() {
					@Override
					public boolean validate() {
						return index != Inventory.indexOf(tabletID);
					}
				});
				tablet = Inventory.getItem(tabletID);
			}*/
		if (ctx.hud.view(Hud.Window.BACKPACK)) {
			if (tablet.isValid()) {
				sleep(100, 350);
				if (!script.houseTask.isInHouse() && ctx.game.getClientState() == Game.INDEX_MAP_LOADED && tablet.interact("Break")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.game.getClientState() == Game.INDEX_MAP_LOADED && ctx.players.local().getAnimation() == -1 && (script.houseTask.isInHouse() || script.housePortal.getPortalLocation() != null);
						}
					});
				}
			}
		}
	}
}
