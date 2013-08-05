package org.logicail.scripts.logartisanarmourer.tasks.track.smith;

import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;

/**
 * Created with IntelliJ IDEA.
 * Date: 17/03/13
 * Time: 16:45
 */
public class Track40 extends Node {
	private Tracks tracks;

	public Track40(Tracks tracks) {
		super(tracks.ctx);
		this.tracks = tracks;
	}

	@Override
	public String toString() {
		return "Track40";
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(tracks.getIngotId()).isEmpty() || !ctx.backpack.select().id(tracks.getBasePlate()).isEmpty();
	}

	@Override
	public void execute() {
		if (!tracks.smithTrack.anvilReady()) {
			return;
		}

		int rails = ctx.backpack.select().id(this.tracks.getRails()).size();

		int baseplate = ctx.backpack.select().id(this.tracks.getBasePlate()).size();
		int ingots = ctx.backpack.select().id(this.tracks.getIngotId()).size();

		if (ingots > 0) {
			if (rails != baseplate || rails == 0) {
				if (rails == 0) {
					tracks.smithTrack.smith(this.tracks.getRails());
				} else {
					if (rails > baseplate) {
						tracks.smithTrack.smith(this.tracks.getBasePlate(), rails - baseplate);
					} else {
						tracks.smithTrack.smith(this.tracks.getRails(), baseplate - rails);
					}
				}
			}
		} else {
			tracks.smithTrack.smith(this.tracks.getTrack40());
		}
	}
}
