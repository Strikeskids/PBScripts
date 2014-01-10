package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.logicail.rsbot.util.DoorBetweenRoomsFilter;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

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
		nextPoints = Random.nextInt((int) (options.beastOfBurden.getRequiredPoints() * 1.5), (int) (options.beastOfBurden.getRequiredPoints() * 2.5));
	}

	@Override
	public String toString() {
		return "Recharging at house obelisk";
	}

	@Override
	public boolean isValid() {
		if (//&& options.beastOfBurden != Summoning.Familiar.CLAN_AVATAR
				ctx.summoning.getTimeLeft() <= 420
						&& ctx.summoning.getSummoningPoints() < nextPoints
						&& (ctx.backpack.isFull() || getBackpackOffering().isEmpty())
				) {
			Room startRoom = script.roomStorage.getRoom(ctx.players.local());
			final GameObject obelisk = script.altarTask.getMiniObelisk();
			if (obelisk.isValid()) {
				Room endRoom = script.roomStorage.getRoom(obelisk);
				return endRoom.equals(startRoom) || ((pathToObelisk = new Astar(script).findRoute(obelisk)) != null);
			}
		}
		return false;
	}

	@Override
	public void run() {
		ctx.log.info("Finding path to obelisk");

		try {
			final GameObject obelisk = script.altarTask.getMiniObelisk();
			if (!obelisk.isValid()) return;

			Room startRoom = script.roomStorage.getRoom(ctx.players.local());
			Room endRoom = script.roomStorage.getRoom(obelisk);
			if (startRoom == null || endRoom == null) return;

			final List<Tile> tilesNear = ctx.movement.getTilesNear(endRoom.getArea(), obelisk, 3);
			final Tile destination = tilesNear.isEmpty() ? obelisk.getLocation() : tilesNear.get(0);

			if (!startRoom.equals(endRoom)) {
				if (!pathToObelisk.traverse(destination)) {
					if (!destination.getMatrix(ctx).isReachable()) {
						// Find closed door
						for (GameObject door : ctx.objects.select().id(Room.DOOR_CLOSED).select(new DoorBetweenRoomsFilter(startRoom, endRoom)).shuffle().first()) {
							if (DoorOpener.open(ctx, door)) {
								sleep(100, 500);
							}
						}
					}
				}

				ctx.movement.walk(destination);
			}

			if (destination.getMatrix(ctx).isReachable() || endRoom.equals(script.roomStorage.getRoom(ctx.players.local()))) {
				if (destination.distanceTo(ctx.players.local()) < 10) {
					final int points = ctx.summoning.getSummoningPoints();
					options.status = "Clicking on obelisk";

					if (ctx.camera.prepare(obelisk) && obelisk.interact("Renew-points", "Small obelisk")) {
						Timer t = new Timer(10000);
						while (t.isRunning()) {
							if (ctx.players.local().isInMotion()) {
								t.reset();
							}

							if (ctx.camera.getPitch() < 80) {
								ctx.camera.setPitch(Random.nextInt(80, 101));
							}

							if (ctx.summoning.getSummoningPoints() > points && ctx.players.local().getAnimation() == -1) {
								options.status = "Recharged at house obelisk";
								ctx.log.info(options.status);
								script.summoningTask.nextPoints = Random.nextInt((int) (options.beastOfBurden.getRequiredPoints() * 1.5), (int) (options.beastOfBurden.getRequiredPoints() * 2.33));
								resetNextPoints();
								sleep(600, 1400);
								break;
							}

							sleep(400, 800);
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
