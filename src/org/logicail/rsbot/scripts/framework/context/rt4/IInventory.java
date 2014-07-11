package org.logicail.rsbot.scripts.framework.context.rt4;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Inventory;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 17:35
 */
public class IInventory extends Inventory {
	public IInventory(ClientContext ctx) {
		super(ctx);
	}

	public boolean selected() {
		return selectedItemIndex() > -1;
	}

	public boolean deselect() {
		if (!selected()) {
			return true;
		}

		return selectedItem().interact("Cancel") && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !selected();
			}
		}, 100, 10);
	}
}
