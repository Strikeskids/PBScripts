package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 11:25
 */
public abstract class Node<T extends LogicailScript> extends Task<T> {
	public Node(T script) {
		super(script);
	}

	public abstract boolean isValid();
}
