package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 18:00
 */
public class Running extends GraphScript.Action<IClientContext> {
	private static final String CLICK_ON_THE_RUN_BUTTON_NOW = "click on the run button now.";

	@Override
	public String toString() {
		return "Running";
	}

	public Running(IClientContext ctx) {
		super(ctx);
	}

	@Override
	public boolean valid() {
		return ctx.chat.visible(CLICK_ON_THE_RUN_BUTTON_NOW);
	}

	@Override
	public void run() {
		if (ctx.chat.visible(CLICK_ON_THE_RUN_BUTTON_NOW)) {
			ctx.movement.running(true);
			Condition.sleep(500);
		}
	}
}
