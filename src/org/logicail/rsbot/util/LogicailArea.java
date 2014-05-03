package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.framework.wrappers.ITile;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/01/14
 * Time: 12:01
 */
public class LogicailArea extends Area {
	protected final int plane;
	private Set<Tile> tiles = null;

	public LogicailArea(Tile... tile) {
		super(tile);
		plane = tile[0].floor();
	}

	public LogicailArea(Tile t1, Tile t2) {
		super(t1, t2);
		plane = t1.floor();
	}

	public Tile getRandomReachable(IClientContext ctx, double distanceFromCenter) {
		final Tile centralTile = getCentralTile();
		java.util.List<Tile> reachable = new ArrayList<Tile>(getReachable(ctx));
		Collections.shuffle(reachable);

		for (Tile tile : reachable) {
			if (IMovement.Euclidean(centralTile, tile) <= distanceFromCenter) {
				return tile;
			}
		}

		return ITile.randomize(centralTile, 2, 2);
	}

	public Set<Tile> getReachable(ClientContext ctx) {
		HashSet<Tile> reachable = new HashSet<Tile>();

		for (Tile tile : getTileArray()) {
			if (tile.matrix(ctx).reachable()) {
				reachable.add(tile);
			}
		}

		return reachable;
	}

	public Set<Tile> getTileArray() {
		if (tiles != null) {
			return tiles;
		}

		final Polygon polygon = getPolygon();
		final Rectangle bounds = polygon.getBounds();
		final Set<Tile> set = new HashSet<Tile>();

		int x = 0;
		while (x < bounds.width) {
			int y = 0;
			while (y < bounds.height) {
				final int tempX = bounds.x + x;
				final int tempY = bounds.y + y;
				if (polygon.contains(tempX, tempY)) {
					set.add(new Tile(tempX, tempY, plane));
				}
				++y;
			}
			++x;
		}

		return this.tiles = set;
	}
}
