package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.powerbot.script.rt6.Game;

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
	public boolean valid() {
		return ctx.game.clientState() == Game.INDEX_MAP_LOADING || script.houseTask.isLoadingHouse();
	}

	@Override
	public void run() {
		options.status = "House Loading";
	}
}
