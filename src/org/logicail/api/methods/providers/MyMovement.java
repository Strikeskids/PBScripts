package org.logicail.api.methods.providers;

import org.logicail.api.Area;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Movement;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 18:23
 */
public class MyMovement extends Movement {
	public MyMovement(MethodContext ctx) {
		super(ctx);
	}

	public int getDistance(Locatable locatable) {
		return getDistance(ctx.players.local(), locatable);
	}

	public Tile reachableNear(Locatable locatable) {
		Tile min = locatable.getLocation().derive(-6, -6);
		Tile max = min.derive(12, 12);
		Area area = new Area(min, max);
		ArrayList<Tile> possibleTiles = new ArrayList<>();
		for (Tile tile : area.getTileArray()) {
			if (tile.getMatrix(ctx).isReachable()) {
				possibleTiles.add(tile);
			}
		}

		if (!possibleTiles.isEmpty()) {
			final Player local = ctx.players.local();
			Collections.sort(possibleTiles, new Comparator<Tile>() {
				@Override
				public int compare(Tile o1, Tile o2) {
					return getDistance(local, o1) - getDistance(local, o2);
				}
			});

			ArrayList<Tile> tiles = new ArrayList<>();
			int distance = getDistance(possibleTiles.get(0)) + 3;
			for (Tile tile : possibleTiles) {
				if (getDistance(local, tile) <= distance) {
					tiles.add(tile);
				}
			}

			return tiles.get(Random.nextInt(0, tiles.size()));
		}

		return Tile.NIL;
	}
}
