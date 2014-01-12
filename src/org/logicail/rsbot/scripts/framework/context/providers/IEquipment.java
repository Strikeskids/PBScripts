package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Equipment;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:45
 */
public class IEquipment extends Equipment {
	private final IMethodContext ctx;

	public IEquipment(IMethodContext context) {
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
		if (ctx.bank.isOpen()) {
			item = ctx.bank.backpack.select().id(ids).poll();
		} else {
			item = ctx.backpack.select().id(ids).poll();
			if (item.getId() > -1) {
				if (!ctx.hud.isVisible(Hud.Window.BACKPACK) && ctx.hud.view(Hud.Window.BACKPACK)) {
					sleep(200, 800);
				}
			}
		}

		if (item.isValid()) {
			if (item.interact(new Filter<Menu.Entry>() {
				@Override
				public boolean accept(Menu.Entry entry) {
					return entry.action.startsWith("Equip") || entry.action.startsWith("Wear") || entry.action.startsWith("Wield");
				}
			})) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !select().id(ids).isEmpty();
					}
				}, Random.nextInt(300, 800), 5);
				sleep(100, 300);
			}
		}

		return !select().id(ids).isEmpty();
	}

	public boolean unequip(final int... ids) {
		// Disable when bank open
		if (ctx.bank.isOpen()) {
			return false;
		}

		if (ctx.hud.view(Hud.Window.WORN_EQUIPMENT)) {
			final Item item = ctx.equipment.select().id(ids).poll();
			if (item.isValid()) {
				if (item.interact("Remove")) {
					return Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !ctx.equipment.select().id(ids).poll().isValid();
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
