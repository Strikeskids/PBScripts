package org.logicail.api.methods.providers;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Widgets;
import org.powerbot.script.wrappers.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 14:02
 */
public class MyWidgets extends Widgets {
	public MyWidgets(MethodContext ctx) {
		super(ctx);
	}

	public Component getContinue() {
		Component button = get(752, 5);
		if (button != null && button.isValid() && button.getTextColor() == 128 && button.getRelativeLocation().x == 0) {
			return button;
		}

		button = get(1184, 18);
		if (button != null && button.isValid()) {
			return button;
		}

		button = get(1186, 8);
		if (button != null && button.isValid()) {
			return button;
		}

		button = get(1191, 18);
		if (button != null && button.isValid()) {
			return button;
		}

		return null;
	}

	public boolean canContinue() {
		return getContinue() != null;
	}

	public boolean clickContinue() {
		final Component widgetChild = getContinue();
		return widgetChild != null && widgetChild.click(true); // TODO: Check getTooltip for interact action
	}
}
