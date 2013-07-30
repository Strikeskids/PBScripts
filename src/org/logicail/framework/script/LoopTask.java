package org.logicail.framework.script;

import org.logicail.api.methods.LogicailMethodContext;

import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/07/13
 * Time: 16:43
 */
public abstract class LoopTask extends Job {
	private final Object init_lock = new Object();
	public LogicailMethodContext ctx;
	protected Logger log = Logger.getLogger(getClass().getName());
	Future<?> future;
	private boolean paused;
	private Thread thread;
	private Container container = null;
	private volatile boolean alive = false;
	private volatile boolean interrupted = false;

	public LoopTask(LogicailMethodContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}

	public final void work() {
		synchronized (init_lock) {
			if (alive) {
				throw new RuntimeException("Task RuntimeException");
			}
			alive = true;
		}
		interrupted = false;
		thread = Thread.currentThread();
		try {
			execute();
		} catch (ThreadDeath ignored) {
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		alive = false;
	}

	public final boolean join() {
		if (future == null || future.isDone()) {
			return true;
		}
		try {
			future.get();
		} catch (Throwable ignored) {
		}
		return future.isDone();
	}

	public final boolean isAlive() {
		return alive;
	}

	public final void interrupt() {
		interrupted = true;
		if (thread != null) {
			try {
				if (!thread.isInterrupted()) {
					thread.interrupt();
				}
			} catch (Throwable ignored) {
			}
		}
	}

	public final boolean isInterrupted() {
		return interrupted;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public final void execute() {
		Container container = getContainer();
		while (!container.isShutdown()) {
			if (container.isPaused()) {
				paused = true;
				sleep(1000);
			} else {
				paused = false;

				int delay;
				try {
					delay = loop();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
					delay = -1;
				}

				if (delay >= 0) {
					sleep(delay);
				} else if (delay == -1) {
					break;
				}
			}
		}
	}

	public abstract int loop();

	public boolean isPaused() {
		return paused;
	}
}
