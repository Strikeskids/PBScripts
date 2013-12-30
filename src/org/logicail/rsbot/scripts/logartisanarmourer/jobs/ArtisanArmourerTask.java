package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;

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
		return LogArtisanArmourer.getAreaSmall().contains(ctx.players.local());
	}
}
