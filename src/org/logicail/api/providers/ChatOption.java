package org.logicail.api.providers;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.powerbot.script.wrappers.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 11:48
 */
public class ChatOption extends LogicailMethodProvider {
	private int number;
	private Component child;

	/**
	 * Constructs a new ChatOption.
	 *
	 * @param number The number this option is bound to.
	 * @param child  An instance of the WidgetChild this option is bound to.
	 */
	public ChatOption(LogicailMethodContext ctx, int number, Component child) {
		super(ctx);
		this.number = number;
		this.child = child;
	}

	/**
	 * @return The number this option is bound to.
	 */
	public int getOptionNumber() {
		return number;
	}

	/**
	 * @return An instance of the WidgetChild this option is bound to.
	 */
	public Component getWidgetChild() {
		return child;
	}

	/**
	 * @param isValid   <tt>true</tt> if the result must valid.
	 * @param isVisible <tt>true</tt> if the result must be visible.
	 * @return <tt>true</tt> if and only if the Widget including all its children match the criteria, otherwise <tt>false</tt>.
	 */
	public boolean revalidate(final boolean isValid, final boolean isVisible) {
		Component w = child.getParent();
		if (w != null) {
			w = ctx.widgets.get(w.getIndex(), child.getIndex());
		}
		return !(w == null || (isValid && !w.isValid()) || (isVisible && !w.isVisible()));
	}

	/**
	 * Interacts with this option, using either the mouse or the keyboard.
	 *
	 * @param key <tt>true</tt> if and only if the keyboard should be used, <tt>false</tt> otherwise.
	 * @return -1 if the mouse failed to interact, 0 is the keyboard was used (regardless of success!) or +1 if the mouse successfully interacted.
	 */
	public int select(final boolean key) {
		if (key) {
			ctx.keyboard.send(number == -1 ? " " : Integer.toString(number), false);
			return 0;
		} else {
			return child.click(true) ? 1 : -1;
		}
	}

	/**
	 * Interacts with this option, using either the mouse or the keyboard.
	 *
	 * @param key      <tt>true</tt> if and only if the keyboard should be used, <tt>false</tt> otherwise.
	 * @param maxSleep The amount of milliseconds the method may wait for the game to respond.
	 * @return <tt>true</tt> if and only if the option was selected successfully
	 */
	public boolean select(final boolean key, final int maxSleep) {
		if (select(key) > -1) {
			final long timeout = System.currentTimeMillis() + maxSleep;
			while (System.currentTimeMillis() < timeout) {
				if (!revalidate(child.isValid(), child.isVisible())) {
					return true;
				}
				sleep(50, 100);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getClass().getName() + "(" + number + ", " + child.getText() + ")";
	}
}
