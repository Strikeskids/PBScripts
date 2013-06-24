package org.logicail.framework.banking;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 21:24
 */
public class RequiredEquipment extends AbstractRequiredItem {
	/**
	 * Require 1 * 1 worn
	 *
	 * @param id
	 */
	public RequiredEquipment(int id) {
		super(new int[]{id}, 1);
	}
}
