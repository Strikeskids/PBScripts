package org.logicail.rsbot.scripts.framework.context.providers.farming;

import org.powerbot.script.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 22:07
 */
public class FarmingDynamicDefinition {
	protected final int object;
	protected final int setting;
	protected final int shift;
	protected final int mask;
	protected final Tile tile;
	protected final int[] children;

	public FarmingDynamicDefinition(int object, int setting, int shift, int mask, Tile tile, int[] children) {
		this.object = object;
		this.setting = setting;
		this.shift = shift;
		this.mask = mask;
		this.tile = tile;
		this.children = children;
	}
}
