package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;

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

	public static HitType getCurrentHitType(LogicailMethodContext ctx) {
		return HitType.values()[ctx.settings.get(132, 16, 3)];
	}

	Component getButtonWidget(LogicailMethodContext ctx) {
		return ctx.widgets.get(MakeSword.WIDGET_SWORD_INTERFACE, widgetButton);
	}

	public static boolean setHitType(final LogicailMethodContext ctx, final HitType hitType) {
		if (getCurrentHitType(ctx) == hitType) {
			return true;
		}

		final Component buttonWidget = hitType.getButtonWidget(ctx);
		if (buttonWidget.isValid() && buttonWidget.interact("Select")) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return getCurrentHitType(ctx) == hitType;
				}
			})) {
				ctx.game.sleep(200, 800);
			}
		}

		return getCurrentHitType(ctx) == hitType;
	}
}
