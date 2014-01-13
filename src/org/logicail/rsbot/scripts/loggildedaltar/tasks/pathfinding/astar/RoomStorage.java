package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.wrappers.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:36
 */
public class RoomStorage extends IMethodProvider {
	public Room[] rooms = new Room[81];
	private final LogGildedAltar script;

	private Tile base = null;
	private CollisionMap collisionMap = null;

	public RoomStorage(LogGildedAltar script) {
		super(script.ctx);
		this.script = script;

		// Create the rooms
		for (int x = 16; x <= 80; x += 8) {
			for (int y = 15; y <= 80; y += 8) {
				final Room r = new Room(ctx, this, x, y);
				rooms[r.getIndex()] = r;
			}
		}

		// Find neighbours
		if (script.houseTask != null && script.houseTask.isInHouse()) {
			findNeighbours();
		}
	}

	public void findNeighbours() {
		if (collisionMap == null || base == null || !base.equals(ctx.game.getMapBase())) {
			base = ctx.game.getMapBase();
			collisionMap = ctx.movement.getCollisionMap();
		}

		for (Room room : rooms) {
			room.clearNeighbours();
		}

		if (!script.houseTask.isInHouse()) {
			return;
		}

		// Joined by doors
		for (GameObject door : ctx.objects.select().id(Room.DOOR_CLOSED_LEFT, Room.DOOR_OPEN_LEFT)) {
			final Room room = getRoom(door);

			for (Room destination : getPossibleNeighbours(room)) {
				if (destination.getWallArea().contains(door)) {
					room.addNeighbour(destination);
					break;
				}
			}
		}

		for (Room room : rooms) {
			int index = room.getIndex();
			final Tile location = room.getLocation();

			// North
			if (!flagAt(location.derive(3, 0)).contains(CollisionFlag.NORTH)) {
				final Room r = getRoom(index + 9);
				if (r != null) {
					r.addNeighbour(room);
				}
			}

			// East
			if (!flagAt(location.derive(7, -3)).contains(CollisionFlag.EAST)) {
				final Room r = getRoom(index + 1);
				if (r != null) {
					r.addNeighbour(room);
				}
			}

			// South
			if (!flagAt(location.derive(3, -7)).contains(CollisionFlag.SOUTH)) {
				final Room r = getRoom(index - 9);
				if (r != null) {
					r.addNeighbour(room);
				}
			}

			// West
			if (!flagAt(location.derive(0, -3)).contains(CollisionFlag.WEST)) {
				final Room r = getRoom(index - 1);
				if (r != null) {
					r.addNeighbour(room);
				}
			}
		}
	}

	private CollisionFlag flagAt(Tile derive) {
		return collisionMap.getFlagAt(derive.x - base.x, derive.y - base.y);
	}

	public List<Room> getPossibleNeighbours(Room room) {
		List<Room> list = new ArrayList<Room>();

		if (room != null) {
			final int index = room.getIndex();
			Room temp = getRoom(index + 9); // north
			if (temp != null) {
				list.add(temp);
			}
			temp = getRoom(index - 9); // south
			if (temp != null) {
				list.add(temp);
			}
			temp = getRoom(index + 1); // east
			if (temp != null) {
				list.add(temp);
			}
			temp = getRoom(index - 1); // west
			if (temp != null) {
				list.add(temp);
			}
		}

		return list;
	}

	public Room getRoom(Locatable locatable) {
		return getRoom(getIndex(locatable));
	}

	/**
	 * Get position in the array
	 *
	 * @param locatable
	 * @return
	 */
	public int getIndex(Locatable locatable) {
		if (locatable == null) {
			return -1;
		}

		final Tile location = locatable.getLocation();
		final Tile mapBase = ctx.game.getMapBase();

		int localX = location.x - mapBase.x;
		int localY = location.y - mapBase.y;

		int x = localX - localX % 8;
		int remainder = localY % 8;
		int y = (remainder == 0) ? localX + 8 : localY + 8 - remainder;

		return -2 + (x / 8) + 9 * (-2 + (y / 8));
	}

	public Room getRoom(int index) {
		if (index > -1 && index < rooms.length) {
			return rooms[index];
		}
		return null;
	}

	public int getLength() {
		return rooms.length;
	}
}
