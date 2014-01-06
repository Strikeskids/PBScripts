package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 18:06
 */
public class TeleportSucceeded extends LogicailMethodProvider implements Callable<Boolean> {
	private final Tile start;

	public TeleportSucceeded(LogicailMethodContext ctx, Tile start) {
		super(ctx);
		this.start = start;
	}

	@Override
	public Boolean call() throws Exception {
		final Player local = ctx.players.local();
		return local == null || (local.getAnimation() == -1 && !local.getLocation().equals(start));
	}
}
