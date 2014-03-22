package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners;

import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.AltarLightBurnersTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.banking.Banking;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 17:55
 */
public class LightBurners extends BurnerAbstract {
	public LightBurners(AltarLightBurnersTask burnersTask) {
		super(burnersTask);
	}

	private static final int[] BURNER_BOUNDS = {-128, 128, -556, 0, -128, 128};

	@Override
	public String toString() {
		return "LightBurners";
	}

	@Override
	public boolean isValid() {
		final Room room = script.roomStorage.getRoom(ctx.players.local());
		if (room != null) {
			final BasicNamedQuery<GameObject> unlitLanterns = burnersTask.getUnlitLanterns(room);
			if (!unlitLanterns.isEmpty() && unlitLanterns.size() <= ctx.backpack.select().id(Banking.ID_MARRENTIL).count()) {
				return true;
			}
		}
		return false;
	}

	private GameObject getBurner(Room room) {
		GameObject burner = burnersTask.getUnlitLanterns(room).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return IMovement.Euclidean(gameObject, ctx.players.local()) < 2;
			}
		}).poll();

		if (!burner.isValid()) {
			burner = burnersTask.getUnlitLanterns(room).shuffle().poll();
		}

		return burner;
	}

	@Override
	public void run() {
		if (ctx.players.local().getAnimation() != -1) {
			return;
		}

		script.log.info("Lighting incense burners");

		final Room room = script.roomStorage.getRoom(ctx.players.local());
		if (room == null) {
			return;
		}

		final GameObject burner = getBurner(room);

		if (!burner.isValid()) {
			return;
		}

		burner.setBounds(BURNER_BOUNDS);

		if (!burner.isInViewport()) {
			ctx.camera.turnTo(burner);
			if (!burner.isInViewport()) {
				ctx.camera.setPitch(Random.nextInt(0, 40));
			}
			if (!burner.isInViewport()) {
				options.status = "Walking to incense burner";
				final Tile tile = burner.getLocation().randomize(1, 1);
				if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(tile)) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return burner.isInViewport();
						}
					}, 100, 25);
				}
			}
			sleep(50, 400);
		}

		if (burner.isValid() && burner.isInViewport() && burner.interact("Light", "Incense burner")) {
			final long start = System.currentTimeMillis();
			final GameObject finalBurner = burner;
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					if (!finalBurner.isValid()) {
						return true;
					}

					if (System.currentTimeMillis() - start > 1000) {
						if (IMovement.Euclidean(finalBurner, ctx.players.local()) < 2) {
							return ctx.players.local().isIdle();
						}
					}

					return false;
				}
			}, 100, 20);
		}

	}
}
