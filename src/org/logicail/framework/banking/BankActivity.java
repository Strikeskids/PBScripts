package org.logicail.framework.banking;

import org.logicail.framework.script.job.state.Node;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Item;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 21:26
 */
// TODO: Check inventory has space
public class BankActivity extends Node {
	private final RequiredItem[] requiredItems;

	public BankActivity(MethodContext ctx, RequiredItem... requiredItems) {
		super(ctx);
		this.requiredItems = requiredItems;
	}

	@Override
	public boolean activate() {
		if (!ctx.bank.isOpen()) {
			return false;
		}

		for (RequiredItem requiredItem : requiredItems) {
			if (requiredItem instanceof RequiredEquipment) { // 1,1
				if (!ctx.equipment.contains(requiredItem.getIds())) {
					return true;
				}
			} else if (requiredItem instanceof RequireOneItem) { // (1 of x),1
				if (ctx.inventory.select().id(requiredItem.getIds()).isEmpty()) {
					return true;
				}
			} else if (requiredItem instanceof SingleRequiredItem) { // 1,x
				if (ctx.inventory.select().id(requiredItem.getIds()).count(true) < requiredItem.getQuantity()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void execute() {
		for (RequiredItem requiredItem : requiredItems) {
			if (requiredItem instanceof RequiredEquipment) {
				if ((!ctx.equipment.contains(requiredItem.getIds()))) {
					if (!ctx.inventory.select().id(requiredItem.getIds()).isEmpty()) {
						// Wield/Wear etc.
					} else {
						ctx.bank.withdraw(requiredItem.getIds()[0], requiredItem.getQuantity());
					}
				}
			} else if (requiredItem instanceof RequireOneItem) { // Require 1 of many // fix for (1 of i and 2 of j)
				if (ctx.inventory.select().id(requiredItem.getIds()).isEmpty()) {
					for (Item item : ctx.bank.select().id(requiredItem.getIds())) {
						if (ctx.bank.withdraw(item.getId(), 1)) {
							break;
						}
					}
				}
			} else if (requiredItem instanceof SingleRequiredItem) {
				if (ctx.inventory.select().id(requiredItem.getIds()).count(true) < requiredItem.getQuantity()) {
					int quantityRequired = requiredItem.getQuantity() - ctx.inventory.count(true);
					for (int id : requiredItem.getIds()) {
						int count = ctx.bank.select().id(id).count(true);

						int withdraw = quantityRequired;

						if (count < withdraw) {
							withdraw = count;
						}

						if (withdraw < 5) {
							// Click one at a time
							for (int i = withdraw; i > 0; i--) {
								if (ctx.bank.withdraw(id, 1)) {
									quantityRequired -= 1;
								}
								sleep(50, 333);
							}
						} else if (ctx.bank.withdraw(id, withdraw)) {
							quantityRequired -= withdraw;
						}

						if (quantityRequired <= 0) {
							break;
						}
					}
				}
			}
			sleep(333, 999);
		}
	}
}
