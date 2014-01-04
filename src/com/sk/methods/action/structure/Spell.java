package com.sk.methods.action.structure;

import com.sk.methods.action.magic.Rune;
import com.sk.methods.action.magic.Spellbook;
import com.sk.windows.Window;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.wrappers.Component;

public interface Spell extends BookIcon {
	public Spellbook getSpellbook();
	public Rune[] getRunes();

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
		public Component getComponent(LogicailMethodContext ctx) {
			return ctx.widgets.get(0, 0);
		}

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public int getId() {
			return -1;
		}

		@Override
		public Spellbook getSpellbook() {
			return Spellbook.NONE;
		}

		@Override
		public Component getCooldownComponent(LogicailMethodContext ctx) {
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
}
