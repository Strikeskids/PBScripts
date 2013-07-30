package org.logicail.framework.script.state;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:31
 */
public abstract class Node extends LogicailMethodProvider {
	public LogicailMethodContext ctx;

	public Node(LogicailMethodContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}

	public abstract boolean activate();

	public abstract void execute();
}
