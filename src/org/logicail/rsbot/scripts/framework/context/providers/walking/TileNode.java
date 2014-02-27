package org.logicail.rsbot.scripts.framework.context.providers.walking;

import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/02/14
 * Time: 18:10
 */
public class TileNode implements Locatable {
	private final int id;
	private Tile location;

	private HashSet<Link> neighbours = new HashSet<Link>();

	public TileNode(int id) {
		this.id = id;
	}

	@Override
	public Tile getLocation() {
		return location;
	}

	public void setLocation(Tile location) {
		this.location = location;
	}

	public HashSet<Link> getNeighbours() {
		return neighbours;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TileNode node = (TileNode) o;

		return id == node.id && !(location != null ? !location.equals(node.location) : node.location != null);
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (location != null ? location.hashCode() : 0);
		return result;
	}

	public void add(Link link) {
		neighbours.add(link);
	}
}
