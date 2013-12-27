package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:58
 */
public abstract class Task extends LogicailMethodProvider implements Runnable {
	public Task(LogicailMethodContext context) {
		super(context);
	}

	public abstract boolean activate();
}
