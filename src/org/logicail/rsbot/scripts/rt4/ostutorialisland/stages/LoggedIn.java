package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/07/2014
 * Time: 15:09
 */
public class LoggedIn extends GraphScript.Action<RT4ClientContext> {
	public LoggedIn(RT4ClientContext ctx) {
		super(ctx);
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean valid() {
		return ctx.game.loggedIn();
	}

	private long nextsleep = 0;

	@Override
	public void run() {
		if (System.currentTimeMillis() > nextsleep) {
			nextsleep = System.currentTimeMillis() + Random.nextInt(5000, 45000);
			Condition.sleep(Random.nextInt(50, 1500));
		}
	}

	public void add(GraphScript.Action<RT4ClientContext> action) {
		chain.add(action);
	}
}
