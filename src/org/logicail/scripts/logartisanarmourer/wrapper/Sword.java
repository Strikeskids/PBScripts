package org.logicail.scripts.logartisanarmourer.wrapper;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.providers.Condition;
import org.logicail.scripts.logartisanarmourer.tasks.swords.MakeSword;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.wrappers.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:39
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

	Sword(final int targetWidgetId, final int currentWidgetId, final int buttonWidgetId) {
		this.targetWidgetId = targetWidgetId;
		this.currentWidgetId = currentWidgetId;
		this.buttonWidgetId = buttonWidgetId;
	}

	/**
	 * Get the number of parts still requiring a hit
	 *
	 * @return
	 */
	private static int getSwordPartsRemaining(LogicailMethodContext ctx) {
		int hits = 0;
		for (Sword sword : Sword.values()) {
			if (sword.validate(ctx)) {
				hits++;
			}
		}
		return hits;
	}

	int getTarget(LogicailMethodContext ctx) {
		try {
			Integer.parseInt(ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, targetWidgetId).getText());
		} catch (Exception ignored) {
		}
		return -1;

	}

	int getCurrent(LogicailMethodContext ctx) {
		try {
			return Integer.parseInt(ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, currentWidgetId).getText());
		} catch (Exception ignored) {
		}
		return -1;
	}

	boolean validate(LogicailMethodContext ctx) {
		return getTarget(ctx) > getCurrent(ctx);
	}

	public int getHitsNeeded(LogicailMethodContext context) {
		if (!validate(context)) {
			return 0;
		}
		return getTarget(context) - getCurrent(context);
	}

	public HitType getRequiredHitType(LogicailMethodContext ctx) {
		final int hitsNeeded = getHitsNeeded(ctx);
		if (hitsNeeded <= 0) {
			return null;
		}

		switch (hitsNeeded) {
			case 1:
				if (ctx.skills.getRealLevel(Skills.SMITHING) < 95 && MakeSword.getCooldown(ctx) >= 2) {
					final int hitsRemaining = getSwordPartsRemaining(ctx);
					if (hitsRemaining * 2 <= MakeSword.getCooldown(ctx)) {
						// Have to risk some Softs
						return HitType.CAREFUL;
					}
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

	public boolean clickButton(final LogicailMethodContext ctx) {
		final int cooldown = MakeSword.getCooldown(ctx);
		final int current = getCurrent(ctx);
		if (getTarget(ctx) > getCurrent(ctx)) {
			final Component button = ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, buttonWidgetId);
			if (button.isValid() && button.interact("Select")) {
				return ctx.waiting.wait(6000, new Condition() {
					@Override
					public boolean validate() {
						return ctx.players.local().getAnimation() == -1 &&
								(MakeSword.getCooldown(ctx) != cooldown || current != getCurrent(ctx));
					}
				});
			}
		}
		return false;
	}

}
