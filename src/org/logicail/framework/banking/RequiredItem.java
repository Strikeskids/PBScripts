package org.logicail.framework.banking;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 22:05
 */
public class RequiredItem extends AbstractRequiredItem {
	/**
	 * Require 1 * 1
	 *
	 * @param ids
	 * @param quantity
	 */
	public RequiredItem(int[] ids, int quantity) {
		super(ids, quantity);
	}
}
