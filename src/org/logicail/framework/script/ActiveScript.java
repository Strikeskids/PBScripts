package org.logicail.framework.script;

import org.logicail.framework.script.job.Container;
import org.logicail.framework.script.job.Job;
import org.logicail.framework.script.job.TaskContainer;
import org.powerbot.script.PollingScript;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:31
 */
public abstract class ActiveScript extends PollingScript {

	@Override
	public void suspend() {
		super.suspend();
		setPaused(true);
	}

	@Override
	public void resume() {
		super.resume();
		setPaused(false);
	}

	@Override
	public void start() {
		super.start();
		shutdown();
	}

	private final Container container = new TaskContainer();

	public final boolean isActive() {
		return !container.isTerminated();
	}

	public final void setPaused(boolean paramBoolean) {
		container.setPaused(paramBoolean);
	}

	public final boolean isPaused() {
		return container.isPaused();
	}

	public final void shutdown() {
		if (!isShutdown()) {
			container.shutdown();
		}
	}

	public final boolean isShutdown() {
		return container.isShutdown();
	}

	public final void stop() {
		container.interrupt();
	}

	public final Container getContainer() {
		return container;
	}

	public final void submit(Job job) {
		container.submit(job);
	}
}
