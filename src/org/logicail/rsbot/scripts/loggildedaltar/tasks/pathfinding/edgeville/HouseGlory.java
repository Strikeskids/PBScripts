package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.edgeville;

import org.logicail.rsbot.scripts.framework.context.providers.Lodestones;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 22:53
 */
public class HouseGlory extends NodePath {
	private static final int MOUNTED_GLORY = 13523;

	public HouseGlory(LogGildedAltar script) {
		super(script, Path.EDGEVILLE_MOUNTED_AMULET_OF_GLORY);
	}

	@Override
	public boolean doLarge() {
		return false;
	}

	@Override
	public boolean isValid() {
		return !locationAttribute.isInSmallArea(ctx)
				&& (locationAttribute.isInLargeArea(ctx) || (script.houseTask.isInHouse() && !ctx.objects.select().id(MOUNTED_GLORY).isEmpty()) || Lodestones.Lodestone.EDGEVILLE.isUnlocked(ctx));
	}

	@Override
	public void run() {
		if (script.houseTask.isInHouse()) {
			final GameObject altar = script.altarTask.getAltar().poll();
			final Player local = ctx.players.local();
			for (final GameObject mountedGlory : ctx.objects.select().id(MOUNTED_GLORY).nearest(altar.isValid() ? altar : local).first()) {
				final Room gloryRoom = script.roomStorage.getRoom(mountedGlory);
				final List<Tile> tiles = ctx.movement.getTilesNear(gloryRoom.getArea(), mountedGlory, 3);
				final Tile destination = tiles.isEmpty() ? mountedGlory.getLocation() : tiles.get(0);

				final HousePath pathToGlory = new Astar(script).findRoute(destination);
				if (pathToGlory == null) {
					options.status = "No path to mounted glory";
					return;
				}

				if (pathToGlory.getPath().size() > 1 && local.getLocation().distanceTo(mountedGlory) > 7) {
					options.status = "Walk to mounted glory";
					if (pathToGlory.traverse(destination)) {
						org.powerbot.script.util.Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return gloryRoom.equals(script.roomStorage.getRoom(ctx.players.local())) || mountedGlory.isOnScreen();
							}
						}, Random.nextInt(100, 250), 10);
					}
				}

				//LogHandler.print("Can I reach glory?: " + (destination.canReach() ? "yes" : "no"));

				if (gloryRoom.equals(script.roomStorage.getRoom(ctx.players.local())) || (destination.getMatrix(ctx).isReachable() && pathToGlory.getNextDoor().isEmpty())) {
					options.status = "Trying to click amulet of glory";
					if (!mountedGlory.isOnScreen()) {
						ctx.camera.turnTo(mountedGlory);
						if (!mountedGlory.isOnScreen()) {
							ctx.camera.turnTo(mountedGlory, Random.nextInt(9, 16));
						}
					}

					if (!mountedGlory.isOnScreen()) {
						ctx.camera.setPitch(Random.nextInt(0, 40));
						sleep(100, 600);
					}

					if (!mountedGlory.isOnScreen() && !gloryRoom.getArea().contains(ctx.players.local())) {
						ctx.movement.walk(destination);
					}

					if (!mountedGlory.isOnScreen()) {
						if (ctx.movement.findPath(destination).traverse()) {
							org.powerbot.script.util.Condition.wait(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return mountedGlory.isOnScreen() || mountedGlory.getLocation().distanceTo(ctx.players.local()) < 5;
								}
							}, Random.nextInt(125, 250), 10);
						}
						if (!mountedGlory.isOnScreen()) {
							ctx.camera.turnTo(mountedGlory, Random.nextInt(0, 8));
						}
					}

					if (mountedGlory.isOnScreen() && mountedGlory.interact("Rub", "Amulet of Glory")) {
						if (org.powerbot.script.util.Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.chat.select().text("Edgeville").isEmpty();
							}
						})) {
							for (ChatOption option : ctx.chat.first()) {
								sleep(80, 800);
								if (option.select(Random.nextBoolean())) {
									org.powerbot.script.util.Condition.wait(new Callable<Boolean>() {
										@Override
										public Boolean call() throws Exception {
											return locationAttribute.isInLargeArea(ctx) && ctx.game.getClientState() == Game.INDEX_MAP_LOADED && ctx.players.local().getAnimation() == -1;
										}
									}, Random.nextInt(120, 160), 100);
									sleep(100, 800);
								}
							}
						}
					} else {
						ctx.camera.prepare(destination);
					}
				}
			}
		} else {
			if (!locationAttribute.isInLargeArea(ctx)) {
				if (Lodestones.Lodestone.EDGEVILLE.isUnlocked(ctx)) {
					ctx.lodestones.teleport(Lodestones.Lodestone.EDGEVILLE);
				}
			} else if (!locationAttribute.isInSmallArea(ctx)) {
				doSmall();
			}
		}
	}
}
