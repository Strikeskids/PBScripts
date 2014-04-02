package com.sk.methods.action.structure;

import com.sk.methods.action.magic.Rune;
import com.sk.methods.action.magic.Spellbook;
import com.sk.windows.Window;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.rt6.Component;

public interface Spell extends BookIcon {
	public static final Spell NIL = new Spell() {
		@Override
		public int getChildIndex() {
			return -1;
		}

		@Override
		public int getChildTexture() {
			return -1;
		}

		@Override
		public Window getWindow() {
			return Window.NIL;
		}

		@Override
		public int getSkillLevel() {
			return 0;
		}

		@Override
		public int getSkill() {
			return -1;
		}

		@Override
		public Component getComponent(IClientContext ctx) {
			return ctx.widgets.component(0, 0);
		}

		@Override
		public boolean valid() {
			return false;
		}

		@Override
		public int id() {
			return -1;
		}

		@Override
		public Spellbook getSpellbook() {
			return Spellbook.NONE;
		}

		@Override
		public Component getCooldownComponent(IClientContext ctx) {
			return getComponent(ctx);
		}

		@Override
		public Rune[] getRunes() {
			return new Rune[0];
		}

		@Override
		public int getWidget() {
			return 0;
		}
	};

	public Rune[] getRunes();

	public Spellbook getSpellbook();
}
