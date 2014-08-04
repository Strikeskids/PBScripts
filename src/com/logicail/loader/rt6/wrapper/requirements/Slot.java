package com.logicail.loader.rt6.wrapper.requirements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/03/14
 * Time: 13:08
 */
public enum Slot {
	HEAD(0),
	CAPE(1),
	NECK(2),
	MAIN_HAND(3),
	TORSO(4),
	OFF_HAND(5),
	LEGS(7),
	HANDS(9),
	FEET(10),
	RING(12),
	QUIVER(13),
	AURA(14),
	POCKET(17);
	private final int index;

	Slot(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	private static final Map<Integer, Slot> SLOT_MAP = new HashMap<Integer, Slot>();

	static {
		for (Slot slot : Slot.values()) {
			SLOT_MAP.put(slot.getIndex(), slot);
		}
	}

	public static Slot get(int index) {
		return SLOT_MAP.get(index);
	}
}
