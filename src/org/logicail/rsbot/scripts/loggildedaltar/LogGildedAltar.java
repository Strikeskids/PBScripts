package org.logicail.rsbot.scripts.loggildedaltar;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.util.LinkedProperties;
import org.powerbot.script.Manifest;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:49
 */
@Manifest(
		name = "LogGildedAltar",
		description = "Train prayer at your own or someone else's gilded altar",
		version = 6,
		hidden = true,
		authors = {"Logicail"}
)
public class LogGildedAltar extends LogicailScript {
	@Override
	public LinkedProperties getPaintInfo() {
		return null;
	}

	@Override
	public void start() {
		ctx.submit(new AnimationMonitor(ctx));
	}
}
