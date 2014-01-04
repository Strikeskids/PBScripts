package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.methods.Chat;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Component;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 18:18
 */
public class MyChat extends Chat {
	public static final int WIDGET_INPUT = 752;
	public static final int WIDGET_INPUT_TEXT_CHILD = 5;
	public MyChat(LogicailMethodContext context) {
		super(context);
	}

	public boolean waitForInputWidget() {
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return isInputWidgetOpen();
			}
		});
	}

	public boolean isInputWidgetOpen() {
		return getInputTextBox().isOnScreen();
	}

	public Component getInputTextBox() {
		return ctx.widgets.get(WIDGET_INPUT, WIDGET_INPUT_TEXT_CHILD);
	}
}
