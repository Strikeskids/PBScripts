package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners;

import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.scripts.framework.wrappers.ITile;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.AltarLightBurnersTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Script;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 18:12
 */
public class WaitForBurners extends BurnerAbstract {
	public WaitForBurners(AltarLightBurnersTask burnersTask) {
		super(burnersTask);
	}

	@Override
	public String toString() {
		return "Wait for burners";
	}

	@Override
	public boolean valid() {
		return /*!options.lightBurners &&*/ /*options.useOtherHouse MOVED TO CONSTRUCTOR*/ true;
	}

	@Override
	public void run() {
		Timer timer = new Timer(Random.nextInt(50000, 70000));

		final Script.Controller controller = ctx.controller;

		while (timer.running()) {
			if (controller == null || controller.isSuspended() || controller.isStopping()) {
				break;
			}

			options.status = "Waiting for burners";
			if (options.detectHouses.get()) {
				script.log.info("Waiting to see if someone lights the burners otherwise skipping house in "
						+ timer.remainingString());
			} else {
				script.log.info("Waiting to see if someone lights the burners");
			}

			if (options.stopOffering.get() && ctx.players.local().animation() != -1 && ITile.randomize(ctx.players.local().tile(), 2, 2).matrix(ctx).interact("Walk here")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().animation() == -1;
					}
				}, Random.nextInt(200, 600), 5);
			}

			final Room room = script.roomStorage.getRoom(ctx.players.local());
			if (room != null && burnersTask.getUnlitLanterns(room).isEmpty()) {
				options.status = "Burners have been lit";
				break;
			}
		}

		final Room room = script.roomStorage.getRoom(ctx.players.local());
		if (room != null && !burnersTask.getUnlitLanterns(room).isEmpty()) {
			script.leaveHouse.leaveHouse.set(true);
			script.houseHandler.currentHouseFailed();
		}
	}
}
