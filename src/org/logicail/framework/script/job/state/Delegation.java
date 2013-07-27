package org.logicail.framework.script.job.state;

import org.logicail.api.methods.MyMethodContext;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:08
 */
public abstract class Delegation extends Branch {
	public Delegation(MyMethodContext ctx) {
		super(ctx);
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
				break;
			}
		}
	}
}
