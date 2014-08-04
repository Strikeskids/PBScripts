package com.logicail.loader.rt6.wrapper.requirements;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 20:44
 */
public class ItemRequirement implements ListRequirement<ItemRequirement> {
	private final int id;
	private final int quantity;

	public ItemRequirement(int id, int quantity) {
		this.id = id;
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "ItemRequirement{" +
				"id=" + id +
				", quantity=" + quantity +
				'}';
	}

	public int getId() {
		return id;
	}

	@Override
	public java.util.List<ItemRequirement> get(Map params) {
		return null;
	}
}
