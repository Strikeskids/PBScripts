package org.logicail.framework.script.job.state;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/06/13
 * Time: 17:38
 */
public class Tree {
	private final Queue<Node> nodes = new ConcurrentLinkedQueue<>();
	private final AtomicReference<Node> current_node = new AtomicReference<>();

	public Tree(Node[] nodes) {
		this.nodes.addAll(Arrays.asList(nodes));
	}

	public final synchronized Node state() {
		Node current = current_node.get();
		if (current != null) {
			if (current.isAlive()) {
				return null;
			}
			if (current.activate()) {
				/*** Don't loop previous is still valid ***/
				return current;
			}
		}

		for (Node next : nodes) {
			if (next != null && next.activate()) {
				return next;
			}
		}
		return null;
	}

	public final void set(Node node) {
		current_node.set(node);
	}

	public final Node get() {
		return current_node.get();
	}
}
