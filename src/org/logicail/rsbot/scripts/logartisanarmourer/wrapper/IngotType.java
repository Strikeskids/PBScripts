package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 14:52
 */
public enum IngotType {
	BRONZE("Bronze", 20631, -1, -1, 20647), // 20632
	IRON("Iron", 20632, 20637, 20642, 20648),
	STEEL("Steel", 20633, 20638, 20643, 20649),
	MITHRIL("Mithril", 20634, 20639, 20644, 20650),
	ADAMANT("Adamant", 20635, 20640, 20645, 20651),
	RUNE("Rune", 20636, 20641, 20646, 20652);

	private final String name;
	private final int[] ids;

	IngotType(String name, int... ids) {
		this.name = name;
		this.ids = ids;
	}

	public int getIngotIdRepairTracks() {
		return ids[0] - 129;
	}

	public int getID(IngotGrade grade) {
		return ids[grade.ordinal()];
	}

	@Override
	public String toString() {
		return name;
	}
}
