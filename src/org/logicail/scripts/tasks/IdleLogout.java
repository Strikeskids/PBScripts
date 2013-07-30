package org.logicail.scripts.tasks;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.Shutdown;
import org.logicail.framework.script.state.Node;
import org.powerbot.script.Script;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:01
 */
public class IdleLogout extends Node {
	private final int secondsMin;
	private final int secondsMax;
	long totalXP = -1;
	private Timer timer;
	private int seconds;

	public IdleLogout(LogicailMethodContext ctx, int secondsMin, int secondsMax) {
		super(ctx);
		this.secondsMin = secondsMin;
		this.secondsMax = secondsMax;

		resetTimer();

		ctx.script.getExecQueue(Script.State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				totalXP = -1;
			}
		});
	}

	private void resetTimer() {
		timer = new Timer(seconds = (Random.nextInt(secondsMin, secondsMax) * 1000));
	}

	// TODO: on resume reset timer
	@Override
	public boolean activate() {
		// TODO: Check could just need isLoggedIn
		int state = ctx.game.getClientState();
		return (state == Game.INDEX_MAP_LOADED || state == Game.INDEX_MAP_LOADING) && !timer.isRunning();
	}

	@Override
	public void execute() {
		if (totalXP == -1) {
			totalXP = getTotalXP();
			resetTimer();
		} else {
			if (getTotalXP() <= totalXP) {
				new Shutdown(ctx).stop("No xp gained in " + Timer.format(seconds) + " stopping script...");
			}
		}
	}

	private long getTotalXP() {
		long total = 0;
		for (int i : ctx.skills.getExperiences()) {
			total += i;
		}
		return total;
	}
}
