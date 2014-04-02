package org.logicail.rsbot.scripts.framework.context.providers.walking;

import org.powerbot.script.rt6.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/02/14
 * Time: 18:39
 */
public class LevelRequirement implements Requirement {
	private final int skill;
	private final int level;

	public LevelRequirement(int skill, int level) {
		this.skill = skill;
		this.level = level;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LevelRequirement that = (LevelRequirement) o;

		return level == that.level && skill == that.skill;
	}

	@Override
	public int hashCode() {
		int result = skill;
		result = 31 * result + level;
		return result;
	}

	@Override
	public String toString() {
		return "LevelRequirement{" +
				"skill=" + skill +
				", level=" + level +
				'}';
	}

	@Override
	public boolean valid(ClientContext ctx) {
		return ctx.skills.level(skill) >= level;
	}
}
