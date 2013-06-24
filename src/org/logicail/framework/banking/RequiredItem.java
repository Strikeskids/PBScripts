package org.logicail.framework.banking;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 20:58
 */
public abstract class RequiredItem {
	private final int[] ids;
	private final int quantity;

	public RequiredItem(int[] ids, int quantity) {
		this.ids = ids;
		this.quantity = quantity;
	}

	public int[] getIds() {
		return ids;
	}

	public int getQuantity() {
		return quantity;
	}
}
