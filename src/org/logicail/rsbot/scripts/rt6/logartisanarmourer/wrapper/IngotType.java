package org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 14:52
 */
public enum IngotType {
	BRONZE("Bronze", 20502, -1, -1, -1, 20647),
	IRON("Iron", 20503, 20632, 20637, 20642, 20648),
	STEEL("Steel", 20504, 20633, 20638, 20643, 20649),
	MITHRIL("Mithril", -1, 20634, 20639, 20644, 20650),
	ADAMANT("Adamant", -1, 20635, 20640, 20645, 20651),
	RUNE("Rune", -1, 20636, 20641, 20646, 20652);

	private final String name;
	private final int idRepairTracks;
	private final int[] ids;

	IngotType(String name, int idRepairTracks, int... ids) {
		this.name = name;
		this.idRepairTracks = idRepairTracks;
		this.ids = ids;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getID(IngotGrade grade) {
		return ids[grade.ordinal()];
	}

	public int getIngotIdRepairTracks() {
		return idRepairTracks;
	}
}
