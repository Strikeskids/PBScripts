package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Locatable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 20:12
 */
public class BankOpen extends BankingAbstract {
	public BankOpen(Banking banking) {
		super(banking);
	}

	@Override
	public String toString() {
		return "Banking: Open";
	}

	@Override
	public boolean isValid() {
		return !ctx.bank.isOpen();
	}

	@Override
	public void run() {
		options.status = "Open bank";

		for (Item item : ctx.equipment.select().id(Banking.ALWAYS_DEPOSIT)) {
			if (ctx.backpack.isFull()) {
				break;
			}
			ctx.equipment.unequip(item.getId());
		}

		if (!ctx.bank.open()) {
			final Locatable nearest = ctx.bank.getNearest();
			ctx.camera.prepare(nearest);
		}
	}
}
