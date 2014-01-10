package org.logicail.rsbot.util;

import java.awt.*;

/**
 * http://www.shodor.org/~jmorrell/interactivate/org/shodor/util11/PolygonUtils.java
 */
public class PolygonUtils {
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