package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 21:33
 */
public class RenewFamiliar extends LogGildedAltarTask {
	@Override
	public String toString() {
		return "RenewFamiliar";
	}

	public RenewFamiliar(LogGildedAltar script) {
		super(script);
	}

	@Override
	public boolean isValid() {
		if (options.useBOB && options.beastOfBurden.getBoBSpace() > 0 && System.currentTimeMillis() > script.nextSummon.get()) {
			if (ctx.summoning.getTimeLeft() <= 300 || !ctx.summoning.isFamiliarSummoned()) {
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

	public static List<Tile> familarTile(MethodContext ctx) {
		final Tile location = ctx.players.local().getLocation();
		final LogicailArea area = new LogicailArea(location.derive(-6, -6), location.derive(6, 6));
		List<Tile> tiles = new ArrayList<Tile>();
		for (Tile tile : area.getTileArray()) {
			final double distanceTo = location.distanceTo(tile);
			if (distanceTo > 2 && distanceTo < 6) {
				tiles.add(tile);
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
