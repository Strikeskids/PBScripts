package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.methods.Camera;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/12/13
 * Time: 16:05
 */
public class MyCamera extends Camera {
	private LogicailMethodContext ctx;

	public MyCamera(LogicailMethodContext context) {
		super(context);
		ctx = context;
	}

	private Tile getReachableTile(Locatable locatable) {
		final Tile location = locatable.getLocation();
		final Area area = new Area(location.derive(-3, -3), location.derive(3, 3));
		int tries = 10;
		while (tries > 0) {
			final Tile tile = area.getRandomTile();
			if (tile.getMatrix(ctx).isReachable()) {
				return tile;
			}
			tries--;
		}

		return null;
	}

	/**
	 * Prepare object for interaction
	 * +TurnTo
	 * +WalkTowards
	 * @param locatable
	 * @return if object is on screen
	 */
	public boolean prepare(final Locatable locatable) {
		if (!(locatable instanceof Interactive)) {
			ctx.log.severe("MyCamera.prepare: Not an instance of interactive");
			return false;
		}

		Interactive targetable = (Interactive) locatable;
		if (targetable.isOnScreen()) {
			return true;
		}

		final int distance = Random.nextInt(6, 10);
		final double currentDistance = locatable.getLocation().distanceTo(ctx.players.local());
		if (currentDistance > distance) {
			final Tile tile = getReachableTile(locatable);
			if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(locatable)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return locatable.getLocation().distanceTo(ctx.players.local()) <= distance;
					}
				});
				sleep(250, 1000);
			}
		}

		if (!targetable.isOnScreen()) {
			ctx.camera.turnTo(locatable);
			if (targetable.isOnScreen()) {
				return true;
			}
		} else return true;

		if (!targetable.isOnScreen()) {
			final Tile tile = getReachableTile(locatable);
			if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(locatable)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return locatable.getLocation().distanceTo(ctx.players.local()) <= 4;
					}
				});
				sleep(250, 1000);
			}
			ctx.camera.setPitch(Random.nextInt(0, 40));
		}

		return targetable.isOnScreen();
	}

}
