package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.util.TargetableRectangle;
import org.powerbot.script.Condition;

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

	public static boolean setHitType(final IClientContext ctx, final HitType hitType) {
		if (getCurrentHitType(ctx) == hitType) {
			return true;
		}

		final org.powerbot.script.rt6.Component buttonWidget = hitType.getButtonWidget(ctx);
		if (buttonWidget.valid()) {
			final Rectangle rect = buttonWidget.boundingRect();
			rect.height -= 10;
			TargetableRectangle targetableRectangle = new TargetableRectangle(ctx, rect);

			if (targetableRectangle.interact("Select") && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return getCurrentHitType(ctx) == hitType;
				}
			}, 175, 10)) {
				ctx.sleep(400);
			}
		}

		return getCurrentHitType(ctx) == hitType;
	}

	public static HitType getCurrentHitType(IClientContext ctx) {
		return HitType.values()[ctx.varpbits.varpbit(132, 16, 3)];
	}

	private org.powerbot.script.rt6.Component getButtonWidget(IClientContext ctx) {
		return ctx.widgets.component(MakeSword.WIDGET_SWORD_INTERFACE, widgetButton);
	}
}
