package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:22
 */
public abstract class Branch extends Tree {
	public Branch(LogicailMethodContext ctx) {
		super(ctx);
	}

	public void addTask(Task... tasks) {
		Collections.addAll(this.tasks, tasks);
	}

	public abstract boolean branch();

	@Override
	public final boolean activate() {
		return branch() && super.activate();
	}
}
