package org.logicail.rsbot.scripts.framework.tasks.impl;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.LoopTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:33
 */
public class AnimationMonitor<T extends LogicailScript<T>> extends LoopTask<T> {
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

	public int loop() {
		try {
			if (ctx.game.isLoggedIn()) {
				put(ctx.players.local().getAnimation());
			}
		} catch (Exception ignored) {
		}
		return 100;
	}

	public static void put(int animation) {
		if (animation != -1) {
			map.put(animation, System.currentTimeMillis());
		}
	}
}
