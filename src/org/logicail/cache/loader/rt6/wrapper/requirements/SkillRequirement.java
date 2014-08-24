package org.logicail.cache.loader.rt6.wrapper.requirements;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 20:41
 */
public class SkillRequirement implements ListRequirement<SkillRequirement> {
	protected final int skill;
	protected final int level;

	public SkillRequirement(int skill, int level) {
		this.skill = skill;
		this.level = level;
	}

	public int getSkill() {
		return skill;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String toString() {
		return "SkillRequirement{" +
				"skill=" + skill +
				", level=" + level +
				'}';
	}

	public RequirementSkill getEnum() {
		switch (skill) {
			case 10:
				return RequirementSkill.FISHING;
			case 15:
				return RequirementSkill.HERBLORE;
			case 19:
				return RequirementSkill.FARMING;
		}
		return null;
	}

	@Override
	public java.util.List<SkillRequirement> get(Map params) {
		return null;
	}


	public static enum RequirementSkill {
		NULL, // For ordinal
		ATTACK,
		STRENGTH,
		RANGED,
		MAGIC,
		DEFENCE,
		CONSTITUTION,
		PRAYER,
		AGILITY,
		HERBLORE,
		THIEVING,
		CRAFTING,
		RUNECRAFTING,
		MINING,
		SMITHING,
		FISHING,
		COOKING,
		FIREMAKING,
		WOODCUTTING,
		FLETCHING,
		SLAYER,
		FARMING,
		CONSTRUCTION,
		HUNTER,
		SUMMONING,
		DUNGEONEERING,
		DIVINATION,
	}
}
