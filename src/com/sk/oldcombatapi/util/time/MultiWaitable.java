package com.sk.oldcombatapi.util.time;

public class MultiWaitable implements PreparedWaitable {
	private final PreparedWaitable[] waitables;

	public MultiWaitable(PreparedWaitable... order) {
		this.waitables = order;
	}

	@Override
	public boolean waitFor() {
		for (PreparedWaitable wait : waitables)
			if (!wait.waitFor())
				return false;
		return true;
	}
}
