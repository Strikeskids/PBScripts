package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille;

import org.logicail.rsbot.scripts.framework.context.providers.ILodestone;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LodestoneTeleport;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.util.Condition;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 19:28
 */
public class YanilleLodestone extends LodestoneTeleport {
	public YanilleLodestone(LogGildedAltar script) {
		super(script, Path.YANILLE_LODESTONE, ILodestone.Lodestone.YANILlE);
	}

	@Override
	public boolean doLarge() {
		return !script.houseTask.isInHouse() && !locationAttribute.isInLargeArea(ctx) && super.doLarge();
	}

	@Override
	public boolean doSmall() {
		if (!script.houseTask.isInHouse()) {

			if (locationAttribute.getSmallRandom(ctx).getMatrix(ctx).isReachable()) {
				script.housePortal.enterPortal();
			} else {
				ctx.movement.findPath(locationAttribute.getSmallRandom(ctx)).traverse();
			}

			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.players.local().isInMotion();
				}
			}, 100, 10)) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.players.local().isInMotion() || locationAttribute.isInSmallArea(ctx);
					}
				});
			}
		}
		return locationAttribute.isInSmallArea(ctx);
	}
}
