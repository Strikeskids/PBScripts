package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;

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
	public boolean activate() {
		return !ctx.backpack.select().id(options.getIngotId()).isEmpty() || !ctx.backpack.select().id(smithTrack.getBasePlate()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			ctx.log.info("Anvil not ready");
			return;
		}

		int rails = ctx.backpack.select().id(smithTrack.getRails()).count();

		int baseplate = ctx.backpack.select().id(smithTrack.getBasePlate()).count();
		int ingots = ctx.backpack.select().id(options.getIngotId()).count();

		//ctx.log.info(rails + " " + baseplate + " " + ingots);

		if (ingots > 0) {
			if (rails != baseplate || rails == 0) {
				if (rails == 0) {
					// Check don't have any partially complete tracks
					if (!ctx.backpack.select().id(SmithTrack.PARTS).isEmpty()) {
						ctx.log.info("Deposit partial tracks/parts");
						ctx.skillingInterface.close();
						sleep(200, 1000);
						for (GameObject minecart : ctx.objects.select().id(LogArtisanWorkshop.ID_MINE_CART).nearest().first()) {
							if (ctx.camera.prepare(minecart) && minecart.interact("Deposit-components", "Mine cart")) {
								Condition.wait(new Callable<Boolean>() {
									@Override
									public Boolean call() throws Exception {
										return ctx.backpack.select().id(SmithTrack.PARTS).isEmpty();
									}
								});
							} else {
								ctx.movement.stepTowards(minecart);
								sleep(1000, 2000);
							}
						}
						sleep(250, 1000);
					} else {
						ctx.log.info("Make Rails");
						smithTrack.smith(smithTrack.getRails());
					}
				} else {
					if (rails > baseplate) {
						ctx.log.info("Make BasePlate");
						smithTrack.smith(smithTrack.getBasePlate(), rails - baseplate);
					} else {
						ctx.log.info("Make Rails");
						smithTrack.smith(smithTrack.getRails(), baseplate - rails);
					}
				}
			}
		} else {
			smithTrack.smith(smithTrack.getTrack40());
		}
	}
}
