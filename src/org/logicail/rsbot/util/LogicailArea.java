package org.logicail.rsbot.util;

import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/01/14
 * Time: 12:01
 */
public class LogicailArea extends org.powerbot.script.wrappers.Area {
	public LogicailArea(Tile tile, Tile tile2) {
		super(tile, tile2);
	}

	public Tile[] getTileArray() {
		List<Tile> list = new LinkedList<Tile>();

		int plane = getCentralTile().plane;

		final Rectangle bounds = getPolygon().getBounds();
		int minx = (int) bounds.getMinX();
		int maxx = (int) bounds.getMaxX();
		int miny = (int) bounds.getMinY();
		int maxy = (int) bounds.getMaxY();

		for (int x = minx; x <=maxx; ++x) {
			for (int y = miny; y <= maxy; ++y) {
				Tile tile = new Tile(x, y, plane);
				if (contains(tile)) {
					list.add(tile);
				}
			}
		}

		return list.toArray(new Tile[list.size()]);
	}
}
