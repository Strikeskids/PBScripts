package com.sk.windows;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;

public interface Window {
	public static final Window NIL = new Window() {
		@Override
		public boolean open(IMethodContext ctx) {
			return false;
		}

		@Override
		public boolean isOpen(IMethodContext ctx) {
			return false;
		}
	};
	public boolean isOpen(final IMethodContext ctx);
	public boolean open(final IMethodContext ctx);
}
