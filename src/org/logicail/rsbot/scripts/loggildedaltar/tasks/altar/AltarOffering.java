package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 20:02
 */
public class AltarOffering extends AltarAbstract {
	public AltarOffering(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Offering";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void run() {
		options.TimeLastOffering = System.currentTimeMillis();
		options.status = "Offering bones";
	}
}