package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.methods.Chat;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 18:18
 */
public class IChat extends Chat {
	public static final int WIDGET_INPUT = 1469;
	public static final int WIDGET_INPUT_TEXT_CHILD = 3;

	public IChat(IMethodContext context) {
		super(context);
	}

	public Component getInputTextBox() {
		return ctx.widgets.get(WIDGET_INPUT, WIDGET_INPUT_TEXT_CHILD);
	}

	public boolean waitForInputWidget() {
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return isInputWidgetOpen();
			}
		}, Random.nextInt(400, 800), Random.nextInt(4, 9));
	}

	public boolean isInputWidgetOpen() {
		return getInputTextBox().isVisible();
	}
}
