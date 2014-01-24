package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.util.TargetableRectangle;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/12/2012
 * Time: 00:53
 */
public enum HitType {
	CAREFUL(60),
	SOFT(59),
	MEDIUM(58),
	HARD(57);
	private final int widgetButton;

	HitType(final int widgetButton) {
		this.widgetButton = widgetButton;
	}

	public static boolean setHitType(final IMethodContext ctx, final HitType hitType) {
		if (getCurrentHitType(ctx) == hitType) {
			return true;
		}

		final Component buttonWidget = hitType.getButtonWidget(ctx);
		if (buttonWidget.isValid()) {
			final Rectangle rect = buttonWidget.getBoundingRect();
			rect.height -= 10;
			TargetableRectangle targetableRectangle = new TargetableRectangle(ctx, rect);

			if (targetableRectangle.interact("Select") && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return getCurrentHitType(ctx) == hitType;
				}
			}, Random.nextInt(150, 250), Random.nextInt(10, 13))) {
				ctx.game.sleep(200, 800);
			}
		}

		return getCurrentHitType(ctx) == hitType;
	}

	public static HitType getCurrentHitType(IMethodContext ctx) {
		return HitType.values()[ctx.settings.get(132, 16, 3)];
	}

	Component getButtonWidget(IMethodContext ctx) {
		return ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, widgetButton);
	}
}
