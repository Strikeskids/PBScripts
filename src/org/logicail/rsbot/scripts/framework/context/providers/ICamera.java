package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.methods.Camera;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/12/13
 * Time: 16:05
 */
public class ICamera extends Camera {
	private final IMethodContext ctx;

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
			//ctx.log.severe("ICamera.prepare: Not an instance of interactive");
			return false;
		}

		final Interactive interactive = (Interactive) locatable;
		if (!interactive.isValid()) {
			return false;
		}

		if (interactive.isInViewport()) {
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
						return interactive.isInViewport() || locatable.getLocation().distanceTo(ctx.players.local()) <= distance;
					}
				}, 100, (int) Math.max(currentDistance * 10, 10));
				sleep(100, 600);
			}
		}

		if (!interactive.isInViewport()) {
			ctx.camera.turnTo(locatable);
			if (interactive.isInViewport()) {
				return true;
			}
		} else return true;

		if (!interactive.isInViewport()) {
			final Tile tile = getReachableTile(locatable);
			if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(tile)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return interactive.isInViewport() || locatable.getLocation().distanceTo(ctx.players.local()) <= 4;
					}
				}, 100, (int) Math.max(currentDistance * 10, 10));
				sleep(100, 600);
			}
			ctx.camera.setPitch(Random.nextInt(0, 40));
		}

		return interactive.isInViewport();
	}

	private Tile getReachableTile(Locatable locatable) {
		final Tile location = locatable.getLocation();
		final LogicailArea area = new LogicailArea(location.derive(-3, -3), location.derive(4, 4));
		final List<Tile> reachable = new ArrayList<Tile>(area.getReachable(ctx));

		if (!reachable.isEmpty()) {
			return reachable.get(Random.nextInt(0, reachable.size()));
		}

		return location.randomize(2, 2);
	}
}
