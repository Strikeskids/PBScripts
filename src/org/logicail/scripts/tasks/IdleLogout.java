package org.logicail.scripts.tasks;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.framework.script.job.state.Node;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:01
 */
public class IdleLogout extends Node {
	public IdleLogout(MyMethodContext ctx) {
		super(ctx);
	}

	// TODO: on resume reset timer
	@Override
	public boolean activate() {
		return false;
	}

	@Override
	public void execute() {
	}
}
