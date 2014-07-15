package org.logicail.rsbot.scripts.framework.context.rt4.providers;

import org.logicail.rsbot.scripts.framework.util.LoopCondition;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Movement;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 13:30
 */
public class IMovement extends Movement {
	public IMovement(ClientContext ctx) {
		super(ctx);
	}

	public boolean myWalk(final org.powerbot.script.Tile tile) {
		final double currentDistance = tile.distanceTo(ctx.players.local());
		if (currentDistance <= 4) {
			return true;
		}

		if (!ctx.players.local().inMotion() || ctx.movement.distance(ctx.movement.destination(), ctx.players.local()) < Random.nextInt(2, 5)) {
			if (ctx.movement.findPath(tile).traverse() || ctx.movement.step(tile)) {
				LoopCondition.wait(new LoopCondition(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.players.local().inMotion() || ctx.movement.distance(ctx.movement.destination(), ctx.players.local()) <= 4 || tile.tile().distanceTo(ctx.players.local()) <= 4;
					}
				}, new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().inMotion();
					}
				}), 250, 10);
				Condition.sleep(250);
			}
		} else {
			Condition.sleep(400);
		}

		return tile.tile().distanceTo(ctx.players.local()) <= 4;
	}
}
