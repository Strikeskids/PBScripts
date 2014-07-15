package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.IMovement;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.LogGildedAltarTask;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:16
 */
public abstract class NodePath extends LogGildedAltarTask {
	protected final LocationAttribute locationAttribute;
	protected final Path path;

	public NodePath(LogGildedAltar script, Path path) {
		super(script);
		this.path = path;
		this.locationAttribute = path.getLocation();
	}

	public Path getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path.getName();
	}

	public List<BankRequiredItem> getItemsNeededFromBank() {
		return new ArrayList<BankRequiredItem>();
	}

	@Override
	public boolean valid() {
		return !locationAttribute.isInSmallArea(ctx)
				&& (locationAttribute.isInLargeArea(ctx) || getItemsNeededFromBank().isEmpty());
	}

	@Override
	public void run() {
		if (!locationAttribute.isInLargeArea(ctx)) {
			//ctx.log.info("large");
			doLarge();
		} else if (!locationAttribute.isInSmallArea(ctx)) {
			//ctx.log.info("small");
			final Tile destination = ctx.movement.destination();
			double distance = IMovement.Euclidean(ctx.players.local().tile(), destination);
			if (!destination.matrix(ctx).onMap() || distance > 100 || !locationAttribute.getSmallArea().contains(destination)) {
				doSmall();
			}
		}
	}

	protected abstract boolean doLarge();

	protected boolean doSmall() {
		final LocationAttribute location = path.getLocation();
		final Tile tile = location.getSmallRandom(ctx);
		if (ctx.movement.findPath(tile).traverse()) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return location.isInSmallArea(ctx) || tile.distanceTo(ctx.players.local()) < 4;
				}
			}, 200, 25);
		}
		ctx.sleep(400);
		return location.isInSmallArea(ctx);
	}
}
