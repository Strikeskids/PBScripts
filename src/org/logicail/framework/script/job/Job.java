package org.logicail.framework.script.job;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.api.methods.MyMethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:15
 */
public abstract class Job extends MyMethodProvider {
	public Job(MyMethodContext ctx) {
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
