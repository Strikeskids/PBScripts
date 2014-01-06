package org.logicail.rsbot.util;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/01/14
 * Time: 12:01
 */
public class LogicailArea extends org.powerbot.script.wrappers.Area {
	final int plane;

	private Tile[] tiles = null;

	public LogicailArea(Tile... tile) {
		super(tile);
		plane = tile[0].plane;
	}

	public LogicailArea(Tile tile, Tile tile2) {
		super(tile, tile2);
		plane = tile.plane;
	}

	public Tile getRandomReachable(MethodContext ctx, double distanceFromCenter) {
		final Tile centralTile = getCentralTile();
		List<Tile> reachable = new ArrayList<Tile>();
		final Tile[] tileArray = getTileArray();
		for (Tile tile : tileArray) {
			if (tile.distanceTo(centralTile) <= distanceFromCenter && tile.getMatrix(ctx).isReachable()) {
				reachable.add(tile);
			}
		}

		if (!reachable.isEmpty()) {
			return reachable.get(Random.nextInt(0, reachable.size()));
		}

		return centralTile;
	}

	public Tile[] getTileArray() {
		if (tiles != null) {
			return tiles;
		}

		final Polygon polygon = getPolygon();
		final Rectangle bounds = polygon.getBounds();
		final Tile[] array = new Tile[bounds.width * bounds.height];
		int numberOfTiles = 0;

		int x = 0;
		while (x < bounds.width) {
			int y = 0;
			while (y < bounds.height) {
				final int tempX = bounds.x + x;
				final int tempY = bounds.y + y;
				if (polygon.contains(tempX, tempY)) {
					array[++numberOfTiles] = new Tile(tempX, tempY, plane);
				}
				++y;
			}
			++x;
		}
		this.tiles = Arrays.copyOf(array, numberOfTiles);
		return tiles;
	}
}
