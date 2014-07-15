package org.logicail.rsbot.scripts.framework.context.rt6.providers;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.rt6.Backpack;
import org.powerbot.script.rt6.Item;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/12/13
 * Time: 21:58
 */
public class IBackpack extends Backpack {
	public IBackpack(IClientContext context) {
		super(context);
	}

	public boolean isFull() {
		return select().count() == 28;
	}

	public Item selectedItem() {
		final int index = selectedItemIndex();
		return index > -1 ? itemAt(index) : nil();
	}
}
