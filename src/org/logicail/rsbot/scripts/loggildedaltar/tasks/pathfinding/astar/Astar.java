package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

import org.powerbot.script.wrappers.Tile;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:07
 */
public class Astar {
	public Route findRoute(Room start, Room destination) {
		if (start == null || destination == null || start.equals(destination)) {
			return null;
		}

		Set<Room> closed = new HashSet<Room>();
		PriorityQueue<Route> open = new PriorityQueue<Route>();
		open.add(new Route(start));

		while (!open.isEmpty()) {
			final Route path = open.poll();
			final Room last = path.getLast();
			if (closed.contains(last)) continue;
			if (last.equals(destination)) {
				return path;
			}
			closed.add(last);
			for (Room room : last.getNeighbours()) {
				Route newPath = path.addRoom(room);
				newPath.estimate = newPath.cost + manhatten(room, destination);
				open.add(newPath);
			}
		}

		return null;
	}

	public int manhatten(Room start, Room destination) {
		final Tile startLocation = start.getLocation();
		final Tile endLocation = destination.getLocation();
		return (Math.abs(startLocation.x - endLocation.x) + Math.abs(startLocation.x - endLocation.y));
	}
}
