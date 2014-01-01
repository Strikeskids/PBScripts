package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
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

	public AbstractTrack(LogArtisanArmourer script, SmithTrack smithTrack) {
		super(script);
		this.smithTrack = smithTrack;
	}
}
