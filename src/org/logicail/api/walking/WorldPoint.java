package org.logicail.api.walking;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 01/07/13
 * Time: 17:38
 */
public class WorldPoint {
	private int x;
	private int y;
	private int z;

	public WorldPoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
