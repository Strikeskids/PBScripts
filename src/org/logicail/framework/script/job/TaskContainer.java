package org.logicail.framework.script.job;

import java.util.Deque;
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
	private final CopyOnWriteArrayList<JobListener> listeners = new CopyOnWriteArrayList<>();
	private final List<Container> children = new CopyOnWriteArrayList<>();
	private Container[] childrenCache = new Container[0];
	private final ThreadGroup group;
	private final ExecutorService executor;
	private final Deque<Job> jobs = new ConcurrentLinkedDeque<>();
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
		this.group = new ThreadGroup(paramThreadGroup, getClass().getName() + "_" + Integer.toHexString(hashCode()));
		this.executor = Executors.newCachedThreadPool(new ThreadPool(group));
		this.parent_container = paramTaskContainer;
	}

	public final void submit(Job job) {
		if (isShutdown()) {
			return;
		}
		job.setContainer(this);
		Future localFuture = executor.submit(createWorker(job));
		if (job instanceof Task) {
			((Task) job).future = localFuture;
		}
	}

	public final void setPaused(boolean paused) {
		if (isShutdown()) {
			return;
		}
		if (this.paused != paused) {
			this.paused = paused;
		}
		for (Container localContainer : getChildren()) {
			localContainer.setPaused(paused);
		}
	}

	public final boolean isPaused() {
		return paused;
	}

	public Job[] enumerate() {
		return jobs.toArray(new Job[jobs.size()]);
	}

	public final int getActiveCount() {
		return jobs.size();
	}

	public final Container branch() {
		TaskContainer taskContainer = new TaskContainer(group, this);
		children.add(taskContainer);
		return taskContainer;
	}

	public final Container[] getChildren() {
		int size = children.size();
		if (size > 0) {
			if (size == childrenCache.length) {
				return childrenCache;
			}
			return childrenCache = children.toArray(new Container[size]);
		}
		return new Container[0];
	}

	public final void shutdown() {
		if (!isShutdown()) {
			shutdown = true;
		}
		for (Container localContainer : getChildren()) {
			localContainer.shutdown();
		}
	}

	public final boolean isShutdown() {
		return shutdown;
	}

	public final void interrupt() {
		shutdown();
		if (!interrupted) {
			interrupted = true;
		}
		for (Container container : getChildren()) {
			container.interrupt();
		}
		for (Job job : jobs) {
			job.interrupt();
		}
	}

	public final boolean isTerminated() {
		return ((shutdown) || (interrupted)) && (getActiveCount() == 0);
	}

	private Runnable createWorker(final Job job) {
		return new Runnable() {
			public void run() {
				jobs.add(job);
				notifyListeners(job, true);
				try {
					job.work();
				} catch (Exception ignored) {
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
				jobs.remove(job);
				notifyListeners(job, false);
				job.setContainer(null);
			}
		};
	}

	private void notifyListeners(Job job, boolean started) {
		JobListener[] jobListeners = new JobListener[listeners.size()];
		listeners.toArray(jobListeners);
		for (JobListener jobListener : jobListeners)
			try {
				if (started) {
					jobListener.jobStarted(job);
				} else {
					jobListener.jobStopped(job);
				}
			} catch (Throwable ignored) {
			}
		if (parent_container != null) {
			parent_container.notifyListeners(job, started);
		}
	}

	private final class ThreadPool implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger worker;

		private ThreadPool(ThreadGroup group) {
			this.group = group;
			this.worker = new AtomicInteger(1);
		}

		public Thread newThread(Runnable paramRunnable) {
			Thread thread = new Thread(group, paramRunnable, group.getName() + "_" + worker.getAndIncrement());
			if (!thread.isDaemon()) {
				thread.setDaemon(false);
			}
			if (thread.getPriority() != 5) {
				thread.setPriority(5);
			}
			return thread;
		}
	}
}
