package org.logicail.rsbot.scripts.framework.context.providers.farming;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 14:01
 */
public enum Herb {
	UNKNOWN(-3, -1, -1, "Unknown", 0, "Unknown", "", false),
	NONE(-2, -1, -1, "None", 0, "None", "", false),
	WEEDS(-1, -1, -1, "Weeds", 6055, "Weeds", "", false),
	EMPTY(0, -1, -1, "Empty", 0, "Empty", "", false),
	GUAM(1, 6, 5291, "Guam seed", 199, "Grimy guam", "Herbs", true),
	MARRENTILL(2, 6, 5292, "Marrentill seed", 201, "Grimy marrentill", "Herbs", true),
	TARROMIN(3, 6, 5293, "Tarromin seed", 203, "Grimy tarromin", "Herbs", true),
	HARRALANDER(4, 6, 5294, "Harralander seed", 205, "Grimy harralander", "Herbs", true),
	RANARR(5, 6, 5295, "Ranarr seed", 207, "Grimy ranarr", "Herbs", true),
	TOADFLAX(6, 6, 5296, "Toadflax seed", 3049, "Grimy toadflax", "Herbs", true),
	IRIT(7, 6, 5297, "Irit seed", 209, "Grimy irit", "Herbs", true),
	AVANTOE(8, 6, 5298, "Avantoe seed", 211, "Grimy avantoe", "Herbs", true),
	WERGALI(9, 6, 14870, "Wergali seed", 14836, "Grimy wergali", "Herbs", true),
	KWUARM(10, 6, 5299, "Kwuarm seed", 213, "Grimy kwuarm", "Herbs", true),
	SNAPDRAGON(11, 6, 5300, "Snapdragon seed", 3051, "Grimy snapdragon", "Herbs", true),
	CADANTINE(12, 6, 5301, "Cadantine seed", 215, "Grimy cadantine", "Herbs", true),
	LANTADYME(13, 6, 5302, "Lantadyme seed", 2485, "Grimy lantadyme", "Herbs", true),
	DWARF_WEED(14, 6, 5303, "Dwarf weed seed", 217, "Grimy dwarf weed", "Herbs", true),
	TORSTOL(15, 6, 5304, "Torstol seed", 219, "Grimy torstol", "Herbs", true),
	//FELLSTALK(16, 6, 21621, "Fellstalk seed", 21626, "Grimy fellstalk", "Herbs", true), //UNCODED
	//SPIRIT_WEED(17, 6, 12176, "Spirit weed seed", 12174, "Grimy spirit weed", "Herbs", true), //UNCODED
	GOUTWEED(18, 4, 6311, "Gout tuber", 3261, "Goutweed", "", false); //UNTESTED itemID, could be 4182, stages uncoded

	private final int id, stages, seedId, itemId;
	private final String seedString, itemString, interactString;
	private final boolean noteable;

	Herb(int id, int stages, int seedId, String seedString, int itemId, String itemString, String interactString, boolean noteable) {
		this.id = id;
		this.stages = stages;
		this.seedId = seedId;
		this.itemId = itemId;
		this.seedString = seedString;
		this.itemString = itemString;
		this.interactString = interactString;
		this.noteable = noteable;
	}

	public int id() {
		return id;
	}

	public int stages() {
		return stages;
	}

	public int seedId() {
		return seedId;
	}

	public int itemId() {
		return itemId;
	}

	public String seedString() {
		return seedString;
	}

	public String itemString() {
		return itemString;
	}

	public String interactString() {
		return interactString;
	}

	public boolean noteable() {
		return noteable;
	}

	public int noteId() {
		return noteable ? (itemId + 1) : itemId;
	}

}
