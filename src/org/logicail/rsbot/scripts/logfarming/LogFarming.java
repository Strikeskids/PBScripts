package org.logicail.rsbot.scripts.logfarming;

import org.logicail.rsbot.scripts.framework.LogicailGraphScript;
import org.powerbot.script.Script;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 21/05/2014
 * Time: 20:10
 */
@Script.Manifest(name = "Log Farming", description = "...", properties = "hidden=true")
public class LogFarming extends LogicailGraphScript {
	public LogFarming() {
		log.info("Log Farming" + ctx.toString());

	}
}
