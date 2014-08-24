package org.logicail.cache.loader.rt6.wrapper.requirements;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 20:41
 */
public class CreationSkillRequirement extends SkillRequirement {
	public CreationSkillRequirement(int skill, int level) {
		super(skill, level);
	}

	public CreationSkill getSkillEnum() {
		return CreationSkill.values()[skill];
	}


	@Override
	public String toString() {
		return "CreationSkillRequirement{" +
				"skill=" + getSkillEnum() +
				", level=" + level +
				'}';
	}

	public static enum CreationSkill {
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
