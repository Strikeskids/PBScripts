package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners;

import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.AltarLightBurnersTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.Script;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;

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
	public boolean isValid() {
		return /*!options.lightBurners &&*/ /*options.useOtherHouse MOVED TO CONSTRUCTOR*/ true;
	}

	@Override
	public void run() {
		Timer timer = new Timer(Random.nextInt(50000, 70000));

		final Script.Controller controller = script.getController();

		while (timer.isRunning()) {
			if (controller == null || controller.isSuspended() || controller.isStopping()) {
				break;
			}

			options.status = "Waiting for burners";
			if (options.detectHouses) {
				ctx.log.info("Waiting to see if someone lights the burners otherwise skipping house in "
						+ timer.toRemainingString());
			} else {
				ctx.log.info("Waiting to see if someone lights the burners");
			}

			if (options.stopOffering && ctx.players.local().getAnimation() != -1 && ctx.players.local().getLocation().randomize(2, 2).getMatrix(ctx).interact("Walk here")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.players.local().getAnimation() != -1;
					}
				});
			}

			sleep(600);

			final Room room = script.roomStorage.getRoom(ctx.players.local());
			if (room != null && burnersTask.getUnlitLanterns(room).isEmpty()) {
				options.status = "Burners have been lit";
				break;
			}
		}

		final Room room = script.roomStorage.getRoom(ctx.players.local());
		if (room != null && !burnersTask.getUnlitLanterns(room).isEmpty()) {
			script.leaveHouse.leaveHouse = true;
			script.houseHandler.currentHouseFailed();
		}
	}
}
