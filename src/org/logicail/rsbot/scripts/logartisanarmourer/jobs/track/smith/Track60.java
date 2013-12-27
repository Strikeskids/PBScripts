package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.smith;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.TakeIngots;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track60 extends Task {
	private SmithTrack smithTrack;

	public Track60(LogicailMethodContext context, SmithTrack smithTrack) {
		super(context);
		this.smithTrack = smithTrack;
	}

	@Override
	public String toString() {
		return "Track60";
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(SmithTrack.getTrack40()).isEmpty() && !ctx.backpack.select().id(TakeIngots.getIngotId(), SmithTrack.getSpikes()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			return;
		}

		int tracks = ctx.backpack.select().id(SmithTrack.getTrack40()).count();

		int spikes = ctx.backpack.select().id(SmithTrack.getSpikes()).count();
		int ingots = ctx.backpack.select().id(TakeIngots.getIngotId()).count();

		if (tracks > spikes && ingots > 0) {
			smithTrack.smith(SmithTrack.getSpikes(), tracks - spikes);
		} else {
			smithTrack.smith(SmithTrack.getTrack60());
		}
	}
}
