package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.wrappers.Locatable;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:07
 */
public class Astar extends IMethodProvider {
	private final LogGildedAltar script;

	public Astar(LogGildedAltar script) {
		super(script.ctx);
		this.script = script;
	}

	public HousePath findRoute(Locatable destination) {
		script.roomStorage.findNeighbours();
		return findRoute(script.roomStorage.getRoom(ctx.players.local()), script.roomStorage.getRoom(destination));
	}

	public HousePath findRoute(Room destination) {
		script.roomStorage.findNeighbours();
		return findRoute(script.roomStorage.getRoom(ctx.players.local()), destination);
	}

	private HousePath findRoute(Room start, Room destination) {
		if (start == null || destination == null) {
			return null;
		}

		if (start.equals(destination)) {
			return new HousePath(script, start, new HousePath(script, start), 0);
		}

		Set<Room> closed = new HashSet<Room>();
		PriorityQueue<HousePath> open = new PriorityQueue<HousePath>();
		open.add(new HousePath(script, start));

		while (!open.isEmpty()) {
			final HousePath path = open.poll();
			final Room last = path.getLast();
			if (closed.contains(last)) continue;
			if (last.equals(destination)) {
				return path;
			}
			closed.add(last);
			for (Room room : last.getNeighbours()) {
				HousePath newPath = path.addRoom(room);
				newPath.estimate = newPath.cost + 1;
				open.add(newPath);
			}
		}

		return null;
	}

	/*public int manhatten(Room start, Room destination) {
		final Tile startLocation = start.getLocation();
		final Tile endLocation = destination.getLocation();
		return (Math.abs(startLocation.x - endLocation.x) + Math.abs(startLocation.x - endLocation.y));
	}*/
}
