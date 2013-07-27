package org.logicail.api.walking.webbuilder;

import java.awt.*;

/**
 * User: Manner
 * Date: 6/25/13
 * Time: 2:49 AM
 */
public class MapTile {

	private int x;
	private int y;

	public MapTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void draw(Graphics2D g, Point mapLoc, Color color) {
		int gx = getGX(mapLoc);
		int gy = getGY(mapLoc);
		g.setColor(color);
		g.fillRect(gx, gy, 2, 2);
		g.setColor(Color.black);
	}

	public void drawInScale(Graphics2D g, Point mapLoc, Color color, double scale) {
		int gx = getScaledGX(mapLoc, scale);
		int gy = getScaledGY(mapLoc, scale);
		g.setColor(color);
		g.fillRect(gx, gy, 2, 2);
		g.setColor(Color.black);
	}

	public int getGX(Point mapLoc) {
		return 2 * (x - 2048) + mapLoc.x;
	}

	public int getGY(Point mapLoc) {
		return -2 * y + 8317 + mapLoc.y;
	}

	public int getScaledGX(Point mapLoc, double scale) {
		return (int) (getGX(mapLoc) * scale + scale - 1 / scale);
	}

	public int getScaledGY(Point mapLoc, double scale) {
		return (int) (getGY(mapLoc) * scale + scale - 1 / scale);
	}

	public String toString() {
		return "(" + x + ", " + y + ", 0)";
	}

	public static void drawLineBetween(Graphics2D g, Point mapLoc, double scale, MapTile t1, MapTile t2) {
		g.setColor(Color.white);
		g.drawLine(t1.getScaledGX(mapLoc, scale), t1.getScaledGY(mapLoc, scale),
				t2.getScaledGX(mapLoc, scale), t2.getScaledGY(mapLoc, scale));
		g.setColor(Color.black);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		MapTile otherTile = (MapTile) other;
		return otherTile.x == this.x && otherTile.y == this.y;
	}
}
