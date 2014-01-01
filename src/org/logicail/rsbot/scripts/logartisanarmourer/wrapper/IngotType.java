package org.logicail.rsbot.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 14:52
 */
public enum IngotType {
	BRONZE("Bronze", 20531),
	IRON("Iron", 20532),
	STEEL("Steel", 20533),
	MITHRIL("Mithril", 20634),
	ADAMANT("Adamant", 20635),
	RUNE("Rune", 20636);

	private final String name;
	private final int gradeOneId;

	IngotType(String name, int gradeOneId) {
		this.name = name;
		this.gradeOneId = gradeOneId;
	}

	public int getIngotIdRepairTracks() {
		return gradeOneId - 30;
	}

	public int getID(IngotGrade grade) {
		if (grade == IngotGrade.ONE) {
			return gradeOneId;
		}

		return gradeOneId + (grade.ordinal() * 5) + ordinal() - 1 + ((grade.ordinal() >= IngotGrade.FOUR.ordinal()) ? 1 : 0);
	}
	/*

		return 20632 + (options.ingotGrade.ordinal() * 5) + options.ingotType.ordinal() - 1 + ((options.ingotGrade.ordinal() >= IngotGrade.FOUR.ordinal()) ? 1 : 0);

	 */

	@Override
	public String toString() {
		return name;
	}
}
