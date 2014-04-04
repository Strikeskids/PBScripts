package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.powerbot.script.Validatable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 11:25
 */
public abstract class Node<T extends LogicailScript> extends Task<T> implements Validatable {
	public Node(T script) {
		super(script);
	}

	public abstract boolean valid();

	protected void sleep(int millis) {
		ctx.sleep(millis);
	}
}
