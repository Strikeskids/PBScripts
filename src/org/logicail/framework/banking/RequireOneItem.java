package org.logicail.framework.banking;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 21:35
 */
public class RequireOneItem extends AbstractRequiredItem {
	/**
	 * Require 1 * quantity
	 *
	 * @param ids
	 * @param quantity
	 */
	public RequireOneItem(int[] ids, int quantity) {
		super(ids, quantity);
	}
}
