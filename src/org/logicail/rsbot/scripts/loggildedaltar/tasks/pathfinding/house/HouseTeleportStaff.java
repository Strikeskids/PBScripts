package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house;

import com.sk.methods.action.magic.Spellbook;
import com.sk.methods.action.magic.StandardSpell;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Equipment;
import org.powerbot.script.rt6.Game;
import org.powerbot.script.rt6.Player;

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
	public boolean valid() {
		return script.housePortal.getPortalLocation() == null
				&& getItemsNeededFromBank().isEmpty()
				&& Spellbook.STANDARD.isOpen(ctx);
	}

	@Override
	public void run() {
		if (!script.houseTask.setHouseTeleportMode()) {
			script.log.info("Wait for game settings to close");
			return;
		}

		if (ctx.equipment.select().id(staff).isEmpty() && !ctx.backpack.select().id(staff).isEmpty()) {
			script.log.info("Equip");
			ctx.equipment.equip(staff);
		}

		if (!ctx.equipment.select().id(staff).isEmpty()) {
			if (!script.houseTask.isInHouse() && ctx.game.clientState() == Game.INDEX_MAP_LOADED && ctx.magic.cast(StandardSpell.HOUSE_TELEPORT)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						final Player local = ctx.players.local();
						return (script.ctx.controller().isSuspended() || script.ctx.controller().isStopping()) || (local != null && local.animation() == -1 && (script.houseTask.isInHouse() || script.housePortal.getPortalLocation() != null));
					}
				}, Random.nextInt(550, 650), Random.nextInt(20, 60));
			}
		}
	}
}
