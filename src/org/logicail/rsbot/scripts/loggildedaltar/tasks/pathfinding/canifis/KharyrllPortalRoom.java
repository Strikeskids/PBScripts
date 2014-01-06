package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.canifis;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TilePath;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 15:34
 */
public class KharyrllPortalRoom extends NodePath {
	public static final Area CANIFIS_PUB = new Area(new Tile(3486, 3480, 0), new Tile(3504, 3467, 0));
	public static final int CLOSED_DOOR = 24936; // 24935 is open
	public static final Tile[] PATH_TO_PUB;
	private static final int[] KYARYRLL_PORTAL = {13621, 13628, 13635};
	private final int destinationDistance = Random.nextInt(2, 5);

	static {
		final List<Tile> list = Arrays.asList(CanifisRecharge.PATH_TO_OBELISK);
		Collections.reverse(list);
		PATH_TO_PUB = list.toArray(new Tile[list.size()]);
	}

	public KharyrllPortalRoom(LogGildedAltar script) {
		super(script, Path.CANIFIS);
	}

	@Override
	public boolean doLarge() {
		for (GameObject portal : ctx.objects.select().id(KYARYRLL_PORTAL).nearest().first()) {
			final Room portalRoom = script.roomStorage.getRoom(portal);
			final Room startRoom = script.roomStorage.getRoom(ctx.players.local());

			List<Tile> tilesNear = ctx.movement.getTilesNear(portalRoom.getArea(), portal, 4);
			Tile destination = tilesNear.isEmpty() ? portal.getLocation() : tilesNear.get(Random.nextInt(0, tilesNear.size()));

			if (!startRoom.equals(portalRoom)) {
				final HousePath pathToPortal = new Astar(script).findRoute(portal);
				if (pathToPortal != null && !pathToPortal.traverse(destination)) {
					if (!destination.getMatrix(ctx).isReachable()) {
						// Find door
						for (GameObject door : ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(startRoom, portalRoom)).shuffle().first()) {
							if (DoorOpener.open(ctx, door)) {
								sleep(100, 600);
							}
						}
					}
				}

				if (ctx.movement.findPath(destination).traverse()) {
					sleep(250, 600);
				}

				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						Tile dest = ctx.movement.getDestination();
						return dest == null || !dest.getMatrix(ctx).isValid() || dest.distanceTo(ctx.players.local()) <= destinationDistance;
					}
				});
			}

			if (startRoom.equals(portalRoom) || destination.getMatrix(ctx).isReachable()) {
				options.status = "Clicking on Kharyrll portal";
				if (ctx.camera.prepare(portal) && portal.interact("Enter", "Kharyrll Portal")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.game.getClientState() == Game.INDEX_MAP_LOADED && ctx.players.local().getAnimation() == -1 && CANIFIS_PUB.contains(ctx.players.local());
						}
					}, 600, 17);
					sleep(200, 600);
				}
			}
		}

		return locationAttribute.isInLargeArea(ctx) || (script.houseTask.isInHouse() && !ctx.objects.select().id(KYARYRLL_PORTAL).isEmpty());
	}

	@Override
	public boolean doSmall() {
		if (CANIFIS_PUB.contains(ctx.players.local())) {
			for (GameObject door : ctx.objects.select().id(CLOSED_DOOR).nearest().first()) {
				if (DoorOpener.open(ctx, door)) {
					sleep(100, 1000);
				}
			}
		}

		final TilePath path = new TilePath(ctx, PATH_TO_PUB);
		if (path.getEnd().distanceTo(ctx.players.local()) > 4) {
			if (path.randomize(2, 2).traverse()) {
				return true;
			}
		}

		return super.doSmall();
	}

	@Override
	public boolean isValid() {
		return !locationAttribute.isInSmallArea(ctx)
				&& (locationAttribute.isInLargeArea(ctx) || (script.houseTask.isInHouse() && !ctx.objects.select().id(KYARYRLL_PORTAL).isEmpty()));
	}
}
