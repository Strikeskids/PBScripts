package org.logicail.framework.node;

import java.util.Collections;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 21/06/13
 * Time: 21:25
 */
public class JobContainer {
	private final PriorityBlockingQueue<Job> queue = new PriorityBlockingQueue<>();

	public JobContainer(Job... jobs) {
		submit(jobs);
	}

	public void submit(Job... jobs) {
		Collections.addAll(queue, jobs);
	}

	public void revoke(Job... jobs) {
		for (Job job : jobs) {
			queue.remove(job);
		}
	}

	public Job poll() {
		for (Job job : queue) {
			if (!job.activate()) {
				continue;
			}
			return job;
		}
		return null;
	}


}
