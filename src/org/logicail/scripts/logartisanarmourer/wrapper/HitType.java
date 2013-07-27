package org.logicail.scripts.logartisanarmourer.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:39
 */
public enum HitType {
	CAREFUL(60),
	SOFT(59),
	MEDIUM(58),
	HARD(57);
	private final int widgetButton;

	HitType(final int widgetButton) {
		this.widgetButton = widgetButton;
	}

	public int getWidgetButton() {
		return widgetButton;
	}
}
