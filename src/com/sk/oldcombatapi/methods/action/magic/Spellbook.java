package com.sk.oldcombatapi.methods.action.magic;

import org.logicail.rsbot.scripts.framework.context.IClientContext;

public enum Spellbook {
	STANDARD(0), ANCIENT(1), LUNAR(2), NONE(-1), ALL(-1) {
		@Override
		public boolean isOpen(IClientContext ctx) {
			return true;
		}
	};

	private static final int SPELLBOOK_SETTING = 4, MASK = 0x3;

	private final int id;

	private Spellbook(int id) {
		this.id = id;
	}

	public boolean isOpen(IClientContext ctx) {
		return (ctx.varpbits.varpbit(SPELLBOOK_SETTING) & MASK) == id;
	}
}
