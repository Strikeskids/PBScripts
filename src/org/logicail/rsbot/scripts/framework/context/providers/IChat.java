package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Chat;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Widget;

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
						ctx.input.send("{VK_BACK_SPACE down}");
						Condition.sleep(Random.nextInt(5, 50));
						ctx.input.send("{VK_BACK_SPACE up}");
						Condition.sleep(Random.nextInt(5, 50));
					}
				}
				ctx.input.send(input);
				text = textBox.text();
			}
			return text != null && text.equalsIgnoreCase(input) && textBox.valid() && textBox.visible() && ctx.input.sendln("");
		}

		return false;
	}

//	public Component getChat(String needle) {
//		for (Component child : ctx.widgets.widget(0).components()) {
//			if (child.valid()) {
//				final Component search = getComponentByText(child, needle);
//				if (search.valid()) {
//					return search;
//				}
//			}
//		}
//		return ctx.widgets.component(0, 0);
//	}

	/**
	 * Recursively search a <code>component</code> for visible components with one of the texts from <code>needle</code>
	 *
	 * @param component to search
	 * @param needle    text to find
	 * @return <code>null</code> if no matches found, otherwise the visible component
	 */
	private Component getComponentByText(Component component, String... needle) {
		final String text = component.text().toLowerCase();
		for (String s : needle) {
			if (text.contains(s.toLowerCase())) {
				return component;
			}
		}

		for (Component child : component.components()) {
			if (child.valid()) {
				final Component search = getComponentByText(child, needle);
				if (search.valid() && search.visible()) {
					return search;
				}
			}
		}

		return null;
	}

	/**
	 * Find a visible component which contains one of text from <code>needle</code>
	 *
	 * @return the component or <code>ctx.widgets.widget(0).component(0)</code> if non found therefore use {@link Component#valid()} to see if it was found
	 */
	public Component getComponentByText(String... needle) {
		for (Widget widget : ctx.widgets.array()) {
			for (Component component : widget.components()) {
				if (component.valid()) {
					final Component search = getComponentByText(component, needle);
					if (search != null && search.valid() && search.visible()) {
						return search;
					}
				}
			}
		}
		return ctx.widgets.widget(0).component(0);
	}

	public boolean isTextVisible(String needle) {
		return getComponentByText(needle).visible();
	}
}
