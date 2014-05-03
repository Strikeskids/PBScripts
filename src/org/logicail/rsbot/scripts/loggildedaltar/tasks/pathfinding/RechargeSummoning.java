package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.providers.IMovement;
import org.logicail.rsbot.scripts.framework.util.Timer;
import org.logicail.rsbot.scripts.framework.wrappers.ITile;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.TilePath;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 18:50
 */
public class RechargeSummoning extends NodePath {
	protected static final int[] OBELISK = {29953, 29954, 67036, 68237};
	protected TilePath pathToObelisk = null;
	protected TilePath pathToSmall = null;

	public RechargeSummoning(LogGildedAltar script, Path path) {
		this(script, path, null);
	}

	public RechargeSummoning(LogGildedAltar script, final Path path, Tile... pathBankToObelisk) {
		super(script, path);

		if (pathBankToObelisk != null) {
			this.pathToObelisk = new TilePath(ctx, pathBankToObelisk);
			this.pathToSmall = pathToObelisk.reverse();
		}
	}

	@Override
	protected boolean doLarge() {
		return false;
	}

	@Override
	public boolean valid() {
		return locationAttribute.isInLargeArea(ctx);
	}

	@Override
	public void run() {
		final GameObject obelisk = ctx.objects.select().id(OBELISK).nearest().poll();
		if (obelisk.valid() && IMovement.Euclidean(obelisk, ctx.players.local()) < 5) {
			renewPoints();
		} else {
			if (!locationAttribute.isInObeliskArea(ctx)) {
				if (doSmall()) {
					ctx.sleep(666);
				}
			}
		}

		ctx.sleep(300);
	}

	@Override
	protected boolean doSmall() {
		Tile obeliskRandom;

		final GameObject obelisk = ctx.objects.select().id(OBELISK).nearest().poll();
		if (obelisk.valid()) {
			obeliskRandom = ITile.randomize(obelisk.tile(), 2, 2);
		} else {
			obeliskRandom = locationAttribute.getObeliskRandom(ctx);
		}

		if (obeliskRandom != null && ctx.movement.findPath(obeliskRandom).traverse() || ctx.movement.step(obeliskRandom)) {
			ctx.sleep(1200);
			return true;
		}

		if (pathToObelisk != null) {
			pathToObelisk.randomize(2, 2);
			return pathToObelisk.traverse();
		}

		return false;
	}

	protected boolean renewPoints() {
		int points = ctx.summoning.points();

		for (GameObject obelisk : ctx.objects.select().id(OBELISK).within(20).first()) {
			if (ctx.camera.prepare(obelisk) && obelisk.interact("Renew")) {
				Timer t = new Timer(10000);
				while (t.running()) {
					if (ctx.players.local().inMotion()) {
						t.reset();
					}
					if (ctx.camera.pitch() < 80) {
						ctx.camera.pitch(Random.nextInt(80, 101));
					}

					if (ctx.summoning.points() > points) {
						options.status = "Recharged at obelisk";
						script.log.info(options.status);
						script.summoningTask.nextPoints = Random.nextInt((int) (options.beastOfBurden.requiredPoints() * 1.5), (int) (options.beastOfBurden.requiredPoints() * 2.33));
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.players.local().idle();
							}
						}, 250, 10);
						ctx.sleep(500);
						return true;
					}

					ctx.sleep(500);
				}
			}
		}

		return false;
	}
}