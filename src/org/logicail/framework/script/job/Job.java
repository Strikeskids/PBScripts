package org.logicail.framework.script.job;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:15
 */
public abstract class Job extends MethodProvider {
	public Job(MethodContext ctx) {
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
