package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.Tile;

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
	private final Path path;

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
	public boolean isValid() {
		return !path.getLocation().isInSmallArea(ctx)
				&& (path.getLocation().isInLargeArea(ctx) || getItemsNeededFromBank().isEmpty());
	}

	@Override
	public void run() {
		if (!locationAttribute.isInLargeArea(ctx)) {
			doLarge();
		} else {
			if (!locationAttribute.isInSmallArea(ctx)) {
				final Tile destination = ctx.movement.getDestination();
				double distance = destination.distanceTo(ctx.players.local());
				if (distance > 100 || !locationAttribute.getSmallArea().contains(destination)) {
					if (doSmall()) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return locationAttribute.isInSmallArea(ctx) || ctx.movement.getDestination().distanceTo(ctx.players.local()) < 5;
							}
						});
					}
				}
			}
		}
	}

	protected abstract boolean doLarge();

	protected boolean doSmall() {
		final LocationAttribute location = path.getLocation();
		final Tile tile = location.getSmallRandom(ctx);
		if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(tile)) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return location.isInSmallArea(ctx);
				}
			});
		}
		sleep(250, 1250);
		return location.isInSmallArea(ctx);
	}
}
