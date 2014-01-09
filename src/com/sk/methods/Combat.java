package com.sk.methods;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.wrappers.Actor;

public class Combat extends MethodProvider {
	private static final int ADRENALINE_SETTING = 679, MAXIMUM_ADRENALINE = 1000;
	private static final int HEALTH_SETTING = 659, HEALTH_BOOST_SETTING = 3596;
	private static final int PRAYER_SETTING = 3274;

	private Actor target;

	public Combat(MethodContext ctx) {
		super(ctx);
	}

	public Actor getTarget() {
		return target;
	}

	public int getAdrenaline() {
		return ctx.settings.get(ADRENALINE_SETTING);
	}

	public int getHealth() {
		return ctx.settings.get(HEALTH_SETTING) >> 1;
	}

	public int getMaximumAdrenaline() {
		return MAXIMUM_ADRENALINE;
	}

	public int getMaximumHealth() {
		return ctx.settings.get(HEALTH_BOOST_SETTING) + 40 * ctx.skills.getLevel(Skills.CONSTITUTION);
	}

	public int getMaximumPrayerPoints() {
		return ctx.skills.getLevel(Skills.PRAYER) * 100;
	}

	public int getMaximumSummoningPoints() {
		return ctx.skills.getRealLevel(Skills.SUMMONING);
	}

	public int getPrayerPoints() {
		return ctx.settings.get(PRAYER_SETTING) & 0x3fff;
	}

	public int getSummoningPoints() {
		return ctx.skills.getLevel(Skills.SUMMONING);
	}

	public void setTarget(Actor act) {
		this.target = act == null ? ctx.npcs.getNil() : act;
	}
}
