package org.logicail.rsbot.scripts.rt6.bankorganiser.tasks;

import org.logicail.rsbot.scripts.rt6.bankorganiser.ItemData;
import org.powerbot.script.rt6.Item;

import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/03/14
 * Time: 19:39
 */
public class ItemSorter implements Comparator<Item> {
	public int size() {
		return order.size();
	}

	private final ItemData itemData;
	private final List<Integer> order;

	public ItemSorter(ItemData itemData, List<Integer> order) {
		this.itemData = itemData;
		this.order = order;
	}

	@Override
	public int compare(Item lhs, Item rhs) {
		final int indexlhs = order.indexOf(itemData.getId(lhs.id()));
		final int indexrhs = order.indexOf(itemData.getId(rhs.id()));

		if (indexlhs == -1 && indexrhs == -1) {
			return lhs.name().compareTo(rhs.name());
		}

		return indexlhs - indexrhs;
	}
}
