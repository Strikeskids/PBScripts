package org.logicail.framework.script.job;

import org.logicail.api.methods.MyMethodContext;

import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:17
 */
public abstract class Task extends Job {
	private final Object init_lock = new Object();
	public MyMethodContext ctx;
	protected Logger log = Logger.getLogger(getClass().getName());
	Future<?> future;
	private Thread thread;
	private Container container = null;
	private volatile boolean alive = false;
	private volatile boolean interrupted = false;

	protected Task(MyMethodContext ctx) {
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

	public abstract void execute();

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
}
