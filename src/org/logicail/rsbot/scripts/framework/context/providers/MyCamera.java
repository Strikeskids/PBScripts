package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.methods.Camera;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Locatable;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/12/13
 * Time: 16:05
 */
public class MyCamera extends Camera {
	public MyCamera(LogicailMethodContext context) {
		super(context);
	}

	public boolean myTurnTo(final Locatable locatable) {
		if (!(locatable instanceof Interactive)) {
			Logger logger = Logger.getLogger("MyCamera.turnTo");
			logger.log(Level.WARNING, "Not an instance of interactive");
			return false;
		}

		Interactive targetable = (Interactive) locatable;
		if (targetable.isOnScreen()) {
			return true;
		}

		final int distance = Random.nextInt(6, 9);
		if (locatable.getLocation().distanceTo(ctx.players.local()) > distance) {
			if (ctx.movement.stepTowards(locatable)) {
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
			ctx.camera.setPitch(Random.nextInt(0, 40));
		}

		return targetable.isOnScreen();
	}

}
