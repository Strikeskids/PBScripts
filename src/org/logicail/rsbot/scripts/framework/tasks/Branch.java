package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:22
 */
public abstract class Branch<T extends LogicailScript<T>> extends Tree<T> {
	public Branch(T script) {
		super(script);
	}

	public void addNode(Node<T>... tasks) {
		Collections.addAll(this.tasks, tasks);
	}

	@Override
	public final boolean isValid() {
		return branch() && super.isValid();
	}

	public abstract boolean branch();
}
