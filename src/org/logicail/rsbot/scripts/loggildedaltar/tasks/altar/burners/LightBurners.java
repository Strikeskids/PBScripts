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

		GameObject burner = burnersTask.getUnlitLanterns(room).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return IMovement.Euclidean(gameObject, ctx.players.local()) < 2;
			}
		}).poll();

		if (!burner.isValid()) {
			burner = burnersTask.getUnlitLanterns(room).shuffle().poll();
			if (!burner.isValid()) {
				return;
			}
		}

		burner.setBounds(BURNER_BOUNDS);

		ctx.camera.turnTo(burner);

		if (!burner.isInViewport()) {
			options.status = "Walking to incense burner";
			ctx.movement.walk(burner.getLocation().randomize(1, 1));
			sleep(333, 1200);
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
			}, 300, Random.nextInt(6, 10));
		}

	}
}
