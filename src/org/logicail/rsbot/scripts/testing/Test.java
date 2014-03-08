package org.logicail.rsbot.scripts.testing;

import org.powerbot.script.methods.Equipment;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/03/14
 * Time: 13:09
 */
public class Test {
	public static void main(String[] args) {
		for (Equipment.Slot slot : Equipment.Slot.values()) {
			System.out.println(slot + " " + slot.getStorageIndex());
		}
	}
}
