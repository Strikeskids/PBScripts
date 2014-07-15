package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.MobileIdNameQuery;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:09
 */
public class Room extends IClientAccessor {
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
	public final double travelTime = 1; // Always room to room on grid
	private final int localX;
	private final int localY;
	private final int index;
	private final Set<Room> neighbours = new HashSet<Room>();
	private LogicailArea area;
	private LogicailArea wallarea;

	private Tile mapBase = null;
	private Tile location = null;
	private LogicailArea eastWestDoorArea;

	public Room(IClientContext context, RoomStorage roomStorage, int localX, int localY) {
		super(context);
		this.localX = localX;
		this.localY = localY;
		this.index = roomStorage.getIndex(getLocation());
	}

	public Tile getLocation() {
		if (location != null && mapBase != null && mapBase.equals(ctx.game.mapOffset())) {
			return location;
		}
		area = null;
		wallarea = null;
		mapBase = ctx.game.mapOffset();
		return location = new Tile(mapBase.x() + localX, mapBase.y() + localY, mapBase.floor());
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

		return index == room.index && localX == room.localX && localY == room.localY;
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

	public MobileIdNameQuery<GameObject> getGameObjectsInRoom(int... ids) {
		return ctx.objects.select().id(ids).within(getArea());
	}

	public LogicailArea getArea() {
		final Tile location = getLocation();
		if (area != null) {
			return area;
		}
		return area = new LogicailArea(location.derive(0, 1), location.derive(8, -7));
	}

	public MobileIdNameQuery<GameObject> getGameObjectsInRoom(int[] ids, int... ids2) {
		return ctx.objects.select().id(ids, ids2).within(getArea());
	}

	public LogicailArea getWallArea() {
		final Tile location = getLocation();
		if (wallarea != null) {
			return wallarea;
		}
		return wallarea = new LogicailArea(location.derive(-1, 2), location.derive(9, -8));
	}

	public LogicailArea getEastWestDoorArea() {
		final Tile location = getLocation();
		if (eastWestDoorArea != null) {
			return eastWestDoorArea;
		}
		return eastWestDoorArea = new LogicailArea(location.derive(-1, 0), location.derive(9, -6));
	}
}
