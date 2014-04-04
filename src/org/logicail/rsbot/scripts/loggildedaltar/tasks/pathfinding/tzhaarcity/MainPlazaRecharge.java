package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.tzhaarcity;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.ItemTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.RechargeSummoning;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.rt6.Equipment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:23
 */
public class MainPlazaRecharge extends RechargeSummoning {
	private static final int RING_ID = 23643;

	public MainPlazaRecharge(LogGildedAltar script) {
		super(script, Path.TZHAAR_MAIN_PLAZA_OBELISK);
	}

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		List<BankRequiredItem> list = super.getItemsNeededFromBank();

		if (ctx.backpack.select().id(RING_ID).isEmpty() && ctx.equipment.select().id(RING_ID).isEmpty()) {
			list.add(new BankRequiredItem(1, true, Equipment.Slot.RING, RING_ID));
		}

		return list;
	}

	@Override
	public boolean valid() {
		return locationAttribute.isInLargeArea(ctx) || getItemsNeededFromBank().isEmpty();
	}

	@Override
	public void run() {
		if (!locationAttribute.isInLargeArea(ctx) || locationAttribute.getObeliskRandom(ctx).distanceTo(ctx.players.local()) > 20) {
			//LogHandler.print("Distance: " + Calculations.distanceTo(getPath().tile().getObeliskRandom()));
			doLarge();
			return;
		}

		if (!locationAttribute.isInObeliskArea(ctx)) {
			doSmall();
		} else {
			renewPoints();
		}

		sleep(300);
	}

	@Override
	public boolean doLarge() {
		options.status = "Recharge at Main Plaza";
		ItemTeleport.useItemTeleport(ctx, "Main Plaza", 12, RING_ID);
		return locationAttribute.isInLargeArea(ctx);
	}
}
