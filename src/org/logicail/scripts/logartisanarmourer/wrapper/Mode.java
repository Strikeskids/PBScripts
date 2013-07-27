package org.logicail.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 19:45
 */
public enum Mode {
	BURIAL_ARMOUR("Burial Armour"),
	CEREMONIAL_SWORDS("Ceremonial Swords"),
	REPAIR_TRACK("Repair Tracks");
	//CANNON_REPAIR

	private final String name;

	Mode(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
