package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.MobileIdNameQuery;
import org.powerbot.script.rt6.Player;

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
public class HousePath extends IClientAccessor implements Comparable<HousePath> {
	public final double cost;
	private final LogGildedAltar script;
	private final HousePath previous;
	public double estimate;
	private Room last;
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

	@Override
	public int compareTo(HousePath o) {
		return Double.compare(estimate, o.estimate);
	}

	public boolean traverse(Tile destination) {
		boolean success = true;
		MobileIdNameQuery<GameObject> nextDoor;

		final int endIndex = script.roomStorage.getIndex(destination);

		// TODO Remove while here
		final Player local = ctx.players.local();
		if (script.roomStorage.getIndex(local) != endIndex) {
			success = false;
			nextDoor = getNextDoor();
			if (!nextDoor.isEmpty()) {
				for (GameObject door : nextDoor) {
					final double distanceToDoor = door.tile().distanceTo(local);
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
						DoorOpener.open(ctx, nextDoor.poll());
						return true;
					}
				}
			} else {
				if (Random.nextBoolean() && destination.matrix(ctx).onMap()) {
					if (ctx.camera.prepare(destination.tile())) {
						if (destination.matrix(ctx).interact("Walk here")) {
							success = true;
						}
					}
				}
				if (!success) {
					if (destination.matrix(ctx).onMap()) {
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
						return local == null || local.inMotion();
					}
				}, 100, 9);
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						final Player local = ctx.players.local();
						return local == null || !local.inMotion() || script.roomStorage.getIndex(local) == endIndex || finalDestination.distanceTo(local) <= 3;
					}
				});
			}
		}

		return success || script.roomStorage.getIndex(local) == endIndex;
	}

	public MobileIdNameQuery<GameObject> getNextDoor() {
		final List<Room> roomPath = getPath();
		if (!roomPath.isEmpty()) {
			for (int i = 0; i < roomPath.size() - 1; i++) {
				final Room current = roomPath.get(i);
				final Room next = roomPath.get(i + 1);
				if (!ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(current, next)).isEmpty()) {
					return ctx.objects.shuffle().first();
				}
			}
		}

		return new MobileIdNameQuery<GameObject>(ctx) {
			@Override
			protected List<GameObject> get() {
				return new ArrayList<GameObject>();
			}

			@Override
			public GameObject nil() {
				return ctx.objects.nil();
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
		if (index != -1) {
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
