package org.logicail.framework.script.job.state;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.framework.script.job.Task;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:31
 */
public abstract class Node extends Task {
	public Node(MyMethodContext ctx) {
		super(ctx);
	}

	public abstract boolean activate();
}
