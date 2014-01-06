package org.logicail.rsbot.util;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.context.LogicailMethodProvider;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.powerbot.script.wrappers.Player;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:13
 */
public class WaitForAnimationToFinish extends LogicailMethodProvider implements Callable<Boolean> {
	private final int animationId;
	private boolean done = false;

	public WaitForAnimationToFinish(LogicailMethodContext context, int animationId) {
		super(context);
		this.animationId = animationId;
	}

	@Override
	public Boolean call() throws Exception {
		if (AnimationMonitor.timeSinceAnimation(animationId) < 20000) {
			done = true;
		}
		final Player local = ctx.players.local();
		return done && local != null && local.getAnimation() == -1;
	}
}
