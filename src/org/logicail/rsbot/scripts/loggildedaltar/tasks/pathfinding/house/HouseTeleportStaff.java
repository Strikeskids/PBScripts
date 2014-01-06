package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house;

import com.sk.methods.action.magic.Spellbook;
import com.sk.methods.action.magic.StandardSpell;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.methods.Equipment;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/09/12
 * Time: 23:15
 */
public class HouseTeleportStaff extends NodePath {
	private static final int AIR_RUNE = 556;
	private static final int LAW_RUNE = 563;

	final int[] staff;

	public HouseTeleportStaff(LogGildedAltar script, Path path, int... staffids) {
		super(script, path);
		staff = staffids;
	}

	@Override
	public boolean doLarge() {
		return false;
	}

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		List<BankRequiredItem> list = new ArrayList<BankRequiredItem>();

		if (ctx.backpack.select().id(AIR_RUNE).isEmpty()) {
			list.add(new BankRequiredItem(0, false, null, AIR_RUNE));
		}

		if (ctx.backpack.select().id(LAW_RUNE).isEmpty()) {
			list.add(new BankRequiredItem(0, false, null, LAW_RUNE));
		}

		if (ctx.equipment.select().id(staff).isEmpty() && ctx.backpack.select().id(staff).isEmpty()) {
			list.add(new BankRequiredItem(1, true, Equipment.Slot.MAIN_HAND, staff));
		}

		return list;
	}

	@Override
	public boolean isValid() {
		return script.housePortal.getPortalLocation() == null
				&& getItemsNeededFromBank().isEmpty()
				&& Spellbook.STANDARD.isOpen(ctx);
	}

	@Override
	public void run() {
		try {
			script.houseTask.setHouseTeleportMode();
		} catch (Exception ignored) {
		}

		if (ctx.equipment.select().id(staff).isEmpty() && !ctx.backpack.select().id(staff).isEmpty()) {
			ctx.equipment.equip(staff);
		}

		if (!script.houseTask.isInHouse() && ctx.game.getClientState() == Game.INDEX_MAP_LOADED && ctx.magic.cast(StandardSpell.HOUSE_TELEPORT)) {
			sleep(200, 800);
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					if (!ctx.hud.isVisible(Hud.Window.BACKPACK)) {
						ctx.hud.view(Hud.Window.BACKPACK);
					}
					final Player local = ctx.players.local();
					return !ctx.isPaused() && !ctx.isShutdown() && local != null && local.getAnimation() == -1 && (script.houseTask.isInHouse() || script.housePortal.getPortalLocation() != null);
				}
			}, 600, 54);
		}
	}
}