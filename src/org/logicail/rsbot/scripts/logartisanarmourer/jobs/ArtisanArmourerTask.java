package org.logicail.rsbot.scripts.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshopOptions;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 22/06/12
 * Time: 10:32
 */
public abstract class ArtisanArmourerTask extends Task<LogArtisanWorkshop> {
	protected final LogArtisanWorkshopOptions options;

	public ArtisanArmourerTask(LogArtisanWorkshop script) {
		super(script);
		this.options = script.options;
	}

	@Override
	public boolean activate() {
		return options.getAreaSmall().contains(ctx.players.local());
	}
}
