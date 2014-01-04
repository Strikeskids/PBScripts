package com.sk.windows;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;

public interface Window {
	public static final Window NIL = new Window() {
		@Override
		public boolean open(LogicailMethodContext ctx) {
			return false;
		}
		@Override
		public boolean isOpen(LogicailMethodContext ctx) {
			return false;
		}
	};
	public boolean open(final LogicailMethodContext ctx);
	public boolean isOpen(final LogicailMethodContext ctx);
}
