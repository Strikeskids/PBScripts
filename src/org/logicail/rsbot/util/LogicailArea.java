package org.logicail.rsbot.util;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.ArrayList;
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

	public LogicailArea(Tile... tile) {
		super(tile);
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

	public Tile getRandomReachable(MethodContext ctx, double distanceFromCenter) {
		final Tile centralTile = getCentralTile();
		List<Tile> reachable = new ArrayList<Tile>();
		for (Tile tile : getTileArray()) {
			if (centralTile.distanceTo(tile)<= distanceFromCenter && tile.getMatrix(ctx).isReachable()) {
				reachable.add(tile);
			}
		}

		if (!reachable.isEmpty()) {
			return reachable.get(Random.nextInt(0, reachable.size()));
		}

		return centralTile;
	}
}
