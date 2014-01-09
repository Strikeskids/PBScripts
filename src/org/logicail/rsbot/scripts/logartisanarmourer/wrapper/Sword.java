package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;

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

	public boolean clickButton(final IMethodContext ctx, final MakeSword makeSword) {
		final int cooldown = makeSword.getCooldown();
		final int current = getCurrent(ctx);
		if (getTarget(ctx) > getCurrent(ctx)) {
			final Component button = ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, buttonWidgetId);
			if (button.isValid() && button.interact("Select")) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().getAnimation() == -1 &&
								(makeSword.getCooldown() != cooldown
										|| current != getCurrent(ctx));
					}
				});
			}
		}
		return false;
	}

	int getCurrent(IMethodContext ctx) {
		try {
			return Integer.parseInt(ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, currentWidgetId).getText());
		} catch (Exception e) {
			return -1;
		}
	}

	int getTarget(IMethodContext ctx) {
		try {
			return Integer.parseInt(ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, targetWidgetId).getText());
		} catch (Exception e) {
			return -1;
		}
	}

	public HitType getRequiredHitType(IMethodContext ctx, MakeSword makeSword) {
		final int hitsNeeded = getHitsNeeded(ctx);
		if (hitsNeeded <= 0) {
			return null;
		}

		switch (hitsNeeded) {
			case 1:
				if (ctx.skills.getRealLevel(Skills.SMITHING) < 95 && makeSword.getCooldown() >= 2) {
					final int hitsRemaining = getSwordPartsRemaining(ctx);
					if (hitsRemaining * 2 <= makeSword.getCooldown()) {
						// Have to risk some Softs
						return HitType.CAREFUL;
					} /*else {
						LogArtisanWorkshop.get().getLogHandler().print("Not enough cooldown, risking soft");
					}*/
				}
				return HitType.SOFT; // Take chance with soft
			case 2:
				return HitType.SOFT;
			case 3:
				return HitType.MEDIUM;
			default:
				if ((this == Eighth || this == Sixteenth) && hitsNeeded <= 4) {
					return HitType.MEDIUM; // Don't risk chance of breaking tip
				}
				return HitType.HARD; // Ignore chance of 5
		}
	}

	public int getHitsNeeded(IMethodContext ctx) {
		if (!validate(ctx)) {
			return 0;
		}
		return getTarget(ctx) - getCurrent(ctx);
	}

	boolean validate(IMethodContext ctx) {
		return getTarget(ctx) > getCurrent(ctx);
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

	/**
	 * Get the number of parts still requiring a hit
	 *
	 * @return
	 */
	private static int getSwordPartsRemaining(IMethodContext ctx) {
		int hits = 0;
		for (Sword sword : Sword.values()) {
			if (sword.validate(ctx)) {
				hits++;
			}
		}
		return hits;
	}
}
