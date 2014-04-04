package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Player;

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
	public boolean valid() {
		final Player local = ctx.players.local();
		return local.idle()
				&& !options.getAreaSmall().contains(local);
	}

	@Override
	public void run() {
		if (options.mode == Mode.BURIAL_ARMOUR) {
			final GameObject tunnel = ctx.objects.select().id(4618).nearest().poll();
			if (tunnel.valid() && ctx.camera.prepare(tunnel) && tunnel.interact("Climb")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return options.getAreaSmall().contains(ctx.players.local());
					}
				});
			}
			return;
		}


		if (ctx.movement.findPath(options.getAreaSmall().getCentralTile().derive(Random.nextInt(-3, 3), Random.nextInt(-3, 3))).traverse()
				|| ctx.movement.step(options.getAreaSmall().getCentralTile().derive(Random.nextInt(-3, 3), Random.nextInt(-3, 3)))) {
			sleep(600);
		}

		/*if (LogArtisanWorkshop.getAreaSmall().getCentralTile().distanceTo(ctx.players.local()) > 100) {
			LogArtisanWorkshop.get().getLogHandler().print("Too far from Artisan Workshop");
		}*/
	}
}
