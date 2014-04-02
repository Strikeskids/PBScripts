package org.logicail.rsbot.scripts.framework.util;

import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Skills;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/01/14
 * Time: 10:38
 */
public final class SkillData extends ClientAccessor {
	public final int[] initialLevels;
	public static final int NUM_SKILL = 26;
	public final int[] initialExp;
	private final Timer timer;

	public SkillData(final ClientContext arg0, final Timer arg1) {
		super(arg0);
		int i = 0;
		final int n = 26;
		final int n2 = 26;
		this.initialExp = new int[n2];
		this.initialLevels = new int[n];
		while (i < 26) {
			final int[] initialExp = this.initialExp;
			final Skills skills = arg0.skills;
			initialExp[i] = skills.experience(i);
			final int[] initialLevels = this.initialLevels;
			final Skills skills2 = arg0.skills;
			final int realLevel = skills2.realLevel(i);
			initialLevels[i] = realLevel;
			++i;
		}
		timer = ((arg1 == null) ? new Timer(0L) : arg1);
	}

	public int experience(final Rate arg0, final int arg1) {
		return (int) (this.experience(arg1) * arg0.time / timer.elapsed());
	}

	public int level(final int arg0) {
		if (arg0 < 0 || arg0 > 26) {
			throw new IllegalArgumentException("Out of bounds");
		}
		return this.ctx.skills.realLevel(arg0) - this.initialLevels[arg0];
	}

	public int experience(final int arg0) {
		if (arg0 < 0 || arg0 > 26) {
			throw new IllegalArgumentException("Out of bounds");
		}
		return this.ctx.skills.experience(arg0) - this.initialExp[arg0];
	}

	public SkillData(final ClientContext arg0) {
		this(arg0, new Timer(0L));
	}

	public int level(final Rate arg0, final int arg1) {
		return (int) (this.level(arg1) * arg0.time / timer.elapsed());
	}

	public long timeToLevel(final Rate arg0, final int arg1) {
		final double n;
		if ((n = this.experience(arg0, arg1)) == 0.0) {
			return 0L;
		}
		return (long) ((this.ctx.skills.experienceAt(this.ctx.skills.realLevel(arg1) + 1) - this.ctx.skills.experience(arg1)) / n * arg0.time);
	}

	public enum Rate {
		MINUTE(60000.0),
		HOUR(3600000.0),
		DAY(8.64E7),
		WEEK(6.048E8);

		public final double time;


		Rate(double v) {
			time = v;
		}

		public double getTime() {
			return this.time;
		}

		public static final Rate[] values;

		static {
			values = new Rate[]{Rate.MINUTE, Rate.HOUR, Rate.DAY, Rate.WEEK};
		}
	}

	public static int getExperenceToNextLevel(ClientContext ctx, final int index) {
		try {
			final int level = ctx.skills.realLevel(index);
			return ctx.skills.experienceAt(level + 1) - ctx.skills.experience(index);
		} catch (Exception ignored) {
			return Integer.MAX_VALUE;
		}
	}
}
