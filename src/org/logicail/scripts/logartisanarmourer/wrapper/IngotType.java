package org.logicail.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:26
 */
public enum IngotType {
	BRONZE("Bronze"),
	IRON("Iron"),
	STEEL("Steel"),
	MITHRIL("Mithril"),
	ADAMANT("Adamant"),
	RUNE("Rune");

	private final String name;

	IngotType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}