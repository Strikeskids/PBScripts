package com.sk.oldcombatapi.util.time;

public interface PreparedWaitable {
	public static final PreparedWaitable NIL = new PreparedWaitable() {
		@Override
		public boolean waitFor() {
			return true;
		}
	};

	public abstract boolean waitFor();
}
