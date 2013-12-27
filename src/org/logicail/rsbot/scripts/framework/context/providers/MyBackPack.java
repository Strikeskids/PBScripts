package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.methods.Backpack;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/12/13
 * Time: 21:58
 */
public class MyBackPack extends Backpack {
	public MyBackPack(LogicailMethodContext context) {
		super(context);
	}

	public boolean isFull() {
		return select().count() == 28;
	}
}
