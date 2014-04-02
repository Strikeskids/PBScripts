package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.powerbot.script.rt6.Game;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:43
 */
public class AltarNoAltar extends LogGildedAltarTask {
	public AltarNoAltar(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "No altar";
	}

	@Override
	public void run() {
		if (ctx.game.clientState() == Game.INDEX_MAP_LOADED) {
			ctx.sleep(2000);
			if (isValid()) {
				options.status = "House does not have a gilded altar";
				if (options.useOtherHouse.get()) {
					script.houseHandler.currentHouseFailed();

					if (!options.detectHouses.get() && script.houseHandler.getOpenHouse() == null) {
						ctx.stop("House does not have a gilded altar");
					} else {
						script.leaveHouse.leaveHouse.set(true);
					}
				}
			}
		}
	}

	@Override
	public boolean isValid() {
		return !script.altarTask.getAltar().valid();
	}
}
