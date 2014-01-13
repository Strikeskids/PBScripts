package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.LocationAttribute;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 21:33
 */
public class RenewFamiliar extends LogGildedAltarTask {
	private static final LogicailArea EDGEVILLE_AREA_LEFT = new LogicailArea(new Tile(3086, 3495, 0), new Tile(3091, 3488, 0));
	private static final LogicailArea EDGEVILLE_AREA_TOP = new LogicailArea(new Tile(3088, 3505, 0), new Tile(3095, 3499, 0));

	public RenewFamiliar(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "RenewFamiliar";
	}

	@Override
	public boolean isValid() {
		if (options.useBOB.get() && options.beastOfBurden.getBoBSpace() > 0 && System.currentTimeMillis() > script.nextSummon.get()) {
			if (ctx.summoning.getTimeLeft() <= 150 || !ctx.summoning.isFamiliarSummoned()) {
				return ctx.summoning.canSummon(options.beastOfBurden);
			}
		}
		return false;
	}

	@Override
	public void run() {
		script.familiarFailed.set(false);
		renew(script);
		sleep(1000, 3000);

		if (script.familiarFailed.get()) {
			script.options.status = "Renewing familiar failed";
			final Tile start = ctx.players.local().getLocation();
			final List<Tile> tiles = familarTile(ctx);
			for (final Tile tile : tiles) {
				script.familiarFailed.set(false);
				if (ctx.movement.findPath(tile).traverse()) {
					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return IMovement.Euclidean(tile, ctx.players.local()) < 2.5;
						}
					}, Random.nextInt(400, 650), Random.nextInt(10, 15)) || !start.equals(start)) {
						renew(script);
						if (!script.familiarFailed.get()) {
							break;
						}
						sleep(600, 1800);
					}
				}
			}
		}

		script.nextSummon.set(System.currentTimeMillis() + Random.nextInt(5000, 15000));
	}

	public static List<Tile> familarTile(IMethodContext ctx) {
		final Tile location = ctx.players.local().getLocation();
		List<Tile> tiles = new ArrayList<Tile>();
		LogicailArea area = new LogicailArea(location.derive(-7, -7), location.derive(5, 5));

		boolean edgeville = false;

		if (LocationAttribute.EDGEVILLE.isInLargeArea(ctx)) {
			edgeville = true;
			if (IMovement.Euclidean(location, EDGEVILLE_AREA_LEFT.getCentralTile()) < IMovement.Euclidean(location, EDGEVILLE_AREA_TOP.getCentralTile())) {
				area = EDGEVILLE_AREA_LEFT;
			} else {
				area = EDGEVILLE_AREA_TOP;
			}
		}

		for (Tile tile : area.getTileArray()) {
			final double distanceTo = location.distanceTo(tile);
			if (distanceTo > 2 && (edgeville || distanceTo < 9)) {
				tiles.add(tile);
			}
		}

		Iterator<Tile> iterator = tiles.iterator();
		while (iterator.hasNext()) {
			final Tile next = iterator.next();
			if (!next.getMatrix(ctx).isReachable()) {
				iterator.remove();
			}
		}

		Collections.shuffle(tiles);

		return tiles.isEmpty() ? tiles : tiles.subList(0, Math.min(6, tiles.size()));
	}

	public static void renew(final LogGildedAltar script) {
		script.options.status = "Renewing familiar";

		if (script.ctx.summoning.isFamiliarSummoned()) {
			script.ctx.summoning.renewFamiliar();
		} else {
			script.ctx.summoning.summon(script.options.beastOfBurden);
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return script.familiarFailed.get() || !(script.ctx.summoning.canSummon(script.options.beastOfBurden) && (script.ctx.summoning.getTimeLeft() <= 300 || !script.ctx.summoning.isFamiliarSummoned()));
				}
			});
		}
	}
}
