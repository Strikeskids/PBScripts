package org.logicail.rsbot.util;

import org.powerbot.script.Filter;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ItemQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/01/14
 * Time: 13:14
 */
public class ItemHelper {
	/**
	 * @param query
	 * @param ids      Ids of items you want in the order of preference
	 * @param quantity
	 * @return
	 */
	public static Item getFirst(ItemQuery<Item> query, int[] ids, final int quantity) {
		for (int id : ids) {
			for (Item item : query.select().id(id).select(new Filter<Item>() {
				@Override
				public boolean accept(Item item) {
					return item.stackSize() >= quantity;
				}
			})) {
				return item;
			}
		}

		return query.nil();
	}
}
