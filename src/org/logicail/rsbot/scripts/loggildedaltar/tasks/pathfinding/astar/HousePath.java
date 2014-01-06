package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:09
 */
public class HousePath extends LogicailMethodProvider {
	public double cost;
	public double estimate;
	private final LogGildedAltar script;
	private Room last;
	private HousePath previous;
	private Room currentRoom;

	public HousePath(LogGildedAltar script, Room start) {
		this(script, start, null, 0);
	}

	public HousePath(LogGildedAltar script, Room last, HousePath previous, double cost) {
		super(script.ctx);
		this.script = script;
		this.last = last;
		this.previous = previous;
		this.cost = cost;
	}

	public Room getLast() {
		return last;
	}

	public void setLast(Room last) {
		this.last = last;
	}

	public HousePath getPrevious() {
		return previous;
	}

	public HousePath addRoom(Room room) {
		return new HousePath(script, room, this, cost + room.travelTime);
	}

	public boolean traverse(Tile destination) {
		boolean success = true;
		BasicNamedQuery<GameObject> nextDoor;

		final int endIndex = script.roomStorage.getIndex(destination);

		// TODO Remove while here
		final Player local = ctx.players.local();
		if (script.roomStorage.getIndex(local) != endIndex) {
			success = false;
			nextDoor = getNextDoor();
			if (!nextDoor.isEmpty()) {
				for (GameObject door : nextDoor) {
					final double distanceToDoor = door.getLocation().distanceTo(local);
					if (distanceToDoor > 12 || (Random.nextBoolean() && distanceToDoor > 6)) {
						// Find correct area
						for (Room room : getPath()) {
							if (room.getWallArea().contains(door)) {
								List<Tile> tiles = ctx.movement.getTilesNear(room.getArea(), door, 3);
								for (Tile tile : tiles) {
									if (ctx.movement.findPath(tile).traverse()) {
										destination = tile;
										success = true;
										break;
									}
								}
								break;
							}
						}
					} else {
						nextDoor.each(new DoorOpener(ctx));
						return true;
					}
				}
			} else {
				if (Random.nextBoolean() && destination.getMatrix(ctx).isOnMap()) {
					if (ctx.camera.prepare(destination.getLocation())) {
						if (destination.getMatrix(ctx).interact("Walk here")) {
							success = true;
						}
					}
				}
				if (!success) {
					if (destination.getMatrix(ctx).isOnMap()) {
						if (ctx.movement.findPath(destination).traverse()) {
							success = true;
						}
					} else {
						if (ctx.movement.findPath(destination).traverse()) {
							success = true;
						} else {
							// Find nearest on map?
							script.options.status = "Can not find path";
						}
					}
				}
			}

			if (success && script.roomStorage.getIndex(local) != endIndex) {
				final Tile finalDestination = destination;
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						final Player local = ctx.players.local();
						return local == null || local.isInMotion();
					}
				}, 300, 4);
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						final Player local = ctx.players.local();
						return local == null || !local.isInMotion() || script.roomStorage.getIndex(local) == endIndex || finalDestination.distanceTo(local) <= 3;
					}
				});
			}
		}

		return success || script.roomStorage.getIndex(local) == endIndex;
	}

	public BasicNamedQuery<GameObject> getNextDoor() {
		final List<Room> roomPath = getPath();
		if (!roomPath.isEmpty()) {
			final boolean left = Random.nextBoolean();
			for (int i = 0; i < roomPath.size() - 1; i++) {
				final Room current = roomPath.get(i);
				final Room next = roomPath.get(i + 1);
				if (!ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(current, next)).isEmpty()) {
					return ctx.objects.shuffle().first();
				}
			}
		}

		return new BasicNamedQuery<GameObject>(ctx) {
			@Override
			protected List<GameObject> get() {
				return new ArrayList<GameObject>();
			}

			@Override
			public GameObject getNil() {
				return ctx.objects.getNil();
			}
		};
	}

	public List<Room> getPath() {
		List<Room> list = new ArrayList<Room>();

		HousePath path = this;
		while (path != null) {
			list.add(path.last);
			path = path.getPrevious();
		}

		Collections.reverse(list);

		final int index = script.roomStorage.getIndex(ctx.players.local());
		if (index > -1 && index < script.roomStorage.getLength()) {
			// Clean up path remove already traversed parts
			int removeBefore = -1;
			for (int i = 0; i < list.size(); i++) {
				Room room = list.get(i);
				if (room.getIndex() == index) {
					removeBefore = i;
					break;
				}
			}
			if (removeBefore > -1) {
				for (int i = 0; i < removeBefore; i++) {
					list.remove(0);
				}
			}
		}

		return list;
	}
}
