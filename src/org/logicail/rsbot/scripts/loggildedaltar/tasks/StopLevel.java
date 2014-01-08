package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Node;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 21:40
 */
public class StopLevel<T extends LogicailScript> extends Node<T> {
	private final int skill;
	private final int level;

	public StopLevel(T script, int skill, int level) {
		super(script);
		this.skill = skill;
		this.level = level;
	}

	@Override
	public boolean isValid() {
		try {
			return ctx.skills.getLevel(skill) >= level;
		} catch (Exception ignored) {
		}
		return false;
	}

	@Override
	public void run() {
		ctx.stop("Stopping because level is " + level, "Stopping", false);
	}
}
