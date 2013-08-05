package org.logicail.scripts.logartisanarmourer.tasks.track.smith;

import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track100 extends Node {
	private Tracks tracks;

	public Track100(Tracks tracks) {
		super(tracks.ctx);
		this.tracks = tracks;
	}

	@Override
	public String toString() {
		return "Track100";
	}

	@Override
	public boolean activate() {
		if (!ctx.backpack.select().id(tracks.getTrack80()).isEmpty()) {
			if (!ctx.backpack.select().id(tracks.getIngotId()).isEmpty() || !ctx.backpack.select().id(tracks.getTie()).isEmpty()) {
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

		int tracks = ctx.backpack.select().id(this.tracks.getTrack80()).size();

		int ties = ctx.backpack.select().id(this.tracks.getTie()).size();
		int ingots = ctx.backpack.select().id(this.tracks.getIngotId()).size();

		if (tracks > ties && ingots > 0) {
			this.tracks.smithTrack.smith(this.tracks.getTie(), tracks - ties);
		} else {
			this.tracks.smithTrack.smith(this.tracks.getTrack100());
		}
	}
}
