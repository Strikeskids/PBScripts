package org.logicail.framework.script.job.state;

import org.logicail.framework.script.job.Task;
import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:31
 */
public abstract class Node extends Task
{
	public Node(MethodContext ctx) {
		super(ctx);
	}

	public abstract boolean activate();
}
