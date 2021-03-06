package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.banking;

import org.powerbot.script.Locatable;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.Npc;

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
	public boolean valid() {
		return !ctx.bank.opened();
	}

	@Override
	public void run() {
		options.status = "Open bank";

		for (Item item : ctx.equipment.select().id(Banking.ALWAYS_DEPOSIT)) {
			if (ctx.backpack.isFull()) {
				break;
			}
			ctx.equipment.unequip(item.id());
		}

		if (!ctx.bank.open()) {
			final Locatable nearest = ctx.bank.nearest();
			if (nearest instanceof Npc) {
				ctx.camera.prepare((Npc) nearest);
			} else if (nearest instanceof GameObject) {
				ctx.camera.prepare((GameObject) nearest);
			}
		}
	}
}
