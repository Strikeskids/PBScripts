package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Chat;
import org.powerbot.script.rt6.Component;

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

	public IChat(IClientContext context) {
		super(context);
	}

	public Component getInputTextBox() {
		return ctx.widgets.component(WIDGET_INPUT, WIDGET_INPUT_TEXT_CHILD);
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
		return getInputTextBox().visible();
	}

	public boolean sendInput(String input) {
		final Component textBox = getInputTextBox();
		if (textBox.valid() && textBox.visible()) {
			String text = textBox.text();
			if (text == null || !text.equalsIgnoreCase(input)) {
				if (text != null) {
					for (int i = 0; i <= text.length(); ++i) {
						ctx.keyboard.send("{VK_BACK_SPACE down}");
						Condition.sleep(Random.nextInt(5, 50));
						ctx.keyboard.send("{VK_BACK_SPACE up}");
						Condition.sleep(Random.nextInt(5, 50));
					}
				}
				ctx.keyboard.send(input);
				text = textBox.text();
			}
			return text != null && text.equalsIgnoreCase(input) && textBox.valid() && textBox.visible() && ctx.keyboard.sendln("");
		}

		return false;
	}
}
