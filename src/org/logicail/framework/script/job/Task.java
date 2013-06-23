package org.logicail.framework.script.job;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

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
		synchronized (this.init_lock) {
			if (this.alive) {
				throw new RuntimeException("Task RuntimeException");
			}
			this.alive = true;
		}
		this.interrupted = false;
		this.thread = Thread.currentThread();
		try {
			execute();
		} catch (ThreadDeath ignored) {
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		this.alive = false;
	}

	public abstract void execute();

	public final boolean join() {
		if ((this.future == null) || (this.future.isDone()))
			return true;
		try {
			this.future.get();
		} catch (Throwable ignored) {
		}
		return this.future.isDone();
	}

	public final boolean isAlive() {
		return this.alive;
	}

	public final void interrupt() {
		this.interrupted = true;
		if (this.thread != null)
			try {
				if (!this.thread.isInterrupted())
					this.thread.interrupt();
			} catch (Throwable ignored) {
			}
	}

	public final boolean isInterrupted() {
		return this.interrupted;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public Container getContainer() {
		return this.container;
	}
}
