package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.methods.Equipment;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:45
 */
public class MyEquipment extends Equipment {
	public MyEquipment(LogicailMethodContext context) {
		super(context);
	}

	/**
	 * Equip an item if it is in the backpack
	 *
	 * @param ids of the item(s) to equip
	 * @return <tt>true</tt> if an item was equipped otherwise <tt>false</tt>
	 */
	public boolean equip(final int... ids) {
		for (Item item : ctx.backpack.select().id(ids).first()) {
			if (!ctx.hud.isVisible(Hud.Window.BACKPACK) && ctx.hud.view(Hud.Window.BACKPACK)) {
				sleep(200, 800);
			}
			if (item.isValid()) {
				for (String action : item.getActions()) {
					if (action == null) continue;
					if (action.contains("Equip") || action.contains("Wear") || action.contains("Wield")) {
						if (item.interact(action, item.getName())) {
							Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return !select().id(ids).isEmpty();
								}
							});
						}
						break;
					}
				}
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
