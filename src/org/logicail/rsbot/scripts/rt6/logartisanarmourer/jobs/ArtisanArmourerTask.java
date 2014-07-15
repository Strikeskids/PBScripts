package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs;

import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshopOptions;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 22/06/12
 * Time: 10:32
 */
public abstract class ArtisanArmourerTask extends Node<LogArtisanWorkshop> {
	protected final LogArtisanWorkshopOptions options;

	protected ArtisanArmourerTask(LogArtisanWorkshop script) {
		super(script);
		this.options = script.options;
	}

	@Override
	public boolean valid() {
		return options.getAreaSmall().contains(ctx.players.local());
	}
}
