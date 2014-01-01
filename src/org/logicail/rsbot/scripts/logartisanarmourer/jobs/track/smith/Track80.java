package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track80 extends AbstractTrack {
	public Track80(LogArtisanArmourer script, SmithTrack smithTrack, int ingotId) {
		super(script, smithTrack, ingotId);
	}

	@Override
	public String toString() {
		return "Track80";
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(smithTrack.getTrack60()).isEmpty() && !ctx.backpack.select().id(ingotId, smithTrack.getJoint()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			return;
		}

		int tracks = ctx.backpack.select().id(smithTrack.getTrack60()).count();
		int joints = ctx.backpack.select().id(smithTrack.getJoint()).count();
		int ingots = ctx.backpack.select().id(ingotId).count();

		if (tracks > joints && ingots > 0) {
			smithTrack.smith(smithTrack.getJoint(), tracks - joints);
		} else {
			smithTrack.smith(smithTrack.getTrack80());
		}
	}
}
