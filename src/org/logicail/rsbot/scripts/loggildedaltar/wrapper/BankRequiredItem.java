package org.logicail.rsbot.scripts.loggildedaltar.wrapper;

import org.powerbot.script.rt6.Equipment;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:29
 */
public class BankRequiredItem {
	private final int[] ids;
	private final int quantity;
	private final boolean equip;
	private final Equipment.Slot slot;

	public BankRequiredItem(int quantity, boolean equip, Equipment.Slot slotWearIn, int... validIds) {
		this.ids = validIds;
		this.quantity = quantity;
		this.equip = equip;
		this.slot = slotWearIn;
	}

	public int[] getIds() {
		return ids;
	}

	public int getQuantity() {
		return quantity;
	}

	public Equipment.Slot getSlot() {
		return slot;
	}

	public boolean equip() {
		return equip;
	}
}