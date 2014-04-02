package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ItemQuery;

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

	public IItemStore(IClientContext context, Component component) {
		super(context);
		this.component = component;
	}

	public boolean isOpen() {
		return component.valid();
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new LinkedList<Item>();

		for (Component child : component.components()) {
			if (child.valid() && child.itemId() != -1) {
				items.add(new Item(ctx, child));
			}
		}

		return items;
	}

	@Override
	public Item nil() {
		return ctx.backpack.nil();
	}
}
