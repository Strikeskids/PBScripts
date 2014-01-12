package org.logicail.rsbot.scripts.test.data;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 12/01/14
 * Time: 19:12
 */
public enum Option {
	NONE(0, "None"),
	SHAFTS(0, "Shafts"),
	SHORTBOW(2, "Shortbow"),
	STOCK(6, "Stock"),
	LONGBOW(10, "Shieldbow");

	private final int widget;
	private final String bowType;

	Option(final int widget, final String bowType) {
		this.widget = widget;
		this.bowType = bowType;
	}

	public int getWidget() {
		return widget;
	}

	public String toString() {
		return bowType;
	}
}