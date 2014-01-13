package org.logicail.rsbot.scripts.framework.util;

import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/01/14
 * Time: 10:38
 */
public class SkillData {
	public static int getExperenceToNextLevel(MethodContext ctx, final int index) {
		try {
			final int level = ctx.skills.getRealLevel(index);
			return ctx.skills.getExperienceAt(level + 1) - ctx.skills.getExperience(index);
		} catch (Exception ignored) {
			return Integer.MAX_VALUE;
		}
	}
}
