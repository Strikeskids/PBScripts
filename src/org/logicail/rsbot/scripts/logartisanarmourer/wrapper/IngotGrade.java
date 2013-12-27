package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 14:54
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
