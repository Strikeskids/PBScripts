package com.sk.oldcombatapi.methods.action.ability;

import com.sk.oldcombatapi.windows.InnerAbilityTab;
import com.sk.oldcombatapi.windows.Window;
import org.powerbot.script.rt6.Skills;

/**
 * An enum of styles of ability. Corresponds to the skill requirement to use the ability
 *
 * @author Strikeskids
 */
public enum AbilityStyle {
	ATTACK(InnerAbilityTab.ATTACK_ABILITY, Skills.ATTACK),
	STRENGTH(InnerAbilityTab.STRENGTH_ABILITY, Skills.STRENGTH),
	RANGED(InnerAbilityTab.RANGED_ABILITY, Skills.RANGE),
	MAGIC(InnerAbilityTab.MAGIC_ABILITY, Skills.MAGIC),
	DEFENCE(InnerAbilityTab.DEFENCE_ABILITY, Skills.DEFENSE),
	CONSTITUTION(InnerAbilityTab.CONSTITUTION_ABILITY, Skills.CONSTITUTION),
	NONE(null, -1);

	private final InnerAbilityTab tab;
	private final int skillId;
	private final int widgetId;

	private AbilityStyle(final InnerAbilityTab t, final int sid) {
		this.tab = t;
		this.skillId = sid;
		if (t == null)
			this.widgetId = 0;
		else
			this.widgetId = t.getSuperWindow().getSource().widget();
	}

	/**
	 * Gets the skill id for the skill for this style of ability
	 *
	 * @return the skill id
	 */
	public int getSkillId() {
		return skillId;
	}

	/**
	 * Gets {@link InnerAbilityTab} of abilities that is used for this style
	 *
	 * @return the tab of abilities for this style
	 */
	public Window getTab() {
		return tab;
	}

	public int getWidgetId() {
		return widgetId;
	}
}
