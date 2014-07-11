package com.sk.oldcombatapi.windows;

import org.logicail.rsbot.scripts.framework.context.IClientContext;

public interface Window {
	public static final Window NIL = new Window() {
		@Override
		public boolean open(IClientContext ctx) {
			return false;
		}

		@Override
		public boolean isOpen(IClientContext ctx) {
			return false;
		}
	};

	public boolean isOpen(final IClientContext ctx);

	public boolean open(final IClientContext ctx);
}
