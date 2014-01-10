package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.*;
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

	public Tile findSpace(final IMethodContext ctx, int width, int height) {
		final Rectangle bounds = getPolygon().getBounds();
		final int minX = (int) bounds.getMinX();
		final int minY = (int) bounds.getMinY();
		Set<Tile> reachable = new HashSet<Tile>();
		Set<Tile> notReachable = new HashSet<Tile>();
		for (GameObject object : ctx.objects.select().select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				final GameObject.Type type = gameObject.getType();
				return type == GameObject.Type.INTERACTIVE || type == GameObject.Type.BOUNDARY;
			}
		})) {
			notReachable.add(object.getLocation());
		}

		for (Tile tile : getTileArray()) {
			if (!notReachable.contains(tile)) {
				if (!tile.getMatrix(ctx).isReachable()) {
					notReachable.add(tile);
				} else {
					reachable.add(tile);
				}
			}
		}

		Set<Tile> tiles = new HashSet<Tile>();

		Tile t = new Tile(minX, minY, plane);

		for (int x = 0; x >= 0 && x <= bounds.width - width; x++) {
			for (int y = 0; y >= 0 && y <= bounds.height - height; y++) {

				// Instead of deriving areas
				boolean valid = true;
				for (int dx = 0; dx < width; dx++) {
					for (int dy = 0; dy < height; dy++) {
						final Tile tile = t.derive(x + dx, y + dy);
						if (notReachable.contains(tile)) {
							valid = false;
							break;

						}
					}
					if (!valid) {
						break;
					}
				}

				if (valid) {
					for (int dx : new int[]{-1, width}) {
						for (int dy : new int[]{-1, height}) {
							Tile east = t.derive(x + dx, y + dy);
							if (tiles.contains(east) || notReachable.contains(east)) {
								continue;
							}
							if (!reachable.contains(east)) {
								if (east.getMatrix(ctx).isReachable()) {
									reachable.add(east);
									tiles.add(east);
								} else {
									notReachable.add(east);
								}
							} else {
								tiles.add(east);
							}
						}
					}
				}
			}
		}

		final Player local = ctx.players.local();
		if (local != null && !tiles.isEmpty()) {
			final ArrayList<Tile> list = new ArrayList<Tile>(tiles);
			Collections.sort(list, new Comparator<Tile>() {
				@Override
				public int compare(Tile o1, Tile o2) {
					return Double.compare(o1.distanceTo(local), o2.distanceTo(local));
				}
			});
			return list.get(org.powerbot.script.util.Random.nextInt(0, Math.min(2, tiles.size())));
		}

		return Tile.NIL;
	}

	private void addToSet(LogGildedAltar script, Set<Tile> reachable, Set<Tile> notReachable, List<Tile> tiles, Tile east) {
		if (!notReachable.contains(east)) {
			if (!reachable.contains(east)) {
				if (east.getMatrix(script.ctx).isReachable()) {
					reachable.add(east);
					tiles.add(east);
				} else {
					notReachable.add(east);
				}
			} else {
				tiles.add(east);
			}
		}
	}

}
