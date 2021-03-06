package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.castlewars;

import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.RechargeSummoning;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.TilePath;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 18:50
 */
public class CastleWarsRecharge extends RechargeSummoning {
	public CastleWarsRecharge(LogGildedAltar script) {
		super(script, Path.CASTLE_WARS_OBELISK);
	}

	@Override
	public boolean doSmall() {
		final Tile destination = locationAttribute.getObeliskRandom(ctx);
		final TilePath path = new TilePath(ctx, new Tile[]{new Tile(2445, 3084, 0), new Tile(2446, 3090, 0), new Tile(2458, 3090, 0), destination}).randomize(1, 1);
		final Tile next = path.next();

		if (path.traverse()) {
			if (next != null) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return next.distanceTo(ctx.players.local()) < 5;
					}
				}, Random.nextInt(400, 600), 5);
			} else {
				ctx.sleep(800);
			}
			return true;
		} else {
			script.log.info("Can not walk to obelisk");
			return false;
		}
	}
}
