package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;

import java.util.Arrays;
import java.util.Collections;
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
public class Tree<T extends LogicailScript> extends Task<T> {
	protected final Queue<Task<T>> tasks = new ConcurrentLinkedQueue<Task<T>>();
	protected final AtomicReference<Task<T>> currentTask = new AtomicReference<Task<T>>();

	public Tree(T script) {
		super(script);
	}

	public Tree(T script, Task<T>[] tasks) {
		this(script);
		this.tasks.addAll(Arrays.asList(tasks));
	}

	public void add(Task<T>... task) {
		Collections.addAll(tasks, task);
	}

	public boolean any() {
		return tasks.size() > 0;
	}

	public void clear() {
		tasks.clear();
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

	@Override
	public boolean activate() {
		Task<T> state = state();
		if (state != null) {
			set(state);
			return true;
		}
		return false;
	}

	public final void set(Task<T> node) {
		currentTask.set(node);
	}

	public final synchronized Task<T> state() {
		/*Task current = currentTask.get();
		if (current != null && current.activate()) {
			if (current.activate()) {
				// Don't loop previous is still valid
				return current;
			}
		}*/

		for (Task<T> next : tasks) {
			if (next != null && next.activate()) {
				return next;
			}
		}

		return null;
	}

	public final Task<T> get() {
		return currentTask.get();
	}

	public Queue<Task<T>> getTasks() {
		return tasks;
	}
}
