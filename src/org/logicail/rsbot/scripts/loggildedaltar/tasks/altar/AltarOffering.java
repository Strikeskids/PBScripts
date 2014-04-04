package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 20:02
 */
public class AltarOffering extends LogGildedAltarTask {
	public AltarOffering(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Offering";
	}

	@Override
	public boolean valid() {
		return true;
	}

	@Override
	public void run() {
		options.status = "Offering bones";
	}
}
