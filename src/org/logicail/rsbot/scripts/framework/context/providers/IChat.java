package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
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
		}, 250, 10);
	}

	public boolean isInputWidgetOpen() {
		return getInputTextBox().isVisible();
	}

	public boolean sendInput(String input) {
		final Component textBox = getInputTextBox();
		if (textBox.isValid() && textBox.isVisible()) {
			String text = textBox.getText();
			if (text == null || !text.equalsIgnoreCase(input)) {
				if (text != null) {
					for (int i = 0; i <= text.length(); ++i) {
						ctx.keyboard.send("{VK_BACK_SPACE down}");
						sleep(5, 50);
						ctx.keyboard.send("{VK_BACK_SPACE up}");
						sleep(5, 50);
					}
				}
				ctx.keyboard.send(input);
				text = textBox.getText();
			}
			return text != null && text.equalsIgnoreCase(input) && textBox.isValid() && textBox.isVisible() && ctx.keyboard.sendln("");
		}

		return false;
	}
}
