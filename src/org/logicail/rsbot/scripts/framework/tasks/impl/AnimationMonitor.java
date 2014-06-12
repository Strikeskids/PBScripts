package org.logicail.rsbot.scripts.framework.tasks.impl;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.LoopTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:33
 */
public class AnimationMonitor<T extends LogicailScript<T>> extends LoopTask<T> {
	private static final Map<Integer, Long> map = new ConcurrentHashMap<Integer, Long>();
	private long nextClear = System.currentTimeMillis() + 600000;
	private static AtomicLong timeLast = new AtomicLong(Long.MAX_VALUE);

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
			if (System.currentTimeMillis() > nextClear) {
				nextClear = System.currentTimeMillis() + 600000;
				map.clear();
			}

			if (ctx.game.loggedIn()) {
				put(ctx.players.local().animation());
			}
		} catch (Exception ignored) {
		}
		return 100;
	}

	public static void put(int animation) {
		if (animation != -1) {
			final long time = System.currentTimeMillis();
			timeLast.set(time);
			map.put(animation, time);
		}
	}

	public static long timeSinceAnimation() {
		return timeLast.get();
	}
}
