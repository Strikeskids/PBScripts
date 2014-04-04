package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.HouseTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.TilePath;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:29
 */
public class YanillePortalWalk extends NodePath {
	private static final Tile[] PATH_TO_PORTAL;

	static {
		final List<Tile> list = Arrays.asList(new Tile(2544, 3092, 0), new Tile(2547, 3092, 0), new Tile(2550, 3092, 0),
				new Tile(2553, 3092, 0), new Tile(2556, 3091, 0), new Tile(2559, 3090, 0),
				new Tile(2562, 3089, 0), new Tile(2565, 3089, 0), new Tile(2568, 3089, 0),
				new Tile(2571, 3089, 0), new Tile(2575, 3089, 0), new Tile(2577, 3091, 0),
				new Tile(2579, 3093, 0), new Tile(2581, 3095, 0), new Tile(2583, 3097, 0),
				new Tile(2585, 3097, 0), new Tile(2588, 3097, 0), new Tile(2591, 3097, 0),
				new Tile(2594, 3097, 0), new Tile(2597, 3097, 0), new Tile(2600, 3096, 0),
				new Tile(2603, 3095, 0), new Tile(2606, 3094, 0), new Tile(2609, 3093, 0),
				new Tile(2610, 3093, 0));
		Collections.reverse(list);
		PATH_TO_PORTAL = list.toArray(new Tile[list.size()]);
	}

	public YanillePortalWalk(LogGildedAltar script) {
		super(script, Path.HOME_YANILLE_WALK);
	}

	@Override
	public boolean doLarge() {
		return false;
	}

	@Override
	public boolean doSmall() {
		if ((script.houseTask.getHouseLocation() != HouseTask.HouseLocation.YANILLE && !options.useOtherHouse.get())) {
			options.status = "House not in yanille";
			return false;
		} else {
			options.status = "Walking to yanille portal";
			final Tile tile = locationAttribute.getSmallRandom(ctx);
			if (tile.distanceTo(ctx.players.local()) < 16) {
				if (ctx.movement.findPath(tile).traverse()) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return tile.distanceTo(ctx.players.local()) < 4;
						}
					});
				}
			}

			return new TilePath(ctx, PATH_TO_PORTAL).randomize(1, 2).traverse();
		}
	}

	@Override
	public boolean valid() {
		return locationAttribute.isInLargeArea(ctx)
				&& !locationAttribute.isInSmallArea(ctx);
	}
}
