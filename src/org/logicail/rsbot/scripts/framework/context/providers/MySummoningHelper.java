package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 17:58
 */
public class MySummoningHelper extends ItemQuery<Item> {
	public static final int WIDGET_STORE = 671;
	public static final int WIDGET_STORE_ITEMS = 27;
	public MySummoningHelper(LogicailMethodContext context) {
		super(context);
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new LinkedList<Item>();

		for (Component component : ctx.widgets.get(WIDGET_STORE, WIDGET_STORE_ITEMS).getChildren()) {
			if (component.getItemId() != -1) {
				items.add(new Item(ctx, component));
			}
		}

		return items;
	}

	@Override
	public Item getNil() {
		return ctx.backpack.getNil();
	}
}
