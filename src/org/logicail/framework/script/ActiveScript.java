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
	protected ActiveScript() {
		getExecQueue(State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				setPaused(true);
			}
		});
		getExecQueue(State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				setPaused(false);
			}
		});
		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				shutdown();
			}
		});
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
}
