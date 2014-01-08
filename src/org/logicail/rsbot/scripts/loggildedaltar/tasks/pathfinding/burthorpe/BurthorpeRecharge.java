package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.burthorpe;

import org.logicail.rsbot.scripts.framework.context.providers.Lodestones;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.RechargeSummoning;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 19:08
 */
public class BurthorpeRecharge extends RechargeSummoning {
	public BurthorpeRecharge(LogGildedAltar script) {
		super(script, Path.BURTHORPE_OBELISK, new Tile(2891, 3526, 0), new Tile(2899, 3526, 0), new Tile(2899, 3520, 0), new Tile(2899, 3515, 0),
				new Tile(2899, 3510, 0), new Tile(2899, 3508, 0), new Tile(2906, 3508, 0),
				new Tile(2911, 3508, 0), new Tile(2915, 3505, 0), new Tile(2916, 3500, 0),
				new Tile(2919, 3496, 0), new Tile(2922, 3492, 0), new Tile(2922, 3489, 0),
				new Tile(2922, 3484, 0), new Tile(2921, 3481, 0), new Tile(2920, 3476, 0),
				new Tile(2922, 3472, 0), new Tile(2923, 3466, 0), new Tile(2923, 3461, 0),
				new Tile(2922, 3456, 0), new Tile(2922, 3453, 0), new Tile(2922, 3449, 0),
				new Tile(2923, 3448, 0), new Tile(2926, 3448, 0), new Tile(2928, 3448, 0));
	}

	@Override
	public boolean isValid() {
		return locationAttribute.isInLargeArea(ctx) || Lodestones.Lodestone.BURTHORPE.isUnlocked(ctx);
	}

	@Override
	public void run() {
		options.status = "Burthorpe rechange summoning";
		if (!locationAttribute.isInLargeArea(ctx)) {
			ctx.lodestones.teleport(Lodestones.Lodestone.BURTHORPE);
		} else {
			if (!locationAttribute.isInObeliskArea(ctx)) {
				if (doSmall()) {
					sleep(400, 1300);
				}
			} else {
				renewPoints();
			}
		}
	}
}