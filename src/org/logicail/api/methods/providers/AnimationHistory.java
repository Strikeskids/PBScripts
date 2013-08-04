package org.logicail.api.methods.providers;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.framework.script.LoopTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 21:22
 */
public class AnimationHistory extends LoopTask {
	private static final Map<Integer, Long> map = new ConcurrentHashMap<>();

	public AnimationHistory(LogicailMethodContext ctx) {
		super(ctx);
	}

	public void put(int animation) {
		if (animation != -1) {
			map.put(animation, System.currentTimeMillis());
		}
	}

	public long timeSinceAnimation(int animationID) {
		if (map.containsKey(animationID)) {
			return System.currentTimeMillis() - map.get(animationID);
		}
		return Long.MAX_VALUE;
	}

	public long timeSinceAnimation(int... animationID) {
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

	public int loop() {
		try {
			if (ctx.game.isLoggedIn()) {
				put(ctx.players.local().getAnimation());
			}
		} catch (Exception ignored) {
		}
		return 100;
	}
}