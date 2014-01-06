package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Movement;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 12:57
 */
public class MyMovement extends Movement {
	private LogicailMethodContext ctx;

	public MyMovement(LogicailMethodContext context) {
		super(context);
		this.ctx = context;
	}

	public List<Tile> getTilesNear(LogicailArea area, Locatable locatable, double maxDistance) {
		List<Tile> tiles = new ArrayList<Tile>();

		final Filter<GameObject> filter = new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				final GameObject.Type type = gameObject.getType();
				return type == GameObject.Type.BOUNDARY || type == GameObject.Type.INTERACTIVE;
			}
		};

		for (Tile tile : area.getTileArray()) {
			if ((!tile.equals(locatable.getLocation()) || tile.getMatrix(ctx).isReachable()) && getDistance(locatable, tile) <= maxDistance) {
				if (ctx.objects.select().at(tile).select(filter).isEmpty()) {
					tiles.add(tile);
				}
			}
		}

		Collections.shuffle(tiles);

		return tiles;
	}

	public void walk(final Locatable locatable) {
		final Tile tile = locatable.getLocation();
		if (ctx.movement.findPath(locatable).traverse()) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return tile.getMatrix(ctx).isOnScreen() || tile.distanceTo(ctx.players.local()) < 5;
				}
			}, Random.nextInt(150, 250), Random.nextInt(10, 15));
		}
	}
}
