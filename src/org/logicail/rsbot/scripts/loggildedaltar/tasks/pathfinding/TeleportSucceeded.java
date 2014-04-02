package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.Player;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/01/14
 * Time: 18:06
 */
public class TeleportSucceeded extends IClientAccessor implements Callable<Boolean> {
	private final Tile start;

	public TeleportSucceeded(IClientContext ctx, Tile start) {
		super(ctx);
		this.start = start;
	}

	@Override
	public Boolean call() throws Exception {
		final Player local = ctx.players.local();
		return local == null || (local.animation() == -1 && !local.tile().equals(start));
	}
}
