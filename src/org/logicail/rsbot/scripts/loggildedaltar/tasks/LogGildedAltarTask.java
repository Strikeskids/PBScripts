package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarOptions;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:10
 */
public abstract class LogGildedAltarTask extends Task<LogGildedAltar> {
	protected final LogGildedAltarOptions options;

	public LogGildedAltarTask(LogGildedAltar script) {
		super(script);
		options = script.options;
	}
}
