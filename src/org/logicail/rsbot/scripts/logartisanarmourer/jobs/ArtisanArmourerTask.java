package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourerOptions;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 22/06/12
 * Time: 10:32
 */
public abstract class ArtisanArmourerTask extends Task {
	protected final LogArtisanArmourer script;
	protected final LogArtisanArmourerOptions options;

	public ArtisanArmourerTask(LogArtisanArmourer script) {
		super(script.ctx);
		this.script = script;
		this.options = script.options;
	}

	@Override
	public boolean activate() {
		return script.getAreaSmall().contains(ctx.players.local());
	}
}
