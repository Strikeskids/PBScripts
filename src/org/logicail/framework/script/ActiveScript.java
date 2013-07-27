package org.logicail.framework.script;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.framework.script.job.Container;
import org.logicail.framework.script.job.Job;
import org.logicail.framework.script.job.TaskContainer;
import org.logicail.framework.script.job.state.Tree;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:31
 */
public abstract class ActiveScript extends PollingScript {
	private final Container container = new TaskContainer();
	public MyMethodContext ctx;
	protected Tree tree = null;

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

		ctx = new MyMethodContext(super.ctx);
	}

	@Override
	public void setContext(MethodContext mc) {
		ctx.init(mc);
	}

	public final boolean isActive() {
		return !container.isTerminated();
	}

	public final boolean isPaused() {
		return container.isPaused();
	}

	public final void setPaused(boolean paused) {
		container.setPaused(paused);
	}

	/**
	 * Graceful shutdown
	 */
	public final void shutdown() {
		if (!isShutdown()) {
			log.info("Shutdown");
			container.shutdown();
		}
	}

	public final boolean isShutdown() {
		return container.isShutdown();
	}

	/**
	 * Force stop
	 */
	public void forceStop() {
		container.interrupt();
	}

	public final Container getContainer() {
		return container;
	}

	public final void submit(Job job) {
		container.submit(job);
	}
}
