package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
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
public class IItemStore extends ItemQuery<Item> {
	private final Component component;

	public IItemStore(IMethodContext context, Component component) {
		super(context);
		this.component = component;
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new LinkedList<Item>();

		for (Component child : component.getChildren()) {
			if (child.isValid() && child.getItemId() != -1) {
				items.add(new Item(ctx, child));
			}
		}

		return items;
	}

	@Override
	public Item getNil() {
		return ctx.backpack.getNil();
	}
}
