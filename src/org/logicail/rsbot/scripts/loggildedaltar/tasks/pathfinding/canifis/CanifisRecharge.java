package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.canifis;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.RechargeSummoning;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 15:30
 */
public class CanifisRecharge extends RechargeSummoning {
	public static final Tile[] PATH_TO_OBELISK = {new Tile(3497, 3482, 0), new Tile(3493, 3482, 0), new Tile(3488, 3482, 0),
			new Tile(3485, 3480, 0), new Tile(3484, 3478, 0), new Tile(3481, 3477, 0),
			new Tile(3478, 3476, 0), new Tile(3475, 3476, 0), new Tile(3470, 3475, 0),
			new Tile(3466, 3476, 0), new Tile(3462, 3477, 0), new Tile(3461, 3479, 0),
			new Tile(3460, 3483, 0), new Tile(3457, 3485, 0), new Tile(3453, 3486, 0),
			new Tile(3452, 3489, 0)};

	public CanifisRecharge(LogGildedAltar script) {
		super(script, Path.CANIFIS_OBELISK, PATH_TO_OBELISK);
	}

	@Override
	public boolean doSmall() {
		if (KharyrllPortalRoom.CANIFIS_PUB.contains(ctx.players.local())) {
			for (GameObject door : ctx.objects.select().id(KharyrllPortalRoom.CLOSED_DOOR).nearest().first()) {
				if (DoorOpener.open(ctx, door)) {
					sleep(250);
				}
			}
		}
		return super.doSmall();
	}

	@Override
	public void run() {
		options.status = "Canifis rechange summoning";
		//Waiting.waitForLoaded();
		super.run();
	}
}
