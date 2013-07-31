package org.logicail.scripts.logartisanarmourer.tasks.track;

import org.logicail.api.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 12:05
 */
public class TakeIngots extends Node {
	private static final int BRONZE_TROUGH = 24821;
	private static final int IRON_TROUGH = 24822;
	private static final int STEEL_TROUGH = 24823;
	private final LogArtisanArmourerOptions options;
	private Tracks tracks;

	public TakeIngots(Tracks tracks) {
		super(tracks.ctx);
		this.tracks = tracks;

		options = ((LogArtisanArmourer) ctx.script).options;
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.isFull()
				&& ctx.backpack.select().id(Tracks.TRACK_100).isEmpty()
				&& ctx.animationHistory.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING) > SmithTrack.animationTimelimit
				&& !ctx.objects.select().id(getTroughId()).nearest().first().isEmpty();
	}

	private int getTroughId() {
		switch (options.ingotType) {
			case BRONZE:
				return BRONZE_TROUGH;
			case IRON:
				return IRON_TROUGH;
			case STEEL:
				return STEEL_TROUGH;
		}
		return -1;
	}

	@Override
	public void execute() {
		//Logger.getLogger(LogArtisanArmourer.class.getName()).info("Should make:" + SmithTrackOld.getMaking());

		if (!ctx.backpack.isEmpty() && Random.nextBoolean()) {
			sleep(500, 1500);
		}

		if (ctx.skillingInterface.isProductionInterfaceOpen()) {
			ctx.skillingInterface.cancelProduction();
			return;
		}

		if (ctx.skillingInterface.isOpen()) {
			if (ctx.skillingInterface.getAction().equals("Take")) {
				if (ctx.skillingInterface.select("Ingots, Tier I", tracks.getIngotId())) {
					if (ctx.skillingInterface.start()) {
						ctx.waiting.wait(1500, new Condition() {
							@Override
							public boolean validate() {
								return !ctx.backpack.select().id(tracks.getIngotId()).isEmpty();
							}
						});
					}
					return;
				}
			} else {
				ctx.skillingInterface.close();
				return;
			}
		}

		for (GameObject trough : ctx.objects) {
			if (ctx.camera.turnTo(trough)) {
				String option = null;
				switch (options.ingotType) {
					case BRONZE:
						option = "Bronze ingot trough";
						break;
					case IRON:
						option = "Iron ingot trough";
						break;
					case STEEL:
						option = "Steel ingot trough";
						break;
				}

				if (trough.interact("Take-ingots", option)) {
					ctx.waiting.wait(2000, new Condition() {
						@Override
						public boolean validate() {
							return ctx.skillingInterface.isOpen();
						}
					});
				}
			}
		}
	}
}
