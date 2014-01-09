package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.methods.Camera;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/12/13
 * Time: 16:05
 */
public class ICamera extends Camera {
	private IMethodContext ctx;

	public ICamera(IMethodContext context) {
		super(context);
		ctx = context;
	}

	/**
	 * Prepare object for interaction
	 * +TurnTo
	 * +WalkTowards
	 *
	 * @param locatable
	 * @return if object is on screen
	 */
	public boolean prepare(final Locatable locatable) {
		if (!(locatable instanceof Interactive)) {
			ctx.log.severe("ICamera.prepare: Not an instance of interactive");
			return false;
		}

		final Interactive targetable = (Interactive) locatable;
		if (targetable.isOnScreen()) {
			return true;
		}

		final int distance = Random.nextInt(6, 10);
		final double currentDistance = locatable.getLocation().distanceTo(ctx.players.local());
		if (currentDistance > distance) {
			final Tile tile = getReachableTile(locatable);
			if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(tile)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return targetable.isOnScreen() || locatable.getLocation().distanceTo(ctx.players.local()) <= distance;
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
			if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(tile)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return targetable.isOnScreen() || locatable.getLocation().distanceTo(ctx.players.local()) <= 4;
					}
				});
				sleep(250, 1000);
			}
			ctx.camera.setPitch(Random.nextInt(0, 40));
		}

		return targetable.isOnScreen();
	}

	private Tile getReachableTile(Locatable locatable) {
		final Tile location = locatable.getLocation();
		final LogicailArea area = new LogicailArea(location.derive(-3, -3), location.derive(4, 4));
		List<Tile> reachable = new LinkedList<Tile>();
		for (Tile tile : area.getTileArray()) {
			if (tile.getMatrix(ctx).isReachable()) {
				reachable.add(tile);
			}
		}

		if (!reachable.isEmpty()) {
			return reachable.get(Random.nextInt(0, reachable.size()));
		}

		return location;
	}
}
