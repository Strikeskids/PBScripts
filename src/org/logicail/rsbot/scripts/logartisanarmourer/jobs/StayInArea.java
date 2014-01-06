package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;

import java.util.concurrent.Callable;

public class StayInArea extends ArtisanArmourerTask {
	public StayInArea(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Stay in Artisan Workshop";
	}

	@Override
	public boolean isValid() {
		final Player local = ctx.players.local();
		return local.isIdle()
				&& !options.getAreaSmall().contains(local);
	}

	@Override
	public void run() {
		if (options.mode == Mode.BURIAL_ARMOUR) {
			for (GameObject tunnel : ctx.objects.select().id(4618).nearest().first()) {
				if (ctx.camera.prepare(tunnel) && tunnel.interact("Climb")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return options.getAreaSmall().contains(ctx.players.local());
						}
					});
				}
				return;
			}
		}

		if (ctx.movement.findPath(options.getAreaSmall().getCentralTile().randomize(3, 3)).traverse()
				|| ctx.movement.stepTowards(options.getAreaSmall().getCentralTile().randomize(3, 3))) {
			sleep(500, 1500);
		}

		/*if (LogArtisanWorkshop.getAreaSmall().getCentralTile().distanceTo(ctx.players.local()) > 100) {
			LogArtisanWorkshop.get().getLogHandler().print("Too far from Artisan Workshop");
		}*/
	}
}
