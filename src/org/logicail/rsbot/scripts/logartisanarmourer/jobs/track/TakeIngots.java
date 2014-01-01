package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track;

import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/01/13
 * Time: 11:57
 */
public class TakeIngots extends ArtisanArmourerTask {
	public TakeIngots(LogArtisanWorkshop script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Take Ingots";
	}

	@Override
	public boolean activate() {
		return super.activate()
				//&& (!Inventory.isFull() && (!Inventory.contains(SmithTrackOld.RAILS) || !Inventory.contains(SmithTrackOld.BASE_PLATE)))
				&& AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING) > SmithTrack.animationTimelimit
				&& ctx.backpack.select().id(SmithTrack.TRACK_100).isEmpty()
				&& !ctx.backpack.isFull()
				//&& !Inventory.contains(getIngotId())
				&& !getTrough().isEmpty();
	}

	private static final int BRONZE_TROUGH = 24821;
	private static final int IRON_TROUGH = 24822;
	private static final int STEEL_TROUGH = 24823;

	private BasicNamedQuery<GameObject> getTrough() {
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
	public void run() {
		options.status = "Take ingots";

		if (ctx.skillingInterface.isProductionInterfaceOpen()) {
			ctx.skillingInterface.cancelProduction();
			return;
		}

		if (ctx.skillingInterface.isOpen()) {
			if (ctx.skillingInterface.getAction().equals("Take")) {
				if (ctx.skillingInterface.select("Ingots, Tier I", options.getIngotId())) {
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
			if (SmithAnvil.anvilLocation == null) {
				SmithAnvil.anvilLocation = trough.getLocation();
			}
			if (ctx.camera.prepare(trough) && trough.interact("Take-ingots", trough.getName())) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return trough.getLocation().distanceTo(ctx.players.local()) < 3;
					}
				});
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.skillingInterface.isOpen();
					}
				});
				sleep(250, 1000);
			} else if (ctx.movement.stepTowards(trough)) {
				sleep(750, 1600);
			}
		}
	}
}
