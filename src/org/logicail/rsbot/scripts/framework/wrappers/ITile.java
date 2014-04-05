package org.logicail.rsbot.scripts.framework.wrappers;

import org.powerbot.script.Random;
import org.powerbot.script.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/04/2014
 * Time: 20:59
 */
public class ITile {
	public static Tile randomize(final Tile tile, final int left, final int right, final int down, final int up) {
		return tile.derive(Random.nextInt(left, right + 1), Random.nextInt(down, up + 1));
	}

	public static Tile randomize(final Tile tile, final int x, final int y) {
		return randomize(tile, -x, x, -y, y);
	}
}
