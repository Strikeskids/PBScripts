package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.daemonheim;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.ItemTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.util.WaitForAnimationToFinish;
import org.powerbot.script.methods.Equipment;
import org.powerbot.script.util.Condition;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:11
 */
public class DaemonheimKinship extends ItemTeleport {
	public DaemonheimKinship(LogGildedAltar script) {
		super(script, Path.DAEMONHEIM_RING_OF_KINSHIP, "Teleport to Daemonheim", Equipment.Slot.RING, 15707, 18817, 18818, 18819, 18820, 18821, 18822, 18823, 18824, 18825, 18826, 18827, 18828);
	}

	@Override
	public boolean doLarge() {
		if (super.doLarge()) {
			Condition.wait(new WaitForAnimationToFinish(ctx, 13654), 600, 20);
			return true;
		}
		return false;
	}
}
