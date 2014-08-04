package com.logicail.loader.rt6.wrapper.requirements;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/03/14
 * Time: 20:18
 */
public class CombatSkillRequirement implements ListRequirement<SkillRequirement> {
	protected final int skill;
	protected final int level;

	public CombatSkillRequirement(int skill, int level) {
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
		return "CombatSkillRequirement{" +
				"skill=" + skill +
				", enum=" + getEnum() +
				", level=" + level +
				'}';
	}

	public Skill getEnum() {
		switch (skill) {
			case 0:
				return Skill.ATTACK;
			case 1:
				return Skill.DEFENCE;
			case 2:
				return Skill.STRENGTH;
			case 3:
				return Skill.CONSTITUTION;
			case 4:
				return Skill.RANGED; // guess
			case 5:
				return Skill.PRAYER;
			case 6:
				return Skill.MAGIC;
			case 18:
				return Skill.SLAYER;
			case 20:
				return Skill.RUNECRAFTING;
		}
		return null;
	}

	@Override
	public java.util.List<SkillRequirement> get(Map params) {
		return null;
	}


	public static enum Skill {
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
		SLAYER,
		FLETCHING,
		FARMING,
		CONSTRUCTION,
		HUNTER,
		SUMMONING,
		DUNGEONEERING,
		DIVINATION
	}
}

