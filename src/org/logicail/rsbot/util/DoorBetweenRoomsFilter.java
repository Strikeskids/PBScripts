package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.Filter;
import org.powerbot.script.rt6.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 15:52
 */
public class DoorBetweenRoomsFilter implements Filter<GameObject> {
	private final Room current;
	private final Room destination;

	public DoorBetweenRoomsFilter(Room current, Room destination) {
		this.current = current;
		this.destination = destination;
	}

	@Override
	public boolean accept(GameObject gameObject) {
		return current != null && current.getWallArea().contains(gameObject)
				&& destination != null && destination.getWallArea().contains(gameObject);
	}
}
