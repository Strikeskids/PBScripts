package org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.rt6.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.jobs.track.SmithTrack;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track60 extends AbstractTrack {
	public Track60(LogArtisanWorkshop script, SmithTrack smithTrack) {
		super(script, smithTrack);
	}

	@Override
	public String toString() {
		return "Track60";
	}

	@Override
	public boolean valid() {
		return !ctx.backpack.select().id(smithTrack.getTrack40()).isEmpty() && !ctx.backpack.select().id(options.getIngotId(), smithTrack.getSpikes()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			return;
		}

		int tracks = ctx.backpack.select().id(smithTrack.getTrack40()).count();

		int spikes = ctx.backpack.select().id(smithTrack.getSpikes()).count();
		int ingots = ctx.backpack.select().id(options.getIngotId()).count();

		if (tracks > spikes && ingots > 0) {
			smithTrack.smith(smithTrack.getSpikes(), tracks - spikes);
		} else {
			smithTrack.smith(smithTrack.getTrack60());
		}
	}
}
