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
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;

import java.util.List;
import java.util.concurrent.Callable;

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
	public boolean valid() {
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
			final org.powerbot.script.rt6.TileMatrix matrix = destination.matrix(ctx);
			if (matrix.inViewport() && matrix.interact("Walk here") || !path.traverse(destination)) {
				if (!matrix.reachable()) {
					for (GameObject door : ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(altarRoom, currentRoom)).first()) {
						door.bounds(script.roomStorage.getRoom(door).getEastWestDoorArea().contains(door) ? DoorOpener.DOOR_BOUNDS_EW : DoorOpener.DOOR_BOUNDS_NS);
						DoorOpener.open(ctx, door);
						ctx.sleep(300);
					}
				}

				if (ctx.players.local().inMotion() || ctx.movement.findPath(destination).traverse()) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return destination.distanceTo(ctx.players.local()) < 4;
						}
					}, 200, 10);
				}
			}
		}
	}
}
