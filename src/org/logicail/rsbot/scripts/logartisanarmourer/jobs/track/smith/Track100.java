package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track100 extends AbstractTrack {
	public Track100(LogArtisanArmourer script, SmithTrack smithTrack) {
		super(script, smithTrack);
	}

	@Override
	public String toString() {
		return "Track100";
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(smithTrack.getTrack80()).isEmpty() && !ctx.backpack.select().id(options.getIngotId(), smithTrack.getTie()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			return;
		}

		int tracks = ctx.backpack.select().id(smithTrack.getTrack80()).count();

		int ties = ctx.backpack.select().id(smithTrack.getTie()).count();
		int ingots = ctx.backpack.select().id(options.getIngotId()).count();

		if (tracks > ties && ingots > 0) {
			smithTrack.smith(smithTrack.getTie(), tracks - ties);
		} else {
			smithTrack.smith(smithTrack.getTrack100());
		}
	}
}
