package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;

import java.util.concurrent.Callable;

public class StayInArea extends AbstractStrategy {
	public StayInArea(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "Stay in Artisan Workshop";
	}

	@Override
	public boolean activate() {
		final Player local = ctx.players.local();
		return local.isIdle()
				&& !LogArtisanArmourer.getAreaSmall().contains(local);
	}

	@Override
	public void run() {
		if (LogArtisanArmourer.mode == Mode.BURIAL_ARMOUR) {
			for (GameObject tunnel : ctx.objects.select().id(4618).nearest().first()) {
				if (ctx.camera.prepare(tunnel) && tunnel.interact("Climb")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return LogArtisanArmourer.getAreaSmall().contains(ctx.players.local());
						}
					});
				}
				return;
			}
		}

		if (ctx.movement.findPath(LogArtisanArmourer.getAreaSmall().getCentralTile().randomize(3, 3)).traverse()
				|| ctx.movement.stepTowards(LogArtisanArmourer.getAreaSmall().getCentralTile().randomize(3, 3))) {
			sleep(500, 1500);
		}

		/*if (LogArtisanArmourer.getAreaSmall().getCentralTile().distanceTo(ctx.players.local()) > 100) {
			LogArtisanArmourer.get().getLogHandler().print("Too far from Artisan Workshop");
		}*/
	}
}
