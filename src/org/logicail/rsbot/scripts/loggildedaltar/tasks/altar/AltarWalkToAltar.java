package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.AltarTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TileMatrix;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 17:27
 */
public class AltarWalkToAltar extends LogGildedAltarTask {
	public AltarWalkToAltar(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Walk to altar";
	}

	@Override
	public boolean isValid() {
		final Room room = script.roomStorage.getRoom(ctx.players.local());
		return room != null && ctx.objects.select().id(AltarTask.ALTAR).within(room.getArea()).isEmpty();
	}

	@Override
	public void run() {
		options.status = "Goin' to the chapel...";

		final GameObject altar = script.altarTask.getAltar();
		final Room currentRoom = script.roomStorage.getRoom(ctx.players.local());
		final Room altarRoom = script.roomStorage.getRoom(altar);
		final HousePath path = new Astar(script).findRoute(altarRoom);
		if (path != null) {
			final Tile location = altarRoom.getLocation();
			final List<Tile> tilesNear = ctx.movement.getTilesNear(new LogicailArea(location.derive(2, -2), location.derive(6, -6)), altar, 4);
			if (tilesNear.isEmpty()) return;
			final Tile destination = tilesNear.get(0);
			final TileMatrix matrix = destination.getMatrix(ctx);
			if (matrix.isOnScreen() && matrix.interact("Walk here") || !path.traverse(destination)) {
				if (!matrix.isReachable()) {
					for (GameObject door : ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(altarRoom, currentRoom)).first()) {
						DoorOpener.open(ctx, door);
						sleep(100, 600);
					}
				}

				ctx.movement.findPath(destination).traverse();
			}
		}
	}
}
