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
public class Track80 extends Task {
	private final SmithTrack smithTrack;

	public Track80(LogicailMethodContext context, SmithTrack smithTrack) {
		super(context);
		this.smithTrack = smithTrack;
	}

	@Override
	public String toString() {
		return "Track80";
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(SmithTrack.getTrack60()).isEmpty() && !ctx.backpack.select().id(TakeIngots.getIngotId(), SmithTrack.getJoint()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			return;
		}

		int tracks = ctx.backpack.select().id(SmithTrack.getTrack60()).count();
		int joints = ctx.backpack.select().id(SmithTrack.getJoint()).count();
		int ingots = ctx.backpack.select().id(TakeIngots.getIngotId()).count();

		if (tracks > joints && ingots > 0) {
			smithTrack.smith(SmithTrack.getJoint(), tracks - joints);
		} else {
			smithTrack.smith(SmithTrack.getTrack80());
		}
	}
}
