package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.burthorpe;

import org.logicail.rsbot.scripts.framework.context.providers.ILodestone;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LodestoneTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.TilePath;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 19:04
 */
public class BurthorpeLodestone extends LodestoneTeleport {
	private final TilePath pathToBank = new TilePath(ctx, new Tile[]{new Tile(2898, 3540, 0), new Tile(2898, 3537, 0), new Tile(2898, 3534, 0),
			new Tile(2898, 3531, 0), new Tile(2896, 3527, 0), new Tile(2893, 3527, 0),
			new Tile(2891, 3527, 0)});

	public BurthorpeLodestone(LogGildedAltar script) {
		super(script, Path.BURTHORPE_LODESTONE, ILodestone.Lodestone.BURTHORPE);
	}

	@Override
	public void run() {
		options.status = "Burthorpe Lodestone";

		if (!locationAttribute.isInLargeArea(ctx)) {
			if (ctx.lodestones.teleport(lodestone)) {
				timeLastTeleport = System.currentTimeMillis();
			}
		}

		if (locationAttribute.isInLargeArea(ctx) && !locationAttribute.isInSmallArea(ctx)) {
			options.status = "Walking";
			pathToBank.randomize(2, 2);
			if (pathToBank.traverse() || ctx.movement.findPath(locationAttribute.getSmallRandom(ctx)).traverse()) {
				sleep(500);
			}
		}
	}
}
