package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 5/29/12
 * Time: 11:41 AM
 */

public enum LocationAttribute {
	ANYWHERE(null, null),
	CASTLE_WARS(new LogicailArea(new Tile(2444, 3088, 0), new Tile(2449, 3082, 0)),
			new LogicailArea(new Tile(2434, 3107, 0), new Tile(2475, 3066, 0)),
			new LogicailArea(new Tile(2461, 3090, 0), new Tile(2465, 3085, 0))),
	DAEMONHEIM(new LogicailArea(new Tile(3447, 3721, 0), new Tile(3452, 3715, 0)),
			new LogicailArea(new Tile(3437, 3727, 0), new Tile(3461, 3690, 0))),
	// TODO: Varrock obelisk at edgeville (Level 21) then bank at grand exchange
	VARROCK(new LogicailArea(new Tile(3180, 3449, 0), new Tile(3190, 3430, 0)),
			new LogicailArea(new Tile(3155, 3469, 0), new Tile(3223, 3419, 0))),

	YANILLE_BANK(new LogicailArea(new Tile(2608, 3098, 0), new Tile(2614, 3087, 0)),
			new LogicailArea(new Tile(2538, 3107, 0), new Tile(2538, 3084, 0),
					new Tile(2632, 3084, 0), new Tile(2632, 3161, 0),
					new Tile(2619, 3161, 0), new Tile(2619, 3138, 0),
					new Tile(2609, 3107, 0)),
			new LogicailArea(new Tile(2619, 3159, 0), new Tile(2628, 3151, 0))),

	/*
	// orig new LogicailArea(new Tile(3089, 3501, 0), new Tile(3099, 3487, 0))
	Area myArea = new Area(new Tile[] { new Tile(3091, 3499, 0), new Tile(3091, 3487, 0), new Tile(3095, 3487, 0),
	 new Tile(3095, 3494, 0), new Tile(3099, 3494, 0), new Tile(3099, 3499, 0),
	 new Tile(3091, 3499, 0) });

	 new LogicailArea(new Tile(3091, 3500, 0), new Tile(3091, 3488, 0), new Tile(3095, 3488, 0),
			new Tile(3095, 3496, 0), new Tile(3099, 3496, 0), new Tile(3099, 3500, 0)),
	 */
	EDGEVILLE(new LogicailArea(new Tile(3091, 3500, 0), new Tile(3091, 3488, 0), new Tile(3095, 3488, 0),
			new Tile(3095, 3496, 0), new Tile(3099, 3496, 0), new Tile(3099, 3500, 0)),
			new LogicailArea(new Tile(3059, 3521, 0), new Tile(3135, 3480, 0)),
			new LogicailArea(new Tile(3124, 3518, 0), new Tile(3131, 3513, 0))),

	LUNAR_ISLE(new LogicailArea(new Tile(2097, 3920, 0), new Tile(2102, 3916, 0)),
			new LogicailArea(new Tile(2073, 3928, 0), new Tile(2120, 3903, 0)),
			new LogicailArea(new Tile(2110, 3918, 0), new Tile(2115, 3912, 0))),

	BURTHORPE(new LogicailArea(new Tile(2889, 3533, 0), new Tile(2895, 3527, 0)),
			new LogicailArea(new Tile(2874, 3575, 0), new Tile(2938, 3437, 0)),
			new LogicailArea(new Tile(2922, 3452, 0), new Tile(2935, 3443, 0))),

	FIGHT_KILN(new LogicailArea(new Tile(4737, 5175, 0), new Tile(4749, 5162, 0)),
			new LogicailArea(new Tile(4635, 5183, 0), new Tile(4749, 5140, 0)),
			new LogicailArea(new Tile(4656, 5145, 0), new Tile(4665, 5137, 0))),

	TZHAAR_MAIN_PLAZA(new LogicailArea(new Tile(4643, 5153, 0), new Tile(4647, 5145, 0)),
			new LogicailArea(new Tile(4635, 5183, 0), new Tile(4749, 5140, 0)),
			new LogicailArea(new Tile(4656, 5145, 0), new Tile(4665, 5137, 0))),

	CANIFIS(new LogicailArea(new Tile(3508, 3484, 0), new Tile(3513, 3477, 0)),
			new LogicailArea(new Tile(3440, 3505, 0), new Tile(3528, 3460, 0)),
			new LogicailArea(new Tile(3449, 3493, 0), new Tile(3454, 3488, 0))),

	// House locations
	YANILLE_HOUSE(new LogicailArea(new Tile(2539, 3100, 0), new Tile(2550, 3089, 0)),
			new LogicailArea(new Tile(2520, 3106, 0), new Tile(2617, 3076, 0))),

	RIMMINGTON_HOUSE(new LogicailArea(new Tile(2950, 3230, 0), new Tile(2958, 3220, 0)),
			new LogicailArea(new Tile(2943, 3223, 0), new Tile(2965, 3227, 0))),

	POLLNIVNEACH_HOUSE(new LogicailArea(new Tile(3331, 3009, 0), new Tile(3346, 3001, 0)),
			new LogicailArea(new Tile(3331, 3009, 0), new Tile(3346, 3001, 0))),

	TAVERLEY_HOUSE(new LogicailArea(new Tile(2881, 3455, 0), new Tile(2884, 3450, 0)),
			new LogicailArea(new Tile(2875, 3457, 0), new Tile(2888, 3430, 0))),

	RELLEKKA_HOUSE(new LogicailArea(new Tile(2663, 3634, 0), new Tile(2676, 3621, 0)),
			new LogicailArea(new Tile(2663, 3634, 0), new Tile(2676, 3621, 0))),

	BRIMHAVEN_HOUSE(new LogicailArea(new Tile(2753, 3184, 0), new Tile(2766, 3174, 0)),
			new LogicailArea(new Tile(2753, 3184, 0), new Tile(2766, 3174, 0))),

	FIGHT_CAVES(new LogicailArea(new Tile(4604, 5140, 0), new Tile(4615, 5120, 0)),
			new LogicailArea(new Tile(4600, 5183, 0), new Tile(4749, 5140, 0)),
			new LogicailArea(new Tile(4656, 5145, 0), new Tile(4665, 5137, 0))),

	BURTHORPE_GAMES_ROOM(new LogicailArea(new Tile(2999, 9681, 0), new Tile(3003, 9677, 0)),
			new LogicailArea(new Tile(2957, 9697, 0), new Tile(3010, 9665, 0)));

	private final LogicailArea smallArea, largeArea, obeliskArea;

	private LocationAttribute(final LogicailArea smallArea, LogicailArea largeArea) {
		this(smallArea, largeArea, null);
	}

	private LocationAttribute(final LogicailArea smallArea, final LogicailArea largeArea, final LogicailArea obeliskArea) {
		this.smallArea = smallArea;
		this.largeArea = largeArea;
		this.obeliskArea = obeliskArea;

		assert smallArea != null;
		assert largeArea != null;
	}

	public LogicailArea getObeliskArea() {
		return obeliskArea;
	}

	public LogicailArea getSmallArea() {
		return smallArea;
	}

	public Tile getObeliskRandom(IMethodContext ctx) {
		return obeliskArea != null ? obeliskArea.getRandomReachable(ctx, 3) : null;
	}

	public Tile getSmallRandom(IMethodContext ctx) {
		return smallArea.getRandomReachable(ctx, 3);
	}

	public boolean isInLargeArea(IMethodContext ctx) {
		waitUntilMapReady(ctx);
		return largeArea.contains(ctx.players.local());
	}

	private void waitUntilMapReady(IMethodContext ctx) {
		if (ctx.game.getClientState() != Game.INDEX_MAP_LOADED) {
			Timer t = new Timer(2000);
			while (t.isRunning()) {
				if (ctx.game.getClientState() == Game.INDEX_MAP_LOADED) {
					break;
				}
				ctx.game.sleep(100, 200);
			}
		}
	}

	public boolean isInObeliskArea(IMethodContext ctx) {
		waitUntilMapReady(ctx);
		return obeliskArea != null && obeliskArea.contains(ctx.players.local());
	}

	public boolean isInSmallArea(IMethodContext ctx) {
		waitUntilMapReady(ctx);
		return smallArea.contains(ctx.players.local());
	}
}
