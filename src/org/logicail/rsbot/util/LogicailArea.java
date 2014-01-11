package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/01/14
 * Time: 12:01
 */
public class LogicailArea extends org.powerbot.script.wrappers.Area {
	protected final int plane;

	private Tile[] tiles = null;
	private Tile central;

	public LogicailArea(Tile... tile) {
		super(tile);
		plane = tile[0].plane;
	}

	public LogicailArea(Tile t1, Tile t2) {
		super(t1, t2);
		plane = t1.plane;
	}

	public Tile getRandomReachable(IMethodContext ctx, double distanceFromCenter) {
		final Tile centralTile = getCentralTile();
		List<Tile> reachable = Arrays.asList(getReachable(ctx));

		Collections.shuffle(reachable);
		for (Tile tile : reachable) {
			if (IMovement.Euclidean(centralTile, tile) <= distanceFromCenter) {
				return tile;
			}
		}

		return centralTile.randomize(2, 2);
	}

	public Tile[] getReachable(MethodContext ctx) {
		List<Tile> reachable = new ArrayList<Tile>();

		for (Tile tile : getTileArray()) {
			if (tile.getMatrix(ctx).isReachable()) {
				reachable.add(tile);
			}
		}

		return reachable.toArray(new Tile[reachable.size()]);
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
					array[numberOfTiles++] = new Tile(tempX, tempY, plane);
				}
				++y;
			}
			++x;
		}
		this.tiles = Arrays.copyOf(array, numberOfTiles);
		return tiles;
	}

	@Override
	public Tile getCentralTile() {
		if (central != null) {
			return central;
		}

		final Point point = PolygonUtils.polygonCenterOfMass(getPolygon());
		central = new Tile(point.x, point.y, plane);

		return central;
	}
}
