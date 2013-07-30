package org.logicail.framework.script;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.providers.AnimationHistory;
import org.logicail.framework.script.state.Tree;
import org.powerbot.script.PollingScript;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/07/13
 * Time: 16:51
 */
public abstract class ActiveScript extends PollingScript {
	private final Container container = new TaskContainer();
	public LogicailMethodContext ctx;
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


		ctx = new LogicailMethodContext(this, super.ctx);

		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				submit(new AnimationHistory(ctx));
			}
		});
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

	public final void setPaused(boolean paused) {
		container.setPaused(paused);
	}

	public final void submit(LoopTask job) {
		container.submit(job);
	}
}
