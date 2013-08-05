package org.logicail.scripts.logartisanarmourer.tasks.track.smith;

import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track80 extends Node {
	private final Tracks tracks;

	public Track80(Tracks tracks) {
		super(tracks.ctx);
		this.tracks = tracks;
	}

	@Override
	public String toString() {
		return "Track80";
	}

	@Override
	public boolean activate() {
		if (!ctx.backpack.select().id(tracks.getTrack60()).isEmpty()) {
			if (!ctx.backpack.select().id(tracks.getIngotId()).isEmpty() || !ctx.backpack.select().id(tracks.getJoint()).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void execute() {
		if (!tracks.smithTrack.anvilReady()) {
			return;
		}

		int tracks = ctx.backpack.select().id(this.tracks.getTrack60()).size();

		int joints = ctx.backpack.select().id(this.tracks.getJoint()).size();
		int ingots = ctx.backpack.select().id(this.tracks.getIngotId()).size();

		if (tracks > joints && ingots > 0) {
			this.tracks.smithTrack.smith(this.tracks.getJoint(), tracks - joints);
		} else {
			this.tracks.smithTrack.smith(this.tracks.getTrack80());
		}
	}
}
