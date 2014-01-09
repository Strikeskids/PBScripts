package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:09
 */
public class Room extends IMethodProvider {
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
	public static final int[] DOOR_OPEN_LEFT = {
			13008,
			13017,
			13096,
			13102,
			13108,
			13120,
			36639
	};
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
	public double travelTime = 1; // Always room to room on grid
	private final RoomStorage roomStorage;
	private final int localX;
	private final int localY;
	private final int index;
	private Set<Room> neighbours = new HashSet<Room>();

	public Room(IMethodContext context, RoomStorage roomStorage, int localX, int localY) {
		super(context);
		this.roomStorage = roomStorage;
		this.localX = localX;
		this.localY = localY;
		this.index = roomStorage.getIndex(getLocation()) - 9;
	}

	public Tile getLocation() {
		final Tile mapBase = ctx.game.getMapBase();
		return new Tile(mapBase.x + localX, mapBase.y + localY, mapBase.getPlane());
	}

	public int getIndex() {
		return index;
	}

	public int getLocalX() {
		return localX;
	}

	public int getLocalY() {
		return localY;
	}

	public Set<Room> getNeighbours() {
		return neighbours;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Room room = (Room) o;

		if (index != room.index) return false;
		return localX == room.localX && localY == room.localY;
	}

	@Override
	public int hashCode() {
		int result = localX;
		result = 31 * result + localY;
		result = 31 * result + index;
		return result;
	}

	@Override
	public String toString() {
		return "Room{" +
				"index=" + index +
				", localX=" + localX +
				", localY=" + localY +
				", neighbours=" + neighbours.size() +
				'}';
	}

	public void addNeighbour(Room room) {
		neighbours.add(room);
		if (!room.neighbours.contains(this)) {
			room.neighbours.add(this);
		}
	}

	public void clearNeighbours() {
		neighbours.clear();
	}

	public BasicNamedQuery<GameObject> getGameObjectsInRoom(int... ids) {
		return ctx.objects.select().id(ids).within(getArea());
	}

	public LogicailArea getArea() {
		final Tile location = getLocation();
		return new LogicailArea(location, location.derive(8, -8));
	}

	public BasicNamedQuery<GameObject> getGameObjectsInRoom(int[] ids, int... ids2) {
		return ctx.objects.select().id(ids, ids2).within(getArea());
	}

	public LogicailArea getWallArea() {
		final Tile location = getLocation();
		return new LogicailArea(location.derive(-1, 1), location.derive(9, -10));
	}
}
