package org.logicail.rsbot.scripts.test.data;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 12/01/14
 * Time: 19:12
 */
public enum Log {
	NONE(-1, -1, -1, "None", new int[] {-1, -1}),
	LOG(1511, 50, 48, "Normal", new int[] {839, 841}),
	OAK(1521, 54, 56, "Oak", new int[] {845, 843}),
	WILLOW(1519, 64, 58, "Willow", new int[] {847, 849}),
	MAPLE(1517, 64, 62, "Maple", new int[] {851, 853}),
	YEW(1515, 68, 66, "Yew", new int[] {855, 857}),
	MAGIC(1513, 72, 70, "Magic", new int[] {859, 861});

	private final int logId;
	private final int bowId;
	private final String logType;
	private final int longbowId;
	private final int[] finishedId;

	Log(final int logId, final int bowId, final int longbowId, final String logType, final int[] finishedId) {
		this.logId = logId;
		this.bowId = bowId;
		this.longbowId = longbowId;
		this.logType = logType;
		this.finishedId = finishedId;
	}

	public int getLog() {
		return logId;
	}

	public int getBow() {
		return bowId;
	}

	public String toString() {
		return logType;
	}

	public int getLongBow() {
		return longbowId;
	}

	public int[] getFinished() {
		return finishedId;
	}

}
