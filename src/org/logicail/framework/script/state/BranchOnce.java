package org.logicail.framework.script.state;

import org.logicail.api.methods.MyMethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:08
 */
public abstract class BranchOnce extends Branch {
	public BranchOnce(MyMethodContext ctx) {
		super(ctx);
	}

	@Override
	public void execute() {
		Node current = current_node.get();
		if (current != null) {
			current.execute();
		}
	}
}
