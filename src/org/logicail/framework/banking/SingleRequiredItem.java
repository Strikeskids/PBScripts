package org.logicail.framework.banking;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 22:05
 */
public class SingleRequiredItem extends RequiredItem {
	public SingleRequiredItem(int id, int quantity) {
		super(new int[]{id}, quantity);
	}
}
