package org.logicail.rsbot.scripts.framework.context.rt4.providers;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
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
		if (selected() && !selectedIsOnOf(ids)) {
			deselect();
		}

		if (selectedIsOnOf(ids)) {
			return true;
		}

		final Item item = ctx.inventory.select().id(ids).shuffle().poll();
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

	public boolean itemOnItem(int a, int b) {
		final Item itemA = select().id(a).shuffle().poll();
		final Item itemB = select().id(b).shuffle().poll();

		if (!itemA.valid() || !itemB.valid()) {
			return false;
		}

		if (ctx.game.tab() == Game.Tab.INVENTORY || ctx.game.tab(Game.Tab.INVENTORY)) {
			if (selectedItem().id() == a || Random.nextBoolean() && select(a)) {
				return itemB.click("Use", selectedItem().name() + " -> " + itemB.name()) && Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !selectedItem().valid();
					}
				}, 200, 4);
			}

			return select(b) && itemB.click("Use", selectedItem().name() + " -> " + itemA.name()) && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !selectedItem().valid();
				}
			}, 200, 4);
		}

		return false;
	}
}
