package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.methods.Backpack;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/12/13
 * Time: 21:58
 */
public class IBackpack extends Backpack {
	public IBackpack(IMethodContext context) {
		super(context);
	}

	public boolean isFull() {
		return select().count() == 28;
	}
}
