package org.logicail.api;

import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 18:41
 */
public class Area {
	protected final Polygon polygon;
	protected int plane = -1;
	org.powerbot.script.wrappers.Area internal;
	private Tile[] tileArrayCache = null;

	/**
	 * Constructs a rectangular area.
	 */
	public Area(final Tile t1, final Tile t2) {
		this(
				new Tile(Math.min(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane()),
				new Tile(Math.min(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane())
		);
	}

	/**
	 * Constructs a polygonal area.
	 */
	public Area(final Tile... bounds) {
		polygon = new Polygon();
		for (final Tile tile : bounds) {
			if (plane != -1 && tile.getPlane() != plane) {
				throw new RuntimeException("area does not support 3d");
			}
			plane = tile.getPlane();
			polygon.addPoint(tile.getX(), tile.getY());
			tileArrayCache = null;
		}
		internal = new org.powerbot.script.wrappers.Area(bounds);
	}

	/**
	 * @return a bounding rectangle of this area.
	 */
	public Rectangle getBounds() {
		return polygon.getBounds();
	}

	/**
	 * @return the plane of this area.
	 */
	public int getPlane() {
		return plane;
	}

	/**
	 * Determines whether the given x,y pair is contained in the area.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return whether the area contains this pair.
	 */
	public boolean contains(final int x, final int y) {
		return polygon.contains(x, y);
	}

	/**
	 * Determines whether at least one of the given tiles is contained in this area.
	 *
	 * @param locatables The tiles to verify.
	 * @return <tt>true</tt> if at least one of the tiles is contained, otherwise <tt>false</tt>.
	 */
	public boolean contains(final Locatable... locatables) {
		return internal.contains(locatables);
	}

	/**
	 * @return the averaged center tile of this area
	 */
	public Tile getCentralTile() {
		return internal.getCentralTile();
	}

	/**
	 * @return the tiles backing this Area.
	 */
	public Tile[] getBoundingTiles() {
		final Tile[] bounding = new Tile[polygon.npoints];
		for (int i = 0; i < polygon.npoints; i++) {
			bounding[i] = new Tile(polygon.xpoints[i], polygon.ypoints[i], plane);
		}
		return bounding;
	}

	/**
	 * @return an array of all the contained tiles in this area.
	 */
	public Tile[] getTileArray() {
		if (tileArrayCache == null) {
			final Rectangle bounds = getBounds();
			final ArrayList<Tile> tiles = new ArrayList<>(bounds.width * bounds.height);
			final int xMax = bounds.x + bounds.width, yMax = bounds.y + bounds.height;
			for (int x = bounds.x; x <= xMax; x++) {
				for (int y = bounds.y; y <= yMax; y++) {
					if (contains(x, y)) {
						tiles.add(new Tile(x, y, plane));
					}
				}
			}
			tileArrayCache = tiles.toArray(new Tile[tiles.size()]);
		}
		return tileArrayCache;
	}
}
