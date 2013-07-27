package org.logicail.framework.script;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:16
 */
public abstract interface Container {
	public abstract void submit(LoopTask job);

	public abstract void setPaused(boolean paused);

	public abstract boolean isPaused();

	public abstract Job[] enumerate();

	public abstract int getActiveCount();

	public abstract Container branch();

	public abstract Container[] getChildren();

	public abstract void shutdown();

	public abstract boolean isShutdown();

	public abstract void interrupt();

	public abstract boolean isTerminated();

	public abstract void addListener(JobListener jobListener);

	public abstract void removeListener(JobListener jobListener);
}
