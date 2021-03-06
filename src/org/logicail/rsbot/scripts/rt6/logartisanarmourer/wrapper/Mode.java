package org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 17:01
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
