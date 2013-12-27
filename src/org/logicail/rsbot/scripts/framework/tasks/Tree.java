package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Because GraphScript is too compilcated
 * <p/>
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:15
 */
public class Tree extends Task {
	protected final Queue<Task> tasks = new ConcurrentLinkedQueue<Task>();
	protected final AtomicReference<Task> currentTask = new AtomicReference<Task>();

	public Tree(LogicailMethodContext ctx) {
		super(ctx);
	}

	public Tree(LogicailMethodContext ctx, Task[] tasks) {
		this(ctx);
		this.tasks.addAll(Arrays.asList(tasks));
	}

	public final synchronized Task state() {
		/*Task current = currentTask.get();
		if (current != null && current.activate()) {
			if (current.activate()) {
				// Don't loop previous is still valid
				return current;
			}
		}*/

		for (Task next : tasks) {
			if (next != null && next.activate()) {
				return next;
			}
		}
		return null;
	}

	public final void set(Task node) {
		currentTask.set(node);
	}

	public final Task get() {
		return currentTask.get();
	}

	@Override
	public boolean activate() {
		Task state = state();
		if (state != null) {
			set(state);
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		if (activate()) {
			Task task = get();
			if (task != null) {
				ctx.log.info("Run: " + task);
				task.run();
			}
		}
	}
}
