package org.logicail.rsbot.scripts.framework.context.rt4.providers;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 12:41
 */
public class IChat extends ClientAccessor {
	public IChat(ClientContext ctx) {
		super(ctx);
	}

	public Component getContinue() {
		return getComponentByText("Click here to continue", "Click to continue");
	}

	public boolean clickContinue() {
		final Component component = getContinue();
		if (component.valid() && component.click()) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return visible("Please wait...");
				}
			}, 50, 5)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !visible("Please wait...");
					}
				}, 50, 5);
			} else {
				Condition.sleep(250);
			}
			return true;
		}
		return false;
	}

	public boolean queryContinue() {
		final Component component = getContinue();
		return component.valid() && component.visible();
	}

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
				if (search != null && search.valid() && search.visible()) {
					return search;
				}
			}
		}

		return null;
	}

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

	public boolean visible(String... text) {
		return getComponentByText(text).valid();
	}
}
