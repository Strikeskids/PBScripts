package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Item;

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
		bankingBranch.fail = 0;
		options.status = "Open bank";

		for (Item item : ctx.equipment.select().id(Banking.ALWAYS_DEPOSIT)) {
			if (ctx.backpack.isFull()) {
				break;
			}
			ctx.equipment.unequip(item.getId());
		}

		if (!ctx.bank.open()) {
			ctx.camera.setAngle(Random.nextInt(0, 360));
		}
	}
}
