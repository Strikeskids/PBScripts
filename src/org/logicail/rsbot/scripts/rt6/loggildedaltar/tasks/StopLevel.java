package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks;

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
	public String toString() {
		return "StopLevel";
	}

	@Override
	public boolean valid() {
		try {
			return ctx.skills.realLevel(skill) >= level;
		} catch (Exception ignored) {
		}
		return false;
	}

	@Override
	public void run() {
		ctx.stop("Stopping because level is " + level, "Stopping", false);
	}
}
