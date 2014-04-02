package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.RechargeSummoning;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.TilePath;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 19:21
 */
public class YanilleRecharge extends RechargeSummoning {
	public static final Tile[] PATH_HOUSE_OBELISK = new Tile[]{
			new Tile(2544, 3096, 0), new Tile(2544, 3092, 0), new Tile(2547, 3092, 0), new Tile(2550, 3092, 0),
			new Tile(2553, 3092, 0), new Tile(2556, 3091, 0), new Tile(2559, 3090, 0), new Tile(2562, 3089, 0),
			new Tile(2565, 3089, 0), new Tile(2568, 3089, 0), new Tile(2571, 3089, 0), new Tile(2575, 3089, 0),
			new Tile(2577, 3091, 0), new Tile(2579, 3093, 0), new Tile(2581, 3095, 0), new Tile(2583, 3097, 0),
			new Tile(2585, 3097, 0), new Tile(2588, 3097, 0), new Tile(2591, 3097, 0), new Tile(2594, 3097, 0),
			new Tile(2597, 3097, 0), new Tile(2600, 3097, 0), new Tile(2603, 3097, 0), new Tile(2606, 3097, 0),
			new Tile(2610, 3101, 0), new Tile(2614, 3104, 0), new Tile(2617, 3108, 0), new Tile(2620, 3112, 0),
			new Tile(2622, 3117, 0), new Tile(2623, 3123, 0), new Tile(2624, 3130, 0), new Tile(2624, 3135, 0),
			new Tile(2624, 3141, 0), new Tile(2624, 3146, 0), new Tile(2623, 3152, 0), new Tile(2625, 3155, 0)};

	public YanilleRecharge(LogGildedAltar script) {
		super(script, Path.YANILLE_OBELISK, PATH_HOUSE_OBELISK);
		this.pathToSmall = new TilePath(ctx, YanilleBankWalk.PATH_FROM_OBELISK);
	}
}