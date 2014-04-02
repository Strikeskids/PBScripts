package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.rt6.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/09/12
 * Time: 23:21
 */
public class RunicStaff extends HouseTeleportStaff {
	public RunicStaff(LogGildedAltar script) {
		super(script, Path.HOME_TELEPORT_RUNIC_STAFF, 24200, 24203);
	}

	// Runic, 24198 no spell, 24199 no charge, 24200 charged

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		List<BankRequiredItem> list = new ArrayList<BankRequiredItem>();

		if (ctx.equipment.select().id(staff).isEmpty() && ctx.backpack.select().id(staff).isEmpty()) {
			list.add(new BankRequiredItem(1, true, Equipment.Slot.MAIN_HAND, staff));
		}

		return list;
	}
}
