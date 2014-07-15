package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 13:59
 */
public enum CropState {
	DEAD(Color.black),
	DISEASED(Color.yellow),
	EMPTY(Color.gray.darker()),
	GROWING(Color.green.brighter()),
	READY(Color.green.darker().darker()),
	WATERED(Color.blue.brighter()),
	WEEDS(new Color(102, 51, 0).brighter());
	private final Color color;
	private final String pretty;

	CropState(Color color) {
		this.color = color;
		pretty = IFarming.pretty(name());
	}

	@Override
	public String toString() {
		return pretty;
	}

	public Color color() {
		return color;
	}
}
