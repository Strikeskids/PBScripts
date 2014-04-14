package org.logicail.rsbot.scripts.framework.context.providers.farming;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 13:59
 */
public enum CropState {
	WEEDS(new Color(102, 51, 0)),
	GROWING(Color.green),
	WATERED(Color.blue),
	DISEASED(Color.green.brighter()),
	EMPTY(Color.gray),
	DEAD(Color.black),
	READY(Color.green.darker());
	private final Color color;

	CropState(Color color) {
		this.color = color;
	}

	public Color color() {
		return color;
	}
}
