package org.logicail.scripts.logartisanarmourer.tasks.track.smith;

import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track60 extends Node {
	private final Tracks tracks;

	public Track60(Tracks tracks) {
		super(tracks.ctx);
		this.tracks = tracks;
	}

	@Override
	public String toString() {
		return "Track60";
	}

	@Override
	public boolean activate() {
		if (!ctx.backpack.select().id(tracks.getTrack40()).isEmpty()) {
			if (!ctx.backpack.select().id(tracks.getIngotId()).isEmpty() || !ctx.backpack.select().id(tracks.getSpikes()).isEmpty()) {
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

		int tracks = ctx.backpack.select().id(this.tracks.getTrack40()).size();

		int spikes = ctx.backpack.select().id(this.tracks.getSpikes()).size();
		int ingots = ctx.backpack.select().id(this.tracks.getIngotId()).size();

		if (tracks > spikes && ingots > 0) {
			this.tracks.smithTrack.smith(this.tracks.getSpikes(), tracks - spikes);
		} else {
			this.tracks.smithTrack.smith(this.tracks.getTrack60());
		}
	}
}
