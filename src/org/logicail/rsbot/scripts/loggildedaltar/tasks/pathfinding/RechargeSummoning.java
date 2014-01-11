package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TilePath;

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
	public boolean isValid() {
		return locationAttribute.isInLargeArea(ctx)/* || getItemsNeededFromBank() == null*/;
	}

	@Override
	public void run() {
		if (!locationAttribute.isInLargeArea(ctx)) {
			if (doLarge()) {
				sleep(250, 1000);
			}
			return;
		}

		if (!locationAttribute.isInObeliskArea(ctx)) {
			if (doSmall()) {
				sleep(400, 1200);
			}
		} else {
			renewPoints();
		}

		sleep(200, 600);
	}

	@Override
	protected boolean doLarge() {
		return false;
	}

	@Override
	protected boolean doSmall() {
		if (pathToObelisk == null) {
			return ctx.movement.findPath(locationAttribute.getObeliskRandom(ctx)).traverse();
		} else {
			pathToObelisk.randomize(2, 2);
			return pathToObelisk.traverse() || ctx.movement.findPath(locationAttribute.getObeliskRandom(ctx)).traverse();
		}
	}

	protected boolean renewPoints() {
		int points = ctx.summoning.getSummoningPoints();

		for (GameObject obelisk : ctx.objects.select().id(OBELISK).within(20).first()) {
			if (ctx.camera.prepare(obelisk) && obelisk.interact("Renew")) {
				Timer t = new Timer(10000);
				while (t.isRunning()) {
					if (ctx.players.local().isInMotion()) {
						t.reset();
					}
					if (ctx.camera.getPitch() < 80) {
						ctx.camera.setPitch(Random.nextInt(80, 101));
					}

					if (ctx.summoning.getSummoningPoints() > points && ctx.players.local().getAnimation() == -1) {
						options.status = "Recharged at obelisk";
						script.log.info(options.status);
						script.summoningTask.nextPoints = Random.nextInt((int) (options.beastOfBurden.getRequiredPoints() * 1.5), (int) (options.beastOfBurden.getRequiredPoints() * 2.33));
						sleep(500, 1500);
						return true;
					}

					sleep(400, 800);
				}
			}
		}

		return false;
	}
}