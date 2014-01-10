package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.powerbot.script.methods.Game;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:41
 */
public class AltarHouseLoading extends LogGildedAltarTask {
	public AltarHouseLoading(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "House Loading";
	}

	@Override
	public boolean isValid() {
		return ctx.game.getClientState() == Game.INDEX_MAP_LOADING || script.houseTask.isLoadingHouse();
	}

	@Override
	public void run() {
		options.status = "House Loading";
	}
}
