package org.logicail.rsbot.scripts.logartisanarmourer.jobs.track;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/01/13
 * Time: 13:49
 */
public class LayTracks extends ArtisanArmourerTask {
	public LayTracks(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "Lay Tracks";
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& !ctx.backpack.select().id(SmithTrack.TRACK_100).isEmpty()
				&& (ctx.backpack.select().id(SmithTrack.TRACK_80).isEmpty() || ctx.backpack.select().id(SmithTrack.TIE).isEmpty());
	}

	private final static int TUNNEL = 24843;

	@Override
	public void run() {
		for (GameObject tunnel : ctx.objects.select().id(TUNNEL).nearest().first()) {
			if (ctx.camera.prepare(tunnel) && tunnel.interact("Lay-tracks", "Tunnel")) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.backpack.select().id(SmithTrack.TRACK_100).isEmpty();
					}
				})) {
					sleep(1750, 3500);
				}
			} else {
				ctx.camera.turnTo(tunnel);
				if (tunnel.interact("Lay-tracks", "Tunnel")) {
					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.backpack.select().id(SmithTrack.TRACK_100).isEmpty();
						}
					})) {
						sleep(1750, 3500);
					}
				} else {
					ctx.movement.stepTowards(tunnel);
					sleep(1000, 2000);
				}
			}
		}
	}
}
