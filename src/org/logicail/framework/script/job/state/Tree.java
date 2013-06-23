package org.logicail.framework.script.job.state;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:38
 */
public class Tree {
	private final Queue<Node> nodes = new ConcurrentLinkedQueue<>();
	private final AtomicReference<Node> current_node = new AtomicReference<>();

	public Tree(Node[] paramArrayOfNode) {
		this.nodes.addAll(Arrays.asList(paramArrayOfNode));
	}

	public final synchronized Node state() {
		Node node = current_node.get();
		if ((node != null) && (node.isAlive()))
			return null;
		for (Node next : this.nodes) {
			if ((next != null) && (next.activate()))
				return next;
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
