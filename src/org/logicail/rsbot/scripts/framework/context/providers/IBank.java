package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.wrappers.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/01/14
 * Time: 14:37
 */
public class IBank extends Bank {
	public static final int WIDGET_BANK_ITEMS = 54;
	private static final int WIDGET_BOUNDS = 48;

	/**
	 * Backpack when bank is open
	 */
	public final IItemStore backpack;

	public IBank(IMethodContext ctx) {
		super(ctx);
		backpack = new IItemStore(ctx, ctx.widgets.get(WIDGET, WIDGET_BANK_ITEMS));
	}

	public Component getWidget() {
		return ctx.widgets.get(WIDGET, WIDGET_BOUNDS);
	}
}
