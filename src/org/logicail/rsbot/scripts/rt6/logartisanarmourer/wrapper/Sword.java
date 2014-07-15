package org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.swords.MakeSword;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Skills;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/12/2012
 * Time: 00:18
 */
public enum Sword {
	First(71, 170, 34),
	Second(155, 171, 35),
	Third(156, 172, 36),
	Fourth(157, 173, 37),
	Fifth(158, 174, 38),
	Sixth(159, 175, 39),
	Seventh(160, 176, 40),
	Eighth(161, 177, 41),
	Ninth(162, 178, 42),
	Tenth(163, 179, 43),
	Eleventh(164, 180, 44),
	Twelfth(165, 181, 45),
	Thirteenth(166, 182, 46),
	Fourteenth(167, 183, 47),
	Fifteenth(168, 184, 48),
	Sixteenth(169, 185, 49);
	private final int targetWidgetId;
	private final int currentWidgetId;
	private final int buttonWidgetId;

	Sword(int targetWidgetId, final int currentWidgetId, final int buttonWidgetId) {
		this.targetWidgetId = targetWidgetId;
		this.currentWidgetId = currentWidgetId;
		this.buttonWidgetId = buttonWidgetId;
	}

	/**
	 * Get the number of parts still requiring a hit
	 *
	 * @return
	 */
	private static int getSwordPartsRemaining(IClientContext ctx) {
		int hits = 0;
		for (Sword sword : Sword.values()) {
			if (sword.validate(ctx)) {
				hits++;
			}
		}
		return hits;
	}

	public boolean clickButton(final IClientContext ctx, final MakeSword makeSword) {
		final int cooldown = makeSword.getCooldown();
		final int current = getCurrent(ctx);
		if (getTarget(ctx) > getCurrent(ctx)) {
			final Component button = ctx.widgets.component(MakeSword.WIDGET_SWORD_INTERFACE, buttonWidgetId);
			if (button.valid() && button.interact("Select")) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().animation() == -1 &&
								makeSword.getCooldown() != cooldown && current != getCurrent(ctx);
					}
				}, Random.nextInt(250, 350), Random.nextInt(18, 25));
			}
		}
		return false;
	}

	public int getCurrent(IClientContext ctx) {
		try {
			return Integer.parseInt(ctx.widgets.component(MakeSword.WIDGET_SWORD_INTERFACE, currentWidgetId).text());
		} catch (Exception e) {
			return -1;
		}
	}

	public int getTarget(IClientContext ctx) {
		try {
			return Integer.parseInt(ctx.widgets.component(MakeSword.WIDGET_SWORD_INTERFACE, targetWidgetId).text());
		} catch (Exception e) {
			return -1;
		}
	}

	public HitType getRequiredHitType(IClientContext ctx, MakeSword makeSword) {
		final int hitsNeeded = getHitsNeeded(ctx);
		if (hitsNeeded <= 0) {
			return null;
		}

		boolean isTip = this == Eighth || this == Sixteenth;
		if (ctx.skills.realLevel(Skills.SMITHING) >= 95) {
			if (hitsNeeded >= 4) {
				return HitType.HARD;
			} else if (hitsNeeded == 3) {
				return HitType.MEDIUM;
			} else if (hitsNeeded == 2) {
				final int remaining = getSwordPartsRemaining(ctx);
				// can we soft hit to completion
				if (isTip || remaining * 1.5d < makeSword.getCooldown()) {
					return HitType.SOFT;
				} else {
					return HitType.MEDIUM;
				}
			} else if (hitsNeeded == 1) {
				return HitType.SOFT;
			}
		} else {
			if (isTip) {
				if (hitsNeeded >= 5) {
					return HitType.HARD;
				} else if (hitsNeeded >= 3) {
					return HitType.MEDIUM;
				} else if (hitsNeeded >= 2) {
					return HitType.SOFT;
				}
			} else {
				if (hitsNeeded >= 4) {
					return HitType.HARD;
				} else if (hitsNeeded >= 2) {
					return HitType.MEDIUM;
				} else if (hitsNeeded >= 1) {
					return HitType.SOFT;
				}
			}

			if (hitsNeeded == 1) {
				final int swordPartsRemaining = getSwordPartsRemaining(ctx);
				if (swordPartsRemaining * 2 <= makeSword.getCooldown()) {
					return HitType.CAREFUL;
				} else {
					return HitType.SOFT;
				}
			}
		}
		return null;
	}

	public int getHitsNeeded(IClientContext ctx) {
		if (!validate(ctx)) {
			return 0;
		}
		return getTarget(ctx) - getCurrent(ctx);
	}

    /*
    Hit	Percentage of hit
                                5	4	  3	    2	1	  0
    Hard	                    10%	17.5% 25%	25%	17.5% 5%
    Medium	                    0%	0%	  17.5%	60%	17.5% 5%
    Soft (below 95)	            0%	0%	  0%	35%	50%	  15%
    Soft (above 95)	            0%	0%	  0%	0%	80%	  20%
    Careful (uses 2 cooldown)	0%	0%	  0%	0%	100%  0%
     */

	boolean validate(IClientContext ctx) {
		return getTarget(ctx) > getCurrent(ctx);
	}
}
