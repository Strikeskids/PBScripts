package com.sk.oldcombatapi.methods.action.ability;

import com.sk.oldcombatapi.methods.action.structure.Ability;
import com.sk.oldcombatapi.windows.InnerAbilityTab;
import com.sk.oldcombatapi.windows.Window;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Skills;

public enum StrengthAbility implements Ability {
	KICK(AbilityLevel.BASIC, 2, 14256, 3, 34, 15, 0),
	PUNISH(AbilityLevel.BASIC, 3, 14257, 8, 50, 5, 0),
	DISMEMBER(AbilityLevel.BASIC, 1, 14255, 14, 18, 30, 0),
	FURY(AbilityLevel.BASIC, 4, 14258, 24, 66, 20, 6000),
	DESTROY(AbilityLevel.THRESHOLD, 9, 14263, 37, 146, 20, 6000),
	QUAKE(AbilityLevel.THRESHOLD, 8, 14262, 37, 130, 20, 0),
	BERSERK(AbilityLevel.ULTIMATE, 10, 14264, 42, 162, 60, 0),
	CLEAVE(AbilityLevel.BASIC, 6, 14260, 48, 98, 10, 0),
	ASSAULT(AbilityLevel.THRESHOLD, 7, 14261, 55, 114, 30, 6000),
	DECIMATE(AbilityLevel.BASIC, 5, 14259, 67, 82, 10, 0),
	PULVERISE(AbilityLevel.ULTIMATE, 12, 14266, 81, 194, 60, 0),
	FRENZY(AbilityLevel.ULTIMATE, 11, 14265, 86, 178, 60, 10000),;

	private final int childIndex, childTexture, skillLevel, skill, id, cooldown, channeled;
	private final Window window;
	private final AbilityStyle style;
	private final AbilityLevel abilityLevel;

	private StrengthAbility(AbilityLevel level, int a, int b, int c, int id, int cd, int ch) {
		this.abilityLevel = level;
		this.childIndex = a;
		this.childTexture = b;
		this.skillLevel = c;
		this.id = id;
		this.cooldown = cd;
		this.channeled = ch;
		this.skill = Skills.STRENGTH;
		this.window = InnerAbilityTab.STRENGTH_ABILITY;
		this.style = AbilityStyle.STRENGTH;
	}

	@Override
	public AbilityLevel getAbilityLevel() {
		return abilityLevel;
	}

	@Override
	public int getChanneled() {
		return channeled;
	}

	@Override
	public int getChildIndex() {
		return childIndex;
	}

	@Override
	public int getChildTexture() {
		return childTexture;
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public int getSkill() {
		return skill;
	}

	@Override
	public int getSkillLevel() {
		return skillLevel;
	}

	@Override
	public AbilityStyle getStyle() {
		return style;
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public Component getComponent(IClientContext ctx) {
		return ctx.widgets.component(getWidget(), MAIN_COMPONENT).component(childIndex);
	}

	@Override
	public Component getCooldownComponent(IClientContext ctx) {
		return ctx.widgets.component(getWidget(), COOLDOWN_COMPONENT).component(childIndex);
	}

	@Override
	public int getWidget() {
		return getStyle().getWidgetId();
	}

	@Override
	public boolean valid() {
		return true;
	}
}

