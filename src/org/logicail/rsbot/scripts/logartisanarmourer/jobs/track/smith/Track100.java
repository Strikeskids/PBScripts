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
public class Track100 extends Task {
	private final SmithTrack smithTrack;

	public Track100(LogicailMethodContext context, SmithTrack smithTrack) {
		super(context);
		this.smithTrack = smithTrack;
	}

	@Override
	public String toString() {
		return "Track100";
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(SmithTrack.getTrack80()).isEmpty() && !ctx.backpack.select().id(TakeIngots.getIngotId(), SmithTrack.getTie()).isEmpty();
	}

	@Override
	public void run() {
		if (!smithTrack.anvilReady()) {
			return;
		}

		int tracks = ctx.backpack.select().id(SmithTrack.getTrack80()).count();

		int ties = ctx.backpack.select().id(SmithTrack.getTie()).count();
		int ingots = ctx.backpack.select().id(TakeIngots.getIngotId()).count();

		if (tracks > ties && ingots > 0) {
			smithTrack.smith(SmithTrack.getTie(), tracks - ties);
		} else {
			smithTrack.smith(SmithTrack.getTrack100());
		}
	}
}
