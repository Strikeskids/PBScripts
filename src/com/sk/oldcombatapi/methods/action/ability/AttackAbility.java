package com.sk.oldcombatapi.methods.action.ability;

import com.sk.oldcombatapi.methods.action.structure.Ability;
import com.sk.oldcombatapi.windows.InnerAbilityTab;
import com.sk.oldcombatapi.windows.Window;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Skills;

public enum AttackAbility implements Ability {
	SLICE(AbilityLevel.BASIC, 1, 14207, 1, 17, 5, 0),
	SLAUGHTER(AbilityLevel.THRESHOLD, 7, 14213, 1, 113, 30, 0),
	OVERPOWER(AbilityLevel.ULTIMATE, 10, 14216, 3, 161, 30, 0),
	HAVOC(AbilityLevel.BASIC, 4, 14210, 7, 65, 10, 0),
	BACKHAND(AbilityLevel.BASIC, 6, 14212, 15, 97, 15, 0),
	SMASH(AbilityLevel.BASIC, 5, 14211, 25, 81, 10, 0),
	BARGE(AbilityLevel.BASIC, 2, 14208, 34, 33, 20, 0),
	FLURRY(AbilityLevel.THRESHOLD, 8, 14214, 37, 129, 20, 6000),
	SEVER(AbilityLevel.BASIC, 3, 14209, 45, 49, 30, 0),
	HURRICANE(AbilityLevel.THRESHOLD, 9, 14215, 55, 145, 20, 0),
	MASSACRE(AbilityLevel.ULTIMATE, 11, 14217, 66, 177, 60, 0),
	METEOR_STRIKE(AbilityLevel.ULTIMATE, 12, 14218, 81, 193, 60, 0),;

	private final int childIndex, childTexture, skillLevel, skill, id, cooldown, channeled;
	private final Window window;
	private final AbilityStyle style;
	private final AbilityLevel abilityLevel;

	private AttackAbility(AbilityLevel level, int a, int b, int c, int id, int cd, int ch) {
		this.abilityLevel = level;
		this.childIndex = a;
		this.childTexture = b;
		this.skillLevel = c;
		this.id = id;
		this.cooldown = cd;
		this.channeled = ch;
		this.skill = Skills.ATTACK;
		this.window = InnerAbilityTab.ATTACK_ABILITY;
		this.style = AbilityStyle.ATTACK;
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
