package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.taverley;

import org.logicail.rsbot.scripts.framework.context.providers.Lodestones;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LodestoneTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/01/14
 * Time: 15:13
 */
public class TaverleyLodestone extends LodestoneTeleport {
	public TaverleyLodestone(LogGildedAltar script) {
		super(script, Path.TAVERLEY_LODESTONE, Lodestones.Lodestone.TAVERLEY);
	}

	@Override
	public boolean doLarge() {
		if (!script.houseTask.isInHouse() && !locationAttribute.isInLargeArea(ctx))
			if (ctx.lodestones.teleport(lodestone, true)) {
				timeLastTeleport = System.currentTimeMillis();

				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return lodestone.getLocation().getMatrix(ctx).isOnMap() || script.houseTask.isInHouse();
					}
				}, Random.nextInt(120, 160), 100)) {
					sleep(250, 1250);
					if (script.houseTask.isInHouse()) {
						options.status = ("Interrupting lodestone teleport");
						ctx.players.local().getLocation().randomize(2, 2).getMatrix(ctx).interact("Walk here");
					}
				}

				return true;
			}
		return false;
	}
}