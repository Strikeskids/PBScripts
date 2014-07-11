package org.logicail.rsbot.scripts.rt4.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/07/2014
 * Time: 15:09
 */
public class LoggedIn extends GraphScript.Action<IClientContext> {
	public LoggedIn(IClientContext ctx) {
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

	@Override
	public void run() {

	}

	public void add(GraphScript.Action<IClientContext> action) {
		chain.add(action);
	}
}
