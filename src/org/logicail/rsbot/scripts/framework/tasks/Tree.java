package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;

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
public class Tree<T extends LogicailScript> extends Node<T> {
	protected final Queue<Node<T>> tasks = new ConcurrentLinkedQueue<Node<T>>();
	protected final AtomicReference<Node<T>> currentTask = new AtomicReference<Node<T>>();

	public Tree(T script) {
		super(script);
	}

	public Tree(T script, Node<T>[] tasks) {
		this(script);
		this.tasks.addAll(Arrays.asList(tasks));
	}

	public void add(Node<T>... nodes) {
		for (Node<T> node : nodes) {
			if(node == null) continue;
			tasks.add(node);
		}
	}

	public boolean any() {
		return tasks.size() > 0;
	}

	public void clear() {
		tasks.clear();
	}

	public Queue<Node<T>> getNodes() {
		return tasks;
	}

	@Override
	public void run() {
		if (isValid()) {
			Task task = get();
			if (task != null) {
				ctx.log.info("Run: " + task);
				task.run();
			}
		}
	}

	public final Task<T> get() {
		return currentTask.get();
	}

	@Override
	public boolean isValid() {
		Node<T> state = state();
		if (state != null) {
			set(state);
			return true;
		}
		return false;
	}

	public final void set(Node<T> node) {
		currentTask.set(node);
	}

	public final synchronized Node<T> state() {
		/*Task current = currentTask.get();
		if (current != null && current.isValid()) {
			if (current.isValid()) {
				// Don't loop previous is still valid
				return current;
			}
		}*/

		for (Node<T> next : tasks) {
			if (next != null && next.isValid()) {
				return next;
			}
		}

		return null;
	}
}
