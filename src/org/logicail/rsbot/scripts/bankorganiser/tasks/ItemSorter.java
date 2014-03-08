package org.logicail.rsbot.scripts.bankorganiser.tasks;

import org.logicail.rsbot.scripts.bankorganiser.ItemData;
import org.powerbot.script.wrappers.Item;

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

	private final List<Integer> order;

	public ItemSorter(List<Integer> order) {
		this.order = order;
	}

	@Override
	public int compare(Item lhs, Item rhs) {
		final int indexlhs = order.indexOf(ItemData.getId(lhs.getId()));
		final int indexrhs = order.indexOf(ItemData.getId(rhs.getId()));

		if (indexlhs == -1 && indexrhs == -1) {
			return lhs.getName().compareTo(rhs.getName());
		}

		return indexlhs - indexrhs;
	}
}
