package com.sk.methods;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.rt6.Actor;
import org.powerbot.script.rt6.Skills;

public class Combat extends IClientAccessor {
	private static final int ADRENALINE_SETTING = 679, MAXIMUM_ADRENALINE = 1000;
	private static final int HEALTH_SETTING = 659, HEALTH_BOOST_SETTING = 3596;
	private static final int PRAYER_SETTING = 3274;

	private Actor target;

	public Combat(IClientContext ctx) {
		super(ctx);
	}

	public Actor getTarget() {
		return target;
	}

	public int getAdrenaline() {
		return ctx.varpbits.varpbit(ADRENALINE_SETTING);
	}

	public int getHealth() {
		return ctx.varpbits.varpbit(HEALTH_SETTING) >> 1;
	}

	public int getMaximumAdrenaline() {
		return MAXIMUM_ADRENALINE;
	}

	public int getMaximumHealth() {
		return ctx.varpbits.varpbit(HEALTH_BOOST_SETTING) + 40 * ctx.skills.level(Skills.CONSTITUTION);
	}

	public int getMaximumPrayerPoints() {
		return ctx.skills.level(Skills.PRAYER) * 100;
	}

	public int getMaximumSummoningPoints() {
		return ctx.skills.realLevel(Skills.SUMMONING);
	}

	public int getPrayerPoints() {
		return ctx.varpbits.varpbit(PRAYER_SETTING) & 0x3fff;
	}

	public int getSummoningPoints() {
		return ctx.skills.level(Skills.SUMMONING);
	}

	public void setTarget(Actor act) {
		this.target = act == null ? ctx.npcs.nil() : act;
	}
}
