package org.logicail.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:26
 */
public enum IngotGrade {
	ONE("One"),
	TWO("Two"),
	THREE("Three"),
	FOUR("Four");

	private final String name;

	IngotGrade(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}