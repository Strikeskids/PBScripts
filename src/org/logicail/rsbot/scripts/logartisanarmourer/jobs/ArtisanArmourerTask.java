package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.powerbot.script.wrappers.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 22/06/12
 * Time: 10:32
 */
public abstract class ArtisanArmourerTask extends Task {
	public ArtisanArmourerTask(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public boolean activate() {
		Player player = ctx.players.local();
		return ctx.game.isLoggedIn()
				&& player != null
				//&& !player.isInMotion() Too stop-starty
				&& LogArtisanArmourer.getAreaSmall().contains(player.getLocation());
	}
}
