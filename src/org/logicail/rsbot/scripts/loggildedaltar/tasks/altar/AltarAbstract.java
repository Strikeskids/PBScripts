package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.AltarTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:44
 */
public abstract class AltarAbstract extends LogGildedAltarTask {
	final AltarTask altarTask;

	protected AltarAbstract(LogGildedAltar script) {
		super(script);
		altarTask = script.altarTask;
	}
}
