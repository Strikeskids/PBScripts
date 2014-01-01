package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 01/01/14
 * Time: 17:18
 */
public abstract class AbstractTrack extends ArtisanArmourerTask {
	protected final SmithTrack smithTrack;

	public AbstractTrack(LogArtisanWorkshop script, SmithTrack smithTrack) {
		super(script);
		this.smithTrack = smithTrack;
	}
}
