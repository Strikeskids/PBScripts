package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.track;

import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.MobileIdNameQuery;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/01/13
 * Time: 11:57
 */
public class TakeIngots extends ArtisanArmourerTask {
	private static final int BRONZE_TROUGH = 24821;
	private static final int IRON_TROUGH = 24822;
	private static final int STEEL_TROUGH = 24823;

	public TakeIngots(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Take Ingots";
	}

	private MobileIdNameQuery<GameObject> getTrough() {
		switch (options.ingotType) {
			case BRONZE:
				return ctx.objects.select().id(BRONZE_TROUGH).nearest().first();
			case IRON:
				return ctx.objects.select().id(IRON_TROUGH).nearest().first();
			default:
				return ctx.objects.select().id(STEEL_TROUGH).nearest().first();
		}
	}

	@Override
	public boolean valid() {
		return super.valid()
				//&& (!Inventory.isFull() && (!Inventory.contains(SmithTrackOld.RAILS) || !Inventory.contains(SmithTrackOld.BASE_PLATE)))
				&& AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING) > SmithTrack.animationTimelimit
				&& ctx.backpack.select().id(SmithTrack.TRACK_100).isEmpty()
				&& !ctx.backpack.isFull()
				//&& !Inventory.contains(getIngotId())
				&& !getTrough().isEmpty();
	}

	@Override
	public void run() {
		options.status = "Take ingots";

		if (ctx.skillingInterface.isProductionInterfaceOpen()) {
			ctx.skillingInterface.cancelProduction();
			return;
		}

		if (ctx.skillingInterface.opened()) {
			if (ctx.skillingInterface.getAction().equals("Take")) {
				if (ctx.skillingInterface.select(0, options.getIngotId())) {
					if (ctx.skillingInterface.start()) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return !ctx.backpack.select().id(options.getIngotId()).isEmpty();
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

		for (final GameObject trough : ctx.objects.first()) {
			if (ctx.camera.prepare(trough) && trough.interact("Take-ingots", trough.name())) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return trough.tile().distanceTo(ctx.players.local()) < 3;
					}
				});
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.skillingInterface.opened();
					}
				});
				Condition.sleep(333);
			} else if (ctx.movement.step(trough)) {
				Condition.sleep(888);
			}
		}
	}
}
