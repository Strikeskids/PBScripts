package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.tzhaarcity;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.ItemTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.methods.Equipment;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:22
 */
public class FightCavesBank extends ItemTeleport {
	public FightCavesBank(LogGildedAltar script) {
		super(script, Path.FIGHT_CAVES, "Fight Caves", Equipment.Slot.RING, 23643);
	}

	@Override
	public void run() {
		if (!locationAttribute.isInLargeArea(ctx) || locationAttribute.getSmallRandom(ctx).distanceTo(ctx.players.local()) > 20) {
			doLarge();
		} else if (!locationAttribute.isInSmallArea(ctx)) {
			doSmall();
		}
	}
}
