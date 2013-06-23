package org.logicail.framework.script.job;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:21
 */
public class TaskContainer implements Container {
	private final CopyOnWriteArrayList<JobListener> listeners = new CopyOnWriteArrayList();
	private final List<Container> children = new CopyOnWriteArrayList();
	private Container[] childrenCache = new Container[0];
	private final ThreadGroup group;
	private final ExecutorService executor;
	private Deque<Job> jobs = new ConcurrentLinkedDeque();
	private volatile boolean paused = false;
	private volatile boolean shutdown = false;
	private volatile boolean interrupted = false;
	private final TaskContainer parent_container;

	public TaskContainer() {
		this(Thread.currentThread().getThreadGroup());
	}

	public TaskContainer(ThreadGroup paramThreadGroup) {
		this(paramThreadGroup, null);
	}

	private TaskContainer(ThreadGroup paramThreadGroup, TaskContainer paramTaskContainer) {
		this.group = new ThreadGroup(paramThreadGroup, getClass().getName() + "_" + hashCode());
		this.executor = Executors.newCachedThreadPool(new ThreadPool(this.group));
		this.parent_container = paramTaskContainer;
	}

	public final void submit(Job job) {
		if (isShutdown())
			return;
		job.setContainer(this);
		Future localFuture = this.executor.submit(createWorker(job));
		if ((localFuture != null) && ((job instanceof Task))) {
			((Task) job).future = localFuture;
		}
	}

	public final void setPaused(boolean paused) {
		if (isShutdown())
			return;
		if (this.paused != paused)
			this.paused = paused;
		for (Container localContainer : getChildren()) {
			localContainer.setPaused(paused);
		}
	}

	public final boolean isPaused() {
		return this.paused;
	}

	public Job[] enumerate() {
		return this.jobs.toArray(new Job[this.jobs.size()]);
	}

	public final int getActiveCount() {
		return this.jobs.size();
	}

	public final Container branch() {
		TaskContainer localTaskContainer = new TaskContainer(this.group, this);
		this.children.add(localTaskContainer);
		return localTaskContainer;
	}

	public final Container[] getChildren() {
		int i;
		if ((i = this.children.size()) > 0) {
			if (i == this.childrenCache.length)
				return this.childrenCache;
			return this.childrenCache = this.children.toArray(new Container[i]);
		}
		return new Container[0];
	}

	public final void shutdown() {
		if (!isShutdown()) {
			this.shutdown = true;
		}
		for (Container localContainer : getChildren()) {
			localContainer.shutdown();
		}
	}

	public final boolean isShutdown() {
		return this.shutdown;
	}

	public final void interrupt() {
		shutdown();
		if (!this.interrupted) {
			this.interrupted = true;
		}
		for (Container container : getChildren()) {
			container.interrupt();
		}
		Iterator<Job> iterator = this.jobs.iterator();
		while (iterator.hasNext()) {
			Job localJob = iterator.next();
			localJob.interrupt();
		}
	}

	public final boolean isTerminated() {
		return ((this.shutdown) || (this.interrupted)) && (getActiveCount() == 0);
	}

	private Runnable createWorker(final Job paramJob) {
		return new Runnable() {
			public void run() {
				TaskContainer.this.jobs.add(paramJob);
				TaskContainer.this.notifyListeners(paramJob, true);
				try {
					paramJob.work();
				} catch (Exception localException) {
				} catch (Throwable localThrowable) {
					localThrowable.printStackTrace();
				}
				TaskContainer.this.jobs.remove(paramJob);
				TaskContainer.this.notifyListeners(paramJob, false);
				paramJob.setContainer(null);
			}
		};
	}

	private void notifyListeners(Job paramJob, boolean paramBoolean) {
		JobListener[] arrayOfJobListener1 = new JobListener[this.listeners.size()];
		this.listeners.toArray(arrayOfJobListener1);
		for (JobListener localJobListener : arrayOfJobListener1)
			try {
				if (paramBoolean)
					localJobListener.jobStarted(paramJob);
				else
					localJobListener.jobStopped(paramJob);
			} catch (Throwable localThrowable) {
			}
		if (this.parent_container != null)
			this.parent_container.notifyListeners(paramJob, paramBoolean);
	}

	private final class ThreadPool implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger worker;

		private ThreadPool(ThreadGroup group) {
			this.group = group;
			this.worker = new AtomicInteger(1);
		}

		public Thread newThread(Runnable paramRunnable) {
			Thread localThread = new Thread(this.group, paramRunnable, this.group.getName() + "_" + this.worker.getAndIncrement());
			if (!localThread.isDaemon()) {
				localThread.setDaemon(false);
			}
			if (localThread.getPriority() != 5) {
				localThread.setPriority(5);
			}
			return localThread;
		}
	}
}
