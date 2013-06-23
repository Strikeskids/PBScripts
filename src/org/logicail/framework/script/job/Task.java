package org.logicail.framework.script.job;

import org.powerbot.script.methods.MethodContext;

import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:17
 */
public abstract class Task extends Job {
	private Thread thread;
	Future<?> future;
	private Container container = null;
	private volatile boolean alive = false;
	private volatile boolean interrupted = false;
	private final Object init_lock = new Object();

	protected Task(MethodContext ctx) {
		super(ctx);
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
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		alive = false;
	}

	public abstract void execute();

	public final boolean join() {
		if ((future == null) || (future.isDone()))
			return true;
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

	public void setContainer(Container container) {
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}
}
