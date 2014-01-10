package org.logicail.rsbot;

import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/01/14
 * Time: 16:06
 */
public class AreaTest {
	public static void main(String[] args) {
		Area regular = new Area(new Tile(3091, 3500, 0), new Tile(3099, 3488, 0));
		Area irregular = new Area(new Tile(3091, 3500, 0), new Tile(3091, 3488, 0), new Tile(3095, 3488, 0),
				new Tile(3095, 3496, 0), new Tile(3099, 3496, 0), new Tile(3099, 3500, 0));

		System.out.println("Regular: " + regular.getCentralTile() + " " + regular.getPolygon().getBounds().getCenterX() + ", " + regular.getPolygon().getBounds().getCenterY());
		System.out.println("Irregular: " + irregular.getCentralTile() + " " + irregular.getPolygon().getBounds().getCenterX() + ", " + irregular.getPolygon().getBounds().getCenterY());

		String[] actions = new String[] {
			"Store-1 <col=ff9040> Impious ashes",
				"Store-5 <col=ff9040> Impious ashes",
				"Store-10 <col=ff9040> Impious ashes",
				"Store-All <col=ff9040> Impious ashes"
		};
	}

	static class MyArea extends Area {
		final int plane;
		private Tile central = null;

		public MyArea(Tile... tile) {
			super(tile);
			plane = tile[0].plane;
		}

		public MyArea(Tile tile, Tile tile2) {
			super(tile, tile2);
			plane = tile.plane;
		}

		@Override
		public Tile getCentralTile() {
			if (central != null) {
				return central;
			}

			// Note will give (0, 0) when Area(tileA, tileB) and (tileA.x == tileB.x || tileA.y == tileB.y) eg "new Area(new Tile(0, 0), new Tile(100, 0))"
			final Point point = PolygonUtils.polygonCenterOfMass(getPolygon());
			central = new Tile(point.x, point.y, plane);

			// Remove this just showing when don't match
			final Tile currentImplementation = super.getCentralTile();
			if (!currentImplementation.equals(central)) {
				System.out.println("CurrentImplementation: " + currentImplementation + " != " + central);
			}

			return central;
		}
	}

	/**
	 * http://www.shodor.org/~jmorrell/interactivate/org/shodor/util11/PolygonUtils.java
	 */
	static class PolygonUtils {
		/**
		 * Finds the centroid of a polygon with integer verticies.
		 *
		 * @param pg The polygon to find the centroid of.
		 * @return The centroid of the polygon.
		 */

		public static Point polygonCenterOfMass(Polygon pg) {
			if (pg == null)
				return null;

			int N = pg.npoints;
			Point[] polygon = new Point[N];

			for (int q = 0; q < N; q++)
				polygon[q] = new Point(pg.xpoints[q], pg.ypoints[q]);

			double cx = 0, cy = 0;
			double A = PolygonArea(polygon, N);
			int i, j;

			double factor = 0;
			for (i = 0; i < N; i++) {
				j = (i + 1) % N;
				factor = (polygon[i].x * polygon[j].y - polygon[j].x * polygon[i].y);
				cx += (polygon[i].x + polygon[j].x) * factor;
				cy += (polygon[i].y + polygon[j].y) * factor;
			}
			factor = 1.0 / (6.0 * A);
			cx *= factor;
			cy *= factor;
			return new Point((int) Math.abs(Math.round(cx)), (int) Math.abs(Math
					.round(cy)));
		}

		/**
		 * Computes the area of any two-dimensional polygon.
		 *
		 * @param polygon The polygon to compute the area of input as an array of points
		 * @param N       The number of points the polygon has, first and last point
		 *                inclusive.
		 * @return The area of the polygon.
		 */
		public static double PolygonArea(Point[] polygon, int N) {
			int i, j;
			double area = 0;

			for (i = 0; i < N; i++) {
				j = (i + 1) % N;
				area += polygon[i].x * polygon[j].y;
				area -= polygon[i].y * polygon[j].x;
			}

			area /= 2.0;
			return (Math.abs(area));
		}
	}
}


