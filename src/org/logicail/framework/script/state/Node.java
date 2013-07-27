package org.logicail.framework.script.state;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.api.methods.MyMethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:31
 */
public abstract class Node extends MyMethodProvider {
	public MyMethodContext ctx;

	public Node(MyMethodContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}

	public abstract boolean activate();

	public abstract void execute();
}
