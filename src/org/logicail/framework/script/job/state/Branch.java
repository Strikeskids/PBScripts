package org.logicail.framework.script.job.state;

import org.logicail.api.methods.MyMethodContext;

import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/07/13
 * Time: 14:28
 */
public abstract class Branch extends Node {
	protected final Queue<Node> nodes = new ConcurrentLinkedQueue();
	protected final AtomicReference<Node> current_node = new AtomicReference();

	public Branch(MyMethodContext ctx) {
		super(ctx);
	}

	public Branch(MyMethodContext ctx, Node[] tasks) {
		super(ctx);
		Collections.addAll(nodes, tasks);
	}

	public void addNodes(Node... add) {
		Collections.addAll(nodes, add);
	}

	public abstract boolean branch();

	@Override
	public final boolean activate() {
		if (branch()) {
			Iterator<Node> iterator = nodes.iterator();
			while (iterator.hasNext()) {
				Node node = iterator.next();
				if (node != null && node.activate()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void execute() {
		Node current = current_node.get();
		if (current != null && current.isAlive()) {
			return;
		}

		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {
			Node next = iterator.next();
			if (next != null && next.activate()) {
				current_node.set(next);
				getContainer().submit(next);
				next.join();
			}
		}
	}
}
