package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 14:52
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
