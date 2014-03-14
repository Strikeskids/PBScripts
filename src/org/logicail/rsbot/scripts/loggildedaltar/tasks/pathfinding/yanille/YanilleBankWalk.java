package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.HouseTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TilePath;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 19:23
 */
public class YanilleBankWalk extends NodePath {
	public static final Tile[] PATH_FROM_OBELISK = new Tile[]{
			new Tile(2625, 3155, 0), new Tile(2623, 3152, 0), new Tile(2624, 3146, 0), new Tile(2624, 3141, 0),
			new Tile(2624, 3135, 0), new Tile(2624, 3130, 0), new Tile(2623, 3123, 0), new Tile(2622, 3117, 0),
			new Tile(2620, 3112, 0), new Tile(2617, 3108, 0), new Tile(2614, 3104, 0), new Tile(2606, 3096, 0)};
	private static final Area OBELISK_AREA = new Area(new Tile(2604, 3112, 0),
			new Tile(2629, 3088, 0), new Tile(2637, 3159, 0), new Tile(2615, 3160, 0), new Tile(2604, 3112, 0));
	private static final Tile[] PATH_TO_BANK = new Tile[]{
			new Tile(2544, 3096, 0), new Tile(2544, 3092, 0), new Tile(2547, 3092, 0), new Tile(2550, 3092, 0),
			new Tile(2553, 3092, 0), new Tile(2556, 3091, 0), new Tile(2559, 3090, 0), new Tile(2562, 3089, 0),
			new Tile(2565, 3089, 0), new Tile(2568, 3089, 0), new Tile(2571, 3089, 0), new Tile(2575, 3089, 0),
			new Tile(2577, 3091, 0), new Tile(2579, 3093, 0), new Tile(2581, 3095, 0), new Tile(2583, 3097, 0),
			new Tile(2585, 3097, 0), new Tile(2588, 3097, 0), new Tile(2591, 3097, 0), new Tile(2594, 3097, 0),
			new Tile(2597, 3097, 0), new Tile(2600, 3096, 0), new Tile(2603, 3095, 0), new Tile(2606, 3094, 0),
			new Tile(2609, 3093, 0), new Tile(2610, 3093, 0)};

	public YanilleBankWalk(LogGildedAltar script) {
		super(script, Path.YANILLE_BANK_WALK);
	}

	@Override
	public boolean doLarge() {
		if (!options.useOtherHouse.get() && script.houseTask.getHouseLocation() != HouseTask.HouseLocation.YANILLE) {
			script.log.info("House not in yanille");
			return false;
		}

		if (script.houseTask.isInHouse() && !locationAttribute.isInLargeArea(ctx)) {
			script.houseTask.leaveHouse();
		}

		return script.houseTask.isInHouse() || locationAttribute.isInLargeArea(ctx);
	}

	@Override
	public boolean doSmall() {
		if (!options.useOtherHouse.get() && script.houseTask.getHouseLocation() != HouseTask.HouseLocation.YANILLE) {
			script.log.info("House not in yanille");
			return false;
		} else {
			options.status = "Walking to yanille bank";
			final Tile smallRandom = locationAttribute.getSmallRandom(ctx);
			if (smallRandom.distanceTo(ctx.players.local()) < 18) {
				if (ctx.movement.findPath(smallRandom).traverse()) {
					return true;
				}
			} else {
				if (OBELISK_AREA.contains(ctx.players.local())) {
					options.status = ("Walking back from obelisk");
					return new TilePath(ctx, PATH_FROM_OBELISK).randomize(2, 2).traverse();
				}
			}

			return new TilePath(ctx, PATH_TO_BANK).randomize(1, 1).traverse();
		}
	}

	@Override
	public boolean isValid() {
		return !locationAttribute.isInSmallArea(ctx) && (locationAttribute.isInLargeArea(ctx) || script.houseTask.isInHouse());
	}
}
