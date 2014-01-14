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
	public final Room[] rooms = new Room[81];
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
	}

	public void findNeighbours() {
		if (!script.houseTask.isInHouse()) {
			return;
		}

		for (Room room : rooms) {
			room.clearNeighbours();
		}

		if (collisionMap == null || base == null || !base.equals(ctx.game.getMapBase())) {
			base = ctx.game.getMapBase();
			collisionMap = ctx.movement.getCollisionMap();
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

		// Archway between rooms
		for (Room room : rooms) {
			int index = room.getIndex();
			final Tile location = room.getLocation();

			if (!flagAt(location.derive(3, 0)).contains(CollisionFlag.NORTH)) {
				final Room r = getRoom(index + 9);
				if (r != null) {
					r.addNeighbour(room);
				}
			}

			if (!flagAt(location.derive(7, -3)).contains(CollisionFlag.EAST)) {
				final Room r = getRoom(index + 1);
				if (r != null) {
					r.addNeighbour(room);
				}
			}

			if (!flagAt(location.derive(3, -7)).contains(CollisionFlag.SOUTH)) {
				final Room r = getRoom(index - 9);
				if (r != null) {
					r.addNeighbour(room);
				}
			}

			if (!flagAt(location.derive(0, -3)).contains(CollisionFlag.WEST)) {
				final Room r = getRoom(index - 1);
				if (r != null) {
					r.addNeighbour(room);
				}
			}
		}
	}

	public List<Room> getPossibleNeighbours(Room room) {
		List<Room> list = new ArrayList<Room>();

		if (room != null) {
			final int index = room.getIndex();
			final int[] possibleIndexes = new int[]{index + 9, index - 9, index + 1, index - 1};
			for (int i : possibleIndexes) {
				final Room temp = getRoom(i);
				if (temp != null) {
					list.add(temp);
				}
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

		int index = -2 + (x / 8) + 9 * (-2 + (y / 8));
		if (index > -1 && index < rooms.length) {
			return index;
		}

		return -1;
	}

	public Room getRoom(int index) {
		if (index > -1 && index < rooms.length) {
			return rooms[index];
		}
		return null;
	}

	private CollisionFlag flagAt(Tile derive) {
		return collisionMap.getFlagAt(derive.x - base.x, derive.y - base.y);
	}
}
