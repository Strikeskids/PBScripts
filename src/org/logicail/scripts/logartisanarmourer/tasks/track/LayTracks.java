package org.logicail.scripts.logartisanarmourer.tasks.track;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.tasks.modes.Tracks;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 12:43
 */
public class LayTracks extends Node {
	private final static int TUNNEL = 24843;

	public LayTracks(LogicailMethodContext ctx) {
		super(ctx);
	}

	@Override
	public String toString() {
		return "Lay Tracks";
	}

	@Override
	public boolean activate() {
		return !ctx.backpack.select().id(Tracks.TRACK_100).isEmpty()
				&& (ctx.backpack.select().id(Tracks.TRACK_80).isEmpty() || ctx.backpack.select().id(Tracks.TIE).isEmpty());
	}

	@Override
	public void execute() {
		for (GameObject tunnel : ctx.objects.select().id(TUNNEL).nearest().first()) {
			if (ctx.camera.turnTo(tunnel) && ctx.interaction.interact(tunnel, "Lay-tracks", "Tunnel")) {
				if (ctx.waiting.wait(9000, new Condition() {
					@Override
					public boolean validate() {
						return ctx.backpack.select().id(Tracks.TRACK_100).isEmpty();
					}
				})) {
					sleep(1500, 3000);
				}
			}
		}
	}
}
