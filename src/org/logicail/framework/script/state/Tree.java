package org.logicail.framework.script.state;

import org.logicail.api.methods.LogicailMethodContext;

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
public class Tree extends Node {
	private final Queue<Node> nodes = new ConcurrentLinkedQueue<>();
	private final AtomicReference<Node> current_node = new AtomicReference<>();

	public Tree(LogicailMethodContext ctx, Node[] nodes) {
		super(ctx);
		this.nodes.addAll(Arrays.asList(nodes));
	}

	public final synchronized Node state() {
		Node current = current_node.get();
		if (current != null && current.activate()) {
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

	@Override
	public boolean activate() {
		Node state = state();
		if (state != null) {
			set(state);
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		Node node = get();
		if (node != null) {
			node.execute();
		}
	}
}
