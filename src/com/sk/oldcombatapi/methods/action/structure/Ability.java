package com.sk.oldcombatapi.methods.action.structure;

import com.sk.oldcombatapi.methods.action.ability.AbilityLevel;
import com.sk.oldcombatapi.methods.action.ability.AbilityStyle;
import com.sk.oldcombatapi.windows.Window;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.rt6.Component;

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
		public boolean valid() {
			return false;
		}

		@Override
		public int id() {
			return -1;
		}

		@Override
		public Component getComponent(IClientContext ctx) {
			return ctx.widgets.component(0, 0);
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
		public Component getCooldownComponent(IClientContext ctx) {
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
