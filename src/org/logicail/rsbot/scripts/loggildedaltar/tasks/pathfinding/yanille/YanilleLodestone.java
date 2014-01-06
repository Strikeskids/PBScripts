package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille;

import org.logicail.rsbot.scripts.framework.context.providers.Lodestones;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LodestoneTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.util.DoorOpener;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 19:28
 */
public class YanilleLodestone extends LodestoneTeleport {
	private static final int[] CLOSED_DOOR = {17091, 17093};
	private static final Area DOOR_AREA = new Area(new Tile(2530, 3094, 0), new Tile(2540, 3089, 0));

	public YanilleLodestone(LogGildedAltar script) {
		super(script, Path.YANILLE_LODESTONE, Lodestones.Lodestone.YANILlE);
	}

	@Override
	public boolean doLarge() {
		return !script.houseTask.isInHouse() && !locationAttribute.isInLargeArea(ctx) && super.doLarge();
	}

	@Override
	public boolean doSmall() {
		if (!script.houseTask.isInHouse()) {
			GameObject door = nextDoor().poll();
			if (door.isValid()) {
				if (DoorOpener.open(ctx, door)) {
					if (locationAttribute.getSmallRandom(ctx).getMatrix(ctx).isReachable()) {
						script.housePortal.enterPortal();
					} else {
						ctx.movement.findPath(locationAttribute.getSmallRandom(ctx)).traverse();
					}
				}
			} else {
				ctx.movement.findPath(locationAttribute.getSmallRandom(ctx)).traverse();
			}

			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.players.local().isInMotion();
				}
			}, 600, Random.nextInt(4, 8))) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.players.local().isInMotion() || locationAttribute.isInSmallArea(ctx);
					}
				});
			}
		}
		return locationAttribute.isInSmallArea(ctx);
	}

	public BasicNamedQuery<GameObject> nextDoor() {
		final Tile playerLocation = ctx.players.local().getLocation();
		return ctx.objects.select().id(CLOSED_DOOR).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return gameObject.getLocation().x >= playerLocation.x && DOOR_AREA.contains(gameObject);
			}
		}).nearest().limit(2).shuffle().first(); // Left and right door => limit 2
	}
}