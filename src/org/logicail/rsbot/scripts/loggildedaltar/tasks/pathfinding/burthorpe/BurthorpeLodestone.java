package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.burthorpe;

import org.logicail.rsbot.scripts.framework.context.providers.Lodestones;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LodestoneTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TilePath;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 19:04
 */
public class BurthorpeLodestone extends LodestoneTeleport {
	private final TilePath PATH_TO_BANK = new TilePath(ctx, new Tile[]{new Tile(2898, 3540, 0), new Tile(2898, 3537, 0), new Tile(2898, 3534, 0),
			new Tile(2898, 3531, 0), new Tile(2896, 3527, 0), new Tile(2893, 3527, 0),
			new Tile(2891, 3527, 0)});
	public BurthorpeLodestone(LogGildedAltar script) {
		super(script, Path.BURTHORPE_LODESTONE, Lodestones.Lodestone.BURTHORPE);
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
			PATH_TO_BANK.randomize(2, 2);
			if (PATH_TO_BANK.traverse() || ctx.movement.findPath(locationAttribute.getSmallRandom(ctx)).traverse()) {
				sleep(250, 1400);
			}
		}
	}
}
