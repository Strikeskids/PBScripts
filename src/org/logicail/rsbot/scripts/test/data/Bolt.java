package org.logicail.rsbot.scripts.test.data;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 12/01/14
 * Time: 19:13
 */
public enum Bolt {
	NONE(-1, "None"),
	HEADLESS_ARROW(52, "Headless Arrows"),
	BRONZE_BOLT(9375, "Bronze Bolts"),
	IRON_BOLT(9377, "Iron Bolts"),
	STEEL_BOLT(9378, "Steel Bolts"),
	MITHRIL_BOLT(9379, "Mithril Bolts"),
	ADAMANT_BOLT(9380, "Adamant Bolts"),
	RUNE_BOLT(9381, "Rune Bolts");

	private final int boltId;
	private final String boltType;

	Bolt(final int boltId, final String boltType) {
		this.boltId = boltId;
		this.boltType = boltType;
	}

	public int getBolt() {
		return boltId;
	}

	public String toString() {
		return boltType;
	}
}