package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:58
 */
public abstract class Task<T extends LogicailScript> extends IMethodProvider implements Runnable {
	public T script;

	public Task(T script) {
		super(script.ctx);
		this.script = script;
	}
}
