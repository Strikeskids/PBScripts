package org.logicail.framework.script;

import org.logicail.framework.script.job.Container;
import org.logicail.framework.script.job.TaskContainer;
import org.powerbot.script.PollingScript;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:31
 */
public abstract class ActiveScript extends PollingScript {
	private final Container container = new TaskContainer();

	public final boolean isActive()
	{
		return !this.container.isTerminated();
	}

	public final void setPaused(boolean paramBoolean)
	{
		this.container.setPaused(paramBoolean);
	}

	public final boolean isPaused()
	{
		return this.container.isPaused();
	}

	public final void shutdown()
	{
		if (!isShutdown())
		{
			this.container.shutdown();
		}
	}

	public final boolean isShutdown()
	{
		return this.container.isShutdown();
	}

	public final void stop()
	{
		this.container.interrupt();
	}

	public final Container getContainer()
	{
		return this.container;
	}
}
