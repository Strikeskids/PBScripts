package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/01/14
 * Time: 21:24
 */
public class LogoutIdle extends LogGildedAltarTask {
	private long nextCheck;

	@Override
	public String toString() {
		return "LogoutIdle";
	}
	private int lastBonesBuried = -1;

	public LogoutIdle(LogGildedAltar script) {
		super(script);
		reset();
	}

	private void reset() {
		nextCheck = System.currentTimeMillis() + Random.nextInt(19 * 60 * 1000, 21 * 60 * 1000);
	}

	@Override
	public boolean isValid() {
		return ctx.game.isLoggedIn() && System.currentTimeMillis() > nextCheck;
	}

	@Override
	public void run() {
		if (options.bonesOffered.get() == lastBonesBuried) {
			options.status = ("Logout idle");
			ctx.stop("No bones offered in last 20 minutes, logging out to avoid idling");
		} else {
			lastBonesBuried = options.bonesOffered.get();
			reset();
		}
	}
}
