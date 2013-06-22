package org.logicail.framework.node;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 19/06/13
 * Time: 17:19
 */
public abstract class Job extends MethodProvider implements Comparable<Job> {
	public Job(MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Delay after Job.execute
	 *
	 * @return milliseconds to sleep
	 */
	public int delay() {
		return Random.nextInt(200, 400);
	}

	public int priority() {
		return 0;
	}

	public abstract boolean activate();

	public abstract void execute();

	@Override
	public int compareTo(Job o) {
		return o.priority() - priority();
	}
}
