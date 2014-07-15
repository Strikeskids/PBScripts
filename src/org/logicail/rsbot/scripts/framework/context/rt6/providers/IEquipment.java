package org.logicail.rsbot.scripts.framework.context.rt6.providers;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt6.Equipment;
import org.powerbot.script.rt6.Hud;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.Menu;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:45
 */
public class IEquipment extends Equipment {
	private final IClientContext ctx;

	public IEquipment(IClientContext context) {
		super(context);
		ctx = context;
	}

	/**
	 * Equip an item if it is in the backpack
	 *
	 * @param ids of the item(s) to equip
	 * @return <tt>true</tt> if an item was equipped otherwise <tt>false</tt>
	 */
	public boolean equip(final int... ids) {
		Item item;
		if (ctx.bank.opened()) {
			item = ctx.bank.backpack.select().id(ids).poll();
		} else {
			item = ctx.backpack.select().id(ids).poll();
			if (item.id() > -1) {
				if (!ctx.hud.opened(Hud.Window.BACKPACK) && ctx.hud.open(Hud.Window.BACKPACK)) {
					ctx.sleep(400);
				}
				if (!ctx.backpack.scroll(item)) {
					return false;
				}
			}
		}

		if (item.valid()) {
			if (item.interact(new Filter<Menu.Command>() {
				@Override
				public boolean accept(Menu.Command entry) {
					return entry.action.startsWith("Equip") || entry.action.startsWith("Wear") || entry.action.startsWith("Wield");
				}
			})) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !select().id(ids).isEmpty();
					}
				}, 200, 10);
			}
		}

		return !select().id(ids).isEmpty();
	}

	public boolean unequip(final int... ids) {
		// Disable when bank open
		if (ctx.bank.opened()) {
			return false;
		}

		if (ctx.hud.open(Hud.Window.WORN_EQUIPMENT)) {
			final Item item = ctx.equipment.select().id(ids).poll();
			if (item.valid()) {
				if (item.interact("Remove")) {
					return Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !ctx.equipment.select().id(ids).poll().valid();
						}
					});
				}
			} else {
				return true;
			}
		}

		return false;
	}
}
