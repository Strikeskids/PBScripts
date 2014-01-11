package org.logicail.rsbot.scripts.framework.tasks.impl;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.powerbot.script.wrappers.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:33
 */
public class AnimationMonitor<T extends LogicailScript<T>> extends Task<T> {
	private static final Map<Integer, Long> map = new ConcurrentHashMap<Integer, Long>();

	/**
	 * Time since since animation
	 *
	 * @param animationID
	 * @return
	 */
	public static long timeSinceAnimation(int animationID) {
		return timeSinceAnimation(new int[]{animationID});
	}

	/**
	 * Most recent time since any of animations
	 *
	 * @param animationID
	 * @return
	 */
	public static long timeSinceAnimation(int... animationID) {
		long mostRecent = Long.MAX_VALUE;
		for (int id : animationID) {
			if (map.containsKey(id)) {
				long time = System.currentTimeMillis() - map.get(id);
				if (time < mostRecent) {
					mostRecent = time;
				}
			}
		}
		return mostRecent;
	}

	public AnimationMonitor(T script) {
		super(script);
	}

	public static void put(int animation) {
		if (animation != -1) {
			map.put(animation, System.currentTimeMillis());
		}
	}

	@Override
	public void run() {
		try {
			if (ctx.game.isLoggedIn()) {
				final Player local = ctx.players.local();
				if (local != null) {
					put(local.getAnimation());
				}
			}
		} catch (Exception ignored) {
		}
		sleep(150);

		if (!Thread.interrupted()) {
			try {
				script.getController().getExecutor().offer(this);
			} catch (NullPointerException ignored) {
			}
		}
	}
}
