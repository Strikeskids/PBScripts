package com.sk.oldcombatapi.util.time;

public interface Waitable {
	public static final Waitable NIL = new Waitable() {
		@Override
		public boolean waitFor(long maxTime, long iterationDelay) {
			return true;
		}

		@Override
		public boolean waitFor(long maxTime) {
			return true;
		}
	};

	public boolean waitFor(long maxTime);

	public boolean waitFor(long maxTime, long iterationDelay);
}
