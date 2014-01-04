package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Tile;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:09
 */
public class Room extends LogicailMethodProvider {
	public static final int[] DOOR_CLOSED = {
			13006, 13007, // Whitewashed stone
			13015, 13016, // Tropical
			13094, 13096, // Basic stone
			13100, 13101, // Basic wood
			13107, 13109, // Fremennick
			13118, 13119, // Fancy stone
			36590, 36625};
	public static final int[] DOOR = {
			13006, 13007, 13008, 13009,
			13015, 13016, 13017, 13018,
			13094, 13095, 13096, 13097,
			13100, 13101, 13102, 13103,
			13107, 13108, 13109, 13110,
			13118, 13119, 13120, 13121,
			36590, 36625, 36639, 36649};
	public static final int[] DOOR_CLOSED_LEFT = {
			13006, // Whitewashed stone
			13015, // Tropical
			13094, // Basic stone
			13100, // Basic wood
			13107, // Fremennick
			13118, // Fancy stone
			36590};
	public static final int[] DOOR_CLOSED_RIGHT = {
			13007, // Whitewashed stone
			13016, // Tropical
			13096, // Basic stone
			13101, // Basic wood
			13109, // Fremennick
			13119, // Fancy stone
			36625};
	private final RoomStorage roomStorage;
	private final int x;
	private final int y;
	private final int index;
	public double travelTime;
	private Set<Room> neighbours = new HashSet<Room>();

	public int getLocalX() {
		return x;
	}

	public int getLocalY() {
		return y;
	}

	public Room(LogicailMethodContext context, RoomStorage roomStorage, int x, int y) {
		super(context);
		this.roomStorage = roomStorage;
		this.x = x;
		this.y = y;
		this.index = roomStorage.getIndex(getLocation()) - 9;
	}

	public void addNeighbour(Room room) {
		neighbours.add(room);
		if (!room.neighbours.contains(this)) {
			room.neighbours.add(this);
		}
	}

	public Set<Room> getNeighbours() {
		return neighbours;
	}

	public Tile getLocation() {
		return new Tile(x, y);
	}

	public Area getWallArea() {
		final Tile location = getLocation();
		return new Area(location.derive(-1, 1), location.derive(9, -10));
	}


	@Override
	public String toString() {
		return String.format("Room {index = %d, neighbours = %d]", index, neighbours.size());
	}

	public int getIndex() {
		return index;
	}

	public void clearNeighbours() {
		neighbours.clear();
	}
}
