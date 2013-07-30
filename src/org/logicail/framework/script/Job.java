package org.logicail.framework.script;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:15
 */
public abstract class Job extends LogicailMethodProvider {
	public Job(LogicailMethodContext ctx) {
		super(ctx);
	}

	public abstract void work();

	public abstract boolean join();

	public abstract boolean isAlive();

	public abstract void interrupt();

	public abstract boolean isInterrupted();

	public abstract void setContainer(Container container);

	public abstract Container getContainer();
}
