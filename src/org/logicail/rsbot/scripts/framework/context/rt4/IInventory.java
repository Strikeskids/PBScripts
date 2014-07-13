package org.logicail.rsbot.scripts.framework.context.rt4;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Inventory;
import org.powerbot.script.rt4.Item;

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

		return (ctx.game.tab() == Game.Tab.INVENTORY || ctx.game.tab(Game.Tab.INVENTORY)) && selectedItem().interact("Cancel") && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !selected();
			}
		}, 100, 10);
	}

	public boolean select(final int... ids) {
		final Item item = ctx.inventory.select().id(ids).shuffle().poll();
		if (selected() && !selectedIsOnOf(ids)) {
			deselect();
		}

		if (selectedIsOnOf(ids)) {
			return true;
		}

		if ((ctx.game.tab() == Game.Tab.INVENTORY || ctx.game.tab(Game.Tab.INVENTORY)) && item.click("Use")) {
			return Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return selectedIsOnOf(ids);
				}
			}, 50, 20);
		}

		deselect();

		return false;
	}

	private boolean selectedIsOnOf(int... ids) {
		final Item selected = ctx.inventory.selectedItem();
		if (selected.valid()) {
			for (int id : ids) {
				if (selected.id() == id) {
					return true;
				}
			}
		}
		return false;
	}
}
