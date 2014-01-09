package com.sk.methods.action.structure;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.wrappers.Component;

import com.sk.methods.action.ability.AbilityLevel;
import com.sk.methods.action.ability.AbilityStyle;
import com.sk.windows.Window;

public interface Ability extends BookIcon {
	public static Ability NIL = new Ability() {
		@Override
		public AbilityStyle getStyle() {
			return AbilityStyle.NONE;
		}

		@Override
		public AbilityLevel getAbilityLevel() {
			return AbilityLevel.NONE;
		}

		@Override
		public int getCooldown() {
			return 0;
		}

		@Override
		public int getChanneled() {
			return 0;
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
		public Component getComponent(IMethodContext ctx) {
			return ctx.widgets.get(0, 0);
		}

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
		public Component getCooldownComponent(IMethodContext ctx) {
			return getComponent(ctx);
		}

		@Override
		public int getWidget() {
			return 0;
		}
	};

	public AbilityLevel getAbilityLevel();

	public int getChanneled();

	public int getCooldown();
	public AbilityStyle getStyle();
}
