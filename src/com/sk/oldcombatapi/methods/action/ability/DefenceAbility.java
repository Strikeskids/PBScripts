package com.sk.oldcombatapi.methods.action.ability;

import com.sk.oldcombatapi.methods.action.structure.Ability;
import com.sk.oldcombatapi.windows.InnerAbilityTab;
import com.sk.oldcombatapi.windows.Window;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Skills;

public enum DefenceAbility implements Ability {
	ANTICIPATION(AbilityLevel.BASIC, 1, 14219, 3, 19, 25, 0),
	BASH(AbilityLevel.BASIC, 6, 14224, 8, 99, 15, 0),
	REVENGE(AbilityLevel.THRESHOLD, 9, 14227, 15, 147, 20, 0),
	PROVOKE(AbilityLevel.BASIC, 3, 14221, 24, 51, 10, 0),
	IMMORTALITY(AbilityLevel.ULTIMATE, 12, 14230, 29, 195, 120, 0),
	FREEDOM(AbilityLevel.BASIC, 2, 14220, 34, 35, 30, 0),
	REFLECT(AbilityLevel.THRESHOLD, 7, 14225, 34, 115, 15, 0),
	RESONANCE(AbilityLevel.BASIC, 4, 14222, 48, 67, 30, 0),
	REJUVENATE(AbilityLevel.ULTIMATE, 11, 14229, 52, 179, 60, 0),
	DEBILITATE(AbilityLevel.THRESHOLD, 8, 14226, 55, 131, 30, 0),
	PREPARATION(AbilityLevel.BASIC, 5, 14223, 67, 83, 5, 0),
	BARRICADE(AbilityLevel.ULTIMATE, 10, 14228, 81, 163, 60, 0),;

	private final int childIndex, childTexture, skillLevel, skill, id, cooldown, channeled;
	private final Window window;
	private final AbilityStyle style;
	private final AbilityLevel abilityLevel;

	private DefenceAbility(AbilityLevel level, int a, int b, int c, int id, int cd, int ch) {
		this.abilityLevel = level;
		this.childIndex = a;
		this.childTexture = b;
		this.skillLevel = c;
		this.id = id;
		this.cooldown = cd;
		this.channeled = ch;
		this.skill = Skills.DEFENSE;
		this.window = InnerAbilityTab.DEFENCE_ABILITY;
		this.style = AbilityStyle.DEFENCE;
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

