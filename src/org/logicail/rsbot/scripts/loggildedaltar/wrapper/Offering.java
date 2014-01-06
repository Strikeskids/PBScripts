package org.logicail.rsbot.scripts.loggildedaltar.wrapper;

/**
 * User: logicail
 * Date: 27/05/12
 * Time: 23:24
 */
public enum Offering {
	ACCURSED_ASHES("Accursed ashes", 20266, 43.7),
	BABYDRAGON("Babydragon bones", 534, 105),
	BIG("Big bones", 532, 52.5),
	BONES("Bones", 526, 15.7),
	DAGANNOTH("Dagannoth bones", 6729, 437.5),
	DRAGON("Dragon bones", 536, 252),
	FAYRG("Fayrg bones", 4830, 294),
	FROST_DRAGON("Frost dragon bones", 18832, 630),
	IMPIOUS_ASHES("Impious ashes", 20264, 14),
	INFERNAL_ASHES("Infernal ashes", 20268, 218.7),
	OURG("Ourg bones", 14793, 490),
	OURG_ANCESTRAL("Ourg bones (Ancestral)", 4834, 490),
	RAURG("Raurg bones", 4832, 336),
	SHAIKAHAN("Shaikahan bones", 3123, 87.5),
	WYVERN("Wyvern bones", 6812, 175),
	ZOGRE("Zogre bones", 4812, 78.7);

	private final String name;
	private final int id;
	private final double xp;

	Offering(String name, int id, double xp) {
		this.name = name;
		this.id = id;
		this.xp = xp;
	}

	public int getId() {
		return id;
	}

	public double getXp() {
		return xp;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getNotedId() {
		return id + 1;
	}
}

