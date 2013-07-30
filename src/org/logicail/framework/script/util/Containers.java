package org.logicail.framework.script.util;

import org.logicail.framework.script.Container;
import org.logicail.framework.script.Job;
import org.logicail.framework.script.JobListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 30/06/13
 * Time: 16:46
 */
public class Containers {
	private static final Object lock = new Object();

	public static boolean awaitTermination(Container container) {
		return awaitTermination(container, 0);
	}

	public static boolean awaitTermination(Container container, int timeout) {
		if (container.isTerminated()) {
			return true;
		}
		JobListener jobListener = new JobListener() {
			public void jobStarted(Job paramAnonymousJob) {
			}

			public void jobStopped(Job paramAnonymousJob) {
				synchronized (this) {
					notify();
				}
			}
		};
		container.addListener(jobListener);
		long end = System.currentTimeMillis() + timeout;
		while (((timeout == 0) || (System.currentTimeMillis() < end)) && (!container.isTerminated())) {
			long timeLeft = end - System.currentTimeMillis();
			if (timeLeft <= 0) {
				break;
			}
			synchronized (lock) {
				try {
					lock.wait(timeLeft);
				} catch (InterruptedException localInterruptedException) {
					container.removeListener(jobListener);
					throw new ThreadDeath();
				}
			}
		}
		container.removeListener(jobListener);
		return container.isTerminated();
	}
}