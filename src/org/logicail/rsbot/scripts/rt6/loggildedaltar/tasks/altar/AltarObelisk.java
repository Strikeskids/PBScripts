package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.LogGildedAltarTask;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.astar.Room;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 17:01
 */
public class AltarObelisk extends LogGildedAltarTask {
	public static final int[] MINI_OBELISK = {44837, 44838, 44839, 44840, 44841, 44842};
	private int nextPoints;

	private HousePath pathToObelisk;

	public AltarObelisk(LogGildedAltar script) {
		super(script);
		resetNextPoints();
	}

	private void resetNextPoints() {
		nextPoints = Random.nextInt((int) (options.beastOfBurden.requiredPoints() * 1.5), (int) (options.beastOfBurden.requiredPoints() * 2.5));
	}

	@Override
	public String toString() {
		return "Recharging at house obelisk";
	}

	@Override
	public boolean valid() {
		if (//&& options.beastOfBurden != Summoning.Familiar.CLAN_AVATAR
				options.useBOB.get() &&
						ctx.summoning.timeLeft() <= 420
						&& ctx.summoning.points() < nextPoints
						&& (ctx.backpack.isFull() || getBackpackOffering().isEmpty())
				) {
			Room startRoom = script.roomStorage.getRoom(ctx.players.local());
			final GameObject obelisk = script.altarTask.getMiniObelisk();
			if (obelisk.valid()) {
				Room endRoom = script.roomStorage.getRoom(obelisk);
				return endRoom.equals(startRoom) || ((pathToObelisk = new Astar(script).findRoute(obelisk)) != null);
			}
		}
		return false;
	}

	@Override
	public void run() {
		script.log.info("Finding path to obelisk");

		try {
			final GameObject obelisk = script.altarTask.getMiniObelisk();
			if (!obelisk.valid()) return;

			Room startRoom = script.roomStorage.getRoom(ctx.players.local());
			Room endRoom = script.roomStorage.getRoom(obelisk);
			if (startRoom == null || endRoom == null) return;

			final List<Tile> tilesNear = ctx.movement.getTilesNear(endRoom.getArea(), obelisk, 3);
			final Tile destination = tilesNear.isEmpty() ? obelisk.tile() : tilesNear.get(0);

			if (!startRoom.equals(endRoom)) {
				if (!pathToObelisk.traverse(destination)) {
					if (!destination.matrix(ctx).reachable()) {
						// Find closed door
						for (GameObject door : ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(startRoom, endRoom)).shuffle().first()) {
							door.bounds(script.roomStorage.getRoom(door).getEastWestDoorArea().contains(door) ? DoorOpener.DOOR_BOUNDS_EW : DoorOpener.DOOR_BOUNDS_NS);
							if (DoorOpener.open(ctx, door)) {
								Condition.sleep(250);
							}
						}
					}
				}

				ctx.movement.walk(destination);
			}

			if (destination.matrix(ctx).reachable() || endRoom.equals(script.roomStorage.getRoom(ctx.players.local()))) {
				if (destination.distanceTo(ctx.players.local()) < 10) {
					final int points = ctx.summoning.points();
					options.status = "Clicking on obelisk";

					if (ctx.camera.prepare(obelisk) && obelisk.interact("Renew-points", "Small obelisk")) {
						Timer t = new Timer(10000);
						while (t.running()) {
							if (ctx.players.local().inMotion()) {
								t.reset();
							}

							if (ctx.camera.pitch() < 80) {
								ctx.camera.pitch(Random.nextInt(80, 101));
							}

							if (ctx.summoning.points() > points && ctx.players.local().animation() == -1) {
								options.status = "Recharged at house obelisk";
								script.log.info(options.status);
								script.summoningTask.nextPoints = Random.nextInt((int) (options.beastOfBurden.requiredPoints() * 1.5), (int) (options.beastOfBurden.requiredPoints() * 2.33));
								resetNextPoints();
								Condition.sleep(750);
								break;
							}

							Condition.sleep(333);
						}
					}
				} else {
					ctx.movement.walk(destination);
				}
			}
		} finally {
			pathToObelisk = null;
		}
	}
}
