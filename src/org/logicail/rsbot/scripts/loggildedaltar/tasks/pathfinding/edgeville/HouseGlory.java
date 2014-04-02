package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.edgeville;

import org.logicail.rsbot.scripts.framework.context.providers.ILodestone;
import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Astar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.HousePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.*;

import java.util.Comparator;
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
	private static final int[] MOUNTED_GLORY_BOUNDS_RIGHT = {-100, 100, -840, -584, 128, 256};

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
				&& (locationAttribute.isInLargeArea(ctx) || (script.houseTask.isInHouse() && !ctx.objects.select().id(MOUNTED_GLORY).isEmpty()) || ILodestone.Lodestone.EDGEVILLE.isUnlocked(ctx));
	}

	@Override
	public void run() {
		if (script.houseTask.isInHouse()) {
			final GameObject altar = script.altarTask.getAltar();
			final Player local = ctx.players.local();
			final GameObject mountedGlory = ctx.objects.select().id(MOUNTED_GLORY).sort(new Comparator<GameObject>() {
				@Override
				public int compare(GameObject o1, GameObject o2) {
					if (altar.valid()) {
						return Double.compare(IMovement.Euclidean(o1, altar), IMovement.Euclidean(o2, altar));
					}
					return Double.compare(IMovement.Euclidean(o1, local), IMovement.Euclidean(o2, local));
				}
			}).each(Interactive.doSetBounds(MOUNTED_GLORY_BOUNDS_RIGHT)).poll();

			if (!mountedGlory.valid()) return;
			final Room gloryRoom = script.roomStorage.getRoom(mountedGlory);
			final List<Tile> tiles = ctx.movement.getTilesNear(gloryRoom.getArea(), mountedGlory, 3);
			final Tile destination = tiles.isEmpty() ? mountedGlory.tile() : tiles.get(0);

			final HousePath pathToGlory = new Astar(script).findRoute(destination);
			if (pathToGlory == null) {
				options.status = "No path to mounted glory";
				return;
			}

			if (pathToGlory.getPath().size() > 1 && local.tile().distanceTo(mountedGlory) > 7) {
				options.status = "Walk to mounted glory";
				if (pathToGlory.traverse(destination)) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return gloryRoom.equals(script.roomStorage.getRoom(ctx.players.local())) || mountedGlory.inViewport();
						}
					}, Random.nextInt(100, 250), 10);
				}
			}

			//LogHandler.print("Can I reach glory?: " + (destination.canReach() ? "yes" : "no"));

			if (gloryRoom.equals(script.roomStorage.getRoom(ctx.players.local())) || (destination.matrix(ctx).reachable() && pathToGlory.getNextDoor().isEmpty())) {
				options.status = "Trying to click amulet of glory";
				if (!mountedGlory.inViewport()) {
					ctx.camera.turnTo(mountedGlory);
					if (!mountedGlory.inViewport()) {
						ctx.camera.turnTo(mountedGlory, Random.nextInt(9, 16));
					}
				}

				if (!mountedGlory.inViewport()) {
					ctx.camera.pitch(Random.nextInt(0, 40));
					ctx.sleep(250);
				}

				if (!mountedGlory.inViewport() && !gloryRoom.getArea().contains(ctx.players.local())) {
					ctx.movement.walk(destination);
				}

				if (!mountedGlory.inViewport()) {
					if (ctx.movement.findPath(destination).traverse()) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return mountedGlory.inViewport() || mountedGlory.tile().distanceTo(ctx.players.local()) < 5;
							}
						}, Random.nextInt(125, 250), 10);
					}
					if (!mountedGlory.inViewport()) {
						ctx.camera.turnTo(mountedGlory, Random.nextInt(0, 8));
					}
				}

				if (mountedGlory.inViewport() && mountedGlory.interact("Rub", "Amulet of Glory")) {
					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !ctx.chat.select().text("Edgeville").isEmpty();
						}
					})) {
						for (ChatOption option : ctx.chat.first()) {
							ctx.sleep(333);
							if (option.select(Random.nextBoolean())) {
								Condition.wait(new Callable<Boolean>() {
									@Override
									public Boolean call() throws Exception {
										return locationAttribute.isInLargeArea(ctx) && ctx.game.clientState() == Game.INDEX_MAP_LOADED && ctx.players.local().animation() == -1;
									}
								}, Random.nextInt(120, 160), 100);
								ctx.sleep(400);
							}
						}
					}
				} else {
					ctx.camera.prepare(destination);
				}
			}
		} else {
			if (!locationAttribute.isInLargeArea(ctx)) {
				if (ILodestone.Lodestone.EDGEVILLE.isUnlocked(ctx)) {
					if (ctx.players.local().animation() == -1) {
						ctx.lodestones.teleport(ILodestone.Lodestone.EDGEVILLE);
					}
				}
			} else if (!locationAttribute.isInSmallArea(ctx)) {
				doSmall();
			}
		}
	}
}
