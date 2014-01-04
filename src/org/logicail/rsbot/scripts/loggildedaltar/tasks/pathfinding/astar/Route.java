package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 21:09
 */
public class Route {
	private Room last;
	private Route previous;
	public double cost;
	public double estimate;

	public Route(Room start) {
		this(start, null, 0);
	}
	public Route(Room last, Route previous, double cost) {
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

	public Route addRoom(Room room) {
		return new Route(room, this, cost + room.travelTime);
	}
}
