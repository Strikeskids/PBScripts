package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.Camera;
import org.powerbot.script.rt6.Interactive;

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
	private final IClientContext ctx;

	public ICamera(IClientContext context) {
		super(context);
		ctx = context;
	}

	/**
	 * Prepare object for interaction
	 * +TurnTo
	 * +WalkTowards
	 *
	 * @param entity
	 * @param <T>
	 * @return
	 */
	public <T extends Interactive & Locatable> boolean prepare(final T entity) {
		if (!entity.valid()) {
			return false;
		}

		if (entity.inViewport()) {
			return true;
		}

		final int distance = Random.nextInt(6, 10);
		final double currentDistance = entity.tile().distanceTo(ctx.players.local());
		if (currentDistance > distance) {
			final Tile tile = getReachableTile(entity);
			if (ctx.movement.findPath(tile).traverse() || ctx.movement.step(tile)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return entity.inViewport() || entity.tile().distanceTo(ctx.players.local()) <= distance;
					}
				}, 100, (int) Math.max(currentDistance * 10, 10));
				ctx.sleep(300);
			}
		}

		if (entity.inViewport() || combineCamera(entity, 50 + Random.nextInt(-40, 25))) {
			return true;
		}

		final Tile tile = getReachableTile(entity);
		if (ctx.movement.findPath(tile).traverse() || ctx.movement.step(tile)) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return entity.inViewport() || entity.tile().distanceTo(ctx.players.local()) <= 4;
				}
			}, 100, (int) Math.max(currentDistance * 10, 10));
			ctx.sleep(300);
			if (!entity.inViewport()) {
				return combineCamera(entity, 50 + Random.nextInt(-40, 25));
			}
		}

		return entity.inViewport();
	}

	private Tile getReachableTile(Locatable locatable) {
		final Tile location = locatable.tile();
		final LogicailArea area = new LogicailArea(location.derive(-3, -3), location.derive(4, 4));
		final List<Tile> reachable = new ArrayList<Tile>(area.getReachable(ctx));

		if (!reachable.isEmpty()) {
			return reachable.get(Random.nextInt(0, reachable.size()));
		}

		return location;
	}

	/**
	 * If necessary adjust the camera so that the entity is in the viewport
	 *
	 * @param entity to turn to
	 * @param pitch
	 * @param <T>
	 * @return if the entity is now inViewport
	 */
	public <T extends Interactive & Locatable> boolean combineCamera(final T entity, final int pitch) {
		if (!entity.valid()) {
			return false;
		}

		if (entity.inViewport()) {
			return true;
		}

		final Thread angleThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ctx.camera.turnTo(entity);
			}
		});
		final Thread pitchThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ctx.camera.pitch(pitch);
			}
		});

		if (Random.nextBoolean()) {
			angleThread.start();
			ctx.sleep(150); // my sleep does Thread.sleep(Math.max(5, (int) (millis * Random.nextDouble(0.75, 1.5))))
			pitchThread.start();
		} else {
			pitchThread.start();
			ctx.sleep(150);
			angleThread.start();
		}

		try {
			angleThread.join();
			pitchThread.join();
		} catch (InterruptedException ignored) {
		}

		return entity.inViewport();
	}
}
