package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track40 extends AbstractTrack {
	public Track40(LogArtisanWorkshop script, SmithTrack smithTrack) {
		super(script, smithTrack);
	}

	@Override
	public String toString() {
		return "Track40";
	}

	@Override
	public boolean valid() {
		return !ctx.backpack.select().id(options.getIngotId()).isEmpty() || !ctx.backpack.select().id(smithTrack.getBasePlate()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			script.log.info("Anvil not ready");
			return;
		}

		int rails = ctx.backpack.select().id(smithTrack.getRails()).count();

		int baseplate = ctx.backpack.select().id(smithTrack.getBasePlate()).count();
		int ingots = ctx.backpack.select().id(options.getIngotId()).count();

		//ctx.log.info(rails + " " + baseplate + " " + ingots);

		if (ingots > 0) {
			int x = (ingots + rails + baseplate) / 2;
			if (rails != x || baseplate != x || rails == 0) {
				if (rails == 0) {
					// Check don't have any partially complete tracks
					if (!ctx.backpack.select().id(SmithTrack.PARTS).isEmpty()) {
						script.log.info("Deposit partial tracks/parts");
						ctx.skillingInterface.close();
						sleep(400);
						for (GameObject minecart : ctx.objects.select().id(LogArtisanWorkshop.ID_MINE_CART).nearest().limit(3).shuffle().first()) {
							if (ctx.camera.prepare(minecart) && minecart.interact("Deposit-components", "Mine cart")) {
								Condition.wait(new Callable<Boolean>() {
									@Override
									public Boolean call() throws Exception {
										return ctx.backpack.select().id(SmithTrack.PARTS).isEmpty();
									}
								});
							} else {
								ctx.movement.step(minecart);
								sleep(1000);
							}
						}
						sleep(300);
					} else {
						script.log.info("Make Rails");
						smithTrack.smith(smithTrack.getRails());
					}
				} else {
					if (rails != x) {
						script.log.info("Make Rails");
						smithTrack.smith(smithTrack.getRails(), x - rails);
					} else {
						script.log.info("Make BasePlate");
						smithTrack.smith(smithTrack.getBasePlate(), x - baseplate);
					}
				}
			}
		} else {
			smithTrack.smith(smithTrack.getTrack40());
		}
	}
}
